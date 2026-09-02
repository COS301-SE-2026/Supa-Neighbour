class ReportRequest {
  final String reportType; // "TASK_DISPUTE"
  final int? reportedUserId;
  final int? reportedPostId;
  final int? reportedCommentId;
  final int? taskId;
  final String? disputeReason; // "NO_SHOW", "INCOMPLETE", "DAMAGE"
  final String reason;
  final String description;

  ReportRequest({
    required this.reportType,
    this.reportedUserId,
    this.reportedPostId,
    this.reportedCommentId,
    this.taskId,
    this.disputeReason,
    required this.reason,
    required this.description,
  });

  Map<String, dynamic> toJson() {
    return {
      'reportType': reportType,
      'reportedUserId': reportedUserId,
      'reportedPostId': reportedPostId,
      'reportedCommentId': reportedCommentId,
      'taskId': taskId,
      'disputeReason': disputeReason,
      'reason': reason,
      'description': description,
    };
  }
}