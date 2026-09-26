// admin/lib/screens/endorsement/endorsement_moderation_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart';
import '../../providers/service_providers.dart';

class EndorsementModerationScreen extends ConsumerStatefulWidget {
  const EndorsementModerationScreen({super.key});

  @override
  ConsumerState<EndorsementModerationScreen> createState() =>
      _EndorsementModerationScreenState();
}

class _EndorsementModerationScreenState
    extends ConsumerState<EndorsementModerationScreen> {
  List<SuspiciousEndorsement> _patterns = [];
  bool _isLoading = true;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadPatterns();
  }

  Future<void> _loadPatterns() async {
    if (!mounted) return;
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final service = ref.read(adminEndorsementServiceProvider);
      final patterns = await service.getSuspiciousPatterns(status: null);

      if (!mounted) return;
      setState(() {
        _patterns = patterns.where((p) => p.status != 'dismiss').toList();
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _errorMessage = e.toString().replaceFirst('Exception: ', '');
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Header row with back button
        Row(
          children: [
            IconButton(
              onPressed: () => Navigator.pop(context),
              icon: const Icon(Icons.arrow_back),
              tooltip: 'Back to Trust Graph',
            ),
            const SizedBox(width: 8),
            Text(
              'Flagged Endorsements',
              style: GoogleFonts.poppins(
                fontSize: 20,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Body
        Expanded(child: _buildBody()),
      ],
    );
  }

  Widget _buildBody() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_errorMessage != null) {
      return _buildErrorState();
    }

    if (_patterns.isEmpty) {
      return _buildEmptyState();
    }

    return _buildPatternList();
  }

  Widget _buildPatternList() {
    return ListView.builder(
      itemCount: _patterns.length,
      itemBuilder: (context, index) {
        return _buildPatternCard(_patterns[index]);
      },
    );
  }

  Widget _buildPatternCard(SuspiciousEndorsement pattern) {
    final severityColor = _severityColor(pattern.severity);

    return Container(
      margin: const EdgeInsets.only(bottom: 12),
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: severityColor.withValues(alpha: 0.3)),
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
          // Top row: severity badge + type + date
          Row(
            children: [
              Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 3,
                ),
                decoration: BoxDecoration(
                  color: severityColor.withValues(alpha: 0.15),
                  borderRadius: BorderRadius.circular(10),
                ),
                child: Text(
                  pattern.severity.toUpperCase(),
                  style: GoogleFonts.openSans(
                    fontSize: 10,
                    fontWeight: FontWeight.w700,
                    color: severityColor,
                  ),
                ),
              ),
              if (pattern.status == 'investigate') ...[
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                  decoration: BoxDecoration(
                    color: AppColors.primaryTeal.withValues(alpha: 0.15),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    'INVESTIGATING',
                    style: GoogleFonts.openSans(fontSize: 9, fontWeight: FontWeight.w700, color: AppColors.primaryTeal),
                  ),
                ),
              ],
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  _patternTypeDisplay(pattern.patternType),
                  style: GoogleFonts.poppins(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              Text(
                _formatDate(pattern.detectedAt),
                style: GoogleFonts.openSans(
                  fontSize: 11,
                  color: AppColors.textGrey,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),

          // Description
          Text(
            pattern.description,
            style: GoogleFonts.openSans(
              fontSize: 13,
              color: AppColors.charcoal,
              height: 1.5,
            ),
          ),
          const SizedBox(height: 12),

          // Involved users
          Text(
            'Involved users',
            style: GoogleFonts.openSans(
              fontSize: 12,
              fontWeight: FontWeight.w600,
              color: AppColors.textGrey,
            ),
          ),
          const SizedBox(height: 6),
          Wrap(
            spacing: 6,
            runSpacing: 6,
            children: pattern.involvedUserIds.map((userId) {
              return Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 10,
                  vertical: 4,
                ),
                decoration: BoxDecoration(
                  color: AppColors.surfaceGrey,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  userId,
                  style: GoogleFonts.openSans(
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                ),
              );
            }).toList(),
          ),
          const SizedBox(height: 16),

          // Action buttons
          
                    // Action buttons
          Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: pattern.status == 'investigate'
                      ? null
                      : () => _dismissPattern(pattern),
                  style: OutlinedButton.styleFrom(
                    side: BorderSide(
                      color: pattern.status == 'investigate'
                          ? AppColors.textGrey.withValues(alpha: 0.3)
                          : AppColors.textGrey,
                    ),
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10),
                    ),
                    disabledForegroundColor: AppColors.textGrey.withValues(alpha: 0.4),
                  ),
                  child: Text(
                    'Dismiss',
                    style: GoogleFonts.openSans(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: pattern.status == 'investigate'
                          ? AppColors.textGrey.withValues(alpha: 0.4)
                          : AppColors.textGrey,
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: ElevatedButton(
                  onPressed: pattern.status == 'investigate'
                      ? null
                      : () => _investigatePattern(pattern),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: severityColor,
                    disabledBackgroundColor: AppColors.textGrey.withValues(alpha: 0.25),
                    padding: const EdgeInsets.symmetric(vertical: 10),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10),
                    ),
                  ),
                  child: Text(
                    pattern.status == 'investigate' ? 'Investigating' : 'Investigate',
                    style: GoogleFonts.openSans(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: pattern.status == 'investigate'
                          ? AppColors.textGrey.withValues(alpha: 0.6)
                          : Colors.white,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Color _severityColor(String severity) {
    switch (severity.toLowerCase()) {
      case 'high':
        return AppColors.error;
      case 'medium':
        return AppColors.citrusYellow;
      case 'low':
      default:
        return AppColors.textGrey;
    }
  }

  String _patternTypeDisplay(String type) {
    switch (type) {
      case 'mutual_ring':
        return 'Mutual Endorsement Ring';
      case 'sudden_spike':
        return 'Sudden Endorsement Spike';
      case 'island_group':
        return 'Isolated Endorsement Group';
      case 'timestamp_anomaly':
        return 'Timestamp Anomaly';
      default:
        return type;
    }
  }

  void _dismissPattern(SuspiciousEndorsement pattern) async {
    try{
      final service = ref.read(adminEndorsementServiceProvider);
      await service.updateFlagStatus(pattern.patternId, 'dismiss');
      if(!mounted) return;
      setState(() {
        _patterns = _patterns
            .where((p) => p.patternId != pattern.patternId)
            .toList();
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Pattern dismissed'),
          duration: Duration(seconds: 1),
        ),
      );
    }catch(e){
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(
          'Failed to dismiss: ${e.toString().replaceFirst('Exception: ', '')}',
        )),
      );
    }
  }

  Future<void> _investigatePattern(SuspiciousEndorsement pattern) async {
    SuspiciousEndorsement updated;
    try {
      final service = ref.read(adminEndorsementServiceProvider);
      updated = await service.updateFlagStatus(pattern.patternId, 'investigate');
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(
          'Failed to update status: ${e.toString().replaceFirst('Exception: ', '')}',
        )),
      );
      return;
    }

    if (!mounted) return;
    setState(() {
      _patterns = _patterns
          .map((p) => p.patternId == pattern.patternId ? updated : p)
          .toList();
    });

    if (!mounted) return;
    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        title: Text('Investigate Pattern', style: GoogleFonts.poppins(fontWeight: FontWeight.w600)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Pattern: ${_patternTypeDisplay(pattern.patternType)}',
                style: GoogleFonts.openSans(fontSize: 13, fontWeight: FontWeight.w600)),
            const SizedBox(height: 8),
            Text(pattern.description, style: GoogleFonts.openSans(fontSize: 13)),
            const SizedBox(height: 16),
            Text(
              'Next steps: view the involved users\' profiles and their endorsement history to decide on action.',
              style: GoogleFonts.openSans(fontSize: 12, color: AppColors.textGrey),
            ),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(dialogContext), child: const Text('Close')),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.verified_outlined,
              size: 64,
              color: AppColors.success.withValues(alpha: 0.6),
            ),
            const SizedBox(height: 16),
            Text(
              'No flagged patterns',
              style: GoogleFonts.poppins(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Your zone\'s endorsement activity is healthy — no abuse patterns detected.',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                fontSize: 14,
                color: AppColors.textGrey,
                height: 1.4,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildErrorState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(
              Icons.error_outline,
              size: 64,
              color: AppColors.error,
            ),
            const SizedBox(height: 16),
            Text(
              'Failed to load patterns',
              style: GoogleFonts.poppins(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              _errorMessage ?? 'Please try again later',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                fontSize: 14,
                color: AppColors.textGrey,
              ),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: _loadPatterns,
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryTeal,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Retry',
                style: GoogleFonts.openSans(color: Colors.white),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}