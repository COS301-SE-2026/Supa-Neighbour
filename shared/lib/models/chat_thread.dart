// shared/lib/models/chat_thread.dart


class ChatThread {
  final String name;
  final String location;
  final String lastMessage;
  final String timestamp;
  final int unreadCount;
  final int avatarColor; 

  final int chatId;
  final int otherUserId;
  final int taskId;

  const ChatThread({
    required this.chatId,
    required this.otherUserId,
    required this.taskId,
    required this.name,
    required this.location,
    required this.lastMessage,
    required this.timestamp,
    required this.unreadCount,
    required this.avatarColor,
  });

  factory ChatThread.fromJson(Map<String, dynamic> json) {
    return ChatThread(
      chatId: json['chatID'] as int,
      otherUserId: json['otherUserID'] as int,
      taskId: json['taskID'] as int,
      name: json['otherUsername'] as String? ?? 'Unknown',
      location: '', // yet to add
      lastMessage: json['lastMessage'] as String? ?? '',
      timestamp: _formatTimestamp(json['lastMessageTimestamp'] as String?),
      unreadCount: (json['unreadCount'] as int?) ?? 0,
      avatarColor: 0xFF2A9D8F,
    );
  }

  static String _formatTimestamp(String? raw) {
    if (raw == null || raw.isEmpty) return '';
    try {
      final dt = DateTime.parse(raw);
      final now = DateTime.now();
      if (dt.day == now.day) {
        final h = dt.hour % 12 == 0 ? 12 : dt.hour % 12;
        final m = dt.minute.toString().padLeft(2, '0');
        final ampm = dt.hour >= 12 ? 'PM' : 'AM';
        return '$h:$m $ampm';
      }
      return 'Yesterday';
    } catch (_) {
      return '';
    }
  }
}

class ChatMessage {
  final String text;
  final bool isMe;
  final String time;

  const ChatMessage({
    required this.text,
    required this.isMe,
    required this.time,
  });
}