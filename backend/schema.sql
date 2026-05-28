-- =============================================
-- community help system database schema
-- postgresql lowercase version
-- =============================================

-- =============================================
-- 1. location table
-- =============================================
create table location_table (
    location_id int generated always as identity primary key,
    location_center_point int,
    location_radius int,
    neighbourhood_id int,
    neighbourhood_name varchar(100)
);

-- =============================================
-- 2. rating table
-- =============================================
create table rating_table (
    rating_id int generated always as identity primary key,
    rating_review varchar(50) unique,
    total_xp_level int,
    current_group varchar(50)
);

-- =============================================
-- 3. badge table
-- =============================================
create table badge_table (
    badge_id varchar(50) primary key,
    badge_name varchar(100) not null,
    is_specialist boolean default false,
    current_xp int default 0,
    rating_review varchar(50),

    foreign key (rating_review)
        references rating_table(rating_review)
);

-- =============================================
-- 4. task type table
-- =============================================
create table task_type_table (
    task_type_id int generated always as identity primary key,
    type_description varchar(255) not null,
    associated_badge_id varchar(50),
    needs_specialist boolean default false,
    xp_worth int,

    foreign key (associated_badge_id)
        references badge_table(badge_id)
);

-- =============================================
-- 5. address table
-- =============================================
create table address_table (
    address_id int generated always as identity primary key,
    address_number int,
    address_street varchar(200),
    address_zip int,
    neighbourhood_id int,

    foreign key (neighbourhood_id)
        references location_table(location_id)
);

-- =============================================
-- 6. user table
-- =============================================
create table user_table (
    user_id int generated always as identity primary key,
    user_password varchar(255) not null,
    user_name varchar(100) not null,
    user_surname varchar(100) not null,
    user_email varchar(255) unique not null,
    user_phone_number varchar(20),
    user_gender varchar(10),
    user_dob date,

    user_address_id int,
    user_badge_id varchar(50),
    user_rating_review varchar(50),

    user_type varchar(20),

    foreign key (user_address_id)
        references address_table(address_id),

    foreign key (user_badge_id)
        references badge_table(badge_id),

    foreign key (user_rating_review)
        references rating_table(rating_review)
);

-- =============================================
-- 7. helper table
-- =============================================
create table helper_table (
    helper_id int generated always as identity primary key,
    user_id int not null,
    task_type_id int,
    badge_id varchar(50),

    foreign key (user_id)
        references user_table(user_id),

    foreign key (task_type_id)
        references task_type_table(task_type_id),

    foreign key (badge_id)
        references badge_table(badge_id)
);

-- =============================================
-- 8. dependent table
-- =============================================
create table dependent_table (
    dependent_id int generated always as identity primary key,
    user_id int not null,
    task_type_id int,

    foreign key (user_id)
        references user_table(user_id),

    foreign key (task_type_id)
        references task_type_table(task_type_id)
);

-- =============================================
-- 9. compatibility table
-- =============================================
create table compatibility_table (
    compatibility_id int generated always as identity primary key,
    compatibility_score int,
    compatibility_color varchar(20),

    dependent_id int,
    helper_id int,

    foreign key (dependent_id)
        references dependent_table(dependent_id),

    foreign key (helper_id)
        references helper_table(helper_id)
);

-- =============================================
-- 10. admin table
-- =============================================
create table admin_table (
    admin_id int generated always as identity primary key,

    admin_password varchar(255) not null,
    admin_name varchar(100) not null,
    admin_surname varchar(100) not null,
    admin_email varchar(255) unique not null,
    admin_phone_number varchar(20),

    admin_create_date date,
    admin_access_level int,

    user_id int,
    admin_address_id int,

    foreign key (user_id)
        references user_table(user_id),

    foreign key (admin_address_id)
        references address_table(address_id)
);

-- =============================================
-- 11. task invoice table
-- =============================================
create table task_invoice_table (
    task_id int generated always as identity primary key,

    helper_id int,
    dependent_id int,

    is_immediate boolean default false,

    location_id int,
    task_type_id int,

    needs_specialist boolean default false,

    signed_admin_id int,

    start_date date,
    end_date date,

    helper_badge_id varchar(50),

    dependent_rating_review varchar(50),
    helper_rating_review varchar(50),

    admin_review text,

    compatibility_id int,

    foreign key (helper_id)
        references helper_table(helper_id),

    foreign key (dependent_id)
        references dependent_table(dependent_id),

    foreign key (location_id)
        references location_table(location_id),

    foreign key (task_type_id)
        references task_type_table(task_type_id),

    foreign key (signed_admin_id)
        references admin_table(admin_id),

    foreign key (compatibility_id)
        references compatibility_table(compatibility_id),

    foreign key (helper_badge_id)
        references badge_table(badge_id),

    foreign key (dependent_rating_review)
        references rating_table(rating_review),

    foreign key (helper_rating_review)
        references rating_table(rating_review)
);

