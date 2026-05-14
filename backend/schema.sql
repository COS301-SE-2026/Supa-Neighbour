CREATE TABLE LocationTable (
    LocationID INT PRIMARY KEY,
    LocationCenterPoint INT,
    LocationRadius INT,
    NeighbourhoodID INT,
    NeighbourhoodName VARCHAR(100)
);

CREATE TABLE RatingTable (
    RatingReview VARCHAR(50) PRIMARY KEY,
    TotalXPLevel INT,
    CurrentGroup VARCHAR(50)
);

CREATE TABLE BadgeTable (
    BadgeID VARCHAR(50) PRIMARY KEY,
    BadgeName VARCHAR(100) NOT NULL,
    IsSpecialist BOOLEAN DEFAULT FALSE,
    CurrentXP INT DEFAULT 0,
    RatingID VARCHAR(50)
);

CREATE TABLE TaskTypeTable (
    TaskTypeID INT PRIMARY KEY,
    TypeDescription TEXT,
    AssociatedBadgeID VARCHAR(50),
    NeedsSpecialist BOOLEAN DEFAULT FALSE,
    XPWorth INT
);

CREATE TABLE AddressTable (
    AddressID INT PRIMARY KEY,
    AddressNumber INT,
    AddressStreet VARCHAR(200),
    AddressZip INT,
    NeighbourhoodID INT,
    ResidentID INT,

    FOREIGN KEY (NeighbourhoodID)
        REFERENCES LocationTable(LocationID)
);

CREATE TABLE UserTable (
    UserID INT PRIMARY KEY,
    UserPassword VARCHAR(255) NOT NULL,
    UserName VARCHAR(100) NOT NULL,
    UserSurname VARCHAR(100) NOT NULL,
    UserEmail VARCHAR(255) UNIQUE NOT NULL,
    UserPhoneNumber VARCHAR(20),
    UserGender VARCHAR(10),
    UserDOB DATE,
    UserAddressID INT,
    UserBadgeID VARCHAR(50),
    UserRatingID VARCHAR(50),
    UserTypeID VARCHAR(50),

    FOREIGN KEY (UserAddressID)
        REFERENCES AddressTable(AddressID),

    FOREIGN KEY (UserBadgeID)
        REFERENCES BadgeTable(BadgeID),

    FOREIGN KEY (UserRatingID)
        REFERENCES RatingTable(RatingReview)
);

CREATE TABLE CompatibilityTable (
    CompatibilityID INT PRIMARY KEY,
    CompatibilityScore INT,
    CompatibilityColor VARCHAR(20),
    DependentID INT,
    HelperID INT
);

CREATE TABLE DependentTable (
    DependentID INT PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    CompatibleID INT,

    FOREIGN KEY (UserID)
        REFERENCES UserTable(UserID),

    FOREIGN KEY (TaskTypeID)
        REFERENCES TaskTypeTable(TaskTypeID)
);

CREATE TABLE HelperTable (
    HelperID INT PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    BadgeID VARCHAR(50),
    CompatibleID INT,

    FOREIGN KEY (UserID)
        REFERENCES UserTable(UserID),

    FOREIGN KEY (TaskTypeID)
        REFERENCES TaskTypeTable(TaskTypeID),
    
    FOREIGN KEY (BadgeID)
        REFERENCES BadgeTable(BadgeID)
);

CREATE TABLE AdminTable (
    AdminID INT PRIMARY KEY,
    AdminPassword VARCHAR(255) NOT NULL,
    AdminName VARCHAR(100) NOT NULL,
    AdminSurname VARCHAR(100) NOT NULL,
    AdminEmail VARCHAR(255) UNIQUE NOT NULL,
    AdminPhoneNumber VARCHAR(20),
    AdminCreateDate DATE,
    AdminAccessLevel INT,
    UserID INT,
    AdminAddressID INT,

    FOREIGN KEY (UserID)
        REFERENCES UserTable(UserID),

    FOREIGN KEY (AdminAddressID)
        REFERENCES AddressTable(AddressID)
);

