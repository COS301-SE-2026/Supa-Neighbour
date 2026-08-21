import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:supa_neighbour/constants/app_colors.dart';
import '../help/help_menu_screen.dart';
import '../../models/auth_session.dart';
import '../../models/task_model.dart';
import '../../models/user_model.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../tasks/create_task_screen.dart';
import '../leaderboard/leaderboard_screen.dart';
import '../chat/inbox_screen.dart';
import '../tasks/my_tasks_screen.dart';
import '../profile/profile_screen.dart';
import '../tasks/task_detail_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';
import '../notifications/notifications_screen.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/services.dart';
import '../../models/notification_model.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;


class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const HomeContent(),
    const MyTasksScreen(),
    const InboxScreen(),
    const LeaderboardScreen(),
    const ProfileScreen(),
  ];

  // Method to change tab from outside
  void changeTab(int index) {
    setState(() {
      _currentIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      body: _screens[_currentIndex],
      bottomNavigationBar: BottomNavBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
      ),
    );
  }
}

class HomeContent extends ConsumerStatefulWidget {
  const HomeContent({super.key});

  @override
  ConsumerState<HomeContent> createState() => _HomeContentState();
}

class _HomeContentState extends ConsumerState<HomeContent> {
  List<Task> _nearbyTasks = [];
  List<Task> _availableTasks = [];
   
  User? _currentUser;
  double _trustScore = 0.0;
  bool _isLoadingStats = true;
  int _helpsGiven = 0;
  bool _isSendingTestNotification = false;

  int? get _currentUserId {
    final id = AuthSession.instance.currentUser?.id;
    return id != null ? int.tryParse(id) : null;
  }

