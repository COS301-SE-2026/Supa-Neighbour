import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../widgets/bottom_nav_bar.dart';
import 'task_completion_page.dart';
import '../leaderboard/helper_profile_preview_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class TaskStartScreen extends ConsumerStatefulWidget {
  final Task task;

  const TaskStartScreen({
    super.key,
    required this.task,
  });

  @override
  ConsumerState<TaskStartScreen> createState() => _TaskStartScreenState();
}

class _TaskStartScreenState extends ConsumerState<TaskStartScreen> {
  bool _isStarting = false;

  Future<void> _startTask() async {
    setState(() => _isStarting = true);

    try {
      final taskService = ref.read(taskServiceProvider);
      await taskService.updateTask(
        taskId: int.parse(widget.task.id),
        status: 'in_progress',
      );

      if (!mounted) return;

      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => TaskCompletionPage(
            taskId: widget.task.id,
            taskTitle: widget.task.title,
            residentName: widget.task.requesterName ?? 'Requester',
            dueDate:
                '${widget.task.date.day}/${widget.task.date.month} · ${widget.task.time.format(context)}',
            xpReward: widget.task.xpReward,
          ),
        ),
      );
    } catch (e) {
      if (!mounted) return;
      setState(() => _isStarting = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            e.toString().replaceFirst('Exception: ', ''),
          ),
          backgroundColor: Colors.redAccent,
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
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Details',
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
                color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    Icons.person_add_alt_1,
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Ready to Start',
                    style: GoogleFonts.openSans(
                      color: AppColors.primaryTeal(context),
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
                      color: AppColors.primaryTeal(context),
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
            GestureDetector(
              onTap: () {
                final requesterId = int.tryParse(widget.task.createdBy);
                if (requesterId != null) {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => HelperProfilePreviewScreen(
                        helperId: requesterId,
                        taskId: widget.task.id,
                        showRequestButton: false,
                        isUserId: true,
                      ),
                    ),
                  );
                }
              },
              child: Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(Icons.person, color: AppColors.primaryTeal(context), size: 20),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        'Requester: ${widget.task.requesterName}',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                        ),
                      ),
                    ),
                    Icon(Icons.chevron_right, color: AppColors.primaryTeal(context), size: 20),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color:  AppColors.citrusYellow(context).withValues(alpha: 0.2),
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
                color: Theme.of(context).brightness == Brightness.dark 
                    ? AppColors.surfaceGrey(context) 
                    : Colors.white,
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
                        Icon(Icons.calendar_today, color: AppColors.primaryTeal(context), size: 20),
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
                        Icon(Icons.access_time, color: AppColors.primaryTeal(context), size: 20),
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
            SizedBox(
              width: double.infinity,
              child: CustomButton(
                text: 'Start Task',
                onTap: _isStarting ? null : _startTask,
                isLoading: _isStarting,
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

  IconData _getCategoryIcon(String category) {
    switch (category) {
      case 'Medical Assistance':
        return Icons.medical_services;
      case 'Pet Care':
        return Icons.pets;
      case 'Technology Support':
        return Icons.computer;
      case 'Transportation Support':
        return Icons.directions_car;
      case 'Home Repair':
        return Icons.home_repair_service;
      default:
        return Icons.assignment;
    }
  }
}