package com.app.api.services;
import com.app.api.dtos.CreateEndorsementRequestDTO;
import com.app.api.dtos.EndorsementResponseDTO;
import com.app.api.dtos.EndorsementSummaryResponseDTO;
import com.app.api.dtos.MyEndorsementResponseDTO;
import com.app.api.dtos.SkillCatalogueResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO;
import com.app.api.dtos.TrustPathResponseDTO;
import com.app.api.dtos.TrustGraphResponseDTO.GraphDirection;
import com.app.api.models.Endorsement;
import com.app.api.models.EndorsementSkill;
import com.app.api.models.HelperAnalytics;
import com.app.api.models.Location;
import com.app.api.models.TaskInvoice;
import com.app.api.models.Address;
import com.app.api.models.Admin;
import com.app.api.models.User;
import com.app.api.repositories.EndorsementRepository;
import com.app.api.repositories.EndorsementSkillsRepository;
import com.app.api.repositories.EndorsementRepository.EdgeRow;
import com.app.api.repositories.EndorsementRepository.EndorserAggregate;
import com.app.api.repositories.EndorsementRepository.SkillAggregate;
import com.app.api.repositories.AdminRepository;

import com.app.api.repositories.LocationRepository;
import com.app.api.repositories.TaskInvoiceRepository;
import com.app.api.repositories.UserRepository;
 import com.app.api.repositories.HelperAnalyticsRepository;
 import com.app.api.repositories.HelperRepository;
import com.app.api.models.Helper;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
 
/**
 * Business logic for peer endorsements and the trust graph they form.
 *
 * <p>An endorsement is a directed edge "endorser vouches for endorsee, for tag
 * X". The tag is a plain value validated against the {@link EndorsementSkill}
 * catalogue on write, so reads never join the catalogue — graph queries in
 * particular carry the tag straight out of the endorsement row. Display metadata
 * is attached afterwards with one catalogue lookup per request.</p>
 *
 * <p>Nothing here takes an endorser or caller identity from a request body or
 * query string.</p>
 */
@Service
public class EndorsementService {

    public static final String UNCATEGORISED="Uncategorised";
    public static final int MAX_GRAPH_DEPTH= 4;
    public static final int MAX_GRAPH_NODES = 500;
    public static final int MAX_PATH_DEPTH = 6;
    public static final int MAX_PATHS_RETURNED = 10;
    public static final int SUMMARY_TOP_SKILLS = 5;
    private static final int SUMMARY_MAX_GRAPH_NODES = 8;

    private final EndorsementRepository endorsementRepository;
    private final EndorsementSkillsRepository endorsementSkillsRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final TaskInvoiceRepository taskInvoiceRepository;
    private final EndorsementSkillService endorsementSkillService;
    private final HelperAnalyticsRepository helperAnalyticsRepository;
    private final AdminRepository adminRepository;
    private final HelperRepository helperRepository;
    /**
     * @param endorsementRepository endorsement persistence and graph projections
     * @param skillRepository       the skill catalogue
     * @param userRepository        used to validate the endorsee
     * @param locationRepository    used to validate the zone
     * @param taskInvoiceRepository used to validate an optional backing task
     */
    
    public EndorsementService(EndorsementRepository endorsementRepository, EndorsementSkillsRepository endorsementSkillsRepository,
        UserRepository userRepository, LocationRepository locationRepository, TaskInvoiceRepository  taskInvoiceRepository,EndorsementSkillService endorsementSkillService,HelperAnalyticsRepository helperAnalyticsRepository, AdminRepository adminRepository, HelperRepository helperRepository) {
            this.endorsementRepository=endorsementRepository;
            this.endorsementSkillsRepository=endorsementSkillsRepository;
            this.locationRepository=locationRepository;
            this.userRepository=userRepository;
            this.taskInvoiceRepository=taskInvoiceRepository;
            this.endorsementSkillService=endorsementSkillService;
            this.helperAnalyticsRepository = helperAnalyticsRepository;
            this.adminRepository = adminRepository;
            this.helperRepository = helperRepository;
        }

