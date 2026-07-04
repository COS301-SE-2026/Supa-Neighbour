import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../../models/user_model.dart';
import '../../models/auth_session.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  late User _user;
  bool _isLoading = true;

  // Mock achievements data
final List<Map<String, dynamic>> _achievements = [
  {'name': 'Reliable Helper', 'icon': Icons.emoji_events, 'color': AppColors.primaryTeal},
  {'name': 'Plant Expert', 'icon': Icons.eco, 'color': AppColors.success},
  {'name': 'Early Bird', 'icon': Icons.wb_sunny, 'color': AppColors.citrusYellow},
  {'name': 'Super Streak', 'icon': Icons.local_fire_department, 'color': Colors.orange},
];
  // Mock skills data
  List<String> _skills = ['Plants', 'Pets', 'Home Check-in'];

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  void _loadUserData() {
    //Replace with actual API call
    setState(() {
      _user = AuthSession.instance.currentUser ?? User.getMockUser();
      _isLoading = false;
    });
  }

  String _getLevel(double trustScore) {
    if (trustScore >= 4.8) return 'Gold';
    if (trustScore >= 4.5) return 'Silver';
    if (trustScore >= 4.0) return 'Bronze';
    return 'Rising';
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
        return AppColors.primaryTeal;
    }
  }

  List<Widget> _buildTrustStars(double score) {
    final fullStars = score.floor();
    final hasHalfStar = score - fullStars >= 0.5;
    final stars = <Widget>[];

    for (int i = 0; i < fullStars; i++) {
      stars.add(const Icon(
        Icons.star,
        size: 14,
        color: AppColors.citrusYellow,
      ));
    }

    if (hasHalfStar) {
      stars.add(const Icon(
        Icons.star_half,
        size: 14,
        color: AppColors.citrusYellow,
      ));
    }

    final remaining = 5 - stars.length;
    for (int i = 0; i < remaining; i++) {
      stars.add(const Icon(
        Icons.star_border,
        size: 14,
        color: AppColors.citrusYellow,
      ));
    }

    return stars;
  }

  void _showEditSkillsDialog() {
    final TextEditingController _skillController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(
          'Edit Skills',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Current skills:',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: _skills.map((skill) {
                return Chip(
                  label: Text(
                    skill,
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal,
                      fontSize: 13,
                    ),
                  ),
                  backgroundColor: AppColors.primaryTeal.withValues(alpha: 0.1),
                  deleteIcon: const Icon(
                    Icons.close,
                    size: 16,
                    color: AppColors.charcoal,
                  ),
                  onDeleted: () {
                    setState(() {
                      _skills.remove(skill);
                    });
                  },
                );
              }).toList(),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _skillController,
                    decoration: InputDecoration(
                      hintText: 'Add a skill...',
                      hintStyle: GoogleFonts.openSans(
                        color: AppColors.textGrey,
                        fontSize: 14,
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                        borderSide: const BorderSide(color: AppColors.primaryTeal),
                      ),
                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                        borderSide: const BorderSide(color: AppColors.primaryTeal, width: 2),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  onPressed: () {
                    if (_skillController.text.isNotEmpty) {
                      setState(() {
                        _skills.add(_skillController.text);
                        _skillController.clear();
                      });
                    }
                  },
                  icon: const Icon(
                    Icons.add_circle,
                    color: AppColors.primaryTeal,
                    size: 40,
                  ),
                ),
              ],
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Done',
              style: GoogleFonts.openSans(
                color: AppColors.primaryTeal,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        backgroundColor: AppColors.background,
        body: Center(
          child: CircularProgressIndicator(
            color: AppColors.primaryTeal,
          ),
        ),
      );
    }

    final level = _getLevel(4.8); // Mock trust score
    final levelColor = _getLevelColor(level);
    final xpProgress = 0.65; // Mock progress
    final nextLevelXp = 250;

    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        elevation: 0,
        title: Text(
          'My Profile',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.settings_outlined, color: AppColors.charcoal),
            onPressed: () {
              //Navigate to settings
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Profile Header
            _buildProfileHeader(level, levelColor),
            const SizedBox(height: 20),

            // XP Progress
            _buildXpProgress(level, xpProgress, nextLevelXp),
            const SizedBox(height: 20),

            // Stats Row
            _buildStatsRow(),
            const SizedBox(height: 20),

            // Skills Section
            _buildSkillsSection(),
            const SizedBox(height: 20),

            // Achievements Section
            _buildAchievementsSection(),
            const SizedBox(height: 20),

            // Task History
            _buildTaskHistory(),
            const SizedBox(height: 20),

            // Action Buttons
            _buildActionButtons(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileHeader(String level, Color levelColor) {
  return Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
      color: Colors.white,
      borderRadius: BorderRadius.circular(16),
      boxShadow: [
        BoxShadow(
          color: Colors.black.withValues(alpha: 0.04),
          blurRadius: 8,
          offset: const Offset(0, 2),
        ),
      ],
    ),
    child: Column(
      children: [
        // Profile Photo
        CircleAvatar(
          radius: 40,
          backgroundColor: AppColors.primaryTeal.withValues(alpha: 0.1),
          child: Text(
            _user.firstName[0],
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.w600,
              color: AppColors.primaryTeal,
            ),
          ),
        ),
        const SizedBox(height: 12),

        // Name and Level Badge
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Flexible(
              child: Text(
                _user.fullName,
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 22,
                  fontWeight: FontWeight.w600,
                ),
                overflow: TextOverflow.ellipsis,
              ),
            ),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
              decoration: BoxDecoration(
                color: levelColor.withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                level,
                style: GoogleFonts.openSans(
                  color: levelColor,
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 6),

        // Trust Score
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ..._buildTrustStars(4.8),
            const SizedBox(width: 8),
            Text(
              '4.8 ★',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal,
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        const SizedBox(height: 4),

        // Member Since
        Text(
          'Member since ${_user.createdAt.year}',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey,
            fontSize: 12,
          ),
        ),
        const SizedBox(height: 12),

        // Edit Profile Button
        OutlinedButton(
          onPressed: () {
            // Navigate to edit profile
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Edit Profile coming soon'),
                duration: Duration(seconds: 1),
              ),
            );
          },
          style: OutlinedButton.styleFrom(
            side: const BorderSide(color: AppColors.primaryTeal),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 6),
          ),
          child: Text(
            'Edit Profile',
            style: GoogleFonts.openSans(
              color: AppColors.primaryTeal,
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    ),
  );
}
  Widget _buildXpProgress(String level, double progress, int nextLevelXp) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                '$level Helper',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                '1,250 XP',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          ClipRRect(
            borderRadius: BorderRadius.circular(8),
            child: LinearProgressIndicator(
              value: progress,
              backgroundColor: AppColors.primaryTeal.withValues(alpha: 0.2),
              color: AppColors.citrusYellow,
              minHeight: 8,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '$nextLevelXp XP to Platinum',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStatsRow() {
  return Row(
    children: [
      Expanded(
        child: _buildStatItem('12', 'Tasks Created'),
      ),
      Expanded(
        child: _buildStatItem('47', 'Tasks Completed'),
      ),
      Expanded(
        child: _buildStatItem('3', 'Active Tasks'),
      ),
    ],
  );
}

  Widget _buildStatItem(String value, String label) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      decoration: BoxDecoration(
        color: AppColors.surfaceGrey,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          Text(
            value,
            style: GoogleFonts.poppins(
              color: AppColors.primaryTeal,
              fontSize: 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            label,
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 11,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildSkillsSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Skills & Services',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              GestureDetector(
                onTap: _showEditSkillsDialog,
                child: Text(
                  'Edit',
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal,
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: _skills.map((skill) {
              return Container(
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Text(
                  skill,
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal,
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildAchievementsSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Achievements',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              GestureDetector(
                onTap: () {
                  //Navigate to Achievements Screen
                },
                child: Text(
                  'View All',
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal,
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          SizedBox(
            height: 70,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: _achievements.length,
              itemBuilder: (context, index) {
                final achievement = _achievements[index];
                return Container(
                  width: 60,
                  margin: const EdgeInsets.only(right: 12),
                  child: Column(
                    children: [
                      Container(
                        width: 44,
                        height: 44,
                        decoration: BoxDecoration(
                          color: (achievement['color'] as Color).withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Icon(
                          achievement['icon'] as IconData,
                          color: achievement['color'] as Color,
                          size: 24,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        achievement['name'] as String,
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey,
                          fontSize: 9,
                        ),
                        textAlign: TextAlign.center,
                        overflow: TextOverflow.ellipsis,
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildTaskHistory() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Recent Tasks',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 10),
          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: 3,
            itemBuilder: (context, index) {
              final tasks = ['Watered plants', 'Walked dog', 'Collected packages'];
              final dates = ['Today', 'Yesterday', '2 days ago'];
              return ListTile(
                contentPadding: EdgeInsets.zero,
                title: Text(
                  tasks[index],
                  style: GoogleFonts.openSans(
                    color: AppColors.charcoal,
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                subtitle: Text(
                  dates[index],
                  style: GoogleFonts.openSans(
                    color: AppColors.textGrey,
                    fontSize: 12,
                  ),
                ),
                trailing: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppColors.citrusYellow,
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '+50 XP',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              );
            },
          ),
        ],
      ),
    );
  }

 Widget _buildActionButtons() {
  return Column(
    children: [
      SizedBox(
        width: double.infinity,
        child: OutlinedButton(
          onPressed: () {
            //Navigate to privacy settings
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Privacy Settings coming soon'),
                duration: Duration(seconds: 1),
              ),
            );
          },
          style: OutlinedButton.styleFrom(
            side: const BorderSide(color: AppColors.textGrey),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.symmetric(vertical: 14),
          ),
          child: Text(
            'Privacy Settings',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
      const SizedBox(height: 12),
      SizedBox(
        width: double.infinity,
        child: OutlinedButton(
          onPressed: () {
            _showLogoutDialog();
          },
          style: OutlinedButton.styleFrom(
            side: const BorderSide(color: AppColors.error),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.symmetric(vertical: 14),
          ),
          child: Text(
            'Logout',
            style: GoogleFonts.openSans(
              color: AppColors.error,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ),
    ],
  );
}

  void _showLogoutDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(
          'Logout?',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Text(
          'Are you sure you want to logout?',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal,
            fontSize: 14,
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey,
                fontSize: 14,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () {
              //Implement logout
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Logged out successfully'),
                  backgroundColor: AppColors.success,
                ),
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.error,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Logout',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }
}