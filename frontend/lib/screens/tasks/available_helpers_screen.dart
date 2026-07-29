import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../../models/task_model.dart';



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
  bool _isLoading = true;
  bool _showVerifiedOnly = false;

  @override
  void initState() {
    super.initState();
    _loadHelpers();
  }

List<Map<String, dynamic>> _matchedHelpers = [];

Future<void> _loadHelpers() async {
  setState(() => _isLoading = true);
  try {
    final taskId = int.tryParse(widget.task.id);
    if (taskId == null) throw Exception('Invalid task ID');
    final token = await fb.FirebaseAuth.instance.currentUser?.getIdToken();
    final dio = Dio(BaseOptions(baseUrl: 'http://localhost:8080'));
    final res = await dio.get(
      '/api/task-invitations',
      options: token != null
          ? Options(headers: {'Authorization': 'Bearer $token'})
          : null,
    );
    final all = res.data as List<dynamic>;
    final filtered = all
        .where((inv) => inv['taskId']?['taskid'] == taskId || inv['taskId']?['task_id'] == taskId)
        .toList();
    if (!mounted) return;
    setState(() {
      _matchedHelpers = filtered.cast<Map<String, dynamic>>();
      _isLoading = false;
    });
  } catch (_) {
    if (!mounted) return;
    setState(() => _isLoading = false);
  }
}


  String _getLevel(int index) {
    //mock level assignment based on index
    if (index == 0) return 'Gold';
    if (index == 1) return 'Silver';
    return 'Bronze';
  }

  Color _getLevelColor(String level, BuildContext context) {
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
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Available Helpers',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.filter_list, color: AppColors.primaryTeal(context)),
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
                  ? Center(
                      child: CircularProgressIndicator(
                        color: AppColors.primaryTeal(context),
                      ),
                    )
                  : _matchedHelpers.isEmpty
                      ? _buildEmptyState()
                      : ListView.builder(
                          padding: EdgeInsets.zero,
                          itemCount: _matchedHelpers.length,
                          itemBuilder: (context, index) {
                            final inv = _matchedHelpers[index];
                            return Padding(
                              padding: const EdgeInsets.only(bottom: 12),
                              child: _buildMatchedHelperCard(inv),
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
        color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Task: ${widget.task.title}',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
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
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  widget.task.category,
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal(context),
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Text(
                '${widget.task.date.day}/${widget.task.date.month} · ${widget.task.time.format(context)}',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 12,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            '${_matchedHelpers.length} helpers matched to this task',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
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
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.filter_list,
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Filter: All',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
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
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.sort,
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Sort: Trust',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
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
                  ? AppColors.primaryTeal(context).withValues(alpha: 0.1)
                  : AppColors.surfaceGrey(context),
              borderRadius: BorderRadius.circular(8),
              border: _showVerifiedOnly
                  ? Border.all(color: AppColors.primaryTeal(context), width: 1)
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
                      ? AppColors.primaryTeal(context)
                      : AppColors.textGrey(context),
                ),
                const SizedBox(width: 4),
                Text(
                  'Verified',
                  style: GoogleFonts.openSans(
                    color: _showVerifiedOnly
                        ? AppColors.primaryTeal(context)
                        : AppColors.textGrey(context),
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


  Widget _buildMatchedHelperCard(Map<String, dynamic> invitation) {
  final helper = invitation['helperId'] as Map<String, dynamic>?;
  final user = helper?['userid'] as Map<String, dynamic>?;
  final firstName = user?['firstName'] as String? ?? 'Helper';
  final lastName = user?['lastName'] as String? ?? '';
  final fullName = '$firstName $lastName'.trim();
  final status = invitation['status'] as String? ?? 'Invited';
  final isInvited = status == 'Accepted';

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
      border: isInvited ? Border.all(color: AppColors.success(context), width: 2) : null,
    ),
    child: Row(
      children: [
        CircleAvatar(
          radius: 28,
          backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
          child: Text(
            firstName.isNotEmpty ? firstName[0] : '?',
            style: TextStyle(
              fontSize: 24,
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
              Text(
                fullName,
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 4),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: (status == 'Accepted'
                          ? AppColors.success(context)
                          : status == 'Declined'
                              ? Colors.red
                              : AppColors.primaryTeal(context))
                      .withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  status,
                  style: GoogleFonts.openSans(
                    color: status == 'Accepted'
                        ? AppColors.success(context)
                        : status == 'Declined'
                            ? Colors.red
                            : AppColors.primaryTeal(context),
                    fontSize: 10,
                    fontWeight: FontWeight.w600,
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
            color: AppColors.textGrey(context).withValues(alpha: 0.5),
          ),
          const SizedBox(height: 16),
          Text(
            'No helpers available',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'No helpers have accepted this task yet. Check back later!',
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
                  color: AppColors.charcoal(context),
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
                  color: AppColors.charcoal(context),
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