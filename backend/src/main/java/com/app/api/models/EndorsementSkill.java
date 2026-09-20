package com.app.api.models;

import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Entity
@Getter
@Setter
@Table(name = "endorsement_skill_table")
public class EndorsementSkill{
    @Id
    @Column(name = "skill_tag", length = 50)
    private String skillTag;

    @Column(name = "display_name", length = 50, nullable = false)
    private String displayName;

    @Column(name = "category", length = 30)
    private String category;

    @Column(name = "approved", nullable = false)
    private Boolean approved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * No-argument constructor required by JPA.
     */
    public EndorsementSkill(){
        // Needed for JPA
    }

    /**
     * Constructs a new {@code EndorsementSkill} with the given details.
     *
     * @param skillTag    the unique skill tag
     * @param displayName the human-readable name of the skill
     * @param category    the optional category of the skill
     */
    public EndorsementSkill(String skillTag, String displayName, String category){
        this.skillTag = skillTag;
        this.displayName = displayName;
        this.category = category;
    }

    /**
     * JPA lifecycle callback that sets {@link #createdAt} to the current
     * date and time immediately before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
