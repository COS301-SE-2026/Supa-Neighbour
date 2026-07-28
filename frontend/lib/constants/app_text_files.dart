import 'package:flutter/material.dart';
import 'app_colors.dart';

class AppTextStyles {
  static  TextStyle primaryHeader(BuildContext context) => TextStyle(
    fontFamily: 'GoogleSansFlex',
    fontSize: 80,
    fontWeight: FontWeight.w600,
    color: AppColors.primary(context),
  );

  static TextStyle secondaryHeader(BuildContext context) => TextStyle(
    fontFamily: 'GoogleSansFlex',
    fontSize: 40,
    fontWeight: FontWeight.w600,
    color: AppColors.secondary(context),
  );

  static TextStyle bodyText(BuildContext context) => TextStyle(
    fontFamily: 'OpenSans',
    fontSize: 14,
    fontWeight: FontWeight.normal,
    color: AppColors.white(context),
  );
}