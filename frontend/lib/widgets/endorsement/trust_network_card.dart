// frontend/lib/widgets/endorsement/trust_network_card.dart

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart' hide AppColors;
import '../../constants/app_colors.dart';
import 'mini_graph_preview.dart';

/// Compact card shown on the profile screen that summarises the current
/// user's endorsement network: total count, top skills, and a mini
/// graph preview of the 1-hop neighbourhood.
///
/// Tapping the card (or the "See full network" link) fires [onViewNetwork].
class TrustNetworkCard extends StatelessWidget {
  final EndorsementSummary summary;
  final VoidCallback? onViewNetwork;

  const TrustNetworkCard({
    super.key,
    required this.summary,
    this.onViewNetwork,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).brightness == Brightness.dark
            ? AppColors.surfaceGrey(context)
            : Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Header row
          Row(
            children: [
              Container(
                padding: const EdgeInsets.all(6),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(
                  Icons.hub_outlined,
                  size: 16,
                  color: AppColors.primaryTeal(context),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Text(
                  'Trust Network',
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              if (summary.totalCount > 0)
                GestureDetector(
                  onTap: onViewNetwork,
                  child: Text(
                    'View network →',
                    style: GoogleFonts.openSans(
                      color: AppColors.primaryTeal(context),
                      fontSize: 13,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 14),

          // Main content which will depend on whether the user has endorsements
          if (summary.totalCount == 0)
            _buildEmptyState(context)
          else
            _buildPopulatedState(context),
        ],
      ),
    );
  }

  Widget _buildEmptyState(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'No endorsements yet',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 13,
          ),
        ),
        const SizedBox(height: 6),
        Text(
          'Complete tasks and get endorsed by neighbours to build your trust network.',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 12,
            height: 1.4,
          ),
        ),
      ],
    );
  }

  Widget _buildPopulatedState(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Big stat
        Row(
          crossAxisAlignment: CrossAxisAlignment.baseline,
          textBaseline: TextBaseline.alphabetic,
          children: [
            Text(
              '${summary.totalCount}',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 28,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(width: 8),
            Text(
              summary.totalCount == 1
                  ? 'neighbour has endorsed you'
                  : 'neighbours have endorsed you',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 13,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),

        // Top skill chips
        if (summary.topSkills.isNotEmpty) ...[
          Wrap(
            spacing: 8,
            runSpacing: 6,
            children: summary.topSkills.map((skill) {
              return Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 5,
                ),
                decoration: BoxDecoration(
                  color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  '${skill.displayName} ×${skill.count}',
                  style: GoogleFonts.openSans(
                    color: AppColors.primaryTeal(context),
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              );
            }).toList(),
          ),
          const SizedBox(height: 14),
        ],

        // Mini graph + "See network"
        Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            MiniGraphPreview(
              nodes: summary.miniGraphNodes,
              edges: summary.miniGraphEdges,
              width: 180,
              height: 60,
            ),
            const Spacer(),
            if (onViewNetwork != null)
              IconButton(
                onPressed: onViewNetwork,
                icon: Icon(
                  Icons.arrow_forward_ios,
                  size: 14,
                  color: AppColors.textGrey(context),
                ),
                tooltip: 'See full network',
              ),
          ],
        ),
      ],
    );
  }
}