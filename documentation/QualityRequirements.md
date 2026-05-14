## Quality Requirements (Derived from NFRs)

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