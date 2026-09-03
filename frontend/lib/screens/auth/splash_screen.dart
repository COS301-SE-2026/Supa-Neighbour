import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../components/loading_bar.dart';
import '../../components/splash_title.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../models/auth_session.dart';
import '../../providers/service_providers.dart';
import 'auth_screen.dart';
import '../home/home_screen.dart';

class SplashScreen extends ConsumerStatefulWidget {
  const SplashScreen({super.key});

  @override
  ConsumerState<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends ConsumerState<SplashScreen> {
  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(seconds: 3), () async {
      if (!mounted) return;

      final prefs = await SharedPreferences.getInstance();
      final rememberMe = prefs.getBool('remember_me') ?? false;

      final fbUser = fb.FirebaseAuth.instance.currentUser;

      if (rememberMe && fbUser != null && fbUser.emailVerified) {
        try {
          final idToken = await fbUser.getIdToken();
          final auth = ref.read(authServiceProvider);

          final user = await auth.loginWithToken(idToken!);
          AuthSession.instance.login(user);

          final prefs2 = await SharedPreferences.getInstance();
          await prefs2.setInt('current_user_id', int.parse(user.id));

          try {
            final dioTemp = Dio(BaseOptions(
              baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net'
            ));
            final depRes = await dioTemp.get('/api/dependents');
            final depList = depRes.data as List<dynamic>;
            for (final item in depList) {
              if (item['userId'] == int.parse(user.id) || 
                  item['userId']?['userid'] == int.parse(user.id)) {
                await prefs2.setInt('current_dependent_id', item['dependentId'] as int);
                break;
              }
            }
          } catch (_) {}

          if (!mounted) return;
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => const HomeScreen()),
          );
          return;
        } catch (_) {}
      }

      if (!mounted) return;
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const AuthScreen()),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    // Responsive sizing
    final isSmallScreen = screenWidth < 400;
    final isLargeScreen = screenWidth > 800;
    
    final logoSize = (screenWidth * 0.3).clamp(100.0, 300.0);
    final titleSize = isSmallScreen ? 28.0 : (isLargeScreen ? 48.0 : 36.0);
    final subtitleSize = isSmallScreen ? 16.0 : (isLargeScreen ? 28.0 : 20.0);
    
    // Responsive spacing - calculated from screen height
    final logoTop = screenHeight * 0.26;
    final titleTop = screenHeight * 0.42;
    final subtitleTop = screenHeight * 0.48;
    final loadingBarTop = screenHeight * 0.80;
    
    final loadingBarWidth = (screenWidth * 0.7).clamp(200.0, 759.0);
    final loadingBarHeight = (screenHeight * 0.007).clamp(8.0, 16.0);

    return Scaffold(
      body: Stack(
        children: [
          // Background
          Container(
            width: screenWidth,
            height: screenHeight,
            color: AppColors.background(context),
          ),

          // Logo
          Positioned(
            left: (screenWidth - logoSize) / 2,
            top: logoTop,
            child: LogoPlaceholder(size: logoSize),
          ),

          // Title
          Positioned(
            left: 0,
            top: titleTop,
            width: screenWidth,
            child: SplashTitle(
              text: 'Super Neighbour',
              fontSize: titleSize,
              color: AppColors.primaryTeal(context),
            ),
          ),

          // Subtitle
          Positioned(
            left: 0,
            top: subtitleTop,
            width: screenWidth,
            child: SplashTitle(
              text: 'Your neighbourly helper',
              fontSize: subtitleSize,
              color: AppColors.primaryTeal(context),
            ),
          ),

          // Animated Loading Bar
          Positioned(
            left: (screenWidth - loadingBarWidth) / 2,
            top: loadingBarTop,
            child: LoadingBar(
              width: loadingBarWidth,
              height: loadingBarHeight,
              duration: const Duration(seconds: 3),
            ),
          ),
        ],
      ),
    );
  }
}