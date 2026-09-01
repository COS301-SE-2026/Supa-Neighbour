// admin/lib/widgets/admin_sidebar.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';

class AdminSidebar extends StatelessWidget {
  final int selectedIndex;

  const AdminSidebar({
    super.key,
    required this.selectedIndex,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 240,
      color: AppColors.charcoal,
      child: Column(
        children: [
          // Logo Section
          Container(
            padding: const EdgeInsets.symmetric(vertical: 24),
            child: Column(
              children: [
                const Icon(
                  Icons.admin_panel_settings,
                  size: 40,
                  color: AppColors.primaryTeal,
                ),
                const SizedBox(height: 8),
                Text(
                  'Supa Neighbour',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                Text(
                  'Admin',
                  style: GoogleFonts.openSans(
                    fontSize: 12,
                    color: AppColors.textGrey,
                  ),
                ),
              ],
            ),
          ),
          const Divider(
            color: AppColors.textGrey,
            thickness: 0.5,
            indent: 16,
            endIndent: 16,
          ),
          // Navigation Items
          Expanded(
            child: Column(
              children: [
                _buildNavItem(
                  context: context,
                  icon: Icons.dashboard,
                  label: 'Dashboard',
                  index: 0,
                  route: '/dashboard',
                ),
                _buildNavItem(
                  context: context,
                  icon: Icons.flag,
                  label: 'Reports',
                  index: 1,
                  route: '/reports',
                ),
                _buildNavItem(
                  context: context,
                  icon: Icons.people,
                  label: 'Users',
                  index: 2,
                  route: '/users',
                ),
                _buildNavItem(
                  context: context,
                  icon: Icons.location_on,
                  label: 'Zones',
                  index: 3,
                  route: '/zones',
                ),
                _buildNavItem(
                  context: context,
                  icon: Icons.settings,
                  label: 'Settings',
                  index: 4,
                  route: '/settings',
                ),
              ],
            ),
          ),
          // Bottom: Logout
          const Divider(
            color: AppColors.textGrey,
            thickness: 0.5,
            indent: 16,
            endIndent: 16,
          ),
          _buildNavItem(
            context: context,
            icon: Icons.logout,
            label: 'Logout',
            index: -1,
            route: '/login',
            isLogout: true,
          ),
          const SizedBox(height: 16),
        ],
      ),
    );
  }

  Widget _buildNavItem({
    required BuildContext context,
    required IconData icon,
    required String label,
    required int index,
    required String route,
    bool isLogout = false,
  }) {
    final isSelected = selectedIndex == index;

    return InkWell(
      onTap: () {
        if (isLogout) {
          _showLogoutDialog(context);
        } else {
          // Use pushReplacement for navigation
          context.pushReplacement(route);
        }
      },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
        decoration: BoxDecoration(
          color: isSelected ? AppColors.primaryTeal : Colors.transparent,
          borderRadius: BorderRadius.circular(8),
        ),
        child: Row(
          children: [
            Icon(
              icon,
              color: isSelected 
                  ? Colors.white 
                  : (isLogout ? AppColors.error : AppColors.textGrey),
              size: 20,
            ),
            const SizedBox(width: 12),
            Text(
              label,
              style: GoogleFonts.openSans(
                fontSize: 14,
                fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
                color: isSelected 
                    ? Colors.white 
                    : (isLogout ? AppColors.error : AppColors.textGrey),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showLogoutDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Logout'),
        content: const Text('Are you sure you want to logout?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(dialogContext);
              context.pushReplacement('/login');
            },
            style: TextButton.styleFrom(
              foregroundColor: AppColors.error,
            ),
            child: const Text('Logout'),
          ),
        ],
      ),
    );
  }
}