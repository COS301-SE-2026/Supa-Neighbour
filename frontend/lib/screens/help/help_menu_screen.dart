import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class HelpMenuScreen extends StatefulWidget {
  const HelpMenuScreen({super.key});

  @override
  State<HelpMenuScreen> createState() => _HelpMenuScreenState();
}

class _HelpMenuScreenState extends State<HelpMenuScreen> {
  final List<Map<String, String>> _faqs = [
    {
      'question': 'How do I post a task?',
      'answer': 'Go to the Tasks tab, tap the + button, fill in the task details, and submit.',
    },
    {
      'question': 'How do I accept a task?',
      'answer': 'Go to the Available tab, tap on a task, and tap "Accept" or swipe right on the task card.',
    },
    {
      'question': 'How is my trust score calculated?',
      'answer': 'Your trust score is calculated based on completed tasks and ratings from other users.',
    },
    {
      'question': 'What are XP points?',
      'answer': 'XP points are earned by completing tasks. They help you level up and unlock achievements.',
    },
    {
      'question': 'How do I contact a helper?',
      'answer': 'Once a helper accepts your task, you can chat with them through the Chat tab.',
    },
    {
      'question': 'What happens if a task is cancelled?',
      'answer': 'If a task is cancelled, no XP is awarded. You can repost the task if needed.',
    },
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF264653)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Help & Support',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Frequently Asked Questions',
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 20,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            ..._faqs.map((faq) => _buildFaqItem(faq)),
            const SizedBox(height: 24),
            _buildSupportSection(),
          ],
        ),
      ),
    );
  }

  Widget _buildFaqItem(Map<String, String> faq) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.04),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ExpansionTile(
        tilePadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
        childrenPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        title: Text(
          faq['question']!,
          style: GoogleFonts.openSans(
            color: const Color(0xFF264653),
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
        children: [
          Text(
            faq['answer']!,
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 14,
              height: 1.5,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSupportSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF2A9D8F).withValues(alpha: 0.05),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(
          color: const Color(0xFF2A9D8F),
          width: 1,
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Need more help?',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'If you\'re still having trouble, our support team is here to help.',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 12),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                // TODO for later: Open email or contact form
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2A9D8F),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Contact Support',
                style: GoogleFonts.openSans(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}