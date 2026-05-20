import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class CreateTaskScreen extends StatelessWidget {
  const CreateTaskScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF264653)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Create Task',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: Center(
        child: Text(
          'Create Task Screen - Coming Soon',
          style: GoogleFonts.openSans(
            color: const Color(0xFF264653),
            fontSize: 16,
          ),
        ),
      ),
    );
  }
}