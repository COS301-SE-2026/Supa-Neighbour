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