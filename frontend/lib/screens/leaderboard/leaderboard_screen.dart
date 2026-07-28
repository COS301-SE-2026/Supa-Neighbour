import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'helper_profile_preview_screen.dart';
import '../../constants/app_colors.dart';
import '../../models/leaderboard_model.dart';
import '../../models/user_model.dart';
import '../help/help_menu_screen.dart';


class LeaderboardScreen extends StatefulWidget {
  const LeaderboardScreen({super.key});

  @override
  State<LeaderboardScreen> createState() => _LeaderboardScreenState();
}

class _LeaderboardScreenState extends State<LeaderboardScreen> {
  LeaderboardData? _leaderboardData;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadLeaderboard();
  }

  Future<void> _loadLeaderboard() async {
    setState(() {
      _isLoading = true;
    });

    //Replace with actual API call later
    await Future.delayed(const Duration(seconds: 1));

    //Mock data
    final mockEntries = [
      LeaderboardEntry(
        rank: 1,
        userId: 'helper_1',
        displayName: 'Sarah Johnson',
        trustScore: 4.9,
        level: 'Gold',
        xp: 1250,
        completedTasks: 47,
      ),
      LeaderboardEntry(
        rank: 2,
        userId: 'helper_2',
        displayName: 'Mike Johnson',
        trustScore: 4.7,
        level: 'Silver',
        xp: 980,
        completedTasks: 32,
      ),
      LeaderboardEntry(
        rank: 3,
        userId: 'helper_3',
        displayName: 'Lisa Wong',
        trustScore: 4.8,
        level: 'Bronze',
        xp: 850,
        completedTasks: 28,
      ),
      LeaderboardEntry(
        rank: 4,
        userId: 'helper_4',
        displayName: 'Tom Brown',
        trustScore: 4.6,
        level: 'Silver',
        xp: 720,
        completedTasks: 21,
      ),
      LeaderboardEntry(
        rank: 5,
        userId: 'helper_5',
        displayName: 'Sarah Adams',
        trustScore: 4.5,
        level: 'Bronze',
        xp: 650,
        completedTasks: 18,
      ),
      LeaderboardEntry(
        rank: 6,
        userId: 'helper_6',
        displayName: 'James Wilson',
        trustScore: 4.4,
        level: 'Bronze',
        xp: 580,
        completedTasks: 15,
      ),
      LeaderboardEntry(
        rank: 7,
        userId: 'helper_7',
        displayName: 'Emily Davis',
        trustScore: 4.3,
        level: 'Bronze',
        xp: 520,
        completedTasks: 12,
      ),
      LeaderboardEntry(
        rank: 8,
        userId: 'helper_8',
        displayName: 'David Miller',
        trustScore: 4.2,
        level: 'Bronze',
        xp: 480,
        completedTasks: 10,
      ),
    ];

    final currentUserEntry = LeaderboardEntry(
      rank: 12,
      userId: 'currentUser',
      displayName: 'You',
      trustScore: 4.2,
      level: 'Bronze',
      xp: 450,
      completedTasks: 9,
      isCurrentUser: true,
    );

        setState(() {
      _leaderboardData = LeaderboardData(
        period: 'week',
        entries: mockEntries,
        currentUserEntry: currentUserEntry,
      );
      _isLoading = false;
    });
  }

  Color _getLevelColor(String level) {
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

  Widget _buildMedalIcon(int rank) {
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
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.info_outline, color: AppColors.primaryTeal(context)),
            onPressed: () {
              _showInfoDialog();
            },
          ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: _isLoading
                ? Center(
                    child: CircularProgressIndicator(
                      color: AppColors.primaryTeal(context),
                    ),
                  )
                : _leaderboardData == null
                    ? _buildEmptyState()
                    : Column(
                        children: [
                              Padding(
                              padding: const EdgeInsets.only(top: 16, bottom: 8, left: 16),
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: Text(
                                  "Last Week's Top 3",
                                  style: GoogleFonts.poppins(
                                    color: AppColors.charcoal(context),
                                    fontSize: 18,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ),
                            ),
                            _buildTop3Circles(),
                            const SizedBox(height: 8),
                            Padding(
                              padding: const EdgeInsets.only(top: 16, bottom: 8, left: 16),
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: Text(
                                  'This Week',
                                  style: GoogleFonts.poppins(
                                    color: AppColors.charcoal(context),
                                    fontSize: 18,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ),
                            ),
                            Expanded(
                              child: ListView.builder(
                              padding: const EdgeInsets.symmetric(horizontal: 16),
                              itemCount: _leaderboardData!.entries.length,
                              itemBuilder: (context, index) {
                                final entry = _leaderboardData!.entries[index];
                                return _buildLeaderboardItem(entry);
                              },
                            ),
                          ),
                          if (_leaderboardData!.currentUserEntry != null)
                            _buildYourRankCard(_leaderboardData!.currentUserEntry!),
                        ],
                      ),
          ),
        ],
      ),
    );
  }

  Widget _buildTop3Circles() {
  final top3 = _leaderboardData!.entries.take(3).toList();

  if (top3.length < 3) {
    return const SizedBox.shrink();
  }

  return Container(
    padding: const EdgeInsets.symmetric(vertical: 8),
    child: Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        _buildTopCircle(top3[1]),
        _buildTopCircle(top3[0], isFirst: true),
        _buildTopCircle(top3[2]),
      ],
    ),
  );
}

 Widget _buildTopCircle(LeaderboardEntry entry, {bool isFirst = false}) {
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
              backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
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

  Widget _buildLeaderboardItem(LeaderboardEntry entry) {
    final bgColor = entry.isCurrentUser
        ? AppColors.primaryTeal(context).withValues(alpha: 0.05)
        : Colors.transparent;

    return GestureDetector(
      onTap: () {
        // Get actual user data from API
        final mockHelper = User(
          id: entry.userId,
          email: '${entry.displayName.toLowerCase().replaceAll(' ', '.')}@example.com',
          firstName: entry.displayName.split(' ').first,
          lastName: entry.displayName.split(' ').last,
          createdAt: DateTime.now(),
        );
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => HelperProfilePreviewScreen(
              helper: mockHelper,
              showRequestButton: false,
            ),
          ),
        );
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
              child: _buildMedalIcon(entry.rank),
            ),
            CircleAvatar(
              radius: 18,
              backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
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
                          color: _getLevelColor(entry.level).withValues(alpha: 0.2),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Text(
                          entry.level,
                          style: GoogleFonts.openSans(
                            color: _getLevelColor(entry.level),
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
                            color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
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

  Widget _buildYourRankCard(LeaderboardEntry entry) {
  final nextRank = entry.rank - 1;
  const xpNeeded = 100; // Mocks
  const progress = 0.65;

  return Container(
    margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
    decoration: BoxDecoration(
      color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
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
                      backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.2),
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
}  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.leaderboard,
            size: 64,
            color: AppColors.textGrey(context).withValues(alpha: 0.5),
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
          ),
        ],
      ),
    );
  }

  void _showInfoDialog() {
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
              '• Leaderboard resets monthly',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '• Gold = Trust ≥ 4.8',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            Text(
              '• Silver = Trust ≥ 4.5',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
              ),
            ),
            Text(
              '• Bronze = Trust ≥ 4.0',
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