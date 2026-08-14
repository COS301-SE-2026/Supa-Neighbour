import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/auth_service.dart';
import '../services/profile_service.dart';
import '../services/task_service.dart';
import '../services/bulletin_service.dart';

// AUTH SERVICE PROVIDER
final authServiceProvider = Provider<IAuthService>((ref) {
  return AuthService();
});

// PROFILE SERVICE PROVIDER
final profileServiceProvider = Provider<IProfileService>((ref) {
  return UserProfileService();
});

// TASK SERVICE PROVIDER
final taskServiceProvider = Provider<ITaskService>((ref) {
  return TaskService();
});

// BULLETIN SERVICE PROVIDER
final bulletinServiceProvider = Provider<IBulletinService>((ref) {
  return BulletinService();
});