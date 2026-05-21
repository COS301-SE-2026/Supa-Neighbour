import 'package:flutter/material.dart';
import 'user_model.dart';

class AuthSession {
  static AuthSession? _instance;
  User? _currentUser;
  bool _isLoggedIn = false;

  // Singleton pattern
  static AuthSession get instance {
    _instance ??= AuthSession._internal();
    return _instance!;
  }

  AuthSession._internal();

  // Getters
  User? get currentUser => _currentUser;
  bool get isLoggedIn => _isLoggedIn;
  String get userName => _currentUser?.fullName ?? 'Guest';
  String get userEmail => _currentUser?.email ?? '';

  // Login user
  void login(User user) {
    _currentUser = user;
    _isLoggedIn = true;
    debugPrint('User logged in: ${user.email}');
  }

  // Logout user
  void logout() {
    _currentUser = null;
    _isLoggedIn = false;
    debugPrint('User logged out');
  }

  // Update current user
  void updateUser(User user) {
    _currentUser = user;
    debugPrint('User updated: ${user.email}');
  }

  // Check if user has completed profile
  bool get isProfileComplete {
    if (_currentUser == null) return false;
    return _currentUser!.phone != null &&
        _currentUser!.username != null &&
        _currentUser!.street != null &&
        _currentUser!.town != null &&
        _currentUser!.zipCode != null;
  }

  // Clear session (for testing)
  void clear() {
    _currentUser = null;
    _isLoggedIn = false;
  }
}