package com.app.api.services;

import com.app.api.AbstractIntegrationTest;
import com.app.api.models.EndorsementClusterCache;
import com.app.api.models.EndorsementFlag;
import com.app.api.repositories.EndorsementFlagRepository;
import com.app.api.services.EndorsementAbuseService;
import com.app.api.repositories.EndorsementFlagParticipantRepository;
import com.app.api.repositories.ClusterCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional 
public class EndorsementAbuseServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired EndorsementAbuseService abuseService;
    @Autowired EndorsementFlagRepository flagRepository;
    @Autowired EndorsementFlagParticipantRepository participantRepository;
    @Autowired ClusterCacheRepository clusterCacheRepository;
    @Autowired JdbcTemplate jdbc;

    int zoneId, userA, userB;

    @BeforeEach
    void seed() {
        zoneId = jdbc.queryForObject(
            "INSERT INTO location_table (location_center_point, location_radius, neighbourhood_id, neighbourhood_name) " +
            "VALUES (0, 10, 1, 'Test Zone') RETURNING location_id", Integer.class);
        jdbc.update("INSERT INTO endorsement_skill_table (skill_tag, display_name, approved) " +
                    "VALUES ('test_skill', 'Test Skill', true)");
        userA = insertUser("a");
        userB = insertUser("b");
    }

    @Test
    void sixReciprocalTasksTriggersMutualRing() {
        for (int i = 0; i < 6; i++) {
            int taskId = insertTaskShell();
            endorse(userA, userB, taskId);
            endorse(userB, userA, taskId);
        }

        int touched = abuseService.scanMutualRings(UUID.randomUUID(), LocalDateTime.now());

        assertThat(touched).isEqualTo(1);
        List<EndorsementFlag> flags = flagRepository.findAllByOrderByLastDetectedAtDesc();
        assertThat(flags).hasSize(1);
        assertThat(flags.get(0).getPatternType()).isEqualTo("mutual_ring");
        assertThat(flags.get(0).getMetricValue()).isEqualTo(6.0);
        assertThat(participantRepository.findByFlagId(flags.get(0).getFlagId())).hasSize(2);
    }

    @Test
    void fiveReciprocalTasksDoesNotTriggerMutualRing() {
        for (int i = 0; i < 5; i++) {
            int taskId = insertTaskShell();
            endorse(userA, userB, taskId);
            endorse(userB, userA, taskId);
        }

        int touched = abuseService.scanMutualRings(UUID.randomUUID(), LocalDateTime.now());

        assertThat(touched).isZero();
        assertThat(flagRepository.findAllByOrderByLastDetectedAtDesc()).isEmpty();
    }

    @Test
    void endorsementsWithinTenSecondsTriggerTimestampAnomaly() {
        LocalDateTime t0 = LocalDateTime.now().minusDays(1);
        endorseAt(userA, userB, t0);
        endorseAt(userB, userA, t0.plusSeconds(4)); // < 10s gap

        int touched = abuseService.scanTimestampAnomalies(UUID.randomUUID(), LocalDateTime.now());

        assertThat(touched).isEqualTo(1);
        EndorsementFlag flag = flagRepository.findAllByOrderByLastDetectedAtDesc().get(0);
        assertThat(flag.getPatternType()).isEqualTo("timestamp_anomaly");
        assertThat(flag.getMetricValue()).isEqualTo(4.0);
    }

    @Test
    void endorsementsThirtySecondsApartDoNotTrigger() {
        LocalDateTime t0 = LocalDateTime.now().minusDays(1);
        endorseAt(userA, userB, t0);
        endorseAt(userB, userA, t0.plusSeconds(30));

        int touched = abuseService.scanTimestampAnomalies(UUID.randomUUID(), LocalDateTime.now());

        assertThat(touched).isZero();
    }

    @Test
    void tightIslandOfThreeTriggersIslandGroup() {
        int u3 = insertUser("c");
        UUID runId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        // three users, entirely internal weight (total_degree == internal_degree), cluster label 0
        saveClusterRow(u3aRow(userA, 0, 4, 4, runId, now));
        saveClusterRow(u3aRow(userB, 0, 4, 4, runId, now));
        saveClusterRow(u3aRow(u3, 0, 4, 4, runId, now));

        int touched = abuseService.scanIslandGroups(UUID.randomUUID(), LocalDateTime.now());

        assertThat(touched).isEqualTo(1);
        EndorsementFlag flag = flagRepository.findAllByOrderByLastDetectedAtDesc().get(0);
        assertThat(flag.getPatternType()).isEqualTo("island_group");
        assertThat(participantRepository.findByFlagId(flag.getFlagId())).hasSize(3);
    }

    @Test
    void secondScanBumpsExistingFlagRatherThanDuplicating() {
        for (int i = 0; i < 6; i++) {
            int taskId = insertTaskShell();
            endorse(userA, userB, taskId);
            endorse(userB, userA, taskId);
        }

        abuseService.scanMutualRings(UUID.randomUUID(), LocalDateTime.now());
        abuseService.scanMutualRings(UUID.randomUUID(), LocalDateTime.now());

        List<EndorsementFlag> flags = flagRepository.findAllByOrderByLastDetectedAtDesc();
        assertThat(flags).hasSize(1);
        assertThat(flags.get(0).getOccurrenceCount()).isEqualTo(2);
    }

    // ---- fixture helpers ----

    private int insertUser(String tag) {
        return jdbc.queryForObject(
            "INSERT INTO user_table (user_firebase_uid, user_name, user_surname, user_username, user_email) " +
            "VALUES (?, ?, 'Test', ?, ?) RETURNING user_id",
            Integer.class, "uid-" + tag, tag, "user-" + tag, tag + "@example.com");
    }

    private int insertTaskShell() {
        return jdbc.queryForObject("INSERT INTO task_invoice_table DEFAULT VALUES RETURNING task_id", Integer.class);
    }

    private void endorse(int from, int to, int taskId) {
        jdbc.update("INSERT INTO endorsement_table (endorser_id, endorsee_id, location_id, skill_tag, task_id, weight) " +
                    "VALUES (?, ?, ?, 'test_skill', ?, 1)", from, to, zoneId, taskId);
    }

    private void endorseAt(int from, int to, LocalDateTime createdAt) {
        jdbc.update("INSERT INTO endorsement_table (endorser_id, endorsee_id, location_id, skill_tag, weight, created_at) " +
                    "VALUES (?, ?, ?, 'test_skill', 1, ?)", from, to, zoneId, createdAt);
    }

    private EndorsementClusterCache u3aRow(int userId, int label, int internal, int total, UUID runId, LocalDateTime now) {
        return new EndorsementClusterCache(zoneId, userId, label, internal, total, runId, now);
    }

    private void saveClusterRow(EndorsementClusterCache row) {
        clusterCacheRepository.save(row);
    }
}