  @override
  void initState() {
    super.initState();
    _currentUser = AuthSession.instance.currentUser;
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoadingStats = true);
    final userId = _currentUserId;
    if (userId == null) {
      setState(() => _isLoadingStats = false);
      return;
    }
    try {
      final taskService = ref.read(taskServiceProvider);
      final tasks = await taskService.getTasksByUserId(userId);
      final profileService = ref.read(profileServiceProvider); 
      final profile = await profileService.getMyProfile();
      if (!mounted) return;
      setState(() {
        _nearbyTasks = tasks;
        _helpsGiven = tasks.where((t) => t.status == 'completed').length;
        _trustScore = profile.trustScore ?? 0.0;
        _isLoadingStats = false;
      });
      final available = await taskService.getAvailableTasks(userId);
      if (mounted) setState(() => _availableTasks = available);
    } catch (_) {
      if (!mounted) return;
      setState(() {
        _nearbyTasks = [];
        _isLoadingStats = false;
      });
    }
  }

 Future<void> _loadNearbyTasks() async {
  await _loadData();
}



  String getGreeting() {
    final hour = DateTime.now().hour;
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  }

  // ============ DEBUG METHODS ============

  /// Show FCM token
  Future<void> _showFcmToken() async {
    try {
      final token = await FirebaseMessaging.instance.getToken();
      
      if (token == null) {
        _showInfoDialog(
          title: ' No Token',
          content: 'No FCM token found. Make sure you are logged in.',
        );
        return;
      }
      
      _showInfoDialog(
        title: '📱 FCM Token',
        content: token,
        isToken: true,
      );
    } catch (e) {
      _showInfoDialog(
        title: ' Error',
        content: 'Failed to get FCM token: $e',
      );
    }
  }

  void _showInfoDialog({
    required String title, 
    required String content,
    bool isToken = false,
  }) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(
          title,
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: SingleChildScrollView(
          child: isToken
              ? SelectableText(
                  content,
                  style: GoogleFonts.openSans(
                    fontSize: 13,
                    color: AppColors.charcoal(context),
                  ),
                )
              : Text(
                  content,
                  style: GoogleFonts.openSans(
                    fontSize: 14,
                    color: AppColors.charcoal(context),
                  ),
                ),
        ),
        actions: [
          if (isToken)
            TextButton(
              onPressed: () {
                Clipboard.setData(ClipboardData(text: content));
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Token copied to clipboard!'),
                    backgroundColor: Colors.green,
                  ),
                );
              },
              child: Text(
                'Copy',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal(context),
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Close',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// Send test notification via backend
  Future<void> _sendTestNotification() async {
    setState(() => _isSendingTestNotification = true);

    try {
      // Get FCM token
      final token = await FirebaseMessaging.instance.getToken();
      
      if (token == null) {
        _showInfoDialog(
          title: ' Error',
          content: 'No FCM token found. Please login again.',
        );
        setState(() => _isSendingTestNotification = false);
        return;
      }

      // Get Firebase ID token
      final user = fb.FirebaseAuth.instance.currentUser;
      if (user == null) {
        _showInfoDialog(
          title: ' Error',
          content: 'No Firebase user found. Please login again.',
        );
        setState(() => _isSendingTestNotification = false);
        return;
      }

      final idToken = await user.getIdToken();
      if (idToken == null) {
        _showInfoDialog(
          title: ' Error',
          content: 'Failed to get ID token.',
        );
        setState(() => _isSendingTestNotification = false);
        return;
      }

      // Send notification via backend
      final dio = Dio();
      dio.options.baseUrl = 'http://localhost:8080';
      dio.options.connectTimeout = const Duration(seconds: 10);
      dio.options.receiveTimeout = const Duration(seconds: 10);

      final response = await dio.post(
        '/api/test/notification',
        options: Options(
          headers: {
            'Authorization': 'Bearer $idToken',
            'Content-Type': 'application/json',
          },
        ),
        data: {
          'fcmToken': token,
          'title': ' Test Notification',
          'body': 'Supa Neighbour is working! 🎉',
          'type': 'TASK_CREATED',
          'entityId': '123',
        },
      );

      setState(() => _isSendingTestNotification = false);

      if (response.statusCode == 200 || response.statusCode == 201) {
        _showInfoDialog(
          title: ' Success!',
          content: 'Test notification sent!\n\n'
                   'Check your device:\n'
                   '• Foreground: SnackBar appears\n'
                   '• Background: System notification\n'
                   '• Terminated: System notification',
        );
        
        // Add to in-app list
        final now = DateTime.now();
        final testNotification = AppNotification(
          id: now.millisecondsSinceEpoch.toString(),
          timestamp: now,
          category: NotificationCategory.newTask,
          title: ' Test Notification',
          body: 'Supa Neighbour is working! 🎉',
          isRead: false,
        );
        ref.read(notificationsProvider.notifier).addNotification(testNotification);
        
      } else {
        _showInfoDialog(
          title: ' Error',
          content: 'Backend returned: ${response.statusCode}',
        );
      }
      
    } catch (e) {
      setState(() => _isSendingTestNotification = false);
      
      if (e.toString().contains('Connection refused')) {
        _showInfoDialog(
          title: ' Connection Error',
          content: 'Make sure:\n\n'
                   '1. Backend is running\n'
                   '2. ADB reverse is set:\n'
                   '   adb reverse tcp:8080 tcp:8080',
        );
      } else {
        _showInfoDialog(
          title: ' Error',
          content: 'Failed: $e',
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    // Get the parent HomeScreen state to call changeTab
    final homeScreenState = context.findAncestorStateOfType<_HomeScreenState>();

    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.info_outline, color: AppColors.primaryTeal(context)),
          onPressed: () {
            HelpMenuScreen.showHelpModal(context, 'home');
          },
        ),
        title: Text(
          'Supa Neighbour',
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.notifications_none, color: AppColors.primaryTeal(context)),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => NotificationsScreen(
                    onNotificationTap: (int tabIndex) {
                      // Call the changeTab method on the parent HomeScreen
                      homeScreenState?.changeTab(tabIndex);
                    },
                  ),
                ),
              );
            },
          ),
          //  DEBUG MENU
          PopupMenuButton<String>(
            icon: Icon(Icons.developer_mode, color: AppColors.primaryTeal(context)),
            onSelected: (value) {
              if (value == 'token') {
                _showFcmToken();
              } else if (value == 'test') {
                _sendTestNotification();
              }
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'token',
                child: Row(
                  children: [
                    Icon(Icons.key, size: 20),
                    SizedBox(width: 8),
                    Text('Show FCM Token'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'test',
                child: Row(
                  children: [
                    Icon(Icons.notifications_active, size: 20),
                    SizedBox(width: 8),
                    Text(' Test Notification'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
         await  _loadNearbyTasks();
          return Future.value();
        },
        child: Stack(
          children: [
            SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildWelcomeSection(),
                  const SizedBox(height: 24),
                  _buildStatsRow(),
                  const SizedBox(height: 24),
                  _buildNearbyTasksSection(context),
                  const SizedBox(height: 12),
                  _nearbyTasks.isEmpty && _availableTasks.isEmpty
                      ? _buildEmptyState()
                      : _buildNearbyTaskList(context),
                  const SizedBox(height: 80),
                ],
              ),
            ),
            // Loading overlay when sending test notification
            if (_isSendingTestNotification)
              Container(
                color: Colors.black.withOpacity(0.3),
                child: const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      CircularProgressIndicator(color: Colors.white),
                      SizedBox(height: 16),
                      Text(
                        'Sending test notification...',
                        style: TextStyle(color: Colors.white, fontSize: 16),
                      ),
                    ],
                  ),
                ),
              ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.push<dynamic>(
            context,
            MaterialPageRoute(builder: (context) => const CreateTaskScreen()),
          );
          if (!mounted) return;
          if (result != null) {
            _loadNearbyTasks();
          }
        },
        backgroundColor: AppColors.primaryTeal(context),
        child: const Icon(Icons.add, color: Colors.white),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }

  Widget _buildWelcomeSection() {
    final greeting = getGreeting();
    final userName = _currentUser?.fullName ?? 'Neighbour';
    
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            AppColors.surfaceGrey(context).withValues(alpha: 0.5),
            AppColors.citrusYellow(context).withValues(alpha: 0.1),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '$greeting,',
                  style: GoogleFonts.openSans(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                  ),
                ),
                Text(
                  userName,
                  style: GoogleFonts.poppins(
                    color: AppColors.primaryTeal(context),
                    fontSize: 24,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppColors.citrusYellow(context),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    _isLoadingStats
                        ? '⭐ -- Trust Score'
                        : '⭐ ${_trustScore.toStringAsFixed(1)} Trust Score',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
          ),
          Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              color: AppColors.primaryTeal(context).withValues(alpha: 0.2),
              shape: BoxShape.circle,
            ),
            child: Icon(
              Icons.person,
              color: AppColors.primaryTeal(context),
              size: 40,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Container(
      padding: const EdgeInsets.all(40),
      child: Column(
        children: [
          Icon(
            Icons.assignment_outlined,
            size: 80,
            color: AppColors.primaryTeal(context).withValues(alpha: 0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'No tasks yet',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Create your first task by tapping the + button',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
            textAlign: TextAlign.center,
          ),
        ],
      ),
    );
  }

  Widget _buildStatsRow() {
    final tasksPosted = _nearbyTasks.length;
    final activeCount = _nearbyTasks
        .where((t) => t.status == 'open' || t.status == 'assigned' || t.status == 'in_progress')
        .length;

    return Row(
      children: [
        _buildStatCard(_helpsGiven.toString(), 'Completed', const Color(0xFF2A9D8F)),
        const SizedBox(width: 12),
        _buildStatCard(tasksPosted.toString(), 'Tasks Posted', const Color(0xFFE9C46A)),
        const SizedBox(width: 12),
        _buildStatCard(activeCount.toString(), 'Active', const Color(0xFF69B578)),
      ],
    );
  }

  Widget _buildStatCard(String value, String label, Color color) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
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
        child: Column(
          children: [
            Text(
              value,
              style: GoogleFonts.poppins(
                color: color,
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNearbyTasksSection(BuildContext context) {
  return Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(
        'Available Nearby', 
        style: GoogleFonts.poppins(
          color: AppColors.charcoal(context),
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
      TextButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const MyTasksScreen(initialTab: 0),
            ),
          );
        },
        child: Text(
          'See All',
          style: GoogleFonts.openSans(
            color: AppColors.primaryTeal(context),
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    ],
  );
}

  Widget _buildNearbyTaskList(BuildContext context) {
    final displayTasks = _availableTasks.isNotEmpty ? _availableTasks : _nearbyTasks;
    return Column(
      children: displayTasks.take(5).map((task) {
        return _buildTaskCard(
          context: context,
          task: task,
          onTap: () async {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) => TaskDetailScreen(
                  task: task,
                  onTaskUpdated: () {
                    _loadNearbyTasks();
                  },
                ),
              ),
            );
            _loadNearbyTasks();
          },
        );
      }).toList(),
    );
  }

  Widget _buildTaskCard({
    required BuildContext context,
    required Task task,
    required VoidCallback onTap,
  }) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
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
                color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(
                _getCategoryIcon(task.category),
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
                      color: AppColors.charcoal(context),
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(Icons.access_time, size: 14, color: AppColors.primaryTeal(context)),
                      const SizedBox(width: 4),
                      Text(
                        '${task.date.day}/${task.date.month} · ${task.time.format(context)}',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: AppColors.citrusYellow(context),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '+${task.xpReward} XP',
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
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