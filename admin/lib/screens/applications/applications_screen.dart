// admin/lib/screens/applications/applications_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart';
import '../../providers/service_providers.dart';
import '../../services/admin_application_service.dart';

class ApplicationsScreen extends ConsumerStatefulWidget {
  const ApplicationsScreen({super.key});

  @override
  ConsumerState<ApplicationsScreen> createState() =>
      _ApplicationsScreenState();
}

class _ApplicationsScreenState extends ConsumerState<ApplicationsScreen> {
  late final IAdminApplicationService _service;

  String? _selectedStatusFilter;
  String _searchQuery = '';
  List<AdminApplication> _applications = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _service = ref.read(adminApplicationServiceProvider);
    _loadApplications();
  }

  Future<void> _loadApplications() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final applications = await _service.getAllApplications(
        status: _selectedStatusFilter,
      );

      setState(() {
        _applications = applications;
        _isLoading = false;
      });
    } on AdminApplicationServiceException catch (e) {
      setState(() {
        _errorMessage = e.message;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load applications';
        _isLoading = false;
      });
    }
  }

  List<AdminApplication> _getFilteredApplications() {
    if (_searchQuery.isEmpty) return _applications;
    return _applications
        .where((a) =>
            (a.username ?? '')
                .toLowerCase()
                .contains(_searchQuery.toLowerCase()) ||
            a.userId.toString().contains(_searchQuery))
        .toList();
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _getFilteredApplications();
    final pendingCount = _applications.where((a) => a.isPending).length;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Stats chips
        Row(
          children: [
            _buildStatChip('Total', _applications.length, AppColors.primaryTeal),
            const SizedBox(width: 12),
            _buildStatChip('Pending', pendingCount, AppColors.citrusYellow),
            const SizedBox(width: 12),
            _buildStatChip(
              'Approved',
              _applications.where((a) => a.isApproved).length,
              AppColors.success,
            ),
            const SizedBox(width: 12),
            _buildStatChip(
              'Rejected',
              _applications.where((a) => a.isRejected).length,
              AppColors.error,
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Search and filter
        Row(
          children: [
            Expanded(
              child: TextField(
                onChanged: (value) {
                  setState(() => _searchQuery = value);
                },
                decoration: const InputDecoration(
                  hintText: 'Search by username or user ID...',
                  prefixIcon: Icon(Icons.search),
                  isDense: true,
                ),
              ),
            ),
            const SizedBox(width: 12),
            _buildStatusDropdown(),
          ],
        ),
        const SizedBox(height: 16),

        if (_errorMessage != null) _buildErrorBanner(),

        // Application list
        Expanded(
          child: _isLoading
              ? const Center(child: CircularProgressIndicator())
              : filtered.isEmpty
                  ? _buildEmptyState()
                  : ListView.builder(
                      itemCount: filtered.length,
                      itemBuilder: (context, index) {
                        return _buildApplicationCard(context, filtered[index]);
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
          TextButton(onPressed: _loadApplications, child: const Text('Retry')),
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

  Widget _buildStatusDropdown() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8),
      decoration: BoxDecoration(
        border: Border.all(color: AppColors.textGrey.withValues(alpha: 0.3)),
        borderRadius: BorderRadius.circular(8),
      ),
      child: DropdownButton<String?>(
        value: _selectedStatusFilter,
        hint: Text('All statuses', style: GoogleFonts.openSans(fontSize: 13)),
        items: [
          DropdownMenuItem<String?>(
            value: null,
            child: Text('All', style: GoogleFonts.openSans(fontSize: 13)),
          ),
          DropdownMenuItem<String?>(
            value: 'Pending',
            child: Text('Pending', style: GoogleFonts.openSans(fontSize: 13)),
          ),
          DropdownMenuItem<String?>(
            value: 'Approved',
            child: Text('Approved', style: GoogleFonts.openSans(fontSize: 13)),
          ),
          DropdownMenuItem<String?>(
            value: 'Rejected',
            child: Text('Rejected', style: GoogleFonts.openSans(fontSize: 13)),
          ),
        ],
        onChanged: (value) {
          setState(() => _selectedStatusFilter = value);
          _loadApplications();
        },
        underline: const SizedBox(),
        icon: const Icon(Icons.arrow_drop_down, size: 20),
      ),
    );
  }

  Widget _buildApplicationCard(
      BuildContext context, AdminApplication application) {
    final statusColor = application.isApproved
        ? AppColors.success
        : application.isRejected
            ? AppColors.error
            : AppColors.citrusYellow;

    return GestureDetector(
      onTap: () {
        context.go('/applications/${application.applicationId}');
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
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        'Application #${application.applicationId}',
                        style: GoogleFonts.poppins(
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                          color: AppColors.charcoal,
                        ),
                      ),
                      const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: statusColor.withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          application.applicationStatus,
                          style: TextStyle(
                            fontSize: 10,
                            fontWeight: FontWeight.w600,
                            color: statusColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    application.justification,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      color: AppColors.textGrey,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.person_outline,
                          size: 12, color: AppColors.textGrey),
                      const SizedBox(width: 4),
                      Text(
                        application.username ?? 'User #${application.userId}',
                        style: GoogleFonts.openSans(
                          fontSize: 11,
                          color: AppColors.textGrey,
                        ),
                      ),
                      const SizedBox(width: 12),
                      const Icon(Icons.calendar_today,
                          size: 12, color: AppColors.textGrey),
                      const SizedBox(width: 4),
                      Text(
                        _formatDate(application.applicationDate),
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
                    context.go('/applications/${application.applicationId}');
                  },
                  style: TextButton.styleFrom(
                    foregroundColor: AppColors.primaryTeal,
                  ),
                  child: const Text('View'),
                ),
                if (application.isPending)
                  ElevatedButton(
                    onPressed: () {
                      context.go('/applications/${application.applicationId}');
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryTeal,
                      minimumSize: const Size(70, 32),
                    ),
                    child: const Text('Review',
                        style: TextStyle(fontSize: 12)),
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
            Icons.admin_panel_settings_outlined,
            size: 64,
            color: AppColors.textGrey.withValues(alpha: 0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'No applications found',
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

  String _formatDate(DateTime? date) {
    if (date == null) return 'Unknown';
    return '${date.day}/${date.month}/${date.year}';
  }
}