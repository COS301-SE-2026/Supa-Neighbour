import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/task_model.dart';
import '../models/verification_model.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter/foundation.dart';


// INTERFACE (Contract)
abstract class ITaskService {
  Future<List<Task>> getTasksByUserId(int userId);
  Future<List<Task>> getMyHelperTasks({
    String? statusFilter,
    int limit = 50,
    int offset = 0,
  });

  Future<Task> createTask({
    required int dependentId,
    required int taskTypeId,
    required DateTime startDate,
    required bool isImmediate,
    required bool needsSpecialist,
    String? title,
    String? instructions,
    String? startTime,
    double? taskLat,
    double? taskLng,
  });

  Future<Task> updateTask({
    required int taskId,
    int? taskTypeId,
    DateTime? startDate,
    String? status,
    String? helperRatingId,
    String? dependentRatingId,
    List<String>? imageUrls,
  });

  Future<VerificationResult> submitCompletionEvidence({
    required int taskId,
    required XFile image,
    required DateTime capturedAt,
    double? lat,
    double? lng,
    double? accuracyM,
    String? deviceId,
  });

  Future<void> deleteTask(int taskId);
  Future<Map<String, dynamic>> getUserById(int userId);
  Future<int?> getDependentIdForUser(int userId);
  Future<int?> getHelperIdForUser(int userId);
  Future<List<Map<String, dynamic>>> getInvitationsForHelper(int helperId);
  Future<void> acceptTaskInvitation(int taskId);
  Future<void> declineTaskInvitation(int taskId);
  Future<List<Task>> getAvailableTasks(int currentUserId);
  Future<void> matchHelpersForTask(int taskId);
  Future<List<VerificationResult>> getCompletionVerifications(int taskId);

  Future<String?> uploadTaskImage(XFile imageFile);
  Future<void> saveTaskImages(int taskId, List<String> imageUrls);
  Future<Map<String, dynamic>> rateTask({
    required int taskId,
    required int rating,
    String? reviewSnippet,
  Future<void> saveTaskImages(int taskId, List<String> imageUrls, {required TaskImageType type});
  Future<void> submitCompletionDecision({
    required int taskId,
    required String decision, // 'CONFIRM' or 'DISPUTE'
    String? note,
  });
}

enum TaskImageType { reference, completion }

/// responsible for all task-related API calls.
class TaskService implements ITaskService {
  final Dio _dio;

  static const String _localBackendUrl = String.fromEnvironment(
    'LOCAL_BACKEND_URL',
    defaultValue: '',
  );

  TaskService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: 'http://localhost:8080',
              //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              connectTimeout: const Duration(seconds: 10),// will update timeut if needed
              receiveTimeout: const Duration(seconds: 10),
            ));

  /// Gets a fresh fb idToken for auth headers.
  Future<String?> _getToken() async {
    return await fb.FirebaseAuth.instance.currentUser?.getIdToken();
  }

