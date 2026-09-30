import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart' hide AppColors;
import '../../constants/app_colors.dart';
import '../../providers/service_providers.dart';
import '../help/help_menu_screen.dart';

class AdminApplicationScreen extends ConsumerStatefulWidget {
  const AdminApplicationScreen({super.key});

  @override
  ConsumerState<AdminApplicationScreen> createState() =>
      _AdminApplicationScreenState();
}

class _AdminApplicationScreenState
    extends ConsumerState<AdminApplicationScreen> {
  final _formKey = GlobalKey<FormState>();
  final _justificationController = TextEditingController();
  bool _isSubmitting = false;
  AdminApplication? _existingApplication;

  @override
  void initState() {
    super.initState();
    _loadExistingApplication();
  }

  @override
  void dispose() {
    _justificationController.dispose();
    super.dispose();
  }

  Future<void> _loadExistingApplication() async {
    try {
      final service = ref.read(adminApplicationServiceProvider);
      final applications = await service.getMyApplications();
      if (applications.isNotEmpty && mounted) {
        setState(() {
          _existingApplication = applications.first;
        });
      }
    } catch (_) {
      // Silent fail — no existing application
    }
  }

  Future<void> _submitApplication() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    try {
      final service = ref.read(adminApplicationServiceProvider);
      final application = await service.submitApplication(
        _justificationController.text.trim(),
      );

      if (!mounted) return;

      setState(() {
        _existingApplication = application;
        _isSubmitting = false;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Application submitted successfully!'),
          backgroundColor: AppColors.success(context),
          duration: const Duration(seconds: 3),
        ),
      );
    } catch (e) {
      if (!mounted) return;

      setState(() => _isSubmitting = false);

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(e.toString().replaceFirst('Exception: ', '')),
          backgroundColor: AppColors.error(context),
          duration: const Duration(seconds: 3),
        ),
      );
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
          icon: Icon(Icons.arrow_back, color: AppColors.primaryTeal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Apply to be Admin',
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.info_outline, color: AppColors.primaryTeal(context)),
            onPressed: () {
              HelpMenuScreen.showHelpModal(context, 'admin_application');
            },
          ),
        ],
      ),
      body: _existingApplication != null
          ? _buildStatusView(_existingApplication!)
          : _buildApplicationForm(),
    );
  }

  Widget _buildStatusView(AdminApplication application) {
    final statusColor = application.isApproved
        ? AppColors.success(context)
        : application.isRejected
            ? AppColors.error(context)
            : AppColors.citrusYellow(context);

    final statusIcon = application.isApproved
        ? Icons.check_circle_outline
        : application.isRejected
            ? Icons.cancel_outlined
            : Icons.hourglass_empty;

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(statusIcon, size: 80, color: statusColor),
            const SizedBox(height: 24),
            Text(
              'Application ${application.applicationStatus}',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 22,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              application.isPending
                  ? 'Your application is being reviewed. You will be notified once a decision has been made.'
                  : application.isApproved
                      ? 'Congratulations! You are now an admin.'
                      : 'Your application was not approved this time.',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            if (application.rejectionReason != null) ...[
              const SizedBox(height: 16),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.surfaceGrey(context),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  'Reason: ${application.rejectionReason}',
                  style: GoogleFonts.openSans(
                    color: AppColors.charcoal(context),
                    fontSize: 13,
                  ),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildApplicationForm() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(24),
      child: Form(
        key: _formKey,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(
              Icons.admin_panel_settings_outlined,
              size: 80,
              color: AppColors.primaryTeal(context),
            ),
            const SizedBox(height: 16),
            Text(
              'Become an Admin',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 22,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Admins help moderate the community, review reports, and keep Supa-Neighbour safe for everyone.',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 32),
            Text(
              'Why do you want to be an admin?',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            TextFormField(
              controller: _justificationController,
              maxLines: 6,
              maxLength: 500,
              decoration: InputDecoration(
                hintText: 'Tell us about your experience and why you would make a good admin...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: AppColors.primaryTeal(context)),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(color: AppColors.primaryTeal(context), width: 2),
                ),
                filled: true,
                fillColor: Theme.of(context).brightness == Brightness.dark
                    ? AppColors.surfaceGrey(context)
                    : Colors.white,
              ),
              validator: (value) {
                if (value == null || value.trim().isEmpty) {
                  return 'Please provide a justification';
                }
                if (value.trim().length < 20) {
                  return 'Please provide at least 20 characters';
                }
                return null;
              },
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSubmitting ? null : _submitApplication,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryTeal(context),
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: _isSubmitting
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      )
                    : Text(
                        'Submit Application',
                        style: GoogleFonts.openSans(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}