// admin/lib/screens/applications/application_detail_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart';
import '../../services/admin_application_service.dart';
import '../../providers/service_providers.dart';

class ApplicationDetailScreen extends ConsumerStatefulWidget {
  final int applicationId;

  const ApplicationDetailScreen({
    super.key,
    required this.applicationId,
  });

  @override
  ConsumerState<ApplicationDetailScreen> createState() =>
      _ApplicationDetailScreenState();
}

class _ApplicationDetailScreenState
    extends ConsumerState<ApplicationDetailScreen> {
  late final IAdminApplicationService _service;

  AdminApplication? _application;
  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _service = ref.read(adminApplicationServiceProvider);
    _loadApplication();
  }

  Future<void> _loadApplication() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final application =
          await _service.getApplicationById(widget.applicationId);

      setState(() {
        _application = application;
        _isLoading = false;
      });
    } on AdminApplicationServiceException catch (e) {
      setState(() {
        _errorMessage = e.message;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to load application';
        _isLoading = false;
      });
    }
  }


  Future<void> _approve() async {
    final confirmed = await _showConfirmDialog(
      title: 'Approve Application?',
      message:
          'This will grant the user admin access. They will be notified automatically.',
      confirmLabel: 'Approve',
      confirmColor: AppColors.success,
    );
    if (!confirmed) return;

    setState(() => _isSubmitting = true);

    try {
      await _service.approveApplication(widget.applicationId);

      if (!mounted) return;

      _showSnack('Application approved successfully.', AppColors.success);
      await _loadApplication();
      if (mounted) setState(() => _isSubmitting = false);

    } on AdminApplicationServiceException catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      _showSnack(e.message, AppColors.error);
    } catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      _showSnack('Failed to approve application.', AppColors.error);
    }
  }

  Future<void> _reject() async {
    final reason = await _showRejectDialog();
    if (reason == null) return;

    setState(() => _isSubmitting = true);

    try {
       await _service.rejectApplication(
        widget.applicationId,
        rejectionReason: reason.isEmpty ? null : reason,
      );

      if (!mounted) return;

      _showSnack('Application rejected.', AppColors.error);
      await _loadApplication();
      if (mounted) setState(() => _isSubmitting = false);

    } on AdminApplicationServiceException catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      _showSnack(e.message, AppColors.error);
    } catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      _showSnack('Failed to reject application.', AppColors.error);
    }
  }

  Future<bool> _showConfirmDialog({
    required String title,
    required String message,
    required String confirmLabel,
    required Color confirmColor,
  }) async {
    final result = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(title,
            style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
        content: Text(message, style: GoogleFonts.openSans()),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            style: ElevatedButton.styleFrom(backgroundColor: confirmColor),
            child: Text(confirmLabel),
          ),
        ],
      ),
    );
    return result ?? false;
  }

  Future<String?> _showRejectDialog() async {
    final reasonController = TextEditingController();

    final result = await showDialog<String>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text('Reject Application?',
            style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Optionally provide a reason that will be shown to the applicant.',
              style: GoogleFonts.openSans(fontSize: 13),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: reasonController,
              maxLines: 3,
              decoration: const InputDecoration(
                hintText: 'Reason (optional)',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () =>
                Navigator.pop(dialogContext, reasonController.text.trim()),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.error,
            ),
            child: const Text('Reject'),
          ),
        ],
      ),
    );

    return result;
  }

  void _showSnack(String message, Color color) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: color),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_errorMessage != null || _application == null) {
      return _buildErrorState();
    }
    return _buildDetail();
  }

  Widget _buildErrorState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: AppColors.error),
          const SizedBox(height: 16),
          Text(
            'Failed to load application',
            style: GoogleFonts.poppins(
              fontSize: 18,
              fontWeight: FontWeight.w600,
              color: AppColors.charcoal,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            _errorMessage ?? 'Please try again later',
            style: GoogleFonts.openSans(
              fontSize: 14,
              color: AppColors.textGrey,
            ),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _loadApplication,
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }

  Widget _buildDetail() {
    final application = _application!;
    final statusColor = application.isApproved
        ? AppColors.success
        : application.isRejected
            ? AppColors.error
            : AppColors.citrusYellow;

    return SingleChildScrollView(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Back button + title
          Row(
            children: [
              IconButton(
                onPressed: () => context.go('/applications'),
                icon: const Icon(Icons.arrow_back),
                tooltip: 'Back to applications',
              ),
              const SizedBox(width: 8),
              Text(
                'Application #${application.applicationId}',
                style: GoogleFonts.poppins(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: AppColors.charcoal,
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),

          // Status badge
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
            decoration: BoxDecoration(
              color: statusColor.withValues(alpha: 0.1),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: statusColor),
            ),
            child: Text(
              application.applicationStatus,
              style: TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w600,
                color: statusColor,
              ),
            ),
          ),
          const SizedBox(height: 16),

          // Applicant details card
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
                  'Applicant',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                ),
                const SizedBox(height: 12),
                _buildInfoRow('Username',
                    application.username ?? 'User #${application.userId}'),
                _buildInfoRow('User ID', application.userId.toString()),
                _buildInfoRow(
                    'Submitted', _formatDate(application.applicationDate)),
              ],
            ),
          ),
          const SizedBox(height: 16),

          // Justification card
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
                  'Justification',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                ),
                const SizedBox(height: 12),
                Text(
                  application.justification,
                  style: GoogleFonts.openSans(
                    fontSize: 14,
                    color: AppColors.charcoal,
                    height: 1.5,
                  ),
                ),
              ],
            ),
          ),

          // Rejection reason (if rejected)
          if (application.isRejected &&
              application.rejectionReason != null) ...[
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.error.withValues(alpha: 0.05),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                    color: AppColors.error.withValues(alpha: 0.3)),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Rejection Reason',
                    style: GoogleFonts.poppins(
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                      color: AppColors.error,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    application.rejectionReason!,
                    style: GoogleFonts.openSans(
                      fontSize: 13,
                      color: AppColors.charcoal,
                    ),
                  ),
                ],
              ),
            ),
          ],

          const SizedBox(height: 24),

          // Action buttons (only if pending)
          if (application.isPending)
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _isSubmitting ? null : _approve,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.success,
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                    icon: _isSubmitting
                        ? const SizedBox(
                            width: 16,
                            height: 16,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white,
                            ),
                          )
                        : const Icon(Icons.check),
                    label: const Text('Approve'),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: OutlinedButton.icon(
                    onPressed: _isSubmitting ? null : _reject,
                    style: OutlinedButton.styleFrom(
                      foregroundColor: AppColors.error,
                      side: const BorderSide(color: AppColors.error),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                    ),
                    icon: const Icon(Icons.close),
                    label: const Text('Reject'),
                  ),
                ),
              ],
            ),
        ],
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
            width: 100,
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

  String _formatDate(DateTime? date) {
    if (date == null) return 'Unknown';
    return '${date.day}/${date.month}/${date.year}';
  }
}