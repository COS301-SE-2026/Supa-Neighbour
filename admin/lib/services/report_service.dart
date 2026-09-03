import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../models/report_model.dart';

class ReportServiceException implements Exception{
  final String message;
  final int? statusCode;

  ReportServiceException(this.message, {this.statusCode});

  @override
  String toString() => message;
}

class ReportService {
  final Dio _dio;
  final fb.FirebaseAuth _firebaseAuth;

  ReportService({Dio? dio, fb.FirebaseAuth ? firebaseAuth})
    : _dio = dio ?? 
      Dio(BaseOptions(

        baseUrl: 'http://localhost:8080',
        connectTimeout: const Duration(seconds: 30),
        receiveTimeout: const Duration(seconds: 30),
      )), 
    _firebaseAuth = firebaseAuth ?? fb.FirebaseAuth.instance;

  Future<List<Report>> getAssignedReports({
    ReportStatus ? status,
    ReportType? reportType,
  })async {
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();
    if(idToken == null){
      throw ReportServiceException('Not Signed in.', statusCode: 401);
    }

    try{
      final response = await _dio.get(
        '/api/report',
        queryParameters: {
          if(status != null && status != ReportStatus.unknown)
            'status': status.apiValue,
          if(reportType != null && reportType != ReportType.unknown)
            'reportType': reportType.apiValue,
        },
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      final data = response.data;
      if(data is List){
        return data
          .whereType<Map<String, dynamic>>()
          .map(Report.fromJson)
          .toList();
      }

      throw ReportServiceException('Unexpected response shape from server.');
    } on DioException catch(e){
      final statusCode = e.response?.statusCode;
      switch(statusCode){
        case 400:
          throw ReportServiceException('Invalid filter value.', statusCode: 400);
        case 401:
          throw ReportServiceException(
              'Session expired — please log in again.',
              statusCode: 401);
        case 403:
          throw ReportServiceException('Not authorized to view reports.',
              statusCode: 403);
        default:
          throw ReportServiceException(
            'Failed to load reports: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    } 
  }

  Future<Report?> getReportById(int reportId) async {
    final reports = await getAssignedReports();
    for (final report in reports) {
      if (report.reportId == reportId) return report;
    }
    return null;
  }

  Future<SuggestedAction?> getSuggestedAction({
    required ViolationType violationType, 
    required Severity severity,
  }) async{
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();
    if(idToken == null){
      throw ReportServiceException('Not Signed in.', statusCode: 401);
    }

    try{
      final response = await _dio.post(
        '/api/suggestion',
        data: {
          'violationType' : violationType.apiValue,
          'severity': severity.apiValue,
        },
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      final data = response.data;
      if(data is Map<String, dynamic>){
        return SuggestedAction.fromApi(data['suggestedAction'] as String?);
      }
      throw ReportServiceException('Unexpected response shape from server.');
    } on DioException catch(e){
      final statusCode = e.response?.statusCode;

      if(statusCode == 400){
        final body = e.response?.data;
        final message = body is String ? body : 'Invalid request';

        if(message.toLowerCase().contains('no rule defined')){
          return null;
        }

        throw ReportServiceException(message, statusCode: 400);
      }

      switch(statusCode){
        case 401:
          throw ReportServiceException(
              'Session expired — please log in again.',
              statusCode: 401);
        case 403:
          throw ReportServiceException(
              'Not authorized to request suggestions.',
              statusCode: 403);
        default:
          throw ReportServiceException(
            'Failed to get suggested action: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    }
  }

  Future<Report> patchReport({
    required int reportId,
    String? status, 
    ViolationType? violationType,
    Severity? severity,
    String? actualAction,
    String? adminNotes,
  }) async{
    final String? idToken = await _firebaseAuth.currentUser?.getIdToken();

    if(idToken == null){
      throw ReportServiceException('Not Signed in.', statusCode: 401);
    }

    try{
      final response = await _dio.patch(
        '/api/report',
        data: {
          'reportId': reportId,
          if(status != null) 'status': status,
          if(violationType != null) 'violationType': violationType.apiValue,
          if(severity != null) 'severity': severity.apiValue,
          if(actualAction != null) 'actualAction': actualAction,
          if(adminNotes != null) 'adminNotes': adminNotes,
        },
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if(response.data is! Map<String, dynamic>){
        throw ReportServiceException('Unexpected response shape from server.');
      }

      final refreshed = await getReportById(reportId);
      if(refreshed == null){
        throw ReportServiceException('Reportupdated but could not be reoloaded.');
      }
      return refreshed;
    }on DioException catch(e){
      final statusCode = e.response?.statusCode;
      switch(statusCode){
        case 400:
          throw ReportServiceException('Invalid field value.', statusCode: 400);
        case 401:
          throw ReportServiceException('Session expired - please log in again.', statusCode: 401);
        case 403:
          throw ReportServiceException('No authorized to updae this report.', statusCode: 404);
        case 404:
          throw ReportServiceException('Report not found.', statusCode: 404);
        default:
          throw ReportServiceException(
            'Failed to update report: ${e.message ?? 'unknown error'}',
            statusCode: statusCode,
          );
      }
    }
  }
}