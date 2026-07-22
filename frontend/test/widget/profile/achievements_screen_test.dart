import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/profile/achievements_screen.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_card.dart';

void main() {
  group('AchievementsScreen Tests', () {
    testWidgets('shows loading indicator initially', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 100));
      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      await tester.pump(const Duration(milliseconds: 500));
    });

    testWidgets('shows achievements after loading', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.textContaining('Achievements Earned'), findsOneWidget);
      expect(find.byType(AchievementCard), findsWidgets);
    });

    testWidgets('has back button in app bar', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

    testWidgets('has settings button in app bar', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.byIcon(Icons.settings), findsOneWidget);
    });

    testWidgets('shows earned achievements with Earned label', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.text('Earned'), findsAtLeastNWidgets(1));
    });

    testWidgets('shows unearned achievements with progress', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.textContaining('/'), findsAtLeastNWidgets(1));
    });

    testWidgets('displays correct badge numbers', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.text('1'), findsOneWidget);
      expect(find.text('2'), findsOneWidget);
      expect(find.text('3'), findsOneWidget);
      expect(find.text('4'), findsOneWidget);
      expect(find.text('5'), findsOneWidget);
      expect(find.text('6'), findsOneWidget);
      expect(find.text('7'), findsOneWidget);
      expect(find.text('8'), findsOneWidget);
      expect(find.text('9'), findsOneWidget);
    });

    testWidgets('calculates and displays correct stats', (tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
      await tester.pump(const Duration(milliseconds: 600));
      expect(find.text('5 '), findsOneWidget);
      expect(find.text('/ 14 Achievements Earned'), findsOneWidget);
    });
  });
}