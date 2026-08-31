--
-- PostgreSQL database dump
--

\restrict J6JhnmN4gAuLlAOwhjw3hjPIPvpASxdWh6RWu4hc2Tab9cb5hduMybgg2ekdmYO

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.6 (Ubuntu 18.6-0ubuntu0.26.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


--
-- Name: theme_mode; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.theme_mode AS ENUM (
    'LIGHT',
    'DARK'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: address_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.address_table (
    address_id integer NOT NULL,
    address_number integer,
    address_street character varying(200),
    address_zip integer,
    neighbourhood_id integer
);


--
-- Name: address_table_address_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.address_table ALTER COLUMN address_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.address_table_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: admin_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.admin_table (
    admin_id integer NOT NULL,
    admin_create_date date,
    admin_access_level integer,
    user_id integer NOT NULL
);


--
-- Name: admin_table_admin_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.admin_table ALTER COLUMN admin_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.admin_table_admin_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: analytics_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.analytics_table (
    analytics_id integer NOT NULL,
    task_id integer,
    admin_id integer,
    helper_type_id character varying(50),
    dependent_type_id character varying(50)
);


--
-- Name: analytics_table_analytics_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.analytics_table ALTER COLUMN analytics_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.analytics_table_analytics_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: availability_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.availability_table (
    availability_id integer NOT NULL,
    user_id integer NOT NULL,
    day_of_week character varying(10),
    time_window character varying(20),
    is_active boolean DEFAULT true
);


--
-- Name: availability_table_availability_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.availability_table ALTER COLUMN availability_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.availability_table_availability_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: badge_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.badge_table (
    badge_id integer NOT NULL,
    badge_name character varying(100) NOT NULL,
    badge_description text,
    is_specialist boolean DEFAULT false,
    current_xp integer DEFAULT 0,
    rating_id integer
);


--
-- Name: badge_table_badge_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.badge_table ALTER COLUMN badge_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.badge_table_badge_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: chat_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chat_table (
    chat_id integer NOT NULL,
    task_id integer NOT NULL,
    dependent_user_id integer NOT NULL,
    helper_user_id integer NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: chat_table_chat_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.chat_table ALTER COLUMN chat_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.chat_table_chat_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: comments_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.comments_table (
    comment_id integer NOT NULL,
    post_id integer NOT NULL,
    user_id integer NOT NULL,
    parent_comment_id integer,
    comment_content text NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone
);


--
-- Name: comments_table_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.comments_table ALTER COLUMN comment_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.comments_table_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: compatibility_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.compatibility_table (
    compatibility_id integer NOT NULL,
    compatibility_score integer,
    compatibility_colour character varying(20),
    dependent_id integer,
    helper_id integer
);


--
-- Name: compatibility_table_compatibility_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.compatibility_table ALTER COLUMN compatibility_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.compatibility_table_compatibility_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: dependent_analytics_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dependent_analytics_table (
    dependent_analytics_id character varying(50) NOT NULL,
    user_id integer,
    task_type_id integer,
    total_tasks integer,
    location_id integer,
    average_rating double precision,
    average_giving_rating double precision
);


--
-- Name: dependent_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dependent_table (
    dependent_id integer NOT NULL,
    user_id integer NOT NULL,
    task_type_id integer
);


--
-- Name: dependent_table_dependent_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.dependent_table ALTER COLUMN dependent_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.dependent_table_dependent_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: helper_analytics_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.helper_analytics_table (
    helper_analytics_id character varying(50) NOT NULL,
    user_id integer,
    task_type_id integer,
    location_id integer,
    average_rating double precision
);


--
-- Name: helper_skill_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.helper_skill_table (
    helper_skill_id integer NOT NULL,
    helper_id integer NOT NULL,
    task_type_id integer NOT NULL
);


--
-- Name: helper_skill_table_helper_skill_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.helper_skill_table ALTER COLUMN helper_skill_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.helper_skill_table_helper_skill_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: helper_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.helper_table (
    helper_id integer NOT NULL,
    user_id integer NOT NULL,
    task_type_id integer,
    badge_id integer,
    helper_xp integer DEFAULT 0 NOT NULL,
    isavailable boolean DEFAULT true
);


--
-- Name: helper_table_helper_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.helper_table ALTER COLUMN helper_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.helper_table_helper_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: reaction_table; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: likes_table_like_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.reaction_table ALTER COLUMN reaction_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.likes_table_like_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: location_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.location_table (
    location_id integer NOT NULL,
    location_center_point integer,
    location_radius integer,
    neighbourhood_id integer,
    neighbourhood_name character varying(100)
);


--
-- Name: location_table_location_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.location_table ALTER COLUMN location_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.location_table_location_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: message_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.message_table (
    message_id integer NOT NULL,
    chat_id integer NOT NULL,
    sender_id integer NOT NULL,
    content text NOT NULL,
    message_type character varying(10) DEFAULT 'text'::character varying,
    is_read boolean DEFAULT false,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: message_table_message_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.message_table ALTER COLUMN message_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.message_table_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: moderation_action; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: moderation_action_moderation_action_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.moderation_action_moderation_action_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: moderation_action_moderation_action_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.moderation_action_moderation_action_id_seq OWNED BY public.moderation_action.moderation_action_id;


--
-- Name: posts_table; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: posts_table_post_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.posts_table ALTER COLUMN post_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.posts_table_post_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: rating_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rating_table (
    rating_id integer NOT NULL,
    rating_review character varying(50),
    total_xp_level integer,
    current_group character varying(50)
);


--
-- Name: rating_table_rating_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.rating_table ALTER COLUMN rating_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.rating_table_rating_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: report_table; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: report_table_report_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.report_table_report_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: report_table_report_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.report_table_report_id_seq OWNED BY public.report_table.report_id;


--
-- Name: settings_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.settings_table (
    user_id integer NOT NULL,
    last_seen timestamp without time zone,
    show_status boolean DEFAULT true NOT NULL,
    show_phone_no boolean DEFAULT true NOT NULL,
    mode character varying(5) DEFAULT 'LIGHT'::character varying NOT NULL,
    CONSTRAINT settings_table_mode_check CHECK (((mode)::text = ANY ((ARRAY['LIGHT'::character varying, 'DARK'::character varying])::text[])))
);


--
-- Name: task_image_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.task_image_table (
    task_image_id integer NOT NULL,
    task_id integer NOT NULL,
    image_url character varying(500) NOT NULL,
    uploaded_at timestamp without time zone DEFAULT now()
);


--
-- Name: task_image_table_task_image_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.task_image_table ALTER COLUMN task_image_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_image_table_task_image_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: task_invitation_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.task_invitation_table (
    invitation_id integer NOT NULL,
    task_id integer NOT NULL,
    helper_id integer NOT NULL,
    status character varying(20) DEFAULT 'Invited'::character varying,
    invited_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT task_invitation_table_status_check CHECK (((status)::text = ANY ((ARRAY['Invited'::character varying, 'Accepted'::character varying, 'Declined'::character varying, 'Rejected'::character varying])::text[])))
);


--
-- Name: task_invitation_table_invitation_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.task_invitation_table ALTER COLUMN invitation_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_invitation_table_invitation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: task_invoice_table; Type: TABLE; Schema: public; Owner: -
--

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
    CONSTRAINT task_invoice_table_status_check CHECK (((status)::text = ANY ((ARRAY['open'::character varying, 'assigned'::character varying, 'in_progress'::character varying, 'pending_approval'::character varying, 'completed'::character varying, 'cancelled'::character varying])::text[])))
);


--
-- Name: task_invoice_table_task_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.task_invoice_table ALTER COLUMN task_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_invoice_table_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: task_type_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.task_type_table (
    task_type_id integer NOT NULL,
    type_description character varying(255) NOT NULL,
    associated_badge_id integer,
    needs_specialist boolean DEFAULT false,
    xp_worth integer
);


--
-- Name: task_type_table_task_type_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.task_type_table ALTER COLUMN task_type_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.task_type_table_task_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_achievement_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_achievement_table (
    user_achievement_id integer NOT NULL,
    user_id integer NOT NULL,
    badge_id integer NOT NULL,
    awarded_on date,
    progress_current integer DEFAULT 0,
    progress_target integer
);


--
-- Name: user_achievement_table_user_achievement_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.user_achievement_table ALTER COLUMN user_achievement_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_achievement_table_user_achievement_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: user_device_table; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_device_table (
    user_device_id integer NOT NULL,
    user_id integer NOT NULL,
    fcm_token character varying(255) NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: user_device_table_user_device_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.user_device_table_user_device_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: user_device_table_user_device_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.user_device_table_user_device_id_seq OWNED BY public.user_device_table.user_device_id;


--
-- Name: user_table; Type: TABLE; Schema: public; Owner: -
--

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


--
-- Name: user_table_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE public.user_table ALTER COLUMN user_id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME public.user_table_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: moderation_action moderation_action_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action ALTER COLUMN moderation_action_id SET DEFAULT nextval('public.moderation_action_moderation_action_id_seq'::regclass);


--
-- Name: report_table report_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table ALTER COLUMN report_id SET DEFAULT nextval('public.report_table_report_id_seq'::regclass);


--
-- Name: user_device_table user_device_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_device_table ALTER COLUMN user_device_id SET DEFAULT nextval('public.user_device_table_user_device_id_seq'::regclass);


--
-- Name: address_table address_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address_table
    ADD CONSTRAINT address_table_pkey PRIMARY KEY (address_id);


--
-- Name: admin_table admin_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_table
    ADD CONSTRAINT admin_table_pkey PRIMARY KEY (admin_id);


--
-- Name: analytics_table analytics_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analytics_table
    ADD CONSTRAINT analytics_table_pkey PRIMARY KEY (analytics_id);


--
-- Name: availability_table availability_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.availability_table
    ADD CONSTRAINT availability_table_pkey PRIMARY KEY (availability_id);


--
-- Name: badge_table badge_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.badge_table
    ADD CONSTRAINT badge_table_pkey PRIMARY KEY (badge_id);


--
-- Name: chat_table chat_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_table
    ADD CONSTRAINT chat_table_pkey PRIMARY KEY (chat_id);


--
-- Name: comments_table comments_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comments_table
    ADD CONSTRAINT comments_table_pkey PRIMARY KEY (comment_id);


--
-- Name: compatibility_table compatibility_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.compatibility_table
    ADD CONSTRAINT compatibility_table_pkey PRIMARY KEY (compatibility_id);


--
-- Name: dependent_analytics_table dependent_analytics_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_analytics_table
    ADD CONSTRAINT dependent_analytics_table_pkey PRIMARY KEY (dependent_analytics_id);


--
-- Name: dependent_table dependent_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_table
    ADD CONSTRAINT dependent_table_pkey PRIMARY KEY (dependent_id);


--
-- Name: helper_analytics_table helper_analytics_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_analytics_table
    ADD CONSTRAINT helper_analytics_table_pkey PRIMARY KEY (helper_analytics_id);


--
-- Name: helper_skill_table helper_skill_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_skill_table
    ADD CONSTRAINT helper_skill_table_pkey PRIMARY KEY (helper_skill_id);


--
-- Name: helper_table helper_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_table
    ADD CONSTRAINT helper_table_pkey PRIMARY KEY (helper_id);


--
-- Name: reaction_table likes_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reaction_table
    ADD CONSTRAINT likes_table_pkey PRIMARY KEY (reaction_id);


--
-- Name: location_table location_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.location_table
    ADD CONSTRAINT location_table_pkey PRIMARY KEY (location_id);


--
-- Name: message_table message_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_table
    ADD CONSTRAINT message_table_pkey PRIMARY KEY (message_id);


--
-- Name: moderation_action moderation_action_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action
    ADD CONSTRAINT moderation_action_pkey PRIMARY KEY (moderation_action_id);


--
-- Name: posts_table posts_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.posts_table
    ADD CONSTRAINT posts_table_pkey PRIMARY KEY (post_id);


--
-- Name: rating_table rating_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rating_table
    ADD CONSTRAINT rating_table_pkey PRIMARY KEY (rating_id);


--
-- Name: rating_table rating_table_rating_review_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rating_table
    ADD CONSTRAINT rating_table_rating_review_key UNIQUE (rating_review);


--
-- Name: report_table report_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_pkey PRIMARY KEY (report_id);


--
-- Name: settings_table settings_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.settings_table
    ADD CONSTRAINT settings_table_pkey PRIMARY KEY (user_id);


--
-- Name: task_image_table task_image_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_image_table
    ADD CONSTRAINT task_image_table_pkey PRIMARY KEY (task_image_id);


--
-- Name: task_invitation_table task_invitation_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invitation_table
    ADD CONSTRAINT task_invitation_table_pkey PRIMARY KEY (invitation_id);


--
-- Name: task_invoice_table task_invoice_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_pkey PRIMARY KEY (task_id);


--
-- Name: task_type_table task_type_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_type_table
    ADD CONSTRAINT task_type_table_pkey PRIMARY KEY (task_type_id);


--
-- Name: chat_table uq_chat_per_task; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_table
    ADD CONSTRAINT uq_chat_per_task UNIQUE (task_id);


--
-- Name: helper_skill_table uq_helper_skill; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_skill_table
    ADD CONSTRAINT uq_helper_skill UNIQUE (helper_id, task_type_id);


--
-- Name: task_invitation_table uq_invite_per_helper; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invitation_table
    ADD CONSTRAINT uq_invite_per_helper UNIQUE (task_id, helper_id);


--
-- Name: user_achievement_table uq_user_badge; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_achievement_table
    ADD CONSTRAINT uq_user_badge UNIQUE (user_id, badge_id);


--
-- Name: user_achievement_table user_achievement_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_achievement_table
    ADD CONSTRAINT user_achievement_table_pkey PRIMARY KEY (user_achievement_id);


--
-- Name: user_device_table user_device_table_fcm_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_device_table
    ADD CONSTRAINT user_device_table_fcm_token_key UNIQUE (fcm_token);


--
-- Name: user_device_table user_device_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_device_table
    ADD CONSTRAINT user_device_table_pkey PRIMARY KEY (user_device_id);


--
-- Name: user_table user_table_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_pkey PRIMARY KEY (user_id);


--
-- Name: user_table user_table_user_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_email_key UNIQUE (user_email);


--
-- Name: user_table user_table_user_firebase_uid_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_firebase_uid_key UNIQUE (user_firebase_uid);


--
-- Name: user_table user_table_user_username_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_username_key UNIQUE (user_username);


--
-- Name: idx_comments_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_comments_parent ON public.comments_table USING btree (parent_comment_id);


--
-- Name: idx_comments_post; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_comments_post ON public.comments_table USING btree (post_id);


--
-- Name: idx_compatibility_score; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_compatibility_score ON public.compatibility_table USING btree (compatibility_score);


--
-- Name: idx_dependent_task; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_dependent_task ON public.dependent_table USING btree (task_type_id);


--
-- Name: idx_helper_badge; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_helper_badge ON public.helper_table USING btree (badge_id);


--
-- Name: idx_location_neighbourhood; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_location_neighbourhood ON public.location_table USING btree (neighbourhood_id);


--
-- Name: idx_messages_chat; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_messages_chat ON public.message_table USING btree (chat_id);


--
-- Name: idx_messages_sent_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_messages_sent_at ON public.message_table USING btree (sent_at);


--
-- Name: idx_posts_user; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_posts_user ON public.posts_table USING btree (user_id);


--
-- Name: idx_reaction_comment; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reaction_comment ON public.reaction_table USING btree (comment_id);


--
-- Name: idx_reaction_post; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_reaction_post ON public.reaction_table USING btree (post_id);


--
-- Name: idx_task_dates; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_task_dates ON public.task_invoice_table USING btree (start_date, end_date);


--
-- Name: idx_user_email; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_user_email ON public.user_table USING btree (user_email);


--
-- Name: uq_no_duplicate_reaction_comment; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_no_duplicate_reaction_comment ON public.reaction_table USING btree (user_id, comment_id) WHERE (comment_id IS NOT NULL);


--
-- Name: uq_no_duplicate_reaction_post; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX uq_no_duplicate_reaction_post ON public.reaction_table USING btree (user_id, post_id) WHERE (post_id IS NOT NULL);


--
-- Name: address_table address_table_neighbourhood_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address_table
    ADD CONSTRAINT address_table_neighbourhood_id_fkey FOREIGN KEY (neighbourhood_id) REFERENCES public.location_table(location_id);


--
-- Name: admin_table admin_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.admin_table
    ADD CONSTRAINT admin_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: analytics_table analytics_table_admin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analytics_table
    ADD CONSTRAINT analytics_table_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.admin_table(admin_id) ON DELETE CASCADE;


--
-- Name: analytics_table analytics_table_dependent_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analytics_table
    ADD CONSTRAINT analytics_table_dependent_type_id_fkey FOREIGN KEY (dependent_type_id) REFERENCES public.dependent_analytics_table(dependent_analytics_id);


--
-- Name: analytics_table analytics_table_helper_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analytics_table
    ADD CONSTRAINT analytics_table_helper_type_id_fkey FOREIGN KEY (helper_type_id) REFERENCES public.helper_analytics_table(helper_analytics_id);


--
-- Name: analytics_table analytics_table_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.analytics_table
    ADD CONSTRAINT analytics_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);


--
-- Name: availability_table availability_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.availability_table
    ADD CONSTRAINT availability_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: badge_table badge_table_rating_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.badge_table
    ADD CONSTRAINT badge_table_rating_id_fkey FOREIGN KEY (rating_id) REFERENCES public.rating_table(rating_id);


--
-- Name: chat_table chat_table_dependent_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_table
    ADD CONSTRAINT chat_table_dependent_user_id_fkey FOREIGN KEY (dependent_user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: chat_table chat_table_helper_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_table
    ADD CONSTRAINT chat_table_helper_user_id_fkey FOREIGN KEY (helper_user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: chat_table chat_table_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chat_table
    ADD CONSTRAINT chat_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);


--
-- Name: comments_table comments_table_parent_comment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comments_table
    ADD CONSTRAINT comments_table_parent_comment_id_fkey FOREIGN KEY (parent_comment_id) REFERENCES public.comments_table(comment_id) ON DELETE CASCADE;


--
-- Name: comments_table comments_table_post_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comments_table
    ADD CONSTRAINT comments_table_post_id_fkey FOREIGN KEY (post_id) REFERENCES public.posts_table(post_id) ON DELETE CASCADE;


--
-- Name: comments_table comments_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.comments_table
    ADD CONSTRAINT comments_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: compatibility_table compatibility_table_dependent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.compatibility_table
    ADD CONSTRAINT compatibility_table_dependent_id_fkey FOREIGN KEY (dependent_id) REFERENCES public.dependent_table(dependent_id) ON DELETE CASCADE;


--
-- Name: compatibility_table compatibility_table_helper_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.compatibility_table
    ADD CONSTRAINT compatibility_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;


--
-- Name: dependent_analytics_table dependent_analytics_table_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_analytics_table
    ADD CONSTRAINT dependent_analytics_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);


--
-- Name: dependent_analytics_table dependent_analytics_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_analytics_table
    ADD CONSTRAINT dependent_analytics_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: dependent_analytics_table dependent_analytics_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_analytics_table
    ADD CONSTRAINT dependent_analytics_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);


--
-- Name: dependent_table dependent_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_table
    ADD CONSTRAINT dependent_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: dependent_table dependent_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dependent_table
    ADD CONSTRAINT dependent_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: moderation_action fk_moderation_issued_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action
    ADD CONSTRAINT fk_moderation_issued_by FOREIGN KEY (issued_by) REFERENCES public.user_table(user_id);


--
-- Name: moderation_action fk_moderation_lifted_by; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action
    ADD CONSTRAINT fk_moderation_lifted_by FOREIGN KEY (lifted_by) REFERENCES public.user_table(user_id);


--
-- Name: moderation_action fk_moderation_report; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action
    ADD CONSTRAINT fk_moderation_report FOREIGN KEY (report_id) REFERENCES public.report_table(report_id);


--
-- Name: moderation_action fk_moderation_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.moderation_action
    ADD CONSTRAINT fk_moderation_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id);


--
-- Name: settings_table fk_settings_user; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.settings_table
    ADD CONSTRAINT fk_settings_user FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: helper_analytics_table helper_analytics_table_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_analytics_table
    ADD CONSTRAINT helper_analytics_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);


