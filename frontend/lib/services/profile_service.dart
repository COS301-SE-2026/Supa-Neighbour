import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:supa_neighbour/models/update_profile_response.dart';
import '../models/user_profile_response.dart';

// INTERFACE (Contract)
abstract class IProfileService {
  Future<UserProfileResponse> getMyProfile();
  Future<UpdateProfileResponse> updateSkills(List<String> skills);
}

class UserProfileService implements IProfileService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  UserProfileService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
  :_dio = dio ??
  Dio(BaseOptions(
    //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
    baseUrl: 'http://localhost:8080',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 10),
    )),
    _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<UserProfileResponse> getMyProfile() async{
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken(false);

    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    final Response<Map<String, dynamic>> res = await _dio.get(
      '/api/users/me/profile',
      options: Options(headers: {'Authorization': 'Bearer $idToken'}),
    );

    if(res.statusCode == 200 && res.data != null){
      return UserProfileResponse.fromJson(res.data!);
    }

    throw Exception('Failed to fetch profile: unexpected response form server.');
  }

  Future<UpdateProfileResponse> updateSkills(List<String> skills) async{
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken(false);
    if(idToken == null){
      throw Exception('No authenticated Firebase user.');
    }

    final Response<Map<String, dynamic>> res = await _dio.patch(
      '/api/users/me/profile',
      options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      data: {'skills': skills},
    );

    if(res.statusCode == 200 && res.data != null){
      return UpdateProfileResponse.fromJson(res.data!);
    }

    throw Exception('Failed to update skills: unexpected response from server');
  }

  
}