# Architectural Requirements

## AR1: System Layering
The system shall follow a layered architecture with clear separation 
between the presentation, api, service, and data layers.

  AR1.1: The presentation layer shall consist of:
    - A Flutter android mobile application
    - A Flutter web admin dashboard hosted on azure static web apps

  AR1.2: The api layer shall consist of a spring boot rest api 
  deployed on azure app service.

  AR1.3: The service layer shall consist of azure Functions handling:
    - Push notification dispatching (TypeScript)
    - Background jobs (C# / .NET 9) **might consider alternatives like  TypeScipt still as its supported by azure functions 
    - Optional AI/CV inference (Python)

  AR1.4: The data layer shall consist of:(divo please confirm if the below is fine as in where there any changes regarding the db)
    - Azure Cosmos DB (primary NoSQL store) 
    - Azure SQL Database (relational store)
    - Azure Blob Storage (photo storage)

## AR2: Communication Architecture
The system shall use appropriate communication protocols per use case.

  AR2.1: The mobile app and web dashboard shall communicate with the 
  backend via REST over HTTPS.

  AR2.2: The matching engine shall expose a GraphQL API using 
  Netflix DGS on Spring Boot.

  AR2.3: Real-time chat shall use Azure SignalR Service via 
  WebSocket connections.

  AR2.4: Notifications shall be dispatched via Azure Notification Hubs, 
  triggered by Azure Functions queue triggers.

## AR3: Security Architecture
The system shall enforce security at every layer.

  AR3.1: Authentication shall be handled by Microsoft Entra ID B2C 
  using JWT tokens.

  AR3.2: Role-based access control shall be enforced via 
  Spring Security using roles: resident, helper, admin.

  AR3.3: Exact user addresses shall only be revealed to a helper 
  after task acceptance.

  AR3.4: Personal contact numbers shall never be exposed between 
  users.

  AR3.5: All secrets shall be managed via Azure Key Vault.

## AR4: Deployment Architecture
The system shall be fully deployed on Microsoft Azure.

  AR4.1: Backend API -> Azure App Service
  AR4.2: Flutter Web Dashboard -> Azure Static Web Apps
  AR4.3: Azure Functions -> Notifications + Jobs function apps
  AR4.4: Databases -> Azure Cosmos DB + Azure SQL
  AR4.5: File storage -> Azure Blob Storage + Azure CDN
  AR4.6: Maps -> Azure Maps

## AR5: CI/CD Architecture
The system shall use GitHub Actions for automated build, test, 
and deployment pipelines.

  AR5.1: All pushes to the `dev` branch shall trigger CI for all components.

  AR5.2: All merges to `main` shall trigger CD for all components.

  AR5.3: E2E tests using Playwright shall run on all PRs 
  to `dev` and `main`.

  AR5.4: Deployment secrets shall be stored as GitHub Actions 
  secrets.

## AR6: Scalability Architecture
The system shall be designed to scale horizontally on Azure.

  AR6.1: The Spring Boot backend shall support horizontal scaling 
  via Azure App Service scale-out rules.

  AR6.2: Azure Functions shall scale automatically based on 
  queue depth and trigger frequency.

  AR6.3: Azure Cosmos DB shall support partitioning as the 
  user base grows as per NFR5.1.2.

## AR7: Modularity
The backend shall be divided into loosely coupled modules 
that can be updated independently as per NFR6.1.1:
  - User Management
  - Task Management
  - Matching Engine 
  - Chat 
  - Notifications
  - Gamification / Trust Scoring
  - Neighbourhood / Geolocation
