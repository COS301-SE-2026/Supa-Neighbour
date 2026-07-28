import 'dart:io';

class ApiConfig {
  // Backend server configuration
  static const String host = 'localhost';
  static const String port = '8080';
  
  // Get the base URL based on platform
  static String get baseUrl {
    // For Android devices (physical or emulator)
    if (Platform.isAndroid) {
      // With ADB reverse, localhost works for physical devices
      // For emulator, 10.0.2.2 would be needed, but localhost works with ADB reverse
      return 'http://$host:$port';
    }
    
    // For iOS simulator
    if (Platform.isIOS) {
      return 'http://$host:$port';
    }
    
    // For macOS desktop
    if (Platform.isMacOS) {
      return 'http://$host:$port';
    }
    
    // For web / other platforms
    return 'http://$host:$port';
  }
  
  // Alternative: Use your computer's IP for physical devices without ADB
  // static const String baseUrl = 'http://192.168.x.x:8080';
  
  // =============================================
  // AUTH ENDPOINTS
  // =============================================
  static const String auth = '/api/auth';
  static const String login = '/api/auth/login';
  static const String register = '/api/auth/register';
  static const String logout = '/api/auth/logout';
  static const String refreshToken = '/api/auth/refresh';
  
  // =============================================
  // USER ENDPOINTS
  // =============================================
  static const String users = '/api/users';
  static const String userById = '/api/users/{id}';
  static const String userProfile = '/api/user/profile';
  static const String updateProfile = '/api/user/profile/update';
  static const String userStatus = '/api/users/status';
  
  // =============================================
  // HELPER ENDPOINTS
  // =============================================
  static const String helpers = '/api/helpers';
  static const String helperById = '/api/helpers/{id}';
  static const String helperProfile = '/api/helper/profile';
  static const String helperSkills = '/api/helper/skills';
  static const String helperAvailability = '/api/helper/availability';
  
  // =============================================
  // TASK ENDPOINTS
  // =============================================
  static const String tasks = '/api/tasks';
  static const String taskById = '/api/tasks/{id}';
  static const String myTasks = '/api/tasks/mine';
  static const String availableTasks = '/api/tasks/available';
  static const String createTask = '/api/tasks';
  static const String updateTask = '/api/tasks/{id}';
  static const String deleteTask = '/api/tasks/{id}';
  static const String taskApproval = '/api/tasks/{id}/approve';
  static const String taskStart = '/api/tasks/{id}/start';
  static const String taskComplete = '/api/tasks/{id}/complete';
  static const String taskInvitations = '/api/tasks/invitations';
  static const String taskInvoice = '/api/tasks/invoice';
  
  // =============================================
  // LEADERBOARD ENDPOINTS
  // =============================================
  static const String leaderboard = '/api/leaderboard';
  
  // =============================================
  // ACHIEVEMENT ENDPOINTS
  // =============================================
  static const String achievements = '/api/achievements';
  static const String userAchievements = '/api/achievements/user';
  static const String allAchievements = '/api/achievements/all';
  static const String earnAchievement = '/api/achievements/earn';
  
  // =============================================
  // CHAT ENDPOINTS
  // =============================================
  static const String chat = '/api/chat';
  static const String conversations = '/api/chat/conversations';
  static const String messages = '/api/chat/messages';
  static const String chatById = '/api/chat/{id}';
  static const String sendMessage = '/api/chat/messages/send';
  static const String markAsRead = '/api/chat/messages/read';
  
  // =============================================
  // POSTS & BULLETIN ENDPOINTS
  // =============================================
  static const String posts = '/api/posts';
  static const String postById = '/api/posts/{id}';
  static const String comments = '/api/comments';
  static const String reactions = '/api/reactions';
  
  // =============================================
  // SETTINGS ENDPOINTS
  // =============================================
  static const String settings = '/api/settings';
  static const String privacySettings = '/api/settings/privacy';
  static const String notificationSettings = '/api/settings/notifications';
  
  // =============================================
  // RATING ENDPOINTS
  // =============================================
  static const String ratings = '/api/ratings';
  static const String ratingById = '/api/ratings/{id}';
  static const String userRatings = '/api/ratings/user/{id}';
  
  // =============================================
  // LOCATION ENDPOINTS
  // =============================================
  static const String locations = '/api/locations';
  static const String neighbourhood = '/api/locations/neighbourhood';
  
  // =============================================
  // ADMIN ENDPOINTS
  // =============================================
  static const String admin = '/api/admin';
  static const String adminUsers = '/api/admin/users';
  static const String adminTasks = '/api/admin/tasks';
  
  // =============================================
  // HEALTH & UTILITY
  // =============================================
  static const String health = '/actuator/health';
  static const String swagger = '/swagger-ui';
  static const String apiDocs = '/api-docs';
  
  // =============================================
  // HELPER METHODS
  // =============================================
  
  // Helper method to build full URL
  static String getUrl(String endpoint) {
    return '$baseUrl$endpoint';
  }
  
  // Helper method to replace path parameters
  static String buildUrl(String endpoint, Map<String, String> params) {
    String url = endpoint;
    params.forEach((key, value) {
      url = url.replaceAll('{$key}', value);
    });
    return '$baseUrl$url';
  }
  
  // Example usage:
  // ApiConfig.buildUrl(ApiConfig.taskById, {'id': '123'})
  // Returns: http://localhost:8080/api/tasks/123
}