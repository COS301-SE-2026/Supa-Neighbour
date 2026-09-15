import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
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

  final FlutterLocalNotificationsPlugin _localNotifications =
      FlutterLocalNotificationsPlugin();

  static const String _channelId = 'supa_neighbour_channel';
  static const String _channelName = 'Supa Neighbour Notifications';
  static const String _channelDescription =
      'Notifications for tasks, posts, comments and account alerts';

  /// Initialize the notification service
  Future<void> init({ProviderContainer? container}) async {
    if (_isInitialized) return;

    _providerContainer = container;

    // 1) Init local notification plugin (must run before any show())
    await _initLocalNotifications();

    // 2) Ask Firebase for permission
    await _requestPermissions();

    // 3) Wire up the FCM listeners
    _setupForegroundListener();
    _setupBackgroundListener();
    await _setupTerminatedListener();

    _isInitialized = true;
    debugPrint('✅ NotificationService initialized');
  }

  /// Initialize the local notifications plugin + android channel
  Future<void> _initLocalNotifications() async {
    const AndroidInitializationSettings androidInit =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const DarwinInitializationSettings iosInit = DarwinInitializationSettings(
      requestAlertPermission: false,
      requestBadgePermission: false,
      requestSoundPermission: false,
    );

    const InitializationSettings settings = InitializationSettings(
      android: androidInit,
      iOS: iosInit,
    );

    await _localNotifications.initialize(
      settings,
      onDidReceiveNotificationResponse: (response) {
        final payload = response.payload;
        if (payload == null || payload.isEmpty) return;

        // payload format: "<type>|<entityId>"
        final parts = payload.split('|');
        if (parts.length != 2) return;
        _handleNotificationTap({'type': parts[0], 'entityId': parts[1]});
      },
    );

    // Android channel (must match channelId used by backend)
    const AndroidNotificationChannel channel = AndroidNotificationChannel(
      _channelId,
      _channelName,
      description: _channelDescription,
      importance: Importance.high,
    );

    await _localNotifications
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.createNotificationChannel(channel);

    // Android 13+ runtime permission
    await _localNotifications
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>()
        ?.requestNotificationsPermission();
  }

  /// Request Firebase messaging permissions
  Future<void> _requestPermissions() async {
    final messaging = FirebaseMessaging.instance;
    final settings = await messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
      provisional: true,
    );
    debugPrint(
        '📱 Notification permission status: ${settings.authorizationStatus}');
  }

  /// Listen for notifications while app is in foreground
  void _setupForegroundListener() {
    FirebaseMessaging.onMessage.listen((RemoteMessage message) async {
      debugPrint(
          '📨 Foreground notification received: ${message.notification?.title}');

      // 1) Show the system-tray notification via local notifications
      await _showSystemNotification(message);

      // 2) Show an in-app SnackBar for immediate feedback
      _showInAppSnackBar(message);

      // 3) Sync the in-app list with the backend.
      //    The backend already persisted this notification (NotificationPersistenceService),
      //    so refresh() will fetch it with its real ID.
      _refreshFromBackend();
    });
  }

  /// Listen for notification taps when app is in background
  void _setupBackgroundListener() {
    FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
      debugPrint(
          '📨 Background notification tapped: ${message.notification?.title}');
      _handleNotificationTap(message.data);
      // Also refresh the list in case the app had a stale view
      _refreshFromBackend();
    });
  }

  /// Handle notifications when app is terminated
  Future<void> _setupTerminatedListener() async {
    final RemoteMessage? initialMessage =
        await FirebaseMessaging.instance.getInitialMessage();
    if (initialMessage != null) {
      debugPrint(
          '📨 Terminated app opened from notification: ${initialMessage.notification?.title}');

      WidgetsBinding.instance.addPostFrameCallback((_) {
        _handleNotificationTap(initialMessage.data);
        _refreshFromBackend();
      });
    }
  }

  /// Show an Android/iOS system notification via the local plugin
  Future<void> _showSystemNotification(RemoteMessage message) async {
    final notification = message.notification;
    if (notification == null) return;

    final type = message.data['type'] ?? '';
    final entityId = message.data['entityId'] ?? '';
    final payload = '$type|$entityId';

    const AndroidNotificationDetails androidDetails =
        AndroidNotificationDetails(
      _channelId,
      _channelName,
      channelDescription: _channelDescription,
      importance: Importance.high,
      priority: Priority.high,
      icon: '@mipmap/ic_launcher',
      playSound: true,
      enableVibration: true,
    );

    const DarwinNotificationDetails iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const NotificationDetails details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _localNotifications.show(
      DateTime.now().millisecondsSinceEpoch.remainder(100000),
      notification.title ?? 'Supa Neighbour',
      notification.body ?? '',
      details,
      payload: payload,
    );
  }

  /// Show a lightweight in-app SnackBar (foreground only)
  void _showInAppSnackBar(RemoteMessage message) {
    final notification = message.notification;
    if (notification == null) return;

    final context = navigatorKey.currentContext;
    if (context == null) return;

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
        duration: const Duration(seconds: 4),
        action: SnackBarAction(
          label: 'View',
          onPressed: () => _handleNotificationTap(message.data),
        ),
      ),
    );
  }

  /// Refresh the in-app list from the backend (source of truth)
  void _refreshFromBackend() {
    if (_providerContainer == null) {
      debugPrint('⚠️ ProviderContainer is null, cannot refresh');
      return;
    }
    try {
      _providerContainer!.read(notificationsProvider.notifier).refresh();
      debugPrint('🔄 Notifications refreshed from backend');
    } catch (e) {
      debugPrint('⚠️ Failed to refresh notifications: $e');
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

    // For now, open the notifications list for every type.
    // TODO: deep-link straight into task/post detail using entityId.
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const NotificationsScreen()),
    );
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