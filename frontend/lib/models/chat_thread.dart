import 'package:flutter/material.dart';

class ChatThread {
  final String name;
  final String location;
  final String lastMessage;
  final String timestamp;
  final int unreadCount;
  final Color avatarColor;

  const ChatThread({  // Added const
    required this.name,
    required this.location,
    required this.lastMessage,
    required this.timestamp,
    required this.unreadCount,
    required this.avatarColor,
  });
}

class ChatMessage {
  final String text;
  final bool isMe;
  final String time;

  const ChatMessage({  // Added const
    required this.text,
    required this.isMe,
    required this.time,
  });
}