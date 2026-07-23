# SupaNeighbour – Testing Policy

| **Version** | **Date** | **Author** | **Approved By** | **Change Description** |
|-------------|----------|------------|-----------------|--------------------------|
| 1.0         | 2026-07-14 | David Ekele Kalu (Tester) | Blessing Gibendi (Team Lead) | Initial release for SupaNeighbour |

---

## 1. Purpose
This policy defines mandatory testing standards for **SupaNeighbour** – a community-driven platform connecting neighbours in need with those willing to assist. The policy ensures:
- Consistent quality across **Flutter mobile/web frontend**, **Spring Boot APIs**, and **Azure services**.
- Compliance with security and data privacy standards (GDPR, POPIA).
- Reliable performance and scalability on Azure infrastructure.

---

## 2. Scope
This policy applies to all components of SupaNeighbour:

| **Component** | **Technology** | **Hosting** |
|---------------|----------------|-------------|
| Mobile App (Android/iOS) | Flutter (Dart) | Google Play / App Store |
| Web Admin Dashboard | Flutter Web (Dart) | Azure Static Web Apps |
| Main REST API | Spring Boot 3.2.0 (Java 21) | Azure App Service |
| Matching Engine (GraphQL) | Netflix DGS + Spring Boot | Azure App Service |
| Admin API Extensions | Express/Fastify (TypeScript) | Azure Functions |
| Primary Database | Azure Cosmos DB (NoSQL) | Azure |
| Relational Database | Azure SQL Database | Azure |
| Authentication | Microsoft Entra ID B2C | Azure |
| Secrets | Azure Key Vault | Azure |

**Exclusions:** Internal development branches (`feature/*`, `dev/*`) are exempt from full regression testing but must pass unit and smoke tests.

---

## 3. Testing Principles
All testing follows ISTQB-aligned principles:
1. **Testing shows presence of defects** – not their absence.
2. **Exhaustive testing is impossible** – risk-based prioritisation using defect history and feature criticality.
3. **Early testing** – test design starts during story refinement.
4. **Defect clustering** – focus on complex modules like the **Matching Engine** and **Authentication flow**.
5. **Pesticide paradox** – review and rotate test cases every 2 sprints.
6. **Context-dependent** – safety/security features (e.g., user data, location) receive higher scrutiny.
7. **Absence-of-errors fallacy** – ensure UX and business needs are validated via UAT.

---

## 4. Roles & Responsibilities

| **Role** | **Team Member** | **Key Responsibilities** |
|----------|-----------------|---------------------------|
| **Team Lead / QA Sponsor** | Blessing Gibendi | – Approve policy changes.<br>– Final release sign-off.<br>– Escalation for quality disputes. |
| **Tester (Test Manager)** | David Ekele Kalu | – Create and maintain test plans.<br>– Manage defect triage.<br>– Report test metrics.<br>– Coordinate UAT with stakeholders. |
| **Data/Service Engineer** | Divo Kohler | – Validate data pipelines and Cosmos DB queries.<br>– Performance/load testing of APIs. |
| **Integration Engineer** | Amantle Temo | – Test CI/CD pipelines and environment configurations.<br>– Validate third-party integrations (Azure Maps, Entra ID). |
| **UI/UX Engineer** | Michelle Njoroge | – Lead usability testing.<br>– Validate Flutter frontend against design specs.<br>– Accessibility testing (WCAG 2.1 AA). |
| **All Developers** | Everyone | – Write unit tests (JUnit for Java, Dart `test` for Flutter).<br>– Conduct integration testing for their modules.<br>– Fix defects promptly. |

**Escalation Path:** Tester (David) → Team Lead (Blessing) → Lecturer/Supervisor (for academic compliance).

---

## 5. Mandatory Testing Levels & Types

### 5.1 Testing Levels

| **Level** | **Owner** | **Tooling** | **Coverage Target** | **Description** |
|-----------|-----------|-------------|---------------------|-----------------|
| **Unit Testing** | Developers | JUnit 5 (Java), Dart `test` (Flutter) | ≥ 80% line coverage | Test individual classes/functions. |
| **Integration Testing** | Developers + David | Spring Boot Test, MockMvc, Flutter integration_test | N/A | Test API-to-database, API-to-API, and frontend-to-backend interactions. |
| **System Testing** | David (QA) | Postman/Newman, Playwright (Flutter web), Appium (mobile) | N/A | End-to-end functional testing on staging environment. |
| **User Acceptance Testing (UAT)** | Blessing + Stakeholders | Manual + Flutter Driver | N/A | Validate business workflows with real user scenarios. Formal sign-off required. |

