import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import 'signup_otp_screen.dart';
import 'login_screen.dart';

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

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  Future<void> _handleSignup() async {
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
    if (_passwordController.text != _confirmPasswordController.text) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Passwords do not match'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      final fb.UserCredential credential = await fb.FirebaseAuth.instance
          .createUserWithEmailAndPassword(
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );

      final String? idToken = await credential.user?.getIdToken();

      if (idToken == null) {
        throw Exception('Could not get Firebase token after signup.');
      }

      if (!mounted) return;

      await credential.user?.sendEmailVerification();
      
      if (!mounted) return;

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

    // Responsive sizing
    final isSmallScreen = screenWidth < 400;
    final isLargeScreen = screenWidth > 800;
    
    final logoSize = (screenWidth * 0.3).clamp(80.0, 180.0);
    final titleSize = isSmallScreen ? 24.0 : (isLargeScreen ? 40.0 : 32.0);
    final subtitleSize = isSmallScreen ? 14.0 : (isLargeScreen ? 24.0 : 18.0);
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
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
                            // Sign Up Title
                            Text(
                              'Sign Up',
                              style: AppTextStyles.primaryHeader(context).copyWith(
                                fontSize: isSmallScreen ? 20.0 : 24.0,
                                fontWeight: FontWeight.w600,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),

                            SizedBox(height: spacing * 1.2),

                            // Email Field
                            _buildField(
                              'Email',
                              _emailController,
                              'Enter your email',
                              keyboardType: TextInputType.emailAddress,
                            ),

                            SizedBox(height: spacing),

                            // Password Field
                            _buildPasswordField(
                              'Password',
                              _passwordController,
                              _obscurePassword,
                              () => setState(() => _obscurePassword = !_obscurePassword),
                            ),

                            SizedBox(height: spacing),

                            // Confirm Password Field
                            _buildPasswordField(
                              'Confirm password',
                              _confirmPasswordController,
                              _obscureConfirmPassword,
                              () => setState(() => _obscureConfirmPassword = !_obscureConfirmPassword),
                            ),

                            SizedBox(height: spacing * 1.5),

                            // Sign Up Button
                            GestureDetector(
                              onTap: _isLoading ? null : _handleSignup,
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
                                          'Sign up',
                                          style: AppTextStyles.buttonText(context).copyWith(
                                            fontSize: isSmallScreen ? 16.0 : 20.0,
                                          ),
                                        ),
                                ),
                              ),
                            ),

                            SizedBox(height: spacing),

                            // Already a neighbour? Login - Navigates to LoginScreen
                            Wrap(
                              alignment: WrapAlignment.center,
                              crossAxisAlignment: WrapCrossAlignment.center,
                              children: [
                                Text(
                                  'Already a neighbour? ',
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: smallFontSize,
                                    color: AppColors.primaryTeal(context),
                                  ),
                                ),
                                GestureDetector(
                                  onTap: () {
                                    Navigator.pushReplacement(
                                      context,
                                      MaterialPageRoute(
                                        builder: (context) => const LoginScreen(),
                                      ),
                                    );
                                  },
                                  child: Text(
                                    'Login',
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

  Widget _buildField(
    String label,
    TextEditingController controller,
    String hint, {
    TextInputType keyboardType = TextInputType.text,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final isSmallScreen = screenWidth < 400;
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
    final spacing = screenHeight * 0.015;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
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
            controller: controller,
            style: AppTextStyles.bodyText(context).copyWith(
              color: AppColors.charcoal(context),
            ),
            keyboardType: keyboardType,
            cursorColor: AppColors.primaryTeal(context),
            decoration: InputDecoration(
              hintText: hint,
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
    );
  }

  Widget _buildPasswordField(
    String label,
    TextEditingController controller,
    bool obscure,
    VoidCallback toggle,
  ) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;
    final isSmallScreen = screenWidth < 400;
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
    final spacing = screenHeight * 0.015;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
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
            controller: controller,
            obscureText: obscure,
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
              suffixIcon: IconButton(
                icon: Icon(
                  obscure ? Icons.visibility_off : Icons.visibility,
                  color: AppColors.primaryTeal(context),
                ),
                onPressed: toggle,
              ),
            ),
          ),
        ),
      ],
    );
  }
}