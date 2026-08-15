import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/user_model.dart';

void main() {
  group('User Model Unit Tests', () {
    late User testUser;
    final now = DateTime.now();

    setUp(() {
      testUser = User(
        id: 'user_1',
        email: 'john@example.com',
        firstName: 'John',
        lastName: 'Doe',
        phone: '1234567890',
        username: 'johndoe',
        birthday: DateTime(1990, 1, 1),
        gender: 'Male',
        street: '123 Main St',
        town: 'Pretoria',
        zipCode: '0001',
        createdAt: now,
      );
    });

    group('Properties and Getters', () {
      test('fullName should return correct concatenation', () {
        expect(testUser.fullName, 'John Doe');
      });

      test('fullName should work with empty lastName', () {
        final user = User(
          id: 'user_2',
          email: 'jane@example.com',
          firstName: 'Jane',
          lastName: '',
          createdAt: now,
        );
        expect(user.fullName, 'Jane ');
      });

      test('fullAddress should return formatted address when all fields present', () {
        expect(testUser.fullAddress, '123 Main St, Pretoria, 0001');
      });

      test('fullAddress should return default message when address missing', () {
        final user = User(
          id: 'user_3',
          email: 'test@example.com',
          firstName: 'Test',
          lastName: 'User',
          createdAt: now,
        );
        expect(user.fullAddress, 'Address not provided');
      });
    });

    group('copyWith', () {
      test('copyWith should create new user with updated email', () {
        final updatedUser = testUser.copyWith(email: 'newemail@example.com');
        
        expect(updatedUser.email, 'newemail@example.com');
        expect(updatedUser.id, testUser.id);
        expect(updatedUser.firstName, testUser.firstName);
        expect(updatedUser.lastName, testUser.lastName);
      });

      test('copyWith should create new user with updated address', () {
        final updatedUser = testUser.copyWith(
          street: '456 New St',
          town: 'Johannesburg',
          zipCode: '2000',
        );
        
        expect(updatedUser.street, '456 New St');
        expect(updatedUser.town, 'Johannesburg');
        expect(updatedUser.zipCode, '2000');
        expect(updatedUser.fullAddress, '456 New St, Johannesburg, 2000');
      });

      test('copyWith should update updatedAt timestamp', () {
        final updatedUser = testUser.copyWith(email: 'new@example.com');
        
        expect(updatedUser.updatedAt, isNotNull);
      });
    });

    group('JSON Serialization', () {
      test('toJson should return correct map', () {
        final json = testUser.toJson();
        
        expect(json['id'], 'user_1');
        expect(json['email'], 'john@example.com');
        expect(json['firstName'], 'John');
        expect(json['lastName'], 'Doe');
        expect(json['phone'], '1234567890');
        expect(json['username'], 'johndoe');
        expect(json['gender'], 'Male');
        expect(json['street'], '123 Main St');
        expect(json['town'], 'Pretoria');
        expect(json['zipCode'], '0001');
      });

      test('fromJson should create User from map', () {
        final json = {
          'userid': 'user_5',
          'email': 'fromjson@example.com',
          'firstName': 'Json',
          'lastName': 'User',
          'phoneNumber': '0987654321',
          'username': 'jsonuser',
          'birthday': DateTime(1995, 5, 15).toIso8601String(),
          'user_gender': 'Female',
          'user_street': '789 Json St',
          'user_town': 'Cape Town',
          'user_zipcode': '8001',
          'createdAt': now.toIso8601String(),
        };
        
        final user = User.fromJson(json);
        
        expect(user.id, 'user_5');
        expect(user.email, 'fromjson@example.com');
        expect(user.firstName, 'Json');
        expect(user.lastName, 'User');
        expect(user.phone, '0987654321');
        expect(user.username, 'jsonuser');
        expect(user.birthday?.year, 1995);
        expect(user.gender, 'Female');
        expect(user.street, '789 Json St');
        expect(user.town, 'Cape Town');
        expect(user.zipCode, '8001');
      });
    });

    group('Static Methods', () {
      test('empty() should return User with empty strings', () {
        final emptyUser = User.empty();
        
        expect(emptyUser.id, '');
        expect(emptyUser.email, '');
        expect(emptyUser.firstName, '');
        expect(emptyUser.lastName, '');
      });

      test('getMockUser should return valid user', () {
        final mockUser = User.getMockUser();
        
        expect(mockUser.id, isNotEmpty);
        expect(mockUser.email, isNotEmpty);
        expect(mockUser.firstName, isNotEmpty);
        expect(mockUser.lastName, isNotEmpty);
      });
    });
  });
}