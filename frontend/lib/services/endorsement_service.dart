// frontend/lib/services/endorsement_service.dart

import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared/shared.dart';

class EndorsementServiceException implements Exception {
  final String message;
  final int? statusCode;

  EndorsementServiceException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

/// Interface for the endorsement service. Allows swapping between
/// the real service and a mock for testing.
abstract class IEndorsementService {
  Future<List<Endorsement>> getMyEndorsements({String? skillTag});
  Future<EndorsementSummary> getMySummary();
  Future<EndorsementGraph> getGraph({
    String? userId,
    int depth = 2,
    String? skillTag,
  });
  Future<List<TrustPath>> getTrustPaths({required String toUserId});
  Future<Endorsement> createEndorsement({
    required String endorseeId,
    required String skillTag,
    String? taskId,
  });
  Future<List<SkillTag>> getSkillTags();
}

class EndorsementService implements IEndorsementService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  EndorsementService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl:
                  'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<String> _requireToken() async {
    final token = await _firebaseAuth.currentUser?.getIdToken();
    if (token == null) {
      throw EndorsementServiceException('Not signed in.', statusCode: 401);
    }
    return token;
  }

  Exception _handleDioError(DioException e, String fallbackMessage) {
    final statusCode = e.response?.statusCode;
    switch (statusCode) {
      case 400:
        return EndorsementServiceException('Invalid request.',
            statusCode: 400);
      case 401:
        return EndorsementServiceException(
            'Session expired — please log in again.',
            statusCode: 401);
      case 403:
        return EndorsementServiceException(
            'Not authorised to perform this action.',
            statusCode: 403);
      case 404:
        return EndorsementServiceException('Not found.', statusCode: 404);
      case 409:
        return EndorsementServiceException(
            'This endorsement already exists.', statusCode: 409);
      default:
        return EndorsementServiceException(
          '$fallbackMessage: ${e.message ?? 'unknown error'}',
          statusCode: statusCode,
        );
    }
  }

  @override
  Future<List<Endorsement>> getMyEndorsements({String? skillTag}) async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/users/endorsements/me',
        queryParameters: {
          if (skillTag != null && skillTag.isNotEmpty) 'skillTag': skillTag,
        },
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      final data = response.data;
      if (data is List) {
        return data
            .whereType<Map<String, dynamic>>()
            .map(Endorsement.fromJson)
            .toList();
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load endorsements');
    }
  }

  @override
  Future<EndorsementSummary> getMySummary() async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/users/endorsement/me/summary',
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (response.data is Map<String, dynamic>) {
        return EndorsementSummary.fromJson(
            response.data as Map<String, dynamic>);
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load summary');
    }
  }

  @override
  Future<EndorsementGraph> getGraph({
    String? userId,
    int depth = 2,
    String? skillTag,
  }) async {
    final token = await _requireToken();
    try {
      final path = userId == null
          ? '/api/users/endorsement-graph'
          : '/api/users/$userId/endorsement-graph';

      final response = await _dio.get(
        path,
        queryParameters: {
          'depth': depth,
          if (skillTag != null && skillTag.isNotEmpty) 'skillTag': skillTag,
        },
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (response.data is Map<String, dynamic>) {
        return EndorsementGraph.fromJson(
            response.data as Map<String, dynamic>);
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load graph');
    }
  }

  @override
  Future<List<TrustPath>> getTrustPaths({required String toUserId}) async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/users/me/trust-paths',
        queryParameters: {'toUserId': toUserId},
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      final data = response.data;
      if (data is List) {
        return data
            .whereType<Map<String, dynamic>>()
            .map(TrustPath.fromJson)
            .toList();
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load trust paths');
    }
  }

  @override
  Future<Endorsement> createEndorsement({
    required String endorseeId,
    required String skillTag,
    String? taskId,
  }) async {
    final token = await _requireToken();
    try {
      final response = await _dio.post(
        '/api/endorsements',
        data: {
          'endorseeId': endorseeId,
          'skillTag': skillTag,
          if (taskId != null) 'taskId': taskId,
        },
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (response.data is Map<String, dynamic>) {
        return Endorsement.fromJson(response.data as Map<String, dynamic>);
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to create endorsement');
    }
  }

  @override
  Future<List<SkillTag>> getSkillTags() async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/endorsements/skills',
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      final data = response.data;
      if (data is List) {
        return data
            .whereType<Map<String, dynamic>>()
            .map(SkillTag.fromJson)
            .toList();
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load skill tags');
    }
  }
}