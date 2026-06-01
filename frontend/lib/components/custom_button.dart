import 'package:flutter/material.dart';

class CustomButton extends StatelessWidget {
  final String text;
  final VoidCallback onTap;
  final bool isOutlined;
  final double width;
  final double height;

  const CustomButton({
    super.key,
    required this.text,
    required this.onTap,
    this.isOutlined = false,
    required this.width,
    required this.height,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: width,
        height: height,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(29),
          color: isOutlined ? Colors.transparent : const Color(0xFF1C9A89),
          border: isOutlined
              ? Border.all(color: const Color(0xFF1C9A89), width: 4)
              : null,
        ),
        child: Center(
          child: Text(
            text,
            style: TextStyle(
              fontSize: 40,
              fontWeight: FontWeight.w600,
              color: isOutlined ? const Color(0xFF1C9A89) : Colors.white,
            ),
          ),
        ),
      ),
    );
  }
}