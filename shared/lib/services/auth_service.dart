// shared/lib/services/auth_service.dart

import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared/models/user_model.dart';
import 'package:shared/services/api_client.dart';

class AuthService {
  final Dio _dio = ApiClient().dio;
  final fb.FirebaseAuth _firebaseAuth = fb.FirebaseAuth.instance;

  // ============================================================
  // LOGIN
  // ============================================================
  Future<User> login(String email, String password) async {
    try {
      // 1. Sign in with Firebase
      final credential = await _firebaseAuth.signInWithEmailAndPassword(
        email: email,
        password: password,
      );

      final idToken = await credential.user?.getIdToken();
      if (idToken == null) {
        throw Exception('Failed to retrieve Firebase ID token');
      }

      // 2. Send token to backend
      final response = await _dio.post(
        '/api/auth/login',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.statusCode == 200 && response.data != null) {
        final user = User.fromJson(response.data!);
        // Store token for subsequent API calls
        ApiClient().setAuthToken(idToken);
        return user;
      }

      throw Exception('Login failed: unexpected response from server');
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Authentication failed: ${e.message}');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  // ============================================================
  // LOGIN WITH TOKEN (for auto-login / session restore)
  // ============================================================
  Future<User> loginWithToken(String idToken) async {
    try {
      final response = await _dio.post(
        '/api/auth/login',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.statusCode == 200 && response.data != null) {
        final user = User.fromJson(response.data!);
        ApiClient().setAuthToken(idToken);
        return user;
      }

      throw Exception('Auto-login failed: unexpected response');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  // ============================================================
  // REGISTER
  // ============================================================
  Future<User> register({
    required String firstName,
    required String lastName,
    required String email,
    required String password,
    required String phoneNumber,
    required String dateOfBirth,
    required String gender,
    required String username,
    required String street,
    required String town,
    required int zip,
  }) async {
    try {
      // 1. Create Firebase user
      final credential = await _firebaseAuth.createUserWithEmailAndPassword(
        email: email,
        password: password,
      );

      final idToken = await credential.user?.getIdToken();
      if (idToken == null) {
        throw Exception('Failed to retrieve Firebase ID token');
      }

      // 2. Register in backend
      final response = await _dio.post(
        '/api/auth/register',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
        data: {
          'firstName': firstName,
          'lastName': lastName,
          'password': password,
          'phoneNumber': phoneNumber,
          'dateOfBirth': dateOfBirth,
          'gender': gender,
          'username': username,
          'street': street,
          'town': town,
          'zip': zip,
        },
      );

      if (response.statusCode == 200 && response.data != null) {
        final user = User.fromJson(response.data!);
        ApiClient().setAuthToken(idToken);
        return user;
      }

      throw Exception('Registration failed: unexpected response');
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Registration failed: ${e.message}');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  // ============================================================
  // LOGOUT
  // ============================================================
  Future<void> logout() async {
    try {
      final idToken = await _firebaseAuth.currentUser?.getIdToken();
      if (idToken != null) {
        await _dio.post(
          '/api/auth/logout',
          options: Options(headers: {'Authorization': 'Bearer $idToken'}),
        );
      }
    } on DioException {
      // Ignore network errors during logout - we still want to clear local state
    } finally {
      await _firebaseAuth.signOut();
      ApiClient().clearAuthToken();
    }
  }

  // ============================================================
  // DELETE ACCOUNT
  // ============================================================
  Future<void> deleteAccount() async {
    try {
      final idToken = await _firebaseAuth.currentUser?.getIdToken();
      if (idToken == null) {
        throw Exception('No authenticated user to delete');
      }

      final response = await _dio.delete(
        '/api/settings/me/user',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.statusCode != 204) {
        throw Exception('Delete account failed');
      }

      await _firebaseAuth.currentUser?.delete();
      ApiClient().clearAuthToken();
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Failed to delete account: ${e.message}');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  // ============================================================
  // GET CURRENT USER
  // ============================================================
  Future<User?> getCurrentUser() async {
    try {
      final firebaseUser = _firebaseAuth.currentUser;
      if (firebaseUser == null) return null;

      final idToken = await firebaseUser.getIdToken();
      final response = await _dio.get(
        '/api/users/me',
        options: Options(headers: {'Authorization': 'Bearer $idToken'}),
      );

      if (response.statusCode == 200 && response.data != null) {
        return User.fromJson(response.data!);
      }

      return null;
    } catch (_) {
      return null;
    }
  }

  // ============================================================
  // CHECK ADMIN STATUS
  // ============================================================
  Future<bool> isAdmin() async {
    try {
      final user = await getCurrentUser();
      return user?.isAdmin ?? false;
    } catch (_) {
      return false;
    }
  }

  // ============================================================
  // ADMIN LOGIN (for admin website)
  // ============================================================
  Future<User> adminLogin(String email, String password) async {
    try {
      final user = await login(email, password);

      if (!user.isAdmin) {
        await logout();
        throw Exception('Admin access denied');
      }

      return user;
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Authentication failed: ${e.message}');
    } on DioException catch (e) {
      throw Exception('Connection error: ${e.message}');
    }
  }

  // ============================================================
  // PASSWORD RESET
  // ============================================================
  Future<void> sendPasswordResetEmail(String email) async {
    try {
      await _firebaseAuth.sendPasswordResetEmail(email: email);
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Password reset failed: ${e.message}');
    }
  }

  // ============================================================
  // EMAIL VERIFICATION
  // ============================================================
  Future<void> sendEmailVerification() async {
    try {
      final user = _firebaseAuth.currentUser;
      if (user == null) throw Exception('No user logged in');
      await user.sendEmailVerification();
    } on fb.FirebaseAuthException catch (e) {
      throw Exception('Verification email failed: ${e.message}');
    }
  }

  // ============================================================
  // CHECK EMAIL VERIFIED
  // ============================================================
  Future<bool> isEmailVerified() async {
    try {
      final user = _firebaseAuth.currentUser;
      if (user == null) return false;
      await user.reload();
      return user.emailVerified;
    } catch (_) {
      return false;
    }
  }
}