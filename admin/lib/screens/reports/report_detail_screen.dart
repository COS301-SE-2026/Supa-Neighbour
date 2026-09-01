// admin/lib/screens/reports/report_detail_screen.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import '../../widgets/admin_scaffold.dart';
import '../../models/report_model.dart';

class ReportDetailScreen extends StatefulWidget {
  final int reportId;

  const ReportDetailScreen({
    super.key,
    required this.reportId,
  });

  @override
  State<ReportDetailScreen> createState() => _ReportDetailScreenState();
}

class _ReportDetailScreenState extends State<ReportDetailScreen> {
  Report? _report;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadReport();
  }

  Future<void> _loadReport() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    // TODO: Replace with actual API call
    await Future.delayed(const Duration(milliseconds: 500));
    
    // Find mock report
    final mockReports = _getMockReports();
    final report = mockReports.firstWhere(
      (r) => r.id == widget.reportId,
      orElse: () => mockReports.first,
    );
    
    setState(() {
      _report = report;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return AdminScaffold(
      selectedIndex: 1,
      title: 'Report #${widget.reportId}',
      child: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? _buildErrorState()
              : _report != null
                  ? _buildReportDetail()
                  : _buildErrorState(),
    );
  }

  Widget _buildErrorState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.error_outline, size: 64, color: AppColors.error),
          const SizedBox(height: 16),
          Text(
            'Failed to load report',
            style: GoogleFonts.poppins(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppColors.charcoal,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            _error ?? 'Please try again later',
            style: GoogleFonts.openSans(
              fontSize: 14,
              color: AppColors.textGrey,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _loadReport,
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }

  Widget _buildReportDetail() {
    final report = _report!;

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Status and severity row
          Row(
            children: [
              _buildStatusBadge(report),
              const SizedBox(width: 12),
              _buildSeverityBadge(report),
              const Spacer(),
              if (report.isUrgent)
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppColors.error.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(color: AppColors.error),
                  ),
                  child: Text(
                    '⚠️ URGENT',
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                      color: AppColors.error,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 16),

          // Report details card
          Container(
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
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildInfoRow('Report ID', '#${report.id}'),
                _buildInfoRow('Type', report.violationTypeDisplay),
                _buildInfoRow('Reported By', report.reporterName),
                _buildInfoRow('Reported Against', report.reportedUserName ?? 'Unknown'),
                _buildInfoRow('Created', _formatDate(report.createdAt)),
                if (report.resolvedAt != null)
                  _buildInfoRow('Resolved', _formatDate(report.resolvedAt!)),
                const Divider(),
                _buildInfoRow('Reason', report.reason),
                if (report.description != null)
                  _buildInfoRow('Description', report.description!),
                if (report.disputeReason != null)
                  _buildInfoRow('Dispute Reason', report.disputeReason!),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Suggested action
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: AppColors.primaryTeal.withOpacity(0.05),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: AppColors.primaryTeal.withOpacity(0.3)),
            ),
            child: Row(
              children: [
                const Icon(Icons.gavel, color: AppColors.primaryTeal),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Suggested Action',
                        style: GoogleFonts.openSans(
                          fontSize: 12,
                          color: AppColors.textGrey,
                        ),
                      ),
                      Text(
                        report.suggestedActionDisplay,
                        style: GoogleFonts.poppins(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                          color: AppColors.primaryTeal,
                        ),
                      ),
                    ],
                  ),
                ),
                ElevatedButton(
                  onPressed: () {
                    // TODO: Accept suggested action
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.success,
                  ),
                  child: const Text('Accept'),
                ),
                const SizedBox(width: 8),
                OutlinedButton(
                  onPressed: () {
                    // TODO: Override action
                  },
                  child: const Text('Override'),
                ),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Action buttons
          Row(
            children: [
              Expanded(
                child: ElevatedButton(
                  onPressed: () {
                    // TODO: Approve report
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.success,
                  ),
                  child: const Text('Approve'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: OutlinedButton(
                  onPressed: () {
                    // TODO: Dismiss report
                  },
                  style: OutlinedButton.styleFrom(
                    side: BorderSide(color: AppColors.textGrey),
                  ),
                  child: Text(
                    'Dismiss',
                    style: TextStyle(color: AppColors.textGrey),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: OutlinedButton(
                  onPressed: () {
                    // TODO: Escalate report
                  },
                  style: OutlinedButton.styleFrom(
                    side: BorderSide(color: AppColors.error),
                  ),
                  child: Text(
                    'Escalate',
                    style: TextStyle(color: AppColors.error),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStatusBadge(Report report) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: report.statusColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 8,
            height: 8,
            decoration: BoxDecoration(
              color: report.statusColor,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 8),
          Text(
            report.statusDisplay,
            style: TextStyle(
              fontSize: 12,
              fontWeight: FontWeight.w600,
              color: report.statusColor,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSeverityBadge(Report report) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: report.severityColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        report.severityDisplay,
        style: TextStyle(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: report.severityColor,
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120,
            child: Text(
              label,
              style: GoogleFonts.openSans(
                fontSize: 13,
                fontWeight: FontWeight.w600,
                color: AppColors.textGrey,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: GoogleFonts.openSans(
                fontSize: 13,
                color: AppColors.charcoal,
              ),
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year} · ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }

  
  // MOCK DATA
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