import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/task_model.dart';
import 'package:supa_neighbour/screens/tasks/task_detail_screen.dart';

void main() {
  final mockTask = Task(
    id: '1',
    title: 'Water my plants',
    category: 'Plants',
    date: DateTime(2026, 5, 23, 15, 0), 
    time: const TimeOfDay(hour: 15, minute: 0),
    xpReward: 50,
    instructions: 'Please water the 3 pots on the balcony. Use the blue watering can under the sink.',
    status: 'open',
    createdAt: DateTime.now(),
    createdBy: 'test_user',
    requesterName: 'Test Requester',
    helperId: null,
    helperName: null,
  );

  Widget buildTestableWidget() {
    return MaterialApp(
      home: TaskDetailScreen(
        task: mockTask,
        isRequesterView: true,
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

    testWidgets('does NOT render Accept Task button when isRequesterView is true', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Accept Task'), findsNothing);
    });

    testWidgets('renders the status badge', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Waiting for helper'), findsOneWidget);
    });
  });
}