--
-- Name: helper_analytics_table helper_analytics_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_analytics_table
    ADD CONSTRAINT helper_analytics_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: helper_analytics_table helper_analytics_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_analytics_table
    ADD CONSTRAINT helper_analytics_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: helper_skill_table helper_skill_table_helper_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_skill_table
    ADD CONSTRAINT helper_skill_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;


--
-- Name: helper_skill_table helper_skill_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_skill_table
    ADD CONSTRAINT helper_skill_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: helper_table helper_table_badge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_table
    ADD CONSTRAINT helper_table_badge_id_fkey FOREIGN KEY (badge_id) REFERENCES public.badge_table(badge_id);


--
-- Name: helper_table helper_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_table
    ADD CONSTRAINT helper_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: helper_table helper_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.helper_table
    ADD CONSTRAINT helper_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: reaction_table likes_table_comment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reaction_table
    ADD CONSTRAINT likes_table_comment_id_fkey FOREIGN KEY (comment_id) REFERENCES public.comments_table(comment_id) ON DELETE CASCADE;


--
-- Name: reaction_table likes_table_post_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reaction_table
    ADD CONSTRAINT likes_table_post_id_fkey FOREIGN KEY (post_id) REFERENCES public.posts_table(post_id) ON DELETE CASCADE;


