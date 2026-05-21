import 'package:flutter/material.dart';

class LogoPlaceholder extends StatelessWidget {
  final double size;

  const LogoPlaceholder({
    super.key,
    required this.size,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: Colors.grey[200],
        shape: BoxShape.circle,
      ),
      child: const Center(
        child: Icon(
          Icons.person,
          size: 120,
          color: Colors.grey,
        ),
      ),
    );
  }
}