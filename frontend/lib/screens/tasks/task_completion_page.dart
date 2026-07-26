import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart'; // ADD: Import google_fonts
import '../../constants/app_colors.dart'; // ADD: Import AppColors

class TaskCompletionPage extends StatefulWidget {
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
  State<TaskCompletionPage> createState() => _TaskCompletionPageState();
}

class _TaskCompletionPageState extends State<TaskCompletionPage> {
  final TextEditingController _noteController = TextEditingController();
  final List<String> _photoPaths = [];
  bool _isSubmitting = false;

  @override
  void dispose() {
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _addPhoto() async {
    // TODO: Implement image picking logic
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Photo picker will be added soon'),
        duration: Duration(seconds: 2),
      ),
    );
  }

  void _removePhoto(int index) {
    setState(() {
      _photoPaths.removeAt(index);
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
                // CHANGE: Use AppColors.citrusYellow
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
              // CHANGE: Use AppColors.primaryTeal
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
    setState(() {
      _isSubmitting = true;
    });

    // TODO: Call API to submit task completion
    await Future.delayed(const Duration(seconds: 1)); // Simulates API call

    if (mounted) {
      setState(() {
        _isSubmitting = false;
      });

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Task submitted! Waiting for resident confirmation.'),
          // CHANGE: Use AppColors.primaryTeal
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Completion',
          style: GoogleFonts.poppins(
            fontWeight: FontWeight.w600,
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
          ),
        ),
        // CHANGE: Use AppColors.background
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
                // CHANGE: Use AppColors.surfaceGrey
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
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Resident: ${widget.residentName}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Due: ${widget.dueDate}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '+${widget.xpReward} XP',
                    style: GoogleFonts.openSans(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      // CHANGE: Use AppColors.citrusYellow
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
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 4),
            Text(
              'Add photos to show your work (optional)',
              style: GoogleFonts.openSans(
                fontSize: 12,
                // CHANGE: Use AppColors.textGrey
                color: AppColors.textGrey(context),
              ),
            ),
            const SizedBox(height: 12),

            // Photo Grid
            SizedBox(
              height: 100,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: _photoPaths.length + 1,
                itemBuilder: (context, index) {
                  // Add Photo Button
                  if (index == _photoPaths.length) {
                    return GestureDetector(
                      onTap: _addPhoto,
                      child: Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          border: Border.all(
                            // CHANGE: Use AppColors.primaryTeal
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
                              // CHANGE: Use AppColors.primaryTeal
                              color: AppColors.primaryTeal(context),
                              size: 32,
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Add Photo',
                              style: GoogleFonts.openSans(
                                fontSize: 10,
                                // CHANGE: Use AppColors.primaryTeal
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
                          image: _photoPaths[index].isNotEmpty
                              ? DecorationImage(
                                  image: FileImage(File(_photoPaths[index])),
                                  fit: BoxFit.cover,
                                )
                              : null,
                        ),
                        child: _photoPaths[index].isEmpty
                            ? Icon(
                                Icons.image,
                                color: isDarkMode ? AppColors.textGrey(context) : Colors.grey,
                                size: 40,
                              )
                            : null,
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
                // CHANGE: Use AppColors.charcoal
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
                  // CHANGE: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.primaryTeal
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
                  // CHANGE: Use AppColors.primaryTeal
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
                  // CHANGE: Use AppColors.textGrey
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