import 'package:supa_neighbour/services/chat_service.dart';

class MockChatService implements IChatService {
  @override
  Future<List<Map<String, dynamic>>> getChatsByUserId(int userId) async {
    return [
      {
        'chatId': 1,
        'name': 'Sarah Johnson',
        'location': 'Hatfield',
        'lastMessage': 'Hey, thanks for helping with my plants!',
        'timestamp': '2:30 PM',
        'unreadCount': 3,
      },
      {
        'chatId': 2,
        'name': 'Mike Brown',
        'location': 'Brooklyn',
        'lastMessage': 'Can you help with my packages tomorrow?',
        'timestamp': 'Yesterday',
        'unreadCount': 0,
      },
      {
        'chatId': 3,
        'name': 'Lisa Wong',
        'location': 'Menlo Park',
        'lastMessage': 'Thanks for the update!',
        'timestamp': '2 days ago',
        'unreadCount': 1,
      },
    ];
  }

  @override
  Future<Map<String, dynamic>> getMessages(int chatId, {int page = 1, int limit = 50}) async {
    return {
      'messages': [
        {'senderID': 1, 'content': 'Hello!', 'timestamp': DateTime.now().toIso8601String()},
        {'senderID': 2, 'content': 'Hi there!', 'timestamp': DateTime.now().toIso8601String()},
      ],
    };
  }

  @override
  Future<Map<String, dynamic>> sendMessage(int chatId, int senderId, String content, {String type = 'text'}) async {
    return {
      'messageId': 1,
      'senderID': senderId,
      'content': content,
      'timestamp': DateTime.now().toIso8601String(),
    };
  }

  @override
  Future<void> markAsRead(int chatId, int userId) async {
    // Mock success
  }

  @override
  Future<Map<String, dynamic>> getOrCreateChatForTask(int taskId, String authToken) async {
    return {
      'chatId': 101,
      'taskId': taskId,
      'name': 'Mock Chat',
      'lastMessage': 'Hello from mock chat',
      'timestamp': DateTime.now().toIso8601String(),
      'participants': [1, 2],
    };
  }
}

class MockChatServiceError implements IChatService {
  @override
  Future<List<Map<String, dynamic>>> getChatsByUserId(int userId) async {
    throw Exception('Failed to load chats');
  }

  @override
  Future<Map<String, dynamic>> getMessages(int chatId, {int page = 1, int limit = 50}) async {
    throw Exception('Failed to load messages');
  }

  @override
  Future<Map<String, dynamic>> sendMessage(int chatId, int senderId, String content, {String type = 'text'}) async {
    throw Exception('Failed to send message');
  }

  @override
  Future<void> markAsRead(int chatId, int userId) async {
    throw Exception('Failed to mark as read');
  }

  @override
  Future<Map<String, dynamic>> getOrCreateChatForTask(int taskId, String authToken) async {
    throw Exception('Failed to open chat for task');
  }
}