  /// GET /users/{userId}/tasks - tasks where the user is the dependent.
  @override
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
  @override
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
  @override
  Future<Task> createTask({
    required int dependentId,
    required int taskTypeId,
    required DateTime startDate,
    required bool isImmediate,
    required bool needsSpecialist,
    String? title,
    String? instructions,
    String? startTime,
    double? taskLat,
    double? taskLng,
  }) async {
    try {
      final token = await _getToken();
      const path = '/tasks/create';
      final url = _localBackendUrl.isNotEmpty ? '$_localBackendUrl$path' : path;
      final Response<Map<String, dynamic>> res = await _dio.post(
        url,
        data: {
          'dependentId': dependentId,
          'taskTypeId': taskTypeId,
          'startDate': startDate.toIso8601String().split('T').first,
          if (startTime != null) 'startTime': startTime,
          'isImmediate': isImmediate,
          'needsSpecialist': needsSpecialist,
          'title': title,
          'instructions': instructions,
          if (taskLat != null) 'taskLat': taskLat,
          if (taskLng != null) 'taskLng': taskLng,
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
  @override
  Future<Task> updateTask({
    required int taskId,
    int? taskTypeId,
    DateTime? startDate,
    String? status,
    String? helperRatingId,      // was helperRatingReview
    String? dependentRatingId,
    List<String>? imageUrls,
  }) async {
    try {
      final token = await _getToken();
      final Map<String, dynamic> body = {};
      if (taskTypeId != null) body['taskTypeId'] = taskTypeId;
      if (startDate != null) {
        body['startDate'] = startDate.toIso8601String().split('T').first;
      }
      if (status != null) body['status'] = status;
      if (helperRatingId != null) body['helperRatingId'] = helperRatingId;
      if (dependentRatingId != null) body['dependentRatingId'] = dependentRatingId;

      if(imageUrls != null && imageUrls.isNotEmpty) {
        body['image'] = imageUrls.map((url) => {'imageUrl': url}).toList();
      }
  
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
  @override
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
  @override
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

  
  @override
  Future<int?> getDependentIdForUser(int userId) async {
    try {
  
      final prefs = await SharedPreferences.getInstance();
      final stored = prefs.getInt('current_dependent_id');
      if (stored != null) return stored;

      // fall back
      final token = await _getToken();
      final Response<List<dynamic>> res = await _dio.get(
        '/api/dependents',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      if (res.statusCode == 200 && res.data != null) {
        for (final d in res.data!) {
          final user = d['userid'];
          final uid = user is Map
              ? (user['userid'] ?? user['userId'])
              : d['userId'] ?? d['userid'];
          if (uid == userId) {
            final id = d['dependentId'] ?? d['dependent_id'] ?? d['dependentid'];
            if (id != null) {
              await prefs.setInt('current_dependent_id', id as int);
              return id ;
            }
          }
        }
      }
      return null;
    } catch (_) {
      return null;
    }
  }



  @override
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

 @override
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
  
  @override
  Future<void> acceptTaskInvitation(int taskId) async {
    try {
      final token = await _getToken();
      final path = '/api/task-invitations/$taskId/accept';
      final url = _localBackendUrl.isNotEmpty ? '$_localBackendUrl$path' : path;

      await _dio.post(
        url,
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't accept task: ${e.message}");
    }
  }

@override
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

 @override
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


 @override
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

  @override
  Future<String?> uploadTaskImage(XFile imageFile) async {
    try {
      final token = await _getToken();
      final bytes = await imageFile.readAsBytes();
      final formData = FormData.fromMap({
        'file': MultipartFile.fromBytes(
          bytes,
          filename: imageFile.name,
        ),
      });
      final res = await _dio.post(
        '/api/upload/task/image',
        data: formData,
        options: token != null ? Options(headers: {'Authorization': 'Bearer $token'}) : null,
      );
      return (res.data as Map<String, dynamic>)['imageUrl'] as String?;
    } on DioException catch (e) {
      throw Exception("Couldn't upload image: ${e.message}");
    }
  }

  /// POST /api/taskinvoices/{taskId}/images
  /// Saves a list of uploaded image URLs to the task in the database.
  @override
  Future<void> saveTaskImages(int taskId, List<String> imageUrls, {required TaskImageType type}) async {
    try {
      final token = await _getToken();
      final path = '/api/taskinvoices/$taskId/images';
      final url = _localBackendUrl.isNotEmpty ? '$_localBackendUrl$path' : path;
      await _dio.post(
        url,
        data: {'imageUrls': imageUrls},
        queryParameters: {'type': type.name.toUpperCase()},
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      throw Exception("Couldn't save task images: ${e.message}");
    }
  }

  @override
  Future<Map<String, dynamic>> rateTask({
    required int taskId,
    required int rating,
    String? reviewSnippet,
  }) async {
    try{
      final token = await _getToken();
      final Response<Map<String, dynamic>> res = await _dio.post(
        '/api/tasks/$taskId/rate',
        data: {
          'rating': rating,
          if(reviewSnippet != null && reviewSnippet.isNotEmpty)
            'reviewSnippet': reviewSnippet,
        },
        options: token != null ? Options(headers: {'Authorization': 'Bearer $token'}) : null,
      );
      return res.data!;
    }on DioException catch (e) {
      final data = e.response?.data;
      String message;
      if (data is Map && data['error'] != null) {
        message = data['error'].toString();
      } else if (data is String && data.isNotEmpty) {
        message = data;
      } else {
        message = e.message ?? 'Failed to submit rating';
      }
      throw Exception(message);
    }
  }

  Future<VerificationResult> submitCompletionEvidence({
    required int taskId,
    required XFile image,
    required DateTime capturedAt,
    double? lat,
    double? lng,
    double? accuracyM,
    String? deviceId,
  }) async {
    try{
      final token = await _getToken();
      final path = '/api/taskinvoices/$taskId/completion-evidence';
      final url = _localBackendUrl.isNotEmpty ? '$_localBackendUrl$path' : path;
      final bytes = await image.readAsBytes();

      final formData = FormData.fromMap({
        'file': MultipartFile.fromBytes(bytes, filename: image.name),
        'captureSource': 'CAMERA',
        'capturedAt': capturedAt.toUtc().toIso8601String(),
        if (lat != null) 'lat': lat,
        if (lng != null) 'lng': lng,
        if (accuracyM != null) 'accuracyM': accuracyM,
        if (deviceId != null) 'deviceId': deviceId,
      });

      final Response<Map<String, dynamic>> res = await _dio.post(
        url,
        data: formData,
        options: Options(
          headers: token != null ? {'Authorization': 'Bearer $token'} : null, 
          sendTimeout: const Duration(seconds: 60),
          receiveTimeout: const Duration(seconds: 60),
        ),
      );
      debugPrint('[completion-evidence] status=${res.statusCode} body=${res.data}');

      final result =  VerificationResult.fromJson(res.data!);
      debugPrint('[completion-evidence] '
      'verificationId=${result.verificationId} '
      'status=${result.status} '
      'score=${result.score} '
      'locationVerified=${result.locationVerified} '
      'distanceM=${result.distanceM} '
      'geofenceRadiusM=${result.geofenceRadiusM} '
      'reasons=${result.reasons} '
      'completionImageId=${result.completionImageId}');
      debugPrint('[completion-evidence] aiInsight=${result.aiInsight} '
      '=> AI ${result.aiInsight != null ? "WAS called" : "was NOT called / unavailable"}');
      return result;
    }on DioException catch (e){
      final data = e.response?.data;
      final message = data is Map && data['error'] != null ? data['error'].toString() : e.message;
      throw Exception(message ?? "Couldn't submit completion photo");
    }
  }

  @override
  Future<List<VerificationResult>> getCompletionVerifications(int taskId) async {
    try {
      final token = await _getToken();
      final Response<List<dynamic>> res = await _dio.get(
        '/api/taskinvoices/$taskId/completion-evidence',
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
      return (res.data ?? [])
          .map((j) => VerificationResult.fromJson(j as Map<String, dynamic>))
          .toList();
    } on DioException catch (e) {
      throw Exception("Couldn't load verification: ${e.message}");
    }
  }

  @override
  Future<void> submitCompletionDecision({
    required int taskId,
    required String decision,
    String? note,
  }) async {
    try {
      final token = await _getToken();
      await _dio.post(
        '/api/taskinvoices/$taskId/completion-decision',
        data: {
          'decision': decision,
          if (note != null) 'note': note,
        },
        options: token != null
            ? Options(headers: {'Authorization': 'Bearer $token'})
            : null,
      );
    } on DioException catch (e) {
      // Backend errors come back as {"error": "..."} (400/401/403/404/409)
      final data = e.response?.data;
      final message = data is Map && data['error'] != null
          ? data['error'].toString()
          : e.message;
      throw Exception(message ?? "Couldn't submit decision");
    }
  }


}