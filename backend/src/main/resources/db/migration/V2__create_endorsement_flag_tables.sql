-- V2__create_endorsement_flag_tables.sql
CREATE TABLE endorsement_flag_table (
    flag_id           BIGSERIAL PRIMARY KEY,
    pattern_type      VARCHAR(30)  NOT NULL
        CHECK (pattern_type IN ('mutual_ring', 'sudden_spike', 'island_group', 'timestamp_anomaly')),
    location_id       INTEGER      REFERENCES location_table (location_id) ON DELETE CASCADE,
    metric_value      DOUBLE PRECISION,
    status            VARCHAR(20)  NOT NULL DEFAULT 'open'
        CHECK (status IN ('open', 'investigate', 'dismiss')),
    occurrence_count  INTEGER      NOT NULL DEFAULT 1
        CHECK (occurrence_count > 0),
    first_detected_at TIMESTAMP    NOT NULL,
    last_detected_at  TIMESTAMP    NOT NULL,
    run_id            UUID         NOT NULL
);

CREATE INDEX idx_endorsement_flag_pattern_status
    ON endorsement_flag_table (pattern_type, status);

CREATE INDEX idx_endorsement_flag_zone
    ON endorsement_flag_table (location_id);

CREATE TABLE endorsement_flag_participant_table (
    flag_participant_id BIGSERIAL PRIMARY KEY,
    flag_id             BIGINT  NOT NULL REFERENCES endorsement_flag_table (flag_id) ON DELETE CASCADE,
    user_id             INTEGER NOT NULL REFERENCES user_table (user_id) ON DELETE CASCADE,
    role                VARCHAR(10)
        CHECK (role IS NULL OR role IN ('a', 'b'))
);

CREATE INDEX idx_endorsement_flag_participant_flag
    ON endorsement_flag_participant_table (flag_id);

CREATE INDEX idx_endorsement_flag_participant_user
    ON endorsement_flag_participant_table (user_id);