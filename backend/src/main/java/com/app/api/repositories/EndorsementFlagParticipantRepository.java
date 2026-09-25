package com.app.api.repositories;

import com.app.api.models.EndorsementFlagParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;


@Repository 
public interface EndorsementFlagParticipantRepository extends JpaRepository<EndorsementFlagParticipant, Long> {
    List<EndorsementFlagParticipant> findByFlagId(Long flagId);
    List<EndorsementFlagParticipant> findByFlagIdIn(Collection<Long> flagIds);
    
}
