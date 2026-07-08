import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/achievement_model.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_grid.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_card.dart';

void main() {
  group('AchievementGrid Tests', () {
    testWidgets('displays empty grid when no achievements', (tester) async {
      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementGrid(
              achievements: [],
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.byType(GridView), findsOneWidget);
      expect(find.byType(AchievementCard), findsNothing);
    });

    testWidgets('displays achievements in grid correctly', (tester) async {
      final achievements = [
        Achievement.earned(
          badgeId: 5,
          name: 'Test 1',
          description: 'Description 1',
          awardedOn: '2026-05-01',
        ),
        Achievement.unearned(
          badgeId: 2,
          name: 'Test 2',
          description: 'Description 2',
          progress: '3/5',
        ),
        Achievement.unearned(
          badgeId: 7,
          name: 'Test 3',
          description: 'Description 3',
          progress: '1/5',
        ),
      ];

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementGrid(
              achievements: achievements,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.byType(AchievementCard), findsNWidgets(3));
      expect(find.text('5'), findsOneWidget);
      expect(find.text('2'), findsOneWidget);
      expect(find.text('7'), findsOneWidget);
    });

    testWidgets('handles mix of earned and unearned achievements', (tester) async {
      final achievements = [
        Achievement.earned(
          badgeId: 5,
          name: 'Earned Badge',
          description: 'Earned description',
          awardedOn: '2026-05-01',
        ),
        Achievement.unearned(
          badgeId: 2,
          name: 'Unearned Badge',
          description: 'Unearned description',
          progress: '2/5',
        ),
      ];

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementGrid(
              achievements: achievements,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('Earned'), findsOneWidget);
      expect(find.text('2/5'), findsOneWidget);
    });

    testWidgets('grid has correct cross axis count of 3', (tester) async {
      final achievements = List.generate(
        6,
        (index) => Achievement.unearned(
          badgeId: index,
          name: 'Badge $index',
          description: 'Description $index',
          progress: '0/5',
        ),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementGrid(
              achievements: achievements,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      final gridView = tester.widget<GridView>(find.byType(GridView));
      expect(gridView.gridDelegate, isA<SliverGridDelegateWithFixedCrossAxisCount>());
      
      final delegate = gridView.gridDelegate as SliverGridDelegateWithFixedCrossAxisCount;
      expect(delegate.crossAxisCount, 3);
    });
  });
}