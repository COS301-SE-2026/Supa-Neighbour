package com.app.api.repositories;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.stereotype.Repository;

import com.app.api.dtos.PostDetailDTO;
import com.app.api.dtos.CommentsDTO;
import com.app.api.dtos.PostFeedItemDTO;
 
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Repository backing the Community Bulletin Board feed (7.1).
 * Uses EntityManager with native SQL, consistent with the rest of the
 * codebase's repository pattern (native SQL over Spring Data derived queries).
 */
@Repository
public class BulletinFeedRepository {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Represents a caller's resolved neighbourhood — the location_id to
     * filter posts by, and the human-readable zone name for the response.
     */
    public static class CallerNeighbourhood{
        public final int locationId;
        public final String neighbourhoodName;

        public CallerNeighbourhood(int locationId, String neighbourhoodName){
            this.locationId = locationId;
            this.neighbourhoodName = neighbourhoodName;
        }
    }
        /**
         * Resolves the caller's neighbourhood via
         * user_table -> address_table -> location_table.
         *
         * @param userId the authenticated user's id
         * @return the caller's neighbourhood, or null if the user has no address on file
         */
        @SuppressWarnings("unchecked")
        public CallerNeighbourhood findCallerNeighbourhood(int userId){
            Query query = entityManager.createNativeQuery(
                "SELECT a.neighbourhood_id, l.neighbourhood_name " +
                "FROM user_table u " +
                "JOIN address_table a ON a.address_id = u.user_address_id " +
                "JOIN location_table l ON l.location_id = a.neighbourhood_id " +
                "WHERE u.user_id = :userId"
            );
            query.setParameter("userId", userId);
            List<Object[]> rows = query.getResultList();
            if(rows.isEmpty()){
                return null;
            }

            Object[] row = rows.get(0);
            int locationId = ((Number) row[0]).intValue();
            String neighbourhoodName = (String) row[1];
            return new CallerNeighbourhood(locationId, neighbourhoodName);
        }
    
     /**
     * Returns one page of the bulletin board feed for a given neighbourhood,
     * with optional category filter and free-text search against post_content.
     * Reaction and comment counts are computed via correlated subqueries.
     *
     * @param neighbourhoodLocationId the caller's neighbourhood location_id (from findCallerNeighbourhood)
     * @param category                optional category filter, or null for no filter
     * @param search                  optional keyword search, or null for no filter
     * @param limit                   page size
     * @param offset                  page offset
     * @return the matching posts for this page, newest first
     *
     */
    @SuppressWarnings("unchecked")
    public List<PostFeedItemDTO> findFeed(int neighbourhoodLocationId, String category, String search, int limit, int offset){
        StringBuilder sql = new StringBuilder(
            "SELECT p.post_id, p.user_id, u.user_username, p.post_content, p.media_url, p.category, " +
                "  (SELECT COUNT(*) FROM reaction_table r WHERE r.post_id = p.post_id AND r.reaction_type = 'like') AS helpful_count, " +
                "  (SELECT COUNT(*) FROM reaction_table r WHERE r.post_id = p.post_id AND r.reaction_type = 'dislike') AS dis_helpful_count, " +
                "  (SELECT COUNT(*) FROM comments_table c WHERE c.post_id = p.post_id) AS comment_count, " +
                "  p.created_at, p.updated_at " +
                "FROM posts_table p " +
                "JOIN user_table u ON u.user_id = p.user_id " +
                "JOIN address_table a ON a.address_id = u.user_address_id " +
                "WHERE a.neighbourhood_id = :neighbourhoodLocationId "
        );

        appendOptionalFilters(sql, category, search);
        sql.append(" ORDER BY p.created_at DESC LIMIT :limit OFFSET :offset");

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("neighbourhoodLocationId", neighbourhoodLocationId);

        bindOptionalFilters(query, category, search);
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        List<Object[]> rows = query.getResultList();
        List<PostFeedItemDTO> result = new ArrayList<>();
        for(Object[] row: rows){
            result.add(new PostFeedItemDTO(
                ((Number) row[0]).intValue(),
                    ((Number) row[1]).intValue(),
                    (String) row[2],
                    (String) row[3],
                    (String) row[4],
                    (String) row[5],
                    ((Number) row[6]).longValue(),
                    ((Number) row[7]).longValue(),
                    ((Number) row[8]).longValue(),
                    (Timestamp) row[9],
                    (Timestamp) row[10]
            ));
        }

        return result;
    }

