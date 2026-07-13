import 'dart:async';
import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart';
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
          backgroundColor: const Color(0xFF1C9A89),
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

    final logoSize = screenWidth * 0.3;
    final titleSize = screenWidth * 0.08;
    final subtitleSize = screenWidth * 0.045;
    final fontSize = screenWidth * 0.04;
    final smallFontSize = screenWidth * 0.035;

    return Scaffold(
      body: SafeArea(
        child: Container(
          width: screenWidth,
          height: screenHeight,
          color: Colors.white,
          child: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.05),
              child: Column(
                children: [
                  SizedBox(height: screenHeight * 0.03),

                  // Back button - returns to signup screen
                  Align(
                    alignment: Alignment.centerLeft,
                    child: GestureDetector(
                      onTap: () => Navigator.pop(context),
                      child: Container(
                        width: 50,
                        height: 50,
                        decoration: const BoxDecoration(
                          color: Color(0xFF1C9A89),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.arrow_back,
                          color: Colors.white,
                          size: 30,
                        ),
                      ),
                    ),
                  ),

                  SizedBox(height: screenHeight * 0.02),

                  LogoPlaceholder(size: logoSize),

                  SizedBox(height: screenHeight * 0.03),

                  Text(
                    'super Neighbour',
                    style: TextStyle(
                      fontSize: titleSize,
                      fontWeight: FontWeight.w600,
                      color: const Color(0xFF1C9A89),
                    ),
                    textAlign: TextAlign.center,
                  ),

                  SizedBox(height: screenHeight * 0.01),

                  Text(
                    'Your neighbourly helper',
                    style: TextStyle(
                      fontSize: subtitleSize,
                      fontWeight: FontWeight.w600,
                      color: const Color(0xFF1C9A89),
                    ),
                    textAlign: TextAlign.center,
                  ),

                  SizedBox(height: screenHeight * 0.04),

                  // Card
                  Container(
                    width: double.infinity,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(30),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.grey.withValues(alpha: 0.2),
                          blurRadius: 20,
                          offset: const Offset(0, 10),
                        ),
                      ],
                    ),
                    child: Padding(
                      padding: EdgeInsets.all(screenWidth * 0.07),
                      child: Column(
                        children: [
                          Text(
                            'Verify Email',
                            style: TextStyle(
                              fontSize: titleSize * 0.8,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                          ),

                          SizedBox(height: screenHeight * 0.03),

                          // Email envelope icon
                          Icon(
                            Icons.mark_email_unread_outlined,
                            size: screenWidth * 0.15,
                            color: const Color(0xFF1C9A89),
                          ),

                          SizedBox(height: screenHeight * 0.03),

                          Text(
                            'We sent a verification link to',
                            style: TextStyle(
                              fontSize: fontSize,
                              fontWeight: FontWeight.w400,
                              color: const Color(0xFF1C9A89),
                            ),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.01),

                          // User's email address in bold
                          Text(
                            widget.email,
                            style: TextStyle(
                              fontSize: fontSize,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          Text(
                            'Click the link in the email to continue.\nThis page will update automatically.',
                            style: TextStyle(
                              fontSize: smallFontSize,
                              color: Colors.grey[600],
                            ),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // Spinner - polling
                          const CircularProgressIndicator(
                            color: Color(0xFF1C9A89),
                            strokeWidth: 2,
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // Resend row
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                "Didn't receive it? ",
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: const Color(0xFF1C9A89),
                                ),
                              ),
                              GestureDetector(
                                onTap: _isResending ? null : _handleResendEmail,
                                child: _isResending
                                    ? SizedBox(
                                        width: smallFontSize,
                                        height: smallFontSize,
                                        child: const CircularProgressIndicator(
                                          color: Color(0xFF1C9A89),
                                          strokeWidth: 1.5,
                                        ),
                                      )
                                    : Text(
                                        'Resend email',
                                        style: TextStyle(
                                          fontSize: smallFontSize,
                                          color: const Color(0xFF1C9A89),
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

                  SizedBox(height: screenHeight * 0.03),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
