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

  /// Create a new task frm POST /tasks/create.
  /// will return created task on success else non
  Future<Task> createTask({
    required int dependentId,
    required int taskTypeId,
    required DateTime startDate,
    required bool isImmediate,
    required bool needsSpecialist,
  }) async {
    try {
      final Response<Map<String, dynamic>> res = await _dio.post('/tasks/create', data: {
        'dependentId': dependentId,
        'taskTypeId': taskTypeId,
        'startDate': startDate.toIso8601String().split('T').first,
        'isImmediate': isImmediate,
        'needsSpecialist': needsSpecialist,
      });

      return Task.fromJson(res.data!);
    } on DioException catch (e) {
      throw Exception(" Couldn't create task: ${e.message}");
    }
}


  /// Updates an exisiting task from PUT /tasks/{taskId}.
  /// should return the updated task if success
  Future<Task> updateTask({
    required int taskId,
    required int taskTypeId,
    required DateTime startDate,
    required String adminReview,
  }) async {
    try{
      final Response<Map<String, dynamic>> res = 
      await _dio.put('/tasks/$taskId', data: {
        'taskTypeId' : taskTypeId,
        'startDate': startDate.toIso8601String().split('T').first,
        'adminReview':adminReview,
      });

      return Task.fromJson(res.data!);
    } on DioException catch (e) {
      throw Exception("Couldn't update task: ${e.message}");
    }
  }

  /// Deletes a task via DELETE /tasks/{taskId}.
  Future<void> deleteTask(int taskId) async {
    try {
      await _dio.delete('/tasks/$taskId');
    } on DioException catch (e) {
      throw Exception("Couldn't delete task: ${e.message}");
    }
  }

  /// Gets a user by ID from GET /api/users/{id}.
Future<Map<String, dynamic>> getUserById(int userId) async {
  try {
    final Response<Map<String, dynamic>> res =
        await _dio.get('/api/users/$userId');
    return res.data!;

  } on DioException catch (e) {
    throw Exception("Couldn't load user: ${e.message}");
  }
}


}
