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
  final int initialTab;
  
  const MyTasksScreen({super.key, this.initialTab = 0});

  @override
  State<MyTasksScreen> createState() => _MyTasksScreenState();
}

class _MyTasksScreenState extends State<MyTasksScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  List<Task> _postedTasks = [];
  List<Task> _acceptedTasks = [];
  List<Task> _availableTasks = [];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(
      length: 3,
      vsync: this,
      initialIndex: widget.initialTab,
    );
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
            Tab(text: 'Available', icon: Icon(Icons.explore, size: 20)),
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
          RefreshIndicator(
            onRefresh: () async => _refreshTasks(),
            child: _buildTaskList(_availableTasks, isRequesterView: false, isAvailableTab: true),
          ),
        ],
      ),
    );
  }

  Widget _buildTaskList(List<Task> tasks, {required bool isRequesterView, bool isAvailableTab = false}) {
    if (tasks.isEmpty) {
  IconData icon;
  String title;
  String subtitle;
  
  if (isAvailableTab) {
    icon = Icons.explore;
    title = 'No available tasks';
    subtitle = 'Check back later for new tasks from neighbours';
  } else if (isRequesterView) {
    icon = Icons.post_add;
    title = 'No tasks posted yet';
    subtitle = 'Create your first task by tapping the + button';
  } else {
    icon = Icons.assignment_turned_in;
    title = 'No accepted tasks';
    subtitle = 'Browse Available tasks and accept one';
  }

  return Center(
    child: Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(
          icon,
          size: 80,
          color: const Color(0xFF2A9D8F).withOpacity(0.3),
        ),
        const SizedBox(height: 16),
        Text(
          title,
          style: GoogleFonts.openSans(
            color: const Color(0xFF264653),
            fontSize: 16,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          subtitle,
          style: GoogleFonts.openSans(
            color: const Color(0xFF9CA3AF),
            fontSize: 14,
          ),
          textAlign: TextAlign.center,
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
           isAvailableTab: isAvailableTab,
        );
      },
    );
  }

Widget _buildTaskCard(Task task, {required bool isRequesterView, bool isAvailableTab = false}) {
  if (isAvailableTab) {
    return Dismissible(
      key: Key(task.id),
      direction: DismissDirection.horizontal,
      onDismissed: (direction) {
        if (direction == DismissDirection.endToStart) {
          //user can swipe left to Pass
          _passTask(task);
        } else if (direction == DismissDirection.startToEnd) {
          // or swipe right to Accept
          _acceptTask(task);
        }
      },
      background: Container(
        color: Colors.green,
        alignment: Alignment.centerLeft,
        padding: const EdgeInsets.only(left: 20),
        child: const Icon(Icons.check, color: Colors.white, size: 30),
      ),
      secondaryBackground: Container(
        color: Colors.red,
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 20),
        child: const Icon(Icons.close, color: Colors.white, size: 30),
      ),
      child: GestureDetector(
        onTap: () async {
          // Navigate to TaskDetailScreen with Accept/Pass buttons
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => TaskDetailScreen(
                task: task,
                isAvailableTab: true,
              ),
            ),
          );
          if (result == 'accept') {
            _acceptTask(task);
          } else if (result == 'pass') {
            _passTask(task);
          }
        },
        child: _buildTaskCardContent(task, isRequesterView, isAvailableTab),
      ),
    );
  }
  
  // For other tabs, use the original GestureDetector
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
    child: _buildTaskCardContent(task, isRequesterView, isAvailableTab),
  );
}

Widget _buildTaskCardContent(Task task, bool isRequesterView, bool isAvailableTab) {
  return Container(
    margin: const EdgeInsets.only(bottom: 12),
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
      color: Colors.white,
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
            color: const Color(0xFF2A9D8F).withOpacity(0.1),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Icon(
            _getCategoryIcon(task.category),
            color: const Color(0xFF2A9D8F),
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
                  color: const Color(0xFF264653),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  const Icon(Icons.category, size: 14, color: Color(0xFF2A9D8F)),
                  const SizedBox(width: 4),
                  Text(
                    task.category,
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF6B7280),
                      fontSize: 12,
                    ),
                  ),
                  const SizedBox(width: 12),
                  const Icon(Icons.access_time, size: 14, color: Color(0xFF2A9D8F)),
                  const SizedBox(width: 4),
                  Text(
                    '${task.date.day}/${task.date.month} · ${task.time.format(context)}',
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF6B7280),
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 6),
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                decoration: BoxDecoration(
                  color: _getStatusColor(task.status).withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  _getStatusDisplay(task.status, isRequesterView: isRequesterView),
                  style: GoogleFonts.openSans(
                    color: _getStatusColor(task.status),
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
                color: const Color(0xFFE9C46A),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '+${task.xpReward} XP',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF264653),
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
                    color: const Color(0xFF2A9D8F),
                    fontSize: 10,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
          ],
        ),
      ],
    ),
  );
}

  void _acceptTask(Task task) {
  final updatedTask = task.copyWith(
    status: 'assigned',
    helperId: 'currentUser',
  );
  
  setState(() {
    _availableTasks.removeWhere((t) => t.id == task.id);
    _acceptedTasks.add(updatedTask);
  });
    
    
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('You accepted "${task.title}"!'),
        backgroundColor: const Color(0xFF2A9D8F),
        duration: const Duration(seconds: 2),
      ),
    );
  }

  void _passTask(Task task) {
  setState(() {
    _availableTasks.removeWhere((t) => t.id == task.id);
  });
  
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: Text('You passed on "${task.title}"'),
      backgroundColor: const Color(0xFF9CA3AF),
      duration: const Duration(seconds: 2),
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