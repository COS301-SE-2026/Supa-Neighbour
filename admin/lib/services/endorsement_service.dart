// admin/lib/services/endorsement_service.dart

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

/// Interface for the admin endorsement service.
/// Allows swapping between real and mock implementations.
abstract class IEndorsementService {
  /// Fetches the endorsement graph for the admin's own zone.
  /// Zone is resolved server-side from the admin's JWT.
  Future<EndorsementGraph> getZoneGraph({int depth = 3});

  /// Fetches all endorsements in the admin's zone.
  Future<List<Endorsement>> getZoneEndorsements({String? skillTag});

  /// Fetches flagged endorsement patterns (abuse detection).
  Future<List<SuspiciousEndorsement>> getSuspiciousPatterns();

  /// Fetches cluster insights for the admin's zone.
  Future<ZoneInsights> getZoneInsights();
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

  Exception _handleDioError(DioException e, String fallback) {
    final statusCode = e.response?.statusCode;
    switch (statusCode) {
      case 401:
        return EndorsementServiceException(
            'Session expired — please log in again.',
            statusCode: 401);
      case 403:
        return EndorsementServiceException(
            'Super admin access required.',
            statusCode: 403);
      case 404:
        return EndorsementServiceException('Not found.', statusCode: 404);
      default:
        return EndorsementServiceException(
          '$fallback: ${e.message ?? 'unknown error'}',
          statusCode: statusCode,
        );
    }
  }

  @override
  Future<EndorsementGraph> getZoneGraph({int depth = 3}) async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/admin/endorsements/zone/graph',
        queryParameters: {'depth': depth},
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (response.data is Map<String, dynamic>) {
        return EndorsementGraph.fromJson(
            response.data as Map<String, dynamic>);
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load zone graph');
    }
  }

  @override
  Future<List<Endorsement>> getZoneEndorsements({String? skillTag}) async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/admin/endorsements/zone',
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
  Future<List<SuspiciousEndorsement>> getSuspiciousPatterns() async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/admin/endorsements/suspicious',
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      final data = response.data;
      if (data is List) {
        return data
            .whereType<Map<String, dynamic>>()
            .map(SuspiciousEndorsement.fromJson)
            .toList();
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load suspicious patterns');
    }
  }

  @override
  Future<ZoneInsights> getZoneInsights() async {
    final token = await _requireToken();
    try {
      final response = await _dio.get(
        '/api/admin/endorsements/zone/insights',
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );
      if (response.data is Map<String, dynamic>) {
        return ZoneInsights.fromJson(response.data as Map<String, dynamic>);
      }
      throw EndorsementServiceException('Unexpected response shape.');
    } on DioException catch (e) {
      throw _handleDioError(e, 'Failed to load zone insights');
    }
  }
}