### 5.2 Testing Types (Mandatory as applicable)

| **Type** | **Applicability** | **Tooling** |
|----------|-------------------|-------------|
| **Functional** | All features | Postman, Flutter Widget Tests |
| **Regression** | Before every release (sprint boundary) | Newman (API), Flutter integration_test |
| **Performance/Load** | Matching Engine & Main API | JMeter or k6 |
| **Security** | All endpoints handling PII/location data | OWASP ZAP, SonarCloud Security Hotspots |
| **Usability/UI** | Flutter mobile + web | Manual + UX heuristics, Flutter golden tests |
| **Compatibility** | Mobile: Android 8+, iOS 14+; Web: Chrome, Edge, Firefox | BrowserStack or Firebase Test Lab |
| **Smoke/Sanity** | Every PR merge to `main` | GitHub Actions (see CI/CD section) |

---

## 6. Test Environment & Data Standards

### 6.1 Environment Requirements
| **Environment** | **Purpose** | **Configuration** |
|-----------------|-------------|-------------------|
| **Local** | Developer testing | Docker Compose with local Cosmos DB emulator |
| **Dev** | Integration testing | Azure Dev instance – ephemeral |
| **Staging** | System testing & UAT | Mirrors production (same SKU, Entra ID config) |
| **Production** | Live release | Fully deployed on Azure |

- Staging must be refreshed from production data (anonymised) before each release candidate.
- Environment availability target: ≥ 95% during testing hours.

### 6.2 Test Data Policy
- **Production data** may only be used after **anonymisation** (mask names, emails, phone numbers, precise locations).
- **Synthetic data** is preferred for unit/integration tests (generated via `faker` libraries).
- Test data seeds must be version-controlled in `/api/src/test/resources` and `/flutter/test/data`.

---

## 7. Defect Management

### 7.1 Severity & Priority Matrix

| **Severity** | **Definition** | **Priority** | **Example (SupaNeighbour)** |
|--------------|----------------|--------------|------------------------------|
| **Critical** | System crash, data loss, security breach, auth failure | P0 – Fix immediately | User cannot log in via Entra ID |
| **High** | Major feature broken, no workaround | P1 – Fix before release | Matching Engine returns no results for valid query |
| **Medium** | Feature bug with acceptable workaround | P2 – Fix in next sprint | Incorrect badge calculation |
| **Low** | Cosmetic / minor UX issues | P3 – Backlog | Spelling error or misaligned button |

### 7.2 Defect Lifecycle
- All defects logged in **GitHub Issues** with label `bug` + severity.
- Critical bugs trigger a Slack/Teams notification to the whole team.

---

## 8. Entry & Exit Criteria

### 8.1 Entry Criteria (to begin system testing)
- Code deployed to **Staging** environment.
- All unit tests pass (no failures).
- Smoke test suite (GitHub Actions workflow) passes ≥ 95%.
- Test data loaded and validated.
- User stories in "Ready for Test" state.

### 8.2 Exit Criteria (to release to production)
- Zero **Critical** open defects.
- ≤ 2 **High** open defects with approved workarounds.
- Overall test pass rate ≥ 95%.
- Performance test: API response time ≤ 500ms (95th percentile).
- Security scan: Zero **High** or **Critical** vulnerabilities.
- UAT sign-off obtained from Blessing (Team Lead) and stakeholder representative.
- SonarCloud Quality Gate = **PASSED** (see badges in README).

---

## 9. Test Automation Strategy

### 9.1 Automation Targets
| **Suite** | **Tool** | **Trigger** | **Minimum Pass Rate** |
|-----------|----------|-------------|------------------------|
| Unit Tests (Backend) | JUnit 5 | `backend.yml` on PR push | 100% (no failures) |
| Unit Tests (Frontend) | Dart `test` | `frontend.yml` on PR push | 100% |
| API Integration Tests | Newman (Postman) | `backend.yml` daily + on release | ≥ 95% |
| Flutter Widget Tests | Flutter `test` | `frontend.yml` on PR push | ≥ 90% |
| Flutter Integration (E2E) | Flutter integration_test | Scheduled nightly + release | ≥ 90% |
| Smoke Tests | GitHub Actions script | Every merge to `main` | 100% (must pass) |
| Performance/Load | k6 | Weekly (staging) | Response < 500ms |

