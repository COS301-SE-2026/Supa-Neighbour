# Testing Guide

## Overview
This project uses Flutter's built-in testing framework to ensure code correctness and UI reliability. Tests are split into categories: unit tests, widget tests, integration tests, and end-to-end tests.

---

## Folder Structure

```
test/
├── unit/                        # Unit tests: pure Dart logic, no UI
│   ├── models/                  # Tests for data models (Task, User, etc.)
│   └── services/                # Tests for services (TaskService, ChatService)
│
├── widget/                      # Widget tests: UI rendering and interaction
│   ├── auth/                    # Auth screens (login, register, etc.)
│   ├── home/                    # Home tab screens
│   ├── tasks/                   # Tasks tab screens
│   ├── chat/                    # Chat tab screens
│   ├── leaderboard/             # Leaderboard tab screens
│   ├── profile/                 # Profile tab screens
│   └── components/              # Reusable components (buttons, inputs, etc.)
│
├── integration/                 # Integration tests: multiple components working together
├── e2e/                         # End-to-End tests: full user journeys
└── README.md
```

---

## Dependencies

### Already included by default

`flutter_test` is bundled with Flutter so it requires no extra setup.

### Mocking with Mockito

Since our services have dependencies (e.g. an API client or repository), we will install Mockito for mocking:

Add to `pubspec.yaml` under `dev_dependencies`:

```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mockito: ^5.4.4
  build_runner: ^2.4.9
```

Then run:

```bash
flutter pub get
```

---

## Running Tests

Run all tests:

```bash
flutter test
```

Run a specific file:

```bash
flutter test test/widget/tasks/task_detail_screen_test.dart
```

Run a specific test by name:

```bash
flutter test --plain-name 'renders the task title'
```

Run tests with coverage:

```bash
flutter test --coverage
```

After adding `@GenerateMocks` annotations (Mockito), regenerate mocks:

```bash
dart run build_runner build
```

---

## Unit Tests

### What they test

Unit tests target **pure Dart logic** with no UI or Flutter framework involved. They live in `test/unit/` and cover:

- Model classes (e.g. `Task`, `User`) — field validation, computed properties, serialisation
- Service classes (e.g. `TaskService`) — business logic, data transformation, API response handling

### When to write them

Write a unit test any time a class or function:

- Computes or transforms data
- Contains conditional logic (if/else, validation rules)
- Parses or maps data from an API response
- Has methods that can succeed or fail

### Example — Model test

```dart
// test/unit/models/task_test.dart

import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/task_model.dart';

void main() {
  group('Task model', () {

    test('fromJson creates Task with correct data', () {
      final json = {
        'taskId': 1,
        'title': 'Water plants',
        'xpReward': 50,
      };
      final task = Task.fromJson(json);
      expect(task.id, '1');
      expect(task.title, 'Water plants');
      expect(task.xpReward, 50);
    });

  });
}
```

### Example — Service test with a mocked dependency

```dart
// test/unit/services/task_service_test.dart

import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/mockito.dart';
import 'package:mockito/annotations.dart';
import 'package:supa_neighbour/services/task_service.dart';
import 'package:supa_neighbour/models/task_model.dart';

@GenerateMocks([ApiClient])
void main() {
  late MockApiClient mockApiClient;
  late TaskService taskService;

  setUp(() {
    mockApiClient = MockApiClient();
    taskService = TaskService(mockApiClient);
  });

  group('TaskService', () {

    test('getTasks returns a list of Task objects', () async {
      when(mockApiClient.fetchTasks()).thenAnswer((_) async => [
        {'title': 'Water plants', 'xpReward': 50}
      ]);

      final tasks = await taskService.getTasks();

      expect(tasks.length, 1);
      expect(tasks.first.title, 'Water plants');
    });

    test('getTasks returns an empty list when no tasks exist', () async {
      when(mockApiClient.fetchTasks()).thenAnswer((_) async => []);

      final tasks = await taskService.getTasks();

      expect(tasks, isEmpty);
    });

  });
}
```

---

## Widget Tests

### What they test

Widget tests render a widget in a virtual environment and verify that:

- The correct text, icons, and UI elements are displayed
- Buttons and interactions trigger the expected behaviour (e.g. snackbars, navigation)
- The widget responds correctly to different input data

They live in `test/widget/` and use `testWidgets()` instead of `test()`.

### When to write them

Write a widget test any time a widget or screen:

- Displays data that must appear in a specific format
- Has buttons or gestures that trigger actions
- Conditionally shows or hides UI elements
- Navigates to another screen

### Example — TaskDetailScreen widget test

```dart
// test/widget/tasks/task_detail_screen_test.dart

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/tasks/task_detail_screen.dart';

void main() {
  Widget buildTestableWidget() {
    return const MaterialApp(
      home: TaskDetailScreen(),
    );
  }

  group('TaskDetailScreen', () {

    testWidgets('renders the app bar with correct title', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Task Details'), findsOneWidget);
    });

    testWidgets('renders the task title', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Water my plants'), findsOneWidget);
    });

    testWidgets('renders the XP reward badge', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('+50 XP'), findsOneWidget);
    });

  });
}
```

---

## Integration Tests

### What they test

Integration tests verify that multiple components work together correctly. This includes:

- Navigation between screens
- API calls and UI updates
- Complete user flows

### When to write them

Write an integration test for core user flows:

- Login -> Home
- Create Task -> My Tasks
- Complete Task -> Approval
- View Helper -> Invite

### Example Structure

```dart
// test/integration/task_flow_test.dart

import 'package:flutter_test/flutter_test.dart';

void main() {
  group('Task Creation Flow', () {
    testWidgets('User can create a task and see it in My Tasks', (tester) async {
      // 1. Login
      // 2. Navigate to Create Task
      // 3. Fill form and submit
      // 4. Verify task appears in My Tasks
    });
  });
}
```

---

## End-to-End Tests

### What they test

End-to-End tests verify complete user journeys with real API calls and database interactions.

### When to write them

Write E2E tests for complete user journeys:

- Register -> Login -> Create Task
- Requester -> View Helpers -> Invite
- Helper -> Accept -> Complete -> Review

### Note

E2E tests require a real backend environment. These tests are recommended but not required for Demo 2.

---

## Key Concepts

| Concept | Description |
|---|---|
| `test()` | Used for unit tests — no Flutter framework needed |
| `testWidgets()` | Used for widget tests — renders UI in a virtual environment |
| `WidgetTester` | Flutter's tool to simulate rendering and user interactions |
| `tester.pumpWidget()` | Renders a widget — always `await` this |
| `tester.pump()` | Triggers a frame/animation — always `await` after interactions |
| `tester.pumpAndSettle()` | Waits for all animations and microtasks to complete |
| `find.text()` | Locates a widget by exact text content |
| `find.byIcon()` | Locates a widget by icon |
| `find.byType()` | Locates a widget by its type |
| `expect(..., findsOneWidget)` | Asserts exactly one matching widget was found |
| `expect(..., findsNothing)` | Asserts no matching widget was found |
| `setUp()` | Runs before each test in a group — used to initialise shared state |
| `setUpAll()` | Runs once before all tests in a group |
| `Mock` | A fake version of a dependency you control in tests |
| `when(...).thenAnswer(...)` | Defines what a mock returns when called |
| `MaterialApp` wrapper | Required for screens that use Navigator, ScaffoldMessenger, etc. |

---

## Common Mistakes to Avoid

### Missing `await` on async calls

```dart
// Wrong
tester.pumpWidget(buildTestableWidget());

// Correct
await tester.pumpWidget(buildTestableWidget());
```

### Text mismatch in `find.text()`

`find.text()` requires an exact match including spacing, capitalisation, and punctuation.

```dart
// Wrong - typo and missing space
find.text('Tommorow at 3:00PM')

// Correct - matches the source exactly
find.text('Tomorrow at 3:00 PM')
```

### Not wrapping screens in `MaterialApp`

Screens that use `Navigator`, `ScaffoldMessenger`, or `Theme` will throw errors without a `MaterialApp` wrapper in tests.

### Not using `pumpAndSettle()` for async operations

```dart
// Wrong - won't wait for async operations to complete
await tester.pumpWidget(buildTestableWidget());
expect(find.text('Loading...'), findsOneWidget);

// Correct - waits for all async operations
await tester.pumpWidget(buildTestableWidget());
await tester.pumpAndSettle();
expect(find.text('Loading...'), findsNothing);
```

---

## Demo 2 Testing Requirements

| Test Type | Required | Target |
|-----------|----------|--------|
| Unit Tests | Yes | All models and services |
| Widget Tests | Yes | All screens and components |
| Integration Tests | Yes | Core user flows |
| E2E Tests | Recommended | Full user journeys |
| Coverage | Yes | 80 percent or higher |

---

**Last updated:** July 2026