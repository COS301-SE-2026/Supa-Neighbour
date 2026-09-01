package com.app.api.repositories;
import com.app.api.dtos.LeaderboardEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
 
import java.util.ArrayList;
import java.util.List;

@Repository
public class LeaderboardRepository {
    @PersistenceContext
    private EntityManager em;

    /**
     * Returns ALL helpers in the given neighbourhood ordered by average_rating DESC.
     * We fetch everything so we can pin the current user's rank even if they fall
     * outside the top N — ranking is assigned in Java, not in SQL, to keep it simple.
     *
     * Joins:
     *   helper_analytics_table  — source of average_rating
     *   helper_table            — links analytics to user
     *   user_table              — display name + address
     *   address_table           — neighbourhood_id filter
     */
    public List<LeaderboardEntry>findRankedHelpersByNeighbourhood(int neighbourhoodId) {
        String sql = """
                SELECT 
                u.user_id,
                u.user_name || ' ' || LEFT(u.user_surname, 1) || '.' AS display_name,
                COALESCE(ha.average_rating, 0.0) AS score,
                h.helper_id
                FROM helper_table h
                JOIN user_table u ON u.user_id = h.user_id
                JOIN address_table a ON a.address_id = u.user_address_id
                JOIN location_table l ON l.location_id = a.neighbourhood_id
                LEFT JOIN helper_analytics_table ha ON ha.user_id = u.user_id
                WHERE l.neighbourhood_id = :neighbourhoodId
                ORDER BY score DESC
            """;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).setParameter("neighbourhoodId", neighbourhoodId).getResultList();

        List<LeaderboardEntry> ranked = new ArrayList<>();
        int rank = 1;
        for(Object[] row : rows){
            int userId = ((Number) row[0]).intValue();
            Integer helperId = row[3] != null ? ((Number) row[3]).intValue() : null;
            String name = (String) row[1];
            double score = ((Number) row[2]).doubleValue();
            System.out.println("User ID: " + userId + ", Helper ID: " + helperId + ", Name: " + name);
            ranked.add(new LeaderboardEntry(rank++, userId, name, score, helperId));
        }
        return ranked;
    }

    /**
     * Returns the neighbourhood name and ID for the authenticated user.
     * Used to scope the leaderboard and populate the response's "neighbourhood" field.
     */
    public Object[] findNeighbourhoodForUser(int userId){
        String sql = """
                SELECT l.neighbourhood_id, l.neighbourhood_name
                FROM user_table    u
                JOIN address_table a ON a.address_id   = u.user_address_id
                JOIN location_table l ON l.location_id = a.neighbourhood_id
                WHERE u.user_id = :userId
                """;
        return (Object[]) em.createNativeQuery(sql).setParameter("userId", userId).getSingleResult();
    }
}
