import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/leaderboard_model.dart';
import '../../services/leaderboard_service.dart';


class LeaderboardScreen extends StatefulWidget {
  const LeaderboardScreen({super.key});

  @override
  State<LeaderboardScreen> createState() => _LeaderboardScreenState();
}

class _LeaderboardScreenState extends State<LeaderboardScreen> {
  String _selectedPeriod = 'week';
  LeaderboardData? _leaderboardData;
  bool _isLoading = true;
  String? _errorMessage;

  final LeaderboardService _leaderboardService = LeaderboardService();

  @override
  void initState() {
    super.initState();
    _loadLeaderboard();
  }

  Future<void> _loadLeaderboard() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final data = await _leaderboardService.getLeaderboard(
        period: _selectedPeriod,
        rankBy: 'averageRating',
        limit: 20,
      );

      setState(() {
        _leaderboardData = data;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
  }

  void _changePeriod(String period) {
    setState(() {
      _selectedPeriod = period;
    });
    _loadLeaderboard();
  }

  Color _getLevelColor(BuildContext context, String level) {
    switch (level) {
      case 'Gold':
        return const Color(0xFFE9C46A);
      case 'Silver':
        return const Color(0xFFC0C0C0);
      case 'Bronze':
        return const Color(0xFFCD7F32);
      default:
        return AppColors.primaryTeal(context);
    }
  }

  Widget _buildMedalIcon(BuildContext context, int rank) {
    if (rank == 1) {
      return const Icon(Icons.emoji_events, color: Color(0xFFE9C46A), size: 24);
    } else if (rank == 2) {
      return const Icon(Icons.emoji_events, color: Color(0xFFC0C0C0), size: 22);
    } else if (rank == 3) {
      return const Icon(Icons.emoji_events, color: Color(0xFFCD7F32), size: 22);
    } else {
      return Container(
        width: 24,
        height: 24,
        alignment: Alignment.center,
        child: Text(
          '$rank',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        title: Text(
          'Leaderboard',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.info_outline, color: AppColors.primaryTeal(context)),
            onPressed: () => _showInfoDialog(context),
          ),
        ],
      ),
      body: Column(
        children: [
          _buildPeriodTabs(context),
          Expanded(
            child: _buildBody(context),
          ),
        ],
      ),
    );
  }

