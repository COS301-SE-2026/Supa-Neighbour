// frontend/lib/services/admin_application_service.dart

import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared/shared.dart';

abstract class IAdminApplicationService {
  Future<AdminApplication> submitApplication(String justification);
  Future<List<AdminApplication>> getMyApplications();
  Future<List<AdminApplication>> getAllApplications({String? status});
}

class AdminApplicationService implements IAdminApplicationService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  AdminApplicationService({Dio? dio, fb.FirebaseAuth? firebaseAuth}) : _dio = dio ?? Dio(BaseOptions(
    //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
    baseUrl: 'http://localhost:8080',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
    )),
  _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  @override
  Future<AdminApplication> submitApplication(String justification) async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken(false);
    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    try {
      final response = await _dio.post(
        '/api/admin/applications',
        data: {'justification': justification},
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );
      return AdminApplication.fromJson(response.data);
    } on DioException catch (e) {
      throw _handleError(e);
    }
  }

  @override
  Future<List<AdminApplication>> getMyApplications() async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken(false);
    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    try {
      final response = await _dio.get(
        '/api/admin/applications/me',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );
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