import 'dart:io';
import 'package:flutter/material.dart';

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
        title: const Text('Complete Task?'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('Resident will be notified to confirm completion.'),
            const SizedBox(height: 12),
            Text(
              'You will earn +${widget.xpReward} XP upon resident confirmation.',
              style: const TextStyle(
                color: Color(0xFFE9C46A),
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2A9D8F),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: const Text('Confirm'),
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
        const SnackBar(
          content: Text('Task submitted! Waiting for resident confirmation.'),
          backgroundColor: Color(0xFF2A9D8F),
        ),
      );
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        title: const Text(
          'Task Completion',
          style: TextStyle(
            fontFamily: 'Poppins',
            fontWeight: FontWeight.w600,
            color: Color(0xFF264653),
          ),
        ),
        backgroundColor: Colors.white,
        elevation: 0,
        foregroundColor: const Color(0xFF264653),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.grey.shade50,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.grey.shade200,
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
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                      fontFamily: 'Poppins',
                      color: Color(0xFF264653),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Resident: ${widget.residentName}',
                    style: const TextStyle(
                      fontSize: 14,
                      fontFamily: 'Inter',
                      color: Color(0xFF264653),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Due: ${widget.dueDate}',
                    style: const TextStyle(
                      fontSize: 14,
                      fontFamily: 'Inter',
                      color: Color(0xFF264653),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '+${widget.xpReward} XP',
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: Color(0xFFE9C46A),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            
            const Text(
              'Completion Proof',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                fontFamily: 'Poppins',
                color: Color(0xFF264653),
              ),
            ),
            const SizedBox(height: 4),
            const Text(
              'Add photos to show your work (optional)',
              style: TextStyle(
                fontSize: 12,
                fontFamily: 'Inter',
                color: Color(0xFF264653),
              ),
            ),
            const SizedBox(height: 12),

            
            SizedBox(
              height: 100,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: _photoPaths.length + 1,
                itemBuilder: (context, index) {
                  
                  if (index == _photoPaths.length) {
                    return GestureDetector(
                      onTap: _addPhoto,
                      child: Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          border: Border.all(
                            color: const Color(0xFF2A9D8F),
                            width: 2,
                          ),
                          borderRadius: BorderRadius.circular(12),
                          color: Colors.white,
                        ),
                        child: const Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.add,
                              color: Color(0xFF2A9D8F),
                              size: 32,
                            ),
                            SizedBox(height: 4),
                            Text(
                              'Add Photo',
                              style: TextStyle(
                                fontSize: 10,
                                color: Color(0xFF2A9D8F),
                                fontFamily: 'Inter',
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  }

                  
                  return Stack(
                    children: [
                      Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          color: Colors.grey.shade300,
                          borderRadius: BorderRadius.circular(12),
                          image: _photoPaths[index].isNotEmpty
                              ? DecorationImage(
                                  image: FileImage(File(_photoPaths[index])),
                                  fit: BoxFit.cover,
                                )
                              : null,
                        ),
                        child: _photoPaths[index].isEmpty
                            ? const Center(
                                child: Icon(
                                  Icons.image,
                                  color: Colors.grey,
                                  size: 40,
                                ),
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

            
            const Text(
              'Completion Note (optional)',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                fontFamily: 'Poppins',
                color: Color(0xFF264653),
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _noteController,
              maxLines: 4,
              decoration: InputDecoration(
                hintText: 'Tell the resident what you did...',
                hintStyle: const TextStyle(
                  color: Colors.grey,
                  fontFamily: 'Inter',
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Colors.grey),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: const BorderSide(color: Color(0xFF2A9D8F)),
                ),
                filled: true,
                fillColor: Colors.white,
              ),
            ),

            const SizedBox(height: 24),

            
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSubmitting ? null : _showCompletionDialog,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2A9D8F),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
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
                    : const Text(
                        'MARK AS COMPLETE',
                        style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          fontFamily: 'Poppins',
                        ),
                      ),
              ),
            ),

            const SizedBox(height: 12),

            
            const Center(
              child: Text(
                'Resident will need to confirm before XP is awarded',
                style: TextStyle(
                  fontSize: 12,
                  fontFamily: 'Inter',
                  color: Colors.grey,
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