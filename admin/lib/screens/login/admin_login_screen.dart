// admin/lib/screens/login/admin_login_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';

class AdminLoginScreen extends ConsumerStatefulWidget {
  const AdminLoginScreen({super.key});

  @override
  ConsumerState<AdminLoginScreen> createState() => _AdminLoginScreenState();
}

class _AdminLoginScreenState extends ConsumerState<AdminLoginScreen> {
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  final bool _isLoading = false;
  bool _obscurePassword = true;
  String? _errorMessage;

  //MOCK LOGIN - Bypass for development
  void _mockLogin() {
    context.go('/dashboard');
  }

  Future<void> _login() async {
    //TEMPORARY: Use mock login for development
    _mockLogin();
    return;

    // REAL LOGIN CODE (will be uncommented later)
    // if (_emailController.text.isEmpty || _passwordController.text.isEmpty) {
    //   setState(() {
    //     _errorMessage = 'Please enter both email and password';
    //   });
    //   return;
    // }
    //
    // setState(() {
    //   _isLoading = true;
    //   _errorMessage = null;
    // });
    //
    // try {
    //   final authService = AuthService();
    //   await authService.adminLogin(
    //     _emailController.text.trim(),
    //     _passwordController.text,
    //   );
    //   
    //   if (mounted) {
    //     context.go('/dashboard');
    //   }
    // } catch (e) {
    //   setState(() {
    //     _errorMessage = e.toString().replaceAll('Exception: ', '');
    //     _isLoading = false;
    //   });
    // }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Container(
          constraints: const BoxConstraints(maxWidth: 400),
          padding: const EdgeInsets.all(24),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(
                'assets/images/Logo.png',
                height: 80,
                errorBuilder: (context, error, stackTrace) {
                  // Fallback if image not found
                  return const Icon(
                    Icons.admin_panel_settings,
                    size: 64,
                    color: AppColors.primaryTeal,
                  );
                },
              ),
              const SizedBox(height: 16),
              Text(
                'Admin Dashboard',
                style: GoogleFonts.poppins(
                  fontSize: 28,
                  fontWeight: FontWeight.w600,
                  color: AppColors.charcoal,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                'Supa Neighbour',
                style: GoogleFonts.openSans(
                  fontSize: 16,
                  color: AppColors.textGrey,
                ),
              ),
              const SizedBox(height: 48),
              
              // Error message
              if (_errorMessage != null)
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: AppColors.error.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    _errorMessage!,
                    style: const TextStyle(
                      color: AppColors.error,
                      fontSize: 14,
                    ),
                  ),
                ),
              const SizedBox(height: 16),
              
              // Email field
              TextField(
                controller: _emailController,
                decoration: const InputDecoration(
                  labelText: 'Email',
                  prefixIcon: Icon(Icons.email_outlined),
                ),
                keyboardType: TextInputType.emailAddress,
                textInputAction: TextInputAction.next,
              ),
              const SizedBox(height: 16),
              
              // Password field
              TextField(
                controller: _passwordController,
                obscureText: _obscurePassword,
                decoration: InputDecoration(
                  labelText: 'Password',
                  prefixIcon: const Icon(Icons.lock_outline),
                  suffixIcon: IconButton(
                    icon: Icon(
                      _obscurePassword 
                          ? Icons.visibility_outlined 
                          : Icons.visibility_off_outlined,
                    ),
                    onPressed: () {
                      setState(() {
                        _obscurePassword = !_obscurePassword;
                      });
                    },
                  ),
                ),
                textInputAction: TextInputAction.done,
                onSubmitted: (_) => _login(),
              ),
              const SizedBox(height: 24),
              
              // Login button
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _login,
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: Colors.white,
                          ),
                        )
                      : const Text('Login'),
                ),
              ),
              
              // DEV MODE: Skip login button
              const SizedBox(height: 12),
              TextButton(
                onPressed: _mockLogin,
                child: const Text(
                  'Skip Login (Dev Mode)',
                  style: TextStyle(
                    color: AppColors.textGrey,
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}