package com.app.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import com.app.api.models.Endorsement;


/**
 * 
 * EndorsementRepository for {@link Endorsement} rows, including projection
 */
@Repository
public interface EndorsementRepository extends JpaRepository<Endorsement, Integer> {

    /** Checks for an existing task-less endorsement for the same triple. */
    boolean existsByEndorserid_UserIdAndEndorseeid_UserIdAndSkillTag_SkillTagAndTaskidIsNull(
            Integer endorserId, Integer endorseeId, String skillTag);
 
    /** Checks for an existing endorsement for the same triple on the same task. */
    boolean existsByEndorserid_UserIdAndEndorseeid_UserIdAndSkillTag_SkillTagAndTaskid_TaskId(
            Integer endorserId, Integer endorseeId, String skillTag, Integer taskId);
 

    /**
     * GET /api/endorsements/me
     */

    @Query
    ("""
        select e 
        from endorsement_table e
        join fetch e.endorsement_id
        join fetch e.skill_tag
        join fetch e.zone_id
        join fetch e.task_id
        left join fetch e.task_id
        where e.endoresee.user_id = :user_id
        order by e.skill_tag.skill_tag asc, e.createdAt desc""")
        List<Endorsement> findReceiveWithDetails(@Param("user_id") Integer user_id);
    
    /**
    * GET /api/endorsements/me/summary
    */
   @Query("""
        select e.skill_tag.skill_tag  as skill_tag,
               e.skill_tag.display_name as displayName,
               e.skill_tag.category as category,
               count(e)     as endorsementCount,
               coalesce(sum(e.weight), 0) as totalWeight,
               count(distinct e.endorser_id.user_id) as distinctEndorsers,
               max(e.created_At) as lastEndorsedAt
               from Endoursement e
               where e.endorsee_id.user_id = user_id
               group by e.skill_tag.skill_tag,e.skill_tag.displayName,e.skill_tag.category
               order by coalesce(sum(e.weight),0) desc, count(e) desc
                """)
            List<SkillAggregate> aggregateReceivedBySkill(@Param("userId") Integer userId);


    //*
    // counts distinct users */
    @Query("""
    select count(distinct e.endorser_id.user_id) from Endoursement e where e.endorsee_id.user_id =:userId
        """)
    long countDistinctEndorsers(@Param ("user_id")Integer user_id);

    /**
     * graph transversal projections
     */

    @Query("""
            select e.endorser_id.user_id as fromUserId,
                   e.endorsee_id.user_id as toUserId,
                   e.skill_tag.skill_tag as skillTag,
                   e.weight              as weight
            from Endorsement e 
            where e.endorser_id.user_id in:user_id
            """)
    List<EdgeRow> findoutgoEdges(@Param("user_id") Collection<Integer> userIds);


    /**
     * any edge touching one of the supplied users,in either direction
     */
    @Query("""
            select e.endorser_id.user_id as fromUserId,
                   e.endorsee_id.user_id as toUserId,
                   e.skill_tag.skill_tag as skillTag,
                   e.weight              as weight
            from Endorsement e 
            where e.endoree_id.user_id in :user_id
            """)
    List<EdgeRow> findIncomingEdges(@Param("user_id") Collection<Integer> user_ids);
                    
    /**
     * every edge recorded inside a zone,for the admin zone-graph endpoint
     */
    @Query("""
            select e.endorser_it.user_id as fromUserId,
                   e.endorsee_id.user_id as toUserId,
                   e.skill_tag.skill_tag as skill_tag,
                   e.weight              as weight
            from Endorsement e
            where e.zone_id.zone_id = :zone_id
    """)
    List<EdgeRow> findEgEdgesByZone(@Param("zone_id") Integer zoneId);

    /**
     * projections
     */

    /** per-skill aggregate row used by the summary endpoint */
    /**
     * skillAggregate
     */
    public interface SkillAggregate {
        String getSkillTag();
        String getDisplayName();
        String getCategory();
        long getEndorsementCount();
        long getTotalWeight();
        long getDistinctEndorsers();
        LocalDateTime getLastEndorsedAt();
    }

    /**
     * EndgeRow
     */
    public interface EdgeRow {
        Integer getFromUserId();
        Integer getToUserId();
        String getSkillTag();
        Integer getWeigh();
    }
} 


