import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../components/custom_button.dart';
import '../../components/custom_field_input.dart';
import '../../constants/app_colors.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../../services/task_service.dart';



class TaskApprovalScreen extends StatefulWidget {
  final Task task;

  const TaskApprovalScreen({
    super.key,
    required this.task,
  });

  @override
  State<TaskApprovalScreen> createState() => _TaskApprovalScreenState();
}

class _TaskApprovalScreenState extends State<TaskApprovalScreen> {
  double _rating = 0;
  final TextEditingController _reviewController = TextEditingController();
  bool _isSubmitting = false;

  final TaskService _taskService = TaskService();

  @override
  void dispose() {
    _reviewController.dispose();
    super.dispose();
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
          'Approve Task',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
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
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppColors.citrusYellow(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    Icons.hourglass_top,
                    size: 16,
                    color: AppColors.citrusYellow(context),
                  ),
                  SizedBox(width: 8),
                  Text(
                    'Awaiting Your Approval',
                    style: GoogleFonts.openSans(
                      color: AppColors.citrusYellow(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    _getCategoryIcon(widget.task.category),
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    widget.task.category,
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Text(
              widget.task.title,
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: AppColors.charcoal(context).withValues(alpha: 0.05),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  Icon(Icons.person, color: AppColors.charcoal(context), size: 20),
                  const SizedBox(width: 8),
                  Text(
                    'Helper: ${widget.task.helperName}',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.citrusYellow(context).withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  Icon(Icons.stars, color: AppColors.citrusYellow(context), size: 32),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'XP Reward',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 12,
                        ),
                      ),
                      Text(
                        '+${widget.task.xpReward} XP',
                        style: GoogleFonts.poppins(
                          color: AppColors.charcoal(context),
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
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
              child: Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        Icon(Icons.calendar_today, color: AppColors.charcoal(context), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          '${widget.task.date.day}/${widget.task.date.month}/${widget.task.date.year}',
                          style: GoogleFonts.openSans(
                            color: AppColors.charcoal(context),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Icon(Icons.access_time, color: AppColors.charcoal(context), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          widget.task.time.format(context),
                          style: GoogleFonts.openSans(
                            color: AppColors.charcoal(context),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Instructions',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                widget.task.instructions,
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                  fontSize: 14,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 24),
            Divider(color: AppColors.surfaceGrey(context)),
            const SizedBox(height: 16),
            Text(
              'Completion Details',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.charcoal(context).withValues(alpha: 0.05),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Helper\'s Note',
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    widget.task.completionNote ?? 'No note provided',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            if (widget.task.completionPhotos != null && widget.task.completionPhotos!.isNotEmpty)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Completion Photos',
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  SizedBox(
                    height: 120,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: widget.task.completionPhotos!.length,
                      itemBuilder: (context, index) {
                        return Container(
                          width: 120,
                          height: 120,
                          margin: const EdgeInsets.only(right: 8),
                          decoration: BoxDecoration(
                            color: AppColors.surfaceGrey(context),
                            borderRadius: BorderRadius.circular(8),
                            image: DecorationImage(
                              image: NetworkImage(widget.task.completionPhotos![index]),
                              fit: BoxFit.cover,
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            const SizedBox(height: 24),
            Divider(color: AppColors.surfaceGrey(context)),
            const SizedBox(height: 16),
            Text(
              'Rate Helper',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),
            Row(
              children: List.generate(5, (index) {
                return GestureDetector(
                  onTap: () {
                    setState(() {
                      _rating = index + 1.0;
                    });
                  },
                  child: Icon(
                    index < _rating ? Icons.star : Icons.star_border,
                    color: AppColors.citrusYellow(context),
                    size: 36,
                  ),
                );
              }),
            ),
            SizedBox(height: 8),
            Text(
              _rating > 0 ? '${_rating.toStringAsFixed(1)} / 5.0' : 'Tap a star to rate',
              style: GoogleFonts.openSans(
                color: _rating > 0 ? AppColors.charcoal(context) : AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 16),
            CustomInputField(
              label: 'Review (optional)',
              hintText: 'Write a review for the helper...',
              controller: _reviewController,
              maxLines: 4,
            ),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: CustomButton(
                text: 'Approve & Rate',
                onTap: _rating > 0 ? () => _approveCompletion(context) : null,
                isLoading: _isSubmitting,
              ),
            ),
            const SizedBox(height: 32),
          ],
        ),
      ),
      bottomNavigationBar: BottomNavBar(
        currentIndex: 1,
        onTap: (_) {},
      ),
    );
  }

 

 Future<void> _approveCompletion(BuildContext context) async {
  final confirmed = await showDialog<bool>(
    context: context,
    barrierDismissible: false,
    builder: (dialogContext) => AlertDialog(
      title: const Text('Approve Task Completion?'),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
              'Confirming...will award XP to the helper and mark this task as complete.'),
          const SizedBox(height: 8),
          Text(
            'Rating: ${_rating.toStringAsFixed(1)} / 5.0',
            style: TextStyle(
              fontWeight: FontWeight.w600,
              color: AppColors.citrusYellow(context),
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(dialogContext, false),
          child: const Text('Cancel'),
        ),
        ElevatedButton(
          onPressed: () => Navigator.pop(dialogContext, true),
          style: ElevatedButton.styleFrom(
            backgroundColor: const Color(0xFF4CAF50),
          ),
          child: const Text('Approve'),
        ),
      ],
    ),
  );

  if (confirmed != true) return;

  setState(() => _isSubmitting = true);

  try {
    await _taskService.updateTask(
      taskId: int.parse(widget.task.id),
      status: 'completed',
      adminReview: _reviewController.text.isNotEmpty
          ? '${_rating.toStringAsFixed(1)}/5 — ${_reviewController.text}'
          : '${_rating.toStringAsFixed(1)}/5',
    );

    Task.updateTaskStatus(widget.task.id, 'completed');

    if (context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Task approved! XP awarded to helper.'),
          backgroundColor: Color(0xFF4CAF50),
        ),
      );
      Navigator.pop(context);
    }
  } on Exception catch (e) {
    if (context.mounted) {
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


  IconData _getCategoryIcon(String category) {
    switch (category) {
      case 'Plants':
        return Icons.eco;
      case 'Pets':
        return Icons.pets;
      case 'Bins':
        return Icons.delete;
      case 'Packages':
        return Icons.inventory;
      case 'Home Check-in':
        return Icons.home;
      case 'Pool Pump':
        return Icons.water;
      default:
        return Icons.assignment;
    }
  }
}