import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/notification_model.dart';
import '../profile/settings_screen.dart';
import '../profile/achievements_screen.dart';

// Provider for notifications
final notificationsProvider = StateNotifierProvider<NotificationsNotifier, List<AppNotification>>((ref) {
  return NotificationsNotifier();
});

class NotificationsNotifier extends StateNotifier<List<AppNotification>> {
  NotificationsNotifier() : super(_generateMockNotifications());

  static List<AppNotification> _generateMockNotifications() {
    final now = DateTime.now();
    return [
      AppNotification(
        id: '1',
        timestamp: now.subtract(const Duration(minutes: 5)),
        category: NotificationCategory.newTask,
        title: 'Plant watering needed this weekend',
        body: 'Sarah needs someone to water her indoor plants while she\'s away for 3 days.',
        isRead: false,
      ),
      AppNotification(
        id: '2',
        timestamp: now.subtract(const Duration(hours: 2)),
        category: NotificationCategory.newChat,
        title: 'New message from Mike Johnson',
        body: '"Thanks for helping with the bins yesterday! Really appreciate it."',
        isRead: false,
      ),
      AppNotification(
        id: '3',
        timestamp: now.subtract(const Duration(hours: 4)),
        category: NotificationCategory.newBulletin,
        title: 'Community garden cleanup this Saturday',
        body: 'Join us at 10 AM for the monthly neighbourhood garden maintenance session. All tools provided!',
        isRead: false,
      ),
      AppNotification(
        id: '4',
        timestamp: now.subtract(const Duration(days: 1)),
        category: NotificationCategory.achievement,
        title: '🏆 New Achievement Unlocked!',
        body: 'You\'ve completed 10 tasks! You\'re now a "Community Helper" - keep up the great work!',
        isRead: true,
      ),
      AppNotification(
        id: '5',
        timestamp: now.subtract(const Duration(days: 1, hours: 3)),
        category: NotificationCategory.systemUpdate,
        title: 'App Updated to Version 2.4.0',
        body: 'New achievements system and improved chat notifications are now live. Check out what\'s new!',
        isRead: true,
      ),
      AppNotification(
        id: '6',
        timestamp: now.subtract(const Duration(days: 1, hours: 8)),
        category: NotificationCategory.taskComplete,
        title: 'Task Completed: Dog Walking',
        body: 'Great job! Your task "Walk Max the Golden Retriever" has been marked as completed. +50 XP earned!',
        isRead: true,
      ),
      AppNotification(
        id: '7',
        timestamp: now.subtract(const Duration(days: 2)),
        category: NotificationCategory.newTask,
        title: 'Grocery shopping assistance needed',
        body: 'Emma is looking for someone to help with grocery shopping on Thursday afternoon.',
        isRead: true,
      ),
      AppNotification(
        id: '8',
        timestamp: now.subtract(const Duration(days: 2, hours: 5)),
        category: NotificationCategory.newChat,
        title: 'New message from David Chen',
        body: '"Are you available to help with tutoring this weekend?"',
        isRead: true,
      ),
      AppNotification(
        id: '9',
        timestamp: now.subtract(const Duration(days: 3)),
        category: NotificationCategory.newBulletin,
        title: 'Neighbourhood watch meeting',
        body: 'Monthly neighbourhood watch meeting will be held at the community center at 7 PM.',
        isRead: true,
      ),
      AppNotification(
        id: '10',
        timestamp: now.subtract(const Duration(days: 4)),
        category: NotificationCategory.achievement,
        title: '🎉 5-Star Helper Rating',
        body: 'You\'ve received your 10th 5-star rating! You\'re one of the most trusted helpers in the community.',
        isRead: true,
      ),
      AppNotification(
        id: '11',
        timestamp: now.subtract(const Duration(days: 5)),
        category: NotificationCategory.taskComplete,
        title: 'Task Completed: Home Repair',
        body: 'Your task "Fix kitchen faucet" has been completed. Thank you for being a helpful neighbour!',
        isRead: true,
      ),
      AppNotification(
        id: '12',
        timestamp: now.subtract(const Duration(days: 6)),
        category: NotificationCategory.systemUpdate,
        title: 'New Features Available',
        body: 'You can now add photos to task descriptions and track your achievements in the profile section.',
        isRead: true,
      ),
      AppNotification(
        id: '13',
        timestamp: now.subtract(const Duration(days: 7)),
        category: NotificationCategory.newTask,
        title: 'Pet sitting needed for 2 days',
        body: 'Looking for a responsible helper to take care of a friendly cat for the weekend.',
        isRead: true,
      ),
      AppNotification(
        id: '14',
        timestamp: now.subtract(const Duration(days: 8)),
        category: NotificationCategory.newChat,
        title: 'New message from Lisa Park',
        body: '"Thank you so much for helping with my plants! They look great."',
        isRead: true,
      ),
    ];
  }

