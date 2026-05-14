## Constraints

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

---