    /**
     * Records an endorsement given by the authenticated caller.
     *
     * <p>This is the only place a skill tag is checked against the catalogue,
     * which is what makes it safe to treat the stored value as a constant
     * everywhere else.</p>
     *
     * @param endorser the caller, resolved from the Firebase ID token
     * @param request  the validated request body
     * @return the persisted endorsement
     * @throws ResponseStatusException 400 for self-endorsement, 404 for an unknown
     *                                 endorsee, zone, tag or task, 409 for a
     *                                 duplicate, 422 for an unapproved tag
     */


/**
 * Verifies the given user holds admin access at or above the required level.
 *
 * @param minimumLevel the minimum admin access level required
 * @param user         the user to check
 * @return the same user, if authorized
 * @throws ResponseStatusException 403 if the user is not an admin, or is
 *                                 below the required access level
 */
    public User requireAdmin(int minimumLevel, User user) {
        Admin admin = adminRepository.findByUserId(user.getUserid())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required"));

        Integer level = admin.getAdminaccesslevel();
        if (level == null || level < minimumLevel) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
        return user;
    }


/**
 * Records an endorsement given by the authenticated caller.
 *
 * <p>This is the only place a skill tag is checked against the catalogue,
 * which is what makes it safe to treat the stored value as a constant
 * everywhere else.</p>
 *
 * @param endorser the caller, resolved from the Firebase ID token
 * @param request  the validated request body
 * @return the persisted endorsement
 * @throws ResponseStatusException 400 for self-endorsement, 404 for an unknown
 *                                 endorsee, zone, tag or task, 409 for a
 *                                 duplicate, 422 for an unapproved tag
 */
    @Transactional
    public EndorsementResponseDTO create(User endorser, CreateEndorsementRequestDTO request) {
        Integer endorserId = endorser.getUserid();

        Helper endorsee = helperRepository.findById(request.endorseeId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        EndorsementSkill skill = endorsementSkillService.requireUsableSkill(request.skillTag());

        Address address = endorser.getAddressid();
        if (address == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Endorser has no address on file");
        }

        if (endorserId.equals(endorsee.getUserid().getUserid())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot endorse yourself");
        }

        Integer zoneId = address.getNeighbourhoodid().getLocationid(); 
        Location zone = locationRepository.findById(zoneId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));

        TaskInvoice task = null;
        if (request.taskId() != null) {
            task = taskInvoiceRepository.findById(request.taskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

            boolean bothOnTask = task.getHelperid() != null
                && task.getDependentid() != null
                && task.getHelperid().getUserid() != null
                && task.getDependentid().getUserId() != null
                && task.getHelperid().getUserid().getUserid() == endorsee.getUserid().getUserid()
                && task.getDependentid().getUserId().getUserid() == endorserId;

            if (!bothOnTask) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This task does not involve both users");
            }
        }

        String tag = skill.getSkillTag();

        boolean duplicate = task == null
            ? endorsementRepository.existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskidIsNull(endorserId, endorsee.getUserid().getUserid(), tag)
            : endorsementRepository.existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskid_Taskid(endorserId, endorsee.getUserid().getUserid(), tag, task.getTaskid());

        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already endorsed this user for this skill");
        }

        int weight = weightFromTrustScore(endorserId);

        Endorsement endorsement = new Endorsement(endorser, endorsee.getUserid(), zone, skill, task, weight);

        return toResponse(endorsementRepository.save(endorsement), skill);
    }

    /**
     * Derives an endorsement weight (1-5) from the endorser's helper_analytics_table row.
     * Floors the average_rating; endorsers with no analytics row or a 0.0 average
     * (e.g. no completed tasks yet) get the minimum weight of 1 rather than 0.
     */
    private int weightFromTrustScore(Integer endorserId) {
        return helperAnalyticsRepository.findByUserid_Userid(endorserId)
            .map(HelperAnalytics::getAverageRating)
            .map(rating -> {
                int floored = (int) Math.floor(rating);
                return Math.max(1, Math.min(5, floored));
            })
            .orElse(1);
    }

