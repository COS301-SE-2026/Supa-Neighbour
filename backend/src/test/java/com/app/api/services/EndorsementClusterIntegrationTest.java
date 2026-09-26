package com.app.api.services;

import com.app.api.AbstractIntegrationTest;
import com.app.api.dtos.ClusterAnalysisRunResponseDTO;
import com.app.api.repositories.ClusterCacheRepository;
import com.app.api.services.EndorsementClusterService;
import com.app.api.models.EndorsementClusterCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class EndorsementClusterIntegrationTest extends AbstractIntegrationTest{
    
    @Autowired EndorsementClusterService clusterService;
    @Autowired ClusterCacheRepository clusterCacheRepository;
    @Autowired JdbcTemplate jdbc;

    int zoneId;

    @BeforeEach
    void seed() {
        zoneId = jdbc.queryForObject(
            "INSERT INTO location_table (location_center_point, location_radius, neighbourhood_id, neighbourhood_name) " +
            "VALUES (0, 10, 1, 'Test Zone') RETURNING location_id", Integer.class);

        jdbc.update("INSERT INTO endorsement_skill_table (skill_tag, display_name, approved) " +
                    "VALUES ('test_skill', 'Test Skill', true)");

        // two tight triangles {u1,u2,u3} and {u4,u5,u6}, joined by one weak bridge edge
        int u1 = insertUser("u1"), u2 = insertUser("u2"), u3 = insertUser("u3");
        int u4 = insertUser("u4"), u5 = insertUser("u5"), u6 = insertUser("u6");

        endorse(u1, u2); endorse(u2, u3); endorse(u3, u1);
        endorse(u4, u5); endorse(u5, u6); endorse(u6, u4);
        endorse(u3, u4); // the bridge
    }

    @Test
    void twoTrianglesJoinedByOneEdgeFormTwoClusters() {
        ClusterAnalysisRunResponseDTO result = clusterService.runForZone(zoneId);

        assertThat(result.nodeCount()).isEqualTo(6);
        assertThat(result.clusterCount()).isEqualTo(2);
        assertThat(result.modularity()).isGreaterThan(0.0);

        List<EndorsementClusterCache> rows =
            clusterCacheRepository.findByZoneIdOrderByClusterLabelAscUserIdAsc(zoneId);
        assertThat(rows).hasSize(6);
        long distinctLabels = rows.stream().map(EndorsementClusterCache::getClusterLabel).distinct().count();
        assertThat(distinctLabels).isEqualTo(2);
    }

    @Test
    void secondRunReplacesRatherThanDuplicatesCache() {
        clusterService.runForZone(zoneId);
        clusterService.runForZone(zoneId);

        List<EndorsementClusterCache> rows =
            clusterCacheRepository.findByZoneIdOrderByClusterLabelAscUserIdAsc(zoneId);
        assertThat(rows).hasSize(6); // not 12 — confirms deleteByZoneId actually deletes
    }

    private int insertUser(String tag) {
        return jdbc.queryForObject(
            "INSERT INTO user_table (user_firebase_uid, user_name, user_surname, user_username, user_email) " +
            "VALUES (?, ?, 'Test', ?, ?) RETURNING user_id",
            Integer.class, "uid-" + tag, tag, "user-" + tag, tag + "@example.com");
    }

    private void endorse(int fromUser, int toUser) {
        jdbc.update("INSERT INTO endorsement_table (endorser_id, endorsee_id, location_id, skill_tag, weight) " +
                    "VALUES (?, ?, ?, 'test_skill', 1)", fromUser, toUser, zoneId);
    }
}