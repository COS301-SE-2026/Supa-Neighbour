import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb; //to avoid clash with usr model
import '../models/user_model.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:io';

class AuthService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  AuthService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
             // baseUrl: 'http://10.0.2.2:8080',
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 10),
              receiveTimeout: const Duration(seconds: 10),
              headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
              },
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

 
  Future<User> login(String email, String password) async {
    // sign in with fb
    final fb.UserCredential credential = await _firebaseAuth
        .signInWithEmailAndPassword(email: email, password: password);

    // get the id token of fb usr.
    final String? idToken =
        await credential.user?.getIdToken(false);

    if (idToken == null) {
      throw Exception('Failed to retrieve Firebase ID token.');
    }

    final Response<Map<String, dynamic>> res = await _dio.post(
      '/api/auth/login',
      options: Options(
        headers: {'Authorization': 'Bearer $idToken'},
      ),
    );

    if (res.statusCode == 200 && res.data != null) {
        await _saveUserSession(res.data!);
        return User.fromJson(res.data!);
      }

    throw Exception('Login failed: unexpected response from server.');
     on DioException catch (e) {
      if (e.type == DioExceptionType.connectionTimeout) {
        throw Exception('Connection timeout. Please check your internet.');
      } else if (e.type == DioExceptionType.connectionError) {
        throw Exception(
          'Cannot connect to server.\n'
          'Please check:\n'
          '• Backend is running\n'
          '• ADB reverse is set (adb reverse tcp:8080 tcp:8080)\n'
          '• Port 8080 is not blocked'
        );
      } else if (e.response?.statusCode == 401) {
        throw Exception('Invalid email or password.');
      } else {
        throw Exception('Login failed: ${e.message}');
      }
    }
  }

Future<User> loginWithToken(String idToken) async {
  try {
  final Response<Map<String, dynamic>> response = await _dio.post(
    '/api/auth/login',
    options: Options(
      headers: {'Authorization': 'Bearer $idToken'},
    ),
  );

  if (response.statusCode == 200 && response.data != null) {
        await _saveUserSession(response.data!);
    return User.fromJson(response.data!);
  }

  throw Exception('Auto-login failed: unexpected response from server.');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
}


  Future<void> logout() async {
    try{
      final String? idToken = await _firebaseAuth.currentUser?.getIdToken();

      if(idToken != null){
        await _dio.post(
          '/api/auth/logout',
          options: Options(headers: {'Authorization': 'Bearer $idToken'}),
        );
      }
      }catch(e){
        // Nothing here
      }
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('remember_me', false);
    await _firebaseAuth.signOut();
  }


  
  Future<User> register({
    required String idToken,
    required String firstName,
    required String lastName,
    required String password,
    required String phoneNumber,
    required String dateOfBirth, 
    required String gender,
    required String username,
    String userType = 'user',
    int addressId = 1,
    int badgeId = 1,
    int ratingId = 1,
  }) async {
    try {
      final Response<Map<String, dynamic>> response = await _dio.post(
        '/api/auth/register',
        options: Options(
          headers: {'Authorization': 'Bearer $idToken'},
        ),
        data: {
          'firstName': firstName,
          'lastName': lastName,
          'password': password,
          'phoneNumber': phoneNumber,
          'dateOfBirth': dateOfBirth,
          'gender': gender,
          'username': username,
          'userType': userType,
          'addressId': addressId,
          'badgeId': badgeId,
          'ratingId': ratingId,
        },
      );

      if (response.statusCode == 200 && response.data != null) {
        await _saveUserSession(response.data!);
        return User.fromJson(response.data!);
      }

      throw Exception('Registration failed: unexpected response from server.');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  Future<void> deleteAccount() async{
    final String? idToken = await  _firebaseAuth.currentUser?.getIdToken(false);

    if(idToken == null){
      throw Exception('No authenticated user to delete.');
    }

    final Response response = await _dio.delete(
      '/api/settings/me/user',
       options: Options(headers: {'Authorization': 'Bearer $idToken'}),
    );

    if(response.statusCode != 204){
      throw Exception('Delete account failed: unexpected respose from server');
    }


    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('remember_me', false);

  }

}
