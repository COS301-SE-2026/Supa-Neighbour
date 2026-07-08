\set ON_ERROR_STOP on
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
    badge_id int generated always as identity primary key ,
    badge_name varchar(100) not null,
    badge_description text,
    is_specialist boolean default false,
    current_xp int default 0,
    rating_id int,

    foreign key (rating_id)
        references rating_table(rating_id)
);

-- =============================================
-- 4. task type table
-- =============================================
create table task_type_table (
    task_type_id int generated always as identity primary key,
    type_description varchar(255) not null,
    associated_badge_id int,
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
    user_firebase_uid VARCHAR(128) UNIQUE NOT NULL,
    user_email_verified boolean default false,
    user_phone_verified boolean default false,
    user_name varchar(100) not null,
    user_surname varchar(100) not null,
    user_username varchar(100) unique not null,
    user_email varchar(255) unique not null,
    user_phone_number varchar(20),
    user_gender varchar(10),
    user_dob date,

    user_address_id int,
    user_badge_id int,
    user_rating_id int,

    user_type varchar(20),

    foreign key (user_address_id)
        references address_table(address_id),

    foreign key (user_badge_id)
        references badge_table(badge_id),

    foreign key (user_rating_id)
        references rating_table(rating_id)
);

create table availability_table (
    availability_id int generated always as identity primary key,
    user_id int not null,
    day_of_week varchar(10), 
    time_window varchar(20),
    is_active boolean default true,
    foreign key (user_id) 
        references user_table(user_id)
);

create table helper_table (
    helper_id int generated always as identity primary key,
    user_id int not null,
    task_type_id int,
    badge_id int,

    foreign key (user_id) 
        references user_table(user_id),

    foreign key (task_type_id) 
        references task_type_table(task_type_id),

    foreign key (badge_id) 
        references badge_table(badge_id)
);

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
    compatibility_colour varchar(20),

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

    helper_badge_id int,

    dependent_rating_review varchar(50),
    helper_rating_review varchar(50),
    admin_review varchar(300),

    compatibility_id int,

    review_snippet varchar(300),
    status varchar(20) default 'open'  
        check (status in ('open', 'assigned', 'in_progress','pending_approval', 'completed', 'cancelled')),

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
    helper_analytics_id varchar(50) primary key,

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
    dependent_analytics_id varchar(50) primary key,

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
        references helper_analytics_table(helper_analytics_id),

    foreign key (dependent_type_id)
        references dependent_analytics_table(dependent_analytics_id)
);

