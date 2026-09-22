package com.app.api.repositories;

import com.app.api.models.EndorsementSkill;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.app.api.models.EndorsementSkill;
import java.util.List;
/**
 * 
 * EndorsementSkillsRepository
 */
@Repository 
public interface EndorsementSkillsRepository extends JpaRepository <EndorsementSkill,String>{

    @Query("""
            select s
            from endorsement_skill_table s
            where s.approved = true
            order by coalesce(s.category,'Uncategorised') asc, s.displayName asc
            """
        )
    List<EndorsementSkill> findApprovedOrdered();
    
}
