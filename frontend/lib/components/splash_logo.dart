import 'package:flutter/material.dart';

class SplashLogo extends StatelessWidget {
  final double width;
  final double height;

  const SplashLogo({
    super.key,
    required this.width,
    required this.height,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: width,
      height: height,
      decoration: BoxDecoration(
        color: Colors.grey[200],
        shape: BoxShape.circle,
      ),
      child: const Center(
        child: Icon(
          Icons.image,
          size: 80,
          color: Colors.grey,
        ),
      ),
    );
  }
}