import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/leaderboard_model.dart';
import '../config/api_config.dart';

class LeaderboardService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  LeaderboardService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: ApiConfig.baseUrl,
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
              headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
              },
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<LeaderboardData> getLeaderboard({
    String period = 'week',
    String rankBy = 'averageRating',
    int limit = 20,
  }) async {
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
        '/api/leaderboard',
        options: Options(
          headers: {
            'Authorization': 'Bearer $idToken',
          },
        ),
        queryParameters: {
          'rankBy': rankBy,
          'limit': limit,
        },
      );

      print('Response status: ${response.statusCode}');

      if (response.statusCode == 200 && response.data != null) {
      print('Data received: ${response.data}');
        final leaderboardResponse = LeaderboardResponse.fromJson(response.data);
      print('Neighbourhood: ${leaderboardResponse.neighbourhood}');
      print('Entries: ${leaderboardResponse.leaderboard.length}');
        return leaderboardResponse.toLeaderboardData(period: period);
      }

      throw Exception('Failed to load leaderboard');
    } on DioException catch (e) {
      print(' Error: ${e.message}');
      throw Exception('Connection error: ${e.message}');
    }
  }
}