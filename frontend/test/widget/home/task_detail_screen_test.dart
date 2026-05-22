import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/task_model.dart';
import 'package:supa_neighbour/screens/home/task_detail_screen.dart';

void main() {
  // Create a mock task for testing using the correct constructor
  final mockTask = Task(
    id: '1',
    title: 'Water my plants',
    category: 'Plants',
    date: DateTime(2026, 5, 23, 15, 0), // Tomorrow at 3:00 PM
    time: const TimeOfDay(hour: 15, minute: 0),
    xpReward: 50,
    instructions: 'Please water the 3 pots on the balcony. Use the blue watering can under the sink.',
    status: 'pending',
    createdAt: DateTime.now(),
  );

  Widget buildTestableWidget() {
    return MaterialApp(
      home: TaskDetailScreen(
        task: mockTask,
      ),
    );
  }

  group('TaskDetailScreen', () {
    testWidgets('renders the app bar with correct title', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Task Details'), findsOneWidget);
    });

    testWidgets('renders the task title', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Water my plants'), findsOneWidget);
    });

    testWidgets('renders the XP reward badge', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('+50 XP'), findsOneWidget);
    });

    testWidgets('renders Accept Task button', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Accept Task'), findsOneWidget);
    });

    testWidgets('tapping Accept Task shows a snackbar', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.tap(find.text('Accept Task'));
      await tester.pump();
      expect(find.text('Task accepted!'), findsOneWidget);
    });
  });
}