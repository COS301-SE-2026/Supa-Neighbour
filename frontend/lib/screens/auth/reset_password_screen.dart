import 'package:flutter/material.dart';

class ResetPasswordScreen extends StatefulWidget {
  const ResetPasswordScreen({super.key});

  @override
  State<ResetPasswordScreen> createState() => _ResetPasswordScreenState();
}

class _ResetPasswordScreenState extends State<ResetPasswordScreen> {
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController =
      TextEditingController();
  bool _isLoading = false;
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;
  String _errorMessage = '';

  void _handleUpdate() {
    // Validate password
    if (_passwordController.text.isEmpty) {
      setState(() {
        _errorMessage = 'Please enter your password';
      });
      return;
    }

    if (_passwordController.text.length < 6) {
      setState(() {
        _errorMessage = 'Password must be at least 6 characters';
      });
      return;
    }

    if (_passwordController.text != _confirmPasswordController.text) {
      setState(() {
        _errorMessage = 'Passwords do not match';
      });
      return;
    }

    setState(() {
      _isLoading = true;
      _errorMessage = '';
    });

    // Simulate API call
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });

        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Password updated successfully!'),
            backgroundColor: Color(0xFF1C9A89),
          ),
        );

        // Navigate back to Login Screen
        Navigator.pushAndRemoveUntil(
          context,
          MaterialPageRoute(builder: (context) => const LoginScreenRedirect()),
          (route) => false,
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    // Responsive sizing
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

                  // Back Button
                  Align(
                    alignment: Alignment.centerLeft,
                    child: GestureDetector(
                      onTap: () {
                        Navigator.pop(context);
                      },
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

                  // Logo Placeholder
                  Container(
                    width: logoSize,
                    height: logoSize,
                    decoration: BoxDecoration(
                      color: Colors.grey[200],
                      shape: BoxShape.circle,
                    ),
                    child: const Center(
                      child: Icon(
                        Icons.lock_reset,
                        size: 60,
                        color: Colors.grey,
                      ),
                    ),
                  ),

                  SizedBox(height: screenHeight * 0.03),

                  // Title
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

                  // Subtitle
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

                  // White Card Container
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
                          // Title
                          Text(
                            'Create a new Password',
                            style: TextStyle(
                              fontSize: titleSize * 0.7,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // New Password Field
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Password',
                                style: TextStyle(
                                  fontSize: fontSize,
                                  fontWeight: FontWeight.w400,
                                  color: const Color(0xFF1C9A89),
                                ),
                              ),
                              SizedBox(height: screenHeight * 0.01),
                              Container(
                                width: double.infinity,
                                height: buttonHeight,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(29),
                                  color: Colors.white,
                                  border: Border.all(
                                    color: const Color(0xFF1C9A89),
                                    width: 2,
                                  ),
                                ),
                                child: TextField(
                                  controller: _passwordController,
                                  obscureText: _obscurePassword,
                                  style: TextStyle(fontSize: fontSize * 0.7),
                                  decoration: InputDecoration(
                                    hintText: 'Enter your password',
                                    hintStyle: TextStyle(
                                      fontSize: fontSize * 0.6,
                                      color: Colors.grey,
                                    ),
                                    border: InputBorder.none,
                                    contentPadding: EdgeInsets.symmetric(
                                      horizontal: screenWidth * 0.05,
                                      vertical: screenHeight * 0.02,
                                    ),
                                    suffixIcon: IconButton(
                                      icon: Icon(
                                        _obscurePassword
                                            ? Icons.visibility_off
                                            : Icons.visibility,
                                        color: const Color(0xFF1C9A89),
                                      ),
                                      onPressed: () {
                                        setState(() {
                                          _obscurePassword = !_obscurePassword;
                                        });
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.03),

                          // Confirm Password Field
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Confirm Password',
                                style: TextStyle(
                                  fontSize: fontSize,
                                  fontWeight: FontWeight.w400,
                                  color: const Color(0xFF1C9A89),
                                ),
                              ),
                              SizedBox(height: screenHeight * 0.01),
                              Container(
                                width: double.infinity,
                                height: buttonHeight,
                                decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(29),
                                  color: Colors.white,
                                  border: Border.all(
                                    color: const Color(0xFF1C9A89),
                                    width: 2,
                                  ),
                                ),
                                child: TextField(
                                  controller: _confirmPasswordController,
                                  obscureText: _obscureConfirmPassword,
                                  style: TextStyle(fontSize: fontSize * 0.7),
                                  decoration: InputDecoration(
                                    hintText: 'Confirm your password',
                                    hintStyle: TextStyle(
                                      fontSize: fontSize * 0.6,
                                      color: Colors.grey,
                                    ),
                                    border: InputBorder.none,
                                    contentPadding: EdgeInsets.symmetric(
                                      horizontal: screenWidth * 0.05,
                                      vertical: screenHeight * 0.02,
                                    ),
                                    suffixIcon: IconButton(
                                      icon: Icon(
                                        _obscureConfirmPassword
                                            ? Icons.visibility_off
                                            : Icons.visibility,
                                        color: const Color(0xFF1C9A89),
                                      ),
                                      onPressed: () {
                                        setState(() {
                                          _obscureConfirmPassword =
                                              !_obscureConfirmPassword;
                                        });
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          if (_errorMessage.isNotEmpty)
                            Padding(
                              padding: EdgeInsets.only(
                                top: screenHeight * 0.02,
                              ),
                              child: Text(
                                _errorMessage,
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: Colors.red,
                                ),
                              ),
                            ),

                          SizedBox(height: screenHeight * 0.04),

                          // Update Button
                          GestureDetector(
                            onTap: _isLoading ? null : _handleUpdate,
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
                                          color: Colors.white,
                                          strokeWidth: 2,
                                        ),
                                      )
                                    : Text(
                                        'Update',
                                        style: TextStyle(
                                          fontSize: fontSize,
                                          fontWeight: FontWeight.w600,
                                          color: Colors.white,
                                        ),
                                      ),
                              ),
                            ),
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          // Back Link
                          Center(
                            child: GestureDetector(
                              onTap: () {
                                Navigator.pop(context);
                              },
                              child: Text(
                                'Back',
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: const Color(0xFF1C9A89),
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
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

// Temporary Login Redirect Screen
class LoginScreenRedirect extends StatelessWidget {
  const LoginScreenRedirect({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        color: Colors.white,
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children:  [
              const Icon(
                Icons.check_circle,
                size: 80,
                color: Color(0xFF1C9A89),
              ),
              const SizedBox(height: 20),
              const Text(
                'Password Updated!',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.w600,
                  color: Color(0xFF1C9A89),
                ),
              ),
              const SizedBox(height: 10),
              const Text(
                'Redirecting to login...',
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
