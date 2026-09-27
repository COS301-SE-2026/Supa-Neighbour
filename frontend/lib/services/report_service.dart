import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/report_request.dart';
import '../models/report_dto.dart';
import 'dart:typed_data';

class ReportService {
  final Dio _dio;

  ReportService({Dio? dio})
      : _dio = dio ?? Dio(BaseOptions(
            //baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
            baseUrl: 'http://localhost:8080',
            connectTimeout: const Duration(seconds: 30),
            receiveTimeout: const Duration(seconds: 30),
          ));

  Future<int> submitTaskReport(ReportRequest request) async {
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) {
      throw Exception('You must be logged in to submit a report.');
    }

    final idToken = await user.getIdToken();
    if (idToken == null) {
      throw Exception('Failed to retrieve ID token.');
    }

    final response = await _dio.put(
      '/api/report',
      data: request.toJson(),
      options: Options(
        headers: {
          'Authorization': 'Bearer $idToken',
          'Content-Type': 'application/json',
        },
      ),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to submit report. Status: ${response.statusCode}');
    }

    final data = response.data as Map<String, dynamic>;
    final reportId = data['reportId'] as int?;
    if (reportId == null) {
      throw Exception('Report was submitted but no reportId was returned.');
    }
    return reportId;
  }

  /// Get all reports submitted by the current user.
  /// Optional filters: status (submitted, assigned, reviewed) and reportType (USER, POST, COMMENT, TASK_DISPUTE)
  Future<List<ReportDTO>> getMyReports({String? status, String? reportType}) async {
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) {
      throw Exception('You must be logged in to view reports.');
    }

    final idToken = await user.getIdToken();
    if (idToken == null) {
      throw Exception('Failed to retrieve ID token.');
    }

    final queryParams = <String, String>{};
    if (status != null && status.isNotEmpty) {
      queryParams['status'] = status;
    }
    if (reportType != null && reportType.isNotEmpty) {
      queryParams['reportType'] = reportType;
    }

    final response = await _dio.get(
      '/api/report/me',
      queryParameters: queryParams,
      options: Options(
        headers: {
          'Authorization': 'Bearer $idToken',
        },
      ),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = response.data as List<dynamic>;
      return data.map((json) => ReportDTO.fromJson(json as Map<String, dynamic>)).toList();
    } else {
      throw Exception('Failed to fetch reports. Status: ${response.statusCode}');
    }
  }

  Future<String> uploadReportImage(Uint8List bytes, String filename) async {
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) {
      throw Exception('You must be logged in to upload images.');
    }
    final idToken = await user.getIdToken();
    if (idToken == null) {
      throw Exception('Failed to retrieve ID token.');
    }

    final formData = FormData.fromMap({
      'file': MultipartFile.fromBytes(bytes, filename: filename),
    });

    final response = await _dio.post(
      '/api/upload/report/image',
      data: formData,
      options: Options(headers: {'Authorization': 'Bearer $idToken'}),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to upload image. Status: ${response.statusCode}');
    }
    return (response.data as Map<String, dynamic>)['imageUrl'] as String;
  }

  Future<void> linkReportImage(int reportId, String imageUrl) async {
    final user = FirebaseAuth.instance.currentUser;
    if (user == null) {
      throw Exception('You must be logged in.');
    }
    final idToken = await user.getIdToken();
    if (idToken == null) {
      throw Exception('Failed to retrieve ID token.');
    }

    final response = await _dio.post(
      '/api/report/$reportId/images',
      data: {'imageUrl': imageUrl},
      options: Options(
        headers: {
          'Authorization': 'Bearer $idToken',
          'Content-Type': 'application/json',
        },
      ),
    );

    if (response.statusCode != 201) {
      throw Exception('Failed to attach image. Status: ${response.statusCode}');
    }
  }
}