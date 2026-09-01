// admin/lib/screens/settings/settings_screen.dart

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.settings,
            size: 64,
            color: AppColors.primaryTeal.withValues(alpha: 0.3),
          ),
          const SizedBox(height: 16),
          Text(
            'Settings',
            style: GoogleFonts.poppins(
              fontSize: 24,
              fontWeight: FontWeight.w600,
              color: AppColors.charcoal,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Coming soon!',
            style: GoogleFonts.openSans(
              fontSize: 16,
              color: AppColors.textGrey,
            ),
          ),
        ],
      ),
    );
  }
}