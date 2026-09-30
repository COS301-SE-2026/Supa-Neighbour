-- baseline-schema-raw.sql
-- Test-only baseline: mirrors what exists on Azure BEFORE any Flyway
-- migration runs, so Flyway's baseline-on-migrate (version 0) sees the same
-- starting point in tests as it does in production, then applies V1 and V2
-- on top exactly as it does on Azure.
--
-- Deliberately excludes:
--   - endorsement_cluster_cache (created by V1 — Flyway must create this)
--   - flyway_schema_history (Flyway creates and owns this itself)
--   - all Azure-specific ALTER ... OWNER TO / GRANT statements
--   - \restrict / \unrestrict (psql-only, not portable SQL)

CREATE TYPE public.participant_status AS ENUM (
    'ATTENDING',
    'ABSENT'
);

CREATE TYPE public.theme_mode AS ENUM (
    'LIGHT',
    'DARK'
);

SET default_tablespace = '';
SET default_table_access_method = heap;

CREATE TABLE public.address_table (
    address_id integer NOT NULL,
    address_number integer,
    address_street character varying(200),
    address_zip integer,
    neighbourhood_id integer
);

ALTER TABLE public.address_table ALTER COLUMN address_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.address_table_address_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.admin_application_table (
    application_id integer NOT NULL,
    user_id integer NOT NULL,
    application_status character varying(20) DEFAULT 'Pending'::character varying NOT NULL,
    application_date date DEFAULT CURRENT_DATE NOT NULL,
    reviewed_by_user_id integer,
    reviewed_date date,
    justification text,
    rejection_reason text
);

ALTER TABLE public.admin_application_table ALTER COLUMN application_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.admin_application_table_application_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.admin_table (
    admin_id integer NOT NULL,
    admin_create_date date,
    admin_access_level integer,
    user_id integer NOT NULL
);

ALTER TABLE public.admin_table ALTER COLUMN admin_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.admin_table_admin_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.analytics_table (
    analytics_id integer NOT NULL,
    task_id integer,
    admin_id integer,
    helper_type_id character varying(50),
    dependent_type_id character varying(50)
);

ALTER TABLE public.analytics_table ALTER COLUMN analytics_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.analytics_table_analytics_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.availability_table (
    availability_id integer NOT NULL,
    user_id integer NOT NULL,
    day_of_week character varying(10),
    time_window character varying(20),
    is_active boolean DEFAULT true
);

ALTER TABLE public.availability_table ALTER COLUMN availability_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.availability_table_availability_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.badge_table (
    badge_id integer NOT NULL,
    badge_name character varying(100) NOT NULL,
    badge_description text,
    is_specialist boolean DEFAULT false,
    current_xp integer DEFAULT 0,
    rating_id integer
);