/**
 * Lists everything the caller has been endorsed for, across all skills.
 *
 * @param user the caller
 * @return grouped endorsements, heaviest group first
 */
    @Transactional(readOnly = true)
    public MyEndorsementResponseDTO listReceived(User user, String skillTag) {

        List<Endorsement> received = (skillTag == null || skillTag.isBlank())
            ? endorsementRepository.findReceiveWithDetails(user.getUserid())
            : endorsementRepository.findReceiveWithDetailsBySkillTag(user.getUserid(), skillTag);

        // Group endorsements by skill tag, preserving insertion order
        Map<String, List<Endorsement>> byTag = new LinkedHashMap<>();
        for (Endorsement e : received) {
            if (e.getSkillTag() == null) {
                continue;
            }
            String tag = e.getSkillTag().getSkillTag();
            byTag.computeIfAbsent(tag, k -> new ArrayList<>()).add(e);
        }

        // Fetch skill metadata (display name, category) for all tags present
        Map<String, EndorsementSkill> catalogue = catalogueFor(byTag.keySet());

        List<MyEndorsementResponseDTO.SkillGroup> groups = new ArrayList<>(byTag.size());

        for (Map.Entry<String, List<Endorsement>> entry : byTag.entrySet()) {
            String tag = entry.getKey();
            List<Endorsement> rows = entry.getValue();
            EndorsementSkill skill = catalogue.get(tag);

            long totalWeight = rows.stream()
                .mapToLong(e -> e.getWeight() == null ? 0L : e.getWeight().longValue())
                .sum();

            List<EndorsementResponseDTO> endorsementResponses = new ArrayList<>();
            for (Endorsement e : rows) {
                endorsementResponses.add(toResponse(e, skill));
            }

            MyEndorsementResponseDTO.SkillGroup group = new MyEndorsementResponseDTO.SkillGroup(
                tag,
                displayNameOf(skill, tag),
                skill == null ? null : skill.getCategory(),
                rows.size(),
                totalWeight,
                endorsementResponses
            );

            groups.add(group);
        }

        groups.sort(
            Comparator.comparingLong(MyEndorsementResponseDTO.SkillGroup::totalWeight)
                .reversed()
                .thenComparing(MyEndorsementResponseDTO.SkillGroup::skillTag)
        );

        return new MyEndorsementResponseDTO(
            user.getUserid(),
            received.size(),
            groups
        );
    }
/**
 * Lists everything the caller has been endorsed for, across all skills.
 *
 * @param user the caller
 * @return grouped endorsements, heaviest group first
 */
    public MyEndorsementResponseDTO listReceived(User user) {
        return listReceived(user, null);
    }
