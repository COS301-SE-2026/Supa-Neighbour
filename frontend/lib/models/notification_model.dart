import 'package:flutter/material.dart';

enum NotificationCategory {
  newTask,
  newChat,
  newBulletin,
  systemUpdate,
  achievement,
  taskComplete,
}

class AppNotification {
  final String id;
  final DateTime timestamp;
  final NotificationCategory category;
  final String title;
  final String body;
  final bool isRead;

  AppNotification({
    required this.id,
    required this.timestamp,
    required this.category,
    required this.title,
    required this.body,
    this.isRead = false,
  });

  // Create a copy with updated fields
  AppNotification copyWith({
    String? id,
    DateTime? timestamp,
    NotificationCategory? category,
    String? title,
    String? body,
    bool? isRead,
  }) {
    return AppNotification(
      id: id ?? this.id,
      timestamp: timestamp ?? this.timestamp,
      category: category ?? this.category,
      title: title ?? this.title,
      body: body ?? this.body,
      isRead: isRead ?? this.isRead,
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
    
    switch (cat) {
      case NotificationCategory.newTask:
        return NotificationCategoryStyle(
          label: 'TASK',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.assignment_outlined,
        );
      case NotificationCategory.newChat:
        return NotificationCategoryStyle(
          label: 'CHAT',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.chat_bubble_outline,
        );
      case NotificationCategory.newBulletin:
        return NotificationCategoryStyle(
          label: 'BULLETIN',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.push_pin_outlined,
        );
      case NotificationCategory.systemUpdate:
        return NotificationCategoryStyle(
          label: 'SYSTEM',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.settings_outlined,
        );
      case NotificationCategory.achievement:
        return NotificationCategoryStyle(
          label: 'ACHIEVEMENT',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.emoji_events_outlined,
        );
      case NotificationCategory.taskComplete:
        return NotificationCategoryStyle(
          label: 'COMPLETE',
          color: tealColor,
          bgColor: tealBgColor,
          icon: Icons.check_circle_outline,
        );
    }
  }
}