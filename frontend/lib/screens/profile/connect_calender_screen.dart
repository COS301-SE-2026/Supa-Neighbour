import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:google_sign_in/google_sign_in.dart';

//import 'package:shared/services/api_client.dart';


class ConnectCalenderScreen extends StatefulWidget {
  const ConnectCalenderScreen({super.key});
  @override
  State<ConnectCalenderScreen> createState() => _ConnectCalenderScreenState();
}

class _ConnectCalenderScreenState extends State<ConnectCalenderScreen> {
  bool _connecting = false;

  static const String _localBackendUrl = String.fromEnvironment(
    'LOCAL_BACKEND_URL',
    defaultValue: 'http://localhost:8080',
  );

  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes : ['https://www.googleapis.com/auth/calendar.events'],
    serverClientId: '791892980644-ntsn6lh4mfuvs2jsnu6eouk8lhl0m7fd.apps.googleusercontent.com', 
  );

  Future<void> _connect() async{
    setState (() => _connecting = true);

    try{
      final account = await _googleSignIn.signIn();

      if(account == null) return;

      final authCode = account.serverAuthCode;
      if(authCode == null) throw Exception('No server code returned');

      final idToken = await fb.FirebaseAuth.instance.currentUser?.getIdToken();
      if (idToken == null) throw Exception('No authenticated Firebase user');

      debugPrint('--- FIREBASE ID TOKEN ---');
debugPrint(idToken);
debugPrint('-------------------------');

      final localDio = Dio(
        BaseOptions(
          baseUrl: _localBackendUrl,
          connectTimeout: const Duration(seconds: 20),
          receiveTimeout: const Duration(seconds: 20),
        ),
      );

      debugPrint('Calling backend URL: ${localDio.options.baseUrl}/api/users/me/google-calender/connect');

      await localDio.post(
        '/api/users/me/google-calender/connect',
        data: {'authCode': authCode},
        options: Options(
          headers: {
            'Authorization': 'Bearer $idToken',
          },
        ),
      );

      if(mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Google Calender connected')),
        );
      }
    }catch (e){
      debugPrint('Google Calendar connect error: $e');
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to connect: $e')),
        );
      }
    } finally {
        if(mounted) setState(() => _connecting = false);
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
