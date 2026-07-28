import 'package:flutter/material.dart';

class BadgeVisuals{
  static const Map<String, IconData> _icons = {
    'Medical Specialist': Icons.medical_services,
    'Pet Care Helper': Icons.pets,
    'Tech Assistant': Icons.computer,
    'Transport Volunteer': Icons.directions_car,
    'Home Repare Specialist': Icons.home_repair_service,
  };

  static const Map<String, Color> _colors ={
    'Medical Specialist': Color(0xFFE76F51),
    'Pet care Helper': Color(0xFF4CAF50),
    'Tech Assistant': Color(0xFF457B9D),
    'Transport Volunteer': Color(0xFFE9C46A),
    'Home Repair Specialist': Color(0xFF8D6E63),
  };

  static IconData iconFor(String badgeName) => _icons[badgeName] ?? Icons.emoji_events;
  static Color colorFor(String badgeName) => _colors[badgeName] ?? const Color(0xFF2A9D8F);
}