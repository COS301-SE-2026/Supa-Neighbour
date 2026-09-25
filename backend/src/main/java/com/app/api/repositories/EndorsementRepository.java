package com.app.api.repositories;

import com.app.api.models.Endorsement;
import com.app.api.models.EndorsementClusterCache;
import com.app.api.models.EndorsementSkill;

import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 
 * EndorsementRepository for {@link Endorsement} rows, including projection
 */
@Repository
public interface EndorsementRepository extends JpaRepository<Endorsement, Integer> {

    /**
     * Checks for an existing task-less endorsement for the same triple.
     *
     * @param endorserId the endorsing user's id
     * @param endorseeId the endorsed user's id
     * @param skillTag   the skill tag
     * @return true if a matching task-less endorsement exists
     */
    boolean existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskidIsNull(
            Integer endorserId, Integer endorseeId, String skillTag);

    /**
     * Checks for an existing endorsement for the same triple on the same task.
     *
     * @param endorserId the endorsing user's id
     * @param endorseeId the endorsed user's id
     * @param skillTag   the skill tag
     * @param taskId     the task id
     * @return true if a matching endorsement exists
     */
    boolean existsByEndorserid_UseridAndEndorseeid_UseridAndSkillTag_SkillTagAndTaskid_Taskid(
            Integer endorserId, Integer endorseeId, String skillTag, Integer taskId);

    /**
     * Finds endorsements received by a user, with related entities fetched.
     *
     * @param userId the receiving user's id
     * @return the list of endorsements
     */
    @Query("""
            select e
            from Endorsement e
            join fetch e.endorserid
            join fetch e.endorseeid
            join fetch e.skillTag
            left join fetch e.zoneid
            left join fetch e.taskid
            where e.endorseeid.userid = :userId
            order by e.skillTag.skillTag asc, e.createdAt desc""")
    List<Endorsement> findReceiveWithDetails(@Param("userId") Integer userId);

    /**
     * Finds endorsements received by a user, with related entities fetched.
     *
     * @param userId   the receiving user's id
     * @param skillTag
     * @return the list of endorsements
     */
    @Query("""
            select e
            from Endorsement e
            join fetch e.endorserid
            join fetch e.endorseeid
            join fetch e.skillTag
            join fetch e.zoneid
            left join fetch e.taskid
            where e.endorseeid.userid = :userId
            and e.skillTag.skillTag = :skillTag
            order by e.skillTag.skillTag asc, e.createdAt desc""")
    List<Endorsement> findReceiveWithDetailsBySkillTag(
            @Param("userId") Integer userId,
            @Param("skillTag") String skillTag);

    /**
     * Aggregates endorsements received by a user, grouped by skill.
     *
     * @param userId the receiving user's id
     * @return the per-skill aggregate rows
     */
    @Query("""
            select e.skillTag.skillTag                  as skillTag,
                   e.skillTag.displayName               as displayName,
                   e.skillTag.category                  as category,
                   count(e)                             as endorsementCount,
                   coalesce(sum(e.weight), 0)           as totalWeight,
                   count(distinct e.endorserid.userid)  as distinctEndorsers,
                   max(e.createdAt)                    as lastEndorsedAt
                   from Endorsement e
                   where e.endorseeid.userid = :userId
                   group by e.skillTag.skillTag,
                   e.skillTag.displayName,
                   e.skillTag.category
                   order by coalesce(sum(e.weight),0) desc,
                   count(e) desc
                    """)
    List<SkillAggregate> aggregateReceivedBySkill(@Param("userId") Integer userId);

    /**
     * Counts distinct users who have endorsed the given user.
     *
     * @param userId the receiving user's id
     * @return the count of distinct endorsers
     */
    @Query("""
            select count(distinct e.endorserid.userid)
            from Endorsement e
            where e.endorseeid.userid = :userId
            """)
    long countDistinctEndorsers(@Param("userId") Integer user_Id);

    /**
     * Finds outgoing edges (endorsements given) for the given users.
     *
     * @param userIds the ids of the endorsing users
     * @return the outgoing edge rows
     */
    @Query("""
            select e.endorserid.userid   as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skillTag.skillTag   as skillTag,
                   e.weight              as weight
            from Endorsement e
            where e.endorserid.userid in :userIds
            """)
    List<EdgeRow> findOutgoingEdges(@Param("userIds") Collection<Integer> userIds);

