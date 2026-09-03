import 'package:flutter/material.dart';

enum NotificationCategory {
  newTask,
  newChat,
  newBulletin,
  systemUpdate,
  achievement,
  taskComplete,
  accountAlert, // NEW: warnings/suspensions/bans
}

/// Single source of truth for mapping a backend `type` string to a category.
/// Used by both the push-tap handler and the notification list, so they
/// can't drift apart like they had before.
class NotificationTypeMapper {
  static NotificationCategory categoryFromType(String? type) {
    switch (type) {
      case 'TASK_CREATED':
      case 'TASK_START':
        return NotificationCategory.newTask;
      case 'POST_CREATED':
      case 'POST_COMMENT':
        return NotificationCategory.newBulletin;
      case 'ACCOUNT_WARNING':
      case 'ACCOUNT_SUSPENDED':
      case 'ACCOUNT_BANNED':
        return NotificationCategory.accountAlert;
      default:
        return NotificationCategory.systemUpdate;
    }
  }
}

class AppNotification {
  final String id;
  final DateTime timestamp;
  final NotificationCategory category;
  final String title;
  final String body;
  final bool isRead;
  final String? type;     // raw backend type, e.g. 'TASK_START'
  final String? entityId; // raw backend entityId, e.g. taskId/postId/reportId as string

  AppNotification({
    required this.id,
    required this.timestamp,
    required this.category,
    required this.title,
    required this.body,
    this.isRead = false,
    this.type,
    this.entityId,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) {
    final type = json['type'] as String?;
    return AppNotification(
      id: json['notificationId'].toString(),
      timestamp: DateTime.parse(json['createdAt'] as String),
      category: NotificationTypeMapper.categoryFromType(type),
      title: json['title'] as String? ?? '',
      body: json['body'] as String? ?? '',
      isRead: json['isRead'] as bool? ?? false,
      type: type,
      entityId: json['entityId'] as String?,
    );
  }

  AppNotification copyWith({
    String? id,
    DateTime? timestamp,
    NotificationCategory? category,
    String? title,
    String? body,
    bool? isRead,
    String? type,
    String? entityId,
  }) {
    return AppNotification(
      id: id ?? this.id,
      timestamp: timestamp ?? this.timestamp,
      category: category ?? this.category,
      title: title ?? this.title,
      body: body ?? this.body,
      isRead: isRead ?? this.isRead,
      type: type ?? this.type,
      entityId: entityId ?? this.entityId,
    );
  }
}

class NotificationCategoryStyle {
  final String label;
  final Color color;
  final Color bgColor;
  final IconData icon;

  NotificationCategoryStyle({
    required this.label,
    required this.color,
    required this.bgColor,
    required this.icon,
  });

  factory NotificationCategoryStyle.fromCategory(NotificationCategory cat) {
    const tealColor = Color(0xFF2A9D8F);
    const tealBgColor = Color(0xFFE8F6F3);
    const alertColor = Color(0xFFD64545);
    const alertBgColor = Color(0xFFFBEAEA);

    switch (cat) {
      case NotificationCategory.newTask:
        return NotificationCategoryStyle(
          label: 'TASK', color: tealColor, bgColor: tealBgColor,
          icon: Icons.assignment_outlined,
        );
      case NotificationCategory.newChat:
        return NotificationCategoryStyle(
          label: 'CHAT', color: tealColor, bgColor: tealBgColor,
          icon: Icons.chat_bubble_outline,
        );
      case NotificationCategory.newBulletin:
        return NotificationCategoryStyle(
          label: 'BULLETIN', color: tealColor, bgColor: tealBgColor,
          icon: Icons.push_pin_outlined,
        );
      case NotificationCategory.systemUpdate:
        return NotificationCategoryStyle(
          label: 'SYSTEM', color: tealColor, bgColor: tealBgColor,
          icon: Icons.settings_outlined,
        );
      case NotificationCategory.achievement:
        return NotificationCategoryStyle(
          label: 'ACHIEVEMENT', color: tealColor, bgColor: tealBgColor,
          icon: Icons.emoji_events_outlined,
        );
      case NotificationCategory.taskComplete:
        return NotificationCategoryStyle(
          label: 'COMPLETE', color: tealColor, bgColor: tealBgColor,
          icon: Icons.check_circle_outline,
        );
      case NotificationCategory.accountAlert:
        return NotificationCategoryStyle(
          label: 'ACCOUNT', color: alertColor, bgColor: alertBgColor,
          icon: Icons.warning_amber_outlined,
        );
    }
  }
}