import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/notification_model.dart';
import '../profile/settings_screen.dart';
import '../profile/achievements_screen.dart';
import '../../services/notification_api_service.dart';

// Provider for notifications
final notificationsApiServiceProvider = Provider<NotificationsApiService>((ref) {
  return NotificationsApiService();
});

final notificationsProvider =
    StateNotifierProvider<NotificationsNotifier, List<AppNotification>>((ref) {
  return NotificationsNotifier(ref.read(notificationsApiServiceProvider));
});

class NotificationsNotifier extends StateNotifier<List<AppNotification>> {
  final NotificationsApiService _apiService;

  NotificationsNotifier(this._apiService) : super ([]){
    _loadNotifications();
  }

  Future<void> _loadNotifications() async {
    try{
      final notifications = await _apiService.fetchNotifications();
      state = notifications;
    }catch(e){
      debugPrint('Failed to load notificatons : $e');
    }
  }

  Future<void> refresh() => _loadNotifications();

  void markAsRead(String id){
   state = [
      for (final n in state)
        if (n.id == id) n.copyWith(isRead: true) else n
    ];
    _apiService.markAsRead(id).catchError((e) {
      debugPrint('Failed to persist read state for $id: $e');
    });
  }

  void markAllAsRead(){
    for (final n in state.where((n) => !n.isRead)) {
      _apiService.markAsRead(n.id).catchError((e) {
        debugPrint('Failed to persist read state for ${n.id}: $e');
      });
    }
    state = [for (final n in state) n.copyWith(isRead: true)];
  }

  void deleteNotification(String id){
    state = state.where((n) => n.id != id).toList();
  }

  void clearAll(){
    state = [];
  }

  void addNotification(AppNotification  notification){
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

      case NotificationCategory.accountAlert:
        // TODO: route to a dedicated account-status screen once it exists.
        // For now, stay on the notifications list (already open) — no-op.
        break;
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