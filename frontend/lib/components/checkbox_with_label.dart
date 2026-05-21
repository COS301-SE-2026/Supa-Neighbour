import 'package:flutter/material.dart';

class CheckboxWithLabel extends StatefulWidget {
  final String label;
  final double fontSize;

  const CheckboxWithLabel({
    super.key,
    required this.label,
    this.fontSize = 40,
  });

  @override
  State<CheckboxWithLabel> createState() => _CheckboxWithLabelState();
}

class _CheckboxWithLabelState extends State<CheckboxWithLabel> {
  bool _isChecked = false;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: 52,
          height: 52,
          child: Checkbox(
            value: _isChecked,
            onChanged: (value) {
              setState(() {
                _isChecked = value ?? false;
              });
            },
            activeColor: const Color(0xFF1C9A89),
            checkColor: Colors.white,
            side: const BorderSide(color: Color(0xFF1C9A89), width: 3),
          ),
        ),
        const SizedBox(width: 16),
        Text(
          widget.label,
          style: TextStyle(
            fontSize: widget.fontSize,
            fontWeight: FontWeight.w400,
            color: const Color(0xFF1C9A89),
          ),
        ),
      ],
    );
  }
}