import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';

class PrivacySettingsScreen extends StatefulWidget {
  const PrivacySettingsScreen({super.key});

  @override
  State<PrivacySettingsScreen> createState() => _PrivacySettingsScreenState();
}

class _PrivacySettingsScreenState extends State<PrivacySettingsScreen> {
  bool _showProfilePublic = true;
  bool _showEmailPublic = false;
  bool _showPhonePublic = false;
  bool _showLocationPublic = false;
  bool _allowMessages = true;
  bool _allowTaskRequests = true;
  bool _showOnlineStatus = true;
  bool _allowDataCollection = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.primaryTeal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Privacy Settings',
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
            _buildSectionHeader('Profile Visibility'),
            _buildSwitchTile(
              title: 'Show Profile Publicly',
              subtitle: 'Allow other users to view your profile',
              value: _showProfilePublic,
              onChanged: (value) {
                setState(() {
                  _showProfilePublic = value;
                });
              },
            ),
            _buildSwitchTile(
              title: 'Show Email Address',
              subtitle: 'Display your email on your profile',
              value: _showEmailPublic,
              onChanged: (value) {
                setState(() {
                  _showEmailPublic = value;
                });
              },
            ),
            _buildSwitchTile(
              title: 'Show Phone Number',
              subtitle: 'Display your phone number on your profile',
              value: _showPhonePublic,
              onChanged: (value) {
                setState(() {
                  _showPhonePublic = value;
                });
              },
            ),
            _buildSwitchTile(
              title: 'Show Location',
              subtitle: 'Share your general location with neighbours',
              value: _showLocationPublic,
              onChanged: (value) {
                setState(() {
                  _showLocationPublic = value;
                });
              },
            ),
            const SizedBox(height: 24),
            _buildSectionHeader('Communication'),
            _buildSwitchTile(
              title: 'Allow Messages',
              subtitle: 'Receive messages from other users',
              value: _allowMessages,
              onChanged: (value) {
                setState(() {
                  _allowMessages = value;
                });
              },
            ),
            _buildSwitchTile(
              title: 'Allow Task Requests',
              subtitle: 'Receive task requests from neighbours',
              value: _allowTaskRequests,
              onChanged: (value) {
                setState(() {
                  _allowTaskRequests = value;
                });
              },
            ),
            const SizedBox(height: 24),
            _buildSectionHeader('Online Status'),
            _buildSwitchTile(
              title: 'Show Online Status',
              subtitle: 'Let others know when you\'re active',
              value: _showOnlineStatus,
              onChanged: (value) {
                setState(() {
                  _showOnlineStatus = value;
                });
              },
            ),
            const SizedBox(height: 24),
            _buildSectionHeader('Data & Privacy'),
            _buildSwitchTile(
              title: 'Allow Data Collection',
              subtitle: 'Help us improve the app with anonymous data',
              value: _allowDataCollection,
              onChanged: (value) {
                setState(() {
                  _allowDataCollection = value;
                });
              },
            ),
            const SizedBox(height: 32),
            _buildDangerZone(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Text(
        title,
        style: GoogleFonts.poppins(
          color: AppColors.primaryTeal(context),
          fontSize: 16,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _buildSwitchTile({
    required String title,
    required String subtitle,
    required bool value,
    required ValueChanged<bool> onChanged,
  }) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      decoration: BoxDecoration(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: AppColors.charcoal(context).withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        borderRadius: BorderRadius.circular(12),
        child: SwitchListTile(
          title: Text(
            title,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 15,
              fontWeight: FontWeight.w500,
            ),
          ),
          subtitle: Text(
            subtitle,
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 13,
            ),
          ),
          value: value,
          onChanged: onChanged,
          activeColor: AppColors.primaryTeal(context),
          activeTrackColor: AppColors.primaryTeal(context).withValues(alpha: 0.3),
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
        ),
      ),
    );
  }

  Widget _buildDangerZone() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: AppColors.error(context).withValues(alpha: 0.3),
          width: 1,
        ),
        boxShadow: [
          BoxShadow(
            color: AppColors.charcoal(context).withValues(alpha: 0.04),
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
              Icon(
                Icons.warning_amber_rounded,
                color: AppColors.error(context),
                size: 24,
              ),
              const SizedBox(width: 12),
              Text(
                'Danger Zone',
                style: GoogleFonts.poppins(
                  color: AppColors.error(context),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            'These actions are irreversible. Please proceed with caution.',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 13,
            ),
          ),
          const SizedBox(height: 16),
          Material(
            color: Colors.transparent,
            borderRadius: BorderRadius.circular(12),
            child: SizedBox(
              width: double.infinity,
              child: OutlinedButton(
                onPressed: () {
                  _showDeleteAccountDialog();
                },
                style: OutlinedButton.styleFrom(
                  side: BorderSide(color: AppColors.error(context)),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
                child: Text(
                  'Delete Account',
                  style: GoogleFonts.openSans(
                    color: AppColors.error(context),
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          ),
          const SizedBox(height: 8),
          Material(
            color: Colors.transparent,
            borderRadius: BorderRadius.circular(12),
            child: SizedBox(
              width: double.infinity,
              child: OutlinedButton(
                onPressed: () {
                  _showClearDataDialog();
                },
                style: OutlinedButton.styleFrom(
                  side: BorderSide(color: AppColors.error(context)),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
                child: Text(
                  'Clear All Data',
                  style: GoogleFonts.openSans(
                    color: AppColors.error(context),
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _showDeleteAccountDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Row(
          children: [
            Icon(
              Icons.delete_forever,
              color: AppColors.error(context),
              size: 28,
            ),
            const SizedBox(width: 12),
            Text(
              'Delete Account',
              style: GoogleFonts.poppins(
                color: AppColors.error(context),
                fontSize: 20,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        content: Text(
          'Are you sure you want to delete your account? This action is permanent and cannot be undone. All your data, tasks, and achievements will be lost.',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal(context),
            fontSize: 14,
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('Account deletion initiated'),
                  backgroundColor: AppColors.success(context),
                ),
              );
              Navigator.pushNamedAndRemoveUntil(
                context,
                '/login',
                (route) => false,
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.error(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Delete Permanently',
              style: GoogleFonts.openSans(
                color: AppColors.textLight(context),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  void _showClearDataDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        title: Text(
          'Clear All Data?',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 20,
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Text(
          'This will clear all app data including settings, cache, and preferences. This action cannot be undone.',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal(context),
            fontSize: 14,
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text('Data cleared successfully'),
                  backgroundColor: AppColors.success(context),
                ),
              );
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.error(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Clear Data',
              style: GoogleFonts.openSans(
                color: AppColors.textLight(context),
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }
}