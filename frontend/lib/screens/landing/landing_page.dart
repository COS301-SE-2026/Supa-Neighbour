import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../auth/auth_screen.dart';
import '../style_guide/style_guide_page.dart';

class LandingPage extends StatelessWidget {
  const LandingPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SingleChildScrollView(
        child: Column(
          children: [
            _buildHeader(context),
            _buildHeroSection(context),
            _buildFeatureRows(context),     
            _buildScreenshotCarousel(context),
            _buildHowItWorksSection(context),
            _buildCTASection(context),
            _buildFooter(context),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(
            color: Colors.grey.withValues(alpha: 0.2),
            width: 1,
          ),
        ),
      ),
      child: Row(
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
              TextButton(
                onPressed: () {},
                child: Text(
                  'Features',
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              const SizedBox(width: 8),
              TextButton(
                onPressed: () {},
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
                onPressed: () {
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const AuthScreen(),
                    ),
                  );
                },
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

  Widget _buildHeroSection(BuildContext context) {
    final screenshots = [
      'assets/screenshots/home-screen.png',
      'assets/screenshots/tasks-available.png',
      'assets/screenshots/task-detail.png',
      'assets/screenshots/leaderboard.png',
      'assets/screenshots/profile.png',
    ];

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
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                  decoration: BoxDecoration(
                    color: const Color(0xFFE9C46A).withValues(alpha: 0.3),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    '🏠 Community-Driven',
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF264653),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                Text(
                  'Your Neighbourhood,\nConnected',
                  style: GoogleFonts.poppins(
                    color: const Color(0xFF264653),
                    fontSize: 48,
                    fontWeight: FontWeight.bold,
                    height: 1.1,
                  ),
                ),
                const SizedBox(height: 16),
                Text(
                  'Request and provide help for small household tasks with trusted neighbours. Build your community, one task at a time.',
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF6B7280),
                    fontSize: 18,
                    height: 1.5,
                  ),
                ),
                const SizedBox(height: 24),
                Row(
                  children: [
                    ElevatedButton(
                      onPressed: () {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const AuthScreen(),
                          ),
                        );
                      },
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
            ),
          ),
          const SizedBox(width: 40),
          Expanded(
            child: SizedBox(
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
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFeatureRows(BuildContext context) {
    final features = [
      {
        'title': 'Post and Accept Tasks',
        'desc': 'Create tasks in seconds and find helpers in your neighbourhood. Whether you need plants watered or packages collected, help is just a tap away.',
        'image': 'assets/screenshots/home-screen.png',
        'imageLeft': true,
      },
      {
        'title': 'Chat Securely with Neighbours',
        'desc': 'Communicate directly with neighbours without sharing personal contact details. All messages are private and secure.',
        'image': 'assets/screenshots/chat-inbox.png',
        'imageLeft': false,
      },
      {
        'title': 'Build Trust and Earn XP',
        'desc': 'Complete tasks, receive ratings, and earn XP. Progress from Bronze to Gold as you become a trusted member of your community.',
        'image': 'assets/screenshots/leaderboard.png',
        'imageLeft': true,
      },
      {
        'title': 'Level Up with Gamification',
        'desc': 'Earn achievements and climb the leaderboard. The more you help, the more you earn. Your community recognises your contributions.',
        'image': 'assets/screenshots/profile.png',
        'imageLeft': false,
      },
    ];

    return Container(
      padding: const EdgeInsets.symmetric(vertical: 20),
      child: Column(
        children: features.asMap().entries.map((entry) {
          final index = entry.key;
          final feature = entry.value;
          final isEven = index % 2 == 0;

          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 40),
            decoration: BoxDecoration(
              color: isEven ? Colors.white : const Color(0xFFF8FAFA),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (feature['imageLeft'] as bool)
                  _buildFeatureImage(feature['image'] as String),
                if (feature['imageLeft'] as bool)
                  const SizedBox(width: 40),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        feature['title'] as String,
                        style: GoogleFonts.poppins(
                          color: const Color(0xFF264653),
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        feature['desc'] as String,
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF6B7280),
                          fontSize: 16,
                          height: 1.6,
                        ),
                      ),
                      const SizedBox(height: 16),
                      TextButton(
                        onPressed: () {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(
                              content: Text('Learn more coming soon'),
                              duration: Duration(seconds: 1),
                            ),
                          );
                        },
                        style: TextButton.styleFrom(
                          foregroundColor: const Color(0xFF2A9D8F),
                        ),
                        child: Text(
                          'Learn more →',
                          style: GoogleFonts.openSans(
                            fontSize: 14,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                if (!(feature['imageLeft'] as bool))
                  const SizedBox(width: 40),
                if (!(feature['imageLeft'] as bool))
                  _buildFeatureImage(feature['image'] as String),
              ],
            ),
          );
        }).toList(),
      ),
    );
  }

  Widget _buildFeatureImage(String imagePath) {
    return Container(
      width: 300,
      height: 400,
      decoration: BoxDecoration(
        color: const Color(0xFF2A9D8F).withValues(alpha: 0.05),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: const Color(0xFF2A9D8F).withValues(alpha: 0.2),
        ),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(20),
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

  Widget _buildScreenshotCarousel(BuildContext context) {
    final screenshots = [
      'assets/screenshots/home-screen.png',
      'assets/screenshots/tasks-available.png',
      'assets/screenshots/task-detail.png',
      'assets/screenshots/leaderboard.png',
      'assets/screenshots/profile.png',
    ];

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 40),
      color: const Color(0xFFF8FAFA),
      child: Column(
        children: [
          Text(
            'See SupaNeighbour in Action',
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 32,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Screenshots from the app',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 24),
          SizedBox(
            height: 400,
            child: ListView.builder(
              scrollDirection: Axis.horizontal,
              itemCount: screenshots.length,
              itemBuilder: (context, index) {
                return Container(
                  width: 250,
                  margin: const EdgeInsets.only(right: 16),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(16),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.black.withValues(alpha: 0.06),
                        blurRadius: 12,
                        offset: const Offset(0, 4),
                      ),
                    ],
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
          ),
        ],
      ),
    );
  }

  Widget _buildHowItWorksSection(BuildContext context) {
    final steps = [
      {'step': '1', 'title': 'Post a Task', 'desc': 'Describe what you need help with'},
      {'step': '2', 'title': 'Find Helpers', 'desc': 'Trusted neighbours respond to your request'},
      {'step': '3', 'title': 'Complete & Rate', 'desc': 'Earn XP and build trust'},
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
            'Three simple steps to get started',
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 18,
            ),
          ),
          const SizedBox(height: 40),
          Row(
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
                    ],
                  ),
                ),
              );
            }).toList(),
          ),
          const SizedBox(height: 40),
          TextButton(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(
                  content: Text('Learn more about how it works coming soon'),
                  duration: Duration(seconds: 2),
                ),
              );
            },
            style: TextButton.styleFrom(
              foregroundColor: const Color(0xFF2A9D8F),
            ),
            child: Text(
              'Learn more about how it works →',
              style: GoogleFonts.openSans(
                fontSize: 14,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

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
            onPressed: () {
              Navigator.pushReplacement(
                context,
                MaterialPageRoute(
                  builder: (context) => const AuthScreen(),
                ),
              );
            },
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