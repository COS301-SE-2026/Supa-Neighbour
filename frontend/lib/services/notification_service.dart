import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../screens/tasks/task_detail_screen.dart';
import '../screens/chat/bulletin_post_detail_screen.dart';
import '../models/task_model.dart';
import '../screens/notifications/notifications_screen.dart';
import '../models/notification_model.dart';

// Global navigator key - set in main.dart
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

class NotificationService {
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  bool _isInitialized = false;
  ProviderContainer? _providerContainer;

  /// Initialize the notification service
  Future<void> init({ProviderContainer? container}) async {
    if (_isInitialized) return;

    _providerContainer = container;

    // Request permissions
    await _requestPermissions();

    // Setup all listeners
    _setupForegroundListener();
    _setupBackgroundListener();
    await _setupTerminatedListener();

    _isInitialized = true;
  }

  /// Request notification permissions
  Future<void> _requestPermissions() async {
    final messaging = FirebaseMessaging.instance;
    await messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: true,
    );
  }

  /// Listen for notifications while app is in foreground
  void _setupForegroundListener() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      // Show in-app banner/snackbar
      _showInAppNotification(message);
      // Add to in-app notification list
      _addToNotificationList(message);
    });
  }

  /// Listen for notification taps when app is in background
  void _setupBackgroundListener() {
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      _handleNotificationTap(message.data);
    });
  }

  /// Handle notifications when app is terminated
  Future<void> _setupTerminatedListener() async {
    RemoteMessage? initialMessage = await FirebaseMessaging.instance.getInitialMessage();
    if (initialMessage != null) {
      // Delay to ensure navigator is ready
      WidgetsBinding.instance.addPostFrameCallback((_) {
        _handleNotificationTap(initialMessage.data);
      });
    }
  }

  /// Show in-app notification banner
  void _showInAppNotification(RemoteMessage message) {
    final notification = message.notification;
    if (notification == null) return;

    final context = navigatorKey.currentContext;
    if (context != null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                notification.title ?? 'Supa Neighbour',
                style: const TextStyle(fontWeight: FontWeight.bold),
              ),
              Text(notification.body ?? ''),
            ],
          ),
          duration: const Duration(seconds: 5),
          action: SnackBarAction(
            label: 'View',
            onPressed: () {
              _handleNotificationTap(message.data);
            },
          ),
        ),
      );
    }
  }

  /// Add notification to in-app notification list
  void _addToNotificationList(RemoteMessage message) {
    if (_providerContainer == null) return;

    final data = message.data;
    final notification = message.notification;

    final appNotification = AppNotification(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      timestamp: DateTime.now(),
      category: _getCategoryFromType(data['type'] ?? ''),
      title: notification?.title ?? 'Supa Neighbour',
      body: notification?.body ?? '',
      isRead: false,
    );

    // Add to notifications list
    _providerContainer!.read(notificationsProvider.notifier).addNotification(appNotification);
  }

  /// Handle notification tap - route to appropriate screen
  void _handleNotificationTap(Map<String, dynamic> data) {
    final type = data['type'] as String?;
    final entityId = data['entityId'] as String?;

    if (type == null || entityId == null) return;

    final context = navigatorKey.currentContext;
    if (context == null) return;

    switch (type) {
      case 'TASK_CREATED':
      case 'TASK_START':
        _navigateToTaskDetail(context, entityId);
        break;

      case 'POST_CREATED':
      case 'POST_COMMENT':
        _navigateToPostDetail(context, entityId);
        break;

      default:
        // Unknown type - ignore silently for future compatibility
        break;
    }
  }

  /// Navigate to task detail screen
  void _navigateToTaskDetail(BuildContext context, String taskId) {
    // Create a dummy task with the ID - the detail screen should fetch the actual data
    final task = Task(
      id: taskId,
      title: 'Loading...',
      category: 'General',
      date: DateTime.now(),
      time: TimeOfDay.now(),
      xpReward: 0,
      instructions: '',
      status: 'open',
      createdAt: DateTime.now(),
      createdBy: '',
    );

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => TaskDetailScreen(
          task: task,
          // The task will be fetched by the detail screen
        ),
      ),
    );
  }

  /// Navigate to post detail screen
  void _navigateToPostDetail(BuildContext context, String postId) {
    final id = int.tryParse(postId);
    if (id == null) return;

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (_) => BulletinPostDetailScreen(
          postId: id,
        ),
      ),
    );
  }

  /// Get the AppNotification category from FCM type
  NotificationCategory _getCategoryFromType(String type) {
    switch (type) {
      case 'TASK_CREATED':
      case 'TASK_START':
        return NotificationCategory.newTask;
      case 'POST_CREATED':
      case 'POST_COMMENT':
        return NotificationCategory.newBulletin;
      default:
        return NotificationCategory.systemUpdate;
    }
  }
}