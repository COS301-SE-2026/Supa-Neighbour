// shared/lib/models/admin_application_model.dart

class AdminApplication {
  final int? applicationId;
  final int userId;
  final String? username;
  final String applicationStatus; // Pending, Approved, Rejected
  final DateTime? applicationDate;
  final String justification;
  final int? reviewedByAdminId;
  final DateTime? reviewedDate;
  final String? rejectionReason;

  AdminApplication({
    this.applicationId,
    required this.userId,
    this.username,
    this.applicationStatus = 'Pending',
    this.applicationDate,
    required this.justification,
    this.reviewedByAdminId,
    this.reviewedDate,
    this.rejectionReason,
  });

  // Status helpers
  bool get isPending => applicationStatus == 'Pending';
  bool get isApproved => applicationStatus == 'Approved';
  bool get isRejected => applicationStatus == 'Rejected';

  // Convert to JSON (for POST request body)
  Map<String, dynamic> toJson() {
    return {
      'justification': justification,
    };
  }

  // Create from JSON (for responses)
  factory AdminApplication.fromJson(Map<String, dynamic> json) {
    return AdminApplication(
      applicationId: json['applicationId'] as int?,
      userId: json['userId'] as int? ?? 0,
      username: json['username'] as String?,
      applicationStatus: json['applicationStatus'] as String? ?? 'Pending',
      applicationDate: json['applicationDate'] != null
          ? DateTime.tryParse(json['applicationDate'].toString())
          : null,
      justification: json['justification'] as String? ?? '',
      reviewedByAdminId: json['reviewedByAdminId'] as int?,
      reviewedDate: json['reviewedDate'] != null
          ? DateTime.tryParse(json['reviewedDate'].toString())
          : null,
      rejectionReason: json['rejectionReason'] as String?,
    );
  }
}