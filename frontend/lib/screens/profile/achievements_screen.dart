import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/achievement_model.dart';
import '../../widgets/achievements/achievement_progress_stats.dart';
import '../../widgets/achievements/achievement_grid.dart';
import 'package:supa_neighbour/screens/profile/settings_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class AchievementsScreen extends ConsumerStatefulWidget {
class AchievementsScreen extends ConsumerStatefulWidget {
  const AchievementsScreen({super.key});

  @override
  ConsumerState<AchievementsScreen> createState() => _AchievementsScreenState();
  ConsumerState<AchievementsScreen> createState() => _AchievementsScreenState();
}

class _AchievementsScreenState extends ConsumerState<AchievementsScreen> {
class _AchievementsScreenState extends ConsumerState<AchievementsScreen> {
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
  Future<void> _fetchAchievements() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final achievementService = ref.read(achievementServiceProvider);
      final response = await achievementService.getAchievements();
      final achievementService = ref.read(achievementServiceProvider);
      final response = await achievementService.getAchievements();
      if (mounted) {
        setState(() {
          _earnedAchievements = response.earned;
          _unearnedAchievements = response.unearned;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString().replaceAll('Exception: ', '');
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.primaryTeal(context)),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: Text(
          'Achievements',
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontSize: 28,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.settings_outlined, color: AppColors.charcoal(context)),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const SettingsScreen(),
                ),
              );
                context,
                MaterialPageRoute(
                  builder: (context) => const SettingsScreen(),
                ),
              );
            },
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return Center(
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(AppColors.primaryTeal(context)),
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
              color: AppColors.error(context).withValues(alpha: 0.5),
            ),
            const SizedBox(height: 16),
            Text(
              'Failed to load achievements',
              style: GoogleFonts.poppins(
                color: AppColors.error(context),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _error!,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
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
                  color: AppColors.primaryTeal(context),
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