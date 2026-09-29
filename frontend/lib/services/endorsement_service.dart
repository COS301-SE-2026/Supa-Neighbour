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
    required int endorseeId,
    required String skillTag,
    int? taskId,
  });
  Future<List<SkillTag>> getSkillTags();
}

class EndorsementService implements IEndorsementService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  EndorsementService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'http://localhost:8080',
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
        '/api/endorsements/me/summary',
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
      final response = await _dio.get(
        '/api/endorsements/me/graph',
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
    required int endorseeId,
    required String skillTag,
    int? taskId,
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
      if (data is Map<String, dynamic> && data['categories'] is List) {
        final tags = <SkillTag>[];
        for (final category in data['categories'] as List) {
          if (category is! Map<String, dynamic>) continue;
          final categoryName = category['category'] as String?;
          final skills = category['skills'];
          if (skills is! List) continue;
          for (final skill in skills) {
            if (skill is! Map<String, dynamic>) continue;
            tags.add(SkillTag.fromJson({
              ...skill,
              'category': categoryName,
            }));
          }
        }
        return tags;
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load skill tags');
    }
  }
}