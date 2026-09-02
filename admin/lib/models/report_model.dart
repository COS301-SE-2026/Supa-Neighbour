// admin/lib/models/report_model.dart

import 'package:flutter/material.dart';
import 'package:shared/constants/constants.dart';

enum ReportType {
  user('User'),
  post('Post'),
  comment('Comment'),
  taskDispute('Task Dispute'),
  unknown('Unknown');

  final String display;
  const ReportType(this.display);

  static ReportType fromApi(String? raw) {
    switch ((raw ?? '').toLowerCase().replaceAll('_', '')) {
      case 'user':
        return ReportType.user;
      case 'post':
        return ReportType.post;
      case 'comment':
        return ReportType.comment;
      case 'taskdispute':
        return ReportType.taskDispute;
      default:
        return ReportType.unknown;
    }
  }

  String get apiValue => name;
}

enum ReportStatus {
  submitted('Submitted'),
  assigned('Assigned'),
  reviewed('Reviewed'),
  resolved('Resolved'),
  unknown('Unknown');

  final String display;
  const ReportStatus(this.display);

  static ReportStatus fromApi(String? raw) {
    switch ((raw ?? '').toLowerCase().replaceAll('_', '').replaceAll(' ', '')) {
      case 'submitted':
        return ReportStatus.submitted;
      case 'assigned':
        return ReportStatus.assigned;
      case 'reviewed':
        return ReportStatus.reviewed;
      case 'resolved':
        return ReportStatus.resolved;
      default:
        return ReportStatus.unknown;
    }
  }

  String get apiValue => name;
}

/// Fed by a separate suggested-action endpoint (not GET /api/report) that
/// takes violationType + severity and returns a SuggestedAction for the
/// admin to approve. `unassessed` is the default until that call is made.
enum Severity {
  minor('Minor'),
  moderate('Moderate'),
  severe('Severe'),
  unassessed('Not assessed');

  final String display;
  const Severity(this.display);

  static Severity fromApi(String? raw) {
    switch ((raw ?? '').toUpperCase()) {
      case 'MINOR':
        return Severity.minor;
      case 'MODERATE':
        return Severity.moderate;
      case 'SEVERE':
        return Severity.severe;
      default:
        return Severity.unassessed;
    }
  }

  String get apiValue => name.toUpperCase();
}

enum ViolationType {
  harassment('Harassment', 'HARASSMENT'),
  hateSpeech('Hate Speech', 'HATE_SPEECH'),
  inappropriateContent('Inappropriate Content', 'INAPPROPRIATE_CONTENT'),
  spamScam('Spam/Scam', 'SPAM_SCAM'),
  privacyViolation('Privacy Violation', 'PRIVACY_VIOLATION'),
  impersonation('Impersonation', 'IMPERSONATION'),
  taskNoShow('Task: No-show', 'TASK_NO_SHOW'),
  taskPoorQuality('Task: Poor Quality', 'TASK_POOR_QUALITY'),
  taskPropertyDamage('Task: Property Damage', 'TASK_PROPERTY_DAMAGE'),
  taskUnsafeConditions('Task: Unsafe Conditions', 'TASK_UNSAFE_CONDITIONS'),
  threatsViolence('Threats of Violence', 'THREATS_VIOLENCE'),
  unassessed('Not yet assessed', 'UNASSESSED');

  final String display;
  final String apiValue;
  const ViolationType(this.display, this.apiValue);

  static ViolationType fromApi(String? raw) {
    final value = (raw ?? '').toUpperCase();
    for (final type in ViolationType.values) {
      if (type.apiValue == value) return type;
    }
    return ViolationType.unassessed;
  }
}

enum SuggestedAction {
  warning('⚠️ Warning', 'WARNING'),
  suspend7d('⛔ Suspend 7 days', 'SUSPEND_7D'),
  suspend14d('⛔ Suspend 14 days', 'SUSPEND_14D'),
  suspend30d('⛔ Suspend 30 days', 'SUSPEND_30D'),
  ban('🚫 Ban', 'BAN'),
  pending('Pending review', 'PENDING');

  final String display;
  final String apiValue;
  const SuggestedAction(this.display, this.apiValue);

  static SuggestedAction fromApi(String? raw) {
    final value = (raw ?? '').toUpperCase();
    for (final action in SuggestedAction.values) {
      if (action.apiValue == value) return action;
    }
    return SuggestedAction.pending;
  }
}

class Report {
  final int reportId;
  final ReportType reportType;
  final ReportStatus status;
  final int? adminId;
  final int reporterUserId;
  final int? reportedUserId;
  final int? reportedPostId;
  final int? reportedCommentId;
  final int? taskId;
  final String? disputeReason;
  final String reason;
  final DateTime createdAt;

