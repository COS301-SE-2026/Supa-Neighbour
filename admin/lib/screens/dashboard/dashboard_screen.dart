// admin/lib/screens/dashboard/dashboard_screen.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart';
import '../../providers/service_providers.dart';

class DashboardScreen extends ConsumerStatefulWidget {
  const DashboardScreen({super.key});

  @override
  ConsumerState<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends ConsumerState<DashboardScreen> {
  ZoneInsights? _insights;
  int _flaggedCount = 0;
  bool _isLoadingNetwork = true;

  @override
  void initState() {
    super.initState();
    _loadNetworkHealth();
  }

  Future<void> _loadNetworkHealth() async {
    try {
      final service = ref.read(adminEndorsementServiceProvider);
      final results = await Future.wait([
        service.getZoneInsights(),
        service.getSuspiciousPatterns(),
      ]);

      if (!mounted) return;
      setState(() {
        _insights = results[0] as ZoneInsights;
        _flaggedCount = (results[1] as List<SuspiciousEndorsement>).length;
        _isLoadingNetwork = false;
      });
    } catch (_) {
      if (!mounted) return;
      setState(() {
        _isLoadingNetwork = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Welcome Section
        Container(
          padding: const EdgeInsets.all(20),
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [
                AppColors.primaryTeal,
                AppColors.primaryTeal.withValues(alpha: 0.8)
              ],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(16),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Welcome back, Admin',
                style: GoogleFonts.poppins(
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                'Here\'s what\'s happening in your community',
                style: GoogleFonts.openSans(
                  fontSize: 14,
                  color: Colors.white.withValues(alpha: 0.8),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 24),

        // Stats Cards
        Row(
          children: [
            _buildStatCard(
              title: 'Total Users',
              value: '1,284',
              icon: Icons.people,
              color: AppColors.primaryTeal,
            ),
            const SizedBox(width: 12),
            _buildStatCard(
              title: 'Pending Reports',
              value: '23',
              icon: Icons.flag,
              color: AppColors.citrusYellow,
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            _buildStatCard(
              title: 'Tasks Completed',
              value: '156',
              icon: Icons.check_circle,
              color: AppColors.success,
            ),
            const SizedBox(width: 12),
            _buildStatCard(
              title: 'Banned Users',
              value: '12',
              icon: Icons.block,
              color: AppColors.error,
            ),
          ],
        ),
        const SizedBox(height: 24),
        // Network Health
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'Network Health',
              style: GoogleFonts.poppins(
                fontSize: 18,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal,
              ),
            ),
            if (_flaggedCount > 0)
              TextButton.icon(
                onPressed: () => context.go('/trust-graph'),
                icon: const Icon(Icons.flag_outlined, size: 16,
                    color: AppColors.error),
                label: Text(
                  '$_flaggedCount flagged',
                  style: GoogleFonts.openSans(
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                    color: AppColors.error,
                  ),
                ),
              ),
          ],
        ),
        const SizedBox(height: 12),
        _buildNetworkHealthCard(),
        const SizedBox(height: 24),

        // Quick Actions
        Text(
          'Quick Actions',
          style: GoogleFonts.poppins(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: AppColors.charcoal,
          ),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            _buildQuickAction(
              title: 'Reports',
              icon: Icons.flag,
              color: AppColors.error,
              onTap: () {
                context.go('/reports');
              },
            ),
            const SizedBox(width: 12),
            _buildQuickAction(
              title: 'Users',
              icon: Icons.people,
              color: AppColors.primaryTeal,
              onTap: () {
                context.go('/users');
              },
            ),
            const SizedBox(width: 12),
            _buildQuickAction(
              title: 'Zones',
              icon: Icons.location_on,
              color: AppColors.citrusYellow,
              onTap: () {
                context.go('/zones');
              },
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildStatCard({
    required String title,
    required String value,
    required IconData icon,
    required Color color,
  }) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha:0.04),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: color.withValues(alpha:0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Icon(icon, color: color, size: 20),
                ),
                const SizedBox(width: 8),
                Text(
                  value,
                  style: GoogleFonts.poppins(
                    fontSize: 20,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: GoogleFonts.openSans(
                fontSize: 12,
                color: AppColors.textGrey,
              ),
            ),
          ],
        ),
      ),
    );
  }

    Widget _buildNetworkHealthCard() {
    if (_isLoadingNetwork) {
      return Container(
        padding: const EdgeInsets.all(24),
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
        child: const Center(child: CircularProgressIndicator()),
      );
    }

    final insights = _insights;
    if (insights == null) {
      return Container(
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
        child: Text(
          'No endorsement data available',
          style: GoogleFonts.openSans(
            fontSize: 13,
            color: AppColors.textGrey,
          ),
        ),
      );
    }

    return GestureDetector(
      onTap: () => context.go('/trust-graph'),
      child: Container(
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
        child: Column(
          children: [
            Row(
              children: [
                _buildNetworkMetric(
                  label: 'Endorsements',
                  value: insights.totalEndorsements.toString(),
                  icon: Icons.hub_outlined,
                  color: AppColors.primaryTeal,
                ),
                const SizedBox(width: 12),
                _buildNetworkMetric(
                  label: 'Clusters',
                  value: insights.clusterCount.toString(),
                  icon: Icons.bubble_chart_outlined,
                  color: AppColors.citrusYellow,
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _buildNetworkMetric(
                  label: 'Isolated Users',
                  value: insights.isolatedUserCount.toString(),
                  icon: Icons.person_off_outlined,
                  color: AppColors.error,
                ),
                const SizedBox(width: 12),
                _buildNetworkMetric(
                  label: 'Flagged',
                  value: _flaggedCount.toString(),
                  icon: Icons.flag_outlined,
                  color: _flaggedCount > 0
                      ? AppColors.error
                      : AppColors.success,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNetworkMetric({
    required String label,
    required String value,
    required IconData icon,
    required Color color,
  }) {
    return Expanded(
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(6),
            decoration: BoxDecoration(
              color: color.withValues(alpha: 0.1),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(icon, color: color, size: 16),
          ),
          const SizedBox(width: 8),
          Flexible(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  value,
                  style: GoogleFonts.poppins(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
                Text(
                  label,
                  style: GoogleFonts.openSans(
                    fontSize: 10,
                    color: AppColors.textGrey,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildQuickAction({
    required String title,
    required IconData icon,
    required Color color,
    required VoidCallback onTap,
  }) {
    return Expanded(
      child: GestureDetector(
        onTap: onTap,
        child: Container(
          padding: const EdgeInsets.symmetric(vertical: 16),
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
          child: Column(
            children: [
              Icon(icon, color: color, size: 28),
              const SizedBox(height: 4),
              Text(
                title,
                style: GoogleFonts.openSans(
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                  color: AppColors.charcoal,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}