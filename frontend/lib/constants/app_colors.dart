import 'package:flutter/material.dart';

class AppColors {
  static bool _isDark(BuildContext context) =>
      Theme.of(context).brightness == Brightness.dark;

  static Color _pick(BuildContext context, Color light, Color dark) =>
      _isDark(context) ? dark : light;

  // Original set
  static Color primary(BuildContext context) =>
      _pick(context, const Color(0xFF1C9A89), const Color(0xFF2FBFAA));
  static Color secondary(BuildContext context) =>
      _pick(context, const Color(0xFFEAC059), const Color(0xFFEAC059));
  static Color background(BuildContext context) =>
      _pick(context, const Color(0xFFFFFFFF), const Color(0xFF121212));
  static Color lightGrey(BuildContext context) =>
      _pick(context, const Color(0xFFF0F1F5), const Color(0xFF1E1E1E));
  static Color white(BuildContext context) =>
      _pick(context, const Color(0xFFFFFFFF), const Color(0xFF1E1E1E));
  static Color loadingTrack(BuildContext context) =>
      _pick(context, const Color(0xFFE5E7EB), const Color(0xFF374151));

  // Brand style guide set
  static Color primaryTeal(BuildContext context) =>
      _pick(context, const Color(0xFF2A9D8F), const Color(0xFF35B3A3));
  static Color citrusYellow(BuildContext context) =>
      _pick(context, const Color(0xFFE9C46A), const Color(0xFFE9C46A));
  static Color charcoal(BuildContext context) =>
      _pick(context, const Color(0xFF264653), const Color(0xFFECECEC)); // flips: dark text -> light text
  static Color textLight(BuildContext context) =>
      _pick(context, const Color(0xFFFFFFFF), const Color(0xFFFFFFFF));
  static Color textGrey(BuildContext context) =>
      _pick(context, const Color(0xFF6B7280), const Color(0xFF9CA3AF));
  static Color error(BuildContext context) =>
      _pick(context, const Color(0xFFF4A261), const Color(0xFFF4A261));
  static Color success(BuildContext context) =>
      _pick(context, const Color(0xFF69B578), const Color(0xFF69B578));
  static Color surfaceGrey(BuildContext context) =>
      _pick(context, const Color(0xFFF5F5F5), const Color(0xFF1E1E1E));
}