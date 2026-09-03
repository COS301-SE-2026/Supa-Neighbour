// shared/lib/services/api_client.dart

import 'package:dio/dio.dart';

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  late Dio _dio;

  factory ApiClient() => _instance;

  ApiClient._internal() {
    _dio = Dio(BaseOptions(
      baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net', // Will be configurable via environment
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ));

    // Add interceptors for logging (optional)
    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) {
        print('Request: ${options.method} ${options.path}');
        return handler.next(options);
      },
      onResponse: (response, handler) {
        print('Response: ${response.statusCode} ${response.requestOptions.path}');
        return handler.next(response);
      },
      onError: (error, handler) {
        print('Error: ${error.message}');
        return handler.next(error);
      },
    ));
  }

  Dio get dio => _dio;

  void setAuthToken(String token) {
    _dio.options.headers['Authorization'] = 'Bearer $token';
  }

  void clearAuthToken() {
    _dio.options.headers.remove('Authorization');
  }

  void setBaseUrl(String baseUrl) {
    _dio.options.baseUrl = baseUrl;
  }

  // Optional: Add token refresh interceptor
  void enableTokenRefresh() {
    _dio.interceptors.add(InterceptorsWrapper(
      onError: (error, handler) async {
        if (error.response?.statusCode == 401) {
          // Token expired - attempt refresh
          try {
            // You can implement token refresh logic here
            // final newToken = await refreshToken();
            // setAuthToken(newToken);
            // final retry = await _dio.request(
            //   error.requestOptions.path,
            //   options: error.requestOptions,
            // );
            // return handler.resolve(retry);
          } catch (_) {
            // Refresh failed - force logout
            clearAuthToken();
          }
        }
        return handler.next(error);
      },
    ));
  }
}