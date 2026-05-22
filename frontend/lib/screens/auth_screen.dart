import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';  // Add this import
import 'auth/login_screen.dart';
import 'auth/signup_screen.dart';

class AuthScreen extends StatelessWidget {
  const AuthScreen({super.key});

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
    
    final logoSize = 286 * scaleX;

    return Scaffold(
      body: Container(
        width: screenWidth,
        height: screenHeight,
        color: Colors.white,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Logo - Using LogoPlaceholder (replaced placeholder container)
            LogoPlaceholder(size: logoSize),

            const SizedBox(height: 60),

            // Title
            Text(
              'super Neighbour',
              style: TextStyle(
                fontSize: 80 * scaleX,
                fontWeight: FontWeight.w600,
                color: const Color(0xFF1C9A89),
              ),
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 16),

            // Subtitle
            Text(
              'Your neighbourly helper',
              style: TextStyle(
                fontSize: 40 * scaleX,
                fontWeight: FontWeight.w600,
                color: const Color(0xFF1C9A89),
              ),
              textAlign: TextAlign.center,
            ),

            const SizedBox(height: 80),

            // Login Button - Navigates to Login Screen
            GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const LoginScreen()),
                );
              },
              child: Container(
                width: 671 * scaleX,
                height: 90 * scaleY,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(29),
                  color: const Color(0xFF1C9A89),
                ),
                child: Center(
                  child: Text(
                    'login',
                    style: TextStyle(
                      fontSize: 40 * scaleX,
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ),
              ),
            ),

            const SizedBox(height: 20),

            // Create Account Button
            GestureDetector(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const SignupScreen()),
                );
              },
              child: Container(
                width: 671 * scaleX,
                height: 90 * scaleY,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(29),
                  border: Border.all(
                    color: const Color(0xFF1C9A89),
                    width: 4 * scaleX,
                  ),
                  color: Colors.transparent,
                ),
                child: Center(
                  child: Text(
                    'create an account',
                    style: TextStyle(
                      fontSize: 40 * scaleX,
                      fontWeight: FontWeight.w600,
                      color: const Color(0xFF1C9A89),
                    ),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}