import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/auth_service.dart';

// AUTH SERVICE PROVIDER
final authServiceProvider = Provider<IAuthService>((ref) {
  return AuthService();
});

// PROFILE SERVICE PROVIDER
final profileServiceProvider = Provider<IProfileService>((ref) {
  return UserProfileService();
});