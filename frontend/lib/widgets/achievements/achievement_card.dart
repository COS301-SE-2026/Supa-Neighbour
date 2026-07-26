import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/achievement_model.dart';

class AchievementCard extends StatelessWidget {
  final Achievement achievement;
  final bool isActive;

  const AchievementCard({
    super.key,
    required this.achievement,
    required this.isActive,
  });

  @override
  Widget build(BuildContext context) {
    final themeColor = isActive ? AppColors.primary(context) : Colors.grey.shade400;
    final badgeBgColor = isActive 
        ? const Color(0xFFAFDCDA) 
        : Colors.grey.shade300;

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: isActive ? AppColors.primary(context) : Colors.transparent,
          width: isActive ? 1.5 : 0,
        ),
        boxShadow: [
          BoxShadow(
            color: isActive 
                ? AppColors.primary(context).withValues(alpha: 0.2) 
                : Colors.black.withValues(alpha: 0.05),
            blurRadius: 10,
            spreadRadius: 1,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 12),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Circular Badge - Number only
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              color: badgeBgColor,
              shape: BoxShape.circle,
            ),
            child: Center(
              child: Text(
                '${achievement.badgeId}',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: isActive ? AppColors.primary(context) : Colors.grey[500],
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          // Badge Title
          Text(
            _truncateText(achievement.name, 15),
            textAlign: TextAlign.center,
            style: GoogleFonts.poppins(
              fontSize: 11,
              fontWeight: FontWeight.bold,
              color: themeColor,
            ),
          ),
          // Brief Description
          Text(
            _truncateText(achievement.description, 20),
            textAlign: TextAlign.center,
            style: GoogleFonts.openSans(
              fontSize: 8,
              color: themeColor.withValues(alpha: 0.8),
              height: 1.2,
            ),
          ),
          const SizedBox(height: 4),
          // Progress or Earned Tag
          if (isActive) ...[
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFEBB13D).withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                'Earned',
                style: const TextStyle(
                  fontSize: 8,
                  fontWeight: FontWeight.bold,
                  color: Color(0xFFD99116),
                ),
              ),
            ),
          ] else if (achievement.progress != null) ...[
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: Colors.grey[200],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                achievement.progress!,
                style: TextStyle(
                  fontSize: 8,
                  fontWeight: FontWeight.bold,
                  color: Colors.grey[600],
                ),
              ),
            ),
          ] else ...[
            const SizedBox(height: 16),
          ],
        ],
      ),
    );
  }

  String _truncateText(String text, int maxLength) {
    if (text.length <= maxLength) return text;
    return '${text.substring(0, maxLength)}...';
  }
}