--
-- Name: reaction_table likes_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reaction_table
    ADD CONSTRAINT likes_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: message_table message_table_chat_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_table
    ADD CONSTRAINT message_table_chat_id_fkey FOREIGN KEY (chat_id) REFERENCES public.chat_table(chat_id) ON DELETE CASCADE;


--
-- Name: message_table message_table_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_table
    ADD CONSTRAINT message_table_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: posts_table posts_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.posts_table
    ADD CONSTRAINT posts_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: report_table report_table_admin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.user_table(user_id);


--
-- Name: report_table report_table_reported_comment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_reported_comment_id_fkey FOREIGN KEY (reported_comment_id) REFERENCES public.comments_table(comment_id);


--
-- Name: report_table report_table_reported_post_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_reported_post_id_fkey FOREIGN KEY (reported_post_id) REFERENCES public.posts_table(post_id);


--
-- Name: report_table report_table_reported_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_reported_user_id_fkey FOREIGN KEY (reported_user_id) REFERENCES public.user_table(user_id);


--
-- Name: report_table report_table_reporter_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_reporter_user_id_fkey FOREIGN KEY (reporter_user_id) REFERENCES public.user_table(user_id);


--
-- Name: report_table report_table_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.report_table
    ADD CONSTRAINT report_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);


--
-- Name: task_image_table task_image_table_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_image_table
    ADD CONSTRAINT task_image_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);


