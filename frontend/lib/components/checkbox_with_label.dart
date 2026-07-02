import 'package:flutter/material.dart';
import '../../constants/app_colors.dart';

class CheckboxWithLabel extends StatelessWidget {
  final String label;
  final bool value;
  final ValueChanged<bool?> onChanged;
  final double fontSize;
  final Color activeColor;

  const CheckboxWithLabel({
    super.key,
    required this.label,
    required this.value,
    required this.onChanged,
    this.fontSize = 14,
    this.activeColor = AppColors.primaryTeal,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: 44,
          height: 44,
          child: Checkbox(
            value: value,
            onChanged: onChanged,
            activeColor: activeColor,
            checkColor: Colors.white,
            side: BorderSide(
              color: activeColor,
              width: 2,
            ),
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(4),
            ),
          ),
        ),
        const SizedBox(width: 12),
        Text(
          label,
          style: TextStyle(
            fontSize: fontSize,
            fontWeight: FontWeight.w400,
            fontFamily: 'Open Sans',
            color: AppColors.charcoal,
          ),
        ),
      ],
    );
  }
}