-- =============================================
-- 12. helper analytics table
-- =============================================
create table helper_analytics_table (
    helper_type_id varchar(50) primary key,

    user_id int,
    task_type_id int,
    compatibility_id int,
    location_id int,

    average_rating float,
    average_giving_rating float,

    foreign key (user_id)
        references user_table(user_id),

    foreign key (task_type_id)
        references task_type_table(task_type_id),

    foreign key (compatibility_id)
        references compatibility_table(compatibility_id),

    foreign key (location_id)
        references location_table(location_id)
);

-- =============================================
-- 13. dependent analytics table
-- =============================================
create table dependent_analytics_table (
    dependent_type_id varchar(50) primary key,

    user_id int,
    task_type_id int,

    total_tasks int,

    location_id int,

    average_rating float,
    average_giving_rating float,

    foreign key (user_id)
        references user_table(user_id),

    foreign key (task_type_id)
        references task_type_table(task_type_id),

    foreign key (location_id)
        references location_table(location_id)
);

-- =============================================
-- 14. analytics table
-- =============================================
create table analytics_table (
    analytics_id int generated always as identity primary key,

    task_id int,
    admin_id int,

    helper_type_id varchar(50),
    dependent_type_id varchar(50),

    foreign key (task_id)
        references task_invoice_table(task_id),

    foreign key (admin_id)
        references admin_table(admin_id),

    foreign key (helper_type_id)
        references helper_analytics_table(helper_type_id),

    foreign key (dependent_type_id)
        references dependent_analytics_table(dependent_type_id)
);

-- =============================================
-- 15. posts table
-- =============================================
create table posts_table (
    post_id int generated always as identity primary key,

    user_id int not null,

    post_content text not null,

    media_url text,

    created_at timestamp default current_timestamp,

    updated_at timestamp default current_timestamp,

    foreign key (user_id)
        references user_table(user_id)
        on delete cascade
);

-- =============================================
-- 16. comments table
-- =============================================
create table comments_table (
    comment_id int generated always as identity primary key,

    post_id int not null,
    user_id int not null,

    parent_comment_id int null,

    comment_content text not null,

    created_at timestamp default current_timestamp,

    foreign key (post_id)
        references posts_table(post_id)
        on delete cascade,

    foreign key (user_id)
        references user_table(user_id)
        on delete cascade,

    foreign key (parent_comment_id)
        references comments_table(comment_id)
        on delete cascade
);

-- =============================================
-- 17. likes table
-- =============================================
create table likes_table (
    like_id int generated always as identity primary key,

    user_id int not null,

    post_id int null,
    comment_id int null,

    created_at timestamp default current_timestamp,

    foreign key (user_id)
        references user_table(user_id)
        on delete cascade,

    foreign key (post_id)
        references posts_table(post_id)
        on delete cascade,

    foreign key (comment_id)
        references comments_table(comment_id)
        on delete cascade,

    constraint chk_one_target check (
        (post_id is not null and comment_id is null)
        or
        (post_id is null and comment_id is not null)
    ),

    constraint uq_no_duplicate_likes
        unique (user_id, post_id, comment_id)
);

-- =============================================
-- indexes
-- =============================================

create index idx_user_email
on user_table(user_email);

create index idx_task_dates
on task_invoice_table(start_date, end_date);

create index idx_location_neighbourhood
on location_table(neighbourhood_id);

create index idx_compatibility_score
on compatibility_table(compatibility_score);

create index idx_helper_badge
on helper_table(badge_id);

create index idx_dependent_task
on dependent_table(task_type_id);

create index idx_posts_user
on posts_table(user_id);

create index idx_comments_post
on comments_table(post_id);

create index idx_comments_parent
on comments_table(parent_comment_id);

create index idx_likes_post
on likes_table(post_id);

create index idx_likes_comment
on likes_table(comment_id);