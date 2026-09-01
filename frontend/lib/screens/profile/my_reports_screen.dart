import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/report_dto.dart';
import '../../services/report_service.dart';

class MyReportsScreen extends ConsumerStatefulWidget {
  const MyReportsScreen({super.key});

  @override
  ConsumerState<MyReportsScreen> createState() => _MyReportsScreenState();
}

class _MyReportsScreenState extends ConsumerState<MyReportsScreen> {
  List<ReportDTO> _reports = [];
  bool _isLoading = true;
  String? _error;
  String? _selectedStatus;
  String? _selectedType;

  final List<String> _statusOptions = ['All', 'submitted', 'assigned', 'reviewed'];
  final List<String> _typeOptions = ['All', 'USER', 'POST', 'COMMENT', 'TASK_DISPUTE'];

  @override
  void initState() {
    super.initState();
    _fetchReports();
  }

  Future<void> _fetchReports() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final reportService = ReportService();
      final status = _selectedStatus == 'All' ? null : _selectedStatus;
      final type = _selectedType == 'All' ? null : _selectedType;

      final reports = await reportService.getMyReports(
        status: status,
        reportType: type,
      );

      if (mounted) {
        setState(() {
          _reports = reports;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString().replaceFirst('Exception: ', '');
          _isLoading = false;
        });
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
          'My Reports',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: Column(
        children: [
          // Filter Row
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              children: [
                Expanded(
                  child: _buildFilterDropdown(
                    value: _selectedStatus ?? 'All',
                    items: _statusOptions,
                    label: 'Status',
                    onChanged: (value) {
                      setState(() {
                        _selectedStatus = value;
                      });
                      _fetchReports();
                    },
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: _buildFilterDropdown(
                    value: _selectedType ?? 'All',
                    items: _typeOptions,
                    label: 'Type',
                    onChanged: (value) {
                      setState(() {
                        _selectedType = value;
                      });
                      _fetchReports();
                    },
                  ),
                ),
              ],
            ),
          ),
          Expanded(
            child: _buildBody(),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterDropdown({
    required String value,
    required List<String> items,
    required String label,
    required void Function(String?) onChanged,
  }) {
    return DropdownButtonFormField<String>(
      value: value,
      decoration: InputDecoration(
        labelText: label,
        labelStyle: GoogleFonts.openSans(
          color: AppColors.textGrey(context),
          fontSize: 12,
        ),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.surfaceGrey(context)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: AppColors.primaryTeal(context), width: 2),
        ),
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      ),
      items: items.map((item) {
        return DropdownMenuItem(
          value: item,
          child: Text(
            item,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 14,
            ),
          ),
        );
      }).toList(),
      onChanged: onChanged,
      isDense: true,
      style: GoogleFonts.openSans(
        color: AppColors.charcoal(context),
        fontSize: 14,
      ),
      dropdownColor: AppColors.background(context),
      icon: Icon(Icons.arrow_drop_down, color: AppColors.primaryTeal(context)),
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: Color(0xFF2A9D8F)),
      );
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: AppColors.error(context).withValues(alpha: 0.5),
            ),
            const SizedBox(height: 16),
            Text(
              'Failed to load reports',
              style: GoogleFonts.poppins(
                color: AppColors.error(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _error!,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _fetchReports,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryTeal(context),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
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
      );
    }

    if (_reports.isEmpty) {
      return _buildEmptyState();
    }

    return RefreshIndicator(
      onRefresh: _fetchReports,
      child: ListView.builder(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        itemCount: _reports.length,
        itemBuilder: (context, index) {
          final report = _reports[index];
          return _buildReportCard(report);
        },
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.report_outlined,
            size: 80,
            color: AppColors.textGrey(context).withValues(alpha: 0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'No Reports',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'You haven\'t submitted any reports yet.',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildReportCard(ReportDTO report) {
    final bool isResolved = report.status == 'reviewed' || report.status == 'resolved';
    final bool isAssigned = report.status == 'assigned';
    final bool isPending = report.status == 'submitted';

    Color statusColor;
    String statusLabel;

    if (isResolved) {
      statusColor = AppColors.success(context);
      statusLabel = 'Resolved';
    } else if (isAssigned) {
      statusColor = const Color(0xFF2196F3);
      statusLabel = 'Assigned';
    } else if (isPending) {
      statusColor = AppColors.citrusYellow(context);
      statusLabel = 'Pending';
    } else {
      statusColor = AppColors.textGrey(context);
      statusLabel = report.status ?? 'Unknown';
    }

    final String reportTypeLabel = _getReportTypeLabel(report.reportType);
    final String displayTitle = _getReportTitle(report);
    final String displaySubtitle = _getReportSubtitle(report);

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
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
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusColor.withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  statusLabel,
                  style: GoogleFonts.openSans(
                    color: statusColor,
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  reportTypeLabel,
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal(context),
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            displayTitle,
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            displaySubtitle,
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 13,
            ),
          ),
          if (report.reason != null && report.reason!.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(
              report.reason!,
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 13,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ],
          const SizedBox(height: 8),
          Row(
            children: [
              Icon(
                Icons.access_time,
                size: 14,
                color: AppColors.textGrey(context),
              ),
              const SizedBox(width: 4),
              Text(
                _formatDate(report.createdAt),
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 12,
                ),
              ),
              if (report.resolvedAt != null) ...[
                const SizedBox(width: 12),
                Icon(
                  Icons.check_circle_outline,
                  size: 14,
                  color: AppColors.success(context),
                ),
                const SizedBox(width: 4),
                Text(
                  'Resolved: ${_formatDate(report.resolvedAt)}',
                  style: GoogleFonts.openSans(
                    color: AppColors.success(context),
                    fontSize: 12,
                  ),
                ),
              ],
            ],
          ),
          if (report.actualAction != null && report.actualAction!.isNotEmpty) ...[
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                'Action: ${_getActionLabel(report.actualAction!)}',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 12,
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }

  String _getReportTypeLabel(String? type) {
    switch (type) {
      case 'USER':
        return 'User Report';
      case 'POST':
        return 'Post Report';
      case 'COMMENT':
        return 'Comment Report';
      case 'TASK_DISPUTE':
        return 'Task Dispute';
      default:
        return type ?? 'Report';
    }
  }

  String _getReportTitle(ReportDTO report) {
    if (report.reportType == 'TASK_DISPUTE') {
      return 'Task Dispute #${report.taskId}';
    }
    if (report.reportType == 'USER' && report.reportedUserId != null) {
      return 'User Report #${report.reportedUserId}';
    }
    if (report.reportType == 'POST' && report.reportedPostId != null) {
      return 'Post Report #${report.reportedPostId}';
    }
    if (report.reportType == 'COMMENT' && report.reportedCommentId != null) {
      return 'Comment Report #${report.reportedCommentId}';
    }
    return 'Report #${report.reportId}';
  }

  String _getReportSubtitle(ReportDTO report) {
    if (report.reportType == 'TASK_DISPUTE' && report.disputeReason != null) {
      return 'Dispute: ${_getDisputeReasonLabel(report.disputeReason!)}';
    }
    return '';
  }

  String _getDisputeReasonLabel(String reason) {
    switch (reason) {
      case 'NO_SHOW':
        return 'No Show';
      case 'INCOMPLETE':
        return 'Incomplete';
      case 'DAMAGE':
        return 'Damage';
      default:
        return reason;
    }
  }

  String _getActionLabel(String action) {
    switch (action) {
      case 'WARNING':
        return ' Warning';
      case 'SUSPEND_7D':
        return ' Suspended (7 days)';
      case 'SUSPEND_14D':
        return ' Suspended (14 days)';
      case 'BAN':
        return ' Banned';
      default:
        return action;
    }
  }

  String _formatDate(DateTime? date) {
    if (date == null) return '';
    final now = DateTime.now();
    final diff = now.difference(date);

    if (diff.inDays == 0) {
      if (diff.inHours == 0) {
        return '${diff.inMinutes}m ago';
      }
      return '${diff.inHours}h ago';
    } else if (diff.inDays == 1) {
      return 'Yesterday';
    } else if (diff.inDays < 7) {
      return '${diff.inDays} days ago';
    } else {
      return '${date.day}/${date.month}/${date.year}';
    }
  }
}