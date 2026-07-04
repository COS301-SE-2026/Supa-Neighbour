import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../../models/task_model.dart';
import '../../models/user_model.dart';
import 'helper_profile_preview_screen.dart';

class AvailableHelpersScreen extends StatefulWidget {
  final Task task;

  const AvailableHelpersScreen({
    super.key,
    required this.task,
  });

  @override
  State<AvailableHelpersScreen> createState() => _AvailableHelpersScreenState();
}

class _AvailableHelpersScreenState extends State<AvailableHelpersScreen> {
  List<User> _helpers = [];
  bool _isLoading = true;
  String? _selectedHelperId;
  bool _showVerifiedOnly = false;

  @override
  void initState() {
    super.initState();
    _loadHelpers();
  }

  Future<void> _loadHelpers() async {
    setState(() {
      _isLoading = true;
    });

    //Will need to replace this with actual API call later
    await Future.delayed(const Duration(seconds: 1));

    // Mock data for now
    final mockHelpers = [
      User(
        id: 'helper_1',
        email: 'sarah@example.com',
        firstName: 'Sarah',
        lastName: 'Johnson',
        phone: '0821234567',
        username: 'sarah_helps',
        createdAt: DateTime.now(),
        street: '123 Main St',
        town: 'Pretoria',
        zipCode: '0001',
      ),
      User(
        id: 'helper_2',
        email: 'mike@example.com',
        firstName: 'Mike',
        lastName: 'Johnson',
        phone: '0827654321',
        username: 'mike_helps',
        createdAt: DateTime.now(),
        street: '45 Oak Ave',
        town: 'Pretoria',
        zipCode: '0002',
      ),
      User(
        id: 'helper_3',
        email: 'lisa@example.com',
        firstName: 'Lisa',
        lastName: 'Wong',
        phone: '0834567890',
        username: 'lisa_helps',
        createdAt: DateTime.now(),
        street: '78 Pine Rd',
        town: 'Pretoria',
        zipCode: '0003',
      ),
    ];

    setState(() {
      _helpers = mockHelpers;
      _isLoading = false;
    });
  }

