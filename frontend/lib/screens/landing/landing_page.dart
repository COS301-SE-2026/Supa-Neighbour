import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../style_guide/style_guide_page.dart';
import 'package:url_launcher/url_launcher.dart';

class LandingPage extends StatefulWidget {
  const LandingPage({super.key});

  @override
  State<LandingPage> createState() => _LandingPageState();
}

class _LandingPageState extends State<LandingPage> {
  final ScrollController _scrollController = ScrollController();
  final GlobalKey _feature1Key = GlobalKey();
  final GlobalKey _feature2Key = GlobalKey();
  final GlobalKey _feature3Key = GlobalKey();
  final GlobalKey _feature4Key = GlobalKey();
  final GlobalKey _feature5Key = GlobalKey();

  static const String _apkDownloadUrl = 
  'https://github.com/COS301-SE-2026/Supa-Neighbour/releases/download/latest-dev/supa-neighbour.apk';

  Future<void> _downloadApk() async {
    final uri = Uri.parse(_apkDownloadUrl);
    final launched = await launchUrl(uri, mode: LaunchMode.externalApplication);
    if(!launched && mounted){
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not open download link')),
      );
    }
  }
  void _scrollToFeature(String feature) {
    GlobalKey targetKey;
    switch (feature) {
      case 'create-task':
        targetKey = _feature1Key;
        break;
      case 'complete-task':
        targetKey = _feature2Key;
        break;
      case 'review-rate':
        targetKey = _feature3Key;
        break;
      case 'gamification':
        targetKey = _feature4Key;
        break;
      case 'bulletin':
        targetKey = _feature5Key;
        break;
      default:
        return;
    }

    final context = targetKey.currentContext;
    if (context != null) {
      final renderBox = context.findRenderObject() as RenderBox;
      final position = renderBox.localToGlobal(Offset.zero);
      final offset = position.dy - 80;
      _scrollController.animateTo(
        _scrollController.offset + offset,
        duration: const Duration(milliseconds: 500),
        curve: Curves.easeInOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        controller: _scrollController,
        child: Column(
          children: [
            _buildHeader(context),
            _buildHeroSection(context),
            _buildFeatureDetail1(context),
            _buildFeatureDetail2(context),
            _buildFeatureDetail3(context),
            _buildFeatureDetail4(context),
            _buildFeatureDetail5(context),
            _buildHowItWorksSection(context),
            _buildCTASection(context),
            _buildFooter(context),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // HEADER
  // ============================================================
  Widget _buildHeader(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;

    return Container(
      padding: EdgeInsets.symmetric(horizontal: isMobile ? 16 : 24, vertical: isMobile ? 12 : 16),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(
            color: Colors.grey.withValues(alpha: 0.2),
            width: 1,
          ),
        ),
      ),
      child: isMobile
          ? Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Image.asset(
                      'assets/Logo.png',
                      height: 32,
                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          width: 32,
                          height: 32,
                          decoration: BoxDecoration(
                            color: const Color(0xFF2A9D8F),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: const Center(
                            child: Text(
                              'S',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 16,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                    const SizedBox(width: 8),
                    Text(
                      'SupaNeighbour',
                      style: GoogleFonts.poppins(
                        color: const Color(0xFF264653),
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
                ElevatedButton(
                  onPressed: _downloadApk,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF2A9D8F),
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child: Text(
                    'Get Started',
                    style: GoogleFonts.openSans(
                      color: Colors.white,
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            )
          : Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    Image.asset(
                      'assets/Logo.png',
                      height: 40,
                      errorBuilder: (context, error, stackTrace) {
                        return Container(
                          width: 40,
                          height: 40,
                          decoration: BoxDecoration(
                            color: const Color(0xFF2A9D8F),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: const Center(
                            child: Text(
                              'S',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                    const SizedBox(width: 10),
                    Text(
                      'SupaNeighbour',
                      style: GoogleFonts.poppins(
                        color: const Color(0xFF264653),
                        fontSize: 20,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
                Row(
                  children: [
                    PopupMenuButton<String>(
                      child: Text(
                        'Features',
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      onSelected: (value) {
                        _scrollToFeature(value);
                      },
                      itemBuilder: (context) => [
                        const PopupMenuItem(
                          value: 'create-task',
                          child: Text('Create a Task'),
                        ),
                        const PopupMenuItem(
                          value: 'complete-task',
                          child: Text('Complete a Task'),
                        ),
                        const PopupMenuItem(
                          value: 'review-rate',
                          child: Text('Review & Rate'),
                        ),
                        const PopupMenuItem(
                          value: 'gamification',
                          child: Text('Gamification'),
                        ),
                        const PopupMenuItem(
                          value: 'bulletin',
                          child: Text('Community Bulletin'),
                        ),
                      ],
                    ),
                    const SizedBox(width: 8),
                    TextButton(
                      onPressed: () {
                        _scrollController.animateTo(
                          0,
                          duration: const Duration(milliseconds: 500),
                          curve: Curves.easeInOut,
                        );
                      },
                      child: Text(
                        'How It Works',
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton(
                      onPressed: _downloadApk,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF2A9D8F),
                        padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: Text(
                        'Get Started',
                        style: GoogleFonts.openSans(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),
    );
  }

  // ============================================================
  // HERO SECTION
  // ============================================================
  Widget _buildHeroSection(BuildContext context) {
    final screenshots = [
      'assets/screenshots/home-screen.png',
      'assets/screenshots/tasks-available.png',
      'assets/screenshots/task-detail.png',
      'assets/screenshots/leaderboard.png',
      'assets/screenshots/profile.png',
    ];

    final isMobile = MediaQuery.of(context).size.width < 768;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF2A9D8F).withValues(alpha: 0.08),
            Colors.white,
          ],
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
        ),
      ),
      child: isMobile
          ? Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildHeroText(context),
                const SizedBox(height: 32),
                _buildHeroCarousel(screenshots),
              ],
            )
          : Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Expanded(child: _buildHeroText(context)),
                const SizedBox(width: 40),
                Expanded(child: _buildHeroCarousel(screenshots)),
              ],
            ),
    );
  }

  Widget _buildHeroText(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
          decoration: BoxDecoration(
            color: const Color(0xFFE9C46A).withValues(alpha: 0.3),
            borderRadius: BorderRadius.circular(20),
          ),
          child: Text(
            'Community-Driven',
            style: GoogleFonts.openSans(
              color: const Color(0xFF264653),
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        const SizedBox(height: 16),
        Text(
          'Your Neighbourhood,\nConnected, One Task at a Time',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: isMobile ? 36 : 48,
            fontWeight: FontWeight.bold,
            height: 1.1,
          ),
        ),
        const SizedBox(height: 16),
        Text(
          'Need a hand with plants, pets, or packages? SupaNeighbour connects you with trusted neighbours ready to help. Build a stronger community, earn XP, and make every task count.',
          style: GoogleFonts.openSans(
            color: const Color(0xFF6B7280),
            fontSize: isMobile ? 16 : 18,
            height: 1.5,
          ),
        ),
        const SizedBox(height: 24),
        Row(
          children: [
            ElevatedButton(
              onPressed: _downloadApk,
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF2A9D8F),
                padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Get Started',
                style: GoogleFonts.openSans(
                  color: Colors.white,
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(width: 16),
            OutlinedButton(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Learn More coming soon'),
                    duration: Duration(seconds: 1),
                  ),
                );
              },
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: Color(0xFF2A9D8F)),
                padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
              child: Text(
                'Learn More',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF2A9D8F),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildHeroCarousel(List<String> screenshots) {
    return SizedBox(
      height: 450,
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: screenshots.length,
        itemBuilder: (context, index) {
          return Container(
            width: 250,
            margin: const EdgeInsets.only(right: 16),
            decoration: BoxDecoration(
              color: const Color(0xFF2A9D8F).withValues(alpha: 0.05),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(
                color: const Color(0xFF2A9D8F).withValues(alpha: 0.2),
              ),
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(16),
              child: Image.asset(
                screenshots[index],
                fit: BoxFit.contain,
                width: double.infinity,
                height: double.infinity,
                errorBuilder: (context, error, stackTrace) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.image_not_supported,
                          size: 48,
                          color: const Color(0xFF2A9D8F),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'Screenshot ${index + 1}',
                          style: GoogleFonts.openSans(
                            color: const Color(0xFF2A9D8F),
                            fontSize: 14,
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
          );
        },
      ),
    );
  }

  // ============================================================
  // FEATURE DETAIL 1: Create a Task
  // ============================================================
  Widget _buildFeatureDetail1(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;
    final screenshots = [
      'assets/screenshots/create-task.png',
      'assets/screenshots/available-helpers.png',
      'assets/screenshots/tasks-posted.png',
    ];

    return Container(
      key: _feature1Key,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: Colors.white,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Create a Task',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Post a task in seconds and get help from your neighbours.',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 32),
          isMobile
              ? Column(
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // FEATURE DETAIL 2: Complete a Task
  // ============================================================
  Widget _buildFeatureDetail2(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;
    final screenshots = [
      'assets/screenshots/tasks-accepted.png',
      'assets/screenshots/task-complete.png',
    ];

    return Container(
      key: _feature2Key,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: const Color(0xFFF8FAFA),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Complete a Task',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Accept tasks and guide them from start to finish.',
          style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 32),
          isMobile
              ? Column(
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // FEATURE DETAIL 3: Review & Rate
  // ============================================================
  Widget _buildFeatureDetail3(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;
    final screenshots = [
      'assets/screenshots/task-complete.png',
      'assets/screenshots/rating.png',
    ];

    return Container(
      key: _feature3Key,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: Colors.white,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Review & Rate',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Review completed tasks and rate helpers to build trust in the community.',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 32),
          isMobile
              ? Column(
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // FEATURE DETAIL 4: Gamification
  // ============================================================
  Widget _buildFeatureDetail4(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;
    final screenshots = [
      'assets/screenshots/leaderboard.png',
      'assets/screenshots/achievements.png',
    ];

    return Container(
      key: _feature4Key,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: const Color(0xFFF8FAFA),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Gamification',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Earn XP, level up, and unlock achievements as you help your community.',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 32),
          isMobile
              ? Column(
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // FEATURE DETAIL 5: Community Bulletin
  // ============================================================
  Widget _buildFeatureDetail5(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;
    final screenshots = [
      'assets/screenshots/bulletin-feed.png',
      'assets/screenshots/create-bulletin.png',
    ];

    return Container(
      key: _feature5Key,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: Colors.white,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'Community Bulletin',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Stay informed with neighbourhood announcements, lost pet alerts, and local events.',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 32),
          isMobile
              ? Column(
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: screenshots.map((screenshot) {
                    return Padding(
                      padding: const EdgeInsets.only(right: 16),
                      child: _buildFeatureScreenshot(screenshot),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // FEATURE SCREENSHOT HELPER
  // ============================================================
  Widget _buildFeatureScreenshot(String imagePath) {
    return Container(
      width: 220,
      height: 400,
      decoration: BoxDecoration(
        color: const Color(0xFF2A9D8F).withValues(alpha: 0.05),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: const Color(0xFF2A9D8F).withValues(alpha: 0.2),
        ),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(16),
        child: Image.asset(
          imagePath,
          fit: BoxFit.contain,
          width: double.infinity,
          height: double.infinity,
          errorBuilder: (context, error, stackTrace) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.image_not_supported,
                    size: 48,
                    color: const Color(0xFF2A9D8F),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Screenshot',
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF2A9D8F),
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            );
          },
        ),
      ),
    );
  }

  // ============================================================
  // HOW IT WORKS
  // ============================================================
  Widget _buildHowItWorksSection(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;

    final steps = [
      {
        'step': '1',
        'title': 'Post a Task',
        'desc': 'Describe what you need: plant care, pet feeding, package collection, or any quick favour',
      },
      {
        'step': '2',
        'title': 'Find a Helper',
        'desc': 'Nearby neighbours respond with their availability and skills',
      },
      {
        'step': '3',
        'title': 'Chat and Coordinate',
        'desc': 'Secure in-app chat to confirm details without sharing personal info',
      },
      {
        'step': '4',
        'title': 'Complete and Review',
        'desc': 'Task done! Rate your helper and earn XP towards your next badge',
      },
    ];

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: const Color(0xFFF8FAFA),
      child: Column(
        children: [
          Text(
            'How It Works',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Four simple steps to get started',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 40),
          isMobile
              ? Column(
                  children: steps.map((step) {
                    return Container(
                      margin: const EdgeInsets.only(bottom: 16),
                      padding: const EdgeInsets.all(24),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(16),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withValues(alpha: 0.04),
                            blurRadius: 8,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: Row(
                        children: [
                          Container(
                            width: 50,
                            height: 50,
                            decoration: BoxDecoration(
                              color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
                              shape: BoxShape.circle,
                            ),
                            child: Center(
                              child: Text(
                                step['step'] as String,
                                style: GoogleFonts.poppins(
                                  color: const Color(0xFF2A9D8F),
                                  fontSize: 20,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  step['title'] as String,
                                  style: GoogleFonts.poppins(
                                    color: const Color(0xFF264653),
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600,
                                  ),
                                ),
                                Text(
                                  step['desc'] as String,
                                  style: GoogleFonts.openSans(
                                    color: const Color(0xFF6B7280),
                                    fontSize: 14,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    );
                  }).toList(),
                )
              : Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: steps.map((step) {
                    return Expanded(
                      child: Container(
                        margin: const EdgeInsets.symmetric(horizontal: 12),
                        padding: const EdgeInsets.all(24),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(16),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.black.withValues(alpha: 0.04),
                              blurRadius: 8,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: Column(
                          children: [
                            Container(
                              width: 60,
                              height: 60,
                              decoration: BoxDecoration(
                                color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
                                shape: BoxShape.circle,
                              ),
                              child: Center(
                                child: Text(
                                  step['step'] as String,
                                  style: GoogleFonts.poppins(
                                    color: const Color(0xFF2A9D8F),
                                    fontSize: 24,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                            ),
                            const SizedBox(height: 12),
                            Text(
                              step['title'] as String,
                              style: GoogleFonts.poppins(
                                color: const Color(0xFF264653),
                                fontSize: 18,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              step['desc'] as String,
                              style: GoogleFonts.openSans(
                                color: const Color(0xFF6B7280),
                                fontSize: 14,
                              ),
                              textAlign: TextAlign.center,
                            ),
                            const SizedBox(height: 12),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                              decoration: BoxDecoration(
                                color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
                                borderRadius: BorderRadius.circular(12),
                              ),
                              child: Text(
                                'Step ${step['step']}',
                                style: GoogleFonts.openSans(
                                  color: const Color(0xFF2A9D8F),
                                  fontSize: 12,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  }).toList(),
                ),
        ],
      ),
    );
  }

  // ============================================================
  // CTA SECTION
  // ============================================================
  Widget _buildCTASection(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [
            const Color(0xFF2A9D8F),
            const Color(0xFF1C7A6F),
          ],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: Column(
        children: [
          Text(
            'Ready to Connect?',
            style: GoogleFonts.poppins(
              color: Colors.white,
              fontSize: 36,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Join your neighbourhood today',
            style: GoogleFonts.openSans(
              color: Colors.white.withValues(alpha: 0.8),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton(
            onPressed: _downloadApk,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
            ),
            child: Text(
              'Get Started',
              style: GoogleFonts.openSans(
                color: const Color(0xFF2A9D8F),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // FOOTER
  // ============================================================
  Widget _buildFooter(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
      decoration: BoxDecoration(
        color: const Color(0xFF264653),
      ),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              TextButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const StyleGuidePage(),
                    ),
                  );
                },
                style: TextButton.styleFrom(
                  foregroundColor: Colors.white.withValues(alpha: 0.7),
                ),
                child: Text(
                  'Style Guide',
                  style: GoogleFonts.openSans(
                    fontSize: 14,
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Container(
                width: 1,
                height: 16,
                color: Colors.white.withValues(alpha: 0.3),
              ),
              const SizedBox(width: 16),
              TextButton(
                onPressed: () {
                  // TODO: Open Privacy Policy
                },
                style: TextButton.styleFrom(
                  foregroundColor: Colors.white.withValues(alpha: 0.7),
                ),
                child: Text(
                  'Privacy',
                  style: GoogleFonts.openSans(
                    fontSize: 14,
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Container(
                width: 1,
                height: 16,
                color: Colors.white.withValues(alpha: 0.3),
              ),
              const SizedBox(width: 16),
              TextButton(
                onPressed: () {
                  // TODO: Open Terms of Service
                },
                style: TextButton.styleFrom(
                  foregroundColor: Colors.white.withValues(alpha: 0.7),
                ),
                child: Text(
                  'Terms',
                  style: GoogleFonts.openSans(
                    fontSize: 14,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            '© 2026 SupaNeighbour. All rights reserved.',
            style: GoogleFonts.openSans(
              color: Colors.white.withValues(alpha: 0.4),
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }
}