  void markAsRead(String id) {
    state = state.map((notification) {
      if (notification.id == id) {
        return notification.copyWith(isRead: true);
      }
      return notification;
    }).toList();
  }

  void markAllAsRead() {
    state = state.map((notification) {
      return notification.copyWith(isRead: true);
    }).toList();
  }

  void deleteNotification(String id) {
    state = state.where((notification) => notification.id != id).toList();
  }

  void clearAll() {
    state = [];
  }

  /// Add a new notification to the list (used for push notifications)
  void addNotification(AppNotification notification) {
    state = [notification, ...state];
  }
}

class NotificationsScreen extends ConsumerStatefulWidget {
  final Function(int)? onNotificationTap;

  const NotificationsScreen({
    super.key,
    this.onNotificationTap,
  });

  @override
  ConsumerState<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends ConsumerState<NotificationsScreen> {
  @override
  Widget build(BuildContext context) {
    final notifications = ref.watch(notificationsProvider);
    final unreadCount = notifications.where((n) => !n.isRead).length;

    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.primaryTeal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Notifications',
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          if (unreadCount > 0)
            TextButton(
              onPressed: () {
                ref.read(notificationsProvider.notifier).markAllAsRead();
              },
              child: Text(
                'Mark all read',
                style: GoogleFonts.openSans(
                  color: AppColors.primaryTeal(context),
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
        ],
      ),
      body: notifications.isEmpty
          ? _buildEmptyState(context)
          : Column(
              children: [
                if (unreadCount > 0)
                  _buildUnreadBanner(context, unreadCount),
                Expanded(
                  child: ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    itemCount: notifications.length,
                    itemBuilder: (context, index) {
                      final notif = notifications[index];
                      final style = NotificationCategoryStyle.fromCategory(notif.category);

                      // Show section header if date changes
                      final showHeader = index == 0 ||
                          _formatDate(notif.timestamp) !=
                              _formatDate(notifications[index - 1].timestamp);

                      return Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          if (showHeader)
                            Padding(
                              padding: const EdgeInsets.only(left: 4, bottom: 10, top: 8),
                              child: Text(
                                _formatDate(notif.timestamp).toUpperCase(),
                                style: GoogleFonts.openSans(
                                  fontSize: 12,
                                  fontWeight: FontWeight.w700,
                                  color: AppColors.textGrey(context),
                                  letterSpacing: 0.5,
                                ),
                              ),
                            ),
                          Dismissible(
                            key: Key(notif.id),
                            direction: DismissDirection.endToStart,
                            background: Container(
                              alignment: Alignment.centerRight,
                              padding: const EdgeInsets.only(right: 20),
                              decoration: BoxDecoration(
                                color: AppColors.error(context),
                                borderRadius: BorderRadius.circular(16),
                              ),
                              child: const Icon(
                                Icons.delete_outline,
                                color: Colors.white,
                                size: 28,
                              ),
                            ),
                            onDismissed: (_) {
                              ref.read(notificationsProvider.notifier).deleteNotification(notif.id);
                              ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(
                                  content: Text('Notification dismissed'),
                                  backgroundColor: AppColors.charcoal(context),
                                  duration: const Duration(seconds: 2),
                                ),
                              );
                            },
                            child: _NotificationCard(
                              notification: notif,
                              style: style,
                              onTap: () {
                                if (!notif.isRead) {
                                  ref.read(notificationsProvider.notifier).markAsRead(notif.id);
                                }
                                _handleNotificationTap(notif);
                              },
                            ),
                          ),
                          const SizedBox(height: 10),
                        ],
                      );
                    },
                  ),
                ),
              ],
            ),
    );
  }

  Widget _buildUnreadBanner(BuildContext context, int count) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(
        color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: AppColors.primaryTeal(context).withValues(alpha: 0.2),
        ),
      ),
      child: Row(
        children: [
          Icon(
            Icons.circle,
            color: AppColors.primaryTeal(context),
            size: 8,
          ),
          const SizedBox(width: 12),
          Text(
            '$count new notification${count > 1 ? 's' : ''}',
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyState(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.notifications_off_outlined,
            size: 80,
            color: AppColors.textGrey(context).withValues(alpha: 0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'No notifications yet',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 20,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'You\'re all caught up!',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }

  String _formatDate(DateTime dt) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final notifDay = DateTime(dt.year, dt.month, dt.day);

    if (notifDay == today) return 'Today';
    if (notifDay == today.subtract(const Duration(days: 1))) return 'Yesterday';
    return '${dt.day.toString().padLeft(2, '0')} ${_monthName(dt.month)}';
  }

  String _monthName(int m) => [
        'Jan',
        'Feb',
        'Mar',
        'Apr',
        'May',
        'Jun',
        'Jul',
        'Aug',
        'Sep',
        'Oct',
        'Nov',
        'Dec'
      ][m - 1];


  void _handleNotificationTap(AppNotification notification) {
    // Navigate based on notification type
    switch (notification.category) {
      case NotificationCategory.newTask:
        // Go back to home and switch to Tasks tab (index 1)
        widget.onNotificationTap?.call(1);
        Navigator.pop(context);
        break;

      case NotificationCategory.newChat:
        // Go back to home and switch to Chat tab (index 2)
        widget.onNotificationTap?.call(2);
        Navigator.pop(context);
        break;

      case NotificationCategory.newBulletin:
        // Go back to home and switch to Chat tab (index 2)
        widget.onNotificationTap?.call(2);
        Navigator.pop(context);
        break;

      case NotificationCategory.systemUpdate:
        // System update goes to settings - push settings on top
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const SettingsScreen(),
          ),
        );
        break;

      case NotificationCategory.achievement:
        // Achievements screen - push on top
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => const AchievementsScreen(),
          ),
        );
        break;

      case NotificationCategory.taskComplete:
        // Go back to home and switch to Tasks tab (index 1)
        widget.onNotificationTap?.call(1);
        Navigator.pop(context);
        break;

      // REMOVED the default case since all enum values are covered
    }
  }
}

