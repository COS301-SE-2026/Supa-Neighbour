-- Create database
CREATE DATABASE CommunityHelpSystem;
USE CommunityHelpSystem;

-- User Table
CREATE TABLE UserTable (
    UserID INT PRIMARY KEY,
    UserPassword VARCHAR(255) NOT NULL,
    UserName VARCHAR(100) NOT NULL,
    UserSurname VARCHAR(100) NOT NULL,
    UserEmail VARCHAR(255) UNIQUE NOT NULL,
    UserPhoneNumber INT,
    UserGender VARCHAR(10),
    UserDOB DATE,
    UserAddressID INT,
    UserBadgeID INT,
    UserRatingID INT,
    UserTypeID VARCHAR(50),
    FOREIGN KEY (UserAddressID) REFERENCES AddressTable(AddressID),
    FOREIGN KEY (UserBadgeID) REFERENCES BadgeTable(BadgeID),
    FOREIGN KEY (UserRatingID) REFERENCES RatingTable(RatingReview)
);

-- Address Table
CREATE TABLE AddressTable (
    AddressID INT PRIMARY KEY,
    AddressNumber INT,
    AddressStreet VARCHAR(200),
    AddressZip INT,
    NeighbourhoodID INT,
    ResidentID INT,
    FOREIGN KEY (NeighbourhoodID) REFERENCES LocationTable(LocationID)
);

-- Location Table
CREATE TABLE LocationTable (
    LocationID INT PRIMARY KEY,
    LocationCenterPoint INT,
    LocationRadius INT,
    NeighbourhoodID INT,
    NeighbourhoodName VARCHAR(100)
);

-- Badge Table
CREATE TABLE BadgeTable (
    BadgeID VARCHAR(50) PRIMARY KEY,
    BadgeName VARCHAR(100) NOT NULL,
    IsSpecialist BOOLEAN DEFAULT FALSE,
    CurrentXP INT DEFAULT 0,
    RatingID INT,
    FOREIGN KEY (RatingID) REFERENCES RatingTable(RatingReview)
);

-- Rating Table
CREATE TABLE RatingTable (
    RatingID INT PRIMARY KEY,
    RatingReview VARCHAR(50),
    TotalXPLevel INT,
    CurrentGroup VARCHAR(50)
);

-- Task Type Table
CREATE TABLE TaskTypeTable (
    TaskTypeID INT PRIMARY KEY,
    TypeDescription TEXT,
    AssociatedBadgeID VARCHAR(50),
    NeedsSpecialist BOOLEAN DEFAULT FALSE,
    XPWorth INT,
    FOREIGN KEY (AssociatedBadgeID) REFERENCES BadgeTable(BadgeID)
);

-- Admin Table
CREATE TABLE AdminTable (
    AdminID INT PRIMARY KEY,
    AdminPassword VARCHAR(255) NOT NULL,
    AdminName VARCHAR(100) NOT NULL,
    AdminSurname VARCHAR(100) NOT NULL,
    AdminEmail VARCHAR(255) UNIQUE NOT NULL,
    AdminPhoneNumber INT,
    AdminCreateDate DATE,
    AdminAccessLevel INT,
    UserID INT,
    AdminAddressID INT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID),
    FOREIGN KEY (AdminAddressID) REFERENCES AddressTable(AddressID)
);

-- Helper Table
CREATE TABLE HelperTable (
    HelperID INT PRIMARY KEY,
    UserID INT,
    BadgeID VARCHAR(50),
    TaskTypeID INT,
    CompatibleID INT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID),
    FOREIGN KEY (BadgeID) REFERENCES BadgeTable(BadgeID),
    FOREIGN KEY (TaskTypeID) REFERENCES TaskTypeTable(TaskTypeID),
    FOREIGN KEY (CompatibleID) REFERENCES CompatibilityTable(CompatibilityID)
);

-- Dependent Table
CREATE TABLE DependentTable (
    DependentID INT PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    CompatibleID INT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID),
    FOREIGN KEY (TaskTypeID) REFERENCES TaskTypeTable(TaskTypeID),
    FOREIGN KEY (CompatibleID) REFERENCES CompatibilityTable(CompatibilityID)
);

-- Compatibility Table
CREATE TABLE CompatibilityTable (
    CompatibilityID INT PRIMARY KEY,
    CompatibilityScore INT,
    CompatibilityColor VARCHAR(20),
    DependentID INT,
    HelperID INT,
    FOREIGN KEY (DependentID) REFERENCES DependentTable(DependentID),
    FOREIGN KEY (HelperID) REFERENCES HelperTable(HelperID)
);

-- Analytics Table
CREATE TABLE AnalyticsTable (
    AnalyticsID INT PRIMARY KEY,
    TaskID INT,
    AdminID INT,
    HelperTypeID VARCHAR(50),
    DependentTypeID VARCHAR(50),
    FOREIGN KEY (TaskID) REFERENCES TaskInvoiceTable(TaskID),
    FOREIGN KEY (AdminID) REFERENCES AdminTable(AdminID)
);

-- Helper Analytics Table
CREATE TABLE HelperAnalyticsTable (
    HelperTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    CompatibleID INT,
    LocationID INT,
    AverageRating FLOAT,
    AverageGivingRating FLOAT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID),
    FOREIGN KEY (TaskTypeID) REFERENCES TaskTypeTable(TaskTypeID),
    FOREIGN KEY (LocationID) REFERENCES LocationTable(LocationID)
);

-- Dependent Analytics Table
CREATE TABLE DependentAnalyticsTable (
    DependentTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT,
    TaskTypeID INT,
    TotalTasks INT DEFAULT 0,
    LocationID INT,
    AverageRating FLOAT,
    AverageGivingRating FLOAT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID),
    FOREIGN KEY (TaskTypeID) REFERENCES TaskTypeTable(TaskTypeID),
    FOREIGN KEY (LocationID) REFERENCES LocationTable(LocationID)
);

-- Task Invoice Table
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
    FOREIGN KEY (HelperID) REFERENCES HelperTable(HelperID),
    FOREIGN KEY (DependentID) REFERENCES DependentTable(DependentID),
    FOREIGN KEY (LocationID) REFERENCES LocationTable(LocationID),
    FOREIGN KEY (TaskTypeID) REFERENCES TaskTypeTable(TaskTypeID),
    FOREIGN KEY (SignedAdminID) REFERENCES AdminTable(AdminID),
    FOREIGN KEY (CompatibilityID) REFERENCES CompatibilityTable(CompatibilityID)
);

-- Create indexes for better performance
CREATE INDEX idx_user_email ON UserTable(UserEmail);
CREATE INDEX idx_task_dates ON TaskInvoiceTable(StartDate, EndDate);
CREATE INDEX idx_location_neighbourhood ON LocationTable(NeighbourhoodID);
CREATE INDEX idx_compatibility_score ON CompatibilityTable(CompatibilityScore);
CREATE INDEX idx_helper_badge ON HelperTable(BadgeID);
CREATE INDEX idx_dependent_task ON DependentTable(TaskTypeID);