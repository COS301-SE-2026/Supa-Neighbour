import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/notification_model.dart';

abstract class INotificationsApiService {
  Future<List<AppNotification>> fetchNotifications();
  Future<void> markAsRead(String notificationId);
}

class NotificationsApiService implements INotificationsApiService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  NotificationsApiService({Dio? dio, fb.FirebaseAuth? firebaseAuth})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: 'http://localhost:8080',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
            )),
        _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<String> _authHeader() async {
    final idToken = await _firebaseAuth.currentUser?.getIdToken(false);
    if (idToken == null) {
      throw Exception('No authenticated user.');
    }
    return 'Bearer $idToken';
  }

  @override
  Future<List<AppNotification>> fetchNotifications() async {
    final auth = await _authHeader();
    final Response<List<dynamic>> res = await _dio.get(
      '/api/notifications',
      options: Options(headers: {'Authorization': auth}),
    );

    return (res.data ?? [])
        .map((e) => AppNotification.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  @override
  Future<void> markAsRead(String notificationId) async {
    final auth = await _authHeader();
    await _dio.patch(
      '/api/notifications/$notificationId/read',
      options: Options(headers: {'Authorization': auth}),
    );
  }
}