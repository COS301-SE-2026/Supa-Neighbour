# Testing Guide — Supa-Neighbour Flutter App

## Overview

This project uses Flutter's built-in testing framework to ensure code correctness and UI reliability. Tests are split into two categories: **unit tests** and **widget tests**, each serving a distinct purpose.

---

## Folder Structure

```
test/
├── unit/                        # Unit tests — pure Dart logic, no UI
│   ├── models/                  # Tests for data models (e.g. Task, User)
│   └── services/                # Tests for services (e.g. TaskService, AuthService)
├── widget/                      # Widget tests — UI rendering and interaction
│   └── task_detail_screen_test.dart
└── widget_test.dart             # Default Flutter-generated file (can be deleted)
```

---

## Dependencies

### Already included by default

`flutter_test` is bundled with Flutter and requires no extra setup.

### Optional — Mocking with Mockito

If your services have dependencies (e.g. an API client or repository), install Mockito for mocking:

Add to `pubspec.yaml` under `dev_dependencies`(if it is already not provided):

```yaml
dev_dependencies:
  flutter_test:
    sdk: flutter
  mockito: ^5.4.4
  build_runner: ^2.4.8
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
flutter test test/widget/task_detail_screen_test.dart
```

Run a specific test by name:

```bash
flutter test --plain-name 'renders the task title'
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
import 'package:supa_neighbour/app/models/task.dart';

void main() {
  group('Task model', () {

    test('isHighReward returns true when XP is 100 or more', () {
      final task = Task(title: 'Clean gutters', xpReward: 100);
      expect(task.isHighReward, true);
    });

    test('isHighReward returns false when XP is below 100', () {
      final task = Task(title: 'Water plants', xpReward: 50);
      expect(task.isHighReward, false);
    });

    test('toMap returns the correct structure', () {
      final task = Task(title: 'Water plants', xpReward: 50);
      expect(task.toMap(), {'title': 'Water plants', 'xpReward': 50});
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
import 'package:supa_neighbour/app/services/task_service.dart';
import 'package:supa_neighbour/app/models/task.dart';

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
// test/widget/task_detail_screen_test.dart

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/task_detail_screen.dart';

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

    testWidgets('renders task time and location', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Tomorrow at 3:00 PM'), findsOneWidget);
      expect(find.text('2 doors down • 50m away'), findsOneWidget);
    });

    testWidgets('tapping Accept Task shows a snackbar', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.tap(find.text('Accept Task'));
      await tester.pump();
      expect(find.text('Task accepted! (Coming soon)'), findsOneWidget);
    });

    testWidgets('tapping Message Helper shows a snackbar', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.tap(find.text('Message Helper'));
      await tester.pump();
      expect(find.text('Message helper (Coming soon)'), findsOneWidget);
    });

    testWidgets('back button is present in app bar', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

  });
}
```

---

## Key Concepts

| Concept | Description |
|---|---|
| `test()` | Used for unit tests — no Flutter framework needed |
| `testWidgets()` | Used for widget tests — renders UI in a virtual environment |
| `WidgetTester` | Flutter's tool to simulate rendering and user interactions |
| `tester.pumpWidget()` | Renders a widget — always `await` this |
| `tester.pump()` | Triggers a frame/animation — always `await` after interactions |
| `find.text()` | Locates a widget by exact text content |
| `find.byIcon()` | Locates a widget by icon |
| `expect(..., findsOneWidget)` | Asserts exactly one matching widget was found |
| `expect(..., findsNothing)` | Asserts no matching widget was found |
| `setUp()` | Runs before each test in a group — used to initialise shared state |
| `MockApiClient` | A fake version of a dependency you control in tests |
| `when(...).thenAnswer(...)` | Defines what a mock returns when called |
| `MaterialApp` wrapper | Required for screens that use Navigator, ScaffoldMessenger, etc. |

---

## Common Mistakes to Avoid

**Missing `await` on async calls**

```dart
// ❌ Wrong
tester.pumpWidget(buildTestableWidget());

// ✅ Correct
await tester.pumpWidget(buildTestableWidget());
```

**Text mismatch in `find.text()`**

`find.text()` requires an exact match including spacing, capitalisation, and punctuation.

```dart
// ❌ Wrong — typo and missing space
find.text('Tommorow at 3:00PM')

// ✅ Correct — matches the source exactly
find.text('Tomorrow at 3:00 PM')
```

**Not wrapping screens in `MaterialApp`**

Screens that use `Navigator`, `ScaffoldMessenger`, or `Theme` will throw errors without a `MaterialApp` wrapper in tests.

---

