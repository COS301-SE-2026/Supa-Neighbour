class VerificationResult {
  final int verificationId;
  final int taskId;
  final String status; // VERIFIED, NEEDS_REVIEW, FAILED
  final double? score;
  final bool? locationVerified;
  final double? distanceM;
  final double? geofenceRadiusM;
  final List<String> reasons;
  final String? aiInsight;
  final int? completionImageId;

  const VerificationResult({
    required this.verificationId,
    required this.taskId,
    required this.status,
    this.score,
    this.locationVerified,
    this.distanceM,
    this.geofenceRadiusM,
    this.reasons = const [],
    this.aiInsight,
    this.completionImageId,
  });

  factory VerificationResult.fromJson(Map<String, dynamic> json) {
    return VerificationResult(
      verificationId: json['verificationId'] as int,
      taskId: json['taskId'] as int,
      status: json['status'] as String,
      score: (json['score'] as num?)?.toDouble(),
      locationVerified: json['locationVerified'] as bool?,
      distanceM: (json['distanceM'] as num?)?.toDouble(),
      geofenceRadiusM: (json['geofenceRadiusM'] as num?)?.toDouble(),
      reasons: (json['reasons'] as List<dynamic>? ?? []).cast<String>(),
      aiInsight: json['aiInsight'] as String?,
      completionImageId: json['completionImageId'] as int?,
    );
  }
}