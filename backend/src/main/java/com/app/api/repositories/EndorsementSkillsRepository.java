package com.app.api.repositories;

import com.app.api.models.EndorsementSkill;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
/**
 * 
 * EndorsementSkillsRepository
 */
@Repository 
public interface EndorsementSkillsRepository extends JpaRepository <EndorsementSkill,String>{

    /**
     * Finds approved skills ordered by category then display name.
     *
     * @return the approved skills, ordered
     */
    @Query("""
            select s
            from EndorsementSkill s
            where s.approved = true
            order by coalesce(s.category,'Uncategorised') asc, s.displayName asc
            """
        )
    List<EndorsementSkill> findApprovedOrdered();

    /**
     * Finds a skill by its tag.
     *
     * @param skillTag the skill tag
     * @return the matching skill, if present
     */
    Optional<EndorsementSkill> findBySkillTag(String skillTag);
    
}


