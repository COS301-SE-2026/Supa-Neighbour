package com.app.api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a helper user who provides assistance with tasks.
 * Helpers specialize in certain task types and can earn badges.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "helper_table")
public class Helper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helper_id")
    private int helperid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userid;

    @ManyToOne
    @JoinColumn(name = "task_type_id")
    private TaskType taskTypeid;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badges badgeid;

    @Column(name = "helper_xp")
    private int helperXp;

    @Column(name = "isAvailable")
    private boolean available;


    
}
