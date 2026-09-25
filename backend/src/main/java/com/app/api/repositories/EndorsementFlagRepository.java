package com.app.api.repositories;

import com.app.api.models.EndorsementFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndorsementFlagRepository extends JpaRepository<EndorsementFlag, Long> {
    
    @Query("""
        select f from EndorsementFlag f
        where f.patternType = :patternType
        and (f.status = 'open' or f.status = 'investigate')
        """)
    List<EndorsementFlag> findOpenOrInvestigateByPatternType(@Param("patternType") String patternType);
    List<EndorsementFlag> findByStatusOrderByLastDetectedAtDesc(String status);
    List<EndorsementFlag> findAllByOrderByLastDetectedAtDesc();
}
