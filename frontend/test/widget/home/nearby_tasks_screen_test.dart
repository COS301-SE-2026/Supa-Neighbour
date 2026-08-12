import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/home/nearby_tasks_screen.dart';
import 'package:supa_neighbour/screens/tasks/task_detail_screen.dart';

void main() {
  group('NearbyTasksScreen Widget Tests', () {
    group('Rendering', () {
      testWidgets('should render without crashing', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        // Wait for any async operations
        await tester.pumpAndSettle();

        // Screen should render
        expect(find.byType(NearbyTasksScreen), findsOneWidget);
      });

      testWidgets('should display list of tasks', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Should show at least one task (mock data has tasks)
        final taskCards = find.byType(GestureDetector);
        expect(taskCards, findsWidgets);
      });
    });

    group('Task Cards', () {
      testWidgets('should display task title', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Check for at least one task title
        // Using mock data, "Water my plants" should exist
        expect(find.text('Water my plants'), findsOneWidget);
      });

      testWidgets('should display task date and time', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Check for time format (e.g., "15:00" or similar)
        expect(find.textContaining(RegExp(r'\d{1,2}:\d{2}')), findsWidgets);
      });

      testWidgets('should display XP reward badge', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Check for XP text
        expect(find.textContaining(RegExp(r'\+.*XP')), findsWidgets);
      });

      testWidgets('should display category icon for each task', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Should have at least one icon
        expect(find.byType(Icon), findsWidgets);
      });
    });

    group('Navigation', () {
      testWidgets('should navigate to TaskDetailScreen when task is tapped', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // Find and tap the first task card
        final firstTask = find.byType(GestureDetector).first;
        expect(firstTask, findsOneWidget);

        await tester.tap(firstTask);
        await tester.pumpAndSettle();

        // Should navigate to TaskDetailScreen
        expect(find.byType(TaskDetailScreen), findsOneWidget);
      });
    });

    group('Refresh Indicator', () {
      testWidgets('should have refresh indicator', (tester) async {
        await tester.pumpWidget(
          const MaterialApp(
            home: NearbyTasksScreen(),
          ),
        );

        await tester.pumpAndSettle();

        // RefreshIndicator should be present
        expect(find.byType(RefreshIndicator), findsOneWidget);
      });
    });
  });
}