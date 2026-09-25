package com.app.api.services;

import com.app.api.dtos.AbuseFlagResponseDTO;
import com.app.api.models.EndorsementFlag;
import com.app.api.models.EndorsementFlagParticipant;
import com.app.api.repositories.EndorsementFlagParticipantRepository;
import com.app.api.repositories.EndorsementFlagRepository;
import com.app.api.repositories.EndorsementRepository;
import com.app.api.repositories.ClusterCacheRepository;
import com.app.api.repositories.EndorsementRepository.TaskEdgeRow;
import com.app.api.repositories.EndorsementRepository.TimestampEdgeRow;
import com.app.api.models.EndorsementClusterCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EndorsementAbuseService {
    private static final Logger log = LoggerFactory.getLogger(EndorsementAbuseService.class);

    private static final int RECIPROCAL_TASK_THRESHOLD = 6;
    private static final Duration RAPID_FIRE_GAP = Duration.ofSeconds(10);
    private static final int MIN_ISLAND_SIZE = 3;
    private static final double ISLAND_INTERNAL_RATIO_THRESHOLD = 0.9; // TODO: confirm

    private final EndorsementRepository endorsementRepository;
    private final EndorsementFlagRepository flagRepository;
    private final EndorsementFlagParticipantRepository participantRepository;
    private final ClusterCacheRepository clusterCacheRepository;
    
    public EndorsementAbuseService(
        EndorsementRepository endorsementRepository,
        EndorsementFlagRepository flagRepository,
        EndorsementFlagParticipantRepository participantRepository,
         ClusterCacheRepository clusterCacheRepository
    ){
        this.endorsementRepository = endorsementRepository;
        this.flagRepository = flagRepository;
        this.participantRepository = participantRepository;
        this.clusterCacheRepository = clusterCacheRepository;
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
        log.info("Nightly abuse scan starting, runId={}", runId);

        int flagged = 0;
        flagged += scanMutualRings(runId, now);
        flagged += scanTimestampAnomalies(runId, now);
        flagged += scanIslandGroups(runId, now);
        // sudden_spike: TODO — blocked on FirebaseAuthService account-creation lookup

        log.info("Nightly abuse scan complete, runId={}, {} flag(s) touched", runId, flagged);
    }

    @Transactional 
    int scanMutualRings(UUID runId, LocalDateTime now){
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
            if (forward.isEmpty() || backward.isEmpty()) continue; // reciprocity required

            Set<Integer> union = new HashSet<>(forward);
            union.addAll(backward);
            if (union.size() < RECIPROCAL_TASK_THRESHOLD) continue;

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

    private String patternTypeDisplay(String type) {
        return switch (type) {
            case "mutual_ring" -> "Mutual Endorsement Ring";
            case "sudden_spike" -> "Sudden Endorsement Spike";
            case "island_group" -> "Isolated Endorsement Group";
            case "timestamp_anomaly" -> "Timestamp Anomaly";
            default -> type;
        };
    }

    @Transactional 
    int scanTimestampAnomalies(UUID runId, LocalDateTime now){

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

            if (minGap == null) continue;

            int[] users = pairUsers.get(entry.getKey());
            upsertFlag("timestamp_anomaly", null, (double) minGap.toSeconds(),
                List.of(users[0], users[1]), List.of(), now, runId);
            touched++;
        }
        return touched;
    }

    @Transactional 
    int scanIslandGroups(UUID runId, LocalDateTime now){
        List<Integer> zoneIds = endorsementRepository.findDistinctZoneIds();
        int touched = 0;

        for(Integer zoneId: zoneIds){
            List<EndorsementClusterCache> rows = clusterCacheRepository.findByZoneIdOrderByClusterLabelAscUserIdAsc(zoneId);
            Map<Integer, List<EndorsementClusterCache>> byCluster = rows.stream().collect(Collectors.groupingBy(EndorsementClusterCache::getClusterLabel));

            for(List<EndorsementClusterCache> members: byCluster.values()){
                if (members.size() < MIN_ISLAND_SIZE) continue;

                long internal = members.stream().mapToLong(EndorsementClusterCache::getInternalDegree).sum();
                long total = members.stream().mapToLong(EndorsementClusterCache::getTotalDegree).sum();
                if (total == 0) continue;
                double ratio = internal / (double) total;
                if (ratio < ISLAND_INTERNAL_RATIO_THRESHOLD) continue;

                List<Integer> userIds = members.stream().map(EndorsementClusterCache::getUserId).toList();
                upsertFlag("island_group", zoneId, ratio, userIds, List.of(), now, runId);
                touched++;
            }
        }

        return touched;
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
    private void upsertFlag(
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
}
