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
public record TrustGraphResponseDTO(
    Integer rootUserId,
    Integer zoneId,
    int depth,
    GraphDirection direction,
    List<Node> nodes,
    List<Edge> edges,
    boolean truncated
) {

/**
* A user in the graph.
* @param userId      the user
* @param displayName the user's display name, {@code null} when not resolved
* @param hop         distance in hops from the root; 0 for the root itself,
*                    and 0 for every node of a zone-wide graph
*/
    public record Node(Integer userId,String displayName, int hop) {
    }

/**
* A directed endorsement edge.
*
* @param fromUserId endorser
* @param toUserId   endorsee
* @param skillTag   skill the endorsement was for
* @param weight     endorsement weight
*/
public record Edge(Integer fromUserId,Integer toUserId,String skillTag,Integer weight){

}
}