class _NotificationCard extends StatelessWidget {
  final AppNotification notification;
  final NotificationCategoryStyle style;
  final VoidCallback onTap;

  const _NotificationCard({
    required this.notification,
    required this.style,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;

    return GestureDetector(
      onTap: onTap,
      child: Container(
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
          border: Border(
            left: BorderSide(
              color: notification.isRead
                  ? style.color.withValues(alpha: 0.3)
                  : style.color,
              width: 4,
            ),
          ),
        ),
        padding: const EdgeInsets.all(14),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Icon circle - all use the same teal color now
            Container(
              width: 44,
              height: 44,
              decoration: BoxDecoration(
                color: style.bgColor,
                shape: BoxShape.circle,
              ),
              child: Icon(
                style.icon,
                color: style.color,
                size: 22,
              ),
            ),
            const SizedBox(width: 12),
            // Content
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Category badge + time - all use teal now
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                            decoration: BoxDecoration(
                              color: style.bgColor,
                              borderRadius: BorderRadius.circular(6),
                            ),
                            child: Text(
                              style.label,
                              style: TextStyle(
                                fontSize: 11,
                                fontWeight: FontWeight.w700,
                                color: style.color,
                              ),
                            ),
                          ),
                          if (!notification.isRead) ...[
                            const SizedBox(width: 8),
                            Container(
                              width: 8,
                              height: 8,
                              decoration: BoxDecoration(
                                color: AppColors.primaryTeal(context),
                                shape: BoxShape.circle,
                              ),
                            ),
                          ],
                        ],
                      ),
                      Text(
                        _formatTime(notification.timestamp),
                        style: GoogleFonts.openSans(
                          fontSize: 11,
                          color: AppColors.textGrey(context),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  // Title
                  Text(
                    notification.title,
                    style: GoogleFonts.poppins(
                      fontSize: 14,
                      fontWeight: notification.isRead ? FontWeight.w500 : FontWeight.w600,
                      color: notification.isRead
                          ? AppColors.textGrey(context)
                          : AppColors.charcoal(context),
                      height: 1.35,
                    ),
                  ),
                  const SizedBox(height: 3),
                  // Body
                  Text(
                    notification.body,
                    style: GoogleFonts.openSans(
                      fontSize: 12,
                      color: AppColors.textGrey(context),
                      height: 1.3,
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatTime(DateTime dt) =>
      '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
}