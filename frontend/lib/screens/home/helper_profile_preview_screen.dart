import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../models/user_model.dart';
import '../../models/review_model.dart';

class HelperProfilePreviewScreen extends StatefulWidget {
  final User helper;
  final String? taskId;
  final bool showRequestButton;

  const HelperProfilePreviewScreen({
    super.key,
    required this.helper,
    this.taskId,
    this.showRequestButton = true,
  });

  @override
  State<HelperProfilePreviewScreen> createState() => _HelperProfilePreviewScreenState();
}

class _HelperProfilePreviewScreenState extends State<HelperProfilePreviewScreen> {
  bool _isInviting = false;
  bool _isInvited = false;

  // Mock data, replace with actual API call later
  List<Review> _reviews = [];
  bool _isAvailable = true;

  @override
  void initState() {
    super.initState();
    _loadHelperData();
  }

  void _loadHelperData() {
    //Replace with actual API call
    // For now, use mock data
    _reviews = [
      Review(
        id: '1',
        userId: 'user_1',
        userName: 'Sarah Johnson',
        rating: 5,
        comment: 'Amazing helper! Took great care of my plants.',
        date: DateTime.now().subtract(const Duration(days: 2)),
      ),
      Review(
        id: '2',
        userId: 'user_2',
        userName: 'Mike Brown',
        rating: 4,
        comment: 'Very reliable and punctual. Would recommend!',
        date: DateTime.now().subtract(const Duration(days: 5)),
      ),
      Review(
        id: '3',
        userId: 'user_3',
        userName: 'Lisa Wong',
        rating: 5,
        comment: 'Went above and beyond. Extremely helpful.',
        date: DateTime.now().subtract(const Duration(days: 10)),
      ),
    ];

    _isAvailable = true;
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

  List<String> _getSkills() {
    // Mock skills, replace this with actual data
    return ['Plants', 'Pets', 'Home Check-in', 'Bins'];
  }

  List<Widget> _buildTrustStars(double score) {
    final fullStars = score.floor();
    final hasHalfStar = score - fullStars >= 0.5;
    final stars = <Widget>[];

    for (int i = 0; i < fullStars; i++) {
      stars.add(const Icon(
        Icons.star,
        size: 16,
        color: Color(0xFFE9C46A),
      ));
    }

    if (hasHalfStar) {
      stars.add(const Icon(
        Icons.star_half,
        size: 16,
        color: Color(0xFFE9C46A),
      ));
    }

    final remaining = 5 - stars.length;
    for (int i = 0; i < remaining; i++) {
      stars.add(const Icon(
        Icons.star_border,
        size: 16,
        color: Color(0xFFE9C46A),
      ));
    }

    return stars;
  }

  void _inviteHelper() async {
    if (_isInvited) return;

    setState(() {
      _isInviting = true;
    });

    //Call API to invite helper
    await Future.delayed(const Duration(seconds: 1));

    if (mounted) {
      setState(() {
        _isInviting = false;
        _isInvited = true;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('${widget.helper.fullName} has been invited!'),
          backgroundColor: AppColors.success,
          duration: const Duration(seconds: 2),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final level = _getLevel(4.8); // Mock trust score
    final levelColor = _getLevelColor(level);

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
          'Helper Profile',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.more_vert, color: AppColors.charcoal),
            onPressed: () {
              // Optional menu
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

            const SizedBox(height: 24),

            // Stats Row
            _buildStatsRow(),

            const SizedBox(height: 24),

            // Skills Section
            _buildSkillsSection(),

            const SizedBox(height: 24),

            // About Section
            _buildAboutSection(),

            const SizedBox(height: 24),

            // Reviews Section
            _buildReviewsSection(),

            const SizedBox(height: 24),

            // Invite Button
            // Request Help Button only shows in Available helpers screen, not leaderboard
          if (widget.showRequestButton)
            SizedBox(
              width: double.infinity,
              child: CustomButton(
                text: _isInvited ? 'Requested ✓' : 'Request Help',
                onTap: _isInvited ? null : _inviteHelper,
                isLoading: _isInviting,
                isDisabled: _isInvited,
              ),
            ),

            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileHeader(String level, Color levelColor) {
    return Row(
      children: [
        // Profile Photo
        CircleAvatar(
          radius: 50,
          backgroundColor: AppColors.primaryTeal.withValues(alpha: 0.1),
          child: Text(
            widget.helper.firstName[0],
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.w600,
              color: AppColors.primaryTeal,
            ),
          ),
        ),
        const SizedBox(width: 16),

        // Name and Details
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    widget.helper.fullName,
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal,
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
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
              Row(
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
              Text(
                'Member since May 2026',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey,
                  fontSize: 12,
                ),
              ),
              const SizedBox(height: 6),
              // Availability indicator
              Row(
                children: [
                  Container(
                    width: 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: _isAvailable ? AppColors.success : AppColors.error,
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: 6),
                  Text(
                    _isAvailable ? 'Available for tasks' : 'Currently unavailable',
                    style: GoogleFonts.openSans(
                      color: _isAvailable ? AppColors.success : AppColors.error,
                      fontSize: 12,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildStatsRow() {
    return Row(
      children: [
        Expanded(
          child: _buildStatItem('47', 'Tasks Completed'),
        ),
        Expanded(
          child: _buildStatItem('32', 'Neighbours Helped'),
        ),
        Expanded(
          child: _buildStatItem('98%', 'Response Rate'),
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
    final skills = _getSkills();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Skills & Services',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 10),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: skills.map((skill) {
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
    );
  }

  Widget _buildAboutSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'About',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal,
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          'Neighbour for 5 years. Love helping out with plants and pets!',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal,
            fontSize: 14,
            height: 1.5,
          ),
        ),
      ],
    );
  }

  Widget _buildReviewsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Recent Reviews',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal,
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            GestureDetector(
              onTap: () {
                //avigate to all reviews
              },
              child: Text(
                'See All',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal,
                  fontSize: 13,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        if (_reviews.isEmpty)
          Text(
            'No reviews yet',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 14,
            ),
          )
        else
          Column(
            children: _reviews.take(2).map((review) {
              return _buildReviewItem(review);
            }).toList(),
          ),
      ],
    );
  }

  Widget _buildReviewItem(Review review) {
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.surfaceGrey,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              ..._buildTrustStars(review.rating),
              const SizedBox(width: 8),
              Text(
                review.userName,
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal,
                  fontSize: 13,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            review.comment,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal,
              fontSize: 13,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            _formatDate(review.date),
            style: GoogleFonts.openSans(
              color: AppColors.textGrey,
              fontSize: 11,
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final difference = now.difference(date);

    if (difference.inDays == 0) {
      return 'Today';
    } else if (difference.inDays == 1) {
      return 'Yesterday';
    } else if (difference.inDays < 7) {
      return '${difference.inDays} days ago';
    } else if (difference.inDays < 30) {
      final weeks = (difference.inDays / 7).floor();
      return '$weeks week${weeks > 1 ? 's' : ''} ago';
    } else {
      return '${date.day}/${date.month}/${date.year}';
    }
  }
}