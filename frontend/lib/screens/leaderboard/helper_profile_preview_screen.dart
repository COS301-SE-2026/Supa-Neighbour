import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../models/user_model.dart';
import '../../models/review_model.dart';
import '../../models/helper_profile_response.dart';
import '../../services/helper_profile_service.dart';

class HelperProfilePreviewScreen extends StatefulWidget {
  final User? helper;
  final int? helperId;
  final String? taskId;
  final bool showRequestButton;

  const HelperProfilePreviewScreen({
    super.key,
    this.helper,
    this.helperId,
    this.taskId,
    this.showRequestButton = true,
  });

  @override
  State<HelperProfilePreviewScreen> createState() => _HelperProfilePreviewScreenState();
}

class _HelperProfilePreviewScreenState extends State<HelperProfilePreviewScreen> {
  bool _isLoading = false;
  bool _isInviting = false;
  bool _isInvited = false;
  String? _errorMessage;
  
  HelperProfileResponse? _profileData;
  
  List<Review> _reviews = [];
  bool _isAvailable = true;
  User? _helperUser;

  final HelperProfileService _helperProfileService = HelperProfileService();

  @override
  void initState() {
    super.initState();
    if (widget.helperId != null) {
      _loadHelperDataFromId();
    } else if (widget.helper != null) {
      _loadHelperDataFromUser();
    } else {
      _isLoading = false;
    }
  }

  void _loadHelperDataFromUser() {
    _helperUser = widget.helper;
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
    _isLoading = false;
  }

  Future<void> _loadHelperDataFromId() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final data = await _helperProfileService.getHelperProfile(widget.helperId!);
      setState(() {
        _profileData = data;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = e.toString();
        _isLoading = false;
      });
    }
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
        return AppColors.primaryTeal(context);
    }
  }

  List<String> _getSkills() {
    if (_profileData != null) {
      return _profileData!.skills;
    }
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

      final name = _helperUser?.fullName ?? _profileData?.displayName ?? 'Helper';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('$name has been invited!'),
          backgroundColor: AppColors.success(context),
          duration: const Duration(seconds: 2),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        backgroundColor: AppColors.background(context),
        appBar: AppBar(
          backgroundColor: AppColors.background(context),
          elevation: 0,
          leading: IconButton(
            icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
            onPressed: () => Navigator.pop(context),
          ),
          title: Text(
            'Helper Profile',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 24,
              fontWeight: FontWeight.w600,
            ),
          ),
          centerTitle: true,
        ),
        body: Center(
          child: CircularProgressIndicator(
            color: AppColors.primaryTeal(context),
          ),
        ),
      );
    }

    if (_errorMessage != null && widget.helperId != null) {
      return Scaffold(
        backgroundColor: AppColors.background(context),
        appBar: AppBar(
          backgroundColor: AppColors.background(context),
          elevation: 0,
          leading: IconButton(
            icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
            onPressed: () => Navigator.pop(context),
          ),
          title: Text(
            'Helper Profile',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 24,
              fontWeight: FontWeight.w600,
            ),
          ),
          centerTitle: true,
        ),
        body: Center(
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
                'Failed to load profile',
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
                onPressed: _loadHelperDataFromId,
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

    final displayName = _profileData?.displayName ?? _helperUser?.fullName ?? 'Unknown';
    final trustScore = _profileData?.trustScore ?? 4.8;
    final level = _getLevel(trustScore);
    final levelColor = _getLevelColor(level);

    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Helper Profile',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.more_vert, color: AppColors.charcoal(context)),
            onPressed: () {

            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildProfileHeader(displayName, trustScore, level, levelColor),
            const SizedBox(height: 24),
            _buildStatsRow(),
            const SizedBox(height: 24),
            _buildSkillsSection(),
            const SizedBox(height: 24),
            _buildAboutSection(),
            const SizedBox(height: 24),
            _buildReviewsSection(),
            const SizedBox(height: 24),
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

  Widget _buildProfileHeader(String displayName, double trustScore, String level, Color levelColor) {
    return Row(
      children: [
        CircleAvatar(
          radius: 50,
          backgroundColor: AppColors.primaryTeal(context).withOpacity(0.1),
          child: Text(
            displayName[0],
            style: TextStyle(
              fontSize: 36,
              fontWeight: FontWeight.w600,
              color: AppColors.primaryTeal(context),
            ),
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Text(
                    displayName,
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(width: 8),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 2),
                    decoration: BoxDecoration(
                      color: levelColor.withOpacity(0.2),
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
                  ..._buildTrustStars(trustScore),
                  const SizedBox(width: 8),
                  Text(
                    '${trustScore.toStringAsFixed(1)} ★',
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
                'Member since May 2026',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 12,
                ),
              ),
              const SizedBox(height: 6),
              Row(
                children: [
                  Container(
                    width: 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: _isAvailable ? AppColors.success(context) : AppColors.error(context),
                      shape: BoxShape.circle,
                    ),
                  ),
                  const SizedBox(width: 6),
                  Text(
                    _isAvailable ? 'Available for tasks' : 'Currently unavailable',
                    style: GoogleFonts.openSans(
                      color: _isAvailable ? AppColors.success(context) : AppColors.error(context),
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
    final completedTasks = _profileData?.completedTasks ?? 47;
    final neighboursHelped = _profileData?.neighboursHelped ?? 32;

    return Row(
      children: [
        Expanded(
          child: _buildStatItem(completedTasks.toString(), 'Tasks Completed'),
        ),
        Expanded(
          child: _buildStatItem(neighboursHelped.toString(), 'Neighbours Helped'),
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
    final skills = _getSkills();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Skills & Services',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
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
                color: AppColors.primaryTeal(context).withOpacity(0.1),
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
    );
  }

  Widget _buildAboutSection() {
    const about = 'Neighbour for 5 years. Love helping out with plants and pets!';

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'About',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          about,
          style: GoogleFonts.openSans(
            color: AppColors.charcoal(context),
            fontSize: 14,
            height: 1.5,
          ),
        ),
      ],
    );
  }

  Widget _buildReviewsSection() {
    final reviews = _profileData?.reviews.isNotEmpty == true
        ? _profileData!.reviews
        : _reviews;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Recent Reviews',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            GestureDetector(
              onTap: () {},
              child: Text(
                'See All',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal(context),
                  fontSize: 13,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        if (reviews.isEmpty)
          Text(
            'No reviews yet',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          )
        else
          Column(
            children: reviews.take(2).map((review) {
              return _buildReviewItem(review);
            }).toList(),
          ),
      ],
    );
  }

  Widget _buildReviewItem(dynamic review) {
    final String reviewerName;
    final String comment;
    final double rating;

    if (review is Review) {
      reviewerName = review.userName;
      comment = review.comment;
      rating = review.rating;
    } else {
      reviewerName = review.reviewerName;
      comment = review.comment;
      rating = 5.0;
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.surfaceGrey(context),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              ..._buildTrustStars(rating),
              const SizedBox(width: 8),
              Text(
                reviewerName,
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                  fontSize: 13,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 4),
          Text(
            comment,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 13,
            ),
          ),
        ],
      ),
    );
  }
}