CREATE TABLE TaskInvoiceTable (
    TaskID INT PRIMARY KEY,
    HelperID INT,
    DependentID INT,
    IsImmediate BOOLEAN DEFAULT FALSE,
    LocationID INT,
    TaskTypeID INT,
    NeedsSpecialist BOOLEAN DEFAULT FALSE,
    SignedAdminID INT,
    StartDate DATE,
    EndDate DATE,
    HelperBadgeID VARCHAR(50),
    DependentRatingID VARCHAR(50),
    HelperRatingID VARCHAR(50),
    AdminReview TEXT,
    CompatibilityID INT,

    FOREIGN KEY (HelperID)
        REFERENCES HelperTable(HelperID),

    FOREIGN KEY (DependentID)
        REFERENCES DependentTable(DependentID),

    FOREIGN KEY (LocationID)
        REFERENCES LocationTable(LocationID),

    FOREIGN KEY (TaskTypeID)
        REFERENCES TaskTypeTable(TaskTypeID),

    FOREIGN KEY (SignedAdminID)
        REFERENCES AdminTable(AdminID),

    FOREIGN KEY (CompatibilityID)
        REFERENCES CompatibilityTable(CompatibilityID)
);

CREATE TABLE HelperAnalyticsTable (
    HelperTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    CompatibleID INT,
    LocationID INT,
    AverageRating FLOAT,
    AverageGivingRating FLOAT,

    FOREIGN KEY (UserID)
        REFERENCES UserTable(UserID),

    FOREIGN KEY (TaskTypeID)
        REFERENCES TaskTypeTable(TaskTypeID),

    FOREIGN KEY (CompatibleID)
        REFERENCES CompatibilityTable(CompatibilityID),

    FOREIGN KEY (LocationID)
        REFERENCES LocationTable(LocationID)
);

CREATE TABLE DependentAnalyticsTable (
    DependentTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    TotalTasks INT,
    LocationID INT,
    AverageRating FLOAT,
    AverageGivingRating FLOAT,

    FOREIGN KEY (UserID)
        REFERENCES UserTable(UserID),

    FOREIGN KEY (TaskTypeID)
        REFERENCES TaskTypeTable(TaskTypeID),

    FOREIGN KEY (LocationID)
        REFERENCES LocationTable(LocationID)
);


CREATE TABLE AnalyticsTable (
    AnalyticsID INT PRIMARY KEY,
    TaskID INT,
    AdminID INT,
    HelperTypeID VARCHAR(50),
    DependentTypeID VARCHAR(50),

    FOREIGN KEY (TaskID)
        REFERENCES TaskInvoiceTable(TaskID),
    FOREIGN KEY (AdminID)
        REFERENCES AdminTable(AdminID),
    FOREIGN KEY (HelperTypeID)
        REFERENCES HelperAnalyticsTable(HelperTypeID),
    FOREIGN KEY (DependentTypeID)
        REFERENCES DependentAnalyticsTable(DependentTypeID)
);



ALTER TABLE BadgeTable
ADD CONSTRAINT fk_badge_rating
FOREIGN KEY (RatingID)
REFERENCES RatingTable(RatingReview);

ALTER TABLE TaskTypeTable
ADD CONSTRAINT fk_tasktype_badge
FOREIGN KEY (AssociatedBadgeID)
REFERENCES BadgeTable(BadgeID);

ALTER TABLE CompatibilityTable
ADD CONSTRAINT fk_compatibility_helper
FOREIGN KEY (HelperID)
REFERENCES HelperTable(HelperID);

ALTER TABLE CompatibilityTable
ADD CONSTRAINT fk_compatibility_dependent
FOREIGN KEY (DependentID)
REFERENCES DependentTable(DependentID);

ALTER TABLE HelperTable
ADD CONSTRAINT fk_helper_compatibility
FOREIGN KEY (CompatibleID)
REFERENCES CompatibilityTable(CompatibilityID);

ALTER TABLE DependentTable
ADD CONSTRAINT fk_dependent_compatibility
FOREIGN KEY (CompatibleID)
REFERENCES CompatibilityTable(CompatibilityID);