  Widget _buildPeriodTabs(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: AppColors.background(context),
        border: Border(
          bottom: BorderSide(
            color: AppColors.surfaceGrey(context),
            width: 1,
          ),
        ),
      ),
      child: Row(
        children: [
          _buildPeriodTab(context, 'week', 'This Week'),
          _buildPeriodTab(context, 'month', 'This Month'),
          _buildPeriodTab(context, 'all', 'All Time'),
        ],
      ),
    );
  }

  Widget _buildPeriodTab(BuildContext context, String period, String label) {
    final isSelected = _selectedPeriod == period;

    return Expanded(
      child: GestureDetector(
        onTap: () => _changePeriod(period),
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 10),
          decoration: BoxDecoration(
            border: Border(
              bottom: BorderSide(
                color: isSelected ? AppColors.primaryTeal(context) : Colors.transparent,
                width: 3,
              ),
            ),
          ),
          child: Center(
            child: Text(
              label,
              style: GoogleFonts.openSans(
                color: isSelected ? AppColors.primaryTeal(context) : AppColors.textGrey(context),
                fontSize: 14,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildBody(BuildContext context) {
    if (_isLoading) {
      return Center(
        child: CircularProgressIndicator(
          color: AppColors.primaryTeal(context),
        ),
      );
    }

    if (_errorMessage != null) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.error_outline,
                size: 64,
                color: Colors.red.withOpacity(0.5),
              ),
              const SizedBox(height: 16),
              Text(
                'Failed to load leaderboard',
                style: GoogleFonts.poppins(
                  color: AppColors.error(context),
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                _errorMessage!,
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _loadLeaderboard,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryTeal(context),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.symmetric(
                    horizontal: 32,
                    vertical: 12,
                  ),
                ),
                child: Text(
                  'Retry',
                  style: GoogleFonts.openSans(
                    color: Colors.white,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
        ),
      );
    }

    if (_leaderboardData == null || _leaderboardData!.entries.isEmpty) {
      return _buildEmptyState(context);
    }

    final entries = _leaderboardData!.entries;
    final currentUserEntry = _leaderboardData!.currentUserEntry;

    return Column(
      children: [
        _buildTop3Circles(context),
        const SizedBox(height: 8),
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            itemCount: entries.length,
            itemBuilder: (context, index) {
              final entry = entries[index];
              return _buildLeaderboardItem(context, entry);
            },
          ),
        ),
        if (currentUserEntry != null &&
            !entries.any((e) => e.isCurrentUser))
          _buildYourRankCard(context, currentUserEntry),
      ],
    );
  }

  Widget _buildTop3Circles(BuildContext context) {
    final entries = _leaderboardData!.entries;
    final top3 = entries.take(3).toList();

    if (top3.length < 3) {
      return const SizedBox.shrink();
    }

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildTopCircle(context, top3[1]),
          _buildTopCircle(context, top3[0], isFirst: true),
          _buildTopCircle(context, top3[2]),
        ],
      ),
    );
  }

  Widget _buildTopCircle(BuildContext context, LeaderboardEntry entry, {bool isFirst = false}) {
    final size = isFirst ? 56.0 : 44.0;
    final fontSize = isFirst ? 22.0 : 16.0;

    return Container(
      margin: EdgeInsets.symmetric(horizontal: isFirst ? 12 : 6),
      child: Column(
        children: [
          Stack(
            alignment: Alignment.center,
            children: [
              CircleAvatar(
                radius: size / 2,
                backgroundColor: AppColors.primaryTeal(context).withOpacity(0.1),
                child: Text(
                  entry.displayName[0],
                  style: TextStyle(
                    fontSize: fontSize,
                    fontWeight: FontWeight.w600,
                    color: AppColors.primaryTeal(context),
                  ),
                ),
              ),
              if (isFirst)
                Positioned(
                  bottom: 0,
                  right: 0,
                  child: Container(
                    padding: const EdgeInsets.all(2),
                    decoration: const BoxDecoration(
                      color: Color(0xFFE9C46A),
                      shape: BoxShape.circle,
                    ),
                    child: const Icon(
                      Icons.star,
                      color: Colors.white,
                      size: 14,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            entry.displayName.split(' ').first,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 10,
              fontWeight: FontWeight.w500,
            ),
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }

  Widget _buildLeaderboardItem(BuildContext context, LeaderboardEntry entry) {
    final bgColor = entry.isCurrentUser
        ? AppColors.primaryTeal(context).withOpacity(0.05)
        : Colors.transparent;

    return GestureDetector(
      onTap: () {
        _showHelperDetails(context, entry);
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 2),
        padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 8),
        decoration: BoxDecoration(
          color: bgColor,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          children: [
            SizedBox(
              width: 36,
              child: _buildMedalIcon(context, entry.rank),
            ),
            CircleAvatar(
              radius: 18,
              backgroundColor: AppColors.primaryTeal(context).withOpacity(0.1),
              child: Text(
                entry.displayName[0],
                style: TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                  color: AppColors.primaryTeal(context),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        entry.displayName,
                        style: GoogleFonts.poppins(
                          color: entry.isCurrentUser
                              ? AppColors.primaryTeal(context)
                              : AppColors.charcoal(context),
                          fontSize: 14,
                          fontWeight: entry.isCurrentUser
                              ? FontWeight.w700
                              : FontWeight.w500,
                        ),
                      ),
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 1,
                        ),
                        decoration: BoxDecoration(
                          color: _getLevelColor(context, entry.level).withOpacity(0.2),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          entry.level,
                          style: GoogleFonts.openSans(
                            color: _getLevelColor(context, entry.level),
                            fontSize: 9,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                      if (entry.isCurrentUser)
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 6,
                            vertical: 1,
                          ),
                          decoration: BoxDecoration(
                            color: AppColors.primaryTeal(context).withOpacity(0.1),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            'You',
                            style: GoogleFonts.openSans(
                              color: AppColors.primaryTeal(context),
                              fontSize: 9,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      ..._buildTrustStars(entry.trustScore),
                      const SizedBox(width: 4),
                      Text(
                        '${entry.trustScore.toStringAsFixed(1)} ★',
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey(context),
                          fontSize: 11,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Text(
                        '${entry.xp} XP',
                        style: GoogleFonts.openSans(
                          color: AppColors.primaryTeal(context),
                          fontSize: 11,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  List<Widget> _buildTrustStars(double score) {
    final fullStars = score.floor();
    final hasHalfStar = score - fullStars >= 0.5;
    final stars = <Widget>[];

    for (int i = 0; i < fullStars; i++) {
      stars.add(const Icon(
        Icons.star,
        size: 12,
        color: Color(0xFFE9C46A),
      ));
    }

    if (hasHalfStar) {
      stars.add(const Icon(
        Icons.star_half,
        size: 12,
        color: Color(0xFFE9C46A),
      ));
    }

    final remaining = 5 - stars.length;
    for (int i = 0; i < remaining; i++) {
      stars.add(const Icon(
        Icons.star_border,
        size: 12,
        color: Color(0xFFE9C46A),
      ));
    }

    return stars;
  }

  Widget _buildYourRankCard(BuildContext context, LeaderboardEntry entry) {
    final nextRank = entry.rank - 1;
    const xpNeeded = 100;
    const progress = 0.65;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: AppColors.primaryTeal(context).withOpacity(0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: AppColors.primaryTeal(context),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  '#${entry.rank}',
                  style: GoogleFonts.poppins(
                    color: Colors.white,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const SizedBox(width: 8),
              Text(
                'You',
                style: GoogleFonts.poppins(
                  color: AppColors.primaryTeal(context),
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const Spacer(),
              Row(
                children: [
                  ..._buildTrustStars(entry.trustScore),
                  const SizedBox(width: 4),
                  Text(
                    entry.trustScore.toStringAsFixed(1),
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Text(
                    '${entry.xp} XP',
                    style: GoogleFonts.openSans(
                      color: AppColors.primaryTeal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ],
          ),
          const SizedBox(height: 6),
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '$xpNeeded XP to reach Top $nextRank',
                      style: GoogleFonts.openSans(
                        color: AppColors.textGrey(context),
                        fontSize: 11,
                      ),
                    ),
                    const SizedBox(height: 2),
                    ClipRRect(
                      borderRadius: BorderRadius.circular(4),
                      child: LinearProgressIndicator(
                        value: progress,
                        backgroundColor: AppColors.primaryTeal(context).withOpacity(0.2),
                        color: const Color(0xFFE9C46A),
                        minHeight: 6,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.leaderboard,
            size: 64,
            color: AppColors.textGrey(context).withOpacity(0.5),
          ),
          const SizedBox(height: 16),
          Text(
            'No Leaderboard Data',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Helpers will appear here as they complete tasks',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  void _showHelperDetails(BuildContext context, LeaderboardEntry entry) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Row(
          children: [
            CircleAvatar(
              backgroundColor: AppColors.primaryTeal(context).withOpacity(0.1),
              child: Text(
                entry.displayName[0],
                style: TextStyle(
                  color: AppColors.primaryTeal(context),
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Text(
                entry.displayName,
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                ..._buildTrustStars(entry.trustScore),
                const SizedBox(width: 8),
                Text(
                  '${entry.trustScore.toStringAsFixed(1)} ★',
                  style: GoogleFonts.openSans(
                    color: AppColors.charcoal(context),
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              'Level: ${entry.level}',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            Text(
              'XP: ${entry.xp}',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            Text(
              'Tasks Completed: ${entry.completedTasks}',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Close',
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

  void _showInfoDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(
          'How Rankings Work',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 18,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              '• Ranked by trust score',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '• Trust score is calculated from completed tasks and ratings',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '• Leaderboard shows top helpers in your neighbourhood',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '🏅 Gold = Trust ≥ 4.8',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            Text(
              '🥈 Silver = Trust ≥ 4.5',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            Text(
              '🥉 Bronze = Trust ≥ 4.0',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Got it',
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
}