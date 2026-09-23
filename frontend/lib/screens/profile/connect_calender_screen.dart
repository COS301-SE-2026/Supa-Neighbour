import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:google_sign_in/google_sign_in.dart';

class ConnectCalenderScreen extends StatefulWidget {
  const ConnectCalenderScreen({super.key});
  @override
  State<ConnectCalenderScreen> createState() => _ConnectCalenderScreenState();
}

class _ConnectCalenderScreenState extends State<ConnectCalenderScreen> {
  bool _connecting = false;

  static const String _backendBaseUrl = String.fromEnvironment(
    'LOCAL_BACKEND_URL',
    defaultValue: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net',
  );

  late final Dio _localDio = Dio(
    BaseOptions(
      baseUrl: _backendBaseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 15),
    ),
  );

  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes : ['https://www.googleapis.com/auth/calendar.events'],
    serverClientId: '791892980644-ntsn6lh4mfuvs2jsnu6eouk8lhl0m7fd.apps.googleusercontent.com', 
    forceCodeForRefreshToken: true,
  );

  Future<void> _connect() async{
    setState (() => _connecting = true);

    try{
      final account = await _googleSignIn.signIn();

      if(account == null) return;

      final authCode = account.serverAuthCode;
      debugPrint('AUTH CODE: $authCode');
      if(authCode == null) throw Exception('No server code returned');

      final idToken = await fb.FirebaseAuth.instance.currentUser?.getIdToken();
      if (idToken == null) throw Exception('No authenticated Firebase user');

      await _localDio.post(
        '/api/users/me/google-calender/connect',
        data: {'authCode': authCode},
        options: Options(
          headers: {'Authorization': 'Bearer $idToken'},
        ),
      );

      if(mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Google Calender connected')),
        );
      }
    } catch (e) {
      if (mounted) {
        String errorMessage = 'Failed to connect';

        if (e is DioException) {
          final responseData = e.response?.data;

          if (responseData is Map && responseData['message'] != null) {
            errorMessage = 'Failed to connect: ${responseData['message']}';
          } else if (responseData is String && responseData.isNotEmpty) {
            errorMessage = 'Failed to connect: $responseData';
          } else if (e.message != null) {
            errorMessage = 'Failed to connect: ${e.message}';
          } else {
            errorMessage = 'Failed to connect: $e';
          }
        } else {
          errorMessage = 'Failed to connect: $e';
        }

        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(errorMessage)),
        );
      }
    } finally {
      if (mounted) setState(() => _connecting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Calendar Sync')),
      body: Center(
        child: _connecting
            ? const CircularProgressIndicator()
            : ElevatedButton.icon(
                icon: const Icon(Icons.calendar_today),
                label: const Text('Connect Google Calendar'),
                onPressed: _connect,
              ),
      ),
    );
  }
}