  String _getLevel(int index) {
    //mock level assignment based on index
    if (index == 0) return 'Gold';
    if (index == 1) return 'Silver';
    return 'Bronze';
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

  double _getTrustScore(int index) {
    // Mock trust scores
    if (index == 0) return 4.8;
    if (index == 1) return 4.5;
    return 4.2;
  }

  List<String> _getSkills(int index) {
    // Mock skills
    if (index == 0) return ['Plants', 'Pets', 'Bins'];
    if (index == 1) return ['Plants', 'Home Check-in'];
    return ['Pets', 'Packages'];
  }

  String _getDistance(int index) {
    // Mock distances
    if (index == 0) return '50m';
    if (index == 1) return '120m';
    return '80m';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        backgroundColor: AppColors.background,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: AppColors.charcoal),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Available Helpers',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list, color: AppColors.primaryTeal),
            onPressed: () {
              _showFilterOptions();
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildTaskContextCard(),

            const SizedBox(height: 16),
            _buildFilterSortRow(),

            const SizedBox(height: 12),
            Expanded(
              child: _isLoading
                  ? const Center(
                      child: CircularProgressIndicator(
                        color: AppColors.primaryTeal,
                      ),
                    )
                  : _helpers.isEmpty
                      ? _buildEmptyState()
                      : ListView.builder(
                          padding: EdgeInsets.zero,
                          itemCount: _helpers.length,
                          itemBuilder: (context, index) {
                            final helper = _helpers[index];
                            return Padding(
                              padding: const EdgeInsets.only(bottom: 12),
                              child: _buildHelperCard(helper, index),
                            );
                          },
                        ),
            ),
          ],
        ),
      ),
     bottomNavigationBar: BottomNavBar(
        currentIndex: 1, 
        onTap: (index) {
          if (index != 1) {
            ScaffoldMessenger.of(context).showSnackBar(
               SnackBar(
                content: Text('Navigate to tab $index'),
                duration: Duration(seconds: 1),
              ),
            );
          }
        },
      ),
    );
  }

  Widget _buildTaskContextCard() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.primaryTeal.withValues(alpha: 0.05),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppColors.primaryTeal.withValues(alpha: 0.1),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Task: ${widget.task.title}',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal,
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  widget.task.category,
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal,
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Text(
                '${widget.task.date.day}/${widget.task.date.month} · ${widget.task.time.format(context)}',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey,
                  fontSize: 12,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            '${_helpers.length} helpers accepted this task',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterSortRow() {
    return Row(
      children: [
        Expanded(
          child: GestureDetector(
            onTap: () {
              _showFilterOptions();
            },
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  const Icon(
                    Icons.filter_list,
                    size: 16,
                    color: AppColors.primaryTeal,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Filter: All',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: GestureDetector(
            onTap: () {
              _showSortOptions();
            },
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  const Icon(
                    Icons.sort,
                    size: 16,
                    color: AppColors.primaryTeal,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Sort: Trust',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
        const SizedBox(width: 8),
        GestureDetector(
          onTap: () {
            setState(() {
              _showVerifiedOnly = !_showVerifiedOnly;
            });
          },
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            decoration: BoxDecoration(
              color: _showVerifiedOnly
                  ? AppColors.primaryTeal.withValues(alpha: 0.1)
                  : AppColors.surfaceGrey,
              borderRadius: BorderRadius.circular(8),
              border: _showVerifiedOnly
                  ? Border.all(color: AppColors.primaryTeal, width: 1)
                  : null,
            ),
            child: Row(
              children: [
                Icon(
                  _showVerifiedOnly
                      ? Icons.verified
                      : Icons.verified_outlined,
                  size: 16,
                  color: _showVerifiedOnly
                      ? AppColors.primaryTeal
                      : AppColors.textGrey,
                ),
                const SizedBox(width: 4),
                Text(
                  'Verified',
                  style: GoogleFonts.openSans(
                    color: _showVerifiedOnly
                        ? AppColors.primaryTeal
                        : AppColors.textGrey,
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildHelperCard(User helper, int index) {
  final String level = _getLevel(index);
  final double trustScore = _getTrustScore(index);
  final List<String> skills = _getSkills(index);
  final String distance = _getDistance(index);
  final bool isInvited = _selectedHelperId == helper.id;

  return GestureDetector(
    onTap: () {
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => HelperProfilePreviewScreen(
            helper: helper,
            taskId: widget.task.id,
          ),
        ),
      );
    },
    child: Container(
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
        border: isInvited
            ? Border.all(color: AppColors.success, width: 2)
            : null,
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 28,
            backgroundColor: AppColors.primaryTeal.withValues(alpha: 0.1),
            child: Text(
              helper.firstName[0],
              style: TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.w600,
                color: AppColors.primaryTeal,
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
                      helper.fullName,
                      style: GoogleFonts.poppins(
                        color: AppColors.charcoal,
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(width: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 2,
                      ),
                      decoration: BoxDecoration(
                        color: _getLevelColor(level).withValues(alpha: 0.2),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        level,
                        style: GoogleFonts.openSans(
                          color: _getLevelColor(level),
                          fontSize: 10,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    ..._buildTrustStars(trustScore),
                    const SizedBox(width: 4),
                    Text(
                      trustScore.toString(),
                      style: GoogleFonts.openSans(
                        color: AppColors.charcoal,
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const Spacer(),
                    Text(
                      distance,
                      style: GoogleFonts.openSans(
                        color: AppColors.textGrey,
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 6),
                Wrap(
                  spacing: 6,
                  runSpacing: 4,
                  children: skills.map((skill) {
                    return Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 8,
                        vertical: 2,
                      ),
                      decoration: BoxDecoration(
                        color: AppColors.primaryTeal.withValues(alpha: 0.1),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        skill,
                        style: GoogleFonts.openSans(
                          color: AppColors.primaryTeal,
                          fontSize: 10,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    );
                  }).toList(),
                ),
                const SizedBox(height: 8),
                if (isInvited)
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 8,
                      vertical: 4,
                    ),
                    decoration: BoxDecoration(
                      color: AppColors.success.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        const Icon(
                          Icons.check_circle,
                          size: 14,
                          color: AppColors.success,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          'Invited',
                          style: GoogleFonts.openSans(
                            color: AppColors.success,
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
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
        size: 14,
        color: Color(0xFFE9C46A),
      ));
    }

    if (hasHalfStar) {
      stars.add(const Icon(
        Icons.star_half,
        size: 14,
        color: Color(0xFFE9C46A),
      ));
    }

    final remaining = 5 - stars.length;
    for (int i = 0; i < remaining; i++) {
      stars.add(const Icon(
        Icons.star_border,
        size: 14,
        color: Color(0xFFE9C46A),
      ));
    }

    return stars;
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.person_off,
            size: 64,
            color: AppColors.textGrey.withValues(alpha: 0.5),
          ),
          const SizedBox(height: 16),
          Text(
            'No helpers available',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal,
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'No helpers have accepted this task yet. Check back later!',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 14,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  void _showFilterOptions() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Filter Helpers',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 16),
              const Text('Filter options coming soon...'),
              const SizedBox(height: 16),
              CustomButton(
                text: 'Apply Filters',
                onTap: () => Navigator.pop(context),
              ),
            ],
          ),
        );
      },
    );
  }

  void _showSortOptions() {
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Sort Helpers',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal,
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 16),
              const Text('Sort options coming soon...'),
              const SizedBox(height: 16),
              CustomButton(
                text: 'Apply Sort',
                onTap: () => Navigator.pop(context),
              ),
            ],
          ),
        );
      },
    );
  }
}