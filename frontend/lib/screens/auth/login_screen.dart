import 'package:flutter/material.dart';
import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' show FirebaseAuthException;
import 'package:shared_preferences/shared_preferences.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import '../../models/auth_session.dart';
import '../../models/user_model.dart';
import '../home/home_screen.dart';
import 'forgot_password_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';





class LoginScreen extends ConsumerStatefulWidget {
  const LoginScreen({super.key});

  @override
  ConsumerState<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends ConsumerState<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _isLoading = false;
  bool _rememberMe = false;
  
  


  Future<void> _handleLogin() async {
    if (_emailController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your email'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }
    if (_passwordController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your password'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    setState(() => _isLoading = true);

  try {
    final auth = ref.read(authServiceProvider);  
    final User user = await auth.login( 
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
        SnackBar(
          content: const Text('Login successful!'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (context) => const HomeScreen()),
      );
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
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  Future<int?> _fetchDependentId(int userId) async {
    try {
    final dio = Dio(BaseOptions(baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net'));
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
    final isSmallScreen = screenWidth < 400;
    final isLargeScreen = screenWidth > 800;
    
    final logoSize = (screenWidth * 0.3).clamp(80.0, 180.0);
    final titleSize = isSmallScreen ? 28.0 : (isLargeScreen ? 40.0 : 32.0);
    final subtitleSize = isSmallScreen ? 16.0 : (isLargeScreen ? 24.0 : 18.0);
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
    final smallFontSize = isSmallScreen ? 12.0 : (isLargeScreen ? 16.0 : 14.0);
    
    // Spacing - responsive to screen size
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
                  mainAxisAlignment: MainAxisAlignment.center, // ← Centers vertically
                  children: [
                    // Logo
                    LogoPlaceholder(size: logoSize),

                    SizedBox(height: largeSpacing),

                    // Title
                    Text(
                      'Super Neighbour',
                      style: AppTextStyles.primaryHeader(context).copyWith(
                        fontSize: titleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    SizedBox(height: spacing * 0.5),

                    // Subtitle
                    Text(
                      'Your neighbourly helper',
                      style: AppTextStyles.secondaryHeader(context).copyWith(
                        fontSize: subtitleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    SizedBox(height: largeSpacing * 0.8),

                    // Card Container
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
                            // Login Title
                            Text(
                              'Login',
                              style: AppTextStyles.primaryHeader(context).copyWith(
                                fontSize: isSmallScreen ? 20.0 : 24.0,
                                fontWeight: FontWeight.w600,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),

                            SizedBox(height: spacing * 1.2),

                            // Email Field
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Email',
                                  style: AppTextStyles.bodyText(context).copyWith(
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

                            SizedBox(height: spacing * 1.2),

                            // Password Field
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Password',
                                  style: AppTextStyles.bodyText(context).copyWith(
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
                                    controller: _passwordController,
                                    obscureText: true,
                                    style: AppTextStyles.bodyText(context).copyWith(
                                      color: AppColors.charcoal(context),
                                    ),
                                    cursorColor: AppColors.primaryTeal(context),
                                    decoration: InputDecoration(
                                      hintText: 'Enter your password',
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

                            SizedBox(height: spacing * 0.8),

                            // Remember Me & Forgot Password Row
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Expanded(
                                  child: Row(
                                    children: [
                                      Checkbox(
                                        value: _rememberMe,
                                        onChanged: (value) {
                                          setState(() {
                                            _rememberMe = value ?? false;
                                          });
                                        },
                                        activeColor: AppColors.primaryTeal(context),
                                        side: BorderSide(color: AppColors.primaryTeal(context)),
                                        visualDensity: VisualDensity.compact,
                                        materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                                      ),
                                      Flexible(
                                        child: Text(
                                          'Remember me',
                                          style: AppTextStyles.bodyText(context).copyWith(
                                            fontSize: smallFontSize,
                                            color: AppColors.primaryTeal(context),
                                          ),
                                          overflow: TextOverflow.ellipsis,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                                Flexible(
                                  child: GestureDetector(
                                    onTap: () {
                                      Navigator.push(
                                        context,
                                        MaterialPageRoute(builder: (context) => const ForgotPasswordScreen()),
                                      );
                                    },
                                    child: Text(
                                      'Forgot Password?',
                                      style: AppTextStyles.bodyText(context).copyWith(
                                        fontSize: smallFontSize,
                                        color: AppColors.primaryTeal(context),
                                      ),
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                ),
                              ],
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Login Button
                            GestureDetector(
                              onTap: _isLoading ? null : _handleLogin,
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
                                          'Login',
                                          style: AppTextStyles.buttonText(context).copyWith(
                                            fontSize: isSmallScreen ? 16.0 : 20.0,
                                          ),
                                        ),
                                ),
                              ),
                            ),

                            SizedBox(height: spacing * 1.2),

                            // Sign Up Link
                            Wrap(
                              alignment: WrapAlignment.center,
                              crossAxisAlignment: WrapCrossAlignment.center,
                              children: [
                                Text(
                                  'Don\'t have an account? ',
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: smallFontSize,
                                    color: AppColors.primaryTeal(context),
                                  ),
                                ),
                                GestureDetector(
                                  onTap: () {
                                    Navigator.pushReplacement(
                                      context,
                                      MaterialPageRoute(builder: (context) => const SignupScreen()),
                                    );
                                  },
                                  child: Text(
                                    'Sign up',
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