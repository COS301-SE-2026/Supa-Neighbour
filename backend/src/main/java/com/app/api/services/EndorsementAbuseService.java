package com.app.api.services;

import com.app.api.dtos.AbuseFlagResponseDTO;
import com.app.api.models.EndorsementFlag;
import com.app.api.models.EndorsementFlagParticipant;
import com.app.api.repositories.EndorsementFlagParticipantRepository;
import com.app.api.repositories.EndorsementFlagRepository;
import com.app.api.repositories.EndorsementRepository;
import com.app.api.repositories.UserRepository;
import com.google.firebase.auth.FirebaseAuthException;
import com.app.api.repositories.ClusterCacheRepository;
import com.app.api.repositories.EndorsementRepository.TaskEdgeRow;
import com.app.api.repositories.EndorsementRepository.TimestampEdgeRow;
import com.app.api.models.EndorsementClusterCache;
import com.app.api.models.User;
import org.springframework.http.HttpStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.time.Instant;

@Service
public class EndorsementAbuseService {
    private static final Logger LOG = LoggerFactory.getLogger(EndorsementAbuseService.class);

    private static final int RECIPROCAL_TASK_THRESHOLD = 6;
    private static final Duration RAPID_FIRE_GAP = Duration.ofSeconds(10);
    private static final int MIN_ISLAND_SIZE = 3;
    private static final double ISLAND_INTERNAL_RATIO_THRESHOLD = 0.9; 
    private static final Duration SPIKE_WINDOW = Duration.ofDays(1);     
    private static final long SPIKE_MIN_COUNT = 10;                       
    private static final Duration NEW_ACCOUNT_MAX_AGE = Duration.ofDays(7); 

    private final FirebaseAuthService firebaseAuthService;
    private final EndorsementRepository endorsementRepository;
    private final EndorsementFlagRepository flagRepository;
    private final EndorsementFlagParticipantRepository participantRepository;
    private final ClusterCacheRepository clusterCacheRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructs the service with its required collaborators.
     *
     * @param endorsementRepository      source of raw endorsement edges (task-linked and timestamped)
     * @param flagRepository             persistence for {@link EndorsementFlag} aggregates
     * @param participantRepository      persistence for {@link EndorsementFlagParticipant} rows
     * @param clusterCacheRepository     read access to the pre-computed cluster cache (populated by the nightly cluster job)
     * @param firebaseAuthService        resolves account creation times for spike detection
     * @param userRepository             maps internal user IDs to Firebase UIDs
     */
    public EndorsementAbuseService(
        EndorsementRepository endorsementRepository,
        EndorsementFlagRepository flagRepository,
        EndorsementFlagParticipantRepository participantRepository,
        ClusterCacheRepository clusterCacheRepository,
        FirebaseAuthService firebaseAuthService,
        UserRepository userRepository
    ){
        this.endorsementRepository = endorsementRepository;
        this.flagRepository = flagRepository;
        this.participantRepository = participantRepository;
        this.clusterCacheRepository = clusterCacheRepository;
        this.firebaseAuthService = firebaseAuthService;
        this.userRepository = userRepository;
    }

    /**
     * Runs after 2.7's nightly job, per the "trigger after 2.7" decision.
     * Since both are {@code @Scheduled} independently, the cron times need
     * to be staggered with enough headroom for 2.7 to finish across every
     * zone before this reads the cluster cache — not called directly from
     * {@code EndorsementClusterService} to keep the two jobs decoupled.
     */
    @Scheduled(cron = "0 0 4 * * *", zone = "Africa/Johannesburg")
    public void runNightlyAbuseScan() {
        UUID runId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LOG.info("Nightly abuse scan starting, runId={}", runId);

        int flagged = 0;
        flagged += scanMutualRings(runId, now);
        flagged += scanTimestampAnomalies(runId, now);
        flagged += scanIslandGroups(runId, now);
        flagged += scanSuddenSpikes(runId, now);

        LOG.info("Nightly abuse scan complete, runId={}, {} flag(s) touched", runId, flagged);
    }

