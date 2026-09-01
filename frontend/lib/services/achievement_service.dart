import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/achievement_model.dart';

// INTERFACE
abstract class IAchievementService {
  Future<AchievementsResponse> getAchievements();
}

class AchievementService implements IAchievementService {
  final Dio _dio;

  AchievementService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
             // baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 10),
              receiveTimeout: const Duration(seconds: 10),
            ));

  @override
  Future<AchievementsResponse> getAchievements() async {
    final fbUser = fb.FirebaseAuth.instance.currentUser;
    if (fbUser == null) {
      throw Exception('Not authenticated');
    }
    final token = await fbUser.getIdToken();
    try {
      final Response<Map<String, dynamic>> res = await _dio.get(
        '/api/users/me/achievements',
        options: Options(headers: {'Authorization': 'Bearer $token'}),
      );
      return AchievementsResponse.fromJson(res.data!);
    } on DioException catch (e) {
      throw Exception("Couldn't load achievements: ${e.message}");
    }
  }
}
