import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/mode_response.dart';

// INTERFACE (Contract)
abstract class ISettingsService {
  Future<ModeResponse> getMode();
  Future<ModeResponse> setMode(String mode);
}

class SettingsService implements ISettingsService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  SettingsService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
    : _dio = dio ??
            Dio(BaseOptions(
             // baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 10),
              receiveTimeout: const Duration(seconds: 10),
              )),
      _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<String?> _getIdToken(){
    return _firebaseAuth.currentUser?.getIdToken(false ) ?? Future.value(null);
  }

  @override
  Future<ModeResponse> getMode() async {
    final idToken = await _getIdToken();
    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    final Response<Map<String, dynamic>> res = await _dio.get(
      '/api/settings/users/mode',
      options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

    if(res.statusCode == 200 && res.data != null){
      return ModeResponse.fromJson(res.data!);
    }

    throw Exception('Failed to fetch mode: unexpected response form server');
  }

  @override
  Future<ModeResponse> setMode(String mode) async {
    final idToken = await _getIdToken();
    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    final Response<Map<String, dynamic>> res = await _dio.post(
      '/api/settings/users/mode',
      options : Options(headers: {'Authorization': 'Bearer $idToken'}),
      data :{'mode' : mode}
    );

    if(res.statusCode == 200 && res.data != null){
      return ModeResponse.fromJson(res.data!);
    }

    throw Exception('Failed to update mode: unexpected response from server');
  }
}