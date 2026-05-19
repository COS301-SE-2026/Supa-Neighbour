# Tech Stack & Functional Requirements
**Tech used-to-Requirement Mapping**

---


## Tech Stack
| Layer | Technology |
|-------|------------|
| Frontend | Flutter |
| Backend API | Java / Spring Boot |
| Background Jobs / Functions | C# / .NET |
| AI / Computer Vision | Python |
| Database | PostgreSQL |
| Notifications | TypeScript |

---

## R1: User Account Management

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R1.1 - Register via email/phone | Backend API | Java / Spring Boot |
| R1.1.1 - OTP verification | Backend API + Notifications | Java + TypeScript |
| R1.1.2 - Residential address for neighbourhood zone | Backend API | Java / Spring Boot |
| R1.2 - Login & profile management | Backend API | Java / Spring Boot |
| R1.2.1 - Password auth + strength validation | Backend API | Java / Spring Boot Security |
| R1.2.2 - Account lockout + password reset | Backend API | Java / Spring Boot Security |
| R1.2.3 - View/edit trust score, skills, availability | Backend API | Java / Spring Boot |
| UI for all of R1 | Frontend | Flutter |

---

## R2: Task Request and Matching Engine

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R2.1 - Create and publish task requests | Backend API | Java / Spring Boot |
| R2.1.1 - Task types | Backend API + DB | Java + PostgreSQL |
| R2.1.2 - Task date, time window, instructions | Backend API + DB | Java + PostgreSQL |
| R2.2 - Match requests with helpers | Matching Engine | Java / Spring Boot (**GraphQL - Netflix DGS) |
| R2.2.1 - Proximity-based matching | Matching Engine | Java + PostgreSQL |
| R2.2.2 - Trust score, availability, skills | Matching Engine | Java / Spring Boot |
| R2.2.3 - Filter helpers by preference | Matching Engine | Java / Spring Boot |
| UI for all of R2 | Frontend | Flutter |

---

## R3: In-App Communication

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R3.1 - Real-time chat | Backend API | Java / Spring Boot + Azure SignalR |
| R3.1.1 - Text messaging | Backend API | Java / Spring Boot |
| R3.1.2 - Photo sharing | Backend API + Storage | Java + Azure Blob Storage |
| R3.2 - Task status updates | Backend API | Java / Spring Boot |
| R3.2.1 - Push notifications | Azure Functions | TypeScript |
| R3.2.2 - Mark task complete with photo | Backend API | Java / Spring Boot |
| UI for all of R3 | Frontend | Flutter |

---

## R4: Security and Privacy

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R4.1.1 - Address revealed only after acceptance | Backend API | Java / Spring Boot |
| R4.1.2 - No contact number exposure | Backend API | Java / Spring Boot |
| R4.2.1 - Location permission controls | Frontend | Flutter |
| R4.2.2 - RBAC (resident, helper, admin) | Backend API | Java / Spring Boot Security |

---

## R5: Gamification and Trust Scoring

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R5.1.1 - XP points on task completion | Backend API | Java / Spring Boot |
| R5.1.2 - Bronze -> Silver -> Gold progression | Backend API + DB | Java + PostgreSQL |
| R5.2.1 - Monthly achievements | Background Jobs | C# / .NET |
| R5.2.2 - Neighbourhood leaderboard | Backend API + DB | Java + PostgreSQL |
| R5.3.1 - Rate helpers after task | Backend API | Java / Spring Boot |
| R5.3.2 - Calculate and display trust scores | Backend API + DB | Java + PostgreSQL |
| UI for all of R5 | Frontend | Flutter |

---

## R6: Neighbourhood Zones and Mapping

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R6.1.1 - Define zones by area type | Backend API + DB | Java + PostgreSQL |
| R6.1.2 - Assign users to zones | Backend API | Java / Spring Boot |
| R6.2.1 - Interactive map with tasks/helpers | Frontend + Backend | Flutter + Java |
| R6.2.2 - Task type icons on map | Frontend | Flutter |

---

## R7: UI and Accessibility

| Sub-requirement | Layer |  Tech |
|-----------------|-------|-----------------|
| R7.1.1 - Simple task posting interface | Frontend | Flutter |
| R7.1.2 - Large fonts, intuitive navigation | Frontend | Flutter |
| R7.2.1 - Ratings and badges on profiles | Frontend | Flutter |
| R7.2.2 - Scheduling with reminders | Frontend + Notifications | Flutter + TypeScript |

---

## R8–R10: Wow Factors (If Time Permits)

| Requirement | Layer |  Tech |
|-------------|-------|-----------------|
| R8 - AI task suggestions | AI Layer | Python |
| R9 - Computer vision | AI Layer | Python |
| R10 - Community bulletin | Backend API + Frontend | Java + Flutter |

---

## Summary::

| Language | Responsible For |
|----------|-----------------|
| Flutter | All UI - mobile app + web admin dashboard |
| Java / Spring Boot | Core backend API, matching engine, auth, chat, trust scoring, gamification |
| TypeScript | Push notifications, reminders |
| C# / .NET | Background jobs - monthly achievements, scheduled tasks |
| Python | AI task suggestions, computer vision |
| PostgreSQL | All data storage - users, tasks, ratings, neighbourhoods, chat logs |