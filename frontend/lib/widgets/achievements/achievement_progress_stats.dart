import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';

class AchievementProgressStats extends StatelessWidget {
  final int earnedCount;
  final int totalCount;
  final double progressPercentage;

  const AchievementProgressStats({
    super.key,
    required this.earnedCount,
    required this.totalCount,
    required this.progressPercentage,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16.0),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.baseline,
            textBaseline: TextBaseline.alphabetic,
            children: [
              Text(
                '$earnedCount ',
                style: GoogleFonts.poppins(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: AppColors.primary(context),
                ),
              ),
              Text(
                '/ $totalCount Achievements Earned',
                style: GoogleFonts.poppins(
                  fontSize: 18,
                  fontWeight: FontWeight.w500,
                  color: AppColors.primary(context).withValues(alpha: 0.9),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          // Progress Bar
          Container(
            height: 24,
            width: double.infinity,
            padding: const EdgeInsets.all(3),
            decoration: BoxDecoration(
              color: AppColors.primary(context),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Stack(
              children: [
                FractionallySizedBox(
                  widthFactor: progressPercentage,
                  child: Container(
                    decoration: BoxDecoration(
                      color: AppColors.citrusYellow(context),
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                ),
                Align(
                  alignment: Alignment.centerRight,
                  child: Padding(
                    padding: const EdgeInsets.only(right: 8.0),
                    child: Text(
                      '${(progressPercentage * 100).toInt()}%',
                      style: TextStyle(
                        color: AppColors.background(context),
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}