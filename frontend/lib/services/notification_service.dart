import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
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
    
    debugPrint('✅ NotificationService initialized');
  }

  /// Request notification permissions
  Future<void> _requestPermissions() async {
    final messaging = FirebaseMessaging.instance;
    final settings = await messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: true,
    );
    
    debugPrint('📱 Notification permission status: ${settings.authorizationStatus}');
  }

  /// Listen for notifications while app is in foreground
  void _setupForegroundListener() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) {
      debugPrint('📨 Foreground notification received: ${message.notification?.title}');
      
      // Show in-app banner/snackbar
      _showInAppNotification(message);
      
      // Add to in-app notification list
      _addToNotificationList(message);
    });
  }

  /// Listen for notification taps when app is in background
  void _setupBackgroundListener() {
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      debugPrint('📨 Background notification tapped: ${message.notification?.title}');
      _handleNotificationTap(message.data);
    });
  }

  /// Handle notifications when app is terminated
  Future<void> _setupTerminatedListener() async {
    RemoteMessage? initialMessage = await FirebaseMessaging.instance.getInitialMessage();
    if (initialMessage != null) {
      debugPrint('📨 Terminated app opened from notification: ${initialMessage.notification?.title}');
      
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
    if (_providerContainer == null) {
      debugPrint('⚠️ ProviderContainer is null, cannot add notification');
      return;
    }

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
    try {
      _providerContainer!.read(notificationsProvider.notifier).addNotification(appNotification);
      debugPrint('✅ Notification added to in-app list');
    } catch (e) {
      debugPrint('❌ Failed to add notification: $e');
    }
  }

  /// Handle notification tap - route to appropriate screen
  void _handleNotificationTap(Map<String, dynamic> data) {
    final type = data['type'] as String?;
    final entityId = data['entityId'] as String?;

    debugPrint('🔔 Notification tapped: type=$type, entityId=$entityId');

    if (type == null || entityId == null) {
      debugPrint('⚠️ Missing type or entityId in notification data');
      return;
    }

    final context = navigatorKey.currentContext;
    if (context == null) {
      debugPrint('⚠️ Navigator key context is null');
      return;
    }

    // Show a snackbar indicating the notification was tapped
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('🔔 ${_getTypeDisplayName(type)} notification tapped'),
        backgroundColor: Colors.teal,
        duration: const Duration(seconds: 2),
      ),
    );

    switch (type) {
      case 'TASK_CREATED':
      case 'TASK_START':
        // Navigate to the notifications list for now.
        // TODO: once we can resolve entityId to a task, deep-link straight to task detail instead.
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const NotificationsScreen()),
        );
        break;

      case 'POST_CREATED':
      case 'POST_COMMENT':
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const NotificationsScreen()),
        );
        break;

      case 'ACCOUNT_WARNING':
      case 'ACCOUNT_SUSPENDED':
      case 'ACCOUNT_BANNED':
        // TODO: route to an account-status screen once it exists,
        // instead of the general notifications list.
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const NotificationsScreen()),
        );
        break;

      default:
        debugPrint('⚠️ Unknown notification type: $type');
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const NotificationsScreen()),
        );
        break;
    }
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

  /// Get display name for notification type
  String _getTypeDisplayName(String type) {
    switch (type) {
      case 'TASK_CREATED':
        return 'New Task';
      case 'TASK_START':
        return 'Task Started';
      case 'POST_CREATED':
        return 'New Post';
      case 'POST_COMMENT':
        return 'New Comment';
      default:
        return type;
    }
  }

  /// Get the FCM token (for debugging)
  Future<String?> getFcmToken() async {
    try {
      return await FirebaseMessaging.instance.getToken();
    } catch (e) {
      debugPrint('❌ Error getting FCM token: $e');
      return null;
    }
  }
}