--
-- Name: task_invitation_table task_invitation_table_helper_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invitation_table
    ADD CONSTRAINT task_invitation_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE CASCADE;


--
-- Name: task_invitation_table task_invitation_table_task_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invitation_table
    ADD CONSTRAINT task_invitation_table_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.task_invoice_table(task_id);


--
-- Name: task_invoice_table task_invoice_table_compatibility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_compatibility_id_fkey FOREIGN KEY (compatibility_id) REFERENCES public.compatibility_table(compatibility_id);


--
-- Name: task_invoice_table task_invoice_table_dependent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_dependent_id_fkey FOREIGN KEY (dependent_id) REFERENCES public.dependent_table(dependent_id) ON DELETE SET NULL;


--
-- Name: task_invoice_table task_invoice_table_helper_badge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_helper_badge_id_fkey FOREIGN KEY (helper_badge_id) REFERENCES public.badge_table(badge_id);


--
-- Name: task_invoice_table task_invoice_table_helper_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_helper_id_fkey FOREIGN KEY (helper_id) REFERENCES public.helper_table(helper_id) ON DELETE SET NULL;


--
-- Name: task_invoice_table task_invoice_table_location_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_location_id_fkey FOREIGN KEY (location_id) REFERENCES public.location_table(location_id);


--
-- Name: task_invoice_table task_invoice_table_signed_admin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_signed_admin_id_fkey FOREIGN KEY (signed_admin_id) REFERENCES public.admin_table(admin_id) ON DELETE SET NULL;


