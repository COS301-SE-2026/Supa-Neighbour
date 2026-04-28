-- =========================
-- USER TABLE
-- =========================
CREATE TABLE UserTable (
    UserID INT PRIMARY KEY,
    UserPassword VARCHAR(50) NOT NULL,
    UserName VARCHAR(50) NOT NULL,
    UserSurname VARCHAR(50) NOT NULL,
    UserEmail VARCHAR(50) NOT NULL,
    UserPhoneNumber INT NOT NULL,
    UserGender VARCHAR(20) NOT NULL,
    UserDOB DATE NOT NULL,
    UserAddressID INT NOT NULL,
    UserBadgeID INT,
    UserRatingID INT,
    UserTypeID VARCHAR(50) NOT NULL
);

-- =========================
-- ADMIN TABLE
-- =========================
CREATE TABLE AdminTable (
    AdminID INT PRIMARY KEY,
    AdminPassword VARCHAR(50) NOT NULL,
    AdminName VARCHAR(50) NOT NULL,
    AdminSurname VARCHAR(50) NOT NULL,
    AdminEmail VARCHAR(50) NOT NULL,
    AdminPhoneNumber INT NOT NULL,
    AdminCreateDate DATE NOT NULL,
    AdminAccessLevel INT NOT NULL,
    UserID INT NOT NULL,
    AdminAddressID INT NOT NULL,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID)
);

-- =========================
-- ADDRESS TABLE
-- =========================
CREATE TABLE AddressTable (
    AddressID INT PRIMARY KEY,
    AddressNumber INT NOT NULL,
    AddressStreet VARCHAR(50) NOT NULL,
    AddressZip INT NOT NULL,
    NeighbourhoodID INT NOT NULL,
    ResidentID INT NOT NULL
);

-- =========================
-- LOCATION TABLE
-- =========================
CREATE TABLE LocationTable (
    LocationID INT PRIMARY KEY,
    LocationCenterPoint INT NOT NULL,
    LocationRadius INT NOT NULL,
    NeighbourhoodID INT NOT NULL,
    NeighbourhoodName VARCHAR(50) NOT NULL
);

-- =========================
-- TASK TYPE TABLE
-- =========================
CREATE TABLE TaskTypeTable (
    TaskTypeID INT PRIMARY KEY,
    TypeDescription VARCHAR(50) NOT NULL,
    AssociatedBadgeID INT NOT NULL,
    NeedsSpecialist BOOL NOT NULL,
    XPWorth INT NOT NULL
);

-- =========================
-- BADGE TABLE
-- =========================
CREATE TABLE BadgeTable (
    BadgeID VARCHAR(60) PRIMARY KEY,
    BadgeName VARCHAR(50),
    IsSpecialist BOOL NOT NULL,
    CurrentXP INT NOT NULL,
    RatingID INT NOT NULL
);

-- =========================
-- RATING TABLE
-- =========================
CREATE TABLE RatingTable (
    RatingReview VARCHAR(50) PRIMARY KEY,
    TotalXPLevel INT NOT NULL,
    CurrentGroup VARCHAR(50)
);

-- =========================
-- HELPER TABLE
-- =========================
CREATE TABLE HelperTable (
    HelperID INT PRIMARY KEY,
    UserID INT NOT NULL,
    BadgeID VARCHAR(60) NOT NULL,
    TaskTypeID INT NOT NULL,
    CompatibleID INT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID)
);

-- =========================
-- DEPENDENT TABLE
-- =========================
CREATE TABLE DependentTable (
    DependentID INT PRIMARY KEY,
    UserID INT NOT NULL,
    TaskTypeID INT NOT NULL,
    CompatibleID INT,
    FOREIGN KEY (UserID) REFERENCES UserTable(UserID)
);

-- =========================
-- COMPATIBILITY TABLE
-- =========================
CREATE TABLE CompatibilityTable (
    CompatibilityID INT PRIMARY KEY,
    CompatibilityScore INT NOT NULL,
    CompatibilityColor VARCHAR(50) NOT NULL,
    DependentID INT NOT NULL,
    HelperID INT NOT NULL
);

-- =========================
-- TASK INVOICE TABLE
-- =========================
CREATE TABLE TaskInvoiceTable (
    TaskID INT PRIMARY KEY,
    HelperID INT NOT NULL,
    DependentID INT NOT NULL,
    IsImmediate BOOL NOT NULL,
    LocationID INT NOT NULL,
    TaskTypeID INT NOT NULL,
    NeedsSpecialist BOOL NOT NULL,
    SignedAdminID INT,
    StartDate DATE NOT NULL,
    EndDate DATE,
    HelperBadgeID INT NOT NULL,
    DependentRatingID INT NOT NULL,
    HelperRatingID INT NOT NULL,
    AdminReview VARCHAR(50),
    CompatibilityID INT NOT NULL
);

-- =========================
-- ANALYTICS TABLE
-- =========================
CREATE TABLE AnalyticsTable (
    AnalyticsID INT PRIMARY KEY,
    UserID INT NOT NULL,
    UserTypeID INT NOT NULL
);

-- =========================
-- HELPER ANALYTICS TABLE
-- =========================
CREATE TABLE HelperAnalyticsTable (
    UserTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT NOT NULL,
    TaskTypeID INT NOT NULL,
    CompatibleID INT NOT NULL,
    LocationID INT NOT NULL,
    AverageRating FLOAT NOT NULL,
    AverageGivingRating FLOAT NOT NULL
);

-- =========================
-- DEPENDENT ANALYTICS TABLE
-- =========================
CREATE TABLE DependentAnalyticsTable (
    UserTypeID VARCHAR(50) PRIMARY KEY,
    UserID INT NOT NULL,
    TaskTypeID INT NOT NULL,
    TotalTasks INT NOT NULL,
    LocationID INT NOT NULL,
    AverageRating FLOAT NOT NULL,
    AverageGivingRating FLOAT NOT NULL
);