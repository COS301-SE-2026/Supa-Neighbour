import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/achievement_model.dart';
import 'package:supa_neighbour/widgets/achievements/achievement_card.dart';

void main() {
  group('AchievementCard Tests', () {
    testWidgets('displays earned achievement correctly', (tester) async {
      final achievement = Achievement.earned(
        badgeId: 5,
        name: 'Home Repair Specialist',
        description: 'Complete 10 home repair tasks',
        awardedOn: '2026-05-01',
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementCard(
              achievement: achievement,
              isActive: true,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.text('5'), findsOneWidget);
      expect(find.text('Earned'), findsOneWidget);
    });

    testWidgets('displays unearned achievement with progress correctly', (tester) async {
      final achievement = Achievement.unearned(
        badgeId: 2,
        name: 'Pet Care Helper',
        description: 'Complete 5 pet care tasks',
        progress: '3/5',
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementCard(
              achievement: achievement,
              isActive: false,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      //going to modify

      expect(find.text('2'), findsOneWidget);
      expect(find.text('3/5'), findsOneWidget);
      expect(find.text('Earned'), findsNothing);
    });

    testWidgets('applies correct styling for active achievements', (tester) async {
      final achievement = Achievement.earned(
        badgeId: 5,
        name: 'Test Badge',
        description: 'Test description',
        awardedOn: '2026-05-01',
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementCard(
              achievement: achievement,
              isActive: true,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      final container = tester.widget<Container>(find.byType(Container).first);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration, isNotNull);
    });

    testWidgets('applies correct styling for inactive achievements', (tester) async {
      final achievement = Achievement.unearned(
        badgeId: 2,
        name: 'Test Badge',
        description: 'Test description',
        progress: '0/5',
      );

      await tester.pumpWidget(
        MaterialApp(
          home: Scaffold(
            body: AchievementCard(
              achievement: achievement,
              isActive: false,
            ),
          ),
        ),
      );
      await tester.pumpAndSettle();

      expect(find.byType(AchievementCard), findsOneWidget);
    });
  });
}