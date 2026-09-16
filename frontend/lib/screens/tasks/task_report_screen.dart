import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
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

  /// Locally selected photos — UI only for now.
  /// Backend submission for these is stubbed out until the
  /// report-photo endpoint and ReportRequestDTO are extended.
  final List<File> _selectedImages = [];
  final ImagePicker _picker = ImagePicker();

  final List<String> _disputeReasons = ['NO_SHOW', 'INCOMPLETE', 'DAMAGE'];

  /// Maximum photos the user can attach.
  static const int _maxImages = 5;

  @override
  void dispose() {
    _reasonController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _pickImage(ImageSource source) async {
    if (_selectedImages.length >= _maxImages) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('You can attach up to $_maxImages photos.'),
          backgroundColor: AppColors.error(context),
        ),
      );
      return;
    }

    try {
      final XFile? picked = await _picker.pickImage(
        source: source,
        imageQuality: 75,
        maxWidth: 1600,
      );
      if (picked == null) return;

      setState(() {
        _selectedImages.add(File(picked.path));
      });
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to pick image: $e'),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  void _removeImage(int index) {
    setState(() => _selectedImages.removeAt(index));
  }

  /// Bottom sheet with camera / gallery options.
  void _showImageSourceSheet() {
    showModalBottomSheet(
      context: context,
      backgroundColor: AppColors.background(context),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (sheetContext) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 12),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // Grab handle
                Container(
                  width: 40,
                  height: 4,
                  margin: const EdgeInsets.only(bottom: 12),
                  decoration: BoxDecoration(
                    color: AppColors.textGrey(context).withValues(alpha: 0.3),
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
                Text(
                  'Add Photo',
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 12),
                ListTile(
                  leading: Icon(
                    Icons.camera_alt_outlined,
                    color: AppColors.primaryTeal(context),
                  ),
                  title: Text(
                    'Take a photo',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  onTap: () {
                    Navigator.pop(sheetContext);
                    _pickImage(ImageSource.camera);
                  },
                ),
                ListTile(
                  leading: Icon(
                    Icons.photo_library_outlined,
                    color: AppColors.primaryTeal(context),
                  ),
                  title: Text(
                    'Choose from gallery',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  onTap: () {
                    Navigator.pop(sheetContext);
                    _pickImage(ImageSource.gallery);
                  },
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _submitReport() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    try {
      // NOTE: Images are intentionally NOT included in the request yet.
      //       When the backend adds `photoUrls` to ReportRequestDTO and an
      //       upload endpoint, we'll upload each _selectedImages entry first
      //       and pass the returned URLs through.
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
      body: SingleChildScrollView(
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
                    borderSide:
                        BorderSide(color: AppColors.surfaceGrey(context)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(
                        color: AppColors.primaryTeal(context), width: 2),
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
                    borderSide:
                        BorderSide(color: AppColors.surfaceGrey(context)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(
                        color: AppColors.primaryTeal(context), width: 2),
                  ),
                ),
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                ),
                validator: (value) => value?.trim().isEmpty ?? true
                    ? 'Please enter a reason'
                    : null,
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
                    borderSide:
                        BorderSide(color: AppColors.surfaceGrey(context)),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                    borderSide: BorderSide(
                        color: AppColors.primaryTeal(context), width: 2),
                  ),
                ),
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                ),
                validator: (value) => value?.trim().isEmpty ?? true
                    ? 'Please provide a description'
                    : null,
              ),
              const SizedBox(height: 20),

              // ===== PHOTO UPLOAD SECTION (UI ONLY) =====
              Row(
                children: [
                  Icon(
                    Icons.photo_camera_outlined,
                    color: AppColors.primaryTeal(context),
                    size: 20,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Attach photos',
                    style: GoogleFonts.poppins(
                      fontSize: 15,
                      fontWeight: FontWeight.w600,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    '(${_selectedImages.length}/$_maxImages)',
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      color: AppColors.textGrey(context),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Text(
                'Optional — helps support your report (damage, incomplete work, etc.)',
                style: GoogleFonts.openSans(
                  fontSize: 12,
                  color: AppColors.textGrey(context),
                ),
              ),
              const SizedBox(height: 12),

              // Thumbnails grid
              if (_selectedImages.isNotEmpty)
                Wrap(
                  spacing: 10,
                  runSpacing: 10,
                  children: [
                    for (int i = 0; i < _selectedImages.length; i++)
                      _buildImageThumbnail(i),
                  ],
                ),

              if (_selectedImages.isNotEmpty) const SizedBox(height: 12),

              // Add photo button
              if (_selectedImages.length < _maxImages)
                GestureDetector(
                  onTap: _showImageSourceSheet,
                  child: DottedBorderBox(
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.add_photo_alternate_outlined,
                          color: AppColors.primaryTeal(context),
                          size: 22,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          'Add photo',
                          style: GoogleFonts.openSans(
                            color: AppColors.primaryTeal(context),
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                  ),
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
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }

  /// 80x80 image thumbnail with a small delete "X" button.
  Widget _buildImageThumbnail(int index) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: Image.file(
            _selectedImages[index],
            width: 80,
            height: 80,
            fit: BoxFit.cover,
          ),
        ),
        Positioned(
          top: -6,
          right: -6,
          child: GestureDetector(
            onTap: () => _removeImage(index),
            child: Container(
              padding: const EdgeInsets.all(3),
              decoration: BoxDecoration(
                color: AppColors.error(context),
                shape: BoxShape.circle,
                border: Border.all(
                  color: AppColors.background(context),
                  width: 1.5,
                ),
              ),
              child: const Icon(Icons.close, color: Colors.white, size: 12),
            ),
          ),
        ),
      ],
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

/// Simple dashed-look container — Flutter doesn't ship a built-in one.
class DottedBorderBox extends StatelessWidget {
  final Widget child;
  const DottedBorderBox({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 16),
      decoration: BoxDecoration(
        color: AppColors.surfaceGrey(context),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: AppColors.primaryTeal(context).withValues(alpha: 0.4),
          width: 1.5,
        ),
      ),
      child: child,
    );
  }
}