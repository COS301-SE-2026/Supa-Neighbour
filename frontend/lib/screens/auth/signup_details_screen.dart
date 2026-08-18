import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';
import '../../constants/app_colors.dart';
import '../../constants/app_text_files.dart';
import '../../models/user_model.dart';
import 'signup_residential_screen.dart';

class SignupDetailsScreen extends StatefulWidget {
  final String email;
  final String idToken;
  final String password;
  final User? partialUser;
  
  const SignupDetailsScreen({
    super.key, 
    required this.email,
    required this.idToken,
    required this.password,
    this.partialUser,
  });

  @override
  State<SignupDetailsScreen> createState() => _SignupDetailsScreenState();
}

class _SignupDetailsScreenState extends State<SignupDetailsScreen> {
  final TextEditingController _firstNameController = TextEditingController();
  final TextEditingController _lastNameController = TextEditingController();
  DateTime _selectedDate = DateTime.now();
  String _selectedGender = 'Male';
  final bool _isLoading = false;

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    super.dispose();
  }

  User _buildUser() {
    return User(
      id: widget.partialUser?.id ?? DateTime.now().millisecondsSinceEpoch.toString(),
      email: widget.email,
      firstName: _firstNameController.text,
      lastName: _lastNameController.text,
      birthday: _selectedDate,
      gender: _selectedGender,
      createdAt: widget.partialUser?.createdAt ?? DateTime.now(),
    );
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime(1900),
      lastDate: DateTime.now(),
      builder: (context, child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: const ColorScheme.light(
              primary: Color(0xFF2A9D8F),
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  void _handleNext() {
    if (_firstNameController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your first name'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }
    if (_lastNameController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: const Text('Please enter your last name'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      return;
    }

    final user = _buildUser();

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => SignupResidentialScreen(
          user: user,
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

                  // Logo - Centered with Align
                  Center(
                    child: LogoPlaceholder(size: logoSize),
                  ),

                  SizedBox(height: largeSpacing),

                  // Title
                  Center(
                    child: Text(
                      'Complete Profile',
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
                      'Tell us about yourself',
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
                          // Email Display
                          Container(
                            width: double.infinity,
                            padding: EdgeInsets.all(spacing),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(15),
                              color: AppColors.surfaceGrey(context),
                            ),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Email',
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: smallFontSize,
                                    color: AppColors.primaryTeal(context),
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                                SizedBox(height: spacing * 0.3),
                                Text(
                                  widget.email,
                                  style: AppTextStyles.bodyText(context).copyWith(
                                    fontSize: fontSize,
                                    color: AppColors.charcoal(context),
                                  ),
                                ),
                              ],
                            ),
                          ),

                          SizedBox(height: spacing * 1.2),

                          // First Name Field
                          _buildTextField(
                            'First Name',
                            _firstNameController,
                            'Enter your first name',
                          ),

                          SizedBox(height: spacing),

                          // Last Name Field
                          _buildTextField(
                            'Last Name',
                            _lastNameController,
                            'Enter your last name',
                          ),

                          SizedBox(height: spacing),

                          // Birthday Field
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Birthday',
                                style: AppTextStyles.bodyText(context).copyWith(
                                  fontWeight: FontWeight.w500,
                                  color: AppColors.primaryTeal(context),
                                ),
                              ),
                              SizedBox(height: spacing * 0.3),
                              GestureDetector(
                                onTap: () => _selectDate(context),
                                child: Container(
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
                                  child: Padding(
                                    padding: EdgeInsets.symmetric(
                                      horizontal: screenWidth * 0.04,
                                    ),
                                    child: Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        Text(
                                          '${_selectedDate.toLocal()}'.split(' ')[0],
                                          style: AppTextStyles.bodyText(context).copyWith(
                                            color: AppColors.charcoal(context),
                                          ),
                                        ),
                                        Icon(
                                          Icons.calendar_today,
                                          color: AppColors.primaryTeal(context),
                                          size: fontSize * 0.8,
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: spacing),

                          // Gender Field (Dropdown)
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(
                                'Gender',
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
                                child: Padding(
                                  padding: EdgeInsets.symmetric(
                                    horizontal: screenWidth * 0.04,
                                  ),
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButton<String>(
                                      value: _selectedGender,
                                      isExpanded: true,
                                      icon: Icon(
                                        Icons.arrow_drop_down,
                                        color: AppColors.primaryTeal(context),
                                        size: fontSize,
                                      ),
                                      style: AppTextStyles.bodyText(context).copyWith(
                                        color: AppColors.charcoal(context),
                                      ),
                                      items: const [
                                        DropdownMenuItem(
                                          value: 'Male',
                                          child: Text('Male'),
                                        ),
                                        DropdownMenuItem(
                                          value: 'Female',
                                          child: Text('Female'),
                                        ),
                                        DropdownMenuItem(
                                          value: 'Other',
                                          child: Text('Other'),
                                        ),
                                        DropdownMenuItem(
                                          value: 'Prefer not to say',
                                          child: Text('Prefer not to say'),
                                        ),
                                      ],
                                      onChanged: (value) {
                                        setState(() {
                                          _selectedGender = value!;
                                        });
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: spacing * 2.5), // Was spacing * 1.5

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
}