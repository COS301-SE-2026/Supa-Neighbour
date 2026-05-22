import 'package:flutter/material.dart';

class SplashTitle extends StatelessWidget {
  final String text;
  final double fontSize;
  final Color color;

  const SplashTitle({
    super.key,
    required this.text,
    required this.fontSize,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      textAlign: TextAlign.center,
      style: TextStyle(
        fontSize: fontSize,
        fontWeight: FontWeight.w600,
        color: color,
      ),
    );
  }
}