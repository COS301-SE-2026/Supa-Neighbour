import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors
import 'available_helpers_screen.dart';
import 'task_start_screen.dart';
import 'task_detail_screen.dart';
import 'task_completion_page.dart';
import 'task_awaiting_approval_screen.dart';
import 'task_approval_screen.dart';
import '../../models/auth_session.dart';
import '../../services/task_service.dart';


class MyTasksScreen extends StatefulWidget {
  const MyTasksScreen({super.key});

  @override
  State<MyTasksScreen> createState() => _MyTasksScreenState();
}

class _MyTasksScreenState extends State<MyTasksScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  List<Task> _postedTasks = [];
  List<Task> _acceptedTasks = [];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadAllTasks();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  

final TaskService _taskService = TaskService();

Future<void> _loadAllTasks() async {
  final currentUserId = int.tryParse(
    AuthSession.instance.currentUser?.id ?? '',
  );

  try {
    // posted and accepted
    final results = await Future.wait([
      currentUserId != null
          ? _taskService.getTasksByUserId(currentUserId)
          : Future.value(<Task>[]),
      _taskService.getMyHelperTasks(),
    ]);

    if (mounted) {
      setState(() {
        _postedTasks = results[0];
        _acceptedTasks = results[1];
      });
    }
  } on Exception {
    //fallback
    if (mounted) {
      final allTasks = Task.getMockTasks();
      final userId = AuthSession.instance.currentUser?.id ?? 'currentUser';
      setState(() {
        _postedTasks = allTasks
            .where((t) => t.createdBy == userId || t.createdBy == 'currentUser')
            .toList();
        _acceptedTasks = allTasks
            .where((t) => t.helperId == userId || t.helperId == 'currentUser')
            .toList();
      });
    }
  }
}


  void _refreshTasks() {
    _loadAllTasks();
  }

  String _getStatusDisplay(String status, {bool isRequesterView = false}) {
    switch (status) {
      case 'open':
        return 'Waiting for helper';
      case 'assigned':
        return isRequesterView ? 'Helper assigned' : 'Not started';
      case 'in_progress':
        return 'In progress';
      case 'pending_approval':
        return isRequesterView ? 'Awaiting your approval' : 'Awaiting approval';
      case 'completed':
        return 'Completed';
      case 'cancelled':
        return 'Cancelled';
      default:
        return status;
    }
  }

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
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        // CHANGE: Use AppColors.background
        backgroundColor: AppColors.background(context),
        elevation: 0,
        title: Text(
          'My Tasks',
          style: GoogleFonts.poppins(
            // CHANGE: Use AppColors.primaryTeal
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        bottom: TabBar(
          controller: _tabController,
          // CHANGE: Use AppColors.primaryTeal
          labelColor: AppColors.primaryTeal(context),
          // CHANGE: Use AppColors.textGrey
          unselectedLabelColor: AppColors.textGrey(context),
          // CHANGE: Use AppColors.primaryTeal
          indicatorColor: AppColors.primaryTeal(context),
          labelStyle: GoogleFonts.poppins(
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
          unselectedLabelStyle: GoogleFonts.poppins(
            fontSize: 14,
            fontWeight: FontWeight.w500,
          ),
          tabs: const [
            Tab(text: 'Posted', icon: Icon(Icons.post_add, size: 20)),
            Tab(text: 'Accepted', icon: Icon(Icons.check_circle, size: 20)),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          RefreshIndicator(
            onRefresh: () async => _refreshTasks(),
            child: _buildTaskList(_postedTasks, isRequesterView: true),
          ),
          RefreshIndicator(
            onRefresh: () async => _refreshTasks(),
            child: _buildTaskList(_acceptedTasks, isRequesterView: false),
          ),
        ],
      ),
    );
  }

  Widget _buildTaskList(List<Task> tasks, {required bool isRequesterView}) {
    if (tasks.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              isRequesterView ? Icons.post_add : Icons.assignment_turned_in,
              size: 80,
              // CHANGE: Use AppColors.primaryTeal with opacity
              color: AppColors.primaryTeal(context).withOpacity(0.3),
            ),
            const SizedBox(height: 16),
            Text(
              isRequesterView ? 'No tasks posted yet' : 'No accepted tasks',
              style: GoogleFonts.openSans(
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
                fontSize: 16,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              isRequesterView
                  ? 'Create your first task by tapping the + button'
                  : 'Browse Available Helpers and accept tasks',
              style: GoogleFonts.openSans(
                // CHANGE: Use AppColors.textGrey
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: tasks.length,
      itemBuilder: (context, index) {
        final task = tasks[index];
        return _buildTaskCard(
          task,
          isRequesterView: isRequesterView,
        );
      },
    );
  }

  Widget _buildTaskCard(Task task, {required bool isRequesterView}) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return GestureDetector(
      onTap: () async {
        // CASE 1: HELPER VIEW (Accepted Tab)
        if (!isRequesterView) {
          if (task.status == 'assigned') {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskStartScreen(
                  task: task,
                ),
              ),
            );
          } else if (task.status == 'in_progress') {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskCompletionPage(
                  taskId: task.id,
                  taskTitle: task.title,
                  residentName: task.requesterName ?? 'Requester',
                  dueDate: '${task.date.day}/${task.date.month} · ${task.time.format(context)}',
                  xpReward: task.xpReward,
                ),
              ),
            );
          } else if (task.status == 'pending_approval') {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskAwaitingApprovalScreen(
                  task: task,
                ),
              ),
            );
          } else {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskDetailScreen(
                  task: task,
                  onTaskUpdated: () => _refreshTasks(),
                ),
              ),
            );
          }
          _refreshTasks();
          return;
        }

        // CASE 2: REQUESTER VIEW (Posted Tab)
        if (isRequesterView) {
          if (task.status == 'open' || task.status == 'assigned') {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => AvailableHelpersScreen(
                  task: task,
                ),
              ),
            );
          } else if (task.status == 'pending_approval') {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskApprovalScreen(
                  task: task,
                ),
              ),
            );
          } else {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => TaskDetailScreen(
                  task: task,
                  onTaskUpdated: () => _refreshTasks(),
                ),
              ),
            );
          }
          _refreshTasks();
        }
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          // CHANGE: Use dynamic color based on theme
          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
          borderRadius: BorderRadius.circular(16),
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
            Container(
              width: 50,
              height: 50,
              decoration: BoxDecoration(
                // CHANGE: Use AppColors.primaryTeal with opacity
                color: AppColors.primaryTeal(context).withOpacity(0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(
                _getCategoryIcon(task.category),
                // CHANGE: Use AppColors.primaryTeal
                color: AppColors.primaryTeal(context),
                size: 28,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    task.title,
                    style: GoogleFonts.poppins(
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(
                        Icons.category,
                        size: 14,
                        // CHANGE: Use AppColors.primaryTeal
                        color: AppColors.primaryTeal(context),
                      ),
                      const SizedBox(width: 4),
                      Text(
                        task.category,
                        style: GoogleFonts.openSans(
                          // CHANGE: Use AppColors.textGrey
                          color: AppColors.textGrey(context),
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Icon(
                        Icons.access_time,
                        size: 14,
                        // CHANGE: Use AppColors.primaryTeal
                        color: AppColors.primaryTeal(context),
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '${task.date.day}/${task.date.month} · ${task.time.format(context)}',
                        style: GoogleFonts.openSans(
                          // CHANGE: Use AppColors.textGrey
                          color: AppColors.textGrey(context),
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                    decoration: BoxDecoration(
                      // CHANGE: Use _getStatusColor with context
                      color: _getStatusColor(task.status, context).withOpacity(0.1),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      _getStatusDisplay(task.status, isRequesterView: isRequesterView),
                      style: GoogleFonts.openSans(
                        // CHANGE: Use _getStatusColor with context
                        color: _getStatusColor(task.status, context),
                        fontSize: 10,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    // CHANGE: Use AppColors.citrusYellow
                    color: AppColors.citrusYellow(context),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '+${task.xpReward} XP',
                    style: GoogleFonts.openSans(
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                if (!isRequesterView && (task.status == 'assigned' || task.status == 'in_progress'))
                  Padding(
                    padding: const EdgeInsets.only(top: 8),
                    child: Text(
                      'Tap to complete',
                      style: GoogleFonts.openSans(
                        // CHANGE: Use AppColors.primaryTeal
                        color: AppColors.primaryTeal(context),
                        fontSize: 10,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
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