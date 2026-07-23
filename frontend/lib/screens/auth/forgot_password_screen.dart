import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart';

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
        const SnackBar(
          content: Text('Please enter your email'),
          backgroundColor: Color(0xFF1C9A89),
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
              'Reset link sent to ${_emailController.text.trim()}. Check your inbox.'),
          backgroundColor: const Color(0xFF1C9A89),
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

    final logoSize = screenWidth * 0.3;
    final titleSize = screenWidth * 0.08;
    final subtitleSize = screenWidth * 0.045;
    final buttonHeight = screenHeight * 0.07;
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
                        child: const Icon(Icons.arrow_back,
                            color: Colors.white, size: 30),
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
                        color: const Color(0xFF1C9A89)),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: screenHeight * 0.01),
                  Text(
                    'Your neighbourly helper',
                    style: TextStyle(
                        fontSize: subtitleSize,
                        fontWeight: FontWeight.w600,
                        color: const Color(0xFF1C9A89)),
                    textAlign: TextAlign.center,
                  ),

                  SizedBox(height: screenHeight * 0.04),

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
                            'Forgot Password',
                            style: TextStyle(
                                fontSize: titleSize * 0.8,
                                fontWeight: FontWeight.w600,
                                color: const Color(0xFF1C9A89)),
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          // Explanation text
                          Text(
                            'Enter your email and we\'ll send you a link to reset your password.',
                            style: TextStyle(
                                fontSize: smallFontSize,
                                color: Colors.grey[600]),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text('Email',
                                  style: TextStyle(
                                      fontSize: fontSize,
                                      fontWeight: FontWeight.w400,
                                      color: const Color(0xFF1C9A89))),
                              SizedBox(height: screenHeight * 0.01),
                              Container(
                                width: double.infinity,
                                height: buttonHeight,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(29),
                                  color: Colors.white,
                                  border: Border.all(
                                      color: const Color(0xFF1C9A89),
                                      width: 2),
                                ),
                                child: TextField(
                                  controller: _emailController,
                                  style: TextStyle(fontSize: fontSize * 0.7),
                                  keyboardType: TextInputType.emailAddress,
                                  decoration: InputDecoration(
                                    hintText: 'Enter your email',
                                    hintStyle: TextStyle(
                                        fontSize: fontSize * 0.6,
                                        color: Colors.grey),
                                    border: InputBorder.none,
                                    contentPadding: EdgeInsets.symmetric(
                                        horizontal: screenWidth * 0.05,
                                        vertical: screenHeight * 0.02),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          GestureDetector(
                            onTap: _isLoading ? null : _sendResetLink,
                            child: Container(
                              width: double.infinity,
                              height: buttonHeight,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(29),
                                color: const Color(0xFF1C9A89),
                              ),
                              child: Center(
                                child: _isLoading
                                    ? SizedBox(
                                        width: buttonHeight * 0.4,
                                        height: buttonHeight * 0.4,
                                        child: const CircularProgressIndicator(
                                            color: Colors.white, strokeWidth: 2),
                                      )
                                    : Text(
                                        'Send Reset Link',
                                        style: TextStyle(
                                            fontSize: fontSize,
                                            fontWeight: FontWeight.w600,
                                            color: Colors.white),
                                      ),
                              ),
                            ),
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          Center(
                            child: GestureDetector(
                              onTap: () => Navigator.pop(context),
                              child: Text('Back',
                                  style: TextStyle(
                                      fontSize: smallFontSize,
                                      color: const Color(0xFF1C9A89),
                                      fontWeight: FontWeight.w500)),
                            ),
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
