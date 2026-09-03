// admin/lib/screens/reports/report_detail_screen.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import '../../models/report_model.dart';
import '../../services/report_service.dart';

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
  final ReportService _reportService = ReportService();

  Report? _report;
  bool _isLoading = true;
  String? _error;

  ViolationType? _selectedViolationType;
  Severity? _selectedSeverity;
  SuggestedAction? _suggestedAction;
  bool _isLoadingSuggestion = false;
  // Set when the backend has no rule for the selected pair (not an error —
  // e.g. THREATS_VIOLENCE + MINOR) or when the call genuinely fails.
  // _suggestionIsError distinguishes which one it is so the UI can style
  // and word them differently.
  String? _suggestionMessage;
  bool _suggestionIsError = false;
  

  bool _isSubmittingVerdict = false;
  bool _actionSubmitted = false;

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

    try {
      final report = await _reportService.getReportById(widget.reportId);

      if (report == null) {
        setState(() {
          _error = 'Report not found.';
          _isLoading = false;
        });
        return;
      }

      setState(() {
        _report = report;
        _isLoading = false;
        _selectedViolationType = report.violationType == ViolationType.unassessed
            ? null
            : report.violationType;
        _selectedSeverity = report.severity == Severity.unassessed
            ? null
            : report.severity;
        _suggestedAction = report.suggestedAction == SuggestedAction.pending
            ? null
            : report.suggestedAction;
        _actionSubmitted = report.status == ReportStatus.reviewed || report.status == ReportStatus.resolved;
      });
    } on ReportServiceException catch (e) {
      setState(() {
        _error = e.message;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Failed to load report';
        _isLoading = false;
      });
    }
  }

  Future<void> _getSuggestedAction() async {
    final violationType = _selectedViolationType;
    final severity = _selectedSeverity;

    if (violationType == null ||
        severity == null ||
        violationType == ViolationType.unassessed ||
        severity == Severity.unassessed) {
      setState(() {
        _suggestedAction = null;
        _suggestionMessage = null;
        _suggestionIsError = false;
      });
      return;
    }

    setState(() {
      _isLoadingSuggestion = true;
      _suggestionMessage = null;
      _suggestionIsError = false;
    });

    try {
      final action = await _reportService.getSuggestedAction(
        violationType: violationType,
        severity: severity,
      );

      setState(() {
        _suggestedAction = action;
        _isLoadingSuggestion = false;
        if (action == null) {
          _suggestionMessage = 'No suggested action defined for this combination.';
          _suggestionIsError = false;
        }
      });
    } on ReportServiceException catch (e) {
      setState(() {
        _suggestedAction = null;
        _isLoadingSuggestion = false;
        _suggestionMessage = e.message;
        _suggestionIsError = true;
      });
    } catch (e) {
      setState(() {
        _suggestedAction = null;
        _isLoadingSuggestion = false;
        _suggestionMessage = 'Failed to get suggested action.';
        _suggestionIsError = true;
      });
    }
  }
  void _showSnack(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(message)));
  }


  Future<void> _patchReport({
    String? status,
    ViolationType? violationType, 
    Severity? severity,
    String? actualAction,
    String? adminNotes,
    required String successMessage,
  }) async {
    setState (() => _isSubmittingVerdict = true);

    try{
      final updated = await _reportService.patchReport(
        reportId:  widget.reportId,
        status: status,
        violationType: violationType,
        severity: severity,
        actualAction: actualAction,
        adminNotes: adminNotes,
      );

      if(!mounted) return;
      setState(() {
        _report = updated;
        _isSubmittingVerdict = false;
        _actionSubmitted = true;
      });
      _showSnack(successMessage);
    } on ReportServiceException catch(e){
      if(!mounted) return;
      setState(() => _isSubmittingVerdict = false);
      _showSnack(e.message);
    }catch (e){
      if(!mounted) return;
      setState(() => _isSubmittingVerdict = false);
      _showSnack('Failed to update report');
    }
  }

  Future<void> _approve() async{
    if(_selectedViolationType == null || _selectedSeverity == null){
      _showSnack('Select a violation type and severity first.');
      return;
    }

    if(_suggestedAction == null || _suggestedAction == SuggestedAction.pending){
      _showSnack('No suggested action avilable yet.');
      return;
    }
    await _patchReport(
      status: 'reviewed',
      violationType: _selectedViolationType,
      severity: _selectedSeverity,
      actualAction: _suggestedAction!.apiValue,
      successMessage: 'Report approved and action applied.',
    );
  }

  Future<void> _dismiss() async{
    await _patchReport(
      status: 'reviewed',
      violationType: _selectedViolationType,
      severity: _selectedSeverity,
      adminNotes: 'Dismissed - no violation found',
      successMessage: 'Report dismissed.',
    );
  }

  Future<void> _escalate() async{
    await _patchReport(
      severity: Severity.severe,
      status: 'reviewed',
      adminNotes: 'Escalated for senior review.',
      successMessage: 'Report escalated'
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_error != null || _report == null) {
      return _buildErrorState();
    }
    return _buildReportDetail();
  }

  Widget _buildErrorState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: AppColors.error),
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
          //BACK BUTTON
          Row(
            children: [
              IconButton(
                onPressed: () => context.go('/reports'),
                icon: const Icon(Icons.arrow_back),
                tooltip: 'Back to reports',
              ),
              const SizedBox(width: 8),
              Text(
                'Report #${report.reportId}',
                style: GoogleFonts.poppins(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: AppColors.charcoal,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),

          // Status badges
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
                    color: AppColors.error.withValues(alpha: 0.1),
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
                  color: Colors.black.withValues(alpha: 0.04),
                  blurRadius: 8,
                  offset: const Offset(0, 2),
                ),
              ],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildInfoRow('Report ID', '#${report.reportId}'),
                _buildInfoRow('Type', report.reportType.display),
                _buildInfoRow('Reporter', report.reporterName),
                _buildInfoRow('Reported', _buildReportedTargetLabel(report)),
                _buildInfoRow('Created', _formatDate(report.createdAt)),
                const Divider(),
                _buildInfoRow('Reason', report.reason),
                if (report.disputeReason != null)
                  _buildInfoRow('Dispute Reason', report.disputeReason!),
              ],
            ),
          ),
          const SizedBox(height: 16),

          //Violation Type & Severity Selection with Suggested Action
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(12),
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
                  'Review & Action',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                ),
                const SizedBox(height: 16),

                // Violation Type Dropdown
                DropdownButtonFormField<ViolationType>(
                  initialValue: _selectedViolationType,
                  hint: const Text('Select violation type'),
                  isExpanded: true,
                  items: ViolationType.values
                      .where((t) => t != ViolationType.unassessed)
                      .map((type) {
                    return DropdownMenuItem(
                      value: type,
                      child: Text(type.display),
                    );
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedViolationType = value;
                    });
                    _getSuggestedAction();
                  },
                  decoration: const InputDecoration(
                    labelText: 'Violation Type',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 12),

                // Severity Dropdown
                DropdownButtonFormField<Severity>(
                  initialValue: _selectedSeverity,
                  hint: const Text('Select severity'),
                  isExpanded: true,
                  items: Severity.values
                      .where((s) => s != Severity.unassessed)
                      .map((severity) {
                    return DropdownMenuItem(
                      value: severity,
                      child: Text(severity.display),
                    );
                  }).toList(),
                  onChanged: (value) {
                    setState(() {
                      _selectedSeverity = value;
                    });
                    _getSuggestedAction();
                  },
                  decoration: const InputDecoration(
                    labelText: 'Severity',
                    border: OutlineInputBorder(),
                  ),
                ),

                // Suggested Action Result
                if (_isLoadingSuggestion)
                  const Padding(
                    padding: EdgeInsets.only(top: 12),
                    child: Center(child: CircularProgressIndicator()),
                  )
                else if (_suggestionMessage != null)
                  Container(
                    margin: const EdgeInsets.only(top: 12),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: (_suggestionIsError ? AppColors.error : AppColors.textGrey)
                          .withValues(alpha: 0.08),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          _suggestionIsError ? Icons.error_outline : Icons.info_outline,
                          color: _suggestionIsError ? AppColors.error : AppColors.textGrey,
                          size: 20,
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            _suggestionMessage!,
                            style: GoogleFonts.openSans(
                              fontSize: 13,
                              color: _suggestionIsError ? AppColors.error : AppColors.textGrey,
                            ),
                          ),
                        ),
                        if (_suggestionIsError)
                          TextButton(
                            onPressed: _getSuggestedAction,
                            child: const Text('Retry'),
                          ),
                      ],
                    ),
                  )
                else if (_suggestedAction != null &&
                    _suggestedAction != SuggestedAction.pending)
                  Container(
                    margin: const EdgeInsets.only(top: 12),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: AppColors.primaryTeal.withValues(alpha: 0.05),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: AppColors.primaryTeal.withValues(alpha: 0.3)),
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
                                _suggestedAction!.display,
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
                            onPressed:  (_isSubmittingVerdict || _actionSubmitted) ? null : _approve,
                            style: ElevatedButton.styleFrom(
                              backgroundColor: AppColors.success,
                            ),
                            child: _isSubmittingVerdict ? const SizedBox(
                              width: 16,
                              height: 16,
                              child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white
                              ),
                            ) : const Text('Apply'),
                        ),
                      ],
                    ),
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
                  onPressed: (_isSubmittingVerdict || _actionSubmitted) ? null : _approve,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.success,
                  ),
                  child: const Text('Approve'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: OutlinedButton(
                  onPressed: (_isSubmittingVerdict  || _actionSubmitted)? null : _dismiss,
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppColors.textGrey),
                  ),
                  child: const Text(
                    'Dismiss',
                    style: TextStyle(color: AppColors.textGrey),
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: OutlinedButton(
                  onPressed:( _isSubmittingVerdict  || _actionSubmitted)? null : _escalate,
                  style: OutlinedButton.styleFrom(
                    side: const BorderSide(color: AppColors.error),
                  ),
                  child: const Text(
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
        color: report.statusColor.withValues(alpha: 0.1),
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
            report.status.display,
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
        color: report.severityColor.withValues(alpha: 0.1),
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

  /// The DTO only gives us raw target IDs (reportedUserId /
  /// reportedPostId / reportedCommentId / taskId) — no names/content —
  /// so build a readable label from whichever one is set.
  String _buildReportedTargetLabel(Report report) {
    if (report.reportedUserId != null) {
      return 'User #${report.reportedUserId}';
    }
    if (report.reportedPostId != null) {
      return 'Post #${report.reportedPostId}';
    }
    if (report.reportedCommentId != null) {
      return 'Comment #${report.reportedCommentId}';
    }
    if (report.taskId != null) {
      return 'Task #${report.taskId}';
    }
    return 'Unknown';
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year} · ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}