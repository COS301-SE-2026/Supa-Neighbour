import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import 'dart:io';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_file/open_file.dart';

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
      'bulletin': {
        'title': 'How to Use Bulletin',
        'icon': Icons.announcement,
        'items': [
          'View community announcements from neighbours',
          'Create posts to share news or ask for help',
          'Filter posts by category',
          'Search for specific posts',
          'Tap "Helpful" to show appreciation',
          'Report inappropriate content',
        ],
      },
      'endorsement_graph': {
        'title': 'How to Use Your Trust Network',
        'icon': Icons.hub_outlined,
        'items': [
          'Each circle is a neighbour connected to you by endorsements',
          'The larger centre circle is you',
          'Lines show who has endorsed whom',
          'Tap the skill chips at the top to filter the graph',
          'Tap any neighbour to view their profile',
          'Pinch to zoom in and out, drag to pan around',
        ],
      },
      'admin_application': {
        'title': 'How to Apply to be an Admin',
        'icon': Icons.admin_panel_settings,
        'items': [
          'Admins help moderate the community and review reports',
          'Tap the button to submit an application with your reason for applying',
          'Your application is reviewed by an existing admin',
          'You can see the status of your application on this screen',
          'Pending means it\'s waiting for review',
          'If approved, you\'ll gain access to the admin dashboard',
          'If rejected, you can apply again after making changes',
        ],
      },
      'my_reports': {
        'title': 'How to Use My Reports',
        'icon': Icons.report_outlined,
        'items': [
          'See all the reports you\'ve submitted',
          'Filter by status to find pending, assigned, or resolved reports',
          'Filter by type to narrow down to user, post, comment, or task reports',
          'Each card shows the status, type, and reason at a glance',
          'Resolved reports show the action taken',
          'Pull down to refresh the list',
        ],
      },
      'connect_calendar': {
        'title': 'How to Connect Google Calendar',
        'icon': Icons.calendar_today,
        'items': [
          'Sync your accepted tasks to your Google Calendar automatically',
          'Tap the button to sign in with Google',
          'Grant calendar access when prompted',
          'Once connected, task dates and times appear in your calendar',
          'You can disconnect any time from your Google Account settings',
        ],
      },
      'create_task': {
        'title': 'How to Create a Task',
        'icon': Icons.add_task,
        'items': [
          'Give your task a clear title',
          'Pick a category: Pet Care, Home Repair, and more',
          'Capture your location so helpers can find the task',
          'Choose a date and time',
          'Take at least 2 photos of the task using your camera',
          'Add instructions to help the helper',
          'Tap Post Task when everything is filled in',
        ],
      },
      'task_approval': {
        'title': 'How to Approve a Task',
        'icon': Icons.fact_check,
        'items': [
          'Review the helper\'s completion note and photos',
          'Automatic checks show if the photos were taken at the task location',
          'Report the task if something is wrong',
          'Rate the helper by tapping the stars',
          'Add a written review',
          'Tap Approve and Rate to confirm',
          'You will then be asked to endorse the helper',
        ],
      },
      'task_completion': {
        'title': 'How to Complete a Task',
        'icon': Icons.task_alt,
        'items': [
          'Take at least 1 photo of your completed work using your camera',
          'Capture your location so the app can verify where the photo was taken',
          'The app checks that your photo was taken at the task location',
          'Add a completion note to tell the requester what you did',
          'Tap Mark as Complete when you are done',
          'The requester will review your work and confirm completion',
        ],
      },
      'available_helpers': {
        'title': 'How to Choose a Helper',
        'icon': Icons.people_outline,
        'items': [
          'See all helpers who match your task',
          'Each helper card shows their name and invitation status',
          'Tap a helper to view their full profile and trust score',
          'Use the filter button to narrow the list',
          'Use the sort button to reorder by trust or XP',
        ],
      },
      'notifications': {
        'title': 'How to Use Notifications',
        'icon': Icons.notifications,
        'items': [
          'Unread notifications are marked with a coloured dot',
          'Tap any notification to jump to the related screen',
          'Swipe left on a notification to dismiss it',
          'Tap Mark all read to clear the unread count',
        ],
      },
      'report': {
        'title': 'How to Submit a Report',
        'icon': Icons.flag_outlined,
        'items': [
          'Select a dispute reason for task reports (No Show, Incomplete, Damage)',
          'Write a short reason and add a description',
          'Attach photos to support your report (optional)',
          'Attach up to 5 photos from camera or gallery',
          'Tap Submit Report when you are done',
        ],
      },
      'achievements': {
        'title': 'How to Use Achievements',
        'icon': Icons.emoji_events,
        'items': [
          'Earned achievements are shown in full colour',
          'Unearned achievements are shown in grey',
          'Tap any achievement to see how to earn it',
          'Your progress bar shows how many you have earned',
        ],
      },
    };

    final data = helpData[section] ?? helpData['home']!;

    return ConstrainedBox(
      constraints: BoxConstraints(
        maxHeight: MediaQuery.of(context).size.height * 0.75,
        maxWidth: 420,
      ),
      child: SingleChildScrollView(
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 24),
          decoration: BoxDecoration(
            color: AppColors.background(context),
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
                  Expanded(
                    child: Text(
                      data['title'] as String,
                      style: GoogleFonts.poppins(
                        color: AppColors.charcoal(context),
                        fontSize: 22,
                        fontWeight: FontWeight.w600,
                      ),
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
              }),
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
              const SizedBox(height: 8),
              SizedBox(
                width: double.infinity,
                child: TextButton(
                  onPressed: () {
                    final navigator = Navigator.of(context);
                    navigator.pop();
                    navigator.push(
                      MaterialPageRoute(
                        builder: (_) => const HelpMenuScreen(),
                      ),
                    );
                  },
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        'More help',
                        style: GoogleFonts.openSans(
                          color: AppColors.primaryTeal(context),
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(width: 4),
                      Icon(
                        Icons.arrow_forward,
                        size: 16,
                        color: AppColors.primaryTeal(context),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
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
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Help & Support',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
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
                color: AppColors.charcoal(context),
                fontSize: 20,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            ..._faqs.map((faq) => _buildFaqItem(faq)),
            const SizedBox(height: 24),
            _buildUserManualSection(),
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
        color: AppColors.surfaceGrey(context),
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
            color: AppColors.charcoal(context),
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
        children: [
          Text(
            faq['answer']!,
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
              height: 1.5,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildUserManualSection() {
  return Container(
    margin: const EdgeInsets.only(bottom: 16),
    padding: const EdgeInsets.all(16),
    decoration: BoxDecoration(
      color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
      borderRadius: BorderRadius.circular(12),
      border: Border.all(
        color: AppColors.primaryTeal(context),
        width: 1,
      ),
    ),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Icon(
              Icons.book,
              color: AppColors.primaryTeal(context),
              size: 24,
            ),
            const SizedBox(width: 12),
            Text(
              'User Manual',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Text(
          'Learn how to use all the features of SupaNeighbour with our comprehensive user guide.',
          style: GoogleFonts.openSans(
            color: AppColors.textGrey(context),
            fontSize: 14,
            height: 1.4,
          ),
        ),
        const SizedBox(height: 12),
        SizedBox(
          width: double.infinity,
          child: OutlinedButton(
            onPressed: _downloadUserManual,
            style: OutlinedButton.styleFrom(
              side: BorderSide(color: AppColors.primaryTeal(context)),
              padding: const EdgeInsets.symmetric(vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Read User Manual',
              style: GoogleFonts.openSans(
                color: AppColors.primaryTeal(context),
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
  Future<void> _downloadUserManual() async {
    final documentsDirectory = await getApplicationDocumentsDirectory();
    final filePath = '${documentsDirectory.path}/SupaNeighbour_User_Manual_V3.pdf';
    final file = File(filePath);

    if (await file.exists()) {
      await OpenFile.open(filePath);
      return;
    }

    try {
      final manualData = await rootBundle.load(
        'assets/pdf/SupaNeighbour_User_Manual_V3.pdf',
      );
      await file.writeAsBytes(manualData.buffer.asUint8List());
      await OpenFile.open(filePath);
    } catch (error) {
      if (!mounted) {
        return;
      }
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Error opening user manual: $error'),
          duration: const Duration(seconds: 3),
        ),
      );
    }
  }

  Widget _buildSupportSection() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
      color: AppColors.primaryTeal(context).withValues(alpha: 0.05),
      borderRadius: BorderRadius.circular(12),
      border: Border.all(
        color: AppColors.primaryTeal(context),
        width: 1,
      ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Need more help?',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 16,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'If you\'re still having trouble, our support team is here to help.',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
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
                backgroundColor: AppColors.primaryTeal(context),
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