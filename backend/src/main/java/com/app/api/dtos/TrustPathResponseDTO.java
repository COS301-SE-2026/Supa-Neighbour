package com.app.api.dtos;

import java.util.List;
/**
 * Response for {@code GET /api/endorsements/trust-paths} — the shortest chains of
 * endorsements linking the caller to a target user.
 *
 * @param fromUserId the caller, resolved from the token
 * @param toUserId   the requested target
 * @param connected  whether any path was found within the depth limit
 * @param length     hop count of the shortest path, or {@code -1} when disconnected
 * @param paths      all shortest paths found, capped by the service
 */
public record TrustPathResponseDTO(
        Integer fromUserId,
        Integer toUserId,
        boolean connected,
        int length,
        List<Path> paths
) {
    /**
     * One path through the trust graph.
     *
     * @param userIds     the node sequence from source to target inclusive
     * @param hops        the edges joining those nodes
     * @param totalWeight summed weight of the hops, useful for ranking ties
     */
    public record Path(List<PathUser> users, List<Hop> hops, int totalWeight) {}
    public record PathUser(Integer userId, String name) {}

    /**
     * One edge on a path.
     *
     * @param fromUserId endorser
     * @param toUserId   endorsee
     * @param skillTag   skill the endorsement was for
     * @param weight     endorsement weight
     */
    public record Hop(Integer fromUserId,Integer toUserId,String skillTag,Integer weight){
    }
}

