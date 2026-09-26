package com.app.api.services;


import com.app.api.dtos.ClusterAnalysisRunResponseDTO;
import com.app.api.dtos.ClusterMembershipResponseDTO;
import com.app.api.models.EndorsementClusterCache;
import com.app.api.repositories.EndorsementRepository;
import com.app.api.repositories.ClusterCacheRepository;
import com.app.api.repositories.EndorsementRepository.EdgeRow;
import com.app.api.repositories.LocationRepository;

import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;


 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
 
/**
 * Cluster analysis job (2.7): a scheduled nightly (or admin-triggered
 * on-demand) pass that groups each zone's endorsers into trust communities
 * using a lightweight, single-phase Louvain-style local-moving algorithm, and
 * materializes the result into {@code endorsement_cluster_cache}. The admin
 * dashboard reads that cache; it never runs detection live.
 *
 * <p>"Lightweight" here specifically means: one Louvain phase (greedy local
 * node-moving to maximize modularity) with no second phase of recursive graph
 * aggregation into super-nodes. There's no general-purpose graph library in
 * the project, so this is hand-rolled; what it does use is Eclipse
 * Collections' primitive int-keyed/int-valued maps ({@link MutableIntIntMap},
 * {@link MutableIntObjectMap}) instead of {@code java.util.Map<Integer,
 * Integer>} for the adjacency structure and the per-node community-weight
 * tally, since those are rebuilt on every one of the algorithm's inner-loop
 * iterations (node count &times; pass count) and every {@code Integer} in a
 * {@code HashMap<Integer,Integer>} there is a boxed object allocation this
 * avoids entirely.</p>
 */
@Service 
public class EndorsementClusterService {

    private static final Logger LOG = LoggerFactory.getLogger(EndorsementClusterService.class);


    /**
     * Cap on local-moving passes over all nodes. A pass that produces zero
     * moves ends the algorithm early; this only bounds the pathological case
     * where nodes keep oscillating.
     */
    private static final int MAX_LOCAL_MOVING_PASSES=20;


/** Tolerance for comparing modularity gains, to avoid float-noise ties. */
    private static final double GAIN_EPSILON = 1e-9;

    private final EndorsementRepository endorsementRepository;
    private final ClusterCacheRepository clusterCacheRepository;
    private final LocationRepository locationRepository;
    
    /**
     * @param endorsementRepository  supplies the per-zone edge list to cluster
     * @param clusterCacheRepository the materialized cache this job writes to
     * @param locationRepository     used to validate a requested zone exists
     */
    public EndorsementClusterService( EndorsementRepository endorsementRepository, ClusterCacheRepository clusterCacheRepository, LocationRepository locationRepository) {
        this.clusterCacheRepository=clusterCacheRepository;
        this.endorsementRepository=endorsementRepository;
        this.locationRepository=locationRepository;
    }

    /**
     * Nightly entry point: runs cluster analysis for every zone that has
     * endorsements. Each zone is processed independently — a failure in one
     * is logged and skipped, so the rest still run. Scheduled at 03:00,
     * ahead of the abuse scan at 04:00.
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void runNightlyForAllZones() {
        List<Integer> zoneIds = endorsementRepository.findDistinctZoneIds();
        LOG.info("Nightly cluster analysis starting for {} zone(s)",zoneIds.size());

        int succeeded = 0;
        for(Integer zoneId: zoneIds) {
            try {
                ClusterAnalysisRunResponseDTO result = runForZone(zoneId);
                LOG.info("Zone {}:{} nodes,{} edges,{} clusters,modularity {}",
                    zoneId,result.nodeCount(),result.edgeCount(),
                    result.clusterCount(),result.modularity());

                succeeded++;
            }catch(Exception e) {
                LOG.error("Cluster analysis failws for zone {}",zoneId,e);
            }
        }
        LOG.info("Nightly cluster analysis complete: {}/{} zone(s) succeded",succeeded,zoneIds.size());
    }

     /**
     * Runs community detection for a single zone: clears the zone's cached
     * clusters, recomputes them from the zone's endorsement edges, and writes
     * the new cache rows.
     *
     * <p>Zones with no edges produce an empty cache and a zero-valued result
     * rather than an error.</p>
     *
     * @param zoneId the zone to analyse
     * @return a summary of the run (node/edge/cluster counts and modularity)
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND} if no
     *         location exists with {@code zoneId}
     */
    @Transactional 
    public ClusterAnalysisRunResponseDTO runForZone(int zoneId) {
        if(!locationRepository.existsById(zoneId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "cannot find zone");
        }

        List<EdgeRow> edges = endorsementRepository.findEdgesByZone(zoneId);

        UUID runId = UUID.randomUUID();
        LocalDateTime computedAt = LocalDateTime.now();

        clusterCacheRepository.deleteByZoneId(zoneId);

        if(edges.isEmpty()) {
            return new ClusterAnalysisRunResponseDTO(zoneId,runId,computedAt,0,0,0,0.0);
        }

        LouvainResult result = detectCommunities(edges);

        List<EndorsementClusterCache> rows = new ArrayList<>(result.nodes().size());
        result.nodes().forEachKeyValue((userId,node) -> rows.add(new EndorsementClusterCache(zoneId,userId,node.clusterLabel(),node.internalDegree(),node.totalDegree(),runId,computedAt)));
        clusterCacheRepository.saveAll(rows);

        return new ClusterAnalysisRunResponseDTO(zoneId,runId,computedAt,result.nodes().size(),result.edgeCount(),result.clusterCount(),result.modularity());
    }

    /**
     * Reads a zone's current cluster membership straight from the cache. Never
     * triggers detection — an empty, never-run zone comes back as a valid
     * empty snapshot rather than an error.
     *
     * @param zoneId the zone
     * @return the cached snapshot
     * @throws ResponseStatusException 404 if the zone does not exist
     */
    public ClusterMembershipResponseDTO readCached(int zoneId) {
        if(!locationRepository.existsById(zoneId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"couldn't be found");
        }

        List<EndorsementClusterCache> rows =
            clusterCacheRepository.findByZoneIdOrderByClusterLabelAscUserIdAsc(zoneId);

        if(rows.isEmpty()) {
            return new ClusterMembershipResponseDTO(zoneId,null,null,0,List.of());
        }

        EndorsementClusterCache first = rows.get(0);
        int clusterCount = (int) rows.stream().mapToInt(EndorsementClusterCache::getClusterLabel).distinct().count();

        List<ClusterMembershipResponseDTO.Member> memebers = rows.stream()
            .map(r -> new ClusterMembershipResponseDTO.Member(
                        r.getUserId(),r.getClusterLabel(),r.getInternalDegree(),r.getTotalDegree()))
                        .toList();

        return new ClusterMembershipResponseDTO(zoneId,first.getComputedAt(),first.getRunId(),clusterCount,memebers);
    }

    private LouvainResult detectCommunities(List<EdgeRow> edgeRows) {
        MutableIntObjectMap<MutableIntIntMap> adjacency = buildUndirectedAdjacency(edgeRows);

        int[] userIds= adjacency.keySet().toSortedArray();
        int n = userIds.length; 

        if(n == 0) {
            return new LouvainResult(new IntObjectHashMap<>(), 0, 0, 0.0);
        }

        MutableIntIntMap indexOf = new IntIntHashMap();
        for (int i = 0; i < userIds.length; i++) {
            indexOf.put(userIds[i],i);
        }

        int[][] neighbourIndex = new int[n][];
        int[][] neighbourWeight = new int[n][];
        int[] degree = new int[n];
        for (int i = 0; i < n; i++) {
            MutableIntIntMap neighbours = adjacency.get(userIds[i]);
            int[] idxArr = new int[neighbours.size()];
            int[] wArr = new int[neighbours.size()];
            int cursor =0;
            int deg=0;
            for(IntIntPair pair : neighbours.keyValuesView()) {
                idxArr[cursor]= indexOf.get(pair.getOne());
                wArr[cursor]= pair.getTwo();
                deg += pair.getTwo();
                cursor++;
            }
            neighbourIndex[i]=idxArr;
            neighbourWeight[i]=wArr;
            degree[i]= deg;
        }

        int edgeCount = 0;
        long totalDirectedWeight = 0;
        for (int i = 0; i < n; i++) {
            totalDirectedWeight+=degree[i];
            for(int j : neighbourIndex[i]) {
                if(j>i) {
                    edgeCount++;
                }
            }
        }
        long m = totalDirectedWeight/2;

        if(m==0){
            MutableIntObjectMap<NodeResult> nodes = new IntObjectHashMap<>();
            for (int i = 0; i < n; i++) {
                nodes.put(userIds[i], new NodeResult(i, 0,0));
            }
            return new LouvainResult(nodes, 0, n, 0.0);
        }

        int[] community = localMovingPhase(n,neighbourIndex,neighbourWeight,degree,m);

        MutableIntIntMap labelOf = new IntIntHashMap();
        int[] finalLabel = new int[n];
        int nextLabel = 0;
        for (int i = 0; i < n; i++) {
            int raw = community[i];
            if(!labelOf.containsKey(raw)) {
                labelOf.put(raw, nextLabel);
                nextLabel++;
            }
            finalLabel[i] = labelOf.get(raw);
        }
        int clusterCount = nextLabel;

        int[] internalDegree = new int[n];
        long[] clusterInternalWeight=new long[clusterCount];
        long[] clusterTotalDegree = new long[clusterCount];

        for (int i = 0; i < n; i++) {
            clusterTotalDegree[finalLabel[i]] +=degree[i];
            for (int k = 0; k < neighbourIndex[i].length; k++) {
                int j = neighbourIndex[i][k];
                if(finalLabel[j] == finalLabel[i]) {
                    internalDegree[i] += neighbourWeight[i][k];
                    if(j>i) {
                        clusterInternalWeight[finalLabel[i]] += neighbourWeight[i][k];
                    }
                }
            }
        }

        double modularity = 0.0;
        double m2 = 2.0*m;
        for(int c = 0;c<clusterCount;c++) {
            double inTerm= clusterInternalWeight[c]/(double)m;
            double totFraction = clusterTotalDegree[c]/m2;
            modularity += inTerm - totFraction * totFraction;
        }

        MutableIntObjectMap<NodeResult> nodes = new IntObjectHashMap<>();
        for (int i = 0; i < n; i++) {
            nodes.put(userIds[i], new NodeResult(finalLabel[i], internalDegree[i], degree[i]));
        }
        return new LouvainResult(nodes, edgeCount, clusterCount, modularity);
    }

    private int[] localMovingPhase(int n,int[][] neighbourIndex,int[][] neighbourWeight,int[] degree,long m) {
        int[] community = new int[n];
        long[] communityTotalDegree = new long[n];
        for (int i = 0; i < n; i++) {
            community[i] = i;
            communityTotalDegree[i]= degree[i];
        }
        double m2=2.0*m;

        for (int pass = 0; pass < MAX_LOCAL_MOVING_PASSES; pass++) {
            boolean moved = false;

            for (int i = 0; i < communityTotalDegree.length; i++) {
                int oldComm = community[i];
                communityTotalDegree[oldComm]-=degree[i];

                MutableIntIntMap neighbourCommunityWeight = new IntIntHashMap();
                for (int k = 0; k < neighbourIndex[i].length; k++) {
                    int neighborComm = community[neighbourIndex[i][k]]; 
                    neighbourCommunityWeight.addToValue(neighborComm, neighbourWeight[i][k]);
                }

                int bestComm = n + i;
                double bestGain = 0.0;

                for(IntIntPair candidate: neighbourCommunityWeight.keyValuesView()) {
                    int c = candidate.getOne();
                    int weightToC= candidate.getTwo();
                    double gain = weightToC- (communityTotalDegree[c]*(double) degree[i]/m2);

                    boolean strictlyBetter = gain>bestGain+GAIN_EPSILON;
                    boolean tiedButRestoresOldCommunity =
                        Math.abs(gain-bestGain) <=GAIN_EPSILON && c== oldComm &&bestComm!=oldComm;

                    if(strictlyBetter || tiedButRestoresOldCommunity) {
                        bestGain=gain;
                        bestComm=c;
                    }
                }

                communityTotalDegree[bestComm] +=degree[i];
                community[i] = bestComm;
                if(bestComm != oldComm) {
                    moved = true;
                }
            }
            if(!moved) {
                break;
            }
        }

        return community;
    }


    private MutableIntObjectMap<MutableIntIntMap> buildUndirectedAdjacency(List<EdgeRow> edgeRows) {
        MutableIntObjectMap<MutableIntIntMap> adjacency = new IntObjectHashMap<>();
        for(EdgeRow row: edgeRows) {
            int u=row.getFromUserId();
            int v = row.getToUserId();
            if(u==v) {
                continue;
            }
            int weight= row.getWeight() == null ? 1 : row.getWeight();
            adjacency.getIfAbsentPut(u,IntIntHashMap::new).addToValue(v,weight);
            adjacency.getIfAbsentPut(v, IntIntHashMap::new).addToValue(u, weight);
        }
        return adjacency;
    }



    /**
     * @param nodes        userId to that node's cluster result
     * @param edgeCount    distinct undirected pairs clustered
     * @param clusterCount number of clusters found
     * @param modularity   the final partition's modularity score
     */

    private record LouvainResult(IntObjectMap<NodeResult> nodes,int edgeCount,int clusterCount,double modularity) {
    }


   /**
     * @param clusterLabel   compact 0..k-1 community label
     * @param internalDegree summed weight to same-cluster neighbours
     * @param totalDegree    summed weight to all neighbours
     */
    private record NodeResult(int clusterLabel,int internalDegree,int totalDegree) {
    }
}
