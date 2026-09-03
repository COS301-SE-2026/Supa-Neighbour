// admin/lib/screens/reports/reports_screen.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import '../../models/report_model.dart';
import '../../services/report_service.dart';

class ReportsScreen extends StatefulWidget {
  const ReportsScreen({super.key});

  @override
  State<ReportsScreen> createState() => _ReportsScreenState();
}

class _ReportsScreenState extends State<ReportsScreen> {
  final ReportService _reportService = ReportService();
  ReportStatus? _selectedStatusFilter;
  ReportType? _selectedTypeFilter;
  String _searchQuery = '';
  List<Report> _reports = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadReports();
  }

  Future<void> _loadReports() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });
    await Future.delayed(const Duration(milliseconds: 500));

    try{
      final reports = await _reportService.getAssignedReports(
        status: _selectedStatusFilter,
        reportType: _selectedTypeFilter,
      );

      setState(() {
        _reports = reports;
        _isLoading = false;
      });
    }on ReportServiceException catch(e){
      setState(() {
        _errorMessage = e.message;
        _isLoading = false;
      });
    } catch (e){
      setState(() {
        _errorMessage = 'Failed to load reports';
        _isLoading = false;
      });
    }
  }

  List<Report> _getFilteredReports() {
    if (_searchQuery.isEmpty)  return _reports;
    return _reports.where((r) => r.reportId.toString().contains(_searchQuery)).toList();
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
            _buildTypeDropdown(),
            const SizedBox(width: 12),
            _buildTypeDropdown(),
          ],
        ),
        const SizedBox(height: 16),
        if(_errorMessage != null) _buildErrorBanner(),

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

  Widget _buildErrorBanner() {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.error.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Expanded(
            child: Text(
              _errorMessage!,
              style: const TextStyle(color: AppColors.error, fontSize: 13),
            ),
          ),
          TextButton(onPressed: _loadReports, child: const Text('Retry')),
        ],
      ),
    );
  }

  Widget _buildStatChip(String label, int count, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: color.withValues(alpha: 0.3)),
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

  /*Widget _buildStatusDropdown() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      decoration: BoxDecoration(
        border: Border.all(color: AppColors.textGrey.withValues(alpha: 0.3)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButton<ReportStatus?>(
        value: _selectedStatusFilter,
        hint: Text('All statuses', style: GoogleFonts.openSans(fontSize: 13)),
        items: [
          DropdownMenuItem<ReportStatus?>(
            value: null,
            child: Text('All', style: GoogleFonts.openSans(fontSize: 13)),
          ),
          ...ReportStatus.values.where((s) => s != ReportStatus.unknown).map(
                (s) => DropdownMenuItem<ReportStatus?>(
                  value: s,
                  child: Text(s.display, style: GoogleFonts.openSans(fontSize: 13)),
                ),
              ),
        ],
        onChanged: (value) {
          setState(() => _selectedStatusFilter = value);
          _loadReports();
        },
        underline: const SizedBox(),
        icon: const Icon(Icons.arrow_drop_down, size: 20),
      ),
    );
  }*/

  Widget _buildTypeDropdown() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      decoration: BoxDecoration(
        border: Border.all(color: AppColors.textGrey.withValues(alpha: 0.3)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButton<ReportType?>(
        value: _selectedTypeFilter,
        hint: Text('All types', style: GoogleFonts.openSans(fontSize: 13)),
        items: [
          DropdownMenuItem<ReportType?>(
            value: null,
            child: Text('All', style: GoogleFonts.openSans(fontSize: 13)),
          ),
          ...ReportType.values.where((t) => t != ReportType.unknown).map(
                (t) => DropdownMenuItem<ReportType?>(
                  value: t,
                  child: Text(t.display, style: GoogleFonts.openSans(fontSize: 13)),
                ),
              ),
        ],
        onChanged: (value) {
          setState(() => _selectedTypeFilter = value);
          _loadReports();
        },
        underline: const SizedBox(),
        icon: const Icon(Icons.arrow_drop_down, size: 20),
      ),
    );
  }

  Widget _buildReportCard(BuildContext context, Report report) {
    return GestureDetector(
      onTap: () {
        context.go('/reports/${report.reportId}');
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
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
        child: Row(
          children: [
            /*if (report.isUrgent)
              Container(
                width: 4,
                height: 50,
                decoration: BoxDecoration(
                  color: AppColors.error,
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            if (report.isUrgent) const SizedBox(width: 12),*/
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        '#${report.reportId}',
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
                          color: report.statusColor.withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          report.status.display,
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
                          color: report.severityColor.withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          report.reportType.display,
                          style: const TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    report.contentPreview,
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      color: AppColors.textGrey,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      const Icon(Icons.person_outline, size: 12, color: AppColors.textGrey),
                      const SizedBox(width: 4),
                      Text(
                        report.reporterName,
                        style: GoogleFonts.openSans(
                          fontSize: 11,
                          color: AppColors.textGrey,
                        ),
                      ),
                      const SizedBox(width: 12),
                      const Icon(Icons.access_time, size: 12, color: AppColors.textGrey),
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
                    context.go('/reports/${report.reportId}');
                  },
                  style: TextButton.styleFrom(
                    foregroundColor: AppColors.primaryTeal,
                  ),
                  child: const Text('View'),
                ),
                if (report.status != ReportStatus.resolved)
                  ElevatedButton(
                    onPressed: () {
                      context.go('/reports/${report.reportId}');
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
            color: AppColors.textGrey.withValues(alpha: 0.3),
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
}