  // Not returned by GET /api/report — stay at their "not assessed yet"
  // defaults until copyWithAssessment() is called with data from the
  // suggested-action endpoint.
  final Severity severity;
  final ViolationType violationType;
  final SuggestedAction suggestedAction;

  const Report({
    required this.reportId,
    required this.reportType,
    required this.status,
    this.adminId,
    required this.reporterUserId,
    this.reportedUserId,
    this.reportedPostId,
    this.reportedCommentId,
    this.taskId,
    this.disputeReason,
    required this.reason,
    required this.createdAt,
    this.severity = Severity.unassessed,
    this.violationType = ViolationType.unassessed,
    this.suggestedAction = SuggestedAction.pending,
  });

  factory Report.fromJson(Map<String, dynamic> json) {
    return Report(
      reportId: json['reportId'] as int,
      reportType: ReportType.fromApi(json['reportType'] as String?),
      status: ReportStatus.fromApi(json['status'] as String?),
      adminId: json['adminId'] as int?,
      reporterUserId: json['reporterUserId'] as int,
      reportedUserId: json['reportedUserId'] as int?,
      reportedPostId: json['reportedPostId'] as int?,
      reportedCommentId: json['reportedCommentId'] as int?,
      taskId: json['taskId'] as int?,
      disputeReason: json['disputeReason'] as String?,
      reason: json['reason'] as String? ?? '',
      createdAt: _parseTimestamp(json['createdAt']),
      // severity/violationType/suggestedAction intentionally left at
      // their defaults — ReportResponseDTO doesn't include them.
    );
  }

  /// java.sql.Timestamp can serialize as an ISO string OR epoch millis
  /// depending on Jackson/ObjectMapper config — handles both.
  static DateTime _parseTimestamp(dynamic value) {
    if (value is String) return DateTime.parse(value);
    if (value is num) return DateTime.fromMillisecondsSinceEpoch(value.toInt());
    return DateTime.now();
  }

  /// Returns a copy with severity/violationType/suggestedAction filled in
  /// — call this once the suggested-action endpoint responds, on the
  /// report detail screen.
  Report copyWithAssessment({
    Severity? severity,
    ViolationType? violationType,
    SuggestedAction? suggestedAction,
  }) {
    return Report(
      reportId: reportId,
      reportType: reportType,
      status: status,
      adminId: adminId,
      reporterUserId: reporterUserId,
      reportedUserId: reportedUserId,
      reportedPostId: reportedPostId,
      reportedCommentId: reportedCommentId,
      taskId: taskId,
      disputeReason: disputeReason,
      reason: reason,
      createdAt: createdAt,
      severity: severity ?? this.severity,
      violationType: violationType ?? this.violationType,
      suggestedAction: suggestedAction ?? this.suggestedAction,
    );
  }

  // ---- Display helpers -----------------------------------------------
  // reporter/reported names aren't returned by the backend yet —
  // deliberate placeholders, not real data:

  String get reporterName => 'Anonymous';
  String get reportedName => 'Anonymous';

  /// Short preview built from `reason`, since there's no dedicated
  /// postContent/commentContent field in the DTO.
  String get contentPreview {
    const maxLen = 80;
    final trimmed = reason.trim();
    if (trimmed.length <= maxLen) return trimmed;
    return '${trimmed.substring(0, maxLen).trimRight()}…';
  }

  Color get statusColor {
    switch (status) {
      case ReportStatus.submitted:
        return AppColors.citrusYellow;
      case ReportStatus.assigned:
      case ReportStatus.reviewed:
        return AppColors.primaryTeal;
      case ReportStatus.resolved:
        return AppColors.success;
      case ReportStatus.unknown:
        return AppColors.textGrey;
    }
  }

  Color get severityColor {
    switch (severity) {
      case Severity.minor:
        return const Color(0xFF6B7280); // Grey
      case Severity.moderate:
        return const Color(0xFFF4A261); // Orange
      case Severity.severe:
        return const Color(0xFFF44336); // Red
      case Severity.unassessed:
        return AppColors.textGrey;
    }
  }

  String get severityDisplay => severity.display;

  String get violationTypeDisplay => violationType.display;

  String get suggestedActionDisplay => suggestedAction.display;

  bool get isUrgent {
    if (violationType == ViolationType.privacyViolation ||
        violationType == ViolationType.threatsViolence) {
      return true;
    }
    return severity == Severity.severe;
  }
}