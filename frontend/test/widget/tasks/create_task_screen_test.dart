import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/tasks/create_task_screen.dart';

void main() {
  Widget buildTestableWidget() {
    return const MaterialApp(
      home: CreateTaskScreen(),
    );
  }

  group('CreateTaskScreen', () {
    testWidgets('renders the app bar with correct title', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Create Task'), findsOneWidget);
    });

    testWidgets('renders task title input field', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Task Title'), findsOneWidget);
    });

    testWidgets('renders category dropdown', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Category'), findsOneWidget);
    });

    testWidgets('renders date picker', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Date'), findsOneWidget);
    });

    testWidgets('renders time picker', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Time'), findsOneWidget);
    });

    testWidgets('renders instructions field', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Instructions'), findsOneWidget);
    });

    testWidgets('renders Post Task button', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      expect(find.text('Post Task'), findsOneWidget);
    });
  });
}