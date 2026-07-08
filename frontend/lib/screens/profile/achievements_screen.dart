import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/achievement_model.dart';
import '../../widgets/achievements/achievement_progress_stats.dart';
import '../../widgets/achievements/achievement_grid.dart';

class AchievementsScreen extends StatefulWidget {
  const AchievementsScreen({super.key});

  @override
  State<AchievementsScreen> createState() => _AchievementsScreenState();
}

class _AchievementsScreenState extends State<AchievementsScreen> {
  List<Achievement> _earnedAchievements = [];
  List<Achievement> _unearnedAchievements = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _fetchAchievements();
  }

  Future<void> _fetchAchievements() async {
    await Future.delayed(const Duration(milliseconds: 500));
    if (mounted) {
      setState(() {
        _earnedAchievements = _getMockEarnedAchievements();
        _unearnedAchievements = _getMockUnearnedAchievements();
        _isLoading = false;
      });
    }
  }

  List<Achievement> _getMockEarnedAchievements() {
    return [
      Achievement.earned(
        badgeId: 5,
        name: 'Home Repair Specialist',
        description: 'Complete 10 home repair tasks',
        awardedOn: '2026-05-01',
      ),
      Achievement.earned(
        badgeId: 3,
        name: 'Green Thumb',
        description: 'Complete 5 gardening tasks',
        awardedOn: '2026-04-15',
      ),
      Achievement.earned(
        badgeId: 8,
        name: 'Pet Whisperer',
        description: 'Complete 8 pet care tasks',
        awardedOn: '2026-06-10',
      ),
      Achievement.earned(
        badgeId: 12,
        name: 'Package Pro',
        description: 'Complete 15 package deliveries',
        awardedOn: '2026-05-20',
      ),
      Achievement.earned(
        badgeId: 9,
        name: 'Gardening Guru',
        description: 'Complete 10 gardening tasks',
        awardedOn: '2026-04-28',
      ),
    ];
  }

  List<Achievement> _getMockUnearnedAchievements() {
    return [
      Achievement.unearned(
        badgeId: 2,
        name: 'Pet Care Helper',
        description: 'Complete 5 pet care tasks',
        progress: '3/5',
      ),
      Achievement.unearned(
        badgeId: 7,
        name: 'Package Master',
        description: 'Complete 20 package deliveries',
        progress: '12/20',
      ),
      Achievement.unearned(
        badgeId: 4,
        name: 'Neighbourhood Hero',
        description: 'Complete 50 tasks',
        progress: '42/50',
      ),
      Achievement.unearned(
        badgeId: 1,
        name: 'First Steps',
        description: 'Complete your first task',
        progress: '0/1',
      ),
      Achievement.unearned(
        badgeId: 6,
        name: 'Handyman Helper',
        description: 'Complete 5 repair tasks',
        progress: '2/5',
      ),
      Achievement.unearned(
        badgeId: 10,
        name: 'Community Champion',
        description: 'Complete 25 tasks',
        progress: '18/25',
      ),
      Achievement.unearned(
        badgeId: 11,
        name: 'Reliable Neighbour',
        description: 'Complete 10 tasks with 5-star rating',
        progress: '6/10',
      ),
      Achievement.unearned(
        badgeId: 13,
        name: 'Pool Pal',
        description: 'Complete 5 pool maintenance tasks',
        progress: '1/5',
      ),
      Achievement.unearned(
        badgeId: 14,
        name: 'Bin Buddy',
        description: 'Complete 8 bin collection tasks',
        progress: '4/8',
      ),
    ];
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: AppColors.primary),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: Text(
          'Achievements',
          style: GoogleFonts.poppins(
            color: AppColors.primary,
            fontSize: 28,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.settings, color: AppColors.primary),
            onPressed: () {},
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(AppColors.primary),
        ),
      );
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Colors.red.withValues(alpha: 0.5),
            ),
            const SizedBox(height: 16),
            Text(
              'Failed to load achievements',
              style: GoogleFonts.poppins(
                color: Colors.red,
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _error!,
              style: GoogleFonts.openSans(
                color: Colors.grey[600],
                fontSize: 14,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            TextButton(
              onPressed: _fetchAchievements,
              child: Text(
                'Retry',
                style: GoogleFonts.openSans(
                  color: AppColors.primary,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
      );
    }

    final allAchievements = [..._earnedAchievements, ..._unearnedAchievements];
    final totalEarned = _earnedAchievements.length;
    final totalCount = allAchievements.length;
    final progress = totalCount > 0 ? totalEarned / totalCount : 0.0;

    return SingleChildScrollView(
      child: Column(
        children: [
          AchievementProgressStats(
            earnedCount: totalEarned,
            totalCount: totalCount,
            progressPercentage: progress,
          ),
          const SizedBox(height: 16),
          AchievementGrid(
            achievements: allAchievements,
          ),
          const SizedBox(height: 80),
        ],
      ),
    );
  }
}