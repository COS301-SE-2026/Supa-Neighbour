import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors
import 'edit_task_screen.dart';

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
  // CHANGE: Update to use AppColors with context
  Color _getStatusColor(String status, BuildContext context) {
    switch (status) {
      case 'open':
        return AppColors.citrusYellow(context);
      case 'assigned':
        return AppColors.primaryTeal(context);
      case 'in_progress':
        return const Color(0xFF2196F3);
      case 'pending_approval':
        return const Color(0xFFFF9800);
      case 'completed':
        return AppColors.success(context);
      case 'cancelled':
        return AppColors.error(context);
      default:
        return AppColors.textGrey(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    final bool canEdit = widget.isRequesterView && widget.task.status == 'open';
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        // CHANGE: Use AppColors.background
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Details',
          style: GoogleFonts.poppins(
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          if (canEdit)
            IconButton(
              icon: Icon(
                Icons.edit,
                // CHANGE: Use AppColors.primaryTeal
                color: AppColors.primaryTeal(context),
              ),
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
                // CHANGE: Use _getStatusColor with context
                color: _getStatusColor(widget.task.status, context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                _getStatusDisplay(widget.task.status),
                style: GoogleFonts.openSans(
                  // CHANGE: Use _getStatusColor with context
                  color: _getStatusColor(widget.task.status, context),
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
                // CHANGE: Use AppColors.primaryTeal with alpha
                color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    _getCategoryIcon(widget.task.category),
                    size: 16,
                    // CHANGE: Use AppColors.primaryTeal
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    widget.task.category,
                    style: GoogleFonts.openSans(
                      // CHANGE: Use AppColors.primaryTeal
                      color: AppColors.primaryTeal(context),
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
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
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
                  // CHANGE: Use AppColors.primaryTeal with alpha
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.person,
                      // CHANGE: Use AppColors.primaryTeal
                      color: AppColors.primaryTeal(context),
                      size: 20,
                    ),
                    const SizedBox(width: 8),
                    Text(
                      widget.isRequesterView
                          ? 'Helper: ${widget.task.helperName}'
                          : 'Requester: ${widget.task.requesterName}',
                      style: GoogleFonts.openSans(
                        // CHANGE: Use AppColors.charcoal
                        color: AppColors.charcoal(context),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            const SizedBox(height: 16),
            
            // XP Reward Container
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                // CHANGE: Use AppColors.citrusYellow with alpha
                color: AppColors.citrusYellow(context).withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.stars,
                    // CHANGE: Use AppColors.citrusYellow
                    color: AppColors.citrusYellow(context),
                    size: 32,
                  ),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'XP Reward',
                        style: GoogleFonts.openSans(
                          // CHANGE: Use AppColors.charcoal
                          color: AppColors.charcoal(context),
                          fontSize: 12,
                        ),
                      ),
                      Text(
                        '+${widget.task.xpReward} XP',
                        style: GoogleFonts.poppins(
                          // CHANGE: Use AppColors.charcoal
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
            
            // Date and Time Container
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                // CHANGE: Use dynamic color based on theme
                color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
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
                        Icon(
                          Icons.calendar_today,
                          // CHANGE: Use AppColors.primaryTeal
                          color: AppColors.primaryTeal(context),
                          size: 20,
                        ),
                        const SizedBox(width: 12),
                        Text(
                          '${widget.task.date.day}/${widget.task.date.month}/${widget.task.date.year}',
                          style: GoogleFonts.openSans(
                            // CHANGE: Use AppColors.charcoal
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
                        Icon(
                          Icons.access_time,
                          // CHANGE: Use AppColors.primaryTeal
                          color: AppColors.primaryTeal(context),
                          size: 20,
                        ),
                        const SizedBox(width: 12),
                        Text(
                          widget.task.time.format(context),
                          style: GoogleFonts.openSans(
                            // CHANGE: Use AppColors.charcoal
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
            
            // Instructions Section
            Text(
              'Instructions',
              style: GoogleFonts.poppins(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                // CHANGE: Use AppColors.surfaceGrey
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                widget.task.instructions,
                style: GoogleFonts.openSans(
                  // CHANGE: Use AppColors.charcoal
                  color: AppColors.charcoal(context),
                  fontSize: 14,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 24),
            
            // Non-editable Message
            if (!canEdit && widget.isRequesterView &&
                widget.task.status != 'completed' &&
                widget.task.status != 'pending_approval')
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  // CHANGE: Use AppColors.surfaceGrey
                  color: AppColors.surfaceGrey(context),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.info_outline,
                      // CHANGE: Use AppColors.textGrey
                      color: AppColors.textGrey(context),
                      size: 20,
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        _getNonEditableMessage(widget.task.status),
                        style: GoogleFonts.openSans(
                          // CHANGE: Use AppColors.textGrey
                          color: AppColors.textGrey(context),
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