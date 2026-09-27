package com.app.api.models;


import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
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

        /**
     * Creates a participant row linking a user to a flag.
     *
     * @param flagId the flag this participant belongs to
     * @param userId the participating user's id
     * @param role   the user's role in the pattern (e.g. "endorser"), or
     *               {@code null} for role-less patterns
     */
    public EndorsementFlagParticipant(Long flagId, int userId, String role) {
        this.flagId = flagId;
        this.userId = userId;
        this.role = role;
    }
}
