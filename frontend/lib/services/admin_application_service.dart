// frontend/lib/services/admin_application_service.dart

import 'package:dio/dio.dart';
import 'package:shared/shared.dart';

abstract class IAdminApplicationService {
  Future<AdminApplication> submitApplication(String justification);
  Future<List<AdminApplication>> getMyApplications();
  Future<List<AdminApplication>> getAllApplications({String? status});
  Future<AdminApplication> approveApplication(int applicationId);
  Future<AdminApplication> rejectApplication(int applicationId, {String? rejectionReason});
}

class AdminApplicationService implements IAdminApplicationService {
  final Dio _dio;

  AdminApplicationService({Dio? dio}) : _dio = dio ?? ApiClient().dio;

  @override
  Future<AdminApplication> submitApplication(String justification) async {
    try {
      final response = await _dio.post(
        '/api/admin/applications',
        data: {'justification': justification},
      );
      return AdminApplication.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  @override
  Future<List<AdminApplication>> getMyApplications() async {
    try {
      final response = await _dio.get('/api/admin/applications/me');
      final List<dynamic> data = response.data;
      return data.map((json) => AdminApplication.fromJson(json)).toList();
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  @override
  Future<List<AdminApplication>> getAllApplications({String? status}) async {
    try {
      final response = await _dio.get(
        '/api/admin/applications',
        queryParameters: status != null ? {'status': status} : null,
      );
      final List<dynamic> data = response.data;
      return data.map((json) => AdminApplication.fromJson(json)).toList();
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  @override
  Future<AdminApplication> approveApplication(int applicationId) async {
    try {
      final response = await _dio.patch(
        '/api/admin/applications/$applicationId/approve',
      );
      return AdminApplication.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  @override
  Future<AdminApplication> rejectApplication(
    int applicationId, {
    String? rejectionReason,
  }) async {
    try {
      final response = await _dio.patch(
        '/api/admin/applications/$applicationId/reject',
        data: rejectionReason != null ? {'rejectionReason': rejectionReason} : null,
      );
      return AdminApplication.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  Exception _handleError(DioException e) {
    if (e.response != null) {
      final data = e.response!.data;
      if (data is Map && data.containsKey('error')) {
        return Exception(data['error']);
      }
    }
    return Exception('Network error: ${e.message}');
  }
}