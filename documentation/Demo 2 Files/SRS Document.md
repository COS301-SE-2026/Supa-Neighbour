# SupaNeighbour - SRS Document

| **Version** | **Date** | **Author** | **Approved By** | **Change Description** |
|-------------|----------|------------|-----------------|--------------------------|
| 1.0         | 2026-05-20 | Blessing Gibendi | Blessing Gibendi (Team Lead) | Initial release for SupaNeighbour |
| 2.0         | 2026-07-25 | Blessing Gibendi | Blessing Gibendi(Team Lead)  | Made changes to ensure it meets requirements for Demo 2|

# 1. Introduction

## Business Needs:

Alot of Ubran and sub-urban communities are increasingly characterised by social dissconecton. Despite living in close proximity, the neighbours rarely interact in meaningful ways, leaving individuals without the reliable support network for small but important day-to-day tasks. When residents travel, work long hours, or face unexpected circumstances, simple household responsibilities such as collecting packages, caring for plants, feeding pets, managing bin days, and conducting home check-ins can become significant sources of stress.

Existing solutions seems to only exacerbate this problem. This is were SupaNeighbour comes in: providing a dedicated, secure, community-driven platform that connects the residents within the same nehbourhood by enabling someone to request and provide short-term assistance for small household tasks. By embedding trust, transparency, and community collaboration at the core of the platform, Supa-Neighbour transforms neighbours from strangers into a dependable local support network.

## Project Scope

SupaNeighbour will be delivered as a cross-platform solution consisting of mobile application for general community users and web-based admin dashboard for aplatform administrators

The system will allows residents to post assistance for short-term household tasks, browse and reposnse to requests from neighbouring users and buils a profile of community contributions over time.

The admin dashbord provides a platform for manages with the needed tools to monitor community activity, manage users and handle reported content and maintain the overall health and safety of the platform.



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


## 3. User Stories

**1. Account Management & Authentication**
| ID | User Story | Requirement |
|---|---|---|
| US-R01 | As a Resident, I want to register using my email or phone number so that I can create a secure account on the platform. | R1.1 |
| US-R04 | As a Resident, I want to log in securely using my email and password so that I can access my account.| R1.2.1 |
| US-R05 | As a Resident, I want my account to be locked after too many failed login attempts so that my account is protected from unauthorised access.| R1.2.2 |
| US-R06 | As a Resident, I want to reset my password if I forget it so that I can regain access to my account. | R1.2.2 |
| US-R07 | As a Resident, I want to view and edit my profile, skills, and availability so that helpers and requesters can see relevant information about me. | R1.2.3 |

**2. Task Managenment**
| ID | User Story| Requirement |
|---|---|---|
| US-R08 | As a Resident, I want to create a task request on the app | R2.1.1, R2.1.2 |
| US-R09 | As a Resident, I want to be able to edit my task  | R2.1 |
| US-R10 | As a Resident, I want to view all my posted tasks and their statuses so that I can track what help I have requested.| R2.1 |
| US-R12 | As a Resident, I want to filter helpers| R2.2.3 |
| US-R13 | As a Resident, I want to confirm task completion and rate my helper so that the community benefits from accurate trust scores. | R5.3.1 |
| US-R14 | As a Resident, I want my exact home address to remain hidden until a helper has been accepted so that my privacy is protected. | R4.1.1 |

**3. Communication**

| ID | User Story| Requirement |
|---|---|---|
| US-R15 | As a Resident, I want to chat with my accepted helper in real time so that we can coordinate task details securely.| R3.1.1 |
| US-R16 | As a Resident, I want to receive push notifications when a helper accepts my task so that I am informed immediately. | R3.2.1 |
| US-R17 | As a Resident, I want to receive a notification when my task has been marked complete so that I can confirm and rate the helper. | R3.2.1 |
| US-R18 | As a Resident, I want all communication with helpers to stay within the app so that my personal contact details are never exposed. | R4.1.2 |

# 4. Use cases

## 4.1 Use Cases

## 4.2 Use Case Diagram

Please refer to this image for the use case Diagram: [Use Case Diagram](Images/Use%20Case%20Diagram%20V2.jpeg)

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
- **R5.2.1** — The system must display a neighbourhood leaderboard showing helper rankings.

### R4.3: The system shall maintain a trust score for each user.
- **R5.3.1** — The system must allow users to rate helpers after task completion.
- **R5.3.2** — The system must calculate and display trust scores based on completed tasks and ratings received.

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

---

# 6. Non-Function Requirements

Below are the Non-fuctional requirements that were focused on for Demo 2:


# 7. Domain Model

Please refer to the file for the Domain model: [Domain Model](Images/SNR-Domain-Model.drawio.png)

