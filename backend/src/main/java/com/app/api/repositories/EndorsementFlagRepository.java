package com.app.api.repositories;

import com.app.api.models.EndorsementFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndorsementFlagRepository extends JpaRepository<EndorsementFlag, Long> {
    
    /**
     * Returns all unresolved flags of a given pattern type — those with status
     * {@code "open"} or {@code "investigate"}.
     *
     * <p>Used during upsert to find a candidate flag that a newly detected
     * pattern might merge into, rather than creating a duplicate row.</p>
     *
     * @param patternType the pattern type to match (e.g. {@code "mutual_ring"})
     * @return the matching unresolved flags, or an empty list if none exist
     */
    @Query("""
        select f from EndorsementFlag f
        where f.patternType = :patternType
        and (f.status = 'open' or f.status = 'investigate')
        """)
    List<EndorsementFlag> findOpenOrInvestigateByPatternType(@Param("patternType") String patternType);
    /**
     * Returns all flags with the given status, most recently detected first.
     *
     * @param status the status to filter by ({@code "open"}, {@code "investigate"},
     *               or {@code "dismiss"})
     * @return the matching flags, newest first
     */
    List<EndorsementFlag> findByStatusOrderByLastDetectedAtDesc(String status);
    /**
     * Returns all flags, most recently detected first.
     *
     * @return every flag, newest first
     */
    List<EndorsementFlag> findAllByOrderByLastDetectedAtDesc();
}
