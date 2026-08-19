import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb; //to avoid clash with usr model
import 'package:flutter/widgets.dart';
import '../models/user_model.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/address_model.dart';
import 'package:firebase_messaging/firebase_messaging.dart';


// INTERFACE (Contract)
abstract class IAuthService {
  Future<User> login(String email, String password);
  Future<User> loginWithToken(String idToken);
  Future<void> logout();
  Future<void> deleteAccount();
  Future<User> register({
    required String idToken,
    required String firstName,
    required String lastName,
    required String password,
    required String phoneNumber,
    required String dateOfBirth,
    required String gender,
    required String username,
    required String street,
    required String town,
    required int zip,
    String userType = 'user',
  });
}


class AuthService implements IAuthService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  AuthService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  @override
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
      //await _registerFcmToken();
      return User.fromJson(res.data!);
    }

    throw Exception('Login failed: unexpected response from server.');
  }

@override
Future<User> loginWithToken(String idToken) async {
  final Response<Map<String, dynamic>> response = await _dio.post(
    '/api/auth/login',
    options: Options(
      headers: {'Authorization': 'Bearer $idToken'},
    ),
  );

  if (response.statusCode == 200 && response.data != null) {
    return User.fromJson(response.data!);
  }

  throw Exception('Auto-login failed: unexpected response from server.');
}

 @override
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

  Future<int> createAddress({
    required String street,
    required String town,
    required int zip,
  }) async{
    final Response<Map<String, dynamic>> res = await _dio.post(
      '/api/addresses', 
      data: {
        'street': street, 
        'town': town,
        'zip': zip,
      },
    );

    if(res.statusCode == 201 && res.data != null){
      return Address.fromJson(res.data!).addressid;    
    }

    throw Exception('Address createion failed: unexpected response from server.');
  }


  @override
  Future<User> register({
    required String idToken,
    required String firstName,
    required String lastName,
    required String password,
    required String phoneNumber,
    required String dateOfBirth, 
    required String gender,
    required String username,
    required String street,
    required String town,
    required int zip,
    String userType = 'user',
  }) async {
    final int addressId = await createAddress(
      street: street, 
      town: town,
      zip: zip,
    );
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
      },
    );

    if (response.statusCode == 200 && response.data != null) {
      return User.fromJson(response.data!);
    }

    throw Exception('Registration failed: unexpected response from server.');
  }
  
  @override
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

  Future<void> _postDeviceToken(String fcmToken) async{
    final fb.User? firebaseUser = _firebaseAuth.currentUser;

    if(firebaseUser == null) return;

    final String? idToken = await firebaseUser.getIdToken(false);
    if(idToken == null) return;

    await _dio.post(
      '/api/users/me/device-token',
      data: {'fcmToken': fcmToken},
      options: Options(
        headers:{'Authorization': 'Bearer $idToken'},
      )
    );
  }

  Future<void> _registerFcmToken() async{
    final messaging = FirebaseMessaging.instance;
    try{
      NotificationSettings settings = await messaging.requestPermission(
        alert: true,
        badge: true,
        sound: true,
      );

      if(settings.authorizationStatus == AuthorizationStatus.authorized || settings.authorizationStatus == AuthorizationStatus.provisional){
        String? token = await messaging.getToken();
        if(token != null){
          await _postDeviceToken(token);
        }
      
        FirebaseMessaging.instance.onTokenRefresh.listen((newToken){
          _postDeviceToken(newToken);
        });
      }else if(settings.authorizationStatus == AuthorizationStatus.denied){
        debugPrint(' Push notification permission denied');
      }
    } catch(e){
      debugPrint('Failed to register FCM token: $e');
    }
  }

}
