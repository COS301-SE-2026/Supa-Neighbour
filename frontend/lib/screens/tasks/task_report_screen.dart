import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/report_request.dart';
import '../../providers/service_providers.dart';

class TaskReportScreen extends ConsumerStatefulWidget {
  final int taskId;
  final String taskTitle;

  const TaskReportScreen({
    super.key,
    required this.taskId,
    required this.taskTitle,
  });

  @override
  ConsumerState<TaskReportScreen> createState() => _TaskReportScreenState();
}

class _TaskReportScreenState extends ConsumerState<TaskReportScreen> {
  final _formKey = GlobalKey<FormState>();
  final _reasonController = TextEditingController();
  final _descriptionController = TextEditingController();
  String? _selectedDisputeReason;
  bool _isSubmitting = false;

  final List<String> _disputeReasons = ['NO_SHOW', 'INCOMPLETE', 'DAMAGE'];

  @override
  void dispose() {
    _reasonController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _submitReport() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    try {
      final request = ReportRequest(
        reportType: 'TASK_DISPUTE',
        taskId: widget.taskId,
        disputeReason: _selectedDisputeReason,
        reason: _reasonController.text.trim(),
        description: _descriptionController.text.trim(),
      );

      final reportService = ref.read(reportServiceProvider);
      await reportService.submitTaskReport(request);

      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Report submitted successfully.'),
          backgroundColor: Colors.green,
        ),
      );
      Navigator.pop(context, true);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to submit report: $e'),
          backgroundColor: AppColors.error(context),
        ),
      );
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
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
          'Report Task',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Reporting task: ${widget.taskTitle}',
                style: GoogleFonts.poppins(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                  color: AppColors.charcoal(context),
                ),
              ),
              const SizedBox(height: 24),

              // Dispute Reason Dropdown
              DropdownButtonFormField<String>(
                value: _selectedDisputeReason,
                decoration: InputDecoration(
                  labelText: 'Dispute Reason *',
                  labelStyle: GoogleFonts.openSans(
                    color: AppColors.textGrey(context),
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
                ),
                items: _disputeReasons.map((reason) {
                  return DropdownMenuItem(
                    value: reason,
                    child: Text(
                      _getDisputeReasonLabel(reason),
                      style: GoogleFonts.openSans(
                        color: AppColors.charcoal(context),
                      ),
                    ),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() => _selectedDisputeReason = value);
                },
                validator: (value) =>
                    value == null ? 'Please select a dispute reason' : null,
              ),
              const SizedBox(height: 16),

              // Reason
              TextFormField(
                controller: _reasonController,
                maxLines: 1,
                decoration: InputDecoration(
                  labelText: 'Reason (short label) *',
                  labelStyle: GoogleFonts.openSans(
                    color: AppColors.textGrey(context),
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
                ),
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                ),
                validator: (value) =>
                    value?.trim().isEmpty ?? true ? 'Please enter a reason' : null,
              ),
              const SizedBox(height: 16),

              // Description
              TextFormField(
                controller: _descriptionController,
                maxLines: 5,
                decoration: InputDecoration(
                  labelText: 'Description *',
                  labelStyle: GoogleFonts.openSans(
                    color: AppColors.textGrey(context),
                  ),
                  alignLabelWithHint: true,
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
                ),
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                ),
                validator: (value) =>
                    value?.trim().isEmpty ?? true ? 'Please provide a description' : null,
              ),
              const SizedBox(height: 32),

              // Submit Button
              SizedBox(
                width: double.infinity,
                height: 50,
                child: ElevatedButton(
                  onPressed: _isSubmitting ? null : _submitReport,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primaryTeal(context),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: _isSubmitting
                      ? const SizedBox(
                          width: 24,
                          height: 24,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: Colors.white,
                          ),
                        )
                      : Text(
                          'Submit Report',
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
      ),
    );
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
}