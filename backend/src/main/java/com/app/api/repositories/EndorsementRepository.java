package com.app.api.repositories;

import com.app.api.models.Endorsement;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
     * @return the list of endorsements, ordered by skill tag then most recent
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
     * Finds endorsements received by a user for a single skill, with related
     * entities fetched.
     *
     * @param userId   the receiving user's id
     * @param skillTag the skill tag to filter by
     * @return the list of endorsements, most recent first
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
     * @return the per-skill aggregate rows, heaviest and most-endorsed first
     */
    @Query("""
            select e.skillTag.skillTag                  as skillTag,
                   e.skillTag.displayName               as displayName,
                   e.skillTag.category                  as category,
                   count(e)                             as endorsementCount,
                   coalesce(sum(e.weight), 0)           as totalWeight,
                   count(distinct e.endorserid.userid)  as distinctEndorsers,
                   max(e.createdAt)                     as lastEndorsedAt
            from Endorsement e
            where e.endorseeid.userid = :userId
            group by e.skillTag.skillTag,
                     e.skillTag.displayName,
                     e.skillTag.category
            order by coalesce(sum(e.weight), 0) desc,
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
    long countDistinctEndorsers(@Param("userId") Integer userId);

    /**
     * Finds outgoing edges (endorsements given) for the given users.
     *
     * @param userIds the ids of the endorsing users
     * @return the outgoing edge rows
     */
    @Query("""
            select e.endorserid.userid  as fromUserId,
                   e.endorseeid.userid  as toUserId,
                   e.skillTag.skillTag  as skillTag,
                   e.weight             as weight
            from Endorsement e
            where e.endorserid.userid in :userIds
            """)
    List<EdgeRow> findOutgoingEdges(@Param("userIds") Collection<Integer> userIds);

    /**
     * Finds incoming edges (endorsements received) for the given users.
     *
     * @param userIds the ids of the endorsed users
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
    List<EdgeRow> findIncomingEdges(@Param("userIds") Collection<Integer> userIds);

    /**
     * Finds every edge recorded inside a zone, for the admin zone-graph endpoint.
     *
     * @param zoneId the zone id
     * @return the edge rows within the zone, heaviest and most recent first
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

    /**
     * Returns the distinct zone ids that have at least one endorsement.
     *
     * @return the distinct zone ids
     */
    @Query("""
            select distinct e.zoneid.locationid
            from Endorsement e
            """)
    List<Integer> findDistinctZoneIds();

    /**
     * Finds any edge touching one of the supplied users, in either direction.
     *
     * @param userIds the ids of the users
     * @return the matching edge rows
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
     * Returns the top endorsers of a user by total weight.
     *
     * @param userId   the receiving user's id
     * @param pageable restricts how many rows are returned
     * @return the top-endorser aggregate rows, heaviest first
     */
    @Query("""
            select e.endorserid.userid                                    as userId,
                   concat(e.endorserid.firstName, ' ', e.endorserid.lastName) as name,
                   coalesce(sum(e.weight), 0)                             as totalWeight
            from Endorsement e
            where e.endorseeid.userid = :userId
            group by e.endorserid.userid, e.endorserid.firstName, e.endorserid.lastName
            order by coalesce(sum(e.weight), 0) desc
            """)
    List<EndorserAggregate> aggregateTopEndorsers(
            @Param("userId") Integer userId, Pageable pageable);

    /**
     * Loads every task-linked endorsement as a flat
     * {@code (endorser, endorsee, task)} projection. Task-less endorsements are
     * excluded, since mutual-ring detection keys on shared tasks.
     *
     * <p>Feeds {@code EndorsementAbuseService#scanMutualRings}.</p>
     *
     * @return one row per task-linked endorsement; empty if none exist
     */
    @Query("""
            select e.endorserid.userid as fromUserId,
                   e.endorseeid.userid as toUserId,
                   e.taskid.taskid     as taskId
            from Endorsement e
            where e.taskid is not null
            """)
    List<TaskEdgeRow> findAllTaskLinkedEdges();

    /**
     * Loads every endorsement as a {@code (endorser, endorsee, createdAt)}
     * projection, ordered chronologically.
     *
     * <p>Feeds {@code EndorsementAbuseService#scanTimestampAnomalies}, which
     * walks the result per user pair looking for rapid-fire gaps. The ascending
     * order is relied upon by that scan.</p>
     *
     * @return all endorsements, oldest first
     */
    @Query("""
            select e.endorserid.userid as fromUserId,
                   e.endorseeid.userid as toUserId,
                   e.createdAt         as createdAt
            from Endorsement e
            order by e.createdAt asc
            """)
    List<TimestampEdgeRow> findAllEdgesWithTimestamps();

    /**
     * Endorsers who gave at least {@code minCount} endorsements since the
     * cutoff — candidates for the sudden-spike check. Cheap SQL-side filter so
     * the (comparatively expensive) Firebase account-age lookup only runs
     * against users who already look spiky.
     *
     * @param since    the inclusive lower bound on endorsement timestamp
     * @param minCount the minimum number of endorsements to qualify
     * @return one row per qualifying endorser
     */
    @Query("""
            select e.endorserid.userid as fromUserId, count(e) as cnt
            from Endorsement e
            where e.createdAt >= :since
            group by e.endorserid.userid
            having count(e) >= :minCount
            """)
    List<SpikeCandidateRow> findSpikeCandidates(
            @Param("since") LocalDateTime since,
            @Param("minCount") long minCount);

    /**
     * Per-skill aggregate of endorsements received by a user.
     */
    public interface SkillAggregate {

        /** @return the skill tag */
        String getSkillTag();

        /** @return the skill's display name */
        String getDisplayName();

        /** @return the skill's category */
        String getCategory();

        /** @return the number of endorsements for this skill */
        long getEndorsementCount();

        /** @return the total weight of endorsements for this skill */
        long getTotalWeight();

        /** @return the number of distinct endorsers for this skill */
        long getDistinctEndorsers();

        /** @return the timestamp of the most recent endorsement for this skill */
        LocalDateTime getLastEndorsedAt();
    }

    /**
     * A single directed endorsement edge with its skill and weight.
     */
    public interface EdgeRow {

        /** @return the id of the endorsing user */
        Integer getFromUserId();

        /** @return the id of the endorsed user */
        Integer getToUserId();

        /** @return the skill tag for this edge */
        String getSkillTag();

        /** @return the edge weight */
        Integer getWeight();
    }

    /**
     * Aggregate of a single endorser's contributions to a user.
     */
    public interface EndorserAggregate {

        /** @return the endorser's user id */
        Integer getUserId();

        /** @return the endorser's full name */
        String getName();

        /** @return the total weight contributed by this endorser */
        long getTotalWeight();
    }

    /**
     * Projection row for {@link #findAllTaskLinkedEdges()}.
     */
    public interface TaskEdgeRow {

        /** @return the id of the endorsing user */
        Integer getFromUserId();

        /** @return the id of the endorsed user */
        Integer getToUserId();

        /** @return the id of the shared task */
        Integer getTaskId();
    }

    /**
     * Projection row for {@link #findAllEdgesWithTimestamps()}.
     */
    public interface TimestampEdgeRow {

        /** @return the id of the endorsing user */
        Integer getFromUserId();

        /** @return the id of the endorsed user */
        Integer getToUserId();

        /** @return the endorsement's creation timestamp */
        LocalDateTime getCreatedAt();
    }

    /**
     * Projection row for {@link #findSpikeCandidates}.
     */
    public interface SpikeCandidateRow {

        /** @return the id of the endorsing user */
        Integer getFromUserId();

        /** @return the number of endorsements issued since the cutoff */
        long getCnt();
    }
}