ALTER TABLE AddressTable
ADD CONSTRAINT fk_address_resident
FOREIGN KEY (ResidentID)
REFERENCES UserTable(UserID);

-- Create indexes for better performance
CREATE INDEX idx_user_email ON UserTable(UserEmail);
CREATE INDEX idx_task_dates ON TaskInvoiceTable(StartDate, EndDate);
CREATE INDEX idx_location_neighbourhood ON LocationTable(NeighbourhoodID);
CREATE INDEX idx_compatibility_score ON CompatibilityTable(CompatibilityScore);
CREATE INDEX idx_helper_badge ON HelperTable(BadgeID);
CREATE INDEX idx_dependent_task ON DependentTable(TaskTypeID);

-- =============================================
-- MOCK DATA FOR COMMUNITY HELP SYSTEM
-- =============================================


-- =============================================
-- 1. LOCATION TABLE (Neighbourhoods)
-- =============================================
INSERT INTO LocationTable (LocationID, LocationCenterPoint, LocationRadius, NeighbourhoodID, NeighbourhoodName) VALUES
(1, 100, 5, 101, 'Downtown'),
(2, 200, 3, 102, 'Uptown'),
(3, 150, 4, 103, 'Riverside'),
(4, 300, 6, 104, 'Hillcrest'),
(5, 250, 5, 105, 'Lakeside'),
(6, 180, 4, 106, 'Maplewood'),
(7, 220, 3, 107, 'Oakridge'),
(8, 350, 7, 108, 'Sunset Valley');


-- =============================================
-- 2. ADDRESS TABLE
-- =============================================
INSERT INTO AddressTable (AddressID, AddressNumber, AddressStreet, AddressZip, NeighbourhoodID, ResidentID) VALUES
(1, 123, 'Main Street', 90210, 1, NULL),
(2, 456, 'Oak Avenue', 90211, 2, NULL),
(3, 789, 'Pine Road', 90212, 3, NULL),
(4, 321, 'Elm Street', 90213, 4, NULL),
(5, 654, 'Maple Drive', 90214, 5, NULL),
(6, 987, 'Cedar Lane', 90215, 6, NULL),
(7, 147, 'Birch Boulevard', 90216, 7, NULL),
(8, 258, 'Spruce Court', 90217, 8, NULL),
(9, 369, 'Willow Way', 90218, 1, NULL),
(10, 741, 'Ash Avenue', 90219, 2, NULL);


-- =============================================
-- 3. RATING TABLE
-- =============================================
INSERT INTO RatingTable (RatingReview, TotalXPLevel, CurrentGroup) VALUES
('Excellent', 1000, 'Platinum'),
('Very Good', 750, 'Gold'),
('Good', 500, 'Silver'),
('Satisfactory', 250, 'Bronze'),
('Needs Improvement', 100, 'Novice'),
('Poor', 50, 'Beginner');


-- =============================================
-- 4. BADGE TABLE
-- =============================================
INSERT INTO BadgeTable (BadgeID, BadgeName, IsSpecialist, CurrentXP, RatingID) VALUES
('BDG001', 'Elder Care Specialist', TRUE, 1200, 'Excellent'),
('BDG002', 'Child Care Expert', TRUE, 950, 'Very Good'),
('BDG003', 'Tech Support Helper', FALSE, 600, 'Good'),
('BDG004', 'Grocery Assistant', FALSE, 300, 'Satisfactory'),
('BDG005', 'Pet Care Pro', TRUE, 850, 'Very Good'),
('BDG006', 'Home Repair Master', TRUE, 1100, 'Excellent'),
('BDG007', 'Tutoring Star', FALSE, 450, 'Good'),
('BDG008', 'Transportation Helper', FALSE, 200, 'Satisfactory'),
('BDG009', 'Medical Escort', TRUE, 1300, 'Excellent'),
('BDG010', 'Friendly Visitor', FALSE, 150, 'Needs Improvement');

