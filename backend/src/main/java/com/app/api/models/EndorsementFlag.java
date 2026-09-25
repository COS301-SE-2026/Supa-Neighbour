package com.app.api.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * One detected anti-abuse pattern instance (2.8): "this set of users, this
 * pattern type, first/last seen at these times." Unlike {@link
 * EndorsementClusterCache}, this is NOT wiped and replaced each run — a flag
 * persists across nightly scans so an admin's review decision survives. Each
 * run either bumps an existing open flag's {@code lastDetectedAt}/{@code
 * occurrenceCount} (same pattern type + same participant set, status still
 * {@code open} or {@code investigate}) or inserts a new one; a {@code
 * dismiss}ed flag is never re-bumped, so the same pattern reappearing after a
 * dismissal creates a fresh flag rather than reopening the dismissed one.
 *
 * <p>{@code zoneId} is null for patterns detected in a global (cross-zone)
 * pass — {@code mutual_ring} and {@code timestamp_anomaly} — since a
 * colluding pair's endorsements aren't guaranteed to share one zone. It's
 * always populated for {@code island_group}, which reads a single zone's
 * {@link EndorsementClusterCache}.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "endorsement_flag_table")
public class EndorsementFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flag_id")
    @EqualsAndHashCode.Include
    private Long flagId;

    /** One of: mutual_ring, sudden_spike, island_group, timestamp_anomaly. */
    @Column(name = "pattern_type", nullable = false, length = 30)
    private String patternType;

    /** Null for cross-zone patterns (mutual_ring, timestamp_anomaly); see class javadoc. */
    @Column(name = "location_id")
    private Integer zoneId;

    /**
     * Pattern-specific summary, e.g. the shared-task count for mutual_ring or
     * the gap in seconds for timestamp_anomaly. Interpretation depends on
     * {@link #patternType}; kept as a single numeric column rather than a
     * wider table since the four patterns don't share a summary shape.
     */
    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // 'open' | 'investigate' | 'dismiss'

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "first_detected_at", nullable = false, updatable = false)
    private LocalDateTime firstDetectedAt;

    @Column(name = "last_detected_at", nullable = false)
    private LocalDateTime lastDetectedAt;

    /** Groups every flag row (new or bumped) written by one scan together. */
    @Column(name = "run_id", nullable = false)
    private UUID runId;

    public EndorsementFlag(String patternType, Integer zoneId, Double metricValue,
                            LocalDateTime detectedAt, UUID runId) {
        this.patternType = patternType;
        this.zoneId = zoneId;
        this.metricValue = metricValue;
        this.status = "open";
        this.occurrenceCount = 1;
        this.firstDetectedAt = detectedAt;
        this.lastDetectedAt = detectedAt;
        this.runId = runId;
    }
}