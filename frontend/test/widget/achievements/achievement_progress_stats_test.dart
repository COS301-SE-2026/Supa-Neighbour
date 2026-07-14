import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_progress_stats.dart';
//goign to modify the test cases
void main() {
  group('AchievementProgressStats Tests', () {
    testWidgets('displays correct earned and total counts', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementProgressStats(
              earnedCount: 5,
              totalCount: 14,
              progressPercentage: 0.357,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('5 '), findsOneWidget);
      expect(find.text('/ 14 Achievements Earned'), findsOneWidget);
    });

    testWidgets('displays 0% when no achievements', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementProgressStats(
              earnedCount: 0,
              totalCount: 0,
              progressPercentage: 0.0,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('0 '), findsOneWidget);
      expect(find.text('/ 0 Achievements Earned'), findsOneWidget);
      expect(find.text('0%'), findsOneWidget);
    });

    testWidgets('displays 100% when all achievements earned', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementProgressStats(
              earnedCount: 10,
              totalCount: 10,
              progressPercentage: 1.0,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('10 '), findsOneWidget);
      expect(find.text('/ 10 Achievements Earned'), findsOneWidget);
      expect(find.text('100%'), findsOneWidget);
    });

    testWidgets('displays partial progress correctly', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementProgressStats(
              earnedCount: 3,
              totalCount: 8,
              progressPercentage: 0.375,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('3 '), findsOneWidget);
      expect(find.text('/ 8 Achievements Earned'), findsOneWidget);
    });

    testWidgets('progress bar width reflects percentage', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementProgressStats(
              earnedCount: 5,
              totalCount: 10,
              progressPercentage: 0.5,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      final progressContainer = tester.widget<FractionallySizedBox>(
        find.byType(FractionallySizedBox),
      );
      expect(progressContainer.widthFactor, 0.5);
    });
  });
}