import 'package:flutter/material.dart';
//import 'screens/auth/splash_screen.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart'; 
import 'firebase_options.dart';
import 'providers/theme_mode_provider.dart'; 
//import 'screens/style_guide/style_guide_page.dart';
import 'screens/landing/landing_page.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const ProviderScope(child: MyApp()));
}

class MyApp extends ConsumerWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final themeMode = ref.watch(themeModeProvider);
    return MaterialApp(
      title: 'Super Neighbour',
      debugShowCheckedModeBanner: false,
      themeMode: themeMode,
      theme: ThemeData(
        useMaterial3: true,
        fontFamily: 'Google Sans Flex',
      ),
      darkTheme: ThemeData(                         
        useMaterial3: true,
        fontFamily: 'Google Sans Flex',
        brightness: Brightness.dark,
      ),
      //home: const SplashScreen(),
      home: LandingPage(),
    );
  }
}