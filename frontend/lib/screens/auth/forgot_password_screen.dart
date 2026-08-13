import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';

class ForgotPasswordScreen extends StatefulWidget {
  const ForgotPasswordScreen({super.key});

  @override
  State<ForgotPasswordScreen> createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  final TextEditingController _emailController = TextEditingController();
  bool _isLoading = false;

  // keyword "email enumeration protection"
  Future<void> _sendResetLink() async {
    if (_emailController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your email'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      await fb.FirebaseAuth.instance
          .sendPasswordResetEmail(email: _emailController.text.trim());

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'Reset link sent to ${_emailController.text.trim()}. Check your inbox.',
          ),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );

      // back to login
      Navigator.pop(context);
    } on fb.FirebaseAuthException catch (e) {
      if (!mounted) return;
      final message = e.code == 'invalid-email'
          ? 'Please enter a valid email address.'
          : 'Failed to send reset link. Please try again.';
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message), backgroundColor: Colors.red),
      );
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    // Responsive sizing
    final isSmallScreen = screenWidth < 400;
    final isLargeScreen = screenWidth > 800;
    
    final logoSize = (screenWidth * 0.3).clamp(80.0, 180.0);
    final titleSize = isSmallScreen ? 24.0 : (isLargeScreen ? 40.0 : 32.0);
    final subtitleSize = isSmallScreen ? 14.0 : (isLargeScreen ? 24.0 : 18.0);
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
    final fontSize = isSmallScreen ? 14.0 : (isLargeScreen ? 20.0 : 16.0);
    final smallFontSize = isSmallScreen ? 12.0 : (isLargeScreen ? 16.0 : 14.0);
    
    // Spacing
    final spacing = screenHeight * 0.015;
    final largeSpacing = screenHeight * 0.03;

    return Scaffold(
      body: SafeArea(
        child: Container(
          width: screenWidth,
          height: screenHeight,
          color: AppColors.background(context),
          child: SingleChildScrollView(
            physics: const AlwaysScrollableScrollPhysics(),
            child: SizedBox(
              height: screenHeight,
              child: Padding(
                padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.06),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // Back button
                    Align(
                      alignment: Alignment.centerLeft,
                      child: GestureDetector(
                        onTap: () => Navigator.pop(context),
                        child: Container(
                          width: 50,
                          height: 50,
                          decoration: BoxDecoration(
                            color: AppColors.primaryTeal(context),
                            shape: BoxShape.circle,
                          ),
                          child: Icon(
                            Icons.arrow_back,
                            color: AppColors.textLight(context),
                            size: 30,
                          ),
                        ),
                      ),
                    ),

                    SizedBox(height: spacing),

                    LogoPlaceholder(size: logoSize),

                    SizedBox(height: largeSpacing),

                    Text(
                      'Super Neighbour',
                      style: AppTextStyles.primaryHeader(context).copyWith(
                        fontSize: titleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    SizedBox(height: spacing * 0.5),

                    Text(
                      'Your neighbourly helper',
                      style: AppTextStyles.secondaryHeader(context).copyWith(
                        fontSize: subtitleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    SizedBox(height: largeSpacing * 0.8),

                    // Card
                    Container(
                      width: double.infinity,
                      decoration: BoxDecoration(
                        color: AppColors.background(context),
                        borderRadius: BorderRadius.circular(30),
                        boxShadow: [
                          BoxShadow(
                            color: AppColors.charcoal(context).withValues(alpha: 0.08),
                            blurRadius: 20,
                            offset: const Offset(0, 10),
                          ),
                        ],
                      ),
                      child: Padding(
                        padding: EdgeInsets.all(screenWidth * 0.05),
                        child: Column(
                          children: [
                            Text(
                              'Forgot Password',
                              style: AppTextStyles.primaryHeader(context).copyWith(
                                fontSize: isSmallScreen ? 20.0 : 24.0,
                                fontWeight: FontWeight.w600,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),

                            SizedBox(height: spacing),

                            // Explanation text
                            Text(
                              'Enter your email and we\'ll send you a link to reset your password.',
                              style: AppTextStyles.bodyText(context).copyWith(
                                fontSize: smallFontSize,
                                color: AppColors.textGrey(context),
                              ),
                              textAlign: TextAlign.center,
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Email Field
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Email',
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: fontSize,
                                    fontWeight: FontWeight.w500,
                                    color: AppColors.primaryTeal(context),
                                  ),
                                ),
                                SizedBox(height: spacing * 0.3),
                                Container(
                                  width: double.infinity,
                                  height: buttonHeight,
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(29),
                                    color: AppColors.background(context),
                                    border: Border.all(
                                      color: AppColors.primaryTeal(context),
                                      width: 2,
                                    ),
                                  ),
                                  child: TextField(
                                    controller: _emailController,
                                    style: AppTextStyles.bodyText(context).copyWith(
                                      color: AppColors.charcoal(context),
                                    ),
                                    keyboardType: TextInputType.emailAddress,
                                    cursorColor: AppColors.primaryTeal(context),
                                    decoration: InputDecoration(
                                      hintText: 'Enter your email',
                                      hintStyle: AppTextStyles.bodyText(context).copyWith(
                                        color: AppColors.textGrey(context),
                                      ),
                                      border: InputBorder.none,
                                      contentPadding: EdgeInsets.symmetric(
                                        horizontal: screenWidth * 0.04,
                                        vertical: screenHeight * 0.01,
                                      ),
                                    ),
                                  ),
                                ),
                              ],
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Send Reset Link Button
                            GestureDetector(
                              onTap: _isLoading ? null : _sendResetLink,
                              child: Container(
                                width: double.infinity,
                                height: buttonHeight,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(29),
                                  color: AppColors.primaryTeal(context),
                                ),
                                child: Center(
                                  child: _isLoading
                                      ? SizedBox(
                                          width: 24,
                                          height: 24,
                                          child: CircularProgressIndicator(
                                            color: AppColors.textLight(context),
                                            strokeWidth: 2,
                                          ),
                                        )
                                      : Text(
                                          'Send Reset Link',
                                          style: AppTextStyles.buttonText(context).copyWith(
                                            fontSize: isSmallScreen ? 16.0 : 20.0,
                                          ),
                                        ),
                                ),
                              ),
                            ),

                            SizedBox(height: spacing),

                            // Back link
                            Center(
                              child: GestureDetector(
                                onTap: () => Navigator.pop(context),
                                child: Text(
                                  'Back',
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: smallFontSize,
                                    color: AppColors.primaryTeal(context),
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),

                    SizedBox(height: spacing * 1.5),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
