// admin/lib/screens/dashboard/dashboard_screen.dart

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Dashboard'),
        backgroundColor: AppColors.background,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              // TODO: Implement logout
              // For now, go back to login
              Navigator.pushReplacementNamed(context, '/login');
            },
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Welcome Section
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    AppColors.primaryTeal,
                    AppColors.primaryTeal.withValues(alpha: 0.8),
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
                    'Welcome back, Admin 👋',
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
                    // TODO: Navigate to reports
                  },
                ),
                const SizedBox(width: 12),
                _buildQuickAction(
                  title: 'Users',
                  icon: Icons.people,
                  color: AppColors.primaryTeal,
                  onTap: () {
                    // TODO: Navigate to users
                  },
                ),
                const SizedBox(width: 12),
                _buildQuickAction(
                  title: 'Zones',
                  icon: Icons.location_on,
                  color: AppColors.citrusYellow,
                  onTap: () {
                    // TODO: Navigate to zones
                  },
                ),
              ],
            ),
          ],
        ),
      ),
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
              color: Colors.black.withValues(alpha: 0.04),
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
                    color: color.withValues(alpha: 0.1),
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