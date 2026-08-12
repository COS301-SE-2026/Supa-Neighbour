import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../models/task_model.dart';
import '../leaderboard/helper_profile_preview_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class AvailableHelpersScreen extends ConsumerStatefulWidget {
  final Task task;

  const AvailableHelpersScreen({
    super.key,
    required this.task,
  });

  @override
  ConsumerState<AvailableHelpersScreen> createState() => _AvailableHelpersScreenState();
}

class _AvailableHelpersScreenState extends ConsumerState<AvailableHelpersScreen> {
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
      final res = await dio.post(
        '/api/task-invitations/$taskId/match',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      final data = res.data as Map<String, dynamic>;
      final helpers = data['helpers'] as List<dynamic>? ?? [];
      final mapped = helpers.map((h) {
        final m = h as Map<String, dynamic>;
        return {
          'helperId': m['helperId'],
          'helperName': m['helperName'],
          'neighbourhoodZone': m['neighbourhoodZone'],
          'skillMatched': m['skillMatched'],
          'helperXp': m['helperXp'],
          'status': m['invitationStatus'],
        };
      }).toList();
      if (!mounted) return;
      setState(() {
        _matchedHelpers = mapped.cast<Map<String, dynamic>>();
        _isLoading = false;
      });
    } on Exception catch (e) {
      if (mounted) {
        setState(() => _isLoading = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(e.toString().replaceAll('Exception: ', '')),
            backgroundColor: Colors.red,
          ),
        );
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

  Widget _buildMatchedHelperCard(Map<String, dynamic> inv) {
    final fullName = inv['helperName'] as String? ?? 'Helper';
    final status = inv['status'] as String? ?? '';
    final firstName = fullName.isNotEmpty ? fullName.split(' ').first : 'H';
    final isInvited = status == 'Accepted';
    final helperId = inv['helperId'] as int?;

    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => HelperProfilePreviewScreen(
              helperId: helperId,
              taskId: widget.task.id,
              showRequestButton: true,
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
      ),
    );
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