    /**
     * Detects "mutual ring" abuse: pairs of users who have endorsed each other
     * on a sufficiently large set of distinct tasks.
     *
     * <p>The detector groups task-linked endorsement edges by unordered user
     * pair, then requires that endorsements exist in <em>both</em> directions.
     * A pair is flagged when the union of tasks endorsed in either direction
     * meets or exceeds {@link #RECIPROCAL_TASK_THRESHOLD}.</p>
     *
     * @param runId identifier of the current scan run, stamped onto any flag created or updated
     * @param now   detection timestamp applied to any flag created or updated
     * @return the number of flags touched (created or updated)
     */
    @Transactional 
    public int scanMutualRings(UUID runId, LocalDateTime now){
        List<TaskEdgeRow> rows = endorsementRepository.findAllTaskLinkedEdges();

        Map<Long, Map<Boolean, Set<Integer>>> byPair = new HashMap<>();

        Map<Long, int[]> pairUsers = new HashMap<>();
        for(TaskEdgeRow row: rows){
            int a = row.getFromUserId();
            int b = row.getToUserId();
            if(a == b) {
                continue;
            }

            int lo = Math.min(a, b), hi = Math.max(a, b);
            long key = pairKey(lo, hi);
            pairUsers.putIfAbsent(key, new int[]{lo, hi});
            boolean forward = a == lo;
            byPair.computeIfAbsent(key, k -> new HashMap<>())
            .computeIfAbsent(forward, k -> new HashSet<>())
            .add(row.getTaskId());
        }

        int touched = 0;
        for(Map.Entry<Long, Map<Boolean, Set<Integer>>> entry: byPair.entrySet()){
            Map<Boolean, Set<Integer>> dirs = entry.getValue();
            Set<Integer> forward = dirs.getOrDefault(true, Set.of());
            Set<Integer> backward = dirs.getOrDefault(false, Set.of());
            if (forward.isEmpty() || backward.isEmpty()) {
                continue; // reciprocity required
            }

            Set<Integer> union = new HashSet<>(forward);
            union.addAll(backward);
            if (union.size() < RECIPROCAL_TASK_THRESHOLD) {
                continue;
            }

            int[] users = pairUsers.get(entry.getKey());
            upsertFlag("mutual_ring", null, (double) union.size(),
                List.of(users[0], users[1]), List.of(), now, runId);
            touched++;
        }

        return touched++;
    }

    
    private long pairKey(int lo, int hi) {
        return ((long) lo << 32) | (hi & 0xFFFFFFFFL);
    }

    /**
     * Detects "mutual ring" abuse: pairs of users who have endorsed each other
     * on a sufficiently large set of distinct tasks.
     *
     * <p>The detector groups task-linked endorsement edges by unordered user
     * pair, then requires that endorsements exist in <em>both</em> directions.
     * A pair is flagged when the union of tasks endorsed in either direction
     * meets or exceeds {@link #RECIPROCAL_TASK_THRESHOLD}.</p>
     *
     * @param runId identifier of the current scan run, stamped onto any flag created or updated
     * @param now   detection timestamp applied to any flag created or updated
     * @return the number of flags touched (created or updated)
     */
    private String patternTypeDisplay(String type) {
        return switch (type) {
            case "mutual_ring" -> "Mutual Endorsement Ring";
            case "sudden_spike" -> "Sudden Endorsement Spike";
            case "island_group" -> "Isolated Endorsement Group";
            case "timestamp_anomaly" -> "Timestamp Anomaly";
            default -> type;
        };
    }

