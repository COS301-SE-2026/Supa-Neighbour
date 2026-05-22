import 'package:flutter/material.dart';
import 'app_colors.dart';

class AppTextStyles {
  static const TextStyle primaryHeader = TextStyle(
    fontFamily: 'GoogleSansFlex',
    fontSize: 80,
    fontWeight: FontWeight.w600,
    color: AppColors.primary,
  );

  static const TextStyle secondaryHeader = TextStyle(
    fontFamily: 'GoogleSansFlex',
    fontSize: 40,
    fontWeight: FontWeight.w600,
    color: AppColors.secondary,
  );

  static const TextStyle bodyText = TextStyle(
    fontFamily: 'OpenSans',
    fontSize: 14,
    fontWeight: FontWeight.normal,
    color: AppColors.white,
  );
}