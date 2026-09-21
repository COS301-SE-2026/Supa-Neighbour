package com.app.api.dtos;

import java.util.List;

import com.app.api.dtos.endorsement.GraphDirection;

/**
 * Response for the N-hop neighbourhood and zone-graph endpoints.
 *
 * @param rootUserId the traversal root, {@code null} for a zone-wide graph
 * @param zoneId     the zone scope, {@code null} for a user-rooted graph
 * @param depth      hops traversed
 * @param direction  edge direction followed
 * @param nodes      users in the neighbourhood, root first
 * @param edges      endorsement edges between those users
 * @param truncated  {@code true} when a node or edge cap cut the result short
 */

public record TrustGraphResponse(
        Integer rootUserId,
        Integer zoneId,
        int depth,
        GraphDirection direction,
        List<Node> nodes,
        List<Edge> edges,
        boolean truncated
) {
    /** Which endorsement edges a traversal follows. */
    public enum GraphDirection { OUT, IN, BOTH }

    public record Node(Integer userId, String displayName, int hop) { }

    public record Edge(Integer fromUserId, Integer toUserId, String skillTag, Integer weight) { }

}


