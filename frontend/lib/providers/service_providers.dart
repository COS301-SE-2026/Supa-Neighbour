import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/auth_service.dart';
import '../services/profile_service.dart';
import '../services/task_service.dart';
import '../services/bulletin_service.dart';
import '../services/chat_service.dart';

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

// CHAT SERVICE PROVIDER
final chatServiceProvider = Provider<IChatService>((ref) {
  return ChatService();
});

// ACHIEVEMENT SERVICE PROVIDER
final achievementServiceProvider = Provider<IAchievementService>((ref) {
  return AchievementService();
});

// LEADERBOARD SERVICE PROVIDER
final leaderboardServiceProvider = Provider<ILeaderboardService>((ref) {
  return LeaderboardService();
});
