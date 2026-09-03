import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:supa_neighbour/providers/theme_mode_provider.dart';
import 'package:supa_neighbour/screens/auth/splash_screen.dart';
import '../../constants/app_colors.dart';
import 'privacy_settings_screen.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../help/help_menu_screen.dart';
import '../../providers/service_providers.dart';
import 'my_reports_screen.dart';

class SettingsScreen extends ConsumerStatefulWidget {
  const SettingsScreen({super.key});

  @override
  ConsumerState<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends ConsumerState<SettingsScreen> {
  bool _locationEnabled = true;
  String _selectedLanguage = 'English';

  Future<void> _confirmAndLogout() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        title: const Text('Sign Out'),
        content: const Text('Are you sure you want to sign out?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            child: Text('Sign Out', style: TextStyle(color: AppColors.error(context))),
          )
        ],
      ),
    );

    if (confirmed != true) return;
    if (!mounted) return;

    try {
      final auth = ref.read(authServiceProvider);
      await auth.logout();
      if (mounted) {
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => const SplashScreen()),
          (route) => false,
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to sign you out. Please try again')),
        );
      }
    }
  }

  Future<void> _sendPasswordResetEmail() async {
    final email = fb.FirebaseAuth.instance.currentUser?.email;
    if (email == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'No email associated with this account.',
            style: GoogleFonts.openSans(),
          ),
          backgroundColor: AppColors.error(context),
        ),
      );
      return;
    }

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Text(
          'Change Password',
          style: GoogleFonts.poppins(
            fontWeight: FontWeight.w600,
            color: AppColors.charcoal(context),
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'We\'ll send a password reset link to:',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              email,
              style: GoogleFonts.openSans(
                color: AppColors.primaryTeal(context),
                fontWeight: FontWeight.w600,
                fontSize: 14,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Check your inbox and follow the link to set a new password.',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 13,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(dialogContext, false),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(dialogContext, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: Text(
              'Send Link',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );

    if (confirmed != true) return;
    if (!mounted) return;

    try {
      await fb.FirebaseAuth.instance.sendPasswordResetEmail(email: email);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              'Reset link sent to $email. Check your inbox.',
              style: GoogleFonts.openSans(),
            ),
            backgroundColor: AppColors.primaryTeal(context),
          ),
        );
      }
    } on fb.FirebaseAuthException catch (e) {
      if (mounted) {
        final message = e.code == 'invalid-email'
            ? 'Invalid email address.'
            : 'Failed to send reset link. Please try again.';
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(message, style: GoogleFonts.openSans()),
            backgroundColor: AppColors.error(context),
          ),
        );
      }
    }
  }

  Future<void> _confirmAndDeleteAccount() async {
    final TextEditingController confirmController = TextEditingController();

    final confirmed = await showDialog<bool>(
      context: context,
      builder: (dialogContext) => StatefulBuilder(
        builder: (dialogContext, setDialogState) {
          final isMatch = confirmController.text.trim().toUpperCase() == 'DELETE';

          return AlertDialog(
            title: Text('Delete Account', style: TextStyle(color: AppColors.error(context))),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'This permanently deleted your account, tasks and history. This cannot be undone.'
                ),
                const SizedBox(height: 16),
                Text('Text DELETE to confirm: ', style: TextStyle(color: AppColors.textGrey(context))),
                const SizedBox(height: 8),
                TextField(
                  controller: confirmController,
                  onChanged: (_) => setDialogState(() {}),
                  decoration: const InputDecoration(border: OutlineInputBorder())
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(dialogContext, false),
                child: const Text('Cancel'),
              ),
              TextButton(
                onPressed: isMatch ? () => Navigator.pop(dialogContext, true) : null,
                child: Text('Delete Forever',
                    style: TextStyle(
                      color: isMatch ? AppColors.error(context) : AppColors.textGrey(context),
                    ))
              )
            ],
          );
        },
      ),
    );

    if (confirmed != true || !mounted) return;
    try {
      final auth = ref.read(authServiceProvider);
      await auth.deleteAccount();
      if (mounted) {
        Navigator.of(context).pushAndRemoveUntil(
          MaterialPageRoute(builder: (context) => const SplashScreen()),
          (route) => false,
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Failed to delete account. Please try again')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final themeMode = ref.watch(themeModeProvider);
    bool isDarkMode = themeMode == ThemeMode.dark;

    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.primaryTeal(context)),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
        title: Text(
          'Settings',
          style: GoogleFonts.poppins(
            color: AppColors.primaryTeal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(height: 16),
            _buildPreferencesSection(isDarkMode),
            const SizedBox(height: 16),
            _buildSecuritySection(),
            const SizedBox(height: 16),
            _buildSupportSection(),
            const SizedBox(height: 16),
            _buildDangerSection(),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildPreferencesSection(bool isDarkMode) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Material(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(16),
        elevation: 2,
        shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
        child: Column(
          children: [
            _buildSectionHeader('Preferences'),
            const Divider(height: 1),
            _buildSwitchTile(
              icon: Icons.location_on_outlined,
              title: 'Location Services',
              subtitle: 'Show nearby tasks in your area',
              value: _locationEnabled,
              onChanged: (value) {
                setState(() {
                  _locationEnabled = value;
                });
              },
            ),
            _buildSwitchTile(
              icon: Icons.dark_mode_outlined,
              title: 'Dark Mode',
              subtitle: 'Switch to dark theme',
              value: isDarkMode,
              onChanged: (value) async {
                try {
                  await ref.read(themeModeProvider.notifier).toggleDarkMode(value);
                } catch (e) {
                  if (mounted) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Failed to save theme preference')),
                    );
                  }
                }
              },
            ),
            _buildDropdownTile(
              icon: Icons.language_outlined,
              title: 'Language',
              subtitle: 'Select your preferred language',
              value: _selectedLanguage,
              items: ['English', 'Spanish', 'French', 'German', 'Portuguese'],
              onChanged: (String? newValue) {
                if (newValue != null) {
                  setState(() {
                    _selectedLanguage = newValue;
                  });
                }
              },
            ),
            _buildSettingsTile(
              icon: Icons.privacy_tip_outlined,
              title: 'Privacy Settings',
              subtitle: 'Manage your privacy preferences',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const PrivacySettingsScreen(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSecuritySection() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Material(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(16),
        elevation: 2,
        shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
        child: Column(
          children: [
            _buildSectionHeader('Security'),
            const Divider(height: 1),
            _buildSettingsTile(
              icon: Icons.lock_outline,
              title: 'Change Password',
              subtitle: 'Update your account password',
              onTap: _sendPasswordResetEmail,
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSupportSection() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Material(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(16),
        elevation: 2,
        shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
        child: Column(
          children: [
            _buildSectionHeader('Support'),
            const Divider(height: 1),
            _buildSettingsTile(
              icon: Icons.help_outline,
              title: 'Help Center',
              subtitle: 'Get help and FAQs',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const HelpMenuScreen(),
                  ),
                );
              },
            ),
            _buildSettingsTile(
              icon: Icons.report_outlined,
              title: 'My Reports',
              subtitle: 'View your submitted reports',
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const MyReportsScreen(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDangerSection() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Material(
        color: AppColors.white(context),
        borderRadius: BorderRadius.circular(16),
        elevation: 2,
        shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
        child: Column(
          children: [
            _buildSectionHeader('Account', isDanger: true),
            const Divider(height: 1),
            _buildSettingsTile(
              icon: Icons.logout,
              title: 'Sign Out',
              subtitle: 'Sign out of your account',
              isDanger: true,
              onTap: () => _confirmAndLogout(),
            ),
            _buildSettingsTile(
              icon: Icons.delete_outline,
              title: 'Delete Account',
              subtitle: 'Permanently delete your account',
              isDanger: true,
              onTap: () => _confirmAndDeleteAccount(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title, {bool isDanger = false}) {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            title,
            style: GoogleFonts.poppins(
              color: isDanger ? AppColors.error(context) : AppColors.primaryTeal(context),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          if (isDanger)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: AppColors.error(context).withValues(alpha: 0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                'DANGER',
                style: GoogleFonts.poppins(
                  color: AppColors.error(context),
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildSettingsTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
    bool isDanger = false,
  }) {
    return Material(
      color: AppColors.white(context),
      borderRadius: BorderRadius.circular(12),
      elevation: 2,
      shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          child: Row(
            children: [
              Icon(
                icon,
                color: isDanger ? AppColors.error(context) : AppColors.primaryTeal(context),
                size: 24,
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: GoogleFonts.poppins(
                        color: isDanger ? AppColors.error(context) : AppColors.charcoal(context),
                        fontSize: 14,
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    Text(
                      subtitle,
                      style: GoogleFonts.openSans(
                        color: AppColors.textGrey(context),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
              Icon(
                Icons.chevron_right,
                color: AppColors.textGrey(context),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSwitchTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required bool value,
    required ValueChanged<bool> onChanged,
  }) {
    return Material(
      color: AppColors.white(context),
      borderRadius: BorderRadius.circular(12),
      elevation: 2,
      shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Row(
          children: [
            Icon(icon, color: AppColors.primaryTeal(context), size: 24),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: GoogleFonts.openSans(
                      color: AppColors.textGrey(context),
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
            Switch(
              value: value,
              onChanged: onChanged,
              activeColor: AppColors.primaryTeal(context),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDropdownTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required String value,
    required List<String> items,
    required ValueChanged<String?> onChanged,
  }) {
    return Material(
      color: AppColors.white(context),
      borderRadius: BorderRadius.circular(12),
      elevation: 2,
      shadowColor: AppColors.charcoal(context).withValues(alpha: 0.04),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Row(
          children: [
            Icon(icon, color: AppColors.primaryTeal(context), size: 24),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: GoogleFonts.openSans(
                      color: AppColors.textGrey(context),
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
            DropdownButton<String>(
              value: value,
              items: items.map((String item) {
                return DropdownMenuItem<String>(
                  value: item,
                  child: Text(
                    item,
                    style: GoogleFonts.openSans(fontSize: 14),
                  ),
                );
              }).toList(),
              onChanged: onChanged,
              underline: const SizedBox(),
              icon: Icon(
                Icons.arrow_drop_down,
                color: AppColors.primaryTeal(context),
              ),
            ),
          ],
        ),
      ),
    );
  }
}