    /**
     * Finds any edge touching one of the supplied users, in either direction.
     *
     * @param userIds the ids of the users
     * @return the incoming edge rows
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skillTag.skillTag  as skillTag,
                   e.weight             as weight
            from Endorsement e
            where e.endorseeid.userid in :userIds
            """)
    List<EdgeRow> findIncomingEdges(@Param("userIds") Collection<Integer> userids);

    /**
     * Finds every edge recorded inside a zone, for the admin zone-graph endpoint.
     *
     * @param zoneId the zone id
     * @return the edge rows within the zone
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skillTag.skillTag  as skillTag,
                   e.weight             as weight
            from Endorsement e
            where e.zoneid.locationid = :zoneId
            order by e.weight desc, e.createdAt desc
            """)
    List<EdgeRow> findEdgesByZone(@Param("zoneId") Integer zoneId);

       @Query("""
                select distinct e.zoneid.locationid from endorsement e
                       """)
       List<Integer> findDistinctZoneIds();     
    
    /**
     * any edge touching one of the supplied users, in either direction
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                e.endorseeid.userid  as toUserId,
                e.skillTag.skillTag  as skillTag,
                e.weight             as weight
            from Endorsement e
            where e.endorserid.userid in :userIds
            or e.endorseeid.userid in :userIds
            """)
    List<EdgeRow> findAllEdges(@Param("userIds") Collection<Integer> userIds);

    /**
     * @param userId
     * @return top endorsements
     */
    @Query("""
            select e.endorserid.userid                                   as userId,
                concat(e.endorserid.firstName, ' ', e.endorserid.lastName) as name,
                coalesce(sum(e.weight),0)                                as totalWeight
            from Endorsement e
            where e.endorseeid.userid = :userId
            group by e.endorserid.userid, e.endorserid.firstName, e.endorserid.lastName
            order by coalesce(sum(e.weight),0) desc
            """)
    List<EndorserAggregate> aggregateTopEndorsers(@Param("userIds") Integer userId, Pageable pageable);

    /**
     * projections
     */

    /** per-skill aggregate row used by the summary endpoint */
    /**
     * skillAggregate
     */
    public interface SkillAggregate {
        /**
         * @return the skill tag
         */
        String getSkillTag();

        /**
         * @return the get display name
         */
        String getDisplayName();

        /**
         * @return the skill's category
         */
        String getCategory();

        /**
         * @return the endorsement cout
         */
        long getEndorsementCount();

        /**
         * @return the total weight
         */
        long getTotalWeight();

        /**
         * @return the find specific endorser
         */
        long getDistinctEndorsers();

        /**
         * @return the last Endordsement was
         */
        LocalDateTime getLastEndorsedAt();
    }

    /**
     * EndgeRow
     */
    public interface EdgeRow {

        /**
         * @return the id of the endorseing user
         */
        Integer getFromUserId();

        /**
         * @return the id of the endorsed user
         */
        Integer getToUserId();

        /**
         * @return the skill tag for this edgw
         */
        String getSkillTag();

        /**
         * @return the total weight
         */
        Integer getWeight();
    }

    /**
     * Endorsement Aggregate
     */
    public interface EndorserAggregate {
        /**
         * @return the user
         */
        Integer getUserId();

        /**
         * @return the users name
         */
        String getName();

        /**
         * @return the total weight for the user
         */
        long getTotalWeight();
    }

    public interface SkillRepository extends JpaRepository<EndorsementSkill,String> {
    
        /**
         * Returns every approved skill, ordered by category then display name so
         * the catalogue endpoint can group without a secondary sort.
         *
         * @return approved skills in catalogue order
         */

        @Query("""
            select s
            from EndorsementSkill s
            where s.approved = true
            order by coalesce(s.category, 'Uncategorised') asc, s.displayName asc            
                        """)
        List<EndorsementSkill> findApprovedOrdered();
        
    }

    public interface ClusterCacheRepository extends JpaRepository<EndorsementClusterCache, Long> {

        /**
         * Reads back a zone's current cluster membership for the admin
         * dashboard. Never runs detection — that only happens in the job.
         *
         * @param zoneId the zone
         * @return cached memberships, lowest cluster label first
         */
        List<EndorsementClusterCache> findByZoneIdOrderByClusterLabelAscUserIdAsc(int zoneId);

        /**
         * Clears a zone's cached membership immediately before the job writes
         * its fresh replacement, inside the same transaction.
         *
         * @param zoneId the zone to clear
         * @return rows deleted
         */
        @Modifying
        @Query("delete from EndorsementClusterCache c where c.zoneId =: zoneId")
        int deleteByZoneId(@Param("zoneId") int zoneId);
    }
}
