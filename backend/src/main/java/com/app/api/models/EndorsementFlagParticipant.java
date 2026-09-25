package com.app.api.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "endorsement_flag_participant_table")
public class EndorsementFlagParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flag_participant_id")
    @EqualsAndHashCode.Include
    private Long flagParticipantId;

    @Column(name = "flag_id", nullable = false)
    private Long flagId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "role", length = 10)
    private String role; 

    public EndorsementFlagParticipant(Long flagId, int userId, String role) {
        this.flagId = flagId;
        this.userId = userId;
        this.role = role;
    }
}