--
-- Name: task_invoice_table task_invoice_table_task_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_invoice_table
    ADD CONSTRAINT task_invoice_table_task_type_id_fkey FOREIGN KEY (task_type_id) REFERENCES public.task_type_table(task_type_id);


--
-- Name: task_type_table task_type_table_associated_badge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.task_type_table
    ADD CONSTRAINT task_type_table_associated_badge_id_fkey FOREIGN KEY (associated_badge_id) REFERENCES public.badge_table(badge_id);


--
-- Name: user_achievement_table user_achievement_table_badge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_achievement_table
    ADD CONSTRAINT user_achievement_table_badge_id_fkey FOREIGN KEY (badge_id) REFERENCES public.badge_table(badge_id);


--
-- Name: user_achievement_table user_achievement_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_achievement_table
    ADD CONSTRAINT user_achievement_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: user_device_table user_device_table_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_device_table
    ADD CONSTRAINT user_device_table_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.user_table(user_id) ON DELETE CASCADE;


--
-- Name: user_table user_table_user_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_address_id_fkey FOREIGN KEY (user_address_id) REFERENCES public.address_table(address_id);


--
-- Name: user_table user_table_user_badge_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_badge_id_fkey FOREIGN KEY (user_badge_id) REFERENCES public.badge_table(badge_id);


--
-- Name: user_table user_table_user_rating_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_table
    ADD CONSTRAINT user_table_user_rating_id_fkey FOREIGN KEY (user_rating_id) REFERENCES public.rating_table(rating_id);


--
-- PostgreSQL database dump complete
--

\unrestrict J6JhnmN4gAuLlAOwhjw3hjPIPvpASxdWh6RWu4hc2Tab9cb5hduMybgg2ekdmYO