    /**
     * Detects "timestamp anomaly" abuse: endorsements between the same pair
     * that occur implausibly close together.
     *
     * <p>For each unordered user pair, the method walks the chronological
     * sequence of endorsements and records the smallest gap below
     * {@link #RAPID_FIRE_GAP}. Any pair with at least one such gap is flagged,
     * with the metric value set to the smallest observed gap in seconds.</p>
     *
     * @param runId identifier of the current scan run, stamped onto any flag created or updated
     * @param now   detection timestamp applied to any flag created or updated
     * @return the number of flags touched (created or updated)
     */
    @Transactional 
    public int scanTimestampAnomalies(UUID runId, LocalDateTime now){

        List<TimestampEdgeRow> rows = endorsementRepository.findAllEdgesWithTimestamps();

        Map<Long, List<TimestampEdgeRow>> byPair = new HashMap<>();
        Map<Long, int[]> pairUsers = new HashMap<>();
        for(TimestampEdgeRow row: rows){
            int a = row.getFromUserId(), b = row.getToUserId();
            if(a == b){
                continue;
            }

            int lo = Math.min(a, b), hi = Math.max(a, b);
            long key = pairKey(lo, hi);
            pairUsers.putIfAbsent(key, new int[]{lo, hi});
            byPair.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        int touched = 0;
        for(Map.Entry<Long, List<TimestampEdgeRow>> entry: byPair.entrySet()){
            List<TimestampEdgeRow> events = entry.getValue();

            Duration minGap = null;
            for(int i =  1; i < events.size(); i++){
                Duration gap = Duration.between(events.get(i -1).getCreatedAt(), events.get(i).getCreatedAt());
                if(gap.compareTo(RAPID_FIRE_GAP) < 0 && (minGap == null || gap.compareTo(minGap) < 0)){
                    minGap = gap;
                }
            }

            if (minGap == null) {
                continue;
            }

            int[] users = pairUsers.get(entry.getKey());
            upsertFlag("timestamp_anomaly", null, (double) minGap.toSeconds(),
                List.of(users[0], users[1]), List.of(), now, runId);
            touched++;
        }
        return touched;
    }

    /**
     * Detects "island group" abuse: endorsement clusters whose members almost
     * exclusively endorse one another rather than the wider population.
     *
     * <p>Reads the pre-computed cluster cache (populated by the nightly
     * clustering job) and flags any cluster that has at least
     * {@link #MIN_ISLAND_SIZE} members and an internal-to-total degree ratio
     * of at least {@link #ISLAND_INTERNAL_RATIO_THRESHOLD}. The ratio is
     * stored as the flag's metric value.</p>
     *
     * @param runId identifier of the current scan run, stamped onto any flag created or updated
     * @param now   detection timestamp applied to any flag created or updated
     * @return the number of flags touched (created or updated)
     */
    @Transactional 
    public int scanIslandGroups(UUID runId, LocalDateTime now){
        List<Integer> zoneIds = clusterCacheRepository.findDistinctZoneIds();
        int touched = 0;

        for(Integer zoneId: zoneIds){
            List<EndorsementClusterCache> rows = clusterCacheRepository.findByZoneIdOrderByClusterLabelAscUserIdAsc(zoneId);
            Map<Integer, List<EndorsementClusterCache>> byCluster = rows.stream().collect(Collectors.groupingBy(EndorsementClusterCache::getClusterLabel));

            for(List<EndorsementClusterCache> members: byCluster.values()){
                if (members.size() < MIN_ISLAND_SIZE){
                     continue;
                }

                long internal = members.stream().mapToLong(EndorsementClusterCache::getInternalDegree).sum();
                long total = members.stream().mapToLong(EndorsementClusterCache::getTotalDegree).sum();
                if (total == 0) {
                    continue;
                }
                double ratio = internal / (double) total;
                if (ratio < ISLAND_INTERNAL_RATIO_THRESHOLD) {
                    continue;
                }

                List<Integer> userIds = members.stream().map(EndorsementClusterCache::getUserId).toList();
                upsertFlag("island_group", zoneId, ratio, userIds, List.of(), now, runId);
                touched++;
            }
        }

        return touched;
    }

    /**
     * Detects "island group" abuse: endorsement clusters whose members almost
     * exclusively endorse one another rather than the wider population.
     *
     * <p>Reads the pre-computed cluster cache (populated by the nightly
     * clustering job), groups cached members by cluster label within each zone,
     * and flags any cluster that:</p>
     * <ul>
     *   <li>has at least {@link #MIN_ISLAND_SIZE} members, and</li>
     *   <li>has an internal-degree-to-total-degree ratio of at least
     *       {@link #ISLAND_INTERNAL_RATIO_THRESHOLD}.</li>
     * </ul>
     *
     * <p>The metric value stored on the flag is the computed internal ratio.
     * Unlike the pair-based detectors, island flags carry a {@code zoneId}.</p>
     *
     * @param runId identifier of the current scan run, stamped onto any flag created or updated
     * @param now   detection timestamp applied to any flag created or updated
     * @return the number of flags touched (created or updated)
     */
    public void upsertFlag(
        String patternType, 
        Integer zoneId, 
        double metricValue,
        List<Integer> userIds, 
        List<String> roles,
        LocalDateTime detectedAt, 
        UUID runId
    ){
        Set<Integer> targetSet = new HashSet<>(userIds);
        List<EndorsementFlag> candidates = flagRepository.findOpenOrInvestigateByPatternType(patternType);
        for(EndorsementFlag candidate: candidates){
            if(!Objects.equals(candidate.getZoneId(), zoneId)){
                continue;
            }

            Set<Integer> existingSet = participantRepository.findByFlagId(candidate.getFlagId()).stream().map(EndorsementFlagParticipant::getUserId).collect(Collectors.toSet());

            if(!existingSet.equals(targetSet)){
                continue;
            }
            candidate.setLastDetectedAt(detectedAt);
            candidate.setOccurrenceCount(candidate.getOccurrenceCount() + 1);
            candidate.setMetricValue(metricValue);
            candidate.setRunId(runId);
            flagRepository.save(candidate);
            return;
        }

        EndorsementFlag flag = new EndorsementFlag(patternType, zoneId, metricValue, detectedAt, runId);
        flag = flagRepository.save(flag);
        List<EndorsementFlagParticipant> participants = new ArrayList<>(userIds.size());

        for (int i = 0; i < userIds.size(); i++) {
            String role = (roles.size() == userIds.size()) ? roles.get(i) : null;
            participants.add(new EndorsementFlagParticipant(flag.getFlagId(), userIds.get(i), role));
        }
        participantRepository.saveAll(participants);
    }

    /**
     * Merges into an existing open/investigate flag with the same pattern
     * type, zone, and exact participant set, or inserts a new one.
     *
     * @param userIds participants in a fixed, meaningful order matched against
     *                {@code roles} by index (e.g. [endorserA, endorserB]);
     *                pass an empty {@code roles} list for role-less patterns
     *                (island_group)
     */
    @Transactional(readOnly = true)
    public List<AbuseFlagResponseDTO> listFlags(String status) {
        List<EndorsementFlag> flags = (status == null || status.isBlank())
            ? flagRepository.findAllByOrderByLastDetectedAtDesc()
            : flagRepository.findByStatusOrderByLastDetectedAtDesc(status);

        Map<Long, List<EndorsementFlagParticipant>> participantsByFlag =
            participantRepository.findByFlagIdIn(flags.stream().map(EndorsementFlag::getFlagId).toList())
                .stream().collect(Collectors.groupingBy(EndorsementFlagParticipant::getFlagId));

        return flags.stream().map(f -> new AbuseFlagResponseDTO(
            f.getFlagId(), f.getPatternType(), patternTypeDisplay(f.getPatternType()),
            f.getZoneId(), f.getMetricValue(), f.getStatus(), f.getOccurrenceCount(),
            f.getFirstDetectedAt(), f.getLastDetectedAt(), f.getRunId(),
            participantsByFlag.getOrDefault(f.getFlagId(), List.of()).stream()
                .map(p -> new AbuseFlagResponseDTO.Participant(p.getUserId(), p.getRole()))
                .toList()
        )).toList();
    }

    /**
     * Lists abuse flags, optionally filtered by status.
     *
     * <p>Flags are returned most-recently-detected first, with their
     * participants eagerly attached. The {@code patternTypeDisplay} field on
     * each DTO is populated via {@link #patternTypeDisplay(String)}.</p>
     *
     * @param status optional status filter ({@code "open"}, {@code "investigate"},
     *               or {@code "dismiss"}); if {@code null} or blank, all flags
     *               are returned
     * @return the matching flags, newest first, with participants resolved
     */
    @Transactional 
    public int scanSuddenSpikes(UUID runId, LocalDateTime now){
        LocalDateTime since = now.minus(SPIKE_WINDOW);
        List<EndorsementRepository.SpikeCandidateRow> candidates = endorsementRepository.findSpikeCandidates(since, SPIKE_MIN_COUNT);
        if(candidates.isEmpty()){
            return 0;
        }

        List<Integer> candidateIds = candidates.stream().map(EndorsementRepository.SpikeCandidateRow::getFromUserId).toList();
        Map<Integer, String> uidByUserId = userRepository.findAllById(candidateIds).stream()
             .collect(Collectors.toMap(User::getUserid, User::getFirebaseUid));
        Map<String, Instant> creationByUid;

        try {
            creationByUid = firebaseAuthService.getAccountCreationTimes(uidByUserId.values());
        } catch (FirebaseAuthException e) {
            LOG.error("Could not resolve account creation times for sudden_spike scan", e);
            return 0;
        }

        int touched = 0;
        Instant nowInstant = now.atZone(java.time.ZoneId.of("Africa/Johannesburg")).toInstant();
        for (EndorsementRepository.SpikeCandidateRow candidate : candidates) {
            String uid = uidByUserId.get(candidate.getFromUserId());
            Instant createdAt = uid == null ? null : creationByUid.get(uid);
            if (createdAt == null) {
                continue; // couldn't resolve — skip rather than guess
            }

            if (Duration.between(createdAt, nowInstant).compareTo(NEW_ACCOUNT_MAX_AGE) <= 0) {
                upsertFlag("sudden_spike", null, (double) candidate.getCnt(),
                    List.of(candidate.getFromUserId()), List.of(), now, runId);
                touched++;
            }
        }
        return touched;

    }

    private static final Set<String> VALID_STATUSES = Set.of("open", "investigate", "dismiss");

    /**
     * Updates the review status of an existing abuse flag.
     *
     * @param flagId    the flag to update
     * @param newStatus the new status; must be one of {@link #VALID_STATUSES}
     * @return the updated flag, with its participants attached
     * @throws ResponseStatusException {@link HttpStatus#BAD_REQUEST} if the
     *         status is not recognised, or {@link HttpStatus#NOT_FOUND} if no
     *         flag exists with {@code flagId}
     */
    @Transactional
    public AbuseFlagResponseDTO updateStatus(Long flagId, String newStatus) {
        if (!VALID_STATUSES.contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + newStatus);
        }
        EndorsementFlag flag = flagRepository.findById(flagId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flag not found"));
        flag.setStatus(newStatus);
        flagRepository.save(flag);

        List<EndorsementFlagParticipant> participants = participantRepository.findByFlagId(flagId);
        return new AbuseFlagResponseDTO(
            flag.getFlagId(), flag.getPatternType(), patternTypeDisplay(flag.getPatternType()),
            flag.getZoneId(), flag.getMetricValue(), flag.getStatus(), flag.getOccurrenceCount(),
            flag.getFirstDetectedAt(), flag.getLastDetectedAt(), flag.getRunId(),
            participants.stream().map(p -> new AbuseFlagResponseDTO.Participant(p.getUserId(), p.getRole())).toList()
        );
    }
}