    /**
     * Counts the total posts matching the same filters as findFeed, for
     * populating totalPosts in the response (used for pagination on the client).
     */

    public long countFeed(int neighbourhoodLocationId, String category, String search){
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) " +
                "FROM posts_table p " +
                "JOIN user_table u ON u.user_id = p.user_id " +
                "JOIN address_table a ON a.address_id = u.user_address_id " +
                "WHERE a.neighbourhood_id = :neighbourhoodLocationId "

        );

        appendOptionalFilters(sql, category, search);
        Query query = entityManager.createNativeQuery(sql.toString());

        query.setParameter("neighbourhoodLocationId", neighbourhoodLocationId);

        bindOptionalFilters(query, category, search);

        return ((Number) query.getSingleResult()).longValue();
    }

    private void appendOptionalFilters(StringBuilder sql,String category, String search){
        if(category != null && !category.isBlank()){
            sql.append(" AND p.category = :category");
        }
        if(search != null && !search.isBlank()){
            sql.append(" AND p.post_content ILIKE :search");
        }
    }

    private void bindOptionalFilters(Query query, String category, String search){
        if(category != null && !category.isBlank()){
            query.setParameter("category", category);
        }
        if(search != null && !search.isBlank()){
            query.setParameter("search", "%" + search + "%");
        }
    }

    /**
     * Returns the full detail view for a single post (7.2), regardless of
     * the caller's own zone — a permalink should be viewable directly by ID.
     *
     * @param postId the post's id
     * @return the post detail (with comments already attached), or null if the post doesn't exist
     */
    @SuppressWarnings("unchecked")
    public PostDetailDTO findPostDetail(int postId){
        Query query = entityManager.createNativeQuery(
            "SELECT p.post_id, p.user_id, u.user_username, p.post_content, p.media_url, p.category, " +
                "  (SELECT COUNT(*) FROM reaction_table r WHERE r.post_id = p.post_id AND r.reaction_type = 'like') AS helpful_count, " +
                "  (SELECT COUNT(*) FROM reaction_table r WHERE r.post_id = p.post_id AND r.reaction_type = 'dislike') AS dis_helpful_count, " +
                "  p.created_at, p.updated_at " +
                "FROM posts_table p " +
                "JOIN user_table u ON u.user_id = p.user_id " +
                "WHERE p.post_id = :postId"
        );
        query.setParameter("postId", postId);

        List<Object[]> rows = query.getResultList();
        if(rows.isEmpty()){
            return null;
        }
        
        Object[] row = rows.get(0);

        List<CommentsDTO> comments = findCommentsForPost(postId);
        return new PostDetailDTO(
            ((Number) row[0]).intValue(),
            ((Number) row[1]).intValue(),
            (String) row[2],
            (String) row[3],
            (String) row[4],
            (String) row[5],
            ((Number) row[6]).longValue(),
            ((Number) row[7]).longValue(),
            comments,
            (Timestamp) row[8],
            (Timestamp) row[9]
        );
    }

    /**
     * Returns all comments for a post (both top-level and threaded replies),
     * oldest first, for the post detail view (7.2).
     *
     * @param postId the post's id
     * @return the list of comments, empty if there are none
     */
    @SuppressWarnings("unchecked")
    public List<CommentsDTO> findCommentsForPost(int postId){
        Query query = entityManager.createNativeQuery(
            "SELECT c.comment_id, c.user_id, u.user_username, c.parent_comment_id, " +
                "  c.comment_content, c.created_at, c.updated_at " +
                "FROM comments_table c " +
                "JOIN user_table u ON u.user_id = c.user_id " +
                "WHERE c.post_id = :postId " +
                "ORDER BY c.created_at ASC"
        );

        query.setParameter("postId", postId);

        List<Object[]> rows = query.getResultList();

        List<CommentsDTO> result = new ArrayList<>();
        for(Object[] row : rows){
            result.add(new CommentsDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                (String) row[2],
                row[3] == null ? null : ((Number) row[3]).intValue(),
                (String) row[4],
                (Timestamp) row[5],
                (Timestamp) row[6]
            ));
        }
        return result;
    }


}
