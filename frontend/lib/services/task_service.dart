import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/task_model.dart';
import 'package:shared_preferences/shared_preferences.dart';


/// responsible for all task-related API calls.
class TaskService {
  final Dio _dio;

  TaskService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net/',
              connectTimeout: const Duration(seconds: 10),// will update timeut if needed
              receiveTimeout: const Duration(seconds: 10),
            ));

  /// Gets a fresh fb idToken for auth headers.
  Future<String?> _getToken() async {
    return await fb.FirebaseAuth.instance.currentUser?.getIdToken();
  }

  /// GET /users/{userId}/tasks - tasks where the user is the dependent.
  Future<List<Task>> getTasksByUserId(int userId) async {
    try {
      final token = await _getToken();
      final Response<List<dynamic>> res = await _dio.get(
        '/users/$userId/tasks',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
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

  /// GET /api/helpers/me/tasks - tasks where the auth user is the helper.
  Future<List<Task>> getMyHelperTasks({
    String? statusFilter,
    int limit = 50,
    int offset = 0,
  }) async {
    try {
      final token = await _getToken();
      if (token == null) return [];

      final Response<Map<String, dynamic>> res = await _dio.get(
        '/api/helpers/me/tasks',
        queryParameters: {
          if (statusFilter != null) 'statusFilter': statusFilter,
          'limit': limit,
          'offset': offset,
        },
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (res.statusCode == 200 && res.data != null) {
        final taskList = res.data!['tasks'] as List<dynamic>? ?? [];
        return taskList
          .map((json) => Task.fromHelperTaskJson(json as Map<String, dynamic>))
          .toList();
      }
      return [];
    } on DioException catch (e) {
      throw Exception("Couldn't load helper tasks: ${e.message}");
    }
  }

  /// POST /tasks/create
  Future<Task> createTask({
    required int dependentId,
    required int taskTypeId,
    required DateTime startDate,
    required bool isImmediate,
    required bool needsSpecialist,
  }) async {
    try {
      final token = await _getToken();
      final Response<Map<String, dynamic>> res = await _dio.post(
        '/tasks/create',
        data: {
          'dependentId': dependentId,
          'taskTypeId': taskTypeId,
          'startDate': startDate.toIso8601String().split('T').first,
          'isImmediate': isImmediate,
          'needsSpecialist': needsSpecialist,
        },
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      return Task.fromJson(res.data!);
    } on DioException catch (e) {
      throw Exception("Couldn't create task: ${e.message}");
    }
  }

  /// PUT /tasks/{taskId}
  Future<Task> updateTask({
    required int taskId,
    int? taskTypeId,
    DateTime? startDate,
    String? adminReview,
    String? status,
  }) async {
    try {
      final token = await _getToken();
      final Map<String, dynamic> body = {};
      if (taskTypeId != null) body['taskTypeId'] = taskTypeId;
      if (startDate != null) {
        body['startDate'] = startDate.toIso8601String().split('T').first;
      }
      if (adminReview != null) body['adminReview'] = adminReview;
      if (status != null) body['status'] = status;

      final Response<Map<String, dynamic>> res = await _dio.put(
        '/tasks/$taskId',
        data: body,
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      return Task.fromJson(res.data!);
    } on DioException catch (e) {
      throw Exception("Couldn't update task: ${e.message}");
    }
  }

  /// DELETE /tasks/{taskId}
  Future<void> deleteTask(int taskId) async {
    try {
      final token = await _getToken();
      await _dio.delete(
        '/tasks/$taskId',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't delete task: ${e.message}");
    }
  }

  /// GET /api/users/{id} - fetch a user's profile by id.
  Future<Map<String, dynamic>> getUserById(int userId) async {
    try {
      final token = await _getToken();
      final Response<Map<String, dynamic>> res = await _dio.get(
        '/api/users/$userId',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      return res.data!;
    } on DioException catch (e) {
      throw Exception("Couldn't load user: ${e.message}");
    }
  }

  
  


Future<int?> getDependentIdForUser(int userId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final stored = prefs.getInt('current_dependent_id');
      if (stored != null) return stored;
      return null;
    } catch (_) {
      return null;
    }
  }

  Future<int?> getHelperIdForUser(int userId) async {
    try {
      final token = await _getToken();
      final Response<List<dynamic>> res = await _dio.get(
        '/api/helpers',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      if (res.statusCode == 200 && res.data != null) {
        for (final h in res.data!) {
          final user = h['userid'];
          final uid = user is Map ? user['userid'] ?? user['userId'] : null;
          if (uid == userId) {
            return h['helperid'] as int?;
          }
        }
      }
      return null;
    } on DioException {
      return null;
    }
  }

  Future<List<Map<String, dynamic>>> getInvitationsForHelper(int helperId) async {
    try {
      final token = await _getToken();
     final Response<dynamic> res = await _dio.get(
        '/api/task-invitations', 
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      if (res.statusCode == 200 && res.data != null) {
      final list = res.data as List<dynamic>;
        return list
            .where((inv) {
              final h = inv['helperid'];
              final hId = h is Map ? (h['helperid'] ?? h['helperId']) : null;
              return hId == helperId && inv['status'] == null;
            })
            .map((inv) => inv as Map<String, dynamic>)
            .toList();
      }
      return [];
    } on DioException catch (e) {
      throw Exception("Couldn't load invitations: ${e.message}");
    }
  }

  Future<void> acceptTaskInvitation(int taskId) async {
    try {
      final token = await _getToken();
      await _dio.post(
        '/api/task-invitations/$taskId/accept',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't accept task: ${e.message}");
    }
  }

  
Future<void> declineTaskInvitation(int taskId) async {
   try {
      final token = await _getToken();
      await _dio.post(
        '/api/task-invitations/$taskId/decline',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't decline task: ${e.message}");
    }
  }

  Future<List<Task>> getAvailableTasks(int currentUserId) async {
    try {
      final token = await _getToken();
      final Response<List<dynamic>> res = await _dio.get(
        '/tasks',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      if (res.statusCode == 200 && res.data != null) {
        return res.data!
            .map((json) => Task.fromJson(json as Map<String, dynamic>))
          .toList();
      }
      return [];
    } on DioException catch (e) {
      throw Exception("Couldn't load available tasks: ${e.message}");
    }
  }

  Future<void> matchHelpersForTask(int taskId) async {
    try {
      final token = await _getToken();
      await _dio.post(
        '/api/task-invitations/$taskId/match',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't match helpers for task: ${e.message}");
    }
  }



}