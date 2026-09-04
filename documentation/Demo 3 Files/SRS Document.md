# SupaNeighbour - SRS Document


## Table of Contents

- [SupaNeighbour - SRS Document](#supaneighbour---srs-document)
  - [Table of Contents](#table-of-contents)
- [1. Introduction](#1-introduction)
  - [Business Needs:](#business-needs)
  - [Project Scope](#project-scope)
- [2. User Characteristics](#2-user-characteristics)
- [3. User Stories](#3-user-stories)
  - [1. Account Management (R1)](#1-account-management-r1)
  - [2. Task Management \& Matching (R2)](#2-task-management--matching-r2)
  - [3. Communication (R3)](#3-communication-r3)
  - [4. Security \& Privacy (R4)](#4-security--privacy-r4)
  - [5. Gamification \& Trust Scoring (R5)](#5-gamification--trust-scoring-r5)
- [4. Use cases](#4-use-cases)
  - [4.1 Use Cases](#41-use-cases)
  - [4.2 Use Case Diagram](#42-use-case-diagram)
- [5. Functional requirements](#5-functional-requirements)
  - [R1: User Account Management](#r1-user-account-management)
    - [R1.1: The system shall allow new users to register using their email address.](#r11-the-system-shall-allow-new-users-to-register-using-their-email-address)
    - [R1.2: The system shall allow users to securely log in and manage their profile.](#r12-the-system-shall-allow-users-to-securely-log-in-and-manage-their-profile)
  - [R2: Task Request and Matching Engine](#r2-task-request-and-matching-engine)
    - [R2.1: The system shall allow users to create and publish task requests.](#r21-the-system-shall-allow-users-to-create-and-publish-task-requests)
    - [R2.2: The system shall match task requests with suitable helpers based on multiple criteria.](#r22-the-system-shall-match-task-requests-with-suitable-helpers-based-on-multiple-criteria)
  - [R3: In-App Communication](#r3-in-app-communication)
    - [R3.1: The system shall provide real-time chat messaging between task requesters and accepted helpers.](#r31-the-system-shall-provide-real-time-chat-messaging-between-task-requesters-and-accepted-helpers)
    - [R3.2: The system shall provide task status updates and reminders.](#r32-the-system-shall-provide-task-status-updates-and-reminders)
  - [R4: Gamification and Trust Scoring](#r4-gamification-and-trust-scoring)
    - [R4.1: The system shall award points and progression levels for completed tasks.](#r41-the-system-shall-award-points-and-progression-levels-for-completed-tasks)
    - [R4.2: The system shall provide community recognition features.](#r42-the-system-shall-provide-community-recognition-features)
    - [R4.3: The system shall maintain a trust score for each user.](#r43-the-system-shall-maintain-a-trust-score-for-each-user)
  - [R5: Security and Privacy](#r5-security-and-privacy)
    - [R5.1: The system shall protect sensitive location and contact information.](#r51-the-system-shall-protect-sensitive-location-and-contact-information)
    - [R5.2: The system shall enforce strict permission controls.](#r52-the-system-shall-enforce-strict-permission-controls)
  - [R6: User Interface and Accessibility](#r6-user-interface-and-accessibility)
    - [R6.1: The system shall follow design best practices for usability.](#r61-the-system-shall-follow-design-best-practices-for-usability)
    - [R6.2: The system shall provide visual feedback and guidance.](#r62-the-system-shall-provide-visual-feedback-and-guidance)
- [6. Non-Functional Requirements](#6-non-functional-requirements)
  - [6.1 Reliability](#61-reliability)
  - [6.2 Maintainability](#62-maintainability)
  - [6.3 Availability](#63-availability)
- [7. Domain Model](#7-domain-model)

---

# 1. Introduction

## Business Needs

Many urban and sub-urban communities are increasingly characterised by social disconnection. Despite living in close proximity, neighbours rarely interact in meaningful ways, leaving individuals without a reliable support network for small but important day-to-day tasks. When residents travel, work long hours, or face unexpected circumstances, simple household responsibilities; collecting packages, caring for plants, feeding pets, managing bin days, home check-ins can become significant sources of stress.

Existing solutions tend to exacerbate this problem rather than solve it. Supa-Neighbour addresses this gap: a dedicated, secure, community-driven platform that connects residents within the same neighbourhood, enabling someone to request and provide short-term assistance for small household tasks. By embedding trust, transparency, and community collaboration at its core, Supa-Neighbour transforms neighbours from strangers into a dependable local support network.

## Project Scope

Supa-Neighbour is delivered as a cross-platform solution:
- A **mobile application** for general community users
- A **web-based admin dashboard** for platform administrators

**Mobile application** — residents can:
- Post requests for assistance with short-term household tasks
- Browse and respond to requests from neighbouring users
- Build a profile of community contributions over time

**Admin dashboard** — administrators can:
- Monitor community activity
- Manage users
- Handle reported content
- Maintain the overall health and safety of the platform



# 2. User Characteristics
The Supa-Neighbour platform serves three distinct user types. A single registered account can either be a Resident (requester) and a Helper depending on the context

**1.1 Resident**
A Resident is any registered community member who uses the platform to request short-term household assistance. 

* Characteristics
  - Lives within a defined neighbourhood zone
  - May have varying levels of technical literacy
  - Motivated by convenience, trust, and community connection
  - Interacts primarily in the mobile app

* Primary Goals
  - Post tasks easily and quickly
  - Find a trustworthy, nearby helper
  - Communicate securly without exposing personal contact details
  - Track the status of their requests in real time

**1.2 Helper**
A Helper is a registered community member who browses available task requests and offers their assistance to neighbours.

* Characteristics
  - Motivated by community contribution, trust score growth, and gamification rewards
  - May specialise in certain task types (e.g., pet care, home check-ins)
  - Interacts primarily with the mobile app
  - Builds a reputation over time through ratings and XP progression

* Primary Goals
  - Browse and accept nearby tasks that match their skills and availability
  - Build a strong trust score and progress through helper levels
  - Communicate with requesters clearly and securely
  - Complete tasks and receive recognition for contributions
  

**1.3 Administrator**
An Administrator is a platform manager who oversees the health, safety, and integrity of the Supa-Neighbour community. Admins do not participate in the creation or accepting of tasks

* Characteristics
  - Has elevated access permissions not available to regular users
  - Interacts primarily with the web admin dashboard
  - Responsible for moderation, user management, and platform configuration
  - Technically proficient — the admin interface does not need to be simplified to the same degree as the mobile app
  
* Primary goals
  - Monitor and moderate community activity
  - Manage reported content, flagged messages, and suspended accounts
  - Configure neighbourhood zones and platform settings
  - View platform-wide analytics and activity logs


# 3. User Stories

The following user stories are grouped according to functionality.


## 1. Account Management (R1)


**US-R01: Account Registration**


The system shall allow a user to register for an account using their email address or phone number.


**Acceptance Criteria**

- **AC 1.1:** The system provides a registration form with fields for email/phone and password.
- **AC 1.2:** The system validates that the email/phone is not already registered.
- **AC 1.3:** Upon successful registration, a new user account is created in the system.

**US-R04: Secure Login**


The system shall allow a registered user to log in securely using their email and password.


**Acceptance Criteria**

- **AC 4.1:** The system provides a login form requiring email and password.
- **AC 4.2:** The system authenticates the user against stored credentials.
- **AC 4.3:** Upon successful authentication, the user is granted access to their account dashboard.

**US-R05: Account Lockout**


The system shall lock a user's account after a predefined number of consecutive failed login attempts to prevent unauthorised access.


**Acceptance Criteria**

- **AC 5.1:** The system tracks consecutive failed login attempts for each account.
- **AC 5.2:** The account is automatically locked after the configured threshold (e.g., 5 attempts) is exceeded.
- **AC 5.3:** A locked account prevents any further login attempts until unlocked (e.g., via admin or a time delay).

**US-R06: Password Reset**


The system shall provide a mechanism for users to reset their password if they forget it, enabling them to regain access to their account.


**Acceptance Criteria**

- **AC 6.1:** The login screen provides a "Forgot Password" link.
- **AC 6.2:** The system sends a password reset link to the user's registered email/phone upon request.
- **AC 6.3:** The user can set a new password using the valid, one-time reset link.

**US-R07: Manage Profile**


The system shall allow users to view and edit their profile information, including personal details, skills, and availability.


**Acceptance Criteria**

- **AC 7.1:** The user profile page displays all relevant details.
- **AC 7.2:** The user can edit and save changes to their profile information.
- **AC 7.3:** The updated profile information is visible to other users as per the system's privacy settings.

## 2. Task Management & Matching (R2)


**2.1: Create Task Request**


The system shall allow a requester to create a new task request, specifying the task type, date, time window, and any special instructions.


**Acceptance Criteria**

- **AC 8.1:** The system provides a task creation form with mandatory fields for task type, date, and time window.
- **AC 8.2:** The requester can add optional instructions for the helper.
- **AC 8.3:** Upon submission, the task is posted and becomes available for matching.

**2.2: Edit Task**


The system shall allow a requester to edit the details of an existing task that has not yet been accepted.


**Acceptance Criteria**

- **AC 9.1:** The task details page for a pending task shows an 'Edit' option.
- **AC 9.2:** The requester can modify any field of the task (e.g., date, time, instructions).
- **AC 9.3:** The system prevents editing of a task that has already been accepted or is in progress.

**2.3: View Posted Tasks**


The system shall allow a requester to view a list of all their posted tasks along with their current statuses.


**Acceptance Criteria**

- **AC 10.1:** The requester's dashboard displays a list of all tasks they have created.
- **AC 10.2:** Each task in the list clearly displays its current status (e.g., Open, Accepted, Completed).
- **AC 10.3:** The list can be filtered or sorted to manage viewing.


**2.4: Filter Helpers**


The system shall allow a requester to filter available helpers based on criteria such as proximity, skills, or a "verified only" status.


**Acceptance Criteria**

- **AC 12.1:** When viewing potential helpers, the requester has access to filter options.
- **AC 12.2:** Selecting a filter (e.g., proximity, skill, verified) updates the list of displayed helpers accordingly.
- **AC 12.3:** The system returns results that match the selected filter criteria.

**2.5: View Matched Tasks**


The system shall show a helper a list of available tasks that match their skill set and trust score.


**Acceptance Criteria**

- **AC 19.1:** The helper's task feed only displays tasks appropriate for their listed skills.
- **AC 19.2:** The tasks shown to the helper are compatible with their current trust score.
- **AC 19.3:** The task list is prioritised or filtered based on these matching criteria.

**2.6: Accept/Decline Task Invite**

**Requirement Statement**

The system shall allow a helper to accept or decline a task request that has been offered to them.


**Acceptance Criteria**

- **AC 20.1:** The helper receives a notification or view of a pending task invitation.
- **AC 20.2:** The helper can choose to 'Accept' the task, confirming their commitment.
- **AC 20.3:** The helper can choose to 'Decline' the task, which removes it from their pending list.


## 3. Communication (R3)



**3.1: In-App Chat**

The system shall provide a real-time chat feature for a requester and helper to coordinate task details securely within the app.


**Acceptance Criteria**

- **AC 15.1:** A chat interface is available for an accepted task, visible to both the requester and the helper.
- **AC 15.2:** Messages sent through the chat are delivered and displayed in real-time.
- **AC 15.3:** The chat history is saved and accessible for the duration of the task.

**3.2: Acceptance Notification**

**Requirement Statement**

The system shall send a push notification to the requester when a helper accepts their task.


**Acceptance Criteria**

- **AC 16.1:** A push notification is triggered immediately upon a helper's acceptance of a task.
- **AC 16.2:** The notification is sent to the requester's device.
- **AC 16.3:** The notification contains relevant details (e.g., helper name, task title).

**3.3: Completion Notification**


The system shall send a notification to the requester when a helper marks a task as complete.


**Acceptance Criteria**

- **AC 17.1:** A notification is triggered when a task is marked as 'Complete'.
- **AC 17.2:** The requester receives a notification on their device.
- **AC 17.3:** The notification prompts the requester to confirm completion and rate the helper.

**3.4: Share Photo Updates**


The system shall allow a helper to share photo updates with the requester during a task.


**Acceptance Criteria**

- **AC 21.1:** The helper can attach and send photos within the in-app chat.
- **AC 21.2:** The sent photos are visible to the requester in the chat stream.
- **AC 21.3:** The photos are stored securely and associated with the correct task.

**3.5: Mark Task as Complete**


The system shall allow a helper to mark a task as complete, optionally with photo evidence, to notify the requester for confirmation.


**Acceptance Criteria**

- **AC 22.1:** The helper can access a 'Mark as Complete' button for an active task.
- **AC 22.2:** The helper can optionally attach photo evidence when marking the task complete.
- **AC 22.3:** Upon submission, the task status changes and the requester is notified.

## 4. Security & Privacy (R4)

**4.1: Hide Address**

**Requirement Statement**

The system shall ensure that a requester's exact home address remains hidden from helpers until the task has been accepted.


**Acceptance Criteria**

- **AC 14.1:** Task listings in the helper's feed do not display the requester's exact address.
- **AC 14.2:** The full address is only revealed to the helper after they have accepted the task.
- **AC 14.3:** The address is accessible within the task details after acceptance.

**4.2: In-App Communication Only**

**Requirement Statement**

The system shall require all communication between requesters and helpers to occur within the app, preventing the exposure of personal contact details.


**Acceptance Criteria**

- **AC 18.1:** The system does not display personal phone numbers or email addresses of users.
- **AC 18.2:** All task-related communication is routed through the app's chat feature.
- **AC 18.3:** The system prohibits sharing of external contact information within the chat.

## 5. Gamification & Trust Scoring (R5)


**5.1: Rate Helper**


The system shall allow a requester to confirm task completion and provide a rating for the helper to contribute to the community trust score.


**Acceptance Criteria**

- **AC 13.1:** After a task is marked complete, the requester is prompted to rate the helper.
- **AC 13.2:** The requester can submit a rating (e.g., 1-5 stars) and optional feedback.
- **AC 13.3:** Upon submission, the rating is recorded and used to update the helper's trust score.

**5.2: Earn XP**


The system shall award Experience Points (XP) to a helper when a task they completed is marked as done, enabling them to progress through levels.


**Acceptance Criteria**

- **AC 23.1:** XP is automatically awarded to the helper when a task is confirmed as complete.
- **AC 23.2:** The helper's XP total is updated and visible on their profile.
- **AC 23.3:** The helper progresses through levels (Bronze → Silver → Gold) upon reaching XP thresholds.

**5.3: View Leaderboard**

**Requirement Statement**

The system shall allow helpers to view a neighbourhood leaderboard to compare their ranking with other helpers.


**Acceptance Criteria**

- **AC 24.1:** A 'Leaderboard' section is accessible to helpers within the app.
- **AC 24.2:** The leaderboard displays a list of helpers ranked by their trust score or XP.
- **AC 24.3:** The current user's own ranking and score are clearly highlighted on the leaderboard.

**5.4: Trust Score Calculation**


The system shall calculate a user's trust score as the average rating they have received from other residents.


**Acceptance Criteria**

- **AC 25.1:** The trust score is automatically calculated based on the average of all received ratings.
- **AC 25.2:** The score is recalculated and updated when a new rating is added.
- **AC 25.3:** The calculated trust score is displayed on the user's profile and used for matching purposes.



## 6. Administration (R7)


**US-R26: Admin Login**


The system shall allow an administrator to securely log in to the admin dashboard using their credentials.


**Acceptance Criteria**

- **AC 26.1:** The system provides a dedicated admin login screen separate from the standard user login.
- **AC 26.2:** The system authenticates the admin against stored credentials and verifies admin privileges.
- **AC 26.3:** Upon successful authentication, the admin is redirected to the admin dashboard.
- **AC 26.4:** The system returns a `403 Forbidden` error if a non-admin user attempts to access the dashboard.


**US-R27: View Admin Dashboard**


The system shall allow an administrator to view key platform metrics and recent activity on the dashboard.


**Acceptance Criteria**

- **AC 27.1:** The dashboard displays summary statistics including total reports, pending reports, and resolved reports.
- **AC 27.2:** The dashboard shows a breakdown of reports by type (User, Post, Comment, Task Dispute).
- **AC 27.3:** The dashboard displays the number of reports currently assigned to the logged-in admin.
- **AC 27.4:** The dashboard highlights urgent or high-severity reports for immediate attention.


**US-R28: View Reports List**


The system shall allow an administrator to view all user-submitted reports.


**Acceptance Criteria**

- **AC 28.1:** The admin can view a list of all reports showing report type, status, severity, and submission date.
- **AC 28.2:** The admin can filter reports by status (Submitted, Assigned, Reviewed, Resolved).
- **AC 28.3:** The admin can filter reports by severity (Minor, Moderate, Severe).
- **AC 28.4:** The admin can filter reports by type (User, Post, Comment, Task Dispute).
- **AC 28.5:** The admin can search for reports by ID or reporter name.
- **AC 28.6:** Urgent/severe reports are visually highlighted.


**US-R29: View Report Details**


The system shall allow an administrator to view the full details of a specific report.


**Acceptance Criteria**

- **AC 29.1:** The admin can view the report ID, type, status, and severity.
- **AC 29.2:** The admin can view the reporter's information and the reported entity.
- **AC 29.3:** The admin can view the reason for the report and any additional description.
- **AC 29.4:** The admin can view the current assigned admin (if any).
- **AC 29.5:** The admin can view the report's creation and resolution dates.


**US-R30: Action a Report**


The system shall allow an administrator to take action on a submitted report.


**Acceptance Criteria**

- **AC 30.1:** The admin can select a violation type from a predefined list (Harassment, Privacy Violation, Task No-Show, Inappropriate Content, etc.).
- **AC 30.2:** The admin can select a severity level (Minor, Moderate, Severe).
- **AC 30.3:** The system provides a suggested action based on the violation type and severity.
- **AC 30.4:** The admin can accept the suggested action or override it with a different action.
- **AC 30.5:** The admin can add notes explaining their decision.
- **AC 30.6:** The admin can approve, dismiss, or escalate the report.
- **AC 30.7:** The system records both the suggested action and the actual action taken.
- **AC 30.8:** The system sends a notification to the affected user with the outcome.
- **AC 30.9:** The report status is updated to "Resolved" once action is taken.

# 4. Use cases

## 4.1 Use Cases

Please find the Use Cases Listed on the following Link: [Use Cases](/documentation/Demo%203%20Files/High%20Level%20Use%20Case%20V2.md)



## 4.2 Use Case Diagram

Please refer to this image for the use case Diagram: [Use Case Diagram](Images/Use%20Cases%20V3.png)

# 5. Functional requirements

## R1: User Account Management
The system shall allow users to create and manage secure accounts with verified neighbourhood information.

### R1.1: The system shall allow new users to register using their email address.
- **R1.1.1** — The system must verify the user's email address via a verification link sent to their email.
- **R1.1.2** — The system must require users to provide their residential address (street name, suburb, and/or complex name) to determine neighbourhood zone membership.

### R1.2: The system shall allow users to securely log in and manage their profile.
- **R1.2.1** — The system must support password-based authentication with password strength validation (minimum 8 characters, mixed case, number, and special character).
- **R1.2.3** — The system must allow users to view their trust score, skills/tags, and availability preferences.

---

## R2: Task Request and Matching Engine
The system shall allow residents to request household task assistance and match requests with suitable helpers.

### R2.1: The system shall allow users to create and publish task requests.
- **R2.1.1** — The system must support task types including plant care, pet feeding, bin collection, package collection, and home check-ins.
- **R2.1.2** — The system must require the user to specify task date, time window, and any special instructions.

### R2.2: The system shall match task requests with suitable helpers based on multiple criteria.
- **R2.2.1** — The system must prioritize helpers based on proximity (same complex, street, or neighbourhood zone).
- **R2.2.2** — The system must consider helper trust score, availability, and skills/tags when recommending helpers.

---

## R3: In-App Communication
The system shall allow users to communicate in real time for task coordination.

### R3.1: The system shall provide real-time chat messaging between task requesters and accepted helpers.
- **R3.1.1** — The system must support text-based messaging for task-related communication.
- **R3.1.2** — The system must allow users to share photo updates (e.g., "Plants watered!").

### R3.2: The system shall provide task status updates and reminders.
- **R3.2.1** — The system must send push notifications for task confirmations, upcoming commitments, and task completion.
- **R3.2.2** — The system must allow helpers to mark tasks as complete with optional photo evidence.

---
## R4: Gamification and Trust Scoring
The system shall encourage ongoing participation through gamification and build trust through a scoring system.

### R4.1: The system shall award points and progression levels for completed tasks.
- **R4.1.1** — The system must award XP points to helpers upon successful task completion.
- **R4.1.2** — The system must support helper progression levels (Bronze → Silver → Gold) based on accumulated XP.

### R4.2: The system shall provide community recognition features.
- **R4.2.1** — The system must display a neighbourhood leaderboard showing helper rankings.

### R4.3: The system shall maintain a trust score for each user.
- **R4.3.1** — The system must allow users to rate helpers after task completion.
- **R4.3.2** — The system must calculate and display trust scores based on completed tasks and ratings received.

---

## R5: Security and Privacy
The system shall protect user privacy and ensure safe task exchanges.

### R5.1: The system shall protect sensitive location and contact information.
- **R5.1.1** — The system must reveal the requester's exact address to a helper ONLY after the task has been accepted.
- **R5.1.2** — The system must never expose personal contact numbers (phone, email) between users; all communication must occur through in-app chat.

### R5.2: The system shall enforce strict permission controls.
- **R5.2.1** — The system must request and respect location permissions with clear user consent.
- **R5.2.2** — The system must implement role-based access control (resident, helper, admin).

---

## R6: User Interface and Accessibility
The system shall provide a friendly, warm, and accessible user interface.

### R6.1: The system shall follow design best practices for usability.
- **R6.1.1** — The system must provide a simple interface for posting and accepting tasks, suitable for all age groups.
- **R6.1.2** — The system must support large fonts and intuitive navigation for accessibility.

### R6.2: The system shall provide visual feedback and guidance.
- **R6.2.1** — The system must display ratings and badges clearly on helper profiles.
- **R6.2.2** — The system must include easy scheduling workflows with reminder confirmations.

## R7: Administration and Moderation
The system shall provide administrators with the tools to monitor community conduct and enforce platform rules.

### R7.1: The system shall allow administrators to securely authenticate and access the admin dashboard.
- **R7.1.1** — The system must restrict access to admin functionality to users flagged with admin privileges.
- **R7.1.2** — The system must redirect authenticated admins to a dedicated admin dashboard, separate from the standard user experience.

### R7.2: The system shall allow administrators to view and manage user-submitted reports.
- **R7.2.1** — The system must display all submitted reports, including the reported entity (user, task, or bulletin post) and current status.
- **R7.2.2** — The system must allow an admin to action a report by selecting a suggested moderation action (Warning, Suspension, or Ban).
- **R7.2.3** — The system must record both the suggested action and the actual action taken against a report, for audit purposes.
- **R7.2.4** — The system must update the report's status once an action has been taken.

### R7.3: The system shall log moderation actions taken against user accounts.
- **R7.3.1** — The system must maintain a record of warnings, suspensions, and bans issued to a user, including the reason and the administrator responsible.

### R7.4: The system shall allow administrators to manage their own profile.
- **R7.4.1** — The system must allow admins to view and update their personal account settings from within the admin dashboard.

---

# 6. Non-Functional Requirements

Below are the Non-functional requirements that were focused on for Demo 3:

## 6.1 Reliability

**Target:** The system should achieve 99.9% uptime and recover from critical failures within 5 minutes.

The backend is deployed as a Docker container configured with an automatic restart policy, allowing rapid recovery from application failures. Persistent application data is stored in Azure Database for PostgreSQL, which provides automated backups with a seven-day retention period. User-uploaded media is stored in Azure Blob Storage using Locally Redundant Storage (LRS), ensuring multiple copies of files are maintained within the Azure region to protect against hardware failures. Together, these measures reduce downtime and support recovery from failures within the project's required timeframe.

## 6.2 Maintainability

**Target:** New features or bug fixes should be deployable within 2 hours, and the codebase should maintain at least 80% automated test coverage.

Maintainability is implemented through a containerized CI/CD pipeline (GitHub Actions → Azure Container Registry → App Service) with environment-specific config (`application-azure.yml`) and secrets managed via Key Vault, so a merged change requires no manual environment or credential steps to reach deployment — supporting the 2-hour deployability target. Automated test coverage is tracked continuously via SonarQube against a minimum 80% threshold, with particular focus on the Demo 3 use cases (UC6, UC7, UC8).

## 6.3 Availability

**Target:** The system should be available 24/7, excluding scheduled maintenance periods not exceeding 2 hours per month.

Availability is supported by Azure's managed infrastructure — Postgres Flexible Server and App Service — which provide documented uptime SLAs, with scheduled maintenance windows communicated in advance and kept under 2 hours/month.


## 6.4 Usability

**Target:** A new user should be able to complete core tasks within 5 minutes of first using the system, with at least 85% user satisfaction during usability testing.

Usability is achieved through a user-centered design approach, consistent UI patterns, and intuitive navigation. The mobile application follows a familiar bottom navigation bar pattern with clearly labelled tabs (Home, Tasks, Chat, Profile), making core functionality discoverable. Task creation and acceptance workflows are streamlined with minimal required inputs and clear visual feedback at each step.

The admin dashboard follows a similar design philosophy with a collapsible sidebar, clear information hierarchy, and consistent interaction patterns. All interfaces use large fonts (minimum 14pt body text), high-contrast colour combinations (WCAG 2.1 AA compliant), and accessible touch targets (minimum 44pt) to support users of all ages and abilities.

A Help Menu is available throughout the application, providing users with quick access to FAQs, user manual, and support contact information.

**Usability Testing Plan:**

| Metric | Method | Target |
|--------|--------|--------|
| Task Completion Rate | Moderated usability testing with 5-8 participants | > 90% |
| Time on Task | Timed observation of core tasks | < 5 minutes |
| User Satisfaction | System Usability Scale (SUS) questionnaire | Average score > 70 |
| Error Recovery | Observation of user errors and recovery | < 5% abandonment rate |

# 7. Domain Model

Please refer to the file for the Domain model: [Domain Model](Images/SNR-Domain-Model.drawio%20V3.png)

---

| **Version** | **Date** | **Author** | **Approved By** | **Change Description** |
|-------------|----------|------------|-----------------|--------------------------|
| 1.0         | 2026-05-20 | Blessing Gibendi | Blessing Gibendi (Team Lead) | Initial release for SupaNeighbour |
| 2.0         | 2026-07-25 | Blessing Gibendi | Blessing Gibendi (Team Lead) | Made changes to ensure it meets requirements for Demo 2 |
| 3.0         | 2026-07-28 | Blessing Gibendi | Blessing Gibendi (Team Lead) | Added table of contents and reviewed document structure |
| 4.0 | 2026-09-03 | Blessing Gibendi | Blessing Gibendi | Added the NFR tests |
|4.0     | 2026-09-03 | Michelle Njoroge | Blessing Gibendi (Team Lead) | Added Admin user stories for administration features and added Usability NFR|
|5.0          | 2026-09-03 | Amantle Temo     | Blessing Gibendi (Team Lead) | Correct link to domain model and corrected numbering for R4 |
