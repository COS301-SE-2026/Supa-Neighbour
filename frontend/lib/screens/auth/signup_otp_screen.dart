import 'dart:async';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import 'signup_details_screen.dart';

class SignupOtpScreen extends StatefulWidget {
  final String email;
  final String idToken;
  final String password;

  const SignupOtpScreen({
    super.key,
    required this.email,
    required this.idToken,
    required this.password,
  });

  @override
  State<SignupOtpScreen> createState() => _SignupOtpScreenState();
}

class _SignupOtpScreenState extends State<SignupOtpScreen> {
  Timer? _pollingTimer;
  bool _isResending = false;

  @override
  void initState() {
    super.initState();
    _pollingTimer = Timer.periodic(const Duration(seconds: 3), (_) async {
      await fb.FirebaseAuth.instance.currentUser?.reload();
      final user = fb.FirebaseAuth.instance.currentUser;

      if (user?.emailVerified == true) {
        _pollingTimer?.cancel();
        if (!mounted) return;
        
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => SignupDetailsScreen(
              email: widget.email,
              idToken: widget.idToken,
              password: widget.password,
            ),
          ),
        );
      }
    });
  }

  @override
  void dispose() {
    _pollingTimer?.cancel();
    super.dispose();
  }

  Future<void> _handleResendEmail() async {
    setState(() => _isResending = true);
    try {
      await fb.FirebaseAuth.instance.currentUser?.sendEmailVerification();
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Verification email resent to ${widget.email}'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
    } on Exception catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(e.toString().replaceAll('Exception: ', '')),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      if (mounted) setState(() => _isResending = false);
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
                              'Verify Email',
                              style: AppTextStyles.primaryHeader(context).copyWith(
                                fontSize: isSmallScreen ? 20.0 : 24.0,
                                fontWeight: FontWeight.w600,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),

                            SizedBox(height: spacing * 1.2),

                            // Email envelope icon
                            Icon(
                              Icons.mark_email_unread_outlined,
                              size: screenWidth * 0.12,
                              color: AppColors.primaryTeal(context),
                            ),

                            SizedBox(height: spacing),

                            Text(
                              'We sent a verification link to',
                              style: AppTextStyles.bodyText(context).copyWith(
                                fontSize: fontSize,
                                color: AppColors.primaryTeal(context),
                              ),
                              textAlign: TextAlign.center,
                            ),

                            SizedBox(height: spacing * 0.5),

                            // User's email address in bold
                            Text(
                              widget.email,
                              style: AppTextStyles.bodyText(context).copyWith(
                                fontSize: fontSize,
                                fontWeight: FontWeight.w600,
                                color: AppColors.primaryTeal(context),
                              ),
                              textAlign: TextAlign.center,
                            ),

                            SizedBox(height: spacing),

                            Text(
                              'Click the link in the email to continue.\nThis page will update automatically.',
                              style: AppTextStyles.bodyText(context).copyWith(
                                fontSize: smallFontSize,
                                color: AppColors.textGrey(context),
                              ),
                              textAlign: TextAlign.center,
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Spinner - polling
                            const CircularProgressIndicator(
                              color: Color(0xFF2A9D8F),
                              strokeWidth: 2,
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Resend row - FIXED with Wrap
                            Wrap(
                              alignment: WrapAlignment.center,
                              crossAxisAlignment: WrapCrossAlignment.center,
                              children: [
                                Text(
                                  "Didn't receive it? ",
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: smallFontSize,
                                    color: AppColors.primaryTeal(context),
                                  ),
                                ),
                                GestureDetector(
                                  onTap: _isResending ? null : _handleResendEmail,
                                  child: _isResending
                                      ? SizedBox(
                                          width: smallFontSize,
                                          height: smallFontSize,
                                          child: const CircularProgressIndicator(
                                            color: Color(0xFF2A9D8F),
                                            strokeWidth: 1.5,
                                          ),
                                        )
                                      : Text(
                                          'Resend email',
                                          style: AppTextStyles.bodyText(context).copyWith(
                                            fontSize: smallFontSize,
                                            color: AppColors.primaryTeal(context),
                                            fontWeight: FontWeight.w600,
                                          ),
                                        ),
                                ),
                              ],
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