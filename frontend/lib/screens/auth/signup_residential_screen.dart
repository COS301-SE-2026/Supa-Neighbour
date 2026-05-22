import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';  // Add this import
import '../../models/user_model.dart';
import 'signup_other_details_screen.dart';

class SignupResidentialScreen extends StatefulWidget {
  final User user;
  
  const SignupResidentialScreen({
    super.key,
    required this.user,
  });

  @override
  State<SignupResidentialScreen> createState() => _SignupResidentialScreenState();
}

class _SignupResidentialScreenState extends State<SignupResidentialScreen> {
  final TextEditingController _streetController = TextEditingController();
  final TextEditingController _townController = TextEditingController();
  final TextEditingController _zipCodeController = TextEditingController();
  bool _isLoading = false;

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
        const SnackBar(
          content: Text('Please enter your street address'),
          backgroundColor: Color(0xFF1C9A89),
        ),
      );
      return;
    }

    if (_townController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please enter your town/city'),
          backgroundColor: Color(0xFF1C9A89),
        ),
      );
      return;
    }

    if (_zipCodeController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please enter your zip code'),
          backgroundColor: Color(0xFF1C9A89),
        ),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });

        final updatedUser = _buildUpdatedUser();

        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => SignupOtherDetailsScreen(
              user: updatedUser,
            ),
          ),
        );
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    const logoSize = 100.0;
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
                      onTap: () => Navigator.pop(context),
                      child: Container(
                        width: 50,
                        height: 50,
                        decoration: const BoxDecoration(
                          color: Color(0xFF1C9A89),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(Icons.arrow_back, color: Colors.white, size: 30),
                      ),
                    ),
                  ),
                  
                  SizedBox(height: screenHeight * 0.02),
                  
                  // Logo - Using LogoPlaceholder (replaced placeholder container)
                  LogoPlaceholder(size: logoSize),
                  
                  SizedBox(height: screenHeight * 0.03),
                  
                  Text(
                    'Residential Address',
                    style: TextStyle(fontSize: titleSize, fontWeight: FontWeight.w600, color: const Color(0xFF1C9A89)),
                    textAlign: TextAlign.center,
                  ),
                  
                  SizedBox(height: screenHeight * 0.01),
                  
                  Text(
                    'Where do you live?',
                    style: TextStyle(fontSize: subtitleSize, fontWeight: FontWeight.w400, color: Colors.grey[600]),
                    textAlign: TextAlign.center,
                  ),
                  
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
                          _buildTextField('Street', _streetController, 'e.g. 23 Prospect Street'),
                          SizedBox(height: screenHeight * 0.025),
                          _buildTextField('Town', _townController, 'e.g. Hatfield, Pretoria'),
                          SizedBox(height: screenHeight * 0.025),
                          _buildTextField('Zip Code', _zipCodeController, 'e.g. 0001', isNumber: true),
                          SizedBox(height: screenHeight * 0.04),
                          
                          GestureDetector(
                            onTap: _isLoading ? null : _handleNext,
                            child: Container(
                              width: double.infinity,
                              height: buttonHeight,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(29),
                                color: const Color(0xFF1C9A89),
                              ),
                              child: Center(
                                child: _isLoading
                                    ? SizedBox(width: buttonHeight * 0.4, height: buttonHeight * 0.4, child: const CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                                    : Text('Next', style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.w600, color: Colors.white)),
                              ),
                            ),
                          ),
                          
                          SizedBox(height: screenHeight * 0.02),
                          
                          Center(
                            child: GestureDetector(
                              onTap: () => Navigator.pop(context),
                              child: Text('Back', style: TextStyle(fontSize: smallFontSize, color: const Color(0xFF1C9A89), fontWeight: FontWeight.w500)),
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

  Widget _buildTextField(String label, TextEditingController controller, String hint, {bool isNumber = false}) {
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
          width: double.infinity,
          height: buttonHeight,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(29),
            color: Colors.white,
            border: Border.all(color: const Color(0xFF1C9A89), width: 2),
          ),
          child: TextField(
            controller: controller,
            style: TextStyle(fontSize: fontSize * 0.7),
            keyboardType: isNumber ? TextInputType.number : TextInputType.text,
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
}