import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import 'available_helpers_screen.dart';
import 'task_start_screen.dart';
import 'task_detail_screen.dart';
import 'task_completion_page.dart';
import 'task_awaiting_approval_screen.dart';
import 'task_approval_screen.dart';

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
  List<Task> _availableTasks = [];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _loadAllTasks();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadAllTasks() async {
    final allTasks = Task.getMockTasks();

    setState(() {
      _postedTasks = allTasks.where((task) => task.createdBy == 'currentUser').toList();
      _acceptedTasks = allTasks.where((task) => task.helperId == 'currentUser').toList();
      _availableTasks = allTasks.where((task) => task.createdBy != 'currentUser' && task.status == 'open').toList();
    });
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        title: Text(
          'My Tasks',
          style: GoogleFonts.poppins(
            color: const Color(0xFF2A9D8F),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        bottom: TabBar(
          controller: _tabController,
          labelColor: const Color(0xFF2A9D8F),
          unselectedLabelColor: const Color(0xFF9CA3AF),
          indicatorColor: const Color(0xFF2A9D8F),
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

        // CASE 3: AVAILABLE TAB (Viewing tasks from other users)
        if (isAvailableTab) {
          //Accept/Pass options
          _showAcceptPassDialog(task);
          return;
        }
      },
      child: Container(
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
      ),
    );
  }

  void _showAcceptPassDialog(Task task) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(
          'Help with this task?',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 18,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              task.title,
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 16,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Category: ${task.category}',
              style: GoogleFonts.openSans(
                color: const Color(0xFF6B7280),
                fontSize: 14,
              ),
            ),
            Text(
              'Date: ${task.date.day}/${task.date.month}',
              style: GoogleFonts.openSans(
                color: const Color(0xFF6B7280),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
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
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
            },
            child: Text(
              'Pass',
              style: GoogleFonts.openSans(
                color: const Color(0xFF9CA3AF),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              _acceptTask(task);
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2A9D8F),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Accept',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
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