-- =============================================
-- 9. TASK TYPE TABLE
-- =============================================
INSERT INTO TaskTypeTable (TaskTypeID, TypeDescription, AssociatedBadgeID, NeedsSpecialist, XPWorth) VALUES
(1, 'Elder Care - Companionship and daily assistance', 'BDG001', TRUE, 150),
(2, 'Child Care - Babysitting and tutoring', 'BDG002', TRUE, 120),
(3, 'Tech Support - Computer and smartphone help', 'BDG003', FALSE, 80),
(4, 'Grocery Shopping - Delivery of groceries', 'BDG004', FALSE, 60),
(5, 'Pet Care - Walking and feeding pets', 'BDG005', TRUE, 100),
(6, 'Home Repair - Minor fixes and maintenance', 'BDG006', TRUE, 140),
(7, 'Tutoring - Academic help for students', 'BDG007', FALSE, 90),
(8, 'Transportation - Ride to appointments', 'BDG008', FALSE, 70),
(9, 'Medical Escort - Hospital visit assistance', 'BDG009', TRUE, 160),
(10, 'Friendly Visit - Social interaction', 'BDG010', FALSE, 50);

-- =============================================
-- 5. USER TABLE
-- =============================================
INSERT INTO UserTable (UserID, UserPassword, UserName, UserSurname, UserEmail, UserPhoneNumber, UserGender, UserDOB, UserAddressID, UserBadgeID, UserRatingID, UserTypeID) VALUES
(101, 'pass123', 'John', 'Smith', 'john.smith@email.com', 5550101, 'Male', '1985-03-15', 1, 'BDG001', 'Excellent', 'H'),
(102, 'pass456', 'Emma', 'Johnson', 'emma.j@email.com', 5550102, 'Female', '1990-07-22', 2, 'BDG002', 'Very Good', 'H'),
(103, 'pass789', 'Michael', 'Brown', 'michael.b@email.com', 5550103, 'Male', '1978-11-30', 3, 'BDG003', 'Good', 'D'),
(104, 'pass101', 'Sarah', 'Davis', 'sarah.d@email.com', 5550104, 'Female', '1995-02-18', 4, 'BDG004', 'Satisfactory', 'B'),
(105, 'pass112', 'David', 'Wilson', 'david.w@email.com', 5550105, 'Male', '1982-09-25', 5, 'BDG005', 'Very Good', 'H'),
(106, 'pass131', 'Lisa', 'Martinez', 'lisa.m@email.com', 5550106, 'Female', '1988-06-10', 6, 'BDG006', 'Excellent', 'H'),
(107, 'pass415', 'James', 'Anderson', 'james.a@email.com', 5550107, 'Male', '1975-12-03', 7, 'BDG007', 'Good', 'D'),
(108, 'pass161', 'Maria', 'Taylor', 'maria.t@email.com', 5550108, 'Female', '1992-08-27', 8, 'BDG008', 'Satisfactory', 'H'),
(109, 'pass718', 'Robert', 'Thomas', 'robert.t@email.com', 5550109, 'Male', '1980-04-19', 9, 'BDG009', 'Excellent', 'D'),
(110, 'pass192', 'Patricia', 'Jackson', 'patricia.j@email.com', 5550110, 'Female', '1987-10-05', 10, 'BDG010', 'Needs Improvement', 'B');


-- =============================================
-- 6. COMPATIBILITY TABLE
-- =============================================
INSERT INTO CompatibilityTable (CompatibilityID, CompatibilityScore, CompatibilityColor, DependentID, HelperID) VALUES
(1, 95, 'Green', NULL, NULL),
(2, 88, 'Green', NULL, NULL),
(3, 72, 'Yellow', NULL, NULL),
(4, 65, 'Yellow', NULL, NULL),
(5, 45, 'Red', NULL, NULL),
(6, 92, 'Green', NULL, NULL),
(7, 78, 'Yellow', NULL, NULL),
(8, 85, 'Green', NULL, NULL),
(9, 58, 'Red', NULL, NULL),
(10, 90, 'Green', NULL, NULL);


