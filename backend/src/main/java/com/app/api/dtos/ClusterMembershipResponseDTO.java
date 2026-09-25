package com.app.api.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** * @param zoneId      the zone
 * @param computedAt  when the cached run was produced, {@code null} if never run
 * @param runId       identifies the run that produced this snapshot, {@code null} if never run
 * @param clusterCount number of distinct clusters in the snapshot
 * @param members     cached memberships, lowest cluster label first
 */
public record ClusterMembershipResponseDTO(
    Integer zoneId,
    LocalDateTime computedAt,
    UUID runId,
    int clusterCount,
    List<Member> members 
) {

    /**
     * One user's cached cluster membership.
     *
     * @param userId         the member
     * @param clusterLabel   community label, meaningful only within this zone/run
     * @param internalDegree summed endorsement weight to same-cluster neighbours
     * @param totalDegree    summed endorsement weight to all neighbours
     */
    public record Member(Integer userId,int clusterLabel,int internalDegree,int totalDegree) {

    }
}
