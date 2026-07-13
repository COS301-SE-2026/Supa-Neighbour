import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';  // Add this import
import 'signup_details_screen.dart';

class SignupOtpScreen extends StatefulWidget {
  final String email;
  final String idToken;   
  final String password;  

  const SignupOtpScreen({
    super.key,
    required this.email,
    required this.idToken,
    required this.password,
  });

  @override
  State<SignupOtpScreen> createState() => _SignupOtpScreenState();
}


class _SignupOtpScreenState extends State<SignupOtpScreen> {
  final List<TextEditingController> _otpControllers = List.generate(6, (index) => TextEditingController());
  final List<FocusNode> _focusNodes = List.generate(6, (index) => FocusNode());
  bool _isLoading = false;
  String _errorMessage = '';

  void _handleVerify() {
  String otp = _otpControllers.map((c) => c.text).join();

  if (otp.length != 6) {
    setState(() => _errorMessage = 'Please enter the 6-digit OTP');//any will just pass
    return;
  }

  // will add real verification later (check if page is complete)
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (context) => SignupDetailsScreen(
        email: widget.email,
        idToken: widget.idToken,   
        password: widget.password, 
      ),
    ),
  );
}


  void _handleResendOTP() {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('OTP resent to ${widget.email}'),
        backgroundColor: const Color(0xFF1C9A89),
      ),
    );
  }

void _handleNext() {
  if (_firstNameController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter your first name'), backgroundColor: Color(0xFF1C9A89)),
    );
    return;
  }
  if (_lastNameController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter your last name'), backgroundColor: Color(0xFF1C9A89)),
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
  void dispose() {
    for (var controller in _otpControllers) {
      controller.dispose();
    }
    for (var focusNode in _focusNodes) {
      focusNode.dispose();
    }
    super.dispose();
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
    final otpBoxSize = screenWidth * 0.12;

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

                  // Logo - Using LogoPlaceholder (replaced placeholder container)
                  LogoPlaceholder(size: logoSize),

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
                          // OTP Title
                          Text(
                            'Verify Email',
                            style: TextStyle(
                              fontSize: titleSize * 0.8,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                          ),

                          SizedBox(height: screenHeight * 0.02),

                          // Instruction Text
                          Text(
                            'Enter the OTP sent to',
                            style: TextStyle(
                              fontSize: fontSize,
                              fontWeight: FontWeight.w400,
                              color: const Color(0xFF1C9A89),
                            ),
                            textAlign: TextAlign.center,
                          ),
                          Text(
                            widget.email,
                            style: TextStyle(
                              fontSize: fontSize,
                              fontWeight: FontWeight.w600,
                              color: const Color(0xFF1C9A89),
                            ),
                            textAlign: TextAlign.center,
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // OTP Input Fields (6 boxes)
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: List.generate(6, (index) {
                              return SizedBox(
                                width: otpBoxSize,
                                height: otpBoxSize,
                                child: TextField(
                                  controller: _otpControllers[index],
                                  focusNode: _focusNodes[index],
                                  textAlign: TextAlign.center,
                                  keyboardType: TextInputType.number,
                                  maxLength: 1,
                                  style: TextStyle(
                                    fontSize: fontSize * 1.2,
                                    fontWeight: FontWeight.w600,
                                    color: const Color(0xFF1C9A89),
                                  ),
                                  decoration: InputDecoration(
                                    counterText: '',
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(15),
                                      borderSide: const BorderSide(color: Color(0xFF1C9A89), width: 2),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(15),
                                      borderSide: const BorderSide(color: Color(0xFF1C9A89), width: 2),
                                    ),
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(15),
                                      borderSide: const BorderSide(color: Color(0xFF1C9A89), width: 2),
                                    ),
                                    contentPadding: EdgeInsets.zero,
                                  ),
                                  onChanged: (value) {
                                    if (value.length == 1 && index < 5) {
                                      FocusScope.of(context).nextFocus();
                                    } else if (value.isEmpty && index > 0) {
                                      FocusScope.of(context).previousFocus();
                                    }
                                  },
                                ),
                              );
                            }),
                          ),

                          if (_errorMessage.isNotEmpty)
                            Padding(
                              padding: EdgeInsets.only(top: screenHeight * 0.02),
                              child: Text(
                                _errorMessage,
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: Colors.red,
                                ),
                              ),
                            ),

                          SizedBox(height: screenHeight * 0.03),

                          // Resend OTP Row
                          Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Text(
                                'Didn\'t receive the code? ',
                                style: TextStyle(
                                  fontSize: smallFontSize,
                                  color: const Color(0xFF1C9A89),
                                ),
                              ),
                              GestureDetector(
                                onTap: _handleResendOTP,
                                child: Text(
                                  'Resend OTP',
                                  style: TextStyle(
                                    fontSize: smallFontSize,
                                    color: const Color(0xFF1C9A89),
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                              ),
                            ],
                          ),

                          SizedBox(height: screenHeight * 0.04),

                          // Verify Button
                          GestureDetector(
                            onTap: _isLoading ? null : _handleVerify,
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
                                        'Verify',
                                        style: TextStyle(
                                          fontSize: fontSize,
                                          fontWeight: FontWeight.w600,
                                          color: Colors.white,
                                        ),
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