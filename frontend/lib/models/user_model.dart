

class User {
  final String id;
  final String email;
  final String firstName;
  final String lastName;
  final String? phone;
  final String? username;
  final DateTime? birthday;
  final String? gender;
  final String? street;
  final String? town;
  final String? zipCode;
  final DateTime createdAt;
  final DateTime? updatedAt;

  User({
    required this.id,
    required this.email,
    required this.firstName,
    required this.lastName,
    this.phone,
    this.username,
    this.birthday,
    this.gender,
    this.street,
    this.town,
    this.zipCode,
    required this.createdAt,
    this.updatedAt,
  });

  String get fullName => '$firstName $lastName';

  String get fullAddress {
    if (street != null && town != null && zipCode != null) {
      return '$street, $town, $zipCode';
    }
    return 'Address not provided';
  }

  // Create a copy with updated fields
  User copyWith({
    String? id,
    String? email,
    String? firstName,
    String? lastName,
    String? phone,
    String? username,
    DateTime? birthday,
    String? gender,
    String? street,
    String? town,
    String? zipCode,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return User(
      id: id ?? this.id,
      email: email ?? this.email,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      phone: phone ?? this.phone,
      username: username ?? this.username,
      birthday: birthday ?? this.birthday,
      gender: gender ?? this.gender,
      street: street ?? this.street,
      town: town ?? this.town,
      zipCode: zipCode ?? this.zipCode,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? DateTime.now(),
    );
  }

  // Convert to JSON (for future API integration)
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'firstName': firstName,
      'lastName': lastName,
      'phone': phone,
      'username': username,
      'birthday': birthday?.toIso8601String(),
      'gender': gender,
      'street': street,
      'town': town,
      'zipCode': zipCode,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  // Create from JSON (for future API integration)
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      email: json['email'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      phone: json['phone'],
      username: json['username'],
      birthday: json['birthday'] != null ? DateTime.parse(json['birthday']) : null,
      gender: json['gender'],
      street: json['street'],
      town: json['town'],
      zipCode: json['zipCode'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  // Creates an empty user
  static User empty() {
    return User(
      id: '',
      email: '',
      firstName: '',
      lastName: '',
      createdAt: DateTime.now(),
    );
  }

  // Mock current user (temporary for development)
  static User getMockUser() {
    return User(
      id: '1',
      email: 'blessing@example.com',
      firstName: 'Blessing',
      lastName: 'User',
      phone: '+27 12 345 6789',
      username: 'blessing_u',
      birthday: DateTime(1995, 5, 15),
      gender: 'Female',
      street: '123 Main Street',
      town: 'Hatfield, Pretoria',
      zipCode: '0028',
      createdAt: DateTime.now(),
    );
  }
}