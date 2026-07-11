import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb; //to avoid clash with usr model
import '../models/user_model.dart';

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

    final Response<Map<String, dynamic>> response = await _dio.post(
      '/api/auth/login',
      options: Options(
        headers: {'Authorization': 'Bearer $idToken'},
      ),
    );

    if (response.statusCode == 200 && response.data != null) {
      return User.fromJson(response.data!);
    }

    throw Exception('Login failed: unexpected response from server.');
  }

  Future<void> logout() async {
    await _firebaseAuth.signOut();
  }
}
