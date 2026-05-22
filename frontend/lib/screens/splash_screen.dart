import 'package:flutter/material.dart';
import '../components/loading_bar.dart';
import '../components/splash_logo.dart';
import '../components/splash_title.dart';
import 'auth_screen.dart';
import '../components/logo_placeholder.dart';

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    // Navigate to auth screen after 3 seconds (matches loading bar duration)
    Future.delayed(const Duration(seconds: 3), () {
      if (mounted) {
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const AuthScreen()),
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    // Samsung S20 FE reference dimensions
    const double designWidth = 1080;
    const double designHeight = 2400;

    // Scale factors
    final scaleX = screenWidth / designWidth;
    final scaleY = screenHeight / designHeight;

    return Scaffold(
      body: Stack(
        children: [
          // White background
          Container(
            width: screenWidth,
            height: screenHeight,
            color: Colors.white,
          ),

          // Logo
          Positioned(
  left: (screenWidth - (286 * scaleX)) / 2,
  top: 620 * scaleY,
  child: LogoPlaceholder(size: 286 * scaleX),
),

          // Title
          Positioned(
            left: 0,
            top: 987 * scaleY,
            width: screenWidth,
            child: SplashTitle(
              text: 'super Neighbour',
              fontSize: 80 * scaleX,
              color: const Color(0xFF1C9A89),
            ),
          ),

          // Subtitle
          Positioned(
            left: 0,
            top: 1131 * scaleY,
            width: screenWidth,
            child: SplashTitle(
              text: 'Your neighbourly helper',
              fontSize: 40 * scaleX,
              color: const Color(0xFF1C9A89),
            ),
          ),

          // Animated Loading Bar
          Positioned(
            left: (screenWidth - (759 * scaleX)) / 2,
            top: 1929 * scaleY,
            child: LoadingBar(
              width: 759 * scaleX,
              height: 16 * scaleY,
              duration: const Duration(seconds: 3),
            ),
          ),
        ],
      ),
    );
  }
}