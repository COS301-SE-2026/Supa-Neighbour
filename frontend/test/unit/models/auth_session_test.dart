import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/auth_session.dart';
import 'package:supa_neighbour/models/user_model.dart';

void main() {
  group('AuthSession Unit Tests', () {
    late User testUser;

    setUp(() {
      // Clear session before each test
      AuthSession.instance.clear();
      
      testUser = User(
        id: 'test_1',
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
        phone: '1234567890',
        username: 'testuser',
        street: '123 Test St',
        town: 'Test Town',
        zipCode: '1234',
        createdAt: DateTime.now(),
      );
    });

    group('Initial State', () {
      test('should start with no logged in user', () {
        expect(AuthSession.instance.isLoggedIn, false);
        expect(AuthSession.instance.currentUser, null);
      });

      test('should return Guest as default userName', () {
        expect(AuthSession.instance.userName, 'Guest');
      });

      test('should return empty string as default userEmail', () {
        expect(AuthSession.instance.userEmail, '');
      });
    });

    group('Login Functionality', () {
      test('login should set current user and isLoggedIn to true', () {
        AuthSession.instance.login(testUser);
        
        expect(AuthSession.instance.isLoggedIn, true);
        expect(AuthSession.instance.currentUser?.id, testUser.id);
        expect(AuthSession.instance.userName, testUser.fullName);
        expect(AuthSession.instance.userEmail, testUser.email);
      });

      test('login should overwrite existing user', () {
        final firstUser = User(
          id: 'first',
          email: 'first@example.com',
          firstName: 'First',
          lastName: 'User',
          createdAt: DateTime.now(),
        );
        
        AuthSession.instance.login(firstUser);
        expect(AuthSession.instance.currentUser?.id, 'first');
        
        AuthSession.instance.login(testUser);
        expect(AuthSession.instance.currentUser?.id, 'test_1');
      });
    });

    group('Logout Functionality', () {
      test('logout should clear user and set isLoggedIn to false', () {
        AuthSession.instance.login(testUser);
        expect(AuthSession.instance.isLoggedIn, true);
        
        AuthSession.instance.logout();
        
        expect(AuthSession.instance.isLoggedIn, false);
        expect(AuthSession.instance.currentUser, null);
        expect(AuthSession.instance.userName, 'Guest');
      });
    });

    group('Update User Functionality', () {
      test('updateUser should modify current user', () {
        AuthSession.instance.login(testUser);
        
        final updatedUser = testUser.copyWith(
          firstName: 'Updated',
          lastName: 'Name',
        );
        
        AuthSession.instance.updateUser(updatedUser);
        
        expect(AuthSession.instance.currentUser?.firstName, 'Updated');
        expect(AuthSession.instance.currentUser?.lastName, 'Name');
        expect(AuthSession.instance.userName, 'Updated Name');
      });

      test('updateUser should not throw exception when no user is logged in', () {
        // Option 4: Just ensure no exception is thrown
        expect(() => AuthSession.instance.updateUser(testUser), returnsNormally);
      });
    });

    group('Profile Completeness', () {
      test('isProfileComplete should return false for incomplete profile', () {
        final incompleteUser = User(
          id: 'incomplete',
          email: 'test@example.com',
          firstName: 'Test',
          lastName: 'User',
          createdAt: DateTime.now(),
        );
        
        AuthSession.instance.login(incompleteUser);
        expect(AuthSession.instance.isProfileComplete, false);
      });

      test('isProfileComplete should return true for complete profile', () {
        AuthSession.instance.login(testUser);
        expect(AuthSession.instance.isProfileComplete, true);
      });

      test('isProfileComplete should return false when partially complete', () {
        final partialUser = User(
          id: 'partial',
          email: 'test@example.com',
          firstName: 'Test',
          lastName: 'User',
          phone: '1234567890',
          // missing username, street, town, zipCode
          createdAt: DateTime.now(),
        );
        
        AuthSession.instance.login(partialUser);
        expect(AuthSession.instance.isProfileComplete, false);
      });
    });

    group('Singleton Pattern', () {
      test('should return same instance every time', () {
        final instance1 = AuthSession.instance;
        final instance2 = AuthSession.instance;
        
        expect(identical(instance1, instance2), true);
      });
    });
  });
}