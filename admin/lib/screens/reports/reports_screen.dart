// admin/lib/screens/reports/reports_screen.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import '../../models/report_model.dart';

class ReportsScreen extends StatefulWidget {
  const ReportsScreen({super.key});

  @override
  State<ReportsScreen> createState() => _ReportsScreenState();
}

class _ReportsScreenState extends State<ReportsScreen> {
  String _selectedStatusFilter = 'All';
  String _selectedSeverityFilter = 'All';
  String _searchQuery = '';
  List<Report> _reports = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadReports();
  }

  Future<void> _loadReports() async {
    setState(() => _isLoading = true);
    await Future.delayed(const Duration(milliseconds: 500));
    setState(() {
      _reports = _getMockReports();
      _isLoading = false;
    });
  }

  List<Report> _getFilteredReports() {
    var filtered = _reports;

    if (_selectedStatusFilter != 'All') {
      filtered = filtered.where((r) => r.statusDisplay == _selectedStatusFilter).toList();
    }

    if (_selectedSeverityFilter != 'All') {
      filtered = filtered.where((r) => r.severityDisplay == _selectedSeverityFilter).toList();
    }

    if (_searchQuery.isNotEmpty) {
      filtered = filtered.where((r) =>
        r.id.toString().contains(_searchQuery) ||
        r.reporterName.toLowerCase().contains(_searchQuery.toLowerCase())
      ).toList();
    }

    return filtered;
  }

  @override
  Widget build(BuildContext context) {
    final filteredReports = _getFilteredReports();
    final pendingCount = _reports.where((r) => r.status == ReportStatus.submitted || r.status == ReportStatus.assigned).length;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Stats chips
        Row(
          children: [
            _buildStatChip('Total', _reports.length, AppColors.primaryTeal),
            const SizedBox(width: 12),
            _buildStatChip('Pending', pendingCount, AppColors.citrusYellow),
            const SizedBox(width: 12),
            _buildStatChip('Resolved', _reports.where((r) => r.status == ReportStatus.resolved).length, AppColors.success),
          ],
        ),
        const SizedBox(height: 16),

        // Search and filters
        Row(
          children: [
            Expanded(
              child: TextField(
                onChanged: (value) {
                  setState(() => _searchQuery = value);
                },
                decoration: const InputDecoration(
                  hintText: 'Search by ID or reporter...',
                  prefixIcon: Icon(Icons.search),
                  isDense: true,
                ),
              ),
            ),
            const SizedBox(width: 12),
            _buildFilterDropdown(
              value: _selectedStatusFilter,
              items: ['All', 'Submitted', 'In Review', 'Reviewed', 'Resolved'],
              onChanged: (value) => setState(() => _selectedStatusFilter = value!),
            ),
            const SizedBox(width: 12),
            _buildFilterDropdown(
              value: _selectedSeverityFilter,
              items: ['All', 'Minor', 'Moderate', 'Severe'],
              onChanged: (value) => setState(() => _selectedSeverityFilter = value!),
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Report list
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : filteredReports.isEmpty
                  ? _buildEmptyState()
                  : ListView.builder(
                      itemCount: filteredReports.length,
                      itemBuilder: (context, index) {
                        return _buildReportCard(context, filteredReports[index]);
                      },
                    ),
        ),
      ],
    );
  }

  Widget _buildStatChip(String label, int count, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            count.toString(),
            style: GoogleFonts.poppins(
              fontSize: 14,
              fontWeight: FontWeight.w600,
              color: color,
            ),
          ),
          const SizedBox(width: 6),
          Text(
            label,
            style: GoogleFonts.openSans(
              fontSize: 12,
              color: AppColors.textGrey,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterDropdown({
    required String value,
    required List<String> items,
    required ValueChanged<String?> onChanged,
  }) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      decoration: BoxDecoration(
        border: Border.all(color: AppColors.textGrey.withOpacity(0.3)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButton<String>(
        value: value,
        items: items.map((item) {
          return DropdownMenuItem(
            value: item,
            child: Text(item, style: GoogleFonts.openSans(fontSize: 13)),
          );
        }).toList(),
        onChanged: onChanged,
        underline: const SizedBox(),
        icon: const Icon(Icons.arrow_drop_down, size: 20),
      ),
    );
  }

  Widget _buildReportCard(BuildContext context, Report report) {
    return GestureDetector(
      onTap: () {
        context.go('/reports/${report.id}');
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.04),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
          border: report.isUrgent
              ? Border.all(color: AppColors.error, width: 2)
              : null,
        ),
        child: Row(
          children: [
            if (report.isUrgent)
              Container(
                width: 4,
                height: 50,
                decoration: BoxDecoration(
                  color: AppColors.error,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            if (report.isUrgent) const SizedBox(width: 12),

            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        '#${report.id}',
                        style: GoogleFonts.poppins(
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                          color: AppColors.charcoal,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: report.statusColor.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          report.statusDisplay,
                          style: TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                            color: report.statusColor,
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: report.severityColor.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          report.severityDisplay,
                          style: TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                            color: report.severityColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    report.violationTypeDisplay,
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      color: AppColors.textGrey,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      Icon(Icons.person_outline, size: 12, color: AppColors.textGrey),
                      const SizedBox(width: 4),
                      Text(
                        report.reporterName,
                        style: GoogleFonts.openSans(
                          fontSize: 11,
                          color: AppColors.textGrey,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Icon(Icons.access_time, size: 12, color: AppColors.textGrey),
                      const SizedBox(width: 4),
                      Text(
                        _formatTime(report.createdAt),
                        style: GoogleFonts.openSans(
                          fontSize: 11,
                          color: AppColors.textGrey,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            Row(
              children: [
                TextButton(
                  onPressed: () {
                    context.go('/reports/${report.id}');
                  },
                  style: TextButton.styleFrom(
                    foregroundColor: AppColors.primaryTeal,
                  ),
                  child: const Text('View'),
                ),
                if (report.status != ReportStatus.resolved)
                  ElevatedButton(
                    onPressed: () {
                      context.go('/reports/${report.id}');
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryTeal,
                      minimumSize: const Size(70, 32),
                    ),
                    child: const Text('Review', style: TextStyle(fontSize: 12)),
                  ),
              ],
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
            Icons.flag_outlined,
            size: 64,
            color: AppColors.textGrey.withOpacity(0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'No reports found',
            style: GoogleFonts.poppins(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppColors.charcoal,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Try adjusting your filters or search terms',
            style: GoogleFonts.openSans(
              fontSize: 14,
              color: AppColors.textGrey,
            ),
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime date) {
    final now = DateTime.now();
    final diff = now.difference(date);

    if (diff.inDays > 0) {
      return '${diff.inDays}d ago';
    } else if (diff.inHours > 0) {
      return '${diff.inHours}h ago';
    } else if (diff.inMinutes > 0) {
      return '${diff.inMinutes}m ago';
    } else {
      return 'Just now';
    }
  }

  List<Report> _getMockReports() {
    return [
      Report(
        id: 102,
        reportType: ReportType.user,
        reporterUserId: 1,
        reporterName: 'John Doe',
        status: ReportStatus.submitted,
        reportedUserId: 2,
        reportedUserName: 'mike_helps',
        reason: 'User posted abusive content targeting multiple community members.',
        violationType: ViolationType.harassment,
        severity: Severity.moderate,
        suggestedAction: SuggestedAction.suspend7d,
        createdAt: DateTime.now().subtract(const Duration(minutes: 2)),
      ),
      Report(
        id: 101,
        reportType: ReportType.post,
        reporterUserId: 3,
        reporterName: 'Sarah J.',
        status: ReportStatus.submitted,
        reportedPostId: 42,
        postContent: 'Suspicious advertisement for cheap services...',
        reason: 'Suspected spam/scam post.',
        violationType: ViolationType.spamScam,
        severity: Severity.minor,
        suggestedAction: SuggestedAction.warning,
        createdAt: DateTime.now().subtract(const Duration(hours: 1)),
      ),
      Report(
        id: 100,
        reportType: ReportType.comment,
        reporterUserId: 4,
        reporterName: 'Emily R.',
        status: ReportStatus.assigned,
        reportedCommentId: 31,
        commentContent: 'I have your address saved...',
        reason: 'Sharing personal information without consent.',
        violationType: ViolationType.privacyViolation,
        severity: Severity.severe,
        suggestedAction: SuggestedAction.suspend30d,
        createdAt: DateTime.now().subtract(const Duration(hours: 3)),
      ),
      Report(
        id: 99,
        reportType: ReportType.taskDispute,
        reporterUserId: 5,
        reporterName: 'Helper User',
        status: ReportStatus.assigned,
        taskId: 128,
        taskTitle: 'Water Plants',
        disputeReason: 'No-show',
        reason: 'Helper accepted the task but never arrived.',
        violationType: ViolationType.taskNoShow,
        severity: Severity.moderate,
        suggestedAction: SuggestedAction.suspend7d,
        createdAt: DateTime.now().subtract(const Duration(days: 1)),
      ),
      Report(
        id: 98,
        reportType: ReportType.user,
        reporterUserId: 6,
        reporterName: 'Anonymous',
        status: ReportStatus.resolved,
        reportedUserId: 7,
        reportedUserName: 'user_456',
        reason: 'User posted hate speech in multiple threads.',
        violationType: ViolationType.hateSpeech,
        severity: Severity.severe,
        suggestedAction: SuggestedAction.ban,
        actualAction: SuggestedAction.ban,
        createdAt: DateTime.now().subtract(const Duration(days: 2)),
        resolvedAt: DateTime.now().subtract(const Duration(hours: 4)),
      ),
    ];
  }
}