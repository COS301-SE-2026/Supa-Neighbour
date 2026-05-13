## Design Patterns

### Strategy Pattern (Matching Engine)

**Decision:** Encapsulate different matching algorithms as interchangeable strategies.


**Reason:**
- Functional Requirement **R2.2** requires matching based on multiple criteria (proximity, trust, skills, availability)
- **NFR5.2.2** requires proximity algorithm to evolve without changing core logic
- Therefore new  strategies can be added without modifying existing code.

---

### Factory Pattern (Matching Strategy Creation)

**Decision:** Use a Factory to instantiate the appropriate matching strategy based on user preferences.


**Reason:**
- Functional Requirement **R2.2.3** allows filtering by preferences ("verified only")
- Strategy creation logic is centralised
- Simplifies MatchingContext

---

### Observer Pattern (SignalR Chat)

**Decision:** Use Azure SignalR Service implementing the Observer pattern for real-time chat.

**Flow:**
1. User A sends message → ChatHub.SendMessage()
2. ChatHub persists message to Cosmos DB
3. ChatHub.NotifyObservers() → SignalR broadcasts to all clients in task chat room
4. Each connected client receives message (observer notified)

**Reason:**
- **AR2.3** mandates Azure SignalR Service via WebSocket
- **NFR1.1.2** requires message delivery within 2 seconds
- Observer pattern is the natural fit for pub/sub messaging

---

### Decorator Pattern (Security & RBAC)

**Decision:** Use Spring Security filters as decorators around service methods.



**Reason:**
- Architectural Requirement **AR3.1** uses JWT tokens
- Architectural Requirement **AR3.2** enforces Role-based access token (resident, helper, admin)
- Non-Functional Requirement **NFR2.2.1** requires rate limiting
- Decorators can be composed without modifying core business logic

---

### Chain of Responsibility (Task Acceptance)

**Decision:** Validate task acceptance through a chain of handlers.


**Example Chain:**
```
[AddressRevealHandler] → [AvailabilityHandler] → [AnimalSafetyHandler] → [Result]
```

**Reason:**
- Functional Requirement **R4.1.1** requires address revealed only after acceptance
- Chain allows adding new validation rules without modifying existing code
- Each handler can either: process, pass, or reject

---

### Singleton Pattern (Azure Key Vault Client)

**Decision:** Maintain a single instance of the Azure Key Vault secret client.

**Reason:**
- Architecture Requirement **AR3.5** requires Azure Key Vault for secrets
- Creating multiple Key Vault clients wastes resources and connection pools
- Singleton ensures one client for the entire application lifetime
---
