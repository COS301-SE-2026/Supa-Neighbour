class UpdateProfileResponse {
  final String message;
  final String displayName;
  final List<String>? skills;

  UpdateProfileResponse({
    required this.message, 
    required this.displayName, 
    this.skills,
  });

  factory UpdateProfileResponse.fromJson(Map<String, dynamic> json){
    return UpdateProfileResponse(
      message: json['message'] as String, 
      displayName: json['displayName'] as String,
      skills: (json['skills']  as List<dynamic>?)?.map((e) => e as String).toList(),
      );
  }
}