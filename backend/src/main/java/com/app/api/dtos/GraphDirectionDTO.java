package com.app.api.dtos;

/**
 * Which endorsement edges a graph traversal should follow.
 *
 * <p>An edge always points from endorser to endorsee, i.e. "A vouches for B".</p>
 */
public enum GraphDirectionDTO {
    

    /** Follow endorsements the node has given. */
    OUT,

    /** Follow endorsements the node has received. */
    IN,

    /** Treat the graph as undirected. */
    BOTH
}
