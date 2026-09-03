import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared/models/user_model.dart';

class AdminAuthException  implements Exception{
  final String message;
  final int? statusCode;
  AdminAuthException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

class AdminAuthService{
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  AdminAuthService({Dio? dio, fb.FirebaseAuth? firebaseAuth}) 
    : _dio = dio ??
        Dio(BaseOptions(
          baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
          connectTimeout: const Duration(seconds: 30),
          receiveTimeout: const Duration(seconds: 30),
        )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<User> login(String email, String password) async{
    final fb.UserCredential credential = await _firebaseAuth.signInWithEmailAndPassword(email: email, password: password);

    final String? idToken = await credential.user?.getIdToken(false);

    if(idToken == null){
      throw AdminAuthException('Failed to retrieve Firebase ID token');
    }

    try{
      final Response response = await _dio.post(
        '/api/auth/admin/login',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if(response.statusCode == 200 && response.data != null){
        return User.fromJson(response.data as Map<String, dynamic>);
      }

      throw AdminAuthException('Unexpected response from server.');
    } on DioException catch(e){
      await _firebaseAuth.signOut();

      final status = e.response?.statusCode;
      switch(status){
        case 401:
          throw AdminAuthException('Invalid or expired token.', statusCode: 401);
        case 403:
          throw AdminAuthException('This account is not an admin', statusCode: 403);
        case 404:
          throw AdminAuthException('User not found', statusCode: 404);
        default:
          throw AdminAuthException('Login failed: ${e.message ?? 'Unknown error'}', statusCode: status);
      }
    }
  }

  Future<void> logout() async {
    await _firebaseAuth.signOut();
  }
}