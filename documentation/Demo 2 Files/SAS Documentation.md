# Software Architecture Specifications - Supa-Neighbour


## Table of Contents
- [Software Architecture Specifications - Supa-Neighbour](#software-architecture-specifications---supa-neighbour)
  - [Table of Contents](#table-of-contents)
  - [1. Introduction](#1-introduction)
  - [2. Architectural Requirements](#2-architectural-requirements)
    - [2.1 Architectural Pattern](#21-architectural-pattern)
      - [2.1.1 Reasons for Choosing This Architecture](#211-reasons-for-choosing-this-architecture)
    - [2.2 Constraints on the Architecture](#22-constraints-on-the-architecture)
    - [2.3 Quality Requirements](#23-quality-requirements)
  - [3. Technology Requirements](#3-technology-requirements)
  - [4. API Contract](#4-api-contract)
  - [5. Deployment](#5-deployment)
- [Architectural Requirements](#architectural-requirements)
  - [Quality Requirements](#quality-requirements)
    - [QR1: Performance](#qr1-performance)
    - [QR2: Security \& Privacy](#qr2-security--privacy)
    - [QR3: Reliability \& Availability](#qr3-reliability--availability)
    - [QR4: Usability](#qr4-usability)
    - [QR5: Scalability](#qr5-scalability)
    - [QR6: Maintainability](#qr6-maintainability)
    - [QR7: Portability](#qr7-portability)
    - [QR8: Compliance](#qr8-compliance)
  - [Contraints](#contraints)
    - [C1: Platform \& Deployment Constraints](#c1-platform--deployment-constraints)
    - [C2: Privacy \& Security Constraints](#c2-privacy--security-constraints)
    - [C3: Usability Constraints](#c3-usability-constraints)
    - [C4: Domain Constraints](#c4-domain-constraints)
    - [C6: Budget \& Resource Constraints](#c6-budget--resource-constraints)
    - [C7: Timeline Constraints](#c7-timeline-constraints)


## 1. Introduction

FILL THIS IN

## 2. Architectural Requirements

### 2.1 Architectural Pattern
---

Supa-Neighbour employs a **Client-Server Architecture** with the following components:

a) **Frontend (Flutter)** — the mobile client used by residents and helpers to interact with the platform (post tasks, browse helpers, chat, view trust scores and gamification progress).

b) **Backend (Spring Boot)** — the central server, which internally follows a **Layered Architecture** (Controller → Service → Repository → DTO) to separate request handling, business logic, and data access.

c) **Communication** — the frontend and backend communicate via **REST APIs**, keeping the client and server decoupled and independently deployable.

d) **Database (Azure Database for PostgreSQL)** — a centralised database used for both read and write operations, acting as the single source of truth for tasks, users, ratings, and chat data.

---

#### 2.1.1 Reasons for Choosing This Architecture
---

a) **Separation of concerns.** Splitting the system into a client and a server keeps presentation, business logic, and data access cleanly isolated from one another.

b) **Multiple, independent clients need to hit the same backend (admin and user).** The platform supports two distinct kinds of clientsover its lifetime:
   - The **Flutter user app**, used by residents and helpers.
   - The **admin web interface**, used for moderation, verification oversight, and platform management.
  
---

### 2.2 Constraints on the Architecture
---

a) **Azure compatibility requirement.** All chosen technologies had to be compatible with, and provisionable within, the Azure ecosystem (App Service, Azure Database for PostgreSQL, Blob Storage, Key Vault, Container Registry), limiting technology choices to what integrates cleanly with Azure-native services.

b) **Security and compliance standards.**
   - **POPIA compliance**: the system must protect personally identifiable information in line with South Africa's data protection law, driving decisions such as AES-256 column-level encryption for sensitive fields.
   - **Verified-user access only** :the platform must ensure only authenticated, verified users can access the app, enforced through Firebase Authentication and the Admin SDK, rather than a custom-built authentication system.

c) **Mandatory cloud deployment target.** The application is required to be deployed to Azure specifically (rather than any general cloud provider), constraining infrastructure decisions to Azure's available services, deployment models (App Service, ACR-based CI/CD), and regional availability.

d) **Mandated authentication provider**: Firebase Authentication is fixed as the identity provider, meaning the backend cannot own credential storage and must integrate via the Admin SDK.

### 2.3 Quality Requirements


## 3. Technology Requirements

## 4. API Contract

Please follow this link to view the API Contract: [API Contract](/documentation/API_Service_Contract.md)

## 5. Deployment


# Architectural Requirements

## Quality Requirements

Quality requirements define the system's non-functional characteristics. Each requirement is traced to specific NFRs.

### QR1: Performance

QR1.1: Matching engine response time should be  ≤ 3 seconds based on NFR1.1.1

QR1.2: Chat message delivery latency should be ≤ 2 seconds based on NFR1.1.2

QR1.3: Concurrent user support should be be ≥ 1,000 users based on NFR1.2.1

QR1.4: Concurrent chat sessions roughly ≥ 500 sessions based on NFR1.2.2

---
### QR2: Security & Privacy

QR2.1: Password storage ,Hashed + salted (bcrypt/Argon2) based on NFR2.1.1

QR2.4: Authentication via JWT via Microsoft Entra ID B2C based on AR3.1

QR2.5: API rate limiting of roughly 50 requests/min/per user based on NFR2.2.1

QR2.6: Spam detection for Automated flagging + admin review queue based on NFR2.2.2

QR2.7: Address privacy which Revealed only post-task-acceptance based on R4.1.1

QR2.8: Contact privacy,no exposure to any 3rd party; all communication in-app based on R4.1.2

QR2.9: Security audit logging, All failed logins + suspicious activities logged for admin tech-team review based on NFR2.2.3

---
### QR3: Reliability & Availability

QR3.1: System uptime, 99.5% based on NFR3.1.1

QR3.2: Critical service failover which is Automated, < 30 seconds detection + recovery based on NFR3.1.2

QR3.3: Matching engine failure recovery, User-friendly error + auto-retry within 10 seconds based on NFR3.2.1

QR3.4: Chat message queuing, Messages persisted if service down; delivered on restore based on NFR3.2.2

QR3.5: Data backup frequency | Daily (Cosmos DB), hourly transaction logs (Azure SQL)

---
### QR4: Usability

QR4.1: First task posting time of ≤ 3 minutes from registration based on NFR4.1.1

QR4.2: Mobile accessibility should allow for Adjustable text size based on NFR4.1.2 & R7.1.2

QR4.3: Icon clarity, All icons include text labels based on NFR4.1.3

QR4.4: Error messaging, Plain language + resolution suggestions based on NFR4.2.1

QR4.5: Task workflow guidance, Step-by-step with progress indicators based on NFR4.2.2

QR4.6: Age group support Intuitive for users 18-80+ based on R7.1.1

---
### QR5: Scalability

QR5.1: Horizontal scaling with Zero-downtime instance addition based on NFR5.1.1 & AR6.1

QR5.2: Database partitioning trigger for ≥ 100,000 users based on NFR5.1.2

QR5.3: Neighbourhood zone configurability, No code changes for new regions based on NFR5.2.1

QR5.4: Proximity algorithm efficiency, prefferably O(log n) or better for zone lookup based on NFR5.2.2

---
### QR6: Maintainability


QR6.1: Module coupling, Loosely coupled based on NFR6.1.1 & AR7

QR6.2: Layer separation with Clear boundaries: Presentation → API → Service → Data based on NFR6.1.2 & AR1

QR6.3: API documentation, OpenAPI/Swagger for all REST + GraphQL endpoints based on NFR6.2.1

QR6.4: Logging completeness on Errors, warnings, key user actions with timestamps based on NFR6.2.2

---
### QR7: Portability

QR7.1: Azure compatibility, All components deployable to Azure based on NFR7.1.1 & AR4

QR7.2: CI/CD support via the GitHub Actions pipelines based on NFR7.1.2 & AR5

QR7.3: Android minimum version | API level 30 (Android 11) based on NFR7.2.1

QR7.4: Screen size adaptation, Phones + tablets, portrait + landscape based on NFR7.2.2

---
### QR8: Compliance

QR8.1: POPIA compliance with Explicit consent for location data based on NFR8.1.1

QR8.2: Right to deletion, User data deletion request supported based on NFR8.1.2

QR8.4: Privacy policy which is Accessible during registration based on NFR8.2.2

---


## Contraints

### C1: Platform & Deployment Constraints

C1.1: Android app required (iOS optional), Flutter must support Android API 30+

C1.2: Web dashboard for admins

C1.3: Backend must deploy to Azure

C1.4: CI/CD via GitHub Actions

C1.5: Matching engine uses GraphQL

---
### C2: Privacy & Security Constraints

C2.1: Exact address revealed ONLY after task acceptance

C2.2: Personal contact numbers never exposed, All communication via in-app chat only

C2.3: Location data requires explicit consent, Consent screen + preference storage

C2.4: POPIA compliance, Users must havd Right to deletion & data processing records

C2.5: Secrets in Azure Key Vault, No hardcoded secrets in code/config

---
### C3: Usability Constraints

C3.1: Verification must remain simple, OTP only; no complex document uploads initially

C3.2: Large fonts + accessible design, Flutter text scaling must work

C3.3: First task within 3 minutes of registration,Onboarding must be minimal

---
### C4: Domain Constraints

C4.1: Neighbourhood boundaries differ per region, Configuration-driven, not hardcoded

C4.2: Messaging must prevent harassment/spam, Content filtering + rate limiting + reporting

C4.3: Animal tasks require disclaimers, Safety guidance before accepting pet tasks

---

### C6: Budget & Resource Constraints

C6.1: Approval needed from Gendac for expenses,Any paid Azure service requires sign-off

C6.2: Team size = 5, limited to 5 tech memmbers

---
### C7: Timeline Constraints

C7.1: Delivery within Project deadlines which we  Prioritise core (R1-R7) over wow factor (R8-R10)