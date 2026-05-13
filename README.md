# SupaNeighbour
## Parse&Co in conjunction with Gendac ## 

> A community-driven platform that connects people who are in need with those willing to assist the people of within their neighbourhood.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=coverage)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=COS301-SE-2026_Supa-Neighbour&metric=bugs)](https://sonarcloud.io/summary/new_code?id=COS301-SE-2026_Supa-Neighbour)
[![API Build](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/backend.yml/badge.svg)](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/backend.yml)
[![Flutter Build](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/frontend.yml/badge.svg)](https://github.com/COS301-SE-2026/Supa-Neighbour/actions/workflows/flutter.yml)
[![GitHub Issues](https://img.shields.io/github/issues/COS301-SE-2026/Supa-Neighbour)](https://github.com/COS301-SE-2026/Supa-Neighbour/issues)

[![dependencies](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen)](https://github.com/COS301-SE-2026/Supa-Neighbour)

![Uptime](https://img.shields.io/badge/Uptime-Pending%20Deployment-lightgrey)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Flutter](https://img.shields.io/badge/Flutter-3.x-blue?logo=flutter)
![Azure](https://img.shields.io/badge/Azure-Cloud-blue?logo=microsoftazure)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=springboot)
![Flutter](https://img.shields.io/badge/Flutter-3.x-blue?logo=flutter)
![Node](https://img.shields.io/badge/Node.js-20-green?logo=nodedotjs)

---
q
## Table of Contents

* [Overview](#-overview)
* [Features](#-features)
* [Tech Stack](#-tech-stack)
* [Team and Information](#-team-and-information)
* [Contact Information](#-contact)

---

## Overview

**The SupaNeighbour system** aims to create a secure, community-driven platform where residents can request and provide short-term assistance for small household. Modernised in a way such that helping others is both good for them and you

## Enables The Community to:
* People to request help with tasks.
* Volunteers or skilled individuals to offer assistance.
* Smart matching based on compatibility, location, and skills.
* create a connecting that is much closer and the gamification and sleack design connects the youth and the elders.

---

## Features

* User roles: Admin, Helper, Dependent
* Location-based task matching
* Compatibility scoring system
* Badge & XP system for Helpers
* Rating & review system
* Analytics tracking
* Task tracking

---

## Tech Stack

**Frontend**

* Mobile App: Flutter (Dart)

* Web Admin Dashboard: Flutter Web on Azure  Static Web Apps (Dart)

* Maps & Geolocation: Azure Maps + flutter_map (Dart + Java) 


**Backend**

* Main API: Spring Boot on Azure App Service (Java)

* Matching Engine (GraphQL): Netflix DGS on Spring Boot (Java)

* Admin Dashboard API Extensions: Express/Fastify * microservice (TypeScript) - optional



**Database**

* Primary Data Store: Azure Cosmos DB (NoSQL)

* Relational Data Store: Azure SQL Database (SQL)

* Relative API connection SpringBoot/Java


**Security**

* Secrets Management: Azure Key Vault

* Authentication: Microsoft Entra ID B2C


**DevOps & CI/CD**

* CI/CD: GitHub + GitHub Actions

**Other Tools**

* Git & GitHub
* Docker (optional)

---

## Extra Links
* [Project Board](https://github.com/orgs/COS301-SE-2026/projects/34)
* [Functional Requirements](https://docs.google.com/document/d/1PVcbys8ZG97wmAtsVa-1X8_czOlywJprGr-_2dYto4w/edit?usp=sharing)
* [Capstone Demo instructions](https://drive.google.com/file/d/14-R9XofWrY65djt_qI9ZB4H_m_geZqdD/view?usp=drive_link)
* [Database Domain Model](https://docs.google.com/document/d/1nC0Un50nfuOG_E8rg6VxcGCwM4UnnsT5mpWU7TS6gRs/edit?usp=sharing)
---


## Project Structure

```
my-social-app/
├── .github/            # CI/CD workflows
├── api/                # Spring Boot backend
├── flutter/            # Mobile + Web Frontend
├── functions/          # Azure serverless functions
├── e2e/                # End-to-end Testing
├── documenation/       # Documentation   
├── .gitignore
└── README.md
```

---
## Team and Information

* Blessing Gibendi: Team Lead

  [LinkedIn](https://www.linkedin.com/in/blessing-gibendi-774556272?utm_source=share_via&utm_content=profile&utm_medium=member_android)
* David Ekele Kalu: Tester

  [LinkedIn](https://www.linkedin.com/in/david-kalu-504150402/)
* Divo Kohler: Data Engineer/Service Engineer

  [LinkedIn](https://www.linkedin.com/in/divo-kohler-1023b6397/)
* Amantle Keamogetse Temo: Intergration Enigeer

  [LinkedIn](https://www.linkedin.com/in/blessing-gibendi-774556272?utm_source=share_via&utm_content=profile&utm_medium=member_android)
* Michelle W Njoroge: UI Engineer/Designer

  [LinkedIn](https://www.linkedin.com/in/blessing-gibendi-774556272?utm_source=share_via&utm_content=profile&utm_medium=member_android)

---

## 💡 Future Improvements

* Mobile app integration
* Real-time notifications
* AI-based matching system
* Enhanced analytics dashboard

---

## 📬 Contact

For questions or collaboration:

* Email:  parseandco@gmail.com
* GitHub: https://github.com/Supa-Neighbour
