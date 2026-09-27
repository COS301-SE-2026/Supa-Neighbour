package com.app.api.repositories;

import com.app.api.models.EndorsementFlagParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository 
public interface EndorsementFlagParticipantRepository extends JpaRepository<EndorsementFlagParticipant, Long> {
    /**
     * Returns all participants belonging to a single flag.
     *
     * @param flagId the flag whose participants should be returned
     * @return the flag's participants, or an empty list if none exist
     */
    List<EndorsementFlagParticipant> findByFlagId(Long flagId);
     /**
     * Returns all participants belonging to any of the given flags, in a single
     * query. Intended for batch hydration — callers group the result by
     * {@link EndorsementFlagParticipant#getFlagId()} to avoid N+1 queries when
     * loading participants for a list of flags.
     *
     * @param flagIds the flags whose participants should be returned
     * @return the participants across all given flags; empty if {@code flagIds}
     *         is empty or none have participants
     */
    List<EndorsementFlagParticipant> findByFlagIdIn(Collection<Long> flagIds); 
}
