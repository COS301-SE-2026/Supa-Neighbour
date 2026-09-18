// admin/lib/services/admin_application_service.dart

import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared/shared.dart';

class AdminApplicationServiceException implements Exception {
  final String message;
  final int? statusCode;

  AdminApplicationServiceException(this.message, {this.statusCode});

  @override
  String toString() => message;
}


// Interface for the admin application service.
// Allows swapping between the real service and a mock for testing.
abstract class IAdminApplicationService {
  Future<List<AdminApplication>> getAllApplications({String? status});
  Future<AdminApplication> approveApplication(int applicationId);
  Future<AdminApplication> rejectApplication(
    int applicationId, {
    String? rejectionReason,
  });
}

class AdminApplicationService implements IAdminApplicationService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  AdminApplicationService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              //baseUrl:'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl : 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  /// Fetches all admin applications. Optional filter by status:
  /// 'Pending', 'Approved', 'Rejected'.
  @override
  Future<List<AdminApplication>> getAllApplications({String? status}) async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();
    if (idToken == null) {
      throw AdminApplicationServiceException('Not signed in.', statusCode: 401);
    }

    try {
      final response = await _dio.get(
        '/api/admin/applications',
        queryParameters: {
          if (status != null && status.isNotEmpty) 'status': status,
        },
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      final data = response.data;
      if (data is List) {
        return data
            .whereType<Map<String, dynamic>>()
            .map(AdminApplication.fromJson)
            .toList();
      }

      throw AdminApplicationServiceException(
          'Unexpected response shape from server.');
    } on DioException catch (e) {
      final statusCode = e.response?.statusCode;
      switch (statusCode) {
        case 401:
          throw AdminApplicationServiceException(
              'Session expired — please log in again.',
              statusCode: 401);
        case 403:
          throw AdminApplicationServiceException(
              'Super admin access required.',
              statusCode: 403);
        default:
          throw AdminApplicationServiceException(
            'Failed to load applications: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    }
  }

  /// Approves a pending application. Grants tier-1 admin on the backend.
  @override
  Future<AdminApplication> approveApplication(int applicationId) async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();
    if (idToken == null) {
      throw AdminApplicationServiceException('Not signed in.', statusCode: 401);
    }

    try {
      final response = await _dio.patch(
        '/api/admin/applications/$applicationId/approve',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.data is! Map<String, dynamic>) {
        throw AdminApplicationServiceException(
            'Unexpected response shape from server.');
      }

      return AdminApplication.fromJson(response.data as Map<String, dynamic>);
    } on DioException catch (e) {
      final statusCode = e.response?.statusCode;
      switch (statusCode) {
        case 401:
          throw AdminApplicationServiceException(
              'Session expired — please log in again.',
              statusCode: 401);
        case 403:
          throw AdminApplicationServiceException(
              'Super admin access required.',
              statusCode: 403);
        case 404:
          throw AdminApplicationServiceException(
              'Application not found.',
              statusCode: 404);
        case 409:
          throw AdminApplicationServiceException(
              'Application has already been reviewed.',
              statusCode: 409);
        default:
          throw AdminApplicationServiceException(
            'Failed to approve application: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    }
  }

  /// Rejects a pending application. Optional reason shown to the applicant.
  @override
  Future<AdminApplication> rejectApplication(
    int applicationId, {
    String? rejectionReason,
  }) async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();
    if (idToken == null) {
      throw AdminApplicationServiceException('Not signed in.', statusCode: 401);
    }

    try {
      final response = await _dio.patch(
        '/api/admin/applications/$applicationId/reject',
        data: {
          if (rejectionReason != null && rejectionReason.isNotEmpty)
            'rejectionReason': rejectionReason,
        },
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.data is! Map<String, dynamic>) {
        throw AdminApplicationServiceException(
            'Unexpected response shape from server.');
      }

      return AdminApplication.fromJson(response.data as Map<String, dynamic>);
    } on DioException catch (e) {
      final statusCode = e.response?.statusCode;
      switch (statusCode) {
        case 401:
          throw AdminApplicationServiceException(
              'Session expired — please log in again.',
              statusCode: 401);
        case 403:
          throw AdminApplicationServiceException(
              'Super admin access required.',
              statusCode: 403);
        case 404:
          throw AdminApplicationServiceException(
              'Application not found.',
              statusCode: 404);
        case 409:
          throw AdminApplicationServiceException(
              'Application has already been reviewed.',
              statusCode: 409);
        default:
          throw AdminApplicationServiceException(
            'Failed to reject application: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    }
  }
}