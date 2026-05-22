import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class TaskCompletionScreen extends StatefulWidget {
  final String taskId;
  final String taskTitle;
  const TaskCompletionScreen({super.key, required this.taskId, required this.taskTitle});

  @override
  State<TaskCompletionScreen> createState() => _TaskCompletionScreenState();
}

class _TaskCompletionScreenState extends State<TaskCompletionScreen> {
  double _rating = 0;
  final _commentController = TextEditingController();
  

  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }

  void _submitCompletion() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Task completed! Thank you for helping!')),
    );
    Navigator.popUntil(context, (route) => route.isFirst);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF264653)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Complete Task',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFA5D8C9).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Row(
                children: [
                  const Icon(Icons.check_circle, color: Color(0xFF2A9D8F), size: 32),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Mark "${widget.taskTitle}" as complete?',
                          style: GoogleFonts.poppins(
                            color: const Color(0xFF264653),
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        Text(
                          'You will earn XP and the resident can rate you',
                          style: GoogleFonts.openSans(
                            color: const Color(0xFF264653),
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            Text('Rate the Resident', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 16, fontWeight: FontWeight.w600)),
            const SizedBox(height: 8),
            Row(
              children: [
                for (int i = 1; i <= 5; i++)
                  IconButton(
                    onPressed: () => setState(() => _rating = i.toDouble()),
                    icon: Icon(
                      i <= _rating ? Icons.star : Icons.star_border,
                      color: const Color(0xFFE9C46A),
                      size: 32,
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 16),
            Text('Add a Comment (Optional)', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 16, fontWeight: FontWeight.w600)),
            const SizedBox(height: 8),
            TextField(
              controller: _commentController,
              maxLines: 3,
              decoration: InputDecoration(
                hintText: 'Thanks for your help!',
                hintStyle: GoogleFonts.openSans(color: const Color(0xFFB0ADB0), fontSize: 14),
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
                filled: true,
                fillColor: Colors.white,
              ),
            ),
            const SizedBox(height: 16),
            Text('Upload Photo (Optional)', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 16, fontWeight: FontWeight.w600)),
            const SizedBox(height: 8),
            GestureDetector(
              onTap: () {
                // TODO: Implement photo picker
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Photo picker coming soon')),
                );
              },
              child: Container(
                height: 120,
                width: double.infinity,
                decoration: BoxDecoration(
                  border: Border.all(color: const Color(0xFFE5E2E0), style: BorderStyle.solid, width: 1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.cloud_upload, size: 40, color: Color(0xFF2A9D8F)),
                    const SizedBox(height: 8),
                    Text(
                      'Tap to upload completion photo',
                      style: GoogleFonts.openSans(color: const Color(0xFFB0ADB0), fontSize: 14),
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 32),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _rating > 0 ? _submitCompletion : null,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2A9D8F),
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  disabledBackgroundColor: const Color(0xFFE5E2E0),
                ),
                child: Text(
                  'Complete Task',
                  style: GoogleFonts.openSans(fontSize: 16, fontWeight: FontWeight.w600),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}