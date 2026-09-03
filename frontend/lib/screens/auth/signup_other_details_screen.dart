import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import '../../models/auth_session.dart';
import '../../models/user_model.dart';
import '../home/home_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class SignupOtherDetailsScreen extends ConsumerStatefulWidget {
  final User user;
  final String idToken;
  final String password;

  const SignupOtherDetailsScreen({
    super.key,
    required this.user,
    required this.idToken,
    required this.password,
  });

  @override
  ConsumerState<SignupOtherDetailsScreen> createState() => _SignupOtherDetailsScreenState();
}

class _SignupOtherDetailsScreenState extends ConsumerState<SignupOtherDetailsScreen> {
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _usernameController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _phoneController.dispose();
    _usernameController.dispose();
    super.dispose();
  }

  Future<void> _handleFinish() async {
    if (_phoneController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your phone number'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }
    if (_usernameController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter a username'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    setState(() => _isLoading = true);

  try {
    final birthday = widget.user.birthday ?? DateTime.now();
    final dateOfBirth =
        '${birthday.year}-${birthday.month.toString().padLeft(2, '0')}-${birthday.day.toString().padLeft(2, '0')}';

    final int? zip = int.tryParse(widget.user.zipCode ?? '');
    if (widget.user.street == null || widget.user.town == null || zip == null) {
      throw Exception('Missing or invalid residential address details.');
    }
    final auth = ref.read(authServiceProvider);  
    final User registeredUser = await auth.register( 
      idToken: widget.idToken,
      firstName: widget.user.firstName,
      lastName: widget.user.lastName,
      password: widget.password,
      phoneNumber: _phoneController.text.trim(),
      dateOfBirth: dateOfBirth,
      gender: widget.user.gender ?? 'Other',
      username: _usernameController.text.trim(),
      street: widget.user.street!,
      town: widget.user.town!,
      zip: zip,
    );

    AuthSession.instance.login(registeredUser);

    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Profile completed successfully!'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
    );

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (context) => const HomeScreen()),
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
    
    final logoSize = (screenWidth * 0.15).clamp(80.0, 150.0);
    final titleSize = isSmallScreen ? 24.0 : (isLargeScreen ? 40.0 : 32.0);
    final subtitleSize = isSmallScreen ? 14.0 : (isLargeScreen ? 24.0 : 18.0);
    final buttonHeight = isSmallScreen ? 48.0 : 56.0;
    
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
                    // Back Button
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

                    // Logo
                    LogoPlaceholder(size: logoSize),

                    SizedBox(height: largeSpacing),

                    // Title
                    Text(
                      'Other Details',
                      style: AppTextStyles.primaryHeader(context).copyWith(
                        fontSize: titleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),

                    SizedBox(height: spacing * 0.5),

                    // Subtitle
                    Text(
                      'Almost there!',
                      style: AppTextStyles.secondaryHeader(context).copyWith(
                        fontSize: subtitleSize,
                        color: AppColors.textGrey(context),
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
                            // Phone Number Field
                            _buildTextField(
                              'Phone Number',
                              _phoneController,
                              'Enter your phone number',  // Updated placeholder
                              isNumber: true,
                            ),

                            SizedBox(height: spacing),

                            // Username Field
                            _buildTextField(
                              'Username',
                              _usernameController,
                              'Enter a unique username',  // Updated placeholder
                            ),

                            // Increased spacing
                            SizedBox(height: spacing * 2.5),

                            // Finish Button
                            GestureDetector(
                              onTap: _isLoading ? null : _handleFinish,
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
                                          'Finish Profile',
                                          style: AppTextStyles.buttonText(context).copyWith(
                                            fontSize: isSmallScreen ? 16.0 : 20.0,
                                          ),
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

  Widget _buildTextField(
    String label,
    TextEditingController controller,
    String hint, {
    bool isNumber = false,
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
            keyboardType: isNumber ? TextInputType.phone : TextInputType.text,
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
}