create table helper_skill_table (
    helper_skill_id int generated always as identity primary key,
    helper_id int not null,
    task_type_id int not null,

    foreign key (helper_id) 
        references helper_table(helper_id),

    foreign key (task_type_id) 
        references task_type_table(task_type_id),
        constraint uq_helper_skill unique (helper_id, task_type_id)
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
    updated_at timestamp null,
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

    updated_at timestamp null,

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
-- 18. chat table
-- =============================================
create table chat_table (
    chat_id int generated always as identity primary key,
    task_id int not null,
    dependent_user_id int not null,
    helper_user_id int not null,
    created_at timestamp default current_timestamp,

    foreign key (task_id)
        references task_invoice_table(task_id),
    foreign key (dependent_user_id)
        references user_table(user_id),
    foreign key (helper_user_id)
        references user_table(user_id),

    constraint uq_chat_per_task unique (task_id)
);

-- =============================================
-- 19. message table
-- =============================================
create table message_table (
    message_id int generated always as identity primary key,
    chat_id int not null,
    sender_id int not null,
    content text not null,
    message_type varchar(10) default 'text',
    is_read boolean default false,
    sent_at timestamp default current_timestamp,

    foreign key (chat_id)
        references chat_table(chat_id)
        on delete cascade,
    foreign key (sender_id)
        references user_table(user_id)
);

create table user_achievement_table (
    user_achievement_id int generated always as identity primary key,
    user_id             int not null,
    badge_id            int not null,
    awarded_on          date,
    progress_current    int default 0,
    progress_target     int,
    foreign key (user_id)  references user_table(user_id),
    foreign key (badge_id) references badge_table(badge_id),
    constraint uq_user_badge unique (user_id, badge_id)
);

-- =============================================
-- 19. task invitation table
-- =============================================
create table task_invitation_table (
    invitation_id int generated always as identity primary key,
    task_id       int not null,
    helper_id     int not null,
    status        varchar(20) default 'Invited',
        check (status in ('Invited', 'Accepted', 'Declined', 'Rejected')),
    invited_at    timestamp default current_timestamp,
    foreign key (task_id)   references task_invoice_table(task_id),
    foreign key (helper_id) references helper_table(helper_id),
    constraint uq_invite_per_helper unique (task_id, helper_id)
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

create index idx_messages_chat
on message_table(chat_id);

create index idx_messages_sent_at
on message_table(sent_at);

-- =============================================
-- MOCK DATA
-- COMMUNITY HELP SYSTEM
-- =============================================

-- =============================================
-- 1. location table
-- =============================================
insert into location_table
(location_center_point, location_radius, neighbourhood_id, neighbourhood_name)
values
(100, 15, 1, 'Riverside'),
(200, 20, 2, 'Hillcrest'),
(300, 25, 3, 'Downtown'),
(400, 18, 4, 'Westlake'),
(500, 30, 5, 'Greenfield');

-- =============================================
-- 2. rating table
-- =============================================
insert into rating_table
(rating_review, total_xp_level, current_group)
values
('Excellent', 5000, 'Gold'),
('Very Good', 3500, 'Silver'),
('Good', 2000, 'Bronze'),
('Average', 1000, 'Starter'),
('Outstanding', 7000, 'Platinum');

-- =============================================
-- 3. badge table
-- =============================================
insert into badge_table 
( badge_name, badge_description, is_specialist, current_xp, rating_id)
values
('Medical Specialist','has some form of medical training', true, 4500, 4),
('Pet Care Helper', 'assists with pet care needs', false, 2000, 3),
('Tech Assistant', 'provides technology support', false, 1500, 2),
('Transport Volunteer', 'assists with transportation needs', false, 3000, 3),
('Home Repair Specialist', 'specializes in home repair tasks', true, 6000, 5);

-- =============================================
-- 4. task type table
-- =============================================
insert into task_type_table
(type_description, associated_badge_id, needs_specialist, xp_worth)
values
('Medical Assistance', 1, true, 500),
('Pet Care', 2, false, 200),
('Technology Support', 3, false, 150),
('Transportation Support', 4, false, 250),
('Home Repair', 5, true, 600);

-- =============================================
-- 5. address table
-- =============================================
insert into address_table
(address_number, address_street, address_zip, neighbourhood_id)
values
(12, 'Oak Street', 2001, 1),
(45, 'Maple Avenue', 2002, 2),
(88, 'River Road', 2003, 3),
(101, 'Hill Street', 2004, 4),
(55, 'Green Lane', 2005, 5),
(73, 'Sunset Boulevard', 2006, 1),
(9, 'Lake View Drive', 2007, 2),
(64, 'Main Street', 2008, 3),
(120, 'Forest Way', 2009, 4),
(37, 'Palm Crescent', 2010, 5);

-- =============================================
-- 6. user table
-- =============================================
insert into user_table
(
user_firebase_uid,
user_email_verified,
user_phone_verified,
user_name,
user_surname,
user_username,
user_email,
user_phone_number,
user_gender,
user_dob,
user_address_id,
user_badge_id,
user_rating_id,
user_type
)
values
('PDFxzgQ9KwNrAvMgWzfEvhkhJoj1', true, true, 'John', 'Smith', 'johnsmith', 'john.example.298@gmail.com', '5550101', 'Male', '1990-01-10', 1,2,1,'Admin' ),

('1ZkC0pAHZ9UBVKRtbQtrUXuYjKp1', true, true, 'Sarah', 'Johnson', 'sarahj', 'sarah.example.298@gmail.com', '5550102', 'Female', '1988-03-15', 2,1,1, 'User' ),

('WlMsgj9xKRNnLKhBCACGdZsMXVJ3', true, true, 'Michael', 'Brown', 'michaelb', 'michael.example.298@gmail.com', '5550103', 'Male', '1995-07-21', 3,1,1, 'User' ),

('mdW5NZdYeyernK7Dh5J49EQqdaN2', true, true, 'Emily', 'Davis', 'emilyd', 'emily.example.298@gmail.com', '5550104', 'Female', '1992-11-30', 4, 1, 1, 'User'),

('o1Efo9cDkOVqeTBnrlkzD2TUMs43', true, true, 'David', 'Wilson', 'davidw', 'david.example.298@gmail.com', '5550105', 'Male', '1985-05-18', 5, 1, 1, 'User'),

('bUv58sF4iagkhjUWj93GHRN3tvH3', true, true, 'Olivia', 'Taylor', 'oliviat', 'olivia.example.298@gmail.com', '5550106', 'Female', '2000-04-02', 6, 2, 2, 'Admin'),

('yA9Jcgl0P0cMgYrpDp6JonWIRgF2', true, true, 'James', 'Anderson', 'jamesa', 'james.example.298@gmail.com', '5550107', 'Male', '1975-08-14', 7, 1, 1, 'User'),

( '0gRA3wfNjNOGHaJZ84j6sybUvPs2', true, true, 'Sophia', 'Thomas', 'sophiat', 'sophia.example.298@gmail.com', '5550108', 'Female','1998-09-22', 8, 3, 1, 'Admin'),

( 'hJUI8ixYgvY0YqYVFnjPlm0CxIa2', true, true, 'Daniel', 'Jackson', 'danielj', 'daniel.example.298@gmail.com', '5550109', 'Male', '1982-12-11', 9, 5, 2, 'Admin'),

( 'wwEa5s6GFUWzXGujlt67xHNDti73', true, true, 'Emma', 'White', 'emmaw', 'emma.example.298@gmail.com', '5550110', 'Female', '1996-06-25', 10, 1, 1, 'User'),

( 'tV2skp5AgyQQCxkKNrA5FP4P5Pf2', true,true,'Matthew', 'Harris', 'matthewh', 'matthew.example.298@gmail.com', '5550111', 'Male', '1991-03-12', 1, 4, 3, 'User'),

( 'b797OnSbqFe9V2KTiJbKhjEs6ji1',true, true, 'Isabella', 'Martin', 'isabellam', 'isabella.example.298@gmail.com', '5550112', 'Female', '1987-07-19', 2, 2, 2, 'User'),

( 'vRe60bMKSvVRXvCy1EJpRhh0kOy2',true,true, 'William', 'Thompson', 'williamt', 'william.example.298@gmail.com', '5550113', 'Male', '1993-09-28', 3, 3, 1, 'User');


-- =============================================
-- 6. availability table

-- =============================================
insert into availability_table
(user_id, day_of_week, time_window, is_active)
values
(1, 'Monday',    'Morning',  true),
(2, 'Tuesday',   'Evening',  true),
(3, 'Wednesday', 'All day',  true),
(4, 'Thursday',  'Morning',  true),
(5, 'Friday',    'Evening',  true),
(6, 'Saturday',  'All day',  false),
(7, 'Sunday',    'Morning',  true),
(10, 'Monday',   'Evening',  true),
(11, 'Wednesday','Morning',  true),
(12, 'Friday',   'All day',  true),
(13, 'Saturday', 'Morning',  true);

-- =============================================
-- 7. helper table
-- =============================================
insert into helper_table
(user_id, task_type_id, badge_id)
values
(2, 2, 4),  
(3, 3, 2),   
(4, 4, 3),   
(5, 5, 5),   
(7, 1, 1),   
(10, 2, 4),  
(11, 3, 2),  
(12, 4, 3), 
(13, 5, 5);  

-- =============================================
-- 8. dependent table
-- =============================================
insert into dependent_table
(user_id, task_type_id)
values
(2,  1),   -- Sarah    → Medical Assistance
(3,  4),   -- Michael  → Transportation
(4,  3),   -- Emily    → Tech Support
(5,  2),   -- David    → Pet Care
(7,  5),   -- James    → Home Repair
(10, 1),   -- Emma     → Medical Assistance
(11, 4),   -- Matthew  → Transportation
(12, 3),   -- Isabella → Tech Support
(13, 2);   -- William  → Pet Care

-- =============================================
-- 9. compatibility table
-- =============================================
insert into compatibility_table
(compatibility_score, compatibility_colour, dependent_id, helper_id)
values
(95, 'Green', 1, 1),
(80, 'Yellow', 2, 4),
(88, 'Green', 3, 3),
(70, 'Orange', 4, 2),
(99, 'Green', 5, 5);

-- =============================================
-- 10. admin table
-- =============================================
insert into admin_table
(
admin_password,
admin_name,
admin_surname,
admin_email,
admin_phone_number,
admin_create_date,
admin_access_level,
user_id,
admin_address_id
)
values
('admin123', 'Alice', 'Miller', 'alice.admin@example.com', '5550201', '2024-01-01', 5, 1, 1),
('admin123', 'Robert', 'Moore', 'robert.admin@example.com', '5550202', '2024-02-15', 4, 2, 2);

-- =============================================
-- 11. task invoice table
-- =============================================
insert into task_invoice_table
(
    helper_id, 
    dependent_id, 
    is_immediate, 
    location_id, 
    task_type_id,
    needs_specialist,
    signed_admin_id,
    start_date, 
    end_date,
    helper_badge_id, 
    dependent_rating_review, 
    helper_rating_review,
    admin_review, 
    compatibility_id, 
    status
)
values
(1, 1, true,  1, 1, true,  1, '2026-05-01', '2026-05-01', 3, null, 'Outstanding', 'Excellent medical assistance provided.', 1, 'completed'),
(2, 2, false, 2, 4, false, 2, '2026-05-02', '2026-05-03', 2, null, 'Very Good',   'Reliable transport support.',            2, 'completed'),
(3, 3, false, 3, 3, false, 1, '2026-05-04', '2026-05-04', 4, null, 'Very Good',   'Resolved device setup issues quickly.',   3, 'completed');

-- =============================================
-- 12. helper analytics table
-- =============================================
insert into helper_analytics_table
(
helper_analytics_id,
user_id,
task_type_id,
compatibility_id,
location_id,
average_rating,
average_giving_rating
)
values
('HELPER_SARAH',    2,  2, 1, 2, 4.6, 4.5),   
('HELPER_MICHAEL',  3,  3, 3, 3, 4.7, 4.6),   
('HELPER_EMILY',    4,  4, 2, 4, 4.5, 4.4),   
('HELPER_DAVID',    5,  5, 5, 5, 4.8, 4.7),   
('HELPER_JAMES',    7,  1, 1, 2, 4.9, 4.8),   
('HELPER_EMMA',     10, 2, 1, 5, 4.3, 4.2),  
('HELPER_MATTHEW',  11, 3, 2, 1, 4.1, 4.0),   
('HELPER_ISABELLA', 12, 4, 3, 2, 4.4, 4.3),   
('HELPER_WILLIAM',  13, 5, 4, 3, 4.2, 4.1);  

-- =============================================
-- 13. dependent analytics table
-- =============================================
insert into dependent_analytics_table
(dependent_analytics_id, 
user_id, task_type_id, 
total_tasks, 
location_id, 
average_rating, 
average_giving_rating)
values
('DEPENDENT_MED',   2,  1, 12, 1, 4.6, 4.7),  
('DEPENDENT_TRANS', 4,  4,  5, 4, 4.2, 4.0), 
('DEPENDENT_TECH',  3,  3,  8, 3, 4.8, 4.9); 

-- =============================================
-- 14. analytics table
-- =============================================
insert into analytics_table
(task_id, admin_id, helper_type_id, dependent_type_id)
values
(1, 1, 'HELPER_SARAH',   'DEPENDENT_MED'),
(2, 2, 'HELPER_EMILY',   'DEPENDENT_TRANS'),
(3, 1, 'HELPER_MICHAEL', 'DEPENDENT_TECH');

-- =============================================
-- 15. posts table
-- =============================================
insert into posts_table
(user_id, post_content, media_url)
values
(1, 'Just completed an elderly care session today!', 'https://example.com/eldercare.jpg'),

(2, 'Pet care services available this weekend.', 'https://example.com/petcare.jpg'),

(3, 'Offering free technology support for seniors.', null),

(4, 'Transportation assistance available tomorrow morning.', null),

(5, 'Finished repairing a leaking sink today.', 'https://example.com/repair.jpg');

-- =============================================
-- 16. comments table
-- =============================================
insert into comments_table
(post_id, user_id, parent_comment_id, comment_content)
values
(1, 6, null, 'Thank you for helping the community!'),

(1, 1, 1, 'Happy to help anytime.'),

(2, 7, null, 'Are you available on Saturday?'),

(2, 2, 3, 'Yes, Saturday afternoon works.'),

(3, 8, null, 'I need help setting up my phone.'),

(4, 9, null, 'Transportation support is greatly appreciated.'),

(5, 10, null, 'Great repair work!');

-- =============================================
-- 17. likes table
-- =============================================
insert into likes_table
(user_id, post_id, comment_id)
values
(6, 1, null),
(7, 1, null),
(8, 2, null),
(9, 3, null),
(10, 4, null),

(1, null, 1),
(2, null, 3),
(3, null, 5),
(4, null, 6),
(5, null, 7);

-- =============================================
-- 18. chat table mock data
-- =============================================
insert into chat_table
(task_id, dependent_user_id, helper_user_id)
values
(1, 6, 1),
(2, 7, 4);

-- =============================================
-- 19. message table mock data
-- =============================================
insert into message_table
(chat_id, sender_id, content, message_type, is_read)
values
-- 1: user 6 and 1 about task 1
(1, 6, 'Hi John, thanks for helping with my medical assistance today.', 'text', true),
(1, 1, 'Of course! I will be there at 9am. Do you need anything specific?', 'text', true),
(1, 6, 'Just bring your kit, everything else is ready here.', 'text', true),
(1, 1, 'Perfect. On my way now.', 'text', true),
(1, 6, 'Great, the door is open. See you soon!', 'text', true),
(1, 1, 'Just arrived outside.', 'text', false),

-- 2: user 7 and 4 about task 2
(2, 7, 'Hi Emily, I need transport to the clinic tomorrow morning.', 'text', true),
(2, 4, 'Sure James, what time do you need to be there?', 'text', true),
(2, 7, 'Around 8am please.', 'text', true),
(2, 4, 'No problem, I will pick you up at 7:45am.', 'text', true),
(2, 7, 'That is perfect, thank you so much!', 'text', false),
(2, 4, 'See you tomorrow morning.', 'text', false);

-- =============================================
-- 19. message table mock data
-- =============================================
insert into user_achievement_table 
(user_id, badge_id, awarded_on, progress_current, progress_target)
values
(2, 2, '2026-05-03', 5,  5),   
(2, 1, null,         2, 10),   
(2, 3, null,         1,  5),   
(7, 1, '2026-05-01', 10, 10),
(7, 2, null,         3,  5);  

-- =============================================
-- 19. task_invitation_table mock data
-- =============================================
INSERT INTO task_invitation_table (task_id, helper_id, status)
VALUES
(1, 1, 'Invited'),   
(1, 2, 'Rejected'), 
(2, 4, 'Accepted'),  
(2, 5, 'Declined'), 
(3, 3, 'Invited');

-- =============================================
-- 19. helper skill mock data
-- =============================================
insert into helper_skill_table (helper_id, task_type_id)
values
(1, 1), (1, 2),        
(2, 3), (2, 4),        
(3, 3), (3, 5),        
(4, 4), (4, 2),        
(5, 1), (5, 5),       
(6, 2), (6, 3),        
(7, 4), (7, 1),       
(8, 5), (8, 2),        --
(9, 3), (9, 4);  