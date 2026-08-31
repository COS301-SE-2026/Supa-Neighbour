import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import 'login_screen.dart';
import 'signup_screen.dart';
class AuthScreen extends StatelessWidget {
  const AuthScreen({super.key});

  @override
  Widget build(BuildContext context) {
    //Responsive sizing
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    // Responsive sizing
    final isSmallScreen = screenWidth < 400;
    final isLargeScreen = screenWidth > 800;
    
    // Responsive logo size
    final logoSize = (screenWidth * 0.3).clamp(100.0, 300.0);
    
    // Responsive font sizes
    final titleSize = isSmallScreen ? 28.0 : (isLargeScreen ? 48.0 : 36.0);
    final subtitleSize = isSmallScreen ? 16.0 : (isLargeScreen ? 28.0 : 20.0);
    final buttonTextSize = isSmallScreen ? 16.0 : (isLargeScreen ? 24.0 : 20.0);
    
    // Responsive spacing
    final spacing = isSmallScreen ? 16.0 : (isLargeScreen ? 32.0 : 24.0);
    final largeSpacing = isSmallScreen ? 32.0 : (isLargeScreen ? 60.0 : 48.0);
    
    // Button sizing
    final buttonWidth = (screenWidth * 0.8).clamp(200.0, 500.0);
    final buttonHeight = isSmallScreen ? 48.0 : (isLargeScreen ? 64.0 : 56.0);

    return Scaffold(
      body: Container(
        width: screenWidth,
        height: screenHeight,
        color: AppColors.background(context),
        child: SingleChildScrollView(
          physics: const AlwaysScrollableScrollPhysics(),
          child: SizedBox(
            height: screenHeight,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                // Logo
                LogoPlaceholder(size: logoSize),

                SizedBox(height: spacing),

                // Title
                Text(
                  'Super Neighbour',
                  style: AppTextStyles.primaryHeader(context).copyWith(
                    fontSize: titleSize,
                  ),
                  textAlign: TextAlign.center,
                ),

                SizedBox(height: spacing * 0.6),

                // Subtitle
                Text(
                  'Your neighbourly helper',
                  style: AppTextStyles.secondaryHeader(context).copyWith(
                    fontSize: subtitleSize,
                  ),
                  textAlign: TextAlign.center,
                ),

                SizedBox(height: largeSpacing),

                // Login Button
                SizedBox(
                  width: buttonWidth,
                  height: buttonHeight,
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const LoginScreen()),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryTeal(context),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(29),
                      ),
                      elevation: 0,
                    ),
                    child: Text(
                      'Login',
                      style: AppTextStyles.buttonText(context).copyWith(
                        fontSize: buttonTextSize,
                        color: AppColors.textLight(context),
                      ),
                    ),
                  ),
                ),

                SizedBox(height: spacing * 0.6),

                // Create Account Button
                SizedBox(
                  width: buttonWidth,
                  height: buttonHeight,
                  child: OutlinedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const SignupScreen()),
                      );
                    },
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(
                        color: AppColors.primaryTeal(context),
                        width: 2,
                      ),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(29),
                      ),
                      backgroundColor: Colors.transparent,
                      elevation: 0,
                    ),
                    child: Text(
                      'Create an account',
                      style: AppTextStyles.buttonText(context).copyWith(
                        fontSize: buttonTextSize,
                        color: AppColors.primaryTeal(context),
                      ),
                    ),
                  ),
                ),
                
                SizedBox(height: largeSpacing),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
