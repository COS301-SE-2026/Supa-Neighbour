import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared/models/user_model.dart';
import '../services/auth_service.dart';

final adminAuthServiceProvider = Provider<AdminAuthService>((ref) {
  return AdminAuthService();
});

class AdminAuthState {
  final User ? user;
  final bool isLoading;
  final String? error;

  const AdminAuthState({this.user, this.isLoading = false, this.error});

  bool get isAuthenticated => user != null;
}

class AdminAuthNotifier extends StateNotifier<AdminAuthState> {
  final AdminAuthService _authService;

  AdminAuthNotifier(this._authService): super(const AdminAuthState());

  Future<bool> login(String email, String password) async {
    state = const AdminAuthState(isLoading: true);

    try{
      final user = await _authService.login(email, password);
      state = AdminAuthState(user: user, isLoading: false);
      return true;
    }on AdminAuthException catch(e){
      state = AdminAuthState(isLoading: false, error: e.message);
      return false;
    }catch(e){
      state = const AdminAuthState(
        isLoading: false,
        error: 'Something went wrong. Please try again.',
      );
      return false;
    }
  }

  Future<void> logout() async {
    await _authService.logout();
    state = const AdminAuthState();
  }
}

final adminAuthProvider =
    StateNotifierProvider<AdminAuthNotifier, AdminAuthState>((ref) {
  return AdminAuthNotifier(ref.watch(adminAuthServiceProvider));
});