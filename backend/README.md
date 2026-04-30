# Supa-Neighbour — Backend API

This is the Spring Boot backend for the Supa-Neighbour project. It serves as the core API for the mobile app and web admin dashboard, handling business logic, data access, authentication, and real-time messaging.

---

## What this service does

- Exposes a REST API consumed by the Flutter frontend
- Exposes a GraphQL API (via Netflix DGS) for the matching engine
- Handles authentication via Microsoft Entra ID B2C
- Reads and writes to Azure Cosmos DB (posts, feeds, notifications) and Azure SQL (users, groups, memberships)
- Connects to Azure SignalR for real-time messaging
- Documents all endpoints via Swagger UI

---

## Prerequisites

Before running the backend, make sure you have the following installed on your machine:

| Tool | Version | Check |
|---|---|---|
| Java JDK | 21 | `java -version` |
| Maven | 3.x | `mvn -version` |
| Git | Any | `git --version` |
| Docker Desktop | Any | `docker --version` |

> Docker is required for running integration tests via Testcontainers. It does not need to be running for basic development.

---

## First time setup

### Step 1 — Clone the repository

If you have not already cloned the repo:

```bash
git clone https://github.com/YOUR-USERNAME/Supa-Neighbour.git
cd Supa-Neighbour/backend
```

---

### Step 2 — Set up your local environment variables

The backend requires environment variables for database connections, Azure services, and secrets. These are never committed to the repository for security reasons.

Create a local config file:

```bash
cp src/main/resources/application.yml src/main/resources/application-local.yml
```

Open `application-local.yml` and fill in your local values:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=supadb
    username: your_db_username
    password: your_db_password

  data:
    cosmos:
      uri: your_cosmos_db_uri
      key: your_cosmos_db_key
      database: supadb

azure:
  active-directory:
    tenant-id: your_tenant_id
    client-id: your_client_id
```

> Ask a team lead for the local development values. Production secrets are stored in Azure Key Vault and are never shared directly.

---

### Step 3 — Install dependencies and build

```bash
mvn clean install
```

This downloads all dependencies declared in `pom.xml` and compiles the project. A successful build will show:

```
BUILD SUCCESS
```

---

### Step 4 — Run the backend locally

```bash
mvn spring-boot:run
```

The API will start on:

```
http://localhost:8080
```

---

### Step 5 — Verify it is running

Open your browser and go to:

```
http://localhost:8080/swagger-ui
```

You should see the Swagger UI showing all available API endpoints. If this page loads, the backend is running correctly.

---

## Folder structure

```
backend/
└── src/
    ├── main/
    │   ├── java/com/app/api/
    │   │   ├── Application.java         ← entry point
    │   │   ├── config/                  ← app configuration (Swagger, Security, etc.)
    │   │   ├── controllers/             ← REST API endpoints
    │   │   ├── services/                ← business logic
    │   │   ├── repositories/            ← database access (JPA)
    │   │   ├── models/                  ← entity classes (User, Post, etc.)
    │   │   └── graphql/                 ← Netflix DGS GraphQL resolvers
    │   └── resources/
    │       ├── application.yml          ← base config (committed)
    │       └── application-local.yml    ← local overrides (never committed)
    └── test/
        ├── unit/                        ← JUnit 5 + Mockito unit tests
        └── integration/                 ← Spring Boot Test + Testcontainers tests
```

---

## Common commands

| Command | What it does |
|---|---|
| `mvn clean install` | Builds the project and runs all tests |
| `mvn spring-boot:run` | Starts the backend server locally on port 8080 |
| `mvn test` | Runs unit and integration tests only |
| `mvn clean` | Clears compiled output — useful when build behaves strangely |
| `mvn dependency:resolve` | Downloads and verifies all dependencies |

---

## API documentation

Once the server is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui
```

The raw OpenAPI JSON spec (useful for generating client SDKs) is at:

```
http://localhost:8080/api-docs
```

---

## Running tests

**Unit tests only:**
```bash
mvn test -Dtest="**/unit/**"
```

**Integration tests** (requires Docker to be running):
```bash
mvn test -Dtest="**/integration/**"
```

**All tests:**
```bash
mvn test
```

---

## Troubleshooting

**`Unable to find main class` on mvn clean install**
Make sure `Application.java` exists at `src/main/java/com/app/api/Application.java` with the `@SpringBootApplication` annotation.

**`package org.springframework... does not exist`**
Your `pom.xml` is missing the Spring Boot parent or web starter dependency. Check that both are present — refer to the `pom.xml` in the repository.

**`Incorrect Package` error in a Java file**
The `package` declaration at the top of the file must match the folder path starting from `com/`. For example a file at `com/app/api/config/` must declare `package com.app.api.config;` — never include `src/main/java` in the package name.

**Port 8080 already in use**
Another process is using port 8080. Either stop that process or run the backend on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```