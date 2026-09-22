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
    boolean existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskidIsNull(
        Integer endorserId, Integer endorseeId, String skillTag);
 
    /** Checks for an existing endorsement for the same triple on the same task. */
    boolean existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskid_Taskid(
        Integer endorserId, Integer endorseeId, String skillTag, Integer taskId);
 

    /**
     * GET /api/endorsements/me
     */

    @Query
    ("""
        select e 
        from Endorsment e
        join fetch e.endorserid
        join fetch e.endorseeid
        join fetch e.skillTag
        join fetch e.zoneid
        join fetch e.taskid
        left join fetch e.taskid
        where e.endoreseeid.user_id = :userid
        order by e.skill_tag.skill_tag asc, e.createdAt desc""")
        List<Endorsement> findReceiveWithDetails(@Param("userId") Integer userId);
    
    /**
    * GET /api/endorsements/me/summary
    */
   @Query("""
        select e.skillTag.skillTag                  as skillTag,
               e.skillTag.displayName               as displayName,
               e.skillTag.category                  as category,
               count(e)                             as endorsementCount,
               coalesce(sum(e.weight), 0)           as totalWeight,
               count(distinct e.endorserid.userid)  as distinctEndorsers,
               max(e.created_At)                    as lastEndorsedAt
               from Endoursement e
               where e.endorseeid.user_id = :user_id
               group by e.skillTag.skillTag,
               e.skill_tag.displayName,
               e.skill_tag.category
               order by coalesce(sum(e.weight),0) desc, 
               count(e) desc
                """)
    List<SkillAggregate> aggregateReceivedBySkill(@Param("userId") Integer userId);


    //*
    // counts distinct users */
    @Query("""
        select count(distinct e.endorserid.userid) 
        from Endoursement e 
        where e.endorseeid.userid = :userId
        """)
    long countDistinctEndorsers(@Param ("userId")Integer user_Id);

    /**
     * graph transversal projections
     */

    @Query("""
            select e.endorserid.userid   as fromUserId,
                   e.endorsee_id.userid  as toUserId,
                   e.skillTag.skillTag   as skillTag,
                   e.weight              as weight
            from Endorsement e 
            where e.endorserid.userid in :userid
            """)
    List<EdgeRow> findoutgoEdges(@Param("userId") Collection<Integer> userIds);


    /**
     * any edge touching one of the supplied users,in either direction
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skilltag.skillTag  as skillTag,
                   e.weight             as weight
            from Endorsement e 
            where e.endoreeid.userid in :userIds
            """)
    List<EdgeRow> findIncomingEdges(@Param("userIds") Collection<Integer> userids);
                    
    /**
     * every edge recorded inside a zone,for the admin zone-graph endpoint
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skillTag.skillTag  as skillTag,
                   e.weight             as weight
            from Endorsement e
            where e.zoneid.locationid = :zoneId
            """)
    List<EdgeRow> findEgEdgesByZone(@Param("zoneId") Integer zoneId);

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
        Integer getWeight();
    }
} 


