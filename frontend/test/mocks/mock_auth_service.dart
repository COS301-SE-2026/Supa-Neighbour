import 'package:supa_neighbour/models/user_model.dart';
import 'package:supa_neighbour/services/auth_service.dart';

class MockAuthService implements IAuthService {
  bool _isLoggedIn = true;

  @override
  Future<User> login(String email, String password) async {
    return User.getMockUser();
  }

  @override
  Future<User> loginWithToken(String idToken) async {
    return User.getMockUser();
  }

  @override
  Future<void> logout() async {
    _isLoggedIn = false;
  }

  @override
  Future<void> deleteAccount() async {
    _isLoggedIn = false;
  }

  @override
  Future<User> register({
    required String idToken,
    required String firstName,
    required String lastName,
    required String password,
    required String phoneNumber,
    required String dateOfBirth,
    required String gender,
    required String username,
    required String street,
    required String town,
    required int zip,
    String userType = 'user',
  }) async {
    return User.getMockUser();
  }
}