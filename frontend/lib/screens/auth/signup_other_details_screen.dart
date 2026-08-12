import 'package:flutter/material.dart';
import '../../components/logo_placeholder.dart';
import '../../models/auth_session.dart';
import '../../models/user_model.dart';
import '../../services/auth_service.dart';
import '../home/home_screen.dart';


class SignupOtherDetailsScreen extends StatefulWidget {
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
  State<SignupOtherDetailsScreen> createState() => _SignupOtherDetailsScreenState();
}


class _SignupOtherDetailsScreenState extends State<SignupOtherDetailsScreen> {
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _usernameController = TextEditingController();
  bool _isLoading = false;
  final AuthService _authService = AuthService();

 Future<void> _handleFinish() async {
  if (_phoneController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter your phone number'), backgroundColor: Color(0xFF1C9A89)),
    );
    return;
  }
  if (_usernameController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter a username'), backgroundColor: Color(0xFF1C9A89)),
    );
    return;
  }

  setState(() => _isLoading = true);

  try {
    final birthday = widget.user.birthday ?? DateTime.now();
    final dateOfBirth =
        '${birthday.year}-${birthday.month.toString().padLeft(2, '0')}-${birthday.day.toString().padLeft(2, '0')}';

    final int? zip = int.tryParse(widget.user.zipCode ?? '');
    if(widget.user.street == null || widget.user.town == null || zip == null){
      throw Exception(' Missing or invalis residential address details.');
    }
    final User registeredUser = await _authService.register(
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
      const SnackBar(content: Text('Profile completed successfully!'), backgroundColor: Color(0xFF1C9A89)),
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
                        decoration: const BoxDecoration(color: Color(0xFF1C9A89), shape: BoxShape.circle),
                        child: const Icon(Icons.arrow_back, color: Colors.white, size: 30),
                      ),
                    ),
                  ),
                  
                  SizedBox(height: screenHeight * 0.02),
                  
                  // Logo - Using LogoPlaceholder (replaced placeholder container)
                  LogoPlaceholder(size: logoSize),
                  
                  SizedBox(height: screenHeight * 0.03),
                  
                  Text(
                    'Other Details', 
                    style: TextStyle(fontSize: titleSize, fontWeight: FontWeight.w600, color: const Color(0xFF1C9A89)), 
                    textAlign: TextAlign.center
                  ),
                  
                  SizedBox(height: screenHeight * 0.01),
                  
                  Text(
                    'Almost there!', 
                    style: TextStyle(fontSize: subtitleSize, fontWeight: FontWeight.w400, color: Colors.grey[600]), 
                    textAlign: TextAlign.center
                  ),
                  
                  SizedBox(height: screenHeight * 0.04),
                  
                  // White Card Container
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
                          _buildTextField('Phone Number', _phoneController, 'e.g. 012 345 6789', isNumber: true),
                          SizedBox(height: screenHeight * 0.025),
                          _buildTextField('Username', _usernameController, 'e.g. user123'),
                          SizedBox(height: screenHeight * 0.04),
                          
                          // Finish Button
                          GestureDetector(
                            onTap: _isLoading ? null : _handleFinish,
                            child: Container(
                              width: double.infinity,
                              height: buttonHeight,
                              decoration: BoxDecoration(borderRadius: BorderRadius.circular(29), color: const Color(0xFF1C9A89)),
                              child: Center(
                                child: _isLoading
                                    ? SizedBox(width: buttonHeight * 0.4, height: buttonHeight * 0.4, child: const CircularProgressIndicator(color: Colors.white, strokeWidth: 2))
                                    : Text('Finish Profile', style: TextStyle(fontSize: fontSize, fontWeight: FontWeight.w600, color: Colors.white)),
                              ),
                            ),
                          ),
                          
                          SizedBox(height: screenHeight * 0.02),
                          
                          // Back Link
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
            keyboardType: isNumber ? TextInputType.phone : TextInputType.text,
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