ALTER TABLE public.badge_table ALTER COLUMN badge_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.badge_table_badge_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.chat_table (
    chat_id integer NOT NULL,
    task_id integer NOT NULL,
    dependent_user_id integer NOT NULL,
    helper_user_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.chat_table ALTER COLUMN chat_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.chat_table_chat_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.comments_table (
    comment_id integer NOT NULL,
    post_id integer NOT NULL,
    user_id integer NOT NULL,
    parent_comment_id integer,
    comment_content text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone
);

ALTER TABLE public.comments_table ALTER COLUMN comment_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.comments_table_comment_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.compatibility_table (
    compatibility_id integer NOT NULL,
    compatibility_score integer,
    compatibility_colour character varying(20),
    dependent_id integer,
    helper_id integer
);

ALTER TABLE public.compatibility_table ALTER COLUMN compatibility_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.compatibility_table_compatibility_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.dependent_analytics_table (
    dependent_analytics_id character varying(50) NOT NULL,
    user_id integer,
    task_type_id integer,
    total_tasks integer,
    location_id integer,
    average_rating double precision,
    average_giving_rating double precision
);

CREATE TABLE public.dependent_table (
    dependent_id integer NOT NULL,
    user_id integer NOT NULL,
    task_type_id integer
);

ALTER TABLE public.dependent_table ALTER COLUMN dependent_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.dependent_table_dependent_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

-- endorsement_skill_table / endorsement_table: untracked by Flyway, exist on
-- Azure by hand — kept here so V2 (which references endorsement_table only
-- indirectly, via endorsement_flag_participant_table -> user_table) and the
-- services under test have something real to run against.
CREATE TABLE public.endorsement_skill_table (
    skill_tag character varying(50) NOT NULL,
    display_name character varying(50) NOT NULL,
    category character varying(30),
    approved boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE TABLE public.endorsement_table (
    endorsement_id integer NOT NULL,
    endorser_id integer NOT NULL,
    endorsee_id integer NOT NULL,
    location_id integer,
    skill_tag character varying(50) NOT NULL,
    task_id integer,
    weight integer NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    CONSTRAINT chk_endorsement_not_self CHECK ((endorser_id <> endorsee_id))
);

ALTER TABLE public.endorsement_table ALTER COLUMN endorsement_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.endorsement_table_endorsement_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.event_participants (
    participant_id integer NOT NULL,
    event_id integer NOT NULL,
    user_id integer NOT NULL,
    participant_status public.participant_status DEFAULT 'ATTENDING'::public.participant_status NOT NULL
);

ALTER TABLE public.event_participants ALTER COLUMN participant_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.event_participants_participant_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.events_table (
    event_id integer NOT NULL,
    user_id integer NOT NULL,
    event_title character varying(255) NOT NULL,
    description text,
    event_created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    event_start_date_time timestamp without time zone NOT NULL,
    event_end_date_time timestamp without time zone NOT NULL,
    event_location character varying(255) NOT NULL,
    max_participants integer,
    is_live boolean DEFAULT false NOT NULL,
    CONSTRAINT chk_event_dates CHECK ((event_end_date_time > event_start_date_time)),
    CONSTRAINT chk_max_participants CHECK (((max_participants IS NULL) OR (max_participants > 0)))
);

ALTER TABLE public.events_table ALTER COLUMN event_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.events_table_event_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.google_calendar_token_table (
    token_id integer NOT NULL,
    user_id integer NOT NULL,
    refresh_token text NOT NULL,
    access_token text,
    access_token_expiry timestamp without time zone,
    connected_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.google_calendar_token_table_token_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.google_calendar_token_table_token_id_seq OWNED BY public.google_calendar_token_table.token_id;

CREATE TABLE public.helper_analytics_table (
    helper_analytics_id character varying(50) NOT NULL,
    user_id integer,
    task_type_id integer,
    location_id integer,
    average_rating double precision
);

CREATE TABLE public.helper_skill_table (
    helper_skill_id integer NOT NULL,
    helper_id integer NOT NULL,
    task_type_id integer NOT NULL
);

ALTER TABLE public.helper_skill_table ALTER COLUMN helper_skill_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.helper_skill_table_helper_skill_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.helper_table (
    helper_id integer NOT NULL,
    user_id integer NOT NULL,
    task_type_id integer,
    badge_id integer,
    helper_xp integer DEFAULT 0 NOT NULL,
    isavailable boolean DEFAULT true
);

ALTER TABLE public.helper_table ALTER COLUMN helper_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.helper_table_helper_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.reaction_table (
    reaction_id integer CONSTRAINT likes_table_like_id_not_null NOT NULL,
    user_id integer CONSTRAINT likes_table_user_id_not_null NOT NULL,
    post_id integer,
    comment_id integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone,
    reaction_type character varying(10) DEFAULT 'like'::character varying NOT NULL,
    CONSTRAINT chk_one_target CHECK ((((post_id IS NOT NULL) AND (comment_id IS NULL)) OR ((post_id IS NULL) AND (comment_id IS NOT NULL)))),
    CONSTRAINT reaction_table_reaction_type_check CHECK (((reaction_type)::text = ANY ((ARRAY['like'::character varying, 'dislike'::character varying])::text[])))
);

ALTER TABLE public.reaction_table ALTER COLUMN reaction_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.likes_table_like_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.location_table (
    location_id integer NOT NULL,
    location_center_point integer,
    location_radius integer,
    neighbourhood_id integer,
    neighbourhood_name character varying(100)
);

ALTER TABLE public.location_table ALTER COLUMN location_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.location_table_location_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.message_table (
    message_id integer NOT NULL,
    chat_id integer NOT NULL,
    sender_id integer NOT NULL,
    content text NOT NULL,
    message_type character varying(10) DEFAULT 'text'::character varying,
    is_read boolean DEFAULT false,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.message_table ALTER COLUMN message_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.message_table_message_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.moderation_action (
    moderation_action_id integer NOT NULL,
    user_id integer NOT NULL,
    action_type text NOT NULL,
    reason text NOT NULL,
    report_id integer,
    issued_by integer NOT NULL,
    issued_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at timestamp without time zone,
    lifted_at timestamp without time zone,
    lifted_by integer,
    CONSTRAINT moderation_action_action_type_check CHECK ((action_type = ANY (ARRAY['warning'::text, 'suspension'::text, 'ban'::text])))
);

CREATE SEQUENCE public.moderation_action_moderation_action_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.moderation_action_moderation_action_id_seq OWNED BY public.moderation_action.moderation_action_id;

CREATE TABLE public.notification_table (
    notification_id integer NOT NULL,
    user_id integer NOT NULL,
    notification_type character varying(30) NOT NULL,
    entity_id character varying(50),
    notification_title character varying(255) NOT NULL,
    notification_body character varying(500),
    is_read boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE public.notification_table ALTER COLUMN notification_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.notification_table_notification_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.posts_table (
    post_id integer NOT NULL,
    user_id integer NOT NULL,
    post_content text NOT NULL,
    media_url text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    category character varying(20) DEFAULT 'general'::character varying NOT NULL,
    CONSTRAINT posts_table_category_check CHECK (((category)::text = ANY ((ARRAY['general'::character varying, 'lost_pet'::character varying, 'local_event'::character varying, 'alert'::character varying, 'free_items'::character varying, 'complaint'::character varying, 'admin'::character varying])::text[])))
);

ALTER TABLE public.posts_table ALTER COLUMN post_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.posts_table_post_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.rating_table (
    rating_id integer NOT NULL,
    rating_review character varying(50),
    total_xp_level integer,
    current_group character varying(50)
);

ALTER TABLE public.rating_table ALTER COLUMN rating_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.rating_table_rating_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.report_image_table (
    report_image_id integer NOT NULL,
    report_id integer NOT NULL,
    image_url text NOT NULL,
    uploaded_at timestamp without time zone DEFAULT now()
);

CREATE SEQUENCE public.report_image_table_report_image_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.report_image_table_report_image_id_seq OWNED BY public.report_image_table.report_image_id;

CREATE TABLE public.report_table (
    report_id integer NOT NULL,
    report_type character varying(20) NOT NULL,
    reporter_user_id integer NOT NULL,
    status character varying(20) DEFAULT 'submitted'::character varying NOT NULL,
    admin_id integer,
    reported_user_id integer,
    reported_post_id integer,
    reported_comment_id integer,
    task_id integer,
    dispute_reason character varying(30),
    reason text,
    description text,
    violation_type character varying(30),
    severity character varying(10),
    suggested_action character varying(30),
    actual_action character varying(30),
    created_at timestamp without time zone DEFAULT now(),
    resolved_at timestamp without time zone,
    CONSTRAINT chk_report_target CHECK (((((report_type)::text = 'USER'::text) AND (reported_user_id IS NOT NULL) AND (reported_post_id IS NULL) AND (reported_comment_id IS NULL) AND (task_id IS NULL)) OR (((report_type)::text = 'POST'::text) AND (reported_post_id IS NOT NULL) AND (reported_user_id IS NULL) AND (reported_comment_id IS NULL) AND (task_id IS NULL)) OR (((report_type)::text = 'COMMENT'::text) AND (reported_comment_id IS NOT NULL) AND (reported_user_id IS NULL) AND (reported_post_id IS NULL) AND (task_id IS NULL)) OR (((report_type)::text = 'TASK_DISPUTE'::text) AND (task_id IS NOT NULL) AND (reported_user_id IS NULL) AND (reported_post_id IS NULL) AND (reported_comment_id IS NULL))))
);

CREATE SEQUENCE public.report_table_report_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.report_table_report_id_seq OWNED BY public.report_table.report_id;

CREATE TABLE public.settings_table (
    user_id integer NOT NULL,
    last_seen timestamp without time zone,
    show_status boolean DEFAULT true NOT NULL,
    show_phone_no boolean DEFAULT true NOT NULL,
    mode character varying(5) DEFAULT 'LIGHT'::character varying NOT NULL,
    CONSTRAINT settings_table_mode_check CHECK (((mode)::text = ANY ((ARRAY['LIGHT'::character varying, 'DARK'::character varying])::text[])))
);

CREATE TABLE public.task_image_table (
    task_image_id integer NOT NULL,
    task_id integer NOT NULL,
    image_url character varying(500) NOT NULL,
    uploaded_at timestamp without time zone DEFAULT now(),
    image_type character varying(20) DEFAULT 'COMPLETION'::character varying NOT NULL,
    ai_labels jsonb,
    ai_confidence numeric,
    ai_insight text,
    capture_source character varying(20),
    client_captured_at timestamp without time zone,
    client_lat double precision,
    client_lng double precision,
    client_accuracy_m double precision,
    device_id character varying(100),
    camera_make character varying(50),
    camera_model character varying(50),
    has_gps boolean,
    capture_time_valid boolean,
    image_hash character varying(64),
    perceptual_hash bigint,
    CONSTRAINT task_image_table_image_type_check CHECK (((image_type)::text = ANY ((ARRAY['REFERENCE'::character varying, 'COMPLETION'::character varying])::text[])))
);

ALTER TABLE public.task_image_table ALTER COLUMN task_image_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_image_table_task_image_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.task_invitation_table (
    invitation_id integer NOT NULL,
    task_id integer NOT NULL,
    helper_id integer NOT NULL,
    status character varying(20) DEFAULT 'Invited'::character varying,
    invited_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT task_invitation_table_status_check CHECK (((status)::text = ANY ((ARRAY['Invited'::character varying, 'Accepted'::character varying, 'Declined'::character varying, 'Rejected'::character varying])::text[])))
);

ALTER TABLE public.task_invitation_table ALTER COLUMN invitation_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_invitation_table_invitation_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.task_invoice_table (
    task_id integer NOT NULL,
    helper_id integer,
    dependent_id integer,
    is_immediate boolean DEFAULT false,
    location_id integer,
    task_type_id integer,
    needs_specialist boolean DEFAULT false,
    signed_admin_id integer,
    start_date date,
    end_date date,
    helper_badge_id integer,
    dependent_rating_review text,
    helper_rating_review text,
    admin_review character varying(300),
    compatibility_id integer,
    review_snippet character varying(300),
    status character varying(20) DEFAULT 'open'::character varying,
    title character varying(100),
    instructions text,
    start_time time without time zone,
    google_calendar_event_id_helper character varying(255),
    google_calendar_event_id_dependent character varying(255),
    task_lat double precision,
    task_lng double precision,
    CONSTRAINT task_invoice_table_status_check CHECK (((status)::text = ANY ((ARRAY['open'::character varying, 'assigned'::character varying, 'in_progress'::character varying, 'pending_approval'::character varying, 'completed'::character varying, 'cancelled'::character varying])::text[]))),
    CONSTRAINT task_invoice_table_task_lat_check CHECK (((task_lat >= ('-90'::integer)::double precision) AND (task_lat <= (90)::double precision))),
    CONSTRAINT task_invoice_table_task_lng_check CHECK (((task_lng >= ('-180'::integer)::double precision) AND (task_lng <= (180)::double precision)))
);

ALTER TABLE public.task_invoice_table ALTER COLUMN task_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_invoice_table_task_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.task_type_table (
    task_type_id integer NOT NULL,
    type_description character varying(255) NOT NULL,
    associated_badge_id integer,
    needs_specialist boolean DEFAULT false,
    xp_worth integer
);

ALTER TABLE public.task_type_table ALTER COLUMN task_type_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_type_table_task_type_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.task_verification_table (
    verification_id integer NOT NULL,
    task_id integer NOT NULL,
    completion_image_id integer,
    score numeric,
    status character varying(20),
    location_verified boolean,
    distance_m numeric,
    geofence_radius_m numeric,
    reasons jsonb,
    resident_decision character varying(10),
    decision_note text,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT task_verification_table_resident_decision_check CHECK (((resident_decision)::text = ANY ((ARRAY['CONFIRM'::character varying, 'DISPUTE'::character varying])::text[]))),
    CONSTRAINT task_verification_table_status_check CHECK (((status)::text = ANY ((ARRAY['VERIFIED'::character varying, 'NEEDS_REVIEW'::character varying, 'FAILED'::character varying])::text[])))
);

ALTER TABLE public.task_verification_table ALTER COLUMN verification_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_verification_table_verification_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.user_achievement_table (
    user_achievement_id integer NOT NULL,
    user_id integer NOT NULL,
    badge_id integer NOT NULL,
    awarded_on date,
    progress_current integer DEFAULT 0,
    progress_target integer
);

ALTER TABLE public.user_achievement_table ALTER COLUMN user_achievement_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_achievement_table_user_achievement_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

CREATE TABLE public.user_device_table (
    user_device_id integer NOT NULL,
    user_id integer NOT NULL,
    fcm_token character varying(255) NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);

CREATE SEQUENCE public.user_device_table_user_device_id_seq
    AS integer START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER SEQUENCE public.user_device_table_user_device_id_seq OWNED BY public.user_device_table.user_device_id;

CREATE TABLE public.user_table (
    user_id integer NOT NULL,
    user_firebase_uid character varying(128) NOT NULL,
    user_email_verified boolean DEFAULT false,
    user_phone_verified boolean DEFAULT false,
    user_name character varying(100) NOT NULL,
    user_surname character varying(100) NOT NULL,
    user_username character varying(100) NOT NULL,
    user_email character varying(255) NOT NULL,
    user_phone_number character varying(20),
    user_gender character varying(30),
    user_dob date,
    user_address_id integer,
    user_badge_id integer,
    user_rating_id integer,
    user_type character varying(20),
    is_admin boolean DEFAULT false NOT NULL
);

ALTER TABLE public.user_table ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_table_user_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1
);

-- defaults for the plain-CREATE-SEQUENCE tables (not IDENTITY columns)
ALTER TABLE ONLY public.google_calendar_token_table ALTER COLUMN token_id SET DEFAULT nextval('public.google_calendar_token_table_token_id_seq'::regclass);
ALTER TABLE ONLY public.moderation_action ALTER COLUMN moderation_action_id SET DEFAULT nextval('public.moderation_action_moderation_action_id_seq'::regclass);
ALTER TABLE ONLY public.report_image_table ALTER COLUMN report_image_id SET DEFAULT nextval('public.report_image_table_report_image_id_seq'::regclass);
ALTER TABLE ONLY public.report_table ALTER COLUMN report_id SET DEFAULT nextval('public.report_table_report_id_seq'::regclass);
ALTER TABLE ONLY public.user_device_table ALTER COLUMN user_device_id SET DEFAULT nextval('public.user_device_table_user_device_id_seq'::regclass);

-- primary keys
ALTER TABLE ONLY public.address_table ADD CONSTRAINT address_table_pkey PRIMARY KEY (address_id);
ALTER TABLE ONLY public.admin_application_table ADD CONSTRAINT admin_application_table_pkey PRIMARY KEY (application_id);
ALTER TABLE ONLY public.admin_table ADD CONSTRAINT admin_table_pkey PRIMARY KEY (admin_id);
ALTER TABLE ONLY public.analytics_table ADD CONSTRAINT analytics_table_pkey PRIMARY KEY (analytics_id);
ALTER TABLE ONLY public.availability_table ADD CONSTRAINT availability_table_pkey PRIMARY KEY (availability_id);
ALTER TABLE ONLY public.badge_table ADD CONSTRAINT badge_table_pkey PRIMARY KEY (badge_id);
ALTER TABLE ONLY public.chat_table ADD CONSTRAINT chat_table_pkey PRIMARY KEY (chat_id);
ALTER TABLE ONLY public.comments_table ADD CONSTRAINT comments_table_pkey PRIMARY KEY (comment_id);
ALTER TABLE ONLY public.compatibility_table ADD CONSTRAINT compatibility_table_pkey PRIMARY KEY (compatibility_id);
ALTER TABLE ONLY public.dependent_analytics_table ADD CONSTRAINT dependent_analytics_table_pkey PRIMARY KEY (dependent_analytics_id);
ALTER TABLE ONLY public.dependent_table ADD CONSTRAINT dependent_table_pkey PRIMARY KEY (dependent_id);
ALTER TABLE ONLY public.endorsement_skill_table ADD CONSTRAINT endorsement_skill_table_pkey PRIMARY KEY (skill_tag);
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_pkey PRIMARY KEY (endorsement_id);
ALTER TABLE ONLY public.event_participants ADD CONSTRAINT event_participants_pkey PRIMARY KEY (participant_id);
ALTER TABLE ONLY public.events_table ADD CONSTRAINT events_table_pkey PRIMARY KEY (event_id);
ALTER TABLE ONLY public.google_calendar_token_table ADD CONSTRAINT google_calendar_token_table_pkey PRIMARY KEY (token_id);
ALTER TABLE ONLY public.google_calendar_token_table ADD CONSTRAINT google_calendar_token_table_user_id_key UNIQUE (user_id);
ALTER TABLE ONLY public.helper_analytics_table ADD CONSTRAINT helper_analytics_table_pkey PRIMARY KEY (helper_analytics_id);
ALTER TABLE ONLY public.helper_skill_table ADD CONSTRAINT helper_skill_table_pkey PRIMARY KEY (helper_skill_id);
ALTER TABLE ONLY public.helper_table ADD CONSTRAINT helper_table_pkey PRIMARY KEY (helper_id);
ALTER TABLE ONLY public.reaction_table ADD CONSTRAINT likes_table_pkey PRIMARY KEY (reaction_id);
ALTER TABLE ONLY public.location_table ADD CONSTRAINT location_table_pkey PRIMARY KEY (location_id);
ALTER TABLE ONLY public.message_table ADD CONSTRAINT message_table_pkey PRIMARY KEY (message_id);
ALTER TABLE ONLY public.moderation_action ADD CONSTRAINT moderation_action_pkey PRIMARY KEY (moderation_action_id);
ALTER TABLE ONLY public.notification_table ADD CONSTRAINT notification_table_pkey PRIMARY KEY (notification_id);
ALTER TABLE ONLY public.posts_table ADD CONSTRAINT posts_table_pkey PRIMARY KEY (post_id);
ALTER TABLE ONLY public.rating_table ADD CONSTRAINT rating_table_pkey PRIMARY KEY (rating_id);
ALTER TABLE ONLY public.rating_table ADD CONSTRAINT rating_table_rating_review_key UNIQUE (rating_review);
ALTER TABLE ONLY public.report_image_table ADD CONSTRAINT report_image_table_pkey PRIMARY KEY (report_image_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_pkey PRIMARY KEY (report_id);
ALTER TABLE ONLY public.settings_table ADD CONSTRAINT settings_table_pkey PRIMARY KEY (user_id);
ALTER TABLE ONLY public.task_image_table ADD CONSTRAINT task_image_table_pkey PRIMARY KEY (task_image_id);
ALTER TABLE ONLY public.task_invitation_table ADD CONSTRAINT task_invitation_table_pkey PRIMARY KEY (invitation_id);
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_pkey PRIMARY KEY (task_id);
ALTER TABLE ONLY public.task_type_table ADD CONSTRAINT task_type_table_pkey PRIMARY KEY (task_type_id);
ALTER TABLE ONLY public.task_verification_table ADD CONSTRAINT task_verification_table_pkey PRIMARY KEY (verification_id);
ALTER TABLE ONLY public.event_participants ADD CONSTRAINT unique_event_participant UNIQUE (event_id, user_id);
ALTER TABLE ONLY public.admin_table ADD CONSTRAINT uq_admin_table_user_id UNIQUE (user_id);
ALTER TABLE ONLY public.chat_table ADD CONSTRAINT uq_chat_per_task UNIQUE (task_id);
ALTER TABLE ONLY public.helper_skill_table ADD CONSTRAINT uq_helper_skill UNIQUE (helper_id, task_type_id);
ALTER TABLE ONLY public.task_invitation_table ADD CONSTRAINT uq_invite_per_helper UNIQUE (task_id, helper_id);
ALTER TABLE ONLY public.user_achievement_table ADD CONSTRAINT uq_user_badge UNIQUE (user_id, badge_id);
ALTER TABLE ONLY public.user_achievement_table ADD CONSTRAINT user_achievement_table_pkey PRIMARY KEY (user_achievement_id);
ALTER TABLE ONLY public.user_device_table ADD CONSTRAINT user_device_table_fcm_token_key UNIQUE (fcm_token);
ALTER TABLE ONLY public.user_device_table ADD CONSTRAINT user_device_table_pkey PRIMARY KEY (user_device_id);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_pkey PRIMARY KEY (user_id);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_email_key UNIQUE (user_email);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_firebase_uid_key UNIQUE (user_firebase_uid);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_username_key UNIQUE (user_username);

-- indexes
CREATE INDEX idx_comments_parent ON public.comments_table USING btree (parent_comment_id);
CREATE INDEX idx_comments_post ON public.comments_table USING btree (post_id);
CREATE INDEX idx_compatibility_score ON public.compatibility_table USING btree (compatibility_score);
CREATE INDEX idx_dependent_task ON public.dependent_table USING btree (task_type_id);
CREATE INDEX idx_endorsement_endorsee ON public.endorsement_table USING btree (endorsee_id);
CREATE INDEX idx_endorsement_location ON public.endorsement_table USING btree (location_id);
CREATE INDEX idx_endorsement_skill_tag ON public.endorsement_table USING btree (skill_tag);
CREATE INDEX idx_helper_badge ON public.helper_table USING btree (badge_id);
CREATE INDEX idx_location_neighbourhood ON public.location_table USING btree (neighbourhood_id);
CREATE INDEX idx_messages_chat ON public.message_table USING btree (chat_id);
CREATE INDEX idx_messages_sent_at ON public.message_table USING btree (sent_at);
CREATE INDEX idx_notification_user ON public.notification_table USING btree (user_id);
CREATE INDEX idx_posts_user ON public.posts_table USING btree (user_id);
CREATE INDEX idx_reaction_comment ON public.reaction_table USING btree (comment_id);
CREATE INDEX idx_reaction_post ON public.reaction_table USING btree (post_id);
CREATE INDEX idx_report_image_report_id ON public.report_image_table USING btree (report_id);
CREATE INDEX idx_task_dates ON public.task_invoice_table USING btree (start_date, end_date);
CREATE INDEX idx_user_email ON public.user_table USING btree (user_email);
CREATE UNIQUE INDEX uq_no_duplicate_reaction_comment ON public.reaction_table USING btree (user_id, comment_id) WHERE (comment_id IS NOT NULL);
CREATE UNIQUE INDEX uq_no_duplicate_reaction_post ON public.reaction_table USING btree (user_id, post_id) WHERE (post_id IS NOT NULL);
CREATE UNIQUE INDEX ux_task_image_hash ON public.task_image_table USING btree (image_hash) WHERE (image_hash IS NOT NULL);

-- foreign keys
ALTER TABLE ONLY public.address_table ADD CONSTRAINT address_table_neighbourhood_id_fkey FOREIGN KEY (neighbourhood_id) REFERENCES public.location_table(location_id);
ALTER TABLE ONLY public.admin_application_table ADD CONSTRAINT admin_application_table_reviewed_by_user_id_fkey FOREIGN KEY (reviewed_by_user_id) REFERENCES public.user_table(user_id) ON DELETE SET NULL;
ALTER TABLE ONLY public.admin_application_table ADD CONSTRAINT admin_application_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.admin_table ADD CONSTRAINT admin_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.analytics_table ADD CONSTRAINT analytics_table_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.admin_table(admin_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.analytics_table ADD CONSTRAINT analytics_table_dependent_type_id_fkey FOREIGN KEY (dependent_type_id) REFERENCES public.dependent_analytics_table(dependent_analytics_id);
ALTER TABLE ONLY public.analytics_table ADD CONSTRAINT analytics_table_helper_type_id_fkey FOREIGN KEY (helper_type_id) REFERENCES public.helper_analytics_table(helper_analytics_id);
ALTER TABLE ONLY public.analytics_table ADD CONSTRAINT analytics_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.availability_table ADD CONSTRAINT availability_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.badge_table ADD CONSTRAINT badge_table_rating_id_fkey FOREIGN KEY (rating_id) REFERENCES public.rating_table(rating_id);
ALTER TABLE ONLY public.chat_table ADD CONSTRAINT chat_table_dependent_user_id_fkey FOREIGN KEY (dependent_user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.chat_table ADD CONSTRAINT chat_table_helper_user_id_fkey FOREIGN KEY (helper_user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.chat_table ADD CONSTRAINT chat_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.comments_table ADD CONSTRAINT comments_table_parent_comment_id_fkey FOREIGN KEY (parent_comment_id) REFERENCES public.comments_table(comment_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.comments_table ADD CONSTRAINT comments_table_post_id_fkey FOREIGN KEY (post_id) REFERENCES public.posts_table(post_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.comments_table ADD CONSTRAINT comments_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.compatibility_table ADD CONSTRAINT compatibility_table_dependent_id_fkey FOREIGN KEY (dependent_id) REFERENCES public.dependent_table(dependent_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.compatibility_table ADD CONSTRAINT compatibility_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.dependent_analytics_table ADD CONSTRAINT dependent_analytics_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);
ALTER TABLE ONLY public.dependent_analytics_table ADD CONSTRAINT dependent_analytics_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.dependent_analytics_table ADD CONSTRAINT dependent_analytics_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.dependent_table ADD CONSTRAINT dependent_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.dependent_table ADD CONSTRAINT dependent_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_endorsee_id_fkey FOREIGN KEY (endorsee_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_endorser_id_fkey FOREIGN KEY (endorser_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_skill_tag_fkey FOREIGN KEY (skill_tag) REFERENCES public.endorsement_skill_table(skill_tag);
ALTER TABLE ONLY public.endorsement_table ADD CONSTRAINT endorsement_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.event_participants ADD CONSTRAINT fk_event_participant_event FOREIGN KEY (event_id) REFERENCES public.events_table(event_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.event_participants ADD CONSTRAINT fk_event_participant_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.events_table ADD CONSTRAINT fk_event_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.moderation_action ADD CONSTRAINT fk_moderation_issued_by FOREIGN KEY (issued_by) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.moderation_action ADD CONSTRAINT fk_moderation_lifted_by FOREIGN KEY (lifted_by) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.moderation_action ADD CONSTRAINT fk_moderation_report FOREIGN KEY (report_id) REFERENCES public.report_table(report_id);
ALTER TABLE ONLY public.moderation_action ADD CONSTRAINT fk_moderation_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.settings_table ADD CONSTRAINT fk_settings_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.google_calendar_token_table ADD CONSTRAINT google_calendar_token_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.helper_analytics_table ADD CONSTRAINT helper_analytics_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);
ALTER TABLE ONLY public.helper_analytics_table ADD CONSTRAINT helper_analytics_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.helper_analytics_table ADD CONSTRAINT helper_analytics_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.helper_skill_table ADD CONSTRAINT helper_skill_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.helper_skill_table ADD CONSTRAINT helper_skill_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.helper_table ADD CONSTRAINT helper_table_badge_id_fkey FOREIGN KEY (badge_id) REFERENCES public.badge_table(badge_id);
ALTER TABLE ONLY public.helper_table ADD CONSTRAINT helper_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.helper_table ADD CONSTRAINT helper_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.reaction_table ADD CONSTRAINT likes_table_comment_id_fkey FOREIGN KEY (comment_id) REFERENCES public.comments_table(comment_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.reaction_table ADD CONSTRAINT likes_table_post_id_fkey FOREIGN KEY (post_id) REFERENCES public.posts_table(post_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.reaction_table ADD CONSTRAINT likes_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.message_table ADD CONSTRAINT message_table_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES public.chat_table(chat_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.message_table ADD CONSTRAINT message_table_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.notification_table ADD CONSTRAINT notification_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.posts_table ADD CONSTRAINT posts_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.report_image_table ADD CONSTRAINT report_image_table_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.report_table(report_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_reported_comment_id_fkey FOREIGN KEY (reported_comment_id) REFERENCES public.comments_table(comment_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_reported_post_id_fkey FOREIGN KEY (reported_post_id) REFERENCES public.posts_table(post_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_reported_user_id_fkey FOREIGN KEY (reported_user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_reporter_user_id_fkey FOREIGN KEY (reporter_user_id) REFERENCES public.user_table(user_id);
ALTER TABLE ONLY public.report_table ADD CONSTRAINT report_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.task_image_table ADD CONSTRAINT task_image_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.task_invitation_table ADD CONSTRAINT task_invitation_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.task_invitation_table ADD CONSTRAINT task_invitation_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_compatibility_id_fkey FOREIGN KEY (compatibility_id) REFERENCES public.compatibility_table(compatibility_id);
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_dependent_id_fkey FOREIGN KEY (dependent_id) REFERENCES public.dependent_table(dependent_id) ON DELETE SET NULL;
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_helper_badge_id_fkey FOREIGN KEY (helper_badge_id) REFERENCES public.badge_table(badge_id);
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE SET NULL;
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_signed_admin_id_fkey FOREIGN KEY (signed_admin_id) REFERENCES public.admin_table(admin_id) ON DELETE SET NULL;
ALTER TABLE ONLY public.task_invoice_table ADD CONSTRAINT task_invoice_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);
ALTER TABLE ONLY public.task_type_table ADD CONSTRAINT task_type_table_associated_badge_id_fkey FOREIGN KEY (associated_badge_id) REFERENCES public.badge_table(badge_id);
ALTER TABLE ONLY public.task_verification_table ADD CONSTRAINT task_verification_table_completion_image_id_fkey FOREIGN KEY (completion_image_id) REFERENCES public.task_image_table(task_image_id);
ALTER TABLE ONLY public.task_verification_table ADD CONSTRAINT task_verification_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);
ALTER TABLE ONLY public.user_achievement_table ADD CONSTRAINT user_achievement_table_badge_id_fkey FOREIGN KEY (badge_id) REFERENCES public.badge_table(badge_id);
ALTER TABLE ONLY public.user_achievement_table ADD CONSTRAINT user_achievement_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.user_device_table ADD CONSTRAINT user_device_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_address_id_fkey FOREIGN KEY (user_address_id) REFERENCES public.address_table(address_id);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_badge_id_fkey FOREIGN KEY (user_badge_id) REFERENCES public.badge_table(badge_id);
ALTER TABLE ONLY public.user_table ADD CONSTRAINT user_table_user_rating_id_fkey FOREIGN KEY (user_rating_id) REFERENCES public.rating_table(rating_id);