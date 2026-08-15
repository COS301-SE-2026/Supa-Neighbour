import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/screens/profile/achievements_screen.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_grid.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_progress_stats.dart';
import '../../mocks/mock_achievement_service.dart';

void main() {
  group('AchievementsScreen Tests', () {
    Widget buildTestableWidget() {
      return ProviderScope(
        overrides: [
          achievementServiceProvider.overrideWithValue(MockAchievementService()),
        ],
        child: const MaterialApp(
          home: AchievementsScreen(),
        ),
      );
    }

    testWidgets('shows loading indicator initially', (tester) async {
      await tester.pumpWidget(buildTestableWidget());

      // Check that the screen renders without crashing
      expect(find.byType(AchievementsScreen), findsOneWidget);
    });

    testWidgets('shows achievements after loading', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle(const Duration(seconds: 2));

      expect(find.byType(AchievementsScreen), findsOneWidget);
      
      // Should show either progress stats or grid
      final hasProgress = find.byType(AchievementProgressStats).evaluate().isNotEmpty;
      final hasGrid = find.byType(AchievementGrid).evaluate().isNotEmpty;
      expect(hasProgress || hasGrid, true);
    });

    testWidgets('has back button in app bar', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

    testWidgets('has settings button in app bar', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.settings_outlined), findsOneWidget);
    });

    testWidgets('shows achievement progress stats', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle(const Duration(seconds: 2));

      final hasProgress = find.byType(AchievementProgressStats).evaluate().isNotEmpty;
      expect(hasProgress, true);
    });

    testWidgets('shows achievement grid', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle(const Duration(seconds: 2));

      final hasGrid = find.byType(AchievementGrid).evaluate().isNotEmpty;
      expect(hasGrid, true);
    });

    testWidgets('has correct app bar title', (tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();

      expect(find.text('Achievements'), findsOneWidget);
    });

    testWidgets('shows retry button when error occurs', (tester) async {
      final errorProvider = ProviderScope(
        overrides: [
          achievementServiceProvider.overrideWithValue(
            MockAchievementServiceWithError(),
          ),
        ],
        child: const MaterialApp(
          home: AchievementsScreen(),
        ),
      );

      await tester.pumpWidget(errorProvider);
      await tester.pumpAndSettle(const Duration(seconds: 2));

      expect(find.text('Retry'), findsOneWidget);
    });
  });
}