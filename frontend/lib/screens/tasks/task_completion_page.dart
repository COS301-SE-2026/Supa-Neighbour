import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import '../../models/task_model.dart';
import '../../constants/app_colors.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class TaskCompletionPage extends ConsumerStatefulWidget {
  final String taskId;
  final String taskTitle;
  final String residentName;
  final String dueDate;
  final int xpReward;

  const TaskCompletionPage({
    super.key,
    required this.taskId,
    required this.taskTitle,
    required this.residentName,
    required this.dueDate,
    required this.xpReward,
  });

  @override
  ConsumerState<TaskCompletionPage> createState() => _TaskCompletionPageState();
}

class _TaskCompletionPageState extends ConsumerState<TaskCompletionPage> {
  final TextEditingController _noteController = TextEditingController();
  final List<XFile> _selectedImages = [];
  bool _isSubmitting = false;
  final ImagePicker _picker = ImagePicker();

  @override
  void dispose() {
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _addPhoto() async {
    final XFile? picked = await _picker.pickImage(
      source: ImageSource.gallery,
      imageQuality: 85,
      maxWidth: 1920,
    );

    if(picked != null) {
      setState(() {
        _selectedImages.add(picked);
      });
    }
  }

  void _removePhoto(int index) {
    setState(() {
      _selectedImages.removeAt(index);
    });
  }

  Future<void> _showCompletionDialog() async {
    final confirmed = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        title: Text(
          'Complete Task?',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Resident will be notified to confirm completion.',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'You will earn +${widget.xpReward} XP upon resident confirmation.',
              style: GoogleFonts.openSans(
                color: AppColors.citrusYellow(context),
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: Text(
              'Confirm',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _submitCompletion();
    }
  }

  Future<void> _submitCompletion() async {
    setState(() => _isSubmitting = true);
    try {
      final taskService = ref.read(taskServiceProvider);

      final List<String> uploadedUrls = [];
      for (final image in _selectedImages) {
        final url = await taskService.uploadTaskImage(image);
        if (url != null) uploadedUrls.add(url);
      }

      if (uploadedUrls.isNotEmpty) {
        await taskService.saveTaskImages(int.parse(widget.taskId), uploadedUrls);
      }

      await taskService.updateTask(
        taskId: int.parse(widget.taskId),
        status: 'pending_approval',
        helperRatingId: _noteController.text.isNotEmpty ? _noteController.text : null,
      );

      Task.updateTaskStatus(widget.taskId, 'pending_approval');

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Task submitted! Waiting for resident confirmation.'),
            backgroundColor: AppColors.primaryTeal(context),
          ),
        );
        Navigator.pop(context);
      }
    } on Exception catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(e.toString().replaceAll('Exception: ', '')),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Completion',
          style: GoogleFonts.poppins(
            fontWeight: FontWeight.w600,
            color: AppColors.charcoal(context),
          ),
        ),
        backgroundColor: AppColors.background(context),
        elevation: 0,
        foregroundColor: AppColors.charcoal(context),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Task Info Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: isDarkMode ? AppColors.surfaceGrey(context) : AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: isDarkMode 
                        ? Colors.black.withValues(alpha: 0.2) 
                        : Colors.grey.shade200,
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.taskTitle,
                    style: GoogleFonts.poppins(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Resident: ${widget.residentName}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Due: ${widget.dueDate}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '+${widget.xpReward} XP',
                    style: GoogleFonts.openSans(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColors.citrusYellow(context),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Completion Proof Section
            Text(
              'Completion Proof',
              style: GoogleFonts.poppins(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'Add photos to show your work (optional)',
              style: GoogleFonts.openSans(
                fontSize: 12,
                color: AppColors.textGrey(context),
              ),
            ),
            const SizedBox(height: 12),

            // Photo Grid
            SizedBox(
              height: 100,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: _selectedImages.length + 1,
                itemBuilder: (context, index) {
                  // Add Photo Button
                  if (index == _selectedImages.length) {
                    return GestureDetector(
                      onTap: _addPhoto,
                      child: Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          border: Border.all(
                            color: AppColors.primaryTeal(context),
                            width: 2,
                          ),
                          borderRadius: BorderRadius.circular(12),
                          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.add,
                              color: AppColors.primaryTeal(context),
                              size: 32,
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Add Photo',
                              style: GoogleFonts.openSans(
                                fontSize: 10,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  }

                  // Photo Item
                  return Stack(
                    children: [
                      Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.grey.shade300,
                          borderRadius: BorderRadius.circular(12),
                          image: DecorationImage(
                            image: FileImage(File(_selectedImages[index].path)),
                            fit: BoxFit.cover,
                          ),
                        ),
                        child: null,
                      ),
                      Positioned(
                        top: 4,
                        right: 4,
                        child: GestureDetector(
                          onTap: () => _removePhoto(index),
                          child: Container(
                            decoration: const BoxDecoration(
                              color: Colors.red,
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(
                              Icons.close,
                              size: 20,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                    ],
                  );
                },
              ),
            ),

            const SizedBox(height: 24),

            // Completion Note
            Text(
              'Completion Note (optional)',
              style: GoogleFonts.poppins(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _noteController,
              maxLines: 4,
              decoration: InputDecoration(
                hintText: 'Tell the resident what you did...',
                hintStyle: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
              ),
            ),

            const SizedBox(height: 24),

            // Submit Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSubmitting ? null : _showCompletionDialog,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryTeal(context),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                  disabledBackgroundColor: AppColors.surfaceGrey(context),
                ),
                child: _isSubmitting
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      )
                    : Text(
                        'MARK AS COMPLETE',
                        style: GoogleFonts.poppins(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          color: Colors.white,
                        ),
                      ),
              ),
            ),

            const SizedBox(height: 12),

            // Info Text
            Center(
              child: Text(
                'Resident will need to confirm before XP is awarded',
                style: GoogleFonts.openSans(
                  fontSize: 12,
                  color: AppColors.textGrey(context),
                ),
              ),
            ),

            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}