-- =============================================
-- 7. HELPER TABLE
-- =============================================
INSERT INTO HelperTable (HelperID, UserID, BadgeID, TaskTypeID, CompatibleID) VALUES
(1, 101, 'BDG001', 1, 1),
(2, 102, 'BDG002', 2, 2),
(3, 105, 'BDG005', 5, 6),
(4, 106, 'BDG006', 6, 7),
(5, 108, 'BDG008', 8, 8),
(6, 110, 'BDG010', 10, 10);


-- =============================================
-- 8. DEPENDENT TABLE
-- =============================================
INSERT INTO DependentTable (DependentID, UserID, TaskTypeID, CompatibleID) VALUES
(1, 103, 3, 3),
(2, 104, 4, 4),
(3, 107, 7, 5),
(4, 109, 9, 9);


-- Update CompatibilityTable with foreign keys
UPDATE CompatibilityTable SET DependentID = 1, HelperID = 1 WHERE CompatibilityID = 1;
UPDATE CompatibilityTable SET DependentID = 2, HelperID = 2 WHERE CompatibilityID = 2;
UPDATE CompatibilityTable SET DependentID = 3, HelperID = 3 WHERE CompatibilityID = 3;
UPDATE CompatibilityTable SET DependentID = 4, HelperID = 4 WHERE CompatibilityID = 4;
UPDATE CompatibilityTable SET DependentID = 1, HelperID = 5 WHERE CompatibilityID = 5;
UPDATE CompatibilityTable SET DependentID = 2, HelperID = 6 WHERE CompatibilityID = 6;
UPDATE CompatibilityTable SET DependentID = 3, HelperID = 1 WHERE CompatibilityID = 7;
UPDATE CompatibilityTable SET DependentID = 4, HelperID = 2 WHERE CompatibilityID = 8;
UPDATE CompatibilityTable SET DependentID = 1, HelperID = 3 WHERE CompatibilityID = 9;
UPDATE CompatibilityTable SET DependentID = 2, HelperID = 4 WHERE CompatibilityID = 10;


-- =============================================
-- 10. ADMIN TABLE
-- =============================================
INSERT INTO AdminTable (AdminID, AdminPassword, AdminName, AdminSurname, AdminEmail, AdminPhoneNumber, AdminCreateDate, AdminAccessLevel, UserID, AdminAddressID) VALUES
(1, 'admin123', 'Alice', 'Cooper', 'alice.cooper@help.com', 5550201, '2023-01-15', 5, NULL, 1),
(2, 'admin456', 'Bob', 'Miller', 'bob.miller@help.com', 5550202, '2023-03-20', 4, NULL, 2),
(3, 'admin789', 'Carol', 'Wilson', 'carol.wilson@help.com', 5550203, '2023-06-10', 5, NULL, 3),
(4, 'admin101', 'David', 'Garcia', 'david.garcia@help.com', 5550204, '2024-01-05', 3, NULL, 4),
(5, 'admin112', 'Eva', 'Rodriguez', 'eva.rodriguez@help.com', 5550205, '2024-02-18', 4, NULL, 5);


