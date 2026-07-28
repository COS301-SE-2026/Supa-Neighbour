import 'package:flutter/material.dart';
import '../../models/achievement_model.dart';
import 'achievement_card.dart';

class AchievementGrid extends StatelessWidget {
  final List<Achievement> achievements;

  const AchievementGrid({
    super.key,
    required this.achievements,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0),
      child: GridView.builder(
        shrinkWrap: true, // Add this
        physics: const NeverScrollableScrollPhysics(), // Add this
        itemCount: achievements.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 12,
          mainAxisSpacing: 16,
          childAspectRatio: 0.58,
        ),
        itemBuilder: (context, index) {
          final achievement = achievements[index];
          final isEarned = achievement.isEarned;
          return AchievementCard(
            achievement: achievement,
            isActive: isEarned,
          );
        },
      ),
    );
  }
}