package com.app.api.models;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
 
import java.time.LocalDateTime;
import java.util.UUID;
 
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * One row of materialized community-detection output: "in zone X, user Y
 * currently belongs to cluster N".
 *
 * <p>This is a cache, not a relational entity in its own right — the admin
 * dashboard reads it instead of running detection live, per 2.7. It is
 * rebuilt wholesale on every run of {@code EndorsementClusterService}
 * (existing rows for a zone are deleted and replaced inside one transaction),
 * so it deliberately stores plain {@code location_id}/{@code user_id}
 * integers rather than {@code @ManyToOne} associations: the service already
 * has the ids from the edge rows it clustered, and a dashboard read wants ids
 * and labels back, not a joined {@link User} or {@link Location} graph.</p>
 */

@Entity 
@Getter 
@Setter
@NoArgsConstructor 
@EqualsAndHashCode(onlyExplicitlyIncluded  = true)
@Table(name = "endorsement_cluster_cache")
public class EndorsementClusterCache {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="cluster_cache_id")
    @EqualsAndHashCode.Include
    private Long clusterCacheId;
    
    /** the zone this membership applies to */
    @Column (name = "location_id",nullable = false)
    private int zoneId;

    /** The member; {@link User#getUserid()}. */
    @Column(name = "user_id",nullable = false)
    private int userId;

    /**
     * Community label from the Louvain pass, renumbered to {@code 0..k-1}
     * within this zone's run. Only meaningful together with {@link #zoneId}.
     */
    @Column(name ="cluster_label",nullable = false)
    private int clusterLabel;

    /** Summed endorsement weight to neighbours in the same cluster. */
    @Column(name = "internal_degree",nullable = false)
    private int internalDegree;

     /** Summed endorsement weight to all neighbours, in or out of the cluster. */
    @Column(name = "total_degree",nullable = false)
    private int totalDegree;

    /** Groups every row written by one run of the job together. */
    @Column(name = "run_id",nullable = false)
    private UUID runId;

    @Column(name = "computed_at",nullable = false)
    private LocalDateTime computedAt;

    /**
     * Constructs a fully-populated cache row.
     *
     * @param zoneId         the zone this membership belongs to
     * @param userId         the member
     * @param clusterLabel   the community label within this zone's run
     * @param internalDegree summed weight to same-cluster neighbours
     * @param totalDegree    summed weight to all neighbours
     * @param runId          the run that produced this row
     * @param computedAt     when this row was computed
     */
    public EndorsementClusterCache(int zoneId,int userId,int clusterLabel,int internalDegree,int totalDegree,UUID runId,LocalDateTime computedAt) {
        this.zoneId=zoneId;
        this.clusterLabel=clusterLabel;
        this.computedAt=computedAt;
        this.internalDegree=internalDegree;
        this.userId = userId;
        this.totalDegree=totalDegree;
        this.runId=runId;
    }
}

