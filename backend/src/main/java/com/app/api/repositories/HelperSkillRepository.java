package com.app.api.repositories;
import com.app.api.models.HelperSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HelperSkillRepository extends JpaRepository<HelperSkill, Integer>{
    @Query("select hs from HelperSkill hs where hs.helperid.helperid = :helperId")
    List<HelperSkill> findHelperId(@Param("helperId") int helperId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("delete from HelperSkill hs where hs.helperid.helperid = :helperId")
    void deleteHelperId(@Param("helperId") int helperId);
}
