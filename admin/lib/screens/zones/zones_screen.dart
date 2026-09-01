// admin/lib/screens/zones/zones_screen.dart

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import '../../widgets/admin_scaffold.dart';

class ZonesScreen extends StatelessWidget {
  const ZonesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return AdminScaffold(
      selectedIndex: 3,
      title: 'Neighbourhood Zones',
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.location_on,
              size: 64,
              color: AppColors.primaryTeal.withOpacity(0.3),
            ),
            const SizedBox(height: 16),
            Text(
              'Neighbourhood Zones',
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
      ),
    );
  }
}