import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart'; // Keep this import
import 'signup_otp_screen.dart';

class SignupScreen extends StatefulWidget {
  const SignupScreen({super.key});

  @override
  State<SignupScreen> createState() => _SignupScreenState();
}

class _SignupScreenState extends State<SignupScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController = TextEditingController();
  bool _isLoading = false;
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;


  Future<void> _handleSignup() async {
    if (_emailController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please enter your email'), backgroundColor: Color(0xFF1C9A89)),
      );
      return;
    }
    if (_passwordController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please enter your password'), backgroundColor: Color(0xFF1C9A89)),
      );
      return;
    }
    if (_passwordController.text != _confirmPasswordController.text) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Passwords do not match'), backgroundColor: Color(0xFF1C9A89)),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      // create the fb acc
      final fb.UserCredential credential = await fb.FirebaseAuth.instance
          .createUserWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );

      // get the fb id token
      final String? idToken = await credential.user?.getIdToken();

      if (idToken == null) {
        throw Exception('Could not get Firebase token after signup.');
      }

      if (!mounted) return;


      await credential.user?.sendEmailVerification();
      
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => SignupOtpScreen(
            email: _emailController.text.trim(),
            idToken: idToken,
            password: _passwordController.text,
          ),
        ),
      );
    } on fb.FirebaseAuthException catch (e) {
      String message;
      switch (e.code) {
        case 'email-already-in-use':
          message = 'An account already exists for this email.';
          break;
        case 'weak-password':
          message = 'Password must be at least 6 characters.';
          break;
        case 'invalid-email':
          message = 'Please enter a valid email address.';
          break;
        default:
          message = 'Sign up failed. Please try again.';
      }
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message), backgroundColor: Colors.red),
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
                        width: 50, height: 50,
                        decoration: const BoxDecoration(color: Color(0xFF1C9A89), shape: BoxShape.circle),
                        child: const Icon(Icons.arrow_back, color: Colors.white, size: 30),
                      ),
                    ),
                  ),
                  SizedBox(height: screenHeight * 0.02),
                  LogoPlaceholder(size: logoSize),
                  SizedBox(height: screenHeight * 0.03),
                  Text('super Neighbour', style: TextStyle(fontSize: titleSize, fontWeight: FontWeight.w600, color: const Color(0xFF1C9A89)), textAlign: TextAlign.center),
                  SizedBox(height: screenHeight * 0.01),
                  Text('Your neighbourly helper', style: TextStyle(fontSize: subtitleSize, fontWeight: FontWeight.w600, color: const Color(0xFF1C9A89)), textAlign: TextAlign.center),
                  SizedBox(height: screenHeight * 0.04),
                  Container(
                    width: double.infinity,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(30),
                      boxShadow: [BoxShadow(color: Colors.grey.withValues(alpha: 0.2), blurRadius: 20, offset: const Offset(0, 10))],
                    ),
                    child: Padding(
                      padding: EdgeInsets.all(screenWidth * 0.07),
                      child: Column(
                        children: [
                          Text('Sign Up', style: TextStyle(fontSize: titleSize * 0.8, fontWeight: FontWeight.w600, color: const Color(0xFF1C9A89))),
                          SizedBox(height: screenHeight * 0.03),
                          _buildField('Email', _emailController, 'Enter your email', keyboardType: TextInputType.emailAddress),
                          SizedBox(height: screenHeight * 0.025),
                          _buildPasswordField('Password', _passwordController, _obscurePassword, () => setState(() => _obscurePassword = !_obscurePassword)),
                          SizedBox(height: screenHeight * 0.025),
                          _buildPasswordField('Confirm password', _confirmPasswordController, _obscureConfirmPassword, () => setState(() => _obscureConfirmPassword = !_obscureConfirmPassword)),
                          SizedBox(height: screenHeight * 0.04),
                          GestureDetector(
                            onTap: _isLoading ? null : _handleSignup,
                            child: Container(
                              width: double.infinity, height: buttonHeight,
                              decoration: BoxDecoration(borderRadius: BorderRadius.circular(29), color: const Color(0xFF1C9A89)),
                              child: Center(
                                child: _isLoading
                                    ? SizedBox(width: buttonHeight * 0.4, height: buttonHeight * 0.4, child: const CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                                    : Text('Sign up', style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.w600, color: Colors.white)),
                              ),
                            ),
                          ),
                          SizedBox(height: screenHeight * 0.02),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text('Already a neighbour? ', style: TextStyle(fontSize: smallFontSize, color: const Color(0xFF1C9A89))),
                              GestureDetector(
                                onTap: () => Navigator.pop(context),
                                child: Text('Login', style: TextStyle(fontSize: smallFontSize, color: const Color(0xFF1C9A89), fontWeight: FontWeight.w600)),
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

  Widget _buildField(String label, TextEditingController controller, String hint, {TextInputType keyboardType = TextInputType.text}) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final fontSize = screenWidth * 0.04;
    final buttonHeight = screenHeight * 0.07;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.w400, color: const Color(0xFF1C9A89))),
        SizedBox(height: screenHeight * 0.01),
        Container(
          width: double.infinity, height: buttonHeight,
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(29), color: Colors.white, border: Border.all(color: const Color(0xFF1C9A89), width: 2)),
          child: TextField(
            controller: controller,
            style: TextStyle(fontSize: fontSize * 0.7),
            keyboardType: keyboardType,
            decoration: InputDecoration(
              hintText: hint,
              hintStyle: TextStyle(fontSize: fontSize * 0.6, color: Colors.grey),
              border: InputBorder.none,
              contentPadding: EdgeInsets.symmetric(horizontal: screenWidth * 0.05, vertical: screenHeight * 0.02),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildPasswordField(String label, TextEditingController controller, bool obscure, VoidCallback toggle) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final fontSize = screenWidth * 0.04;
    final buttonHeight = screenHeight * 0.07;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.w400, color: const Color(0xFF1C9A89))),
        SizedBox(height: screenHeight * 0.01),
        Container(
          width: double.infinity, height: buttonHeight,
          decoration: BoxDecoration(borderRadius: BorderRadius.circular(29), color: Colors.white, border: Border.all(color: const Color(0xFF1C9A89), width: 2)),
          child: TextField(
            controller: controller,
            obscureText: obscure,
            style: TextStyle(fontSize: fontSize * 0.7),
            decoration: InputDecoration(
              hintText: 'Enter your password',
              hintStyle: TextStyle(fontSize: fontSize * 0.6, color: Colors.grey),
              border: InputBorder.none,
              contentPadding: EdgeInsets.symmetric(horizontal: screenWidth * 0.05, vertical: screenHeight * 0.02),
              suffixIcon: IconButton(icon: Icon(obscure ? Icons.visibility_off : Icons.visibility, color: const Color(0xFF1C9A89)), onPressed: toggle),
            ),
          ),
        ),
      ],
    );
  }
}
