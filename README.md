# SupaNeighbour
## Parse&Co in conjunction with Gendac ## 

> A community-driven platform that connects people who are in need with those willing to assist the people of within their neighbourhood.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=coverage)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=bugs)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![API Build](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/backend.yml/badge.svg)](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/backend.yml)
[![App Build](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/frontend-app.yml/badge.svg)](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/frontend-app.yml)
[![Website Build](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/frontend-admin.yml/badge.svg)](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/frontend-admin.yml)
[![GitHub Issues](https://img.shields.io/github/issues/COS301-SE-2026/Supa-Neighbour)](https://github.com/COS301-SE-2026/Supa-Neighbour/issues)

[![dependencies](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen)](https://github.com/COS301-SE-2026/Supa-Neighbour)

![Uptime](https://img.shields.io/badge/Uptime-Pending%20Deployment-lightgrey)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Flutter](https://img.shields.io/badge/Flutter-3.x-blue?logo=flutter)
![Azure](https://img.shields.io/badge/Azure-Cloud-blue?logo=microsoftazure)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=springboot)
![Node](https://img.shields.io/badge/Node.js-20-green?logo=nodedotjs)

---

## Table of Contents

- [SupaNeighbour](#supaneighbour) <!-- omit in doc  -->
  - [Parse\&Co in conjunction with Gendac](#parseco-in-conjunction-with-gendac)
  - [Table of Contents](#table-of-contents)
  - [Overview](#overview)
  - [Downlading apk](#downloading-the-apk)
  - [Relevant Links/Documents](#relevant-linksdocuments)
  - [Project Structure](#project-structure)
  - [Team and Information](#team-and-information)
  - [Contact](#contact)

---

## Overview

**The SupaNeighbour system** aims to create a secure, community-driven platform where residents can request and provide short-term assistance for small household. Modernised in a way such that helping others is both good for them and you

**Enables The Community to:**

* People to request help with tasks.
* Volunteers or skilled individuals to offer assistance.
* Smart matching based on compatibility, location, and skills.
* create a connecting that is much closer and the gamification and sleack design connects the youth and the elders.

---

## Downloading the APK

> ⚠️ **Android only.** The SupaNeighbour app is currently only available for Android devices. There is no iOS build at this time.

To install the app:

1. Go to the [Landing Page](https://green-beach-06bbfcd03.7.azurestaticapps.net/).
2. Click the **Get Started** button to begin the download.
3. Depending on your browser, you may see security warnings before the download completes (e.g. "This file can be harmful," or Windows SmartScreen/Google Safe Browsing prompts). This is expected for APKs downloaded outside the Play Store — click through the browser's warning to allow the download.
4. Once downloaded, open the APK file on your Android device. You may need to enable **"Install from unknown sources"** in your device settings if prompted.
5. Follow the on-screen instructions to complete installation.

---

## Relevant Links/Documents

| Name | Description | Link |
|------|--------------|------|
| Project Board | GitHub Projects board tracking sprints, issues, and progress | [View](https://github.com/orgs/COS301-SE-2026/projects/34) |
| API Contract | Specification of backend API endpoints and request/response formats | [View API Contract](/documentation/Demo%202%20Files/API_Service_Contract.md) |
| SAS | Software Architecture Specification document | [View SAS](/documentation/Demo%202%20Files/SAS%20Documentation.md) |
| SRS | Software Requirements Specification document | [View SRS](/documentation/Demo%202%20Files/SRS%20Document.md) |
| Testing Policy | Team's testing standards and coverage requirements | [View Testing Policy](/documentation/Demo%202%20Files/Testing_Policy.md) |
| Coding Standards Doc | Team's coding conventions and style guide | [View Coding Standards](/documentation/Demo%202%20Files/Coding_Standards_Document.md) |
| Architecture Diagram | Architectural Diagram of the system | [View Diagram](/documentation/Demo%202%20Files/Images/Architecture%20diagram%20V2.png)
| Landing Page | SupaNeighbour's Landing Page | https://green-beach-06bbfcd03.7.azurestaticapps.net/ |
| Style Guide | SupaNeighbour's style Guide | https://red-pebble-0a5f86903.7.azurestaticapps.net/ |
| Live Backend | The deployed backend to Azure | https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net/ |
| Admin Page | Deployed Admin Page| https://red-rock-009e74b03.3.azurestaticapps.net |

> Please note that the above link to the live backend will lead you to meet with a white label error page. If you would like to confirm that the backend is live, please use the following api to test:
https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net/api/users

> You will be met with a list of all users in json/xml format

---


## Project Structure

**Backend (Spring Boot)**
```
src/main/java/com/app/api/
├── config/
├── controllers/
├── services/
├── repositories/
├── dtos/
├── model/
└── security/
src/main/resources/
├── application.yml
├── application-azure.yml
└── Firebase/
```

**Frontend (Flutter)**
```
frontend/
├── lib/
│   ├── main.dart
│   ├── screens/
│   │   ├── auth/
│   │   ├── home/
│   │   ├── tasks/
│   │   ├── chat/
│   │   ├── leaderboard/
│   │   └── profile/
│   ├── widgets/
│   │   └── achievements/
│   ├── services/
│   ├── models/
│   ├── providers/
│   ├── constants/
│   └── components/
├── assets/
│   └── screenshots/
├── test/
├── pubspec.yaml
└── README.md
```

---
## Team Information

| **Team Member** | **Role** | **Profile** | **Tools** | **LinkedIn** |
|-----------------|----------|-------------|-----------|--------------|
| **Blessing Gibendi** | Team Lead | Third-year Computer Science student at the University of Pretoria with a solid foundation in Java, Python, JavaScript, Angular, React, and databases. A self-taught Python developer, she quickly adapts to new tools and frameworks and leads the team with a calm, methodical approach. With a strong interest in AI, she is using this project to strengthen both her leadership and application development skills. | Python, C++, Angular, React, Java, JavaScript, TypeScript, PHP, PostgreSQL | [LinkedIn](https://www.linkedin.com/in/blessing-gibendi-774556272) |
| **David Ekele Kalu** | Tester | Third-year Computer Science student with practical experience in C++, Java, TypeScript, Python, and PHP. He has taken ownership of application flow and architecture across multiple projects and has built and deployed WordPress websites from frontend to hosting. A resourceful researcher, he enjoys solving unfamiliar problems efficiently. | C++, Java, JavaScript, PHP, Python | [LinkedIn](https://www.linkedin.com/in/david-kalu-504150402/) |
| **Divo Kohler** | Data Engineer / Service Engineer | Third-year Computer Science student at the University of Pretoria with strong backend and database expertise. Experienced in SQL, NoSQL, Java backend development, concurrent systems, Hibernate, Spring Boot, API management, and UI design for intuitive applications. | Java, C++, PostgreSQL, MariaDB, MongoDB, Spring Boot, Lombok, Node.js | [LinkedIn](https://www.linkedin.com/in/divo-kohler-1023b6397/) |
| **Amantle Keamogetse Temo** | Integration Engineer | Third-year Computer Science student with experience in relational, NoSQL, and NewSQL databases, data warehousing, OLAP, big data, machine learning, software engineering, concurrency, AI, networking, and modern full-stack web development. | Python, Java, C++, JavaScript, TypeScript (Angular), R, PHP, PostgreSQL, MongoDB, Neo4j, SAS | [LinkedIn](https://www.linkedin.com/in/amantle-temo-54bb6b369) |
| **Michelle W Njoroge** | UI Engineer / Designer | Third-year Computer Science student at the University of Pretoria specializing in UI engineering and design. Experienced in responsive frontend development, Java backend development, and UI/UX design using Figma and Canva, with additional experience in React, Vue.js, Angular, and C#. | Java, C++, JavaScript, TypeScript, HTML & CSS, Python (NumPy, SymPy), Node.js, PHP, C#, React, Vue.js, Angular, Figma, Canva, Git | [LinkedIn](https://www.linkedin.com/in/michelle-njoroge-12264a209) |