import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import 'edit_task_screen.dart';
import '../../components/custom_button.dart';
import '../../components/custom_field_input.dart';
import '../../constants/app_colors.dart';

class TaskDetailScreen extends StatefulWidget {
  final Task task;
  final VoidCallback? onTaskUpdated;
  final bool isRequesterView;

  const TaskDetailScreen({
    super.key,
    required this.task,
    this.onTaskUpdated,
    this.isRequesterView = true,
  });

  @override
  State<TaskDetailScreen> createState() => _TaskDetailScreenState();
}

class _TaskDetailScreenState extends State<TaskDetailScreen> {
  double _rating = 0;
  final TextEditingController _reviewController = TextEditingController();
  bool _isSubmitting = false;

  @override
  void dispose() {
    _reviewController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bool canEdit = widget.isRequesterView && widget.task.status == 'open';
    final bool showApproveButton = widget.isRequesterView && widget.task.status == 'pending_approval';

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
          'Task Details',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          if (canEdit)
            IconButton(
              icon: const Icon(Icons.edit, color: Color(0xFF2A9D8F)),
              onPressed: () async {
                final result = await Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => EditTaskScreen(task: widget.task),
                  ),
                );
                if (result == true && widget.onTaskUpdated != null) {
                  widget.onTaskUpdated!();
                }
              },
            ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Status Badge
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: _getStatusColor(widget.task.status).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                _getStatusDisplay(widget.task.status),
                style: GoogleFonts.openSans(
                  color: _getStatusColor(widget.task.status),
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Category Badge
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    _getCategoryIcon(widget.task.category),
                    size: 16,
                    color: const Color(0xFF2A9D8F),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    widget.task.category,
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF2A9D8F),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),

            // Task Title
            Text(
              widget.task.title,
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),

            // Helper/Requester Info
            if (widget.task.helperName != null && widget.task.helperName != 'You')
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: const Color(0xFF2A9D8F).withValues(alpha: 0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.person, color: Color(0xFF2A9D8F), size: 20),
                    const SizedBox(width: 8),
                    Text(
                      widget.isRequesterView
                          ? 'Helper: ${widget.task.helperName}'
                          : 'Requester: ${widget.task.requesterName}',
                      style: GoogleFonts.openSans(
                        color: const Color(0xFF264653),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            const SizedBox(height: 16),

            // XP Reward
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFE9C46A).withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  const Icon(Icons.stars, color: Color(0xFFE9C46A), size: 32),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'XP Reward',
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 12,
                        ),
                      ),
                      Text(
                        '+${widget.task.xpReward} XP',
                        style: GoogleFonts.poppins(
                          color: const Color(0xFF264653),
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

            // Date and Time
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
                        const Icon(Icons.calendar_today, color: Color(0xFF2A9D8F), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          '${widget.task.date.day}/${widget.task.date.month}/${widget.task.date.year}',
                          style: GoogleFonts.openSans(
                            color: const Color(0xFF264653),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        const Icon(Icons.access_time, color: Color(0xFF2A9D8F), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          widget.task.time.format(context),
                          style: GoogleFonts.openSans(
                            color: const Color(0xFF264653),
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

            // Instructions
            const Text(
              'Instructions',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: Color(0xFF264653),
              ),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFF5F5F5),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                widget.task.instructions,
                style: GoogleFonts.openSans(
                  color: const Color(0xFF264653),
                  fontSize: 14,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 24),

            // APPROVAL SECTION (only shown when pending_approval)
            if (showApproveButton)
              Column(
                children: [
                  // Helper's completion note
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: AppColors.primaryTeal.withValues(alpha: 0.05),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '📝 Helper\'s Note',
                          style: GoogleFonts.poppins(
                            color: AppColors.charcoal,
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          widget.task.completionNote ?? 'No note provided',
                          style: GoogleFonts.openSans(
                            color: AppColors.charcoal,
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Helper's photos (if any)
                  if (widget.task.completionPhotos != null && widget.task.completionPhotos!.isNotEmpty)
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '📷 Completion Photos',
                          style: GoogleFonts.poppins(
                            color: AppColors.charcoal,
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
                                  color: AppColors.surfaceGrey,
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
                        const SizedBox(height: 16),
                      ],
                    ),

                  // Rating section
                  Row(
                    children: [
                      Text(
                        'Rate Helper',
                        style: GoogleFonts.poppins(
                          color: AppColors.charcoal,
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
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
                          color: const Color(0xFFE9C46A),
                          size: 32,
                        ),
                      );
                    }),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    _rating > 0 ? '${_rating.toStringAsFixed(1)} / 5.0' : 'Tap a star to rate',
                    style: GoogleFonts.openSans(
                      color: _rating > 0 ? AppColors.primaryTeal : AppColors.textGrey,
                      fontSize: 12,
                    ),
                  ),
                  const SizedBox(height: 16),

                  // Review input
                  CustomInputField(
                    label: 'Review (optional)',
                    hintText: 'Write a review for the helper...',
                    controller: _reviewController,
                    maxLines: 4,
                  ),
                  const SizedBox(height: 16),

                  // Approve button
                  SizedBox(
                    width: double.infinity,
                    child: CustomButton(
                      text: 'Approve & Rate',
                      onTap: _rating > 0 ? () => _approveCompletion(context) : null,
                      isLoading: _isSubmitting,
                    ),
                  ),
                ],
              ),
            if (showApproveButton) const SizedBox(height: 16),

            // Info message for non-editable tasks
            if (!canEdit && widget.isRequesterView &&
                widget.task.status != 'completed' &&
                widget.task.status != 'pending_approval')
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: const Color(0xFFF5F5F5),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    const Icon(Icons.info_outline, color: Color(0xFF9CA3AF), size: 20),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        _getNonEditableMessage(widget.task.status),
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF6B7280),
                          fontSize: 12,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  void _approveCompletion(BuildContext context) async {
    final confirmed = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Approve Task Completion?'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Text('Confirming will award XP to the helper and mark this task as complete.'),
            const SizedBox(height: 8),
            Text(
              'Rating: ${_rating.toStringAsFixed(1)} / 5.0',
              style: const TextStyle(
                fontWeight: FontWeight.w600,
                color: Color(0xFFE9C46A),
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

    if (confirmed == true) {
      setState(() => _isSubmitting = true);

      // TODO: Call API to approve task with rating and review
      // await taskService.approveTask(widget.task.id, _rating, _reviewController.text);

      Task.updateTaskStatus(widget.task.id, 'completed');

      // TODO: Save rating and review to database

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Task approved! XP awarded to helper.'),
          backgroundColor: Color(0xFF4CAF50),
        ),
      );
      if (widget.onTaskUpdated != null) {
        widget.onTaskUpdated!();
      }
      Navigator.pop(context);
    }
  }

  String _getStatusDisplay(String status) {
    switch (status) {
      case 'open':
        return 'Waiting for helper';
      case 'assigned':
        return 'Helper assigned';
      case 'in_progress':
        return 'In progress';
      case 'pending_approval':
        return 'Awaiting your approval';
      case 'completed':
        return 'Completed';
      case 'cancelled':
        return 'Cancelled';
      default:
        return status;
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'open':
        return const Color(0xFFE9C46A);
      case 'assigned':
        return const Color(0xFF2A9D8F);
      case 'in_progress':
        return const Color(0xFF2196F3);
      case 'pending_approval':
        return const Color(0xFFFF9800);
      case 'completed':
        return const Color(0xFF4CAF50);
      case 'cancelled':
        return const Color(0xFFF44336);
      default:
        return const Color(0xFF9CA3AF);
    }
  }

  String _getNonEditableMessage(String status) {
    switch (status) {
      case 'assigned':
        return 'A helper has been assigned. You cannot edit this task anymore.';
      case 'in_progress':
        return 'A helper is working on this task. You cannot edit it.';
      case 'pending_approval':
        return 'Waiting for your approval. Review the completion proof.';
      default:
        return 'This task cannot be edited at this stage.';
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