import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../constants/skill_options.dart';
import '../../constants/badge_visuals.dart';
import 'package:supa_neighbour/screens/profile/achievements_screen.dart';
import 'package:supa_neighbour/screens/profile/settings_screen.dart';
import '../../services/auth_service.dart';
import '../auth/splash_screen.dart';
import '../../services/profile_service.dart';
import '../../models/user_profile_response.dart';
import '../profile/privacy_settings_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  UserProfileResponse? _profile;
  bool _isLoading = true;
  String? _errorMessage;

  List<String> _localSkillEdits = [];

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  Future<void> _loadProfile() async {
    
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try{
      final profile = await UserProfileService().getMyProfile();
      setState(() {
        _profile = profile;
        _localSkillEdits = List.from(profile.skills);
        _isLoading = false;
      });
    }catch(e){
      setState((){
        _errorMessage = 'Failed to load profile. Please try again.';
        _isLoading = false;
      });
    }
  }

  Color _getLevelColor(String? level, BuildContext context) {
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

  List<Widget> _buildTrustStars(double score, BuildContext context) {
    final fullStars = score.floor();
    final hasHalfStar = score - fullStars >= 0.5;
    final stars = <Widget>[];

    for (int i = 0; i < fullStars; i++) {
      stars.add( Icon(
        Icons.star,
        size: 14,
        color: AppColors.citrusYellow(context),
      ));
    }

    if (hasHalfStar) {
      stars.add( Icon(
        Icons.star_half,
        size: 14,
        color: AppColors.citrusYellow(context),
      ));
    }

    final remaining = 5 - stars.length;
    for (int i = 0; i < remaining; i++) {
      stars.add(Icon(
        Icons.star_border,
        size: 14,
        color: AppColors.citrusYellow(context),
      ));
    }

    return stars;
  }

  void _showEditSkillsDialog() {
    if(_profile?.currentXp == null){
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Skills are only available for registered helpers')),
      );
      return;
    }

    final Set<String> selectedSkills = Set.from(_localSkillEdits);
    bool isSaving = false;

    showDialog(
      context: context,
      builder: (dialogContext) => StatefulBuilder(
        builder: (dialogContext, setDialogState){
          return AlertDialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            title:Text(
              'Edit Skills',
              style: GoogleFonts.openSans(color: AppColors.charcoal(context), fontSize:20, fontWeight: FontWeight.w600),
            ),
            content:SingleChildScrollView(
              child:Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: kAvailableSkills.map((skill){
                  return CheckboxListTile(
                    title: Text(skill, style: GoogleFonts.openSans(color: AppColors.charcoal(context), fontSize: 14)),
                    value: selectedSkills.contains(skill), 
                    activeColor: AppColors.primaryTeal(context),
                    contentPadding: EdgeInsets.zero,
                    onChanged: (checked){
                      setDialogState(() {
                        if(checked == true){
                          selectedSkills.add(skill);
                        }else{
                          selectedSkills.remove(skill);
                        }
                      });
                    },
                  );
                }).toList(),
              ),
            ),
            actions: [
              TextButton(
                onPressed: isSaving ? null : () => Navigator.pop(dialogContext),
                child: Text('Cancel', style: GoogleFonts.openSans(color: AppColors.textGrey(context))),
              ),
              TextButton(onPressed: isSaving ? null : () async{
                setDialogState (() => isSaving = true);

                try{
                  final response = await UserProfileService().updateSkills(selectedSkills.toList());

                  setState (() {
                    _localSkillEdits = response.skills ?? selectedSkills.toList();
                    _profile = UserProfileResponse(
                      userId: _profile!.userId, 
                      displayName: response.displayName, 
                      neighbourhood:_profile!.neighbourhood, 
                      level: _profile!.level,
                      currentXp: _profile!.currentXp, 
                      trustScore: _profile!.trustScore,
                      skills: _profile!.skills,
                      achievements: _profile!.achievements,
                      recentTasks: _profile!.recentTasks,
                      completedTasks: _profile!.completedTasks,
                      activeTasks: _profile!.activeTasks,
                      createdTasks: _profile!.createdTasks,
                    );
                  });
                  if(dialogContext.mounted) Navigator.pop(dialogContext);
                }catch(e){
                  setDialogState(() => isSaving = false);
                  if(dialogContext.mounted){
                    ScaffoldMessenger.of(dialogContext).showSnackBar(
                      const SnackBar(content: Text('Failed to update skills. Please try again.')),
                    );
                  }
                }
              },
              child: isSaving ? const SizedBox(width: 16, height: 16, child: CircularProgressIndicator(strokeWidth:2))
              : Text('Save', style: GoogleFonts.openSans(color: AppColors.primaryTeal(context), fontWeight: FontWeight.w600)),
              ),
            ],
          );
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return  Scaffold(
        backgroundColor: AppColors.background(context),
        body: Center(
          child: CircularProgressIndicator(
            color: AppColors.primaryTeal(context),
          ),
        ),
      );
    }

    if(_errorMessage != null || _profile == null){
      return Scaffold(
        backgroundColor: AppColors.background(context),
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children:[
              Text(_errorMessage ?? 'Something went wrong', style: TextStyle(color: AppColors.charcoal(context))),
              const SizedBox(height: 12),
              ElevatedButton(onPressed: _loadProfile, child: const Text('Retry')),
            ],
          ),
        ),
      );
    }

    final profile = _profile!;
    final levelColor = _getLevelColor(profile.level, context);


    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        title: Text(
          'My Profile',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.settings_outlined, color: AppColors.charcoal(context)),
            onPressed: () =>
              Navigator.push(
              context,
              MaterialPageRoute(
              builder: (context) => const SettingsScreen())),
          ),
        ],
      ),
      body: RefreshIndicator (
        onRefresh: _loadProfile, 
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildProfileHeader(profile, levelColor),
              const SizedBox(height: 20),
              if(profile.currentXp != null) ...[
                _buildXpCard(profile),
                const SizedBox(height: 20),
              ],
              _buildStatsRow(profile),
              const SizedBox(height: 20),
              _buildSkillsSection(),
              const SizedBox(height: 20),
              _buildAchievementsSection(profile),
              const SizedBox(height: 20),
              _buildTaskHistory(profile),
              const SizedBox(height: 20),
              _buildActionButtons(),
              const SizedBox(height: 32),
          ],
        ),
      ),
    )
  );
}

  Widget _buildProfileHeader(UserProfileResponse profile, Color levelColor) {
  return Container(
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
      color: Theme.of(context).brightness == Brightness.dark 
    ? AppColors.surfaceGrey(context) 
    : Colors.white,
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
        CircleAvatar(
          radius: 40,
          backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
          child: Text(
            profile.displayName.isNotEmpty ? profile.displayName[0] : '?',
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.w600,
              color: AppColors.primaryTeal(context),
            ),
          ),
        ),
        const SizedBox(height: 12),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Flexible(
              child: Text(
                profile.displayName,
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 22,
                  fontWeight: FontWeight.w600,
                ),
                overflow: TextOverflow.ellipsis,
              ),
            ),

            if(profile.level != null) ... [
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10, vertical:2),
                  decoration: BoxDecoration(color: levelColor.withValues(alpha: 0.2), borderRadius: BorderRadius.circular(12)),
                  child: Text(profile.level!, style: GoogleFonts.openSans(color:levelColor, fontSize: 12, fontWeight: FontWeight.w600)),
                )
              ],
            ],
          ),
        const SizedBox(height: 6),
        if(profile.trustScore != null)
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ..._buildTrustStars(4.8, context),
            const SizedBox(width: 8),
            Text(
                '${profile.trustScore!.toStringAsFixed(1)} ★',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        const SizedBox(height: 4),
        Text(
          profile.neighbourhood,
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 12,
          ),
        ),
        const SizedBox(height: 12),
        OutlinedButton(
          onPressed: () {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('Edit Profile coming soon'),
                duration: Duration(seconds: 1),
              ),
            );
          },
          style: OutlinedButton.styleFrom(
            side: BorderSide(color: AppColors.primaryTeal(context)),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 6),
          ),
          child: Text(
            'Edit Profile',
            style: GoogleFonts.openSans(
              color: AppColors.primaryTeal(context),
              fontSize: 12,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    ),
  );
}
  Widget _buildXpCard(UserProfileResponse profile) {

    final currentXp = profile.currentXp!;
    final nextMilestone = ((currentXp ~/ 1000) + 1)  * 1000;
    final xpIntoCurrentBracket = currentXp % 1000;
    final progress = xpIntoCurrentBracket / 1000.0;
    final xpRemaining = nextMilestone - currentXp;

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.dark 
    ? AppColors.surfaceGrey(context) 
    : Colors.white,
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
                '${profile.level}',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              Text(
                '${profile.currentXp} XP',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal(context),
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
            backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.2),
            color: AppColors.primaryTeal(context),
            minHeight: 8,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          '$xpRemaining XP to next milestone',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 12,
          ),
        ),
      ],
    ),
  );
}

  Widget _buildStatsRow(UserProfileResponse profile) {
  return Row(
    children: [
      Expanded(
        child: _buildStatItem('${profile.createdTasks}', 'Tasks Created'),
      ),
      Expanded(
        child: _buildStatItem('${profile.completedTasks}', 'Tasks Completed'),
      ),
      Expanded(
        child: _buildStatItem('${profile.activeTasks}', 'Active Tasks'),
      ),
    ],
  );
}

  Widget _buildStatItem(String value, String label) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      decoration: BoxDecoration(
        color: AppColors.surfaceGrey(context),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: [
          Text(
            value,
            style: GoogleFonts.poppins(
              color: AppColors.primaryTeal(context),
              fontSize: 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 2),
          Text(
            label,
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
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
        color: Theme.of(context).brightness == Brightness.dark 
    ? AppColors.surfaceGrey(context) 
    : Colors.white,
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
                  color: AppColors.charcoal(context),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              GestureDetector(
                onTap: _showEditSkillsDialog,
                child: Text(
                  'Edit',
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal(context),
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          if(_localSkillEdits.isEmpty)
            Text('No added skills yet.', style: GoogleFonts.openSans(color: AppColors.textGrey(context), fontSize: 13))
          else
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: _localSkillEdits.map((skill) {
                return Container(
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
                  decoration: BoxDecoration(
                    color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    skill,
                    style: GoogleFonts.openSans(
                      color: AppColors.primaryTeal(context),
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

  Widget _buildAchievementsSection(UserProfileResponse profile) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.dark 
        ? AppColors.surfaceGrey(context) 
        : Colors.white,
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
                  color: AppColors.charcoal(context),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              GestureDetector(
                onTap: () {
                  Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const AchievementsScreen(),
                  ),
                );
                },
                child: Text(
                  'View All',
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal(context),
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          if(profile.achievements.isEmpty)
              Text('No achievements earned yet.', style: GoogleFonts.openSans(color: AppColors.textGrey(context), fontSize: 13))
          else
            SizedBox(
              height: 70,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: profile.achievements.length,
                itemBuilder: (context, index) {
                  final achievement = profile.achievements[index];
                  final color = BadgeVisuals.colorFor(achievement.badgeName);
                  return Container(
                    width: 60,
                    margin: const EdgeInsets.only(right: 12),
                    child: Column(
                      children: [
                        Container(
                          width: 44,
                          height: 44,
                          decoration: BoxDecoration(
                            color: color.withValues(alpha: 0.1),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Icon(
                            BadgeVisuals.iconFor(achievement.badgeName),
                            color: color,
                            size: 24,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          achievement.badgeName,
                          style: GoogleFonts.openSans(
                            color: AppColors.textGrey(context),
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

  Widget _buildTaskHistory(UserProfileResponse profile) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.dark 
    ? AppColors.surfaceGrey(context) 
    : Colors.white,
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
              color: AppColors.charcoal(context),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 10),
          if(profile.recentTasks.isEmpty)
            Text('No completed tasks yet.', style: GoogleFonts.openSans(color: AppColors.textGrey(context), fontSize: 13))
          else
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: profile.recentTasks.length,
              itemBuilder: (context, index) {
                final task = profile.recentTasks[index];
                final dates = task.endDate ?? '';
                return ListTile(
                  contentPadding: EdgeInsets.zero,
                  title: Text(
                    task.typeDescription,
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  subtitle: Text(
                    dates,
                    style: GoogleFonts.openSans(
                      color: AppColors.textGrey(context),
                      fontSize: 12,
                    ),
                  ),
                  trailing: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: AppColors.citrusYellow(context),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      '+${task.xpWorth ?? 0} XP',
                      style: GoogleFonts.openSans(
                        color: AppColors.charcoal(context),
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
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const PrivacySettingsScreen()),
            );
          },
          style: OutlinedButton.styleFrom(
            side: BorderSide(color: AppColors.textGrey(context)),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.symmetric(vertical: 14),
          ),
          child: Text(
            'Privacy Settings',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
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
            side: BorderSide(color: AppColors.error(context)),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            padding: const EdgeInsets.symmetric(vertical: 14),
          ),
          child: Text(
            'Logout',
            style: GoogleFonts.openSans(
              color: AppColors.error(context),
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
            color: AppColors.charcoal(context),
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Text(
          'Are you sure you want to logout?',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal(context),
            fontSize: 14,
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              await _performLogout();
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.error(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Logout',
              style: GoogleFonts.openSans(
                color: Theme.of(context).brightness == Brightness.dark 
              ? AppColors.surfaceGrey(context) 
              : Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _performLogout() async{
    try{
      await AuthService().logout();
      if(mounted){
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => const SplashScreen()),
          (route) => false,
        );
      }
    }catch(e){
      if(mounted){
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to sign you out. Please try again')),
        );
      }
    }
  }
}