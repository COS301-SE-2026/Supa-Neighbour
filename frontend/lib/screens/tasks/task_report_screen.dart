import 'package:flutter/material.dart';
import '../reports/report_screen.dart';

/// Thin wrapper kept for backward compatibility.
/// All existing call sites can keep using `TaskReportScreen` unchanged —
/// it now just delegates to the unified `ReportScreen`.
class TaskReportScreen extends StatelessWidget {
  final int taskId;
  final String taskTitle;

  const TaskReportScreen({
    super.key,
    required this.taskId,
    required this.taskTitle,
  });

  @override
  Widget build(BuildContext context) {
    return ReportScreen(
      targetType: ReportTargetType.task,
      targetId: taskId,
      targetPreview: taskTitle,
    );
  }
}