import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/helper_profile_response.dart';

// INTERFACE (Contract)
abstract class IHelperProfileService {
  Future<HelperProfileResponse> getHelperProfile(int helperId);
  Future<HelperProfileResponse> getHelperProfileByUserId(int userId);
}

class HelperProfileService implements IHelperProfileService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  HelperProfileService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
             // baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
              headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
              },
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  @override
  Future<HelperProfileResponse> getHelperProfile(int helperId) async {
    try {
      final user = _firebaseAuth.currentUser;
      if (user == null) {
        throw Exception('User not authenticated');
        }
      final idToken = await user.getIdToken();
      if (idToken == null) {
        throw Exception('Failed to get Firebase token');
        }

      final response = await _dio.get(
        '/api/helpers/$helperId/profile',
        options: Options(
          headers: {
            'Authorization': 'Bearer $idToken',
          },
        ),
      );

      if (response.statusCode == 200 && response.data != null) {
        return HelperProfileResponse.fromJson(response.data);
      }
      throw Exception('Failed to load helper profile');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  @override
  Future<HelperProfileResponse> getHelperProfileByUserId(int userId) async {
    try {
      final user = _firebaseAuth.currentUser;
      if (user == null) {
        throw Exception('User not authenticated');
        }
      final idToken = await user.getIdToken();
      if (idToken == null) {
        throw Exception('Failed to get Firebase token');
        }

      final response = await _dio.get(
        '/api/helpers/by-user/$userId/profile',
        options: Options(
          headers: {
            'Authorization': 'Bearer $idToken'}),
      );

      if (response.statusCode == 200 && response.data != null) {
        return HelperProfileResponse.fromJson(response.data);
      }
      throw Exception('Failed to load helper profile');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }
}