-- =============================================
-- 11. TASK INVOICE TABLE
-- =============================================
INSERT INTO TaskInvoiceTable (TaskID, HelperID, DependentID, IsImmediate, LocationID, TaskTypeID, NeedsSpecialist, SignedAdminID, StartDate, EndDate, HelperBadgeID, DependentRatingID, HelperRatingID, AdminReview, CompatibilityID) VALUES
(1001, 1, 1, FALSE, 1, 1, TRUE, 1, '2024-01-10', '2024-01-10', 'BDG001', 'Excellent', 'Very Good', 'Task completed successfully. Great communication!', 1),
(1002, 2, 2, TRUE, 2, 2, TRUE, 1, '2024-01-15', '2024-01-15', 'BDG002', 'Very Good', 'Good', 'Prompt and professional service.', 2),
(1003, 3, 3, FALSE, 3, 5, TRUE, 2, '2024-01-20', '2024-01-25', 'BDG005', 'Good', 'Satisfactory', 'Pet care was adequate.', 6),
(1004, 4, 4, FALSE, 4, 6, TRUE, 2, '2024-02-01', '2024-02-02', 'BDG006', 'Excellent', 'Excellent', 'Fixed multiple issues efficiently.', 7),
(1005, 5, 1, TRUE, 5, 8, FALSE, 3, '2024-02-05', '2024-02-05', 'BDG008', 'Satisfactory', 'Good', 'Transportation provided on time.', 8),
(1006, 1, 2, FALSE, 6, 3, FALSE, 3, '2024-02-10', '2024-02-10', 'BDG001', 'Very Good', 'Very Good', 'Tech help was excellent.', 3),
(1007, 2, 3, FALSE, 7, 4, FALSE, 4, '2024-02-15', '2024-02-15', 'BDG002', 'Good', 'Good', 'Shopping delivered accurately.', 4),
(1008, 6, 4, TRUE, 8, 10, FALSE, 4, '2024-02-20', '2024-02-20', 'BDG010', 'Needs Improvement', 'Satisfactory', 'Social visit was pleasant.', 10),
(1009, 3, 1, FALSE, 1, 7, FALSE, 5, '2024-03-01', '2024-03-15', 'BDG005', 'Excellent', 'Excellent', 'Tutoring improved grades significantly.', 9),
(1010, 4, 2, FALSE, 2, 9, TRUE, 5, '2024-03-05', '2024-03-05', 'BDG006', 'Very Good', 'Very Good', 'Medical escort was professional.', 5),
(1011, 5, 3, TRUE, 3, 1, TRUE, 1, '2024-03-10', '2024-03-10', 'BDG008', 'Good', 'Good', 'Elder care provided compassionately.', 1),
(1012, 6, 4, FALSE, 4, 2, TRUE, 1, '2024-03-12', '2024-03-12', 'BDG010', 'Satisfactory', 'Satisfactory', 'Child care was acceptable.', 2),
(1013, 1, 3, FALSE, 5, 5, TRUE, 2, '2024-03-15', '2024-03-20', 'BDG001', 'Very Good', 'Very Good', 'Excellent pet care services.', 6),
(1014, 2, 4, FALSE, 6, 6, TRUE, 2, '2024-03-18', '2024-03-19', 'BDG002', 'Excellent', 'Excellent', 'Home repairs done perfectly.', 7),
(1015, 3, 2, TRUE, 7, 8, FALSE, 3, '2024-03-22', '2024-03-22', 'BDG005', 'Good', 'Very Good', 'Quick transportation service.', 8);

-- =============================================
-- 13. HELPER ANALYTICS TABLE
-- =============================================
INSERT INTO HelperAnalyticsTable (HelperTypeID, UserID, TaskTypeID, CompatibleID, LocationID, AverageRating, AverageGivingRating) VALUES
('H001', 101, 1, 1, 1, 4.8, 4.5),
('H002', 102, 2, 2, 2, 4.6, 4.3),
('H003', 105, 5, 6, 3, 4.2, 4.0),
('H004', 106, 6, 7, 4, 4.9, 4.7),
('H005', 108, 8, 8, 5, 4.0, 4.1),
('H006', 110, 10, 10, 6, 3.5, 3.8);


-- =============================================
-- 14. DEPENDENT ANALYTICS TABLE
-- =============================================
INSERT INTO DependentAnalyticsTable (DependentTypeID, UserID, TaskTypeID, TotalTasks, LocationID, AverageRating, AverageGivingRating) VALUES
('D001', 103, 3, 5, 3, 4.5, 4.3),
('D002', 104, 4, 4, 4, 4.3, 4.2),
('D003', 107, 7, 3, 7, 4.0, 3.9),
('D004', 109, 9, 3, 8, 4.2, 4.4);


-- =============================================
-- 12. ANALYTICS TABLE
-- =============================================
INSERT INTO AnalyticsTable (AnalyticsID, TaskID, AdminID, HelperTypeID, DependentTypeID) VALUES
(1, 1001, 1, 'H001', 'D001'),
(2, 1002, 1, 'H002', 'D002'),
(3, 1003, 2, 'H003', 'D003'),
(4, 1004, 2, 'H004', 'D004'),
(5, 1005, 3, 'H005', 'D001'),
(6, 1006, 3, 'H001', 'D002'),
(7, 1007, 4, 'H002', 'D003'),
(8, 1008, 4, 'H006', 'D004'),
(9, 1009, 5, 'H003', 'D001'),
(10, 1010, 5, 'H004', 'D002'),
(11, 1011, 1, 'H005', 'D003'),
(12, 1012, 1, 'H006', 'D004'),
(13, 1013, 2, 'H001', 'D003'),
(14, 1014, 2, 'H002', 'D004'),
(15, 1015, 3, 'H003', 'D002');

-- =============================================
-- UPDATE UserTable with foreign keys after data insertion
-- =============================================
UPDATE UserTable SET UserAddressID = 1, UserBadgeID = 'BDG001', UserRatingID = 'Excellent', UserTypeID = 'Helper' WHERE UserID = 101;
UPDATE UserTable SET UserAddressID = 2, UserBadgeID = 'BDG002', UserRatingID = 'Very Good', UserTypeID = 'Helper' WHERE UserID = 102;
UPDATE UserTable SET UserAddressID = 3, UserBadgeID = 'BDG003', UserRatingID = 'Good', UserTypeID = 'Dependent' WHERE UserID = 103;
UPDATE UserTable SET UserAddressID = 4, UserBadgeID = 'BDG004', UserRatingID = 'Satisfactory', UserTypeID = 'Both' WHERE UserID = 104;
UPDATE UserTable SET UserAddressID = 5, UserBadgeID = 'BDG005', UserRatingID = 'Very Good', UserTypeID = 'Helper' WHERE UserID = 105;
UPDATE UserTable SET UserAddressID = 6, UserBadgeID = 'BDG006', UserRatingID = 'Excellent', UserTypeID = 'Helper' WHERE UserID = 106;
UPDATE UserTable SET UserAddressID = 7, UserBadgeID = 'BDG007', UserRatingID = 'Good', UserTypeID = 'Dependent' WHERE UserID = 107;
UPDATE UserTable SET UserAddressID = 8, UserBadgeID = 'BDG008', UserRatingID = 'Satisfactory', UserTypeID = 'Helper' WHERE UserID = 108;
UPDATE UserTable SET UserAddressID = 9, UserBadgeID = 'BDG009', UserRatingID = 'Excellent', UserTypeID = 'Dependent' WHERE UserID = 109;
UPDATE UserTable SET UserAddressID = 10, UserBadgeID = 'BDG010', UserRatingID = 'Needs Improvement', UserTypeID = 'Both' WHERE UserID = 110;


-- Update AdminTable with UserID foreign keys
UPDATE AdminTable SET UserID = 101 WHERE AdminID = 1;
UPDATE AdminTable SET UserID = 102 WHERE AdminID = 2;
UPDATE AdminTable SET UserID = 105 WHERE AdminID = 3;
UPDATE AdminTable SET UserID = 106 WHERE AdminID = 4;
UPDATE AdminTable SET UserID = 108 WHERE AdminID = 5;


-- Update AddressTable with ResidentID
UPDATE AddressTable SET ResidentID = 101 WHERE AddressID = 1;
UPDATE AddressTable SET ResidentID = 102 WHERE AddressID = 2;
UPDATE AddressTable SET ResidentID = 103 WHERE AddressID = 3;
UPDATE AddressTable SET ResidentID = 104 WHERE AddressID = 4;
UPDATE AddressTable SET ResidentID = 105 WHERE AddressID = 5;
UPDATE AddressTable SET ResidentID = 106 WHERE AddressID = 6;
UPDATE AddressTable SET ResidentID = 107 WHERE AddressID = 7;
UPDATE AddressTable SET ResidentID = 108 WHERE AddressID = 8;
UPDATE AddressTable SET ResidentID = 109 WHERE AddressID = 9;
UPDATE AddressTable SET ResidentID = 110 WHERE AddressID = 10;