### 9.2 Tooling Standards
- **Backend API Testing:** Postman collections + Newman CLI (run in CI).
- **UI Automation:** Flutter integration_test + `flutter_driver` for mobile.
- **Performance:** k6 scripts for load testing endpoints (`/api/match`, `/api/tasks`).
- **Security:** OWASP ZAP baseline scan run monthly.

### 9.3 Flaky Test Policy
- Flaky tests must be quarantined (skip via `@FlakyTest` annotation or `skip()`).
- Fix within **2 business days** – assigned to the module owner.

---

## 10. Reporting & Metrics

### 10.1 Mandatory Reports
| **Report** | **Frequency** | **Audience** | **Tool** |
|------------|---------------|--------------|----------|
| Daily Test Execution Summary | Daily (EOD) | Team | GitHub Issues + Slack |
| Sprint Test Report | Every sprint (2 weeks) | Team + Supervisor | Google Docs / Confluence |
| Release Test Summary | Per release (every 3 sprints) | Stakeholders | Email + PDF |

### 10.2 Key Metrics (tracked via SonarCloud + custom dashboards)
| **Metric** | **Target** | **Tool** |
|------------|------------|----------|
| Unit Test Coverage | ≥ 80% | SonarCloud / JaCoCo |
| Test Case Pass Rate | ≥ 95% (RC builds) | Manual + CI logs |
| Defect Detection Percentage (DDP) | ≥ 90% | GitHub Issues analytics |
| Automation Ratio (regression) | ≥ 70% | Custom script |
| Mean Time to Detect (MTTD) | ≤ 2 hours for P0 | GitHub + Slack |
| Technical Debt Ratio | ≤ 5% | SonarCloud |

---

## 11. CI/CD & Quality Gates

### 11.1 GitHub Actions Workflows
| **Workflow** | **File** | **Trigger** | **Checks** |
|--------------|----------|-------------|------------|
| Backend | `backend.yml` | PR to `main` + push | JUnit tests, JaCoCo coverage, SonarCloud analysis |
| Frontend | `frontend.yml` | PR to `main` + push | Dart unit tests, Flutter widget tests, formatting/lint |
| E2E | `e2e.yml` (to be added) | Nightly + on demand | Flutter integration tests against staging |

### 11.2 Quality Gate Rules (SonarCloud)
- **Coverage** ≥ 80% on new code.
- **Bugs** = 0 (Critical).
- **Vulnerabilities** = 0 (Critical/High).
- **Code Smells** – technical debt ratio ≤ 5%.
- Build must be **green** before merge.

---

## 12. Compliance & Data Privacy

SupaNeighbour handles user location, contact details, and task histories. The following apply:
- **POPIA (South Africa)** – User data must be collected with explicit consent and stored securely.
- **GDPR (EU)** – If platform expands, support right to erasure and data portability.
- **Azure Security Best Practices** – Use Managed Identities, Key Vault for secrets, never hard-code credentials.

All test data must **never** contain real personal information – synthetic or anonymised only.

---

## 13. Policy Review & Governance

- **Review frequency:** Every semester (or after major architecture changes).
- **Proposed changes:** Submit via GitHub Issue with label `testing-policy`; approved by Blessing (Team Lead).
- **Compliance tracking:** David (Tester) monitors adherence and reports in sprint retrospectives.

---

## 14. Related Documents

| **Document** | **Location** |
|--------------|--------------|
| Test Plan (Sprint-specific) | `/docs/test-plans/` |
| Functional Requirements | [Google Docs Link](https://docs.google.com/document/d/1PVcbys8ZG97wmAtsVa-1X8_czOlywJprGr-_2dYto4w/edit) |
| Database Domain Model | [Google Docs Link](https://docs.google.com/document/d/1nC0Un50nfuOG_E8rg6VxcGCwM4UnnsT5mpWU7TS6gRs/edit) |
| CI/CD Pipeline Docs | `/.github/workflows/` |
| Project Board | [GitHub Project](https://github.com/orgs/COS301-SE-2026/projects/34) |

---

*This policy is effective as of **2026-07-14** and supersedes all previous testing guidelines for SupaNeighbour.*