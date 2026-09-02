// admin/lib/models/report_model.dart

import 'package:flutter/material.dart';

enum ReportType {
  user,
  post,
  comment,
  taskDispute,
}

enum ReportStatus {
  submitted,
  assigned,
  reviewed,
  resolved,
}

enum Severity {
  minor,
  moderate,
  severe,
}

enum ViolationType {
  harassment,
  hateSpeech,
  inappropriateContent,
  spamScam,
  privacyViolation,
  impersonation,
  taskNoShow,
  taskPoorQuality,
  taskPropertyDamage,
  taskUnsafeConditions,
  threatsViolence,
}

enum SuggestedAction {
  warning,
  suspend7d,
  suspend14d,
  suspend30d,
  ban,
}

class Report {
  final int id;
  final ReportType reportType;
  final int reporterUserId;
  final String reporterName;
  final ReportStatus status;
  final int? adminId;
  final String? adminName;
  final int? reportedUserId;
  final String? reportedUserName;
  final int? reportedPostId;
  final String? postContent;
  final int? reportedCommentId;
  final String? commentContent;
  final int? taskId;
  final String? taskTitle;
  final String? disputeReason;
  final String reason;
  final String? description;
  final ViolationType? violationType;
  final Severity? severity;
  final SuggestedAction? suggestedAction;
  final SuggestedAction? actualAction;
  final DateTime createdAt;
  final DateTime? resolvedAt;

  Report({
    required this.id,
    required this.reportType,
    required this.reporterUserId,
    required this.reporterName,
    required this.status,
    this.adminId,
    this.adminName,
    this.reportedUserId,
    this.reportedUserName,
    this.reportedPostId,
    this.postContent,
    this.reportedCommentId,
    this.commentContent,
    this.taskId,
    this.taskTitle,
    this.disputeReason,
    required this.reason,
    this.description,
    this.violationType,
    this.severity,
    this.suggestedAction,
    this.actualAction,
    required this.createdAt,
    this.resolvedAt,
  });

  String get statusDisplay {
    switch (status) {
      case ReportStatus.submitted:
        return 'Submitted';
      case ReportStatus.assigned:
        return 'In Review';
      case ReportStatus.reviewed:
        return 'Reviewed';
      case ReportStatus.resolved:
        return 'Resolved';
    }
  }

  Color get statusColor {
    switch (status) {
      case ReportStatus.submitted:
        return const Color(0xFFE9C46A); // Yellow
      case ReportStatus.assigned:
        return const Color(0xFF3498DB); // Blue
      case ReportStatus.reviewed:
        return const Color(0xFF9B59B6); // Purple
      case ReportStatus.resolved:
        return const Color(0xFF69B578); // Green
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
      default:
        return const Color(0xFF6B7280);
    }
  }

  String get severityDisplay {
    switch (severity) {
      case Severity.minor:
        return 'Minor';
      case Severity.moderate:
        return 'Moderate';
      case Severity.severe:
        return 'Severe';
      default:
        return 'Unknown';
    }
  }

  String get suggestedActionDisplay {
    switch (suggestedAction) {
      case SuggestedAction.warning:
        return '⚠️ Warning';
      case SuggestedAction.suspend7d:
        return '⛔ Suspend 7 days';
      case SuggestedAction.suspend14d:
        return '⛔ Suspend 14 days';
      case SuggestedAction.suspend30d:
        return '⛔ Suspend 30 days';
      case SuggestedAction.ban:
        return '🚫 Ban';
      default:
        return '—';
    }
  }

  String get violationTypeDisplay {
    switch (violationType) {
      case ViolationType.harassment:
        return 'Harassment';
      case ViolationType.hateSpeech:
        return 'Hate Speech';
      case ViolationType.inappropriateContent:
        return 'Inappropriate Content';
      case ViolationType.spamScam:
        return 'Spam/Scam';
      case ViolationType.privacyViolation:
        return 'Privacy Violation';
      case ViolationType.impersonation:
        return 'Impersonation';
      case ViolationType.taskNoShow:
        return 'Task: No-show';
      case ViolationType.taskPoorQuality:
        return 'Task: Poor Quality';
      case ViolationType.taskPropertyDamage:
        return 'Task: Property Damage';
      case ViolationType.taskUnsafeConditions:
        return 'Task: Unsafe Conditions';
      case ViolationType.threatsViolence:
        return 'Threats of Violence';
      default:
        return 'Unknown';
    }
  }

 
  bool get isUrgent {
    // Check violation type first
    if (violationType == ViolationType.privacyViolation ||
        violationType == ViolationType.threatsViolence) {
      return true;
    }
    // Then check severity
    if (severity == Severity.severe) {
      return true;
    }
    return false;
  }

  factory Report.fromJson(Map<String, dynamic> json) {
    return Report(
      id: json['reportId'] as int,
      reportType: _parseReportType(json['reportType'] as String),
      reporterUserId: json['reporterUserId'] as int,
      reporterName: json['reporterName'] as String? ?? 'Unknown',
      status: _parseStatus(json['status'] as String),
      adminId: json['adminId'] as int?,
      adminName: json['adminName'] as String?,
      reportedUserId: json['reportedUserId'] as int?,
      reportedUserName: json['reportedUserName'] as String?,
      reportedPostId: json['reportedPostId'] as int?,
      postContent: json['postContent'] as String?,
      reportedCommentId: json['reportedCommentId'] as int?,
      commentContent: json['commentContent'] as String?,
      taskId: json['taskId'] as int?,
      taskTitle: json['taskTitle'] as String?,
      disputeReason: json['disputeReason'] as String?,
      reason: json['reason'] as String? ?? '',
      description: json['description'] as String?,
      violationType: json['violationType'] != null 
          ? _parseViolationType(json['violationType'] as String) 
          : null,
      severity: json['severity'] != null 
          ? _parseSeverity(json['severity'] as String) 
          : null,
      suggestedAction: json['suggestedAction'] != null 
          ? _parseSuggestedAction(json['suggestedAction'] as String) 
          : null,
      actualAction: json['actualAction'] != null 
          ? _parseSuggestedAction(json['actualAction'] as String) 
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      resolvedAt: json['resolvedAt'] != null 
          ? DateTime.parse(json['resolvedAt'] as String) 
          : null,
    );
  }

  static ReportType _parseReportType(String value) {
    switch (value.toUpperCase()) {
      case 'USER': return ReportType.user;
      case 'POST': return ReportType.post;
      case 'COMMENT': return ReportType.comment;
      case 'TASK_DISPUTE': return ReportType.taskDispute;
      default: return ReportType.user;
    }
  }

  static ReportStatus _parseStatus(String value) {
    switch (value.toUpperCase()) {
      case 'SUBMITTED': return ReportStatus.submitted;
      case 'ASSIGNED': return ReportStatus.assigned;
      case 'REVIEWED': return ReportStatus.reviewed;
      case 'RESOLVED': return ReportStatus.resolved;
      default: return ReportStatus.submitted;
    }
  }

  static ViolationType _parseViolationType(String value) {
    switch (value.toUpperCase()) {
      case 'HARASSMENT': return ViolationType.harassment;
      case 'HATE_SPEECH': return ViolationType.hateSpeech;
      case 'INAPPROPRIATE_CONTENT': return ViolationType.inappropriateContent;
      case 'SPAM_SCAM': return ViolationType.spamScam;
      case 'PRIVACY_VIOLATION': return ViolationType.privacyViolation;
      case 'IMPERSONATION': return ViolationType.impersonation;
      case 'TASK_NO_SHOW': return ViolationType.taskNoShow;
      case 'TASK_POOR_QUALITY': return ViolationType.taskPoorQuality;
      case 'TASK_PROPERTY_DAMAGE': return ViolationType.taskPropertyDamage;
      case 'TASK_UNSAFE_CONDITIONS': return ViolationType.taskUnsafeConditions;
      case 'THREATS_VIOLENCE': return ViolationType.threatsViolence;
      default: return ViolationType.harassment;
    }
  }

  static Severity _parseSeverity(String value) {
    switch (value.toUpperCase()) {
      case 'MINOR': return Severity.minor;
      case 'MODERATE': return Severity.moderate;
      case 'SEVERE': return Severity.severe;
      default: return Severity.minor;
    }
  }

  static SuggestedAction _parseSuggestedAction(String value) {
    switch (value.toUpperCase()) {
      case 'WARNING': return SuggestedAction.warning;
      case 'SUSPEND_7D': return SuggestedAction.suspend7d;
      case 'SUSPEND_14D': return SuggestedAction.suspend14d;
      case 'SUSPEND_30D': return SuggestedAction.suspend30d;
      case 'BAN': return SuggestedAction.ban;
      default: return SuggestedAction.warning;
    }
  }
}