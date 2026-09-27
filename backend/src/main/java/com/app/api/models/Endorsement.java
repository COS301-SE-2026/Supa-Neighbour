package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "endorsement_table")
public class Endorsement{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "endorsement_id")
    private Integer endorsementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endorser_id", nullable = false)
    private User endorserid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endorsee_id", nullable = false)
    private User endorseeid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location zoneid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_tag", nullable = false)
    private EndorsementSkill skillTag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskInvoice taskid;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    
    /**
     * No-argument constructor required by JPA.
     */
    public Endorsement() {
        // needed for JPA
    } 

    /**
     * Constructs a new {@code Endorsement} with the given details.
     *
     * @param endorserid the {@link User} giving the endorsement
     * @param endorseeid the {@link User} receiving the endorsement
     * @param zoneid     the {@link Location} zone of the endorsement
     * @param skillTag   the {@link EndorsementSkill} being endorsed
     * @param taskid     the optional {@link TaskInvoice} associated with the endorsement
     * @param weight     the weight of the endorsement
     * @throws IllegalArgumentException if {@code endorserid} and {@code endorseeid}
     *                                  refer to the same user (self-endorsement is not allowed)
     */
    public Endorsement(User endorserid, User endorseeid, Location zoneid,
                        EndorsementSkill skillTag, TaskInvoice taskid, Integer weight) {
        if (endorserid.equals(endorseeid)) {
            throw new IllegalArgumentException("A user cannot endorse themselves");
        }
        this.endorserid = endorserid;
        this.endorseeid = endorseeid;
        this.zoneid = zoneid;
        this.skillTag = skillTag;
        this.taskid = taskid;
        this.weight = weight;
        this.createdAt = LocalDateTime.now();
    }


    /**
     * JPA lifecycle callback that stamps {@link #createdAt} immediately before the
     * entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
