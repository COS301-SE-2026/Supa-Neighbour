import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/tasks/my_tasks_screen.dart';

void main() {
  Widget buildTestableWidget() {
    return ProviderScope(
      child: const MaterialApp(
        home: MyTasksScreen(),
      ),
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
      // Check if there's a ListView OR empty state
      final hasListView = find.byType(ListView).evaluate().isNotEmpty;
      final hasEmptyState = find.text('No tasks posted yet').evaluate().isNotEmpty ||
          find.text('No accepted tasks').evaluate().isNotEmpty ||
          find.text('No available tasks').evaluate().isNotEmpty;
      expect(hasListView || hasEmptyState, true);
    });
  });
}