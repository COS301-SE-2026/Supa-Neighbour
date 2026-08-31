import 'package:dio/dio.dart';

// INTERFACE
abstract class IChatService {
  Future<List<Map<String, dynamic>>> getChatsByUserId(int userId);
  Future<Map<String, dynamic>> getMessages(int chatId, {int page = 1, int limit = 50});
  Future<Map<String, dynamic>> sendMessage(int chatId, int senderId, String content, {String type = 'text'});
  Future<void> markAsRead(int chatId, int userId);
  Future<Map<String, dynamic>> getOrCreateChatForTask(int taskId, String authToken);
}

/// Service responsible for all chat-related calls.
class ChatService implements IChatService {
  final Dio _dio;

  ChatService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
              //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 10),
              receiveTimeout: const Duration(seconds: 10),
            ));

  /// Gets all chat threads for a user from GET /api/chats/{userId}.
  @override
  Future<List<Map<String, dynamic>>> getChatsByUserId(int userId) async {
    try {
      final Response<Map<String, dynamic>> res =
          await _dio.get('/api/chats/$userId');
      final data = res.data!;
      final chats = data['chats'] as List<dynamic>;
      return chats.map((c) => c as Map<String, dynamic>).toList();
    } on DioException catch (e) {
      throw Exception("Couldn't load chats: ${e.message}");
    }
  }

  /// Gets paginated messages for a chat from GET /api/chats/{chatId}/messages.
  @override
  Future<Map<String, dynamic>> getMessages(int chatId,
      {int page = 1, int limit = 50}) async {
    try {
      final Response<Map<String, dynamic>> res = await _dio.get(
        '/api/chats/$chatId/messages',
        queryParameters: {'page': page, 'limit': limit},
      );
      return res.data!;
    } on DioException catch (e) {
      throw Exception("Couldn't load messages: ${e.message}");
    }
  }

  /// Sends a message to a chat via POST /api/chats/{chatId}/messages.
  @override
  Future<Map<String, dynamic>> sendMessage(
      int chatId, int senderId, String content,
      {String type = 'text'}) async {
    try {
      final Response<Map<String, dynamic>> res =
          await _dio.post('/api/chats/$chatId/messages', data: {
        'senderID': senderId,
        'content': content,
        'type': type,
      });
      return res.data!;
    } on DioException catch (e) {
      throw Exception("Couldn't send message: ${e.message}");
    }
  }

  /// mark chat as read via /api/chats/$chatId/read.
  @override
  Future<void> markAsRead(int chatId, int userId) async {
    try {
      await _dio.put('/api/chats/$chatId/read', data: {'userID': userId});
    } on DioException catch (e) {
      throw Exception("Couldn't mark as read: ${e.message}");
    }
  }

  @override
  Future<Map<String, dynamic>> getOrCreateChatForTask(
    int taskId, String authToken
  ) async{
    try{
      final Response<Map<String, dynamic>> res = await _dio.post(
        '/api/chats/task/$taskId',
        options: Options(headers: {'Authorization': 'Bearer $authToken'}),
      );
      return res.data!;
    }on DioException catch(e){
      switch(e.response?.statusCode){
        case 409: 
          throw Exception("No helper has been assigned to this task yet.");
        case 403:
          throw Exception("You're not part of this task.");
        case 404: 
          throw Exception("Task not found");
        case 401:
          throw Exception("Your session has expired - please sign in again");
        default:
          throw Exception("Couldn't open chat: ${e.message}");
      }
    }
  }

}