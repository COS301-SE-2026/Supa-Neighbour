import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:supa_neighbour/models/verification_model.dart';
import '../../models/task_model.dart';
import '../../constants/app_colors.dart';
import '../../providers/service_providers.dart';
import '../../widgets/bottom_nav_bar.dart';
import '../leaderboard/helper_profile_preview_screen.dart';

class TaskAwaitingApprovalScreen extends ConsumerWidget {
  final Task task;

  const TaskAwaitingApprovalScreen({
    super.key,
    required this.task,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final verifications = ref.watch(
      completionVerificationsProvider(int.tryParse(task.id) ?? -1),
    );

    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Details',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFFFF9800).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Icon(
                    Icons.hourglass_top,
                    size: 16,
                    color: Color(0xFFFF9800),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'Awaiting Approval',
                    style: GoogleFonts.openSans(
                      color: const Color(0xFFFF9800),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    _getCategoryIcon(task.category),
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    task.category,
                    style: GoogleFonts.openSans(
                      color: AppColors.primaryTeal(context),
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Text(
              task.title,
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            GestureDetector(
              onTap: () {
                final requesterId = int.tryParse(task.createdBy);
                if (requesterId != null) {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => HelperProfilePreviewScreen(
                        helperId: requesterId,
                        taskId: task.id,
                        showRequestButton: false,
                        isUserId: true,
                      ),
                    ),
                  );
                }
              },
              child: Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(Icons.person, color: AppColors.primaryTeal(context), size: 20),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        'Requester: ${task.requesterName}',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                        ),
                      ),
                    ),
                    Icon(Icons.chevron_right, color: AppColors.primaryTeal(context), size: 20),
                  ],
                ),
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFE9C46A).withValues(alpha: 0.2),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Row(
                children: [
                  const Icon(Icons.stars, color: Color(0xFFE9C46A), size: 32),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'XP Reward',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 12,
                        ),
                      ),
                      Text(
                        '+${task.xpReward} XP',
                        style: GoogleFonts.poppins(
                          color: AppColors.charcoal(context),
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withValues(alpha: 0.04),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Row(
                children: [
                  Expanded(
                    child: Row(
                      children: [
                        Icon(Icons.calendar_today, color: AppColors.primaryTeal(context), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          '${task.date.day}/${task.date.month}/${task.date.year}',
                          style: GoogleFonts.openSans(
                            color: AppColors.charcoal(context),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                  Expanded(
                    child: Row(
                      children: [
                        Icon(Icons.access_time, color: AppColors.primaryTeal(context), size: 20),
                        const SizedBox(width: 12),
                        Text(
                          task.time.format(context),
                          style: GoogleFonts.openSans(
                            color: AppColors.charcoal(context),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'Instructions',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                task.instructions,
                style: GoogleFonts.openSans(
                  color: AppColors.charcoal(context),
                  fontSize: 14,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 24),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.2),
                  width: 1,
                ),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.hourglass_empty,
                    color: AppColors.primaryTeal(context),
                    size: 24,
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      'Task is awaiting approval from the requester. You will be notified once they confirm completion.',
                      style: GoogleFonts.openSans(
                        color: AppColors.charcoal(context),
                        fontSize: 14,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            verifications.when(
              loading: () => const Padding(
                padding: EdgeInsets.only(bottom: 16),
                child: Center(child: CircularProgressIndicator(strokeWidth: 2)),
              ),
              error: (e, st) {
                debugPrint('[verification] load failed: $e');
                return Padding(
                  padding: const EdgeInsets.only(bottom: 16),
                  child: Text('Verification error: $e',
                      style: const TextStyle(color: Colors.red, fontSize: 12)),
                );
              }, // non-critical, hide on failure
              data: (list) => list.isEmpty
                  ? const SizedBox.shrink()
                  : Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildVerificationCard(context, list),
                    ),
            ),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Your Completion Note',
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    task.completionNote ?? 'No note provided',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            if (task.completionPhotos != null && task.completionPhotos!.isNotEmpty)
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Your Completion Photos',
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  SizedBox(
                    height: 120,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: task.completionPhotos!.length,
                      itemBuilder: (context, index) {
                        return Container(
                          width: 120,
                          height: 120,
                          margin: const EdgeInsets.only(right: 8),
                          decoration: BoxDecoration(
                            color: AppColors.surfaceGrey(context),
                            borderRadius: BorderRadius.circular(8),
                            image: DecorationImage(
                              image: NetworkImage(task.completionPhotos![index]),
                              fit: BoxFit.cover,
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                ],
              ),
            const SizedBox(height: 32),
          ],
        ),
      ),
      bottomNavigationBar: BottomNavBar(
        currentIndex: 1,
       onTap: (_) {},
      ),
    );
  }

  Widget _buildVerificationCard(
      BuildContext context, List<VerificationResult> list) {
    final verified = list.where((r) => r.status == 'VERIFIED').length;
    final review = list.where((r) => r.status == 'NEEDS_REVIEW').length;
    final failed = list.where((r) => r.status == 'FAILED').length;
    final allGood = verified == list.length;
    final aiRan = list.any((r) => r.aiInsight != null);
    final color =
        allGood ? AppColors.primaryTeal(context) : const Color(0xFFFF9800);

    // First result that has a location verdict
    final loc = list.firstWhere(
      (r) => r.locationVerified != null,
      orElse: () => list.first,
    );
    final insight = list
        .map((r) => r.aiInsight)
        .firstWhere((i) => i != null, orElse: () => null);

    String headline;
    if (allGood) {
      headline = 'Photos verified';
    } else if (failed > 0) {
      headline = '$failed photo(s) did not pass checks';
    } else {
      headline = '$review photo(s) flagged for manual review';
    }

    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.08),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withValues(alpha: 0.3)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(allGood ? Icons.verified : Icons.flag_outlined,
                  color: color, size: 22),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  headline,
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            '$verified of ${list.length} photos passed automatic checks.',
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 13,
            ),
          ),
          if (loc.locationVerified != null) ...[
            const SizedBox(height: 6),
            Row(
              children: [
                Icon(
                  loc.locationVerified! ? Icons.location_on : Icons.location_off,
                  size: 16,
                  color: color,
                ),
                const SizedBox(width: 6),
                Expanded(
                  child: Text(
                    loc.locationVerified!
                        ? 'Completed at the task location'
                        : 'Photo taken ${loc.distanceM?.round() ?? '?'} m from the task',
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 13,
                    ),
                  ),
                ),
              ],
            ),
          ],
          if (!aiRan) ...[
            const SizedBox(height: 6),
            Text(
              'Automatic photo comparison was unavailable, so the requester will review your photos.',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 12,
              ),
            ),
          ],
          if (insight != null) ...[
            const SizedBox(height: 6),
            Text(
              insight,
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 12,
                fontStyle: FontStyle.italic,
              ),
            ),
          ],
        ],
      ),
    );
  }

  IconData _getCategoryIcon(String category) {
    switch (category) {
      case 'Medical Assistance':
        return Icons.medical_services;
      case 'Pet Care':
        return Icons.pets;
      case 'Technology Support':
        return Icons.computer;
      case 'Transportation Support':
        return Icons.directions_car;
      case 'Home Repair':
        return Icons.home_repair_service;
      default:
        return Icons.assignment;
    }
  }
}