class HelperProfileResponse {
  final int userId;
  final String displayName;
  final String? level;
  final double trustScore;
  final int completedTasks;
  final int neighboursHelped;
  final List<String> skills;
  final List<ReviewDTO> reviews;

  HelperProfileResponse({
    required this.userId,
    required this.displayName,
    this.level,
    required this.trustScore,
    required this.completedTasks,
    required this.neighboursHelped,
    required this.skills,
    required this.reviews,
  });

  factory HelperProfileResponse.fromJson(Map<String, dynamic> json) {
    return HelperProfileResponse(
      userId: json['userId'] as int? ?? 0,
      displayName: json['displayName'] as String? ?? '',
      level: json['level'] as String?,
      trustScore: (json['trustScore'] as num?)?.toDouble() ?? 0.0,
      completedTasks: json['completedTasks'] as int? ?? 0,
      neighboursHelped: json['neighboursHelped'] as int? ?? 0,
      skills: (json['skills'] as List<dynamic>?)?.map((e) => e as String).toList() ?? [],
      reviews: (json['reviews'] as List<dynamic>?)
          ?.map((e) => ReviewDTO.fromJson(e as Map<String, dynamic>))
          .toList() ?? [],
    );
  }
}

class ReviewDTO {
  final String reviewerName;
  final String comment;
  final String? rating;

  ReviewDTO({
    required this.reviewerName,
    required this.comment,
    this.rating,
  });

  factory ReviewDTO.fromJson(Map<String, dynamic> json) {
    return ReviewDTO(
      reviewerName: json['reviewerName'] as String? ?? '',
      comment: json['comment'] as String? ?? '',
      rating: json['rating']?.toString(),
    );
  }
}