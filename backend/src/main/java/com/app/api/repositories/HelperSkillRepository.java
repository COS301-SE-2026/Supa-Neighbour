package com.app.api.repositories;
import com.app.api.models.HelperSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository for performing database operations on
 * {@link HelperSkill} entities.
 */
public interface HelperSkillRepository extends JpaRepository<HelperSkill, Integer>{
    /**
     * Retrieves all skills associated with the specified helper.
     *
     * @param helperId the identifier of the helper
     * @return a list of {@link HelperSkill} entities belonging to the helper
     */
    @Query("select hs from HelperSkill hs where hs.helperid.helperid = :helperId")
    List<HelperSkill> findHelperId(@Param("helperId") int helperId);

    /**
     * Deletes all skills associated with the specified helper.
     *
     * @param helperId the identifier of the helper whose skills are to be removed
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from HelperSkill hs where hs.helperid.helperid = :helperId")
    void deleteHelperId(@Param("helperId") int helperId);
}
