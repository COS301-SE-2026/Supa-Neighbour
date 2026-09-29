# Supa-Neighbour — Coding Style Guide

This document defines how all code in this project should be written. Following these conventions keeps the codebase consistent and readable for every member of the team — regardless of who wrote the code.
 
These are not suggestions. All code submitted via Pull Request must follow these conventions before it will be reviewed or merged.
 
## Version history
 
| Version | Date | Changes | 
|---|---|---|
| 1.0 | 2026-07-25 | Converted original style guide to Markdown |
| 1.1 | 2026-07-25 | Added a Section 6 for the Repository folder breakdown |

---

## 1. General Rules

These apply to every file in the project regardless of language.

- **No hardcoded secrets** — passwords, API keys, and connection strings never appear in code. They go in `application-local.yml` or `local.settings.json`.
- **No commented-out code** — if code is not needed, delete it. Git history exists to recover old code if needed.
- **One thing per file** — each file should have one clear responsibility. A file called `UserService.java` should only contain user-related logic.
- **File names must be meaningful** — `Helper.java`, `Utils.dart`, `misc.ts` are not acceptable names.
- **No magic numbers** — never write a raw number in logic without explaining it.

```java
// bad
if (users.size() > 50) { ... }

// good
private static final int MAX_GROUP_SIZE = 50;
if (users.size() > MAX_GROUP_SIZE) { ... }
```

---

## 2. Backend — Java / Spring Boot

### Naming conventions

| Thing | Convention | Example |
|---|---|---|
| Classes | PascalCase | `UserService`, `PostController` |
| Methods | camelCase | `getUserById()`, `createPost()` |
| Variables | camelCase | `userId`, `postContent` |
| Constants | UPPER_SNAKE_CASE | `MAX_GROUP_SIZE`, `DEFAULT_PAGE_SIZE` |
| Packages | lowercase | `com.app.api.services` |
| Database columns | snake_case | `created_at`, `user_id` |

### Class structure

Every class should follow this order:

```java
public class UserService {

    // 1. Constants
    private static final int MAX_RESULTS = 100;

    // 2. Fields / dependencies
    private final UserRepository userRepository;

    // 3. Constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 4. Public methods
    public User getUserById(String id) { ... }

    // 5. Private helper methods
    private void validateUser(User user) { ... }
}
```

### Controllers

- Controllers only handle HTTP — no business logic inside them.
- Business logic belongs in the service layer.
- Always use `ResponseEntity` as the return type.

```java
// bad — logic inside controller
@GetMapping("/{id}")
public User getUser(@PathVariable String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("not found"));
}

// good — controller delegates to service
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable String id) {
    return ResponseEntity.ok(userService.getUserById(id));
}
```

### Services

- All business logic lives here.
- Never call a repository directly from a controller.
- Methods should do one thing and be named clearly.

```java
// bad — method does too many things
public User processUser(String id) { ... }

// good — clear, single responsibility
public User getUserById(String id) { ... }
public User updateUserProfile(String id, UpdateProfileRequest request) { ... }
```

### Exception handling

- Never return a raw 500 error to the client.
- Use a global exception handler.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
```

### Swagger annotations

Every controller endpoint must have Swagger annotations:

```java
@Operation(summary = "Get user by ID")
@ApiResponse(responseCode = "200", description = "User found")
@ApiResponse(responseCode = "404", description = "User not found")
@GetMapping("/{id}")
public ResponseEntity<User> getUser(@PathVariable String id) { ... }
```

---

## 3. Frontend — Dart / Flutter

### Naming conventions

| Thing | Convention | Example |
|---|---|---|
| Classes / Widgets | PascalCase | `HomeScreen`, `PostCard` |
| Variables | camelCase | `userId`, `postContent` |
| Methods / functions | camelCase | `fetchUserProfile()`, `onTap()` |
| Constants | camelCase with `k` prefix | `kPrimaryColor`, `kDefaultPadding` |
| Files | snake_case | `home_screen.dart`, `post_card.dart` |
| Folders | snake_case | `screens/`, `widgets/` |

### File naming

Every file name must match the main class or widget inside it:

```
post_card.dart      → contains class PostCard
home_screen.dart    → contains class HomeScreen
user_service.dart   → contains class UserService
```

### State management

- Use `StatelessWidget` by default.
- Only use `StatefulWidget` when the widget needs to manage its own local state.
- Riverpod will be used as the solutions for the states.

### Widget structure

Keep widgets small and focused. If a widget's `build()` method exceeds 50 lines, it should be broken into smaller widgets.

```dart
// bad — one massive widget doing everything
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // 200 lines of nested widgets...
    );
  }
}

// good — broken into smaller pieces
class HomeScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const HomeAppBar(),
      body: const PostFeed(),
      bottomNavigationBar: const BottomNav(),
    );
  }
}
```

### API calls

- Never make HTTP calls directly inside a widget.
- All API calls belong in a service class inside `lib/services`

---

## 4. Functions — TypeScript

### Naming conventions

| Thing | Convention | Example |
|---|---|---|
| Files | camelCase | `notificationDispatcher.ts` |
| Functions | camelCase | `sendNotification()` |
| Classes | PascalCase | `NotificationService` |
| Constants | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Interfaces | PascalCase with `I` prefix | `INotificationPayload` |

### Always use TypeScript types

Never use `any` — always define the shape of your data:

```typescript
// bad
async function sendNotification(payload: any) { ... }

// good
interface INotificationPayload {
  userId: string;
  message: string;
  type: 'like' | 'comment' | 'follow';
}

async function sendNotification(payload: INotificationPayload) { ... }
```

### Error handling

Always wrap async functions in try/catch and log errors clearly:

```typescript
// bad
async function processQueue(message: string) {
  const data = JSON.parse(message);
  await sendToNotificationHub(data);
}

// good
async function processQueue(message: string) {
  try {
    const data: INotificationPayload = JSON.parse(message);
    await sendToNotificationHub(data);
  } catch (error) {
    console.error('Failed to process notification queue message:', error);
    throw error;
  }
}
```

---
## 5. Repository folder structure
 
### Backend (Spring Boot — `com.app`)
 
```
src/main/java/com/app/
├── api/
    ├── config/          # security, CORS, OpenAPI/Swagger, bean config
    ├── controllers/           # HTTP layer only — delegates to services
    ├── services/              # business logic
    ├── repositories/           # standard repositories (EntityManager, native SQL)    
    ├── dtos/                  # request/response DTOs
    ├── model/ (or entity/)   # JPA entities
    └── security            # All the needed files for authentication
├── test/           #where all the testing files live
src/main/resources/
├── application.yml           # local dev — do not modify for Azure config
├── application-azure.yml     # Azure-specific profile
├──  application.properties 
└── Firebase/
    ├── serviceAccountKey.json   # gitignored, Admin SDK credentials
```

### Frontend (Flutter — `com.app.supa_neighbour`)
 
```
frontend/lib/
├── app/               # mobile user-facing code
│   ├── screens/       # where all the screens have been completed
│   ├── widgets/
│   └── services/       # API calls — never call HTTP directly from a widget
|   └── components/
|   └── constants/
|   └── models/
```