/**
 * Returns a compact summary of endorsements the caller has received,
 * grouped by skill, along with a small graph of their top endorsers.
 *
 * @param user the caller
 * @return the endorsement summary
 */
    @Transactional(readOnly = true)
    public EndorsementSummaryResponseDTO summarise(User user) {
        List<SkillAggregate> aggregates = endorsementRepository.aggregateReceivedBySkill(user.getUserid());

        long totalEndorsements = 0;
        long totalWeight = 0;
        LocalDateTime loastEndorsedAt = null;

        for(SkillAggregate a :aggregates) {
            totalEndorsements +=a.getEndorsementCount();
            totalWeight +=a.getTotalWeight();
            if(a.getLastEndorsedAt() != null && (loastEndorsedAt == null || a.getLastEndorsedAt().isAfter(loastEndorsedAt))) {
                loastEndorsedAt = a.getLastEndorsedAt();
            }
        }

        List<SkillAggregate> top = aggregates.stream().limit(SUMMARY_TOP_SKILLS).toList();

        Map<String, EndorsementSkill> catalogue = catalogueFor(top.stream().map(SkillAggregate::getSkillTag).toList());

        List<EndorsementSummaryResponseDTO.SkillSummary> topSkills = 
        top.stream().map(a-> {
                EndorsementSkill skill = catalogue.get(a.getSkillTag());

                return new EndorsementSummaryResponseDTO.SkillSummary(
                    a.getSkillTag(),
                    displayNameOf(skill, a.getSkillTag()),
                    skill == null ? null : skill.getCategory(),
                    a.getEndorsementCount(),
                    a.getTotalWeight()
                );
            })
            .toList();
        
        List<EndorserAggregate> topEndorsers = endorsementRepository.aggregateTopEndorsers(
            user.getUserid(), PageRequest.of(0, SUMMARY_MAX_GRAPH_NODES));

        List<EndorsementSummaryResponseDTO.EndorserNode> miniGraphNodes = topEndorsers.stream()
            .map(a -> new EndorsementSummaryResponseDTO.EndorserNode(
                a.getUserId(), a.getName()))
            .toList();

        return new EndorsementSummaryResponseDTO(
                user.getUserid(),
                totalEndorsements,
                totalWeight,
                aggregates.size(),
                endorsementRepository.countDistinctEndorsers(user.getUserid()),
                topSkills,
                loastEndorsedAt,
                miniGraphNodes);
    }

    /**
     * Expands the caller's N-hop trust-graph neighbourhood breadth-first, one
     * query per hop rather than one per node.
     *
     * @param root      the caller, used as the graph root
     * @param depth     hops to expand, clamped to {@link #MAX_GRAPH_DEPTH}
     * @param direction which edges to follow
     * @param maxNodes  node cap, clamped to {@link #MAX_GRAPH_NODES}
     * @return the nodes and edges discovered, flagged if a cap was hit
     */
    // /api/endorsment/me/graph

    @Transactional(readOnly = true)
    public TrustGraphResponseDTO neighbourhood(User root, int depth,GraphDirection direction,int maxNodes) {
        int effectiveDepth = clamp(depth,1,MAX_GRAPH_DEPTH);
        int nodeCap = clamp(maxNodes,1,MAX_GRAPH_NODES);
        GraphDirection dir = direction == null ? GraphDirection.BOTH : direction;

        Map<Integer,Integer> hopByUser = new LinkedHashMap<>();
        hopByUser.put(root.getUserid(), 0);

        List<TrustGraphResponseDTO.Edge> edges = new ArrayList<>();
        Set<String> seenEdges = new HashSet<>();
        Set<Integer> frontier = new LinkedHashSet<>(List.of(root.getUserid()));
        boolean truncated = false;

        for(int hop =1; hop<=effectiveDepth && !frontier.isEmpty();hop++) {
            Set<Integer> next = new LinkedHashSet<>();

            for (EdgeRow row : fetchEdges(frontier, dir)) {
                List<Integer> unknown = new ArrayList<>(2);
                if (!hopByUser.containsKey(row.getFromUserId())) {
                    unknown.add(row.getFromUserId());
                }
                if (!hopByUser.containsKey(row.getToUserId())) {
                    unknown.add(row.getToUserId());
                }

                if (hopByUser.size() + unknown.size() > nodeCap) {
                    truncated = true;
                    continue;
                }

                for (Integer userid : unknown) {
                    hopByUser.put(userid, hop);
                    next.add(userid);
                }

                if (seenEdges.add(edgeKey(row))) {
                    edges.add(new TrustGraphResponseDTO.Edge(
                        row.getFromUserId(), row.getToUserId(), row.getSkillTag(), row.getWeight()));
                }
            }
            frontier = next;
        }
        return new TrustGraphResponseDTO(root.getUserid(),null,effectiveDepth,dir,toNodes(hopByUser),edges,truncated);
    }

    /**
     * Builds the whole endorsement graph inside a zone for admin insights.
     *
     * @param zoneId   the zone to scope to
     * @param maxEdges edge cap, clamped to {@link #MAX_GRAPH_NODES}
     * @return the zone graph, flagged if the cap truncated it
     * @throws ResponseStatusException 404 when the zone does not exist
     */
    ///api/admin/endorsements.zone/{zoneId}/graph
    
    @Transactional(readOnly = true) 
    public TrustGraphResponseDTO zoneGraph(Integer zoneId,int maxEdges) {
        if(!locationRepository.existsById(zoneId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Zone not found");
        }

        int cap = clamp(maxEdges,1,10);
        List<EdgeRow> rows= endorsementRepository.findEdgesByZone(zoneId);
        boolean truncated = rows.size()>cap;
        if(truncated) {
            rows = rows.subList(0, cap);
        }

        Map<Integer,Integer>hopByUser = new LinkedHashMap<>();
        List<TrustGraphResponseDTO.Edge> edges = new ArrayList<>(rows.size());
        Set<String> seenEdges = new HashSet<>();

        for(EdgeRow row : rows) {
            hopByUser.putIfAbsent(row.getFromUserId(), 0);
            hopByUser.putIfAbsent(row.getToUserId(), 0);
            if(seenEdges.add(edgeKey(row))) {
                edges.add(new TrustGraphResponseDTO.Edge(
                    row.getFromUserId(),row.getToUserId(),row.getSkillTag(),row.getWeight()));
            }
        }
        return new TrustGraphResponseDTO(
            null,zoneId,0, null,toNodes(hopByUser), edges,truncated);
    }
    

    /** Pairs a predecessor node with the hop that connects it to the node it was discovered from. */
    private record PredecessorEdge(Integer predecessorId, TrustPathResponseDTO.Hop hop) {}
    /**
     * Finds every shortest chain of endorsements running from the caller to a
     * target user, following edges in the vouching direction.
     *
     * <p>Breadth-first search records each node's predecessors at exactly one hop
     * less than its own distance, so backtracking from the target enumerates all
     * shortest paths and cannot revisit a node.</p>
     *
     * @param from     the caller, resolved from the token
     * @param toUserId the target user
     * @param maxDepth search limit, clamped to {@link #MAX_PATH_DEPTH}
     * @param maxPaths result cap, clamped to {@link #MAX_PATHS_RETURNED}
     * @return the shortest paths, or a disconnected result
     * @throws ResponseStatusException 400 when the target is the caller,
     *                                 404 when the target does not exist
     */
    //api/endorsements/trust-paths
    @Transactional(readOnly = true)
    public TrustPathResponseDTO trustPaths(User from, Integer toUserId, int maxDepth, int maxPaths) {
        Integer sourceId = from.getUserid();

        if (sourceId.equals(toUserId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot search a path to yourself");
        }
        if (!userRepository.existsById(toUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        int depthLimit = clamp(maxDepth, 1, 6); // separate constant from graph's MAX_GRAPH_DEPTH
        int pathLimit = clamp(maxPaths, 1, MAX_PATHS_RETURNED);

        Map<Integer, Integer> distance = new HashMap<>();
        Map<Integer, List<PredecessorEdge>> predecessors = new HashMap<>();
        distance.put(sourceId, 0);

        Set<Integer> frontier = new LinkedHashSet<>(List.of(sourceId));
        int found = -1;

        for (int hop = 1; hop <= depthLimit && !frontier.isEmpty() && found < 0; hop++) {
            Set<Integer> next = new LinkedHashSet<>();

            for (EdgeRow row : endorsementRepository.findAllEdges(frontier)) {
                Integer endorser = row.getFromUserId();
                Integer endorsee = row.getToUserId();

                // forward: endorser already known at hop-1 -> endorsee newly/still reachable at hop
                Integer endorserDist = distance.get(endorser);
                if (endorserDist != null && endorserDist == hop - 1) {
                    Integer endorseeDist = distance.get(endorsee);
                    if (endorseeDist == null) {
                        distance.put(endorsee, hop);
                        next.add(endorsee);
                        predecessors.computeIfAbsent(endorsee, k -> new ArrayList<>())
                            .add(new PredecessorEdge(endorser, toHop(row)));
                    } else if (endorseeDist == hop) {
                        predecessors.computeIfAbsent(endorsee, k -> new ArrayList<>())
                            .add(new PredecessorEdge(endorser, toHop(row)));
                    }
                }

                // backward: endorsee already known at hop-1 -> endorser newly/still reachable at hop
                Integer endorseeDist2 = distance.get(endorsee);
                if (endorseeDist2 != null && endorseeDist2 == hop - 1) {
                    Integer endorserDist2 = distance.get(endorser);
                    if (endorserDist2 == null) {
                        distance.put(endorser, hop);
                        next.add(endorser);
                        predecessors.computeIfAbsent(endorser, k -> new ArrayList<>())
                            .add(new PredecessorEdge(endorsee, toHop(row)));
                    } else if (endorserDist2 == hop) {
                        predecessors.computeIfAbsent(endorser, k -> new ArrayList<>())
                            .add(new PredecessorEdge(endorsee, toHop(row)));
                    }
                }
            }

            if (distance.containsKey(toUserId)) {
                found = distance.get(toUserId);
            }
            frontier = next;
        }

        if (found < 0) {
            return new TrustPathResponseDTO(sourceId, toUserId, false, -1, List.of());
        }

        // Build paths with raw user IDs first, then enrich with names in one batch lookup.
        List<List<Integer>> rawPaths = new ArrayList<>();
        List<List<TrustPathResponseDTO.Hop>> rawHops = new ArrayList<>();
        List<Integer> rawWeights = new ArrayList<>();
        collectPath(toUserId, sourceId, predecessors, new ArrayDeque<>(), rawPaths, rawHops, rawWeights, pathLimit);

        Set<Integer> allUserIds = rawPaths.stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
        Map<Integer, User> users = userRepository.findAllById(allUserIds).stream()
            .collect(Collectors.toMap(User::getUserid, u -> u));

        List<TrustPathResponseDTO.Path> paths = new ArrayList<>();
        for (int i = 0; i < rawPaths.size(); i++) {
            List<TrustPathResponseDTO.PathUser> pathUsers = rawPaths.get(i).stream()
                .map(id -> {
                    User u = users.get(id);
                    String name = u == null ? null : (u.getFirstName() + " " + u.getLastName()); // confirm real field names
                    return new TrustPathResponseDTO.PathUser(id, name);
                })
                .toList();
            paths.add(new TrustPathResponseDTO.Path(pathUsers, rawHops.get(i), rawWeights.get(i)));
        }

        return new TrustPathResponseDTO(sourceId, toUserId, true, found, paths);
    }

    /**
     * Returns the approved skill catalogue grouped by category — the set of legal
     * values a client may submit.
     *
     * @return the catalogue; categories are alphabetical
     */
    //api/endorsements/skills

    @Transactional(readOnly = true)
    public SkillCatalogueResponseDTO catalogue() {
        List<EndorsementSkill> skills = endorsementSkillsRepository.findApprovedOrdered();
 
        Map<String, List<SkillCatalogueResponseDTO.SkillItem>> byCategory = new LinkedHashMap<>();
        for (EndorsementSkill skill : skills) {
            String category = skill.getCategory() == null || skill.getCategory().isBlank() ? UNCATEGORISED : skill.getCategory();

            byCategory.computeIfAbsent(category, k->new ArrayList<>()).add(
                        new SkillCatalogueResponseDTO.SkillItem(skill.getSkillTag(),skill.getDisplayName())
            );
        }
 
        List<SkillCatalogueResponseDTO.CategoryGroup> groups = byCategory.entrySet().stream()
                .map(e -> new SkillCatalogueResponseDTO.CategoryGroup(e.getKey(), e.getValue()
                )
            ).toList();
 
        return new SkillCatalogueResponseDTO(skills.size(), groups);
    }

    //helpers
        /**
     * Loads catalogue entries for the given tags in one query.
     *
     * <p>Entries can be missing if a tag was removed from the catalogue after
     * endorsements were recorded against it. Callers fall back to the raw tag
     * rather than failing, so historical rows stay readable. The catalogue is
     * small and near-static, so this is a candidate for {@code @Cacheable} if it
     * ever shows up in profiling.</p>
     *
     * @param tags the tags to resolve
     * @return tag to catalogue entry, omitting unknown tags
     */
    private Map<String,EndorsementSkill> catalogueFor(Collection<String> tags) {
        if(tags.isEmpty()) {
            return Map.of();
        }
        return endorsementSkillsRepository.findAllById(tags).stream()
            .collect(Collectors.toMap(EndorsementSkill::getSkillTag, Function.identity()));
    }

    /**
     * @return the catalogue display name ,or the raw tage when the entry
     */
    private String displayNameOf(EndorsementSkill skill,String tag) {
        return skill == null ? tag: skill.getDisplayName();
    }

    /**
     * @fetches one hop of edges for the given frontier
     */
    private List<EdgeRow> fetchEdges(Set<Integer> frontier,GraphDirection direction) {
        return switch (direction) {
            case OUT -> endorsementRepository.findOutgoingEdges(frontier);
            case IN -> endorsementRepository.findIncomingEdges(frontier);
            case BOTH -> endorsementRepository.findAllEdges(frontier);
        };
    }

    /**
     * walks the predecessor map brackwards from the ytarget
     */
    private void collectPath(Integer current,
                          Integer sourceId,
                          Map<Integer, List<PredecessorEdge>> predecessors,
                          Deque<TrustPathResponseDTO.Hop> stack,
                          List<List<Integer>> outUserIds,
                          List<List<TrustPathResponseDTO.Hop>> outHops,
                          List<Integer> outWeights,
                          int maxPaths) {
            if (outUserIds.size() >= maxPaths) {
                return;
            }

            if (current.equals(sourceId)) {
                // The stack was pushed target-first, so iterating it yields source-to-target order.
                List<TrustPathResponseDTO.Hop> hops = new ArrayList<>(stack);
                List<Integer> userIds = new ArrayList<>(hops.size() + 1);
                userIds.add(sourceId);
                int totalWeight = 0;
                for (TrustPathResponseDTO.Hop h : hops) {
                    userIds.add(h.toUserId());
                    totalWeight += h.weight() == null ? 0 : h.weight();
                }
                outUserIds.add(userIds);
                outHops.add(hops);
                outWeights.add(totalWeight);
                return;
            }

            for (PredecessorEdge pe : predecessors.getOrDefault(current, List.of())) {
                if (outUserIds.size() >= maxPaths) {
                    return;
                }
                stack.push(pe.hop());
                collectPath(pe.predecessorId(), sourceId, predecessors, stack, outUserIds, outHops, outWeights, maxPaths);
                stack.pop();
            }
        }

     /**
     * Converts the discovered hop map into response nodes, root first.
     *
     * <p>Display names are left null: fill them in from the {@code User} entity
     * once its name field is confirmed, ideally with a single
     * {@code findAllById(hopByUser.keySet())} lookup rather than per node.</p>
     */
     private List<TrustGraphResponseDTO.Node> toNodes(Map<Integer, Integer> hopByUser) {
        Map<Integer, User> users = userRepository.findAllById(hopByUser.keySet()).stream()
            .collect(Collectors.toMap(User::getUserid, u -> u));

        List<TrustGraphResponseDTO.Node> nodes = new ArrayList<>(hopByUser.size());
        for (Map.Entry<Integer, Integer> entry : hopByUser.entrySet()) {
            User user = users.get(entry.getKey());
            String displayName = user == null ? null : (user.getFirstName() + " " + user.getLastName()); // confirm actual field names/style
            nodes.add(new TrustGraphResponseDTO.Node(entry.getKey(), displayName, entry.getValue()));
        }
        return nodes;
    }

    /** Stable identity for a parallel edge, so the same pair can differ by tag. */
    private String edgeKey(EdgeRow row) {
        return row.getFromUserId() + ">"+row.getToUserId()+">"+row.getSkillTag();
    }

    private TrustPathResponseDTO.Hop toHop(EdgeRow row) {
        return new TrustPathResponseDTO.Hop(
            row.getFromUserId(),row.getToUserId(),row.getSkillTag(),row.getWeight());
    }

    private int clamp(int value,int min,int max) {
        return Math.max(min, Math.min(max,value));
    }
    /**
     * Maps an entity onto its response shape.
     *
     * @param e     the endorsement, with its user and zone associations loaded
     * @param skill the catalogue entry for the tag, or {@code null} if it is gone
     */
    private EndorsementResponseDTO toResponse(Endorsement e, EndorsementSkill skill) {
            String tag = e.getSkillTag() == null? null : e.getSkillTag().getSkillTag();

            return new EndorsementResponseDTO(
                e.getEndorsementId(), 
                e.getEndorserid().getUserid(), 
                e.getEndorseeid().getUserid(), 
                tag,
                displayNameOf(skill,tag),
                skill == null ? null : skill.getCategory(), 
                e.getZoneid().getLocationid(), 
                e.getTaskid() == null ? null : e.getTaskid().getTaskid(), 
                e.getWeight(), 
                e.getCreatedAt());
    }
} 



