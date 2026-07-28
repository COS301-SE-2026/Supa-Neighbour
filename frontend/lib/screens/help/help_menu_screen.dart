import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';

class HelpMenuScreen extends StatefulWidget {
  const HelpMenuScreen({super.key});

  @override
  State<HelpMenuScreen> createState() => _HelpMenuScreenState();

  static void showHelpModal(BuildContext context, String section) {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        elevation: 8,
        child: _HelpModalContent(section: section),
      ),
    );
  }
}

class _HelpModalContent extends StatelessWidget {
  final String section;

  const _HelpModalContent({required this.section});

  @override
  Widget build(BuildContext context) {
    final Map<String, Map<String, dynamic>> helpData = {
      'home': {
        'title': 'How to Use Home',
        'icon': Icons.home,
        'items': [
          'View nearby tasks posted by neighbours',
          'See your stats: Helps Given, Tasks Posted, Active Tasks',
          'Tap the + button to create a new task',
          'Tap "See All" to view all available tasks',
          'Pull down to refresh the task list',
        ],
      },
      'tasks': {
        'title': 'How to Use Tasks',
        'icon': Icons.assignment,
        'items': [
          'Posted tab: Tasks you have created',
          'Accepted tab: Tasks you are helping with',
          'Available tab: Tasks from neighbours you can help with',
          'Swipe right to accept a task',
          'Swipe left to pass on a task',
          'Tap a task to view details',
        ],
      },
      'chat': {
        'title': 'How to Use Chat',
        'icon': Icons.chat,
        'items': [
          'Inbox tab: Messages from neighbours',
          'Community Bulletin tab: Neighbourhood announcements',
          'Tap a chat to open it',
          'Send text messages and images',
          'Get real-time updates when someone replies',
        ],
      },
      'leaderboard': {
        'title': 'How to Use Leaderboard',
        'icon': Icons.leaderboard,
        'items': [
          'Top 3 helpers from last week are shown at the top',
          'Current week\'s rankings are shown in the list',
          'Your rank card shows your position and progress',
          'Tap any helper to view their profile',
          'Build trust score by completing tasks',
        ],
      },
      'profile': {
        'title': 'How to Use Profile',
        'icon': Icons.person,
        'items': [
          'View your trust score and XP',
          'See your level and progress to next level',
          'Edit your skills and services',
          'View your achievements',
          'Access Help & Support',
          'Manage your privacy settings',
        ],
      },
    };

    final data = helpData[section] ?? helpData['home']!;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.95),
        borderRadius: const BorderRadius.vertical(top: Radius.circular(20)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                data['icon'] as IconData,
                color: AppColors.primaryTeal(context),
                size: 28,
              ),
              const SizedBox(width: 12),
              Text(
                data['title'] as String,
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 22,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          ...(data['items'] as List<String>).map((item) {
            return Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(
                    Icons.circle,
                    size: 6,
                    color: AppColors.primaryTeal(context),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      item,
                      style: GoogleFonts.openSans(
                        color: AppColors.charcoal(context),
                        fontSize: 15,
                        height: 1.4,
                      ),
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () => Navigator.pop(context),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primaryTeal(context),
                padding: const EdgeInsets.symmetric(vertical: 14),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Got it',
                style: GoogleFonts.openSans(
                  color: Colors.white,
                  fontSize: 16,
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