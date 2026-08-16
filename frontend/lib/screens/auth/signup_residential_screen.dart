import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import '../../models/user_model.dart';
import 'signup_other_details_screen.dart';

class SignupResidentialScreen extends StatefulWidget {
  final User user;
  final String idToken;  
  final String password;  

  const SignupResidentialScreen({
    super.key,
    required this.user,
    required this.idToken,
    required this.password,
  });

  @override
  State<SignupResidentialScreen> createState() => _SignupResidentialScreenState();
}

class _SignupResidentialScreenState extends State<SignupResidentialScreen> {
  final TextEditingController _streetController = TextEditingController();
  final TextEditingController _townController = TextEditingController();
  final TextEditingController _zipCodeController = TextEditingController();
  final bool _isLoading = false;

  @override
  void dispose() {
    _streetController.dispose();
    _townController.dispose();
    _zipCodeController.dispose();
    super.dispose();
  }

  User _buildUpdatedUser() {
    return widget.user.copyWith(
      street: _streetController.text,
      town: _townController.text,
      zipCode: _zipCodeController.text,
    );
  }

  void _handleNext() {
    if (_streetController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your street address'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }
    if (_townController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your town/city'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }
    if (_zipCodeController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your zip code'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    final updatedUser = _buildUpdatedUser();

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => SignupOtherDetailsScreen(
          user: updatedUser,
          idToken: widget.idToken,  
          password: widget.password, 
        ),
      ),
    );
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
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: screenWidth * 0.06),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  SizedBox(height: screenHeight * 0.02),

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

                  // Logo - Centered
                  Center(
                    child: LogoPlaceholder(size: logoSize),
                  ),

                  SizedBox(height: largeSpacing),

                  // Title
                  Center(
                    child: Text(
                      'Residential Address',
                      style: AppTextStyles.primaryHeader(context).copyWith(
                        fontSize: titleSize,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),

                  SizedBox(height: spacing * 0.5),

                  // Subtitle
                  Center(
                    child: Text(
                      'Where do you live?',
                      style: AppTextStyles.secondaryHeader(context).copyWith(
                        fontSize: subtitleSize,
                        color: AppColors.textGrey(context),
                      ),
                      textAlign: TextAlign.center,
                    ),
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
                          // Street Field
                          _buildTextField(
                            'Street',
                            _streetController,
                            'Enter your street address',  // Updated placeholder
                          ),

                          SizedBox(height: spacing),

                          // Town Field
                          _buildTextField(
                            'Town',
                            _townController,
                            'Enter your town',  // Updated placeholder
                          ),

                          SizedBox(height: spacing),

                          // Zip Code Field
                          _buildTextField(
                            'Zip Code',
                            _zipCodeController,
                            '',  // Empty placeholder
                            isNumber: true,
                          ),

                          SizedBox(height: spacing * 2.5),

                          // Next Button
                          GestureDetector(
                            onTap: _isLoading ? null : _handleNext,
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
                                        'Next',
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

                  SizedBox(height: spacing * 2),
                ],
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
            keyboardType: isNumber ? TextInputType.number : TextInputType.text,
            cursorColor: AppColors.primaryTeal(context),
            decoration: InputDecoration(
              hintText: hint,
              hintStyle: hint.isEmpty
                  ? null
                  : AppTextStyles.bodyText(context).copyWith(
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