import 'package:flutter/material.dart';

class CustomInputField extends StatelessWidget {
  final String label;
  final bool obscureText;
  final TextEditingController? controller;
  final Widget? suffixIcon;

  const CustomInputField({
    super.key,
    required this.label,
    this.obscureText = false,
    this.controller,
    this.suffixIcon,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          label,
          style: const TextStyle(
            fontSize: 40,
            fontWeight: FontWeight.w400,
            color: Color(0xFF1C9A89),
          ),
        ),
        const SizedBox(height: 16),
        Container(
          width: double.infinity,
          height: 120,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(29),
            color: Colors.white,
            border: Border.all(
              color: const Color(0xFF1C9A89),
              width: 4,
            ),
          ),
          child: TextField(
            controller: controller,
            obscureText: obscureText,
            style: const TextStyle(fontSize: 24),
            decoration: InputDecoration(
              hintText: 'Enter your $label',
              hintStyle: const TextStyle(fontSize: 20, color: Colors.grey),
              border: InputBorder.none,
              contentPadding: const EdgeInsets.symmetric(horizontal: 24, vertical: 40),
              suffixIcon: suffixIcon,
            ),
          ),
        ),
      ],
    );
  }
}