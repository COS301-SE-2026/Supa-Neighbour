import 'package:flutter/material.dart';
import '../../constants/app_colors.dart';

class CustomButton extends StatelessWidget {
  final String text;
  final VoidCallback? onTap;
  final bool isOutlined;
  final bool isDisabled;
  final bool isLoading;
  final double width;
  final double height;
  final double fontSize;
  final EdgeInsets padding;

  const CustomButton({
    super.key,
    required this.text,
    this.onTap,
    this.isOutlined = false,
    this.isDisabled = false,
    this.isLoading = false,
    this.width = double.infinity,
    this.height = 48,
    this.fontSize = 14,
    this.padding = const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
  });

  @override
  Widget build(BuildContext context) {
    final bool isInteractive = !isDisabled && !isLoading && onTap != null;

    return SizedBox(
      width: width,
      height: height,
      child: ElevatedButton(
        onPressed: isInteractive ? onTap : null,
        style: ElevatedButton.styleFrom(
          backgroundColor: isOutlined
              ? Colors.transparent
              : AppColors.primaryTeal,
          foregroundColor: isOutlined
              ? AppColors.primaryTeal
              : AppColors.textLight,
          elevation: 0,
          padding: padding,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(28),
            side: isOutlined
                ? BorderSide(
                    color: AppColors.primaryTeal,
                    width: 2,
                  )
                : BorderSide.none,
          ),
          disabledBackgroundColor: isOutlined
              ? Colors.transparent
              : AppColors.primaryTeal.withValues(alpha: 0.4),
          disabledForegroundColor: isOutlined
              ? AppColors.primaryTeal.withValues(alpha: 0.4)
              : AppColors.textLight.withValues(alpha: 0.6),
        ),
        child: isLoading
            ? const SizedBox(
                width: 20,
                height: 20,
                child: CircularProgressIndicator(
                  strokeWidth: 2,
                  color: Colors.white,
                ),
              )
            : Text(
                text,
                style: TextStyle(
                  fontSize: fontSize,
                  fontWeight: FontWeight.w600,
                  fontFamily: 'Open Sans',
                  letterSpacing: 0.5,
                ),
              ),
      ),
    );
  }
}