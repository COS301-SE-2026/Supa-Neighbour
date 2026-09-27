package com.app.api.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Response for {@code POST /api/admin/endorsements/zone/{zoneId}/cluster-analysis}
 * — a summary of the run that just completed, not the full membership list
 * (fetch {@code GET .../clusters} for that).
 *
 * @param zoneId       the zone that was analysed
 * @param runId        identifies this run; matches the rows it wrote to the cache
 * @param computedAt   when the run completed
 * @param nodeCount    users with at least one endorsement edge in this zone
 * @param edgeCount    distinct undirected user pairs clustered
 * @param clusterCount number of clusters found
 * @param modularity   the final partition's modularity score
 */
public record ClusterAnalysisRunResponseDTO(
    Integer zoneId,
    UUID runId,
    LocalDateTime computedAt,
    int nodeCount,
    int edgeCount, 
    int clusterCount,
    double modularity
) {
    
}
