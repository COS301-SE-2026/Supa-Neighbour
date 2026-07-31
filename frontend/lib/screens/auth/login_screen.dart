import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' show FirebaseAuthException;
import '../../components/logo_placeholder.dart';
import '../../models/auth_session.dart';
import '../../models/user_model.dart';
import '../../services/auth_service.dart';
import '../home/home_screen.dart';
import 'forgot_password_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';





class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;
  bool _rememberMe = false;
  
  final AuthService _authService = AuthService();


Future<void> _handleLogin() async {
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

  setState(() => _isLoading = true);

  try {
    final User user = await _authService.login(
      _emailController.text.trim(),
      _passwordController.text,
    );
    AuthSession.instance.login(user);

    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('remember_me', _rememberMe);
    await prefs.setInt('current_user_id', int.parse(user.id));
    final dependentId = await _fetchDependentId(int.parse(user.id));
    if (dependentId != null) {
      await prefs.setInt('current_dependent_id', dependentId);
    }

    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Login successful!'), backgroundColor: Color(0xFF1C9A89)),
    );
    Navigator.pushReplacement(context,
        MaterialPageRoute(builder: (context) => const HomeScreen()));
  } on FirebaseAuthException catch (e) {
    String message;
    switch (e.code) {
      case 'user-not-found':
        message = 'No account found for this email.';
        break;
      case 'wrong-password':
      case 'invalid-credential':
        message = 'Incorrect email or password.';
        break;
      case 'too-many-requests':
        message = 'Too many attempts. Try again later.';
        break;
      default:
        message = 'Login failed. Please try again.';
    }
    if (!mounted) return;
    ScaffoldMessenger.of(context)
        .showSnackBar(SnackBar(content: Text(message), backgroundColor: Colors.red));
  } on Exception catch (e) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
          content: Text(e.toString().replaceAll('Exception: ', '')),
          backgroundColor: Colors.red),
    );
  } finally {
    if (mounted) setState(() => _isLoading = false);
  }
}

Future<int?> _fetchDependentId(int userId) async {
  try {
    final dio = Dio(BaseOptions(baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net/'));
    final res = await dio.get('/api/dependents');
    final list = res.data as List<dynamic>;
    for (final item in list) {
      if (item['userId'] == userId || item['userId']?['userid'] == userId) {
        return item['dependentId'] as int?;
      }
    }
    return null;
  } catch (_) {
    return null;
  }
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
    final fontSize = screenWidth * 0.045;
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
                  SizedBox(height: screenHeight * 0.05),

                  // Logo - Using LogoPlaceholder (single, no duplicate)
                  LogoPlaceholder(size: logoSize),

                  SizedBox(height: screenHeight * 0.04),

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

                  SizedBox(height: screenHeight * 0.05),

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
                          // Login Title
                          Text(
                            'Login',
                            style: TextStyle(
                              fontSize: titleSize * 0.8,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                          ),

                          SizedBox(height: screenHeight * 0.03),

                          // Email Field
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Email',
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
                                  controller: _emailController,
                                  style: TextStyle(fontSize: fontSize * 0.7),
                                  keyboardType: TextInputType.emailAddress,
                                  decoration: InputDecoration(
                                    hintText: 'Enter your email',
                                    hintStyle: TextStyle(
                                      fontSize: fontSize * 0.6,
                                      color: Colors.grey,
                                    ),
                                    border: InputBorder.none,
                                    contentPadding: EdgeInsets.symmetric(
                                      horizontal: screenWidth * 0.05,
                                      vertical: screenHeight * 0.02,
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.03),

                          // Password Field
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
                                  obscureText: true,
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
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          // Remember Me & Forgot Password Row
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Row(
                                children: [
                                  Checkbox(
                                    value: _rememberMe,
                                    onChanged: (value) {
                                      setState(() {
                                        _rememberMe = value ?? false;
                                      });
                                    },
                                    activeColor: const Color(0xFF1C9A89),
                                    side: const BorderSide(color: Color(0xFF1C9A89)),
                                  ),
                                  Text(
                                    'Remember me',
                                    style: TextStyle(
                                      fontSize: smallFontSize,
                                      color: const Color(0xFF1C9A89),
                                    ),
                                  ),
                                ],
                              ),
                              GestureDetector(
                                onTap: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(builder: (context) => const ForgotPasswordScreen()),
                                  );
                                },
                                child: Text(
                                  'Forgot Password?',
                                  style: TextStyle(
                                    fontSize: smallFontSize,
                                    color: const Color(0xFF1C9A89),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // Login Button
                          GestureDetector(
                            onTap: _isLoading ? null : _handleLogin,
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
                                        'Login',
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

                          // Sign Up Link
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'Don\'t have an account? ',
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: const Color(0xFF1C9A89),
                                ),
                              ),
                              GestureDetector(
                                onTap: () {
                                  Navigator.pop(context);
                                },
                                child: Text(
                                  'Sign up',
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