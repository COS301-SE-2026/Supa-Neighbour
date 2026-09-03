
class ReportDTO {
  final int? reportId;
  final String? reportType;
  final String? status;
  final int? reportedUserId;
  final int? reportedPostId;
  final int? reportedCommentId;
  final int? taskId;
  final String? disputeReason;
  final String? reason;
  final String? actualAction;
  final DateTime? createdAt;
  final DateTime? resolvedAt;
  final dynamic details;

  ReportDTO({
    this.reportId,
    this.reportType,
    this.status,
    this.reportedUserId,
    this.reportedPostId,
    this.reportedCommentId,
    this.taskId,
    this.disputeReason,
    this.reason,
    this.actualAction,
    this.createdAt,
    this.resolvedAt,
    this.details,
  });

  factory ReportDTO.fromJson(Map<String, dynamic> json) {
    return ReportDTO(
      reportId: json['reportId'],
      reportType: json['reportType'],
      status: json['status'],
      reportedUserId: json['reportedUserId'],
      reportedPostId: json['reportedPostId'],
      reportedCommentId: json['reportedCommentId'],
      taskId: json['taskId'],
      disputeReason: json['disputeReason'],
      reason: json['reason'],
      actualAction: json['actualAction'],
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'].toString())
          : null,
      resolvedAt: json['resolvedAt'] != null
          ? DateTime.parse(json['resolvedAt'].toString())
          : null,
      details: json['details'],
    );
  }
}