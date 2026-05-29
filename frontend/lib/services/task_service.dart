import 'package:dio/dio.dart';
import '../models/task_model.dart';

/// responsible for all task-related API calls.
class TaskService {
  final Dio _dio;

  TaskService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 10),// will update timeut if needed
              receiveTimeout: const Duration(seconds: 10),
            ));

  /// Get all tasks for a given user from GET /users/{userId}/tasks.
  /// if the request fails return empty list
  Future<List<Task>> getTasksByUserId(int userId) async {
    try {
      final Response<List<dynamic>> res =
          await _dio.get('/users/$userId/tasks');

      if (res.statusCode == 200 && res.data != null) {
        return res.data!
            .map((json) => Task.fromJson(json as Map<String, dynamic>))
            .toList();
      }
      return [];
    } on DioException catch (e) {
      throw Exception("Couldn't load tasks: ${e.message}");
    }
  }
}
