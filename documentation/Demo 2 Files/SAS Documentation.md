# Software Architecture Specifications - Supa-Neighbour


## Table of Contents

- [Software Architecture Specifications - Supa-Neighbour](#software-architecture-specifications---supa-neighbour)
  - [Table of Contents](#table-of-contents)
  - [1. Introduction](#1-introduction)
  - [2. Architectural Requirements](#2-architectural-requirements)
    - [2.1 Architectural Pattern](#21-architectural-pattern)
      - [2.1.1 Reasons for Choosing This Architecture](#211-reasons-for-choosing-this-architecture)
  - [2.2Architectural Diagram](#22architectural-diagram)
    - [2.3 Constraints on the Architecture](#23-constraints-on-the-architecture)
  - [3. Technology Requirements](#3-technology-requirements)
  - [4. API Contract](#4-api-contract)
  - [5. Deployment Requirements](#5-deployment-requirements)
    - [Environment Parity](#environment-parity)
    - [Secrets Management](#secrets-management)
    - [Rollback Strategy](#rollback-strategy)
    - [Deployment Diagram](#deployment-diagram)


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

## 2.2Architectural Diagram

Please refer to this for the Architectural Diagram: [Architectural Diagram](Images/Architecture%20diagram%20V2.png)

### 2.3 Constraints on the Architecture
---

a) **Azure compatibility requirement.** All chosen technologies had to be compatible with, and provisionable within, the Azure ecosystem (App Service, Azure Database for PostgreSQL, Blob Storage, Key Vault, Container Registry), limiting technology choices to what integrates cleanly with Azure-native services.

b) **Security and compliance standards.**
   - **POPIA compliance**: the system must protect personally identifiable information in line with South Africa's data protection law, driving decisions such as AES-256 column-level encryption for sensitive fields.
   - **Verified-user access only** :the platform must ensure only authenticated, verified users can access the app, enforced through Firebase Authentication and the Admin SDK, rather than a custom-built authentication system.

c) **Mandatory cloud deployment target.** The application is required to be deployed to Azure specifically (rather than any general cloud provider), constraining infrastructure decisions to Azure's available services, deployment models (App Service, ACR-based CI/CD), and regional availability.

d) **Mandated authentication provider**: Firebase Authentication is fixed as the identity provider, meaning the backend cannot own credential storage and must integrate via the Admin SDK.

## 3. Technology Requirements


**Flutter — Frontend (Mobile Application)**

- Single codebase targets both iOS and Android, saving development time for a small team on a fixed timeline
- Widget-based architecture supports the accessible, large-font, intuitive UI required by R6
- Built-in testing framework supports the unit/widget test coverage tracked under the Maintainability NFR
  
**Spring Boot + Docker — Backend**

- Mature ecosystem for building secure, RESTful, role-based APIs, supporting R5.2.2 (role-based access control for resident, helper, admin)
- Docker packaging decouples the backend from the host environment, ensuring consistent behaviour between WSL2 development and production
- Supports the automatic-restart recovery strategy described under the Reliability NFR
  
**Firebase — Authentication (Login & Registration)**
- Provides email verification, password strength enforcement, and account lockout out of the box
- Satisfies R1.1.1 (email verification) and R1.2.1 (password strength validation)
- Removes the need to build and maintain custom authentication/credential-storage logic, reducing security risk
  
**Azure Database for PostgreSQL (Flexible Server) — Database**
- Chosen over Cosmos DB because the data (users, tasks, ratings, trust scores) is inherently relational, with clear foreign-key relationships
- Provides automated backups with a seven-day retention period, supporting the Reliability NFR's recovery targets
  
**Azure Blob Storage — Media Storage**
- Stores all user-uploaded images, including task completion photo evidence and in-app chat photo updates.
- Configured with Locally Redundant Storage (LRS), keeping multiple copies of files within the Azure region
- Supports the Reliability NFR's protection against hardware failure
  
**Azure Container Registry (ACR)**
- Stores versioned Docker images produced by the CI/CD pipeline before deployment
- Integrates natively with GitHub Actions and Azure App Service, allowing a merged change to move from build to deployment without manual intervention
- Supports the 2-hour deployability target in the Maintainability NFR
  
**Azure App Service (Web App)**

- Hosts the live backend container on a fully managed platform with a documented uptime SLA
- Built-in support for restart policies
- Native integration with Key Vault (secrets) and Application Insights (monitoring), supporting the Reliability and Availability NFRs

## 4. API Contract

Please follow the following link to the API Contract: [API Contract](../Demo%202%20Files/API_Service_Contract.md)

## 5. Deployment Requirements

### Environment Parity
 
This project distinguishes between two environments:
 
- **Development (local):** configured via `application.yml`, connects to a local Postgres instance for development/testing without touching cloud resources.
- **Production (Azure):** configured via `application-azure.yml`, activated by setting `SPRING_PROFILES_ACTIVE=azure`. Connects to Azure Database for PostgreSQL Flexible Server and Azure Blob Storage.
A separate staging environment was scoped out of this project by agreement with the course requirement owner, due to constraints of the free-tier Azure resources available to the team.
 
The `main` branch deploys automatically to the production environment via GitHub Actions (see `.github/workflows/backend.yml`).
 
### Secrets Management
 
- **GitHub Secrets:** used for Azure-related credentials needed during CI/CD, such as the username and password required to push the Docker image to Azure Container Registry.
- **Azure Key Vault:** used for backend runtime secrets, such as the database password, keeping them out of source control and environment config files.


### Rollback Strategy
//FILL THIS


### Deployment Diagram

//FILL THIS AS WELL


