import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/home/my_tasks_screen.dart';

void main() {
  Widget buildTestableWidget() {
    return const MaterialApp(
      home: MyTasksScreen(),
    );
  }

  group('MyTasksScreen', () {
    testWidgets('renders the app bar with correct title', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.text('My Tasks'), findsOneWidget);
    });

    testWidgets('shows list of tasks', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.byType(ListView), findsOneWidget);
    });
  });
}