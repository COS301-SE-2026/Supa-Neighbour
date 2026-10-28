import 'dart:async';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:url_launcher/url_launcher.dart';
import '../style_guide/style_guide_page.dart';

class _TeamMember {
  final String name;
  final String role;
  final String linkedInUrl;
  final String cvPath;

  const _TeamMember({
    required this.name,
    required this.role,
    required this.linkedInUrl,
    required this.cvPath,
  });
}

class LandingPage extends StatefulWidget {
  const LandingPage({super.key});

  @override
  State<LandingPage> createState() => _LandingPageState();
}

class _LandingPageState extends State<LandingPage> {
  static const double _contentMaxWidth = 1140;

  // Supa-Neighbour Palette
  static const Color _tealDark = Color(0xFF1C7A6F);
  static const Color _tealPrimary = Color(0xFF2A9D8F);
  static const Color _citrusAccent = Color(0xFFE9C46A);
  static const Color _charcoal = Color(0xFF264653);
  static const Color _mutedGrey = Color(0xFF6B7280);
  static const Color _offWhite = Color(0xFFF8FAFA);
  static const Color _cardTint = Color(0xFFE7F5F3);

  final ScrollController _scrollController = ScrollController();
  double _scrollOffset = 0;

  // Keys for section jumping
  final GlobalKey _feature1Key = GlobalKey();
  final GlobalKey _feature2Key = GlobalKey();
  final GlobalKey _feature3Key = GlobalKey();
  final GlobalKey _feature4Key = GlobalKey();
  final GlobalKey _feature5Key = GlobalKey();
  final GlobalKey _wowKey = GlobalKey();
  final GlobalKey _teamKey = GlobalKey();

  // Hero Image Carousel State
  final PageController _heroPageController = PageController();
  int _activeHeroIndex = 0;
  Timer? _carouselTimer;

  static const List<Map<String, String>> _heroScreenshots = [
    {
      'title': 'SupaNeighbour',
      'path': 'assets/screenshots/splash-screen.jpeg',
      'caption': 'Your neighbourly helper'
    },
    {
      'title': 'Sign In',
      'path': 'assets/screenshots/login-screen.png',
      'caption': 'Log in and join your community'
    },
    {
      'title': 'Home Feed',
      'path': 'assets/screenshots/home-screen.png',
      'caption': 'Connect with neighbours nearby'
    },
    {
      'title': 'Tasks Available',
      'path': 'assets/screenshots/tasks-available.png',
      'caption': 'Browse local requests in real time'
    },
    {
      'title': 'Gamification',
      'path': 'assets/screenshots/leaderboard.png',
      'caption': 'Earn XP and level up in your area'
    },
    {
      'title': 'Bulletin',
      'path': 'assets/screenshots/bulletin-feed.png',
      'caption': 'Stay tuned with community updates'
    },
  ];

  static const List<_TeamMember> _team = [
    _TeamMember(
      name: 'Blessing K Gibendi',
      role: 'Team Leader',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-1',
      cvPath: 'cvs/member-1.pdf',
    ),
    _TeamMember(
      name: 'Michelle W Njoroge',
      role: 'Frontend Lead & Design',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-2',
      cvPath: 'cvs/member-2.pdf',
    ),
    _TeamMember(
      name: 'David Ekele Kalu',
      role: 'Frontend Engineer',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-3',
      cvPath: 'cvs/member-3.pdf',
    ),
    _TeamMember(
      name: 'Amantle Keamogetse Temo',
      role: 'Integration Engineer',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-4',
      cvPath: 'cvs/member-4.pdf',
    ),
    _TeamMember(
      name: 'Divo Kohler',
      role: 'Backend Engineer',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-5',
      cvPath: 'cvs/member-5.pdf',
    ),
  ];

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(() {
      setState(() {
        _scrollOffset = _scrollController.offset;
      });
    });

    _carouselTimer = Timer.periodic(const Duration(seconds: 4), (timer) {
      if (_heroPageController.hasClients) {
        final nextIndex = (_activeHeroIndex + 1) % _heroScreenshots.length;
        _heroPageController.animateToPage(
          nextIndex,
          duration: const Duration(milliseconds: 600),
          curve: Curves.easeInOutCubic,
        );
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _heroPageController.dispose();
    _carouselTimer?.cancel();
    super.dispose();
  }

  Future<void> _openUrl(Uri uri) async {
    final launched = await launchUrl(uri, mode: LaunchMode.externalApplication);
    if (!launched && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not open link')),
      );
    }
  }

  Future<void> _openCv(String cvPath) => _openUrl(Uri.base.resolve(cvPath));

  static const String _apkDownloadUrl =
      'https://github.com/COS301-SE-2026/Supa-Neighbour/releases/download/latest-dev/supa-neighbour.apk';

  Future<void> _downloadApk() async {
    final uri = Uri.parse(_apkDownloadUrl);
    final launched = await launchUrl(uri, mode: LaunchMode.externalApplication);
    if (!launched && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Could not open download link')),
      );
    }
  }

  void _scrollToKey(GlobalKey key) {
    final context = key.currentContext;
    if (context != null) {
      final renderBox = context.findRenderObject() as RenderBox;
      final position = renderBox.localToGlobal(Offset.zero);
      final offset = position.dy - 80;
      _scrollController.animateTo(
        (_scrollController.offset + offset).clamp(0, _scrollController.position.maxScrollExtent),
        duration: const Duration(milliseconds: 600),
        curve: Curves.easeInOutCubic,
      );
    }
  }

  Widget _constrained(Widget child) {
    return Center(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: _contentMaxWidth),
        child: child,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Stack(
        children: [
          SingleChildScrollView(
            controller: _scrollController,
            child: Column(
              children: [
                const SizedBox(height: 72),
                _buildHeroSection(context),
                _buildFeatureBlocks(context),
                _buildWowFactorsSection(context),
                _buildHowItWorksSection(context),
                _buildTeamSection(context),
                _buildCTASection(context),
                _buildFooter(context),
              ],
            ),
          ),
          Positioned(
            top: 0,
            left: 0,
            right: 0,
            child: _buildHeader(context),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // HEADER
  // ============================================================
  Widget _buildHeader(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 1000;
    final isScrolled = _scrollOffset > 20;

    return AnimatedContainer(
      duration: const Duration(milliseconds: 200),
      width: double.infinity,
      decoration: BoxDecoration(
        color: Colors.white.withValues(alpha: 0.95),
        boxShadow: isScrolled
            ? [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.06),
                  blurRadius: 16,
                  offset: const Offset(0, 4),
                )
              ]
            : [],
      ),
      padding: EdgeInsets.symmetric(horizontal: isMobile ? 20 : 32, vertical: 12),
      child: _constrained(
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Flexible(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Image.asset(
                    'assets/Logo.png',
                    height: 38,
                    errorBuilder: (context, error, stackTrace) => Container(
                      width: 38,
                      height: 38,
                      decoration: BoxDecoration(
                        color: _tealPrimary,
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: const Center(
                        child: Text(
                          'S',
                          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 20),
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Flexible(
                    child: Text(
                      'SupaNeighbour',
                      overflow: TextOverflow.ellipsis,
                      style: GoogleFonts.poppins(
                        color: _charcoal,
                        fontSize: 22,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
            ),
            if (!isMobile)
              Row(
                children: [
                  _buildRichFeaturesDropdown(),
                  const SizedBox(width: 8),
                  TextButton(
                    onPressed: () => _scrollToKey(_wowKey),
                    style: TextButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    ),
                    child: Text(
                      'WOW Factors',
                      style: GoogleFonts.openSans(
                        color: _charcoal,
                        fontWeight: FontWeight.w600,
                        fontSize: 15,
                      ),
                    ),
                  ),
                  TextButton(
                    onPressed: () => _scrollToKey(_teamKey),
                    style: TextButton.styleFrom(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    ),
                    child: Text(
                      'Team',
                      style: GoogleFonts.openSans(
                        color: _charcoal,
                        fontWeight: FontWeight.w600,
                        fontSize: 15,
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  _HoverButton(
                    onPressed: _downloadApk,
                    child: ElevatedButton.icon(
                      onPressed: _downloadApk,
                      icon: const Icon(Icons.download, size: 18, color: Colors.white),
                      label: Text(
                        'Get App',
                        style: GoogleFonts.openSans(fontWeight: FontWeight.w600, fontSize: 14),
                      ),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _tealPrimary,
                        foregroundColor: Colors.white,
                        elevation: 0,
                        padding: const EdgeInsets.symmetric(horizontal: 22, vertical: 14),
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
                      ),
                    ),
                  ),
                ],
              )
            else
              ElevatedButton(
                onPressed: _downloadApk,
                style: ElevatedButton.styleFrom(
                  backgroundColor: _tealPrimary,
                  foregroundColor: Colors.white,
                  elevation: 0,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                ),
                child: const Text('Download'),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildRichFeaturesDropdown() {
    return PopupMenuButton<GlobalKey>(
      tooltip: 'Show features',
      offset: const Offset(0, 48),
      elevation: 8,
      shadowColor: Colors.black.withValues(alpha: 0.15),
      color: Colors.white,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
        side: BorderSide(color: Colors.black.withValues(alpha: 0.05)),
      ),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Features',
              style: GoogleFonts.openSans(
                color: _charcoal,
                fontWeight: FontWeight.w600,
                fontSize: 15,
              ),
            ),
            const SizedBox(width: 4),
            const Icon(Icons.keyboard_arrow_down_rounded, size: 20, color: _charcoal),
          ],
        ),
      ),
      onSelected: _scrollToKey,
      itemBuilder: (context) => [
        _buildDropdownMenuItem(
          key: _feature1Key,
          icon: Icons.add_task_rounded,
          title: 'Create a Task',
          subtitle: 'Post requests for plant care, pets & packages',
          iconColor: _tealPrimary,
        ),
        _buildDropdownMenuItem(
          key: _feature2Key,
          icon: Icons.handshake_outlined,
          title: 'Complete a Task',
          subtitle: 'Help out neighbours nearby & earn rewards',
          iconColor: _tealDark,
        ),
        _buildDropdownMenuItem(
          key: _feature3Key,
          icon: Icons.star_outline_rounded,
          title: 'Review & Rate',
          subtitle: 'Build trusted ratings in your community',
          iconColor: _citrusAccent,
        ),
        _buildDropdownMenuItem(
          key: _feature4Key,
          icon: Icons.emoji_events_outlined,
          title: 'Gamification',
          subtitle: 'Earn XP, unlock badges & top leaderboards',
          iconColor: _tealPrimary,
        ),
        _buildDropdownMenuItem(
          key: _feature5Key,
          icon: Icons.campaign_outlined,
          title: 'Community Bulletin',
          subtitle: 'Stay updated with local news & pet alerts',
          iconColor: _charcoal,
        ),
      ],
    );
  }

  PopupMenuItem<GlobalKey> _buildDropdownMenuItem({
    required GlobalKey key,
    required IconData icon,
    required String title,
    required String subtitle,
    required Color iconColor,
  }) {
    return PopupMenuItem<GlobalKey>(
      value: key,
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
      child: Container(
        width: 320,
        padding: const EdgeInsets.all(8),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 38,
              height: 38,
              decoration: BoxDecoration(
                color: iconColor.withValues(alpha: 0.12),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Icon(icon, size: 20, color: iconColor),
            ),
            const SizedBox(width: 14),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.poppins(
                      color: _charcoal,
                      fontSize: 14,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 2),
                  Text(
                    subtitle,
                    style: GoogleFonts.openSans(
                      color: _mutedGrey,
                      fontSize: 12,
                      height: 1.3,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // HERO SECTION
  // ============================================================
  Widget _buildHeroSection(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 1000;

    return Container(
      width: double.infinity,
      color: Colors.white,
      padding: EdgeInsets.symmetric(horizontal: 24, vertical: isMobile ? 40 : 60),
      child: _constrained(
        isMobile
            ? Column(
                children: [
                  _buildHeroContent(context, true),
                  const SizedBox(height: 40),
                  _buildInteractivePhoneMockup(),
                ],
              )
            : Row(
                children: [
                  Expanded(child: _buildHeroContent(context, false)),
                  const SizedBox(width: 40),
                  Expanded(child: _buildInteractivePhoneMockup()),
                ],
              ),
      ),
    );
  }

  Widget _buildHeroContent(BuildContext context, bool isCentered) {
    final align = isCentered ? CrossAxisAlignment.center : CrossAxisAlignment.start;
    final textAlign = isCentered ? TextAlign.center : TextAlign.left;

    return Column(
      crossAxisAlignment: align,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
          decoration: BoxDecoration(
            color: _cardTint,
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: _tealPrimary.withValues(alpha: 0.2)),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: 8,
                height: 8,
                decoration: const BoxDecoration(color: _tealPrimary, shape: BoxShape.circle),
              ),
              const SizedBox(width: 8),
              Text(
                'Simple. Safe. Neighbourhood Help.',
                style: GoogleFonts.openSans(
                  color: _tealDark,
                  fontWeight: FontWeight.bold,
                  fontSize: 13,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 20),
        Text(
          'Connecting neighbours,\none task at a time.',
          textAlign: textAlign,
          style: GoogleFonts.poppins(
            color: _charcoal,
            fontSize: isCentered ? 36 : 52,
            fontWeight: FontWeight.bold,
            height: 1.15,
          ),
        ),
        const SizedBox(height: 20),
        Text(
          'Need help with plant care, pet sitting, or heavy lifting? SupaNeighbour connects you with trusted neighbours through task sharing and gamified badges.',
          textAlign: textAlign,
          style: GoogleFonts.openSans(
            color: _mutedGrey,
            fontSize: 18,
            height: 1.5,
          ),
        ),
        const SizedBox(height: 32),
        Wrap(
          alignment: isCentered ? WrapAlignment.center : WrapAlignment.start,
          spacing: 16,
          runSpacing: 12,
          children: [
            _HoverButton(
              onPressed: _downloadApk,
              child: ElevatedButton.icon(
                onPressed: _downloadApk,
                icon: const Icon(Icons.android, size: 20, color: _charcoal),
                label: Text('Download Android APK', style: GoogleFonts.openSans(fontWeight: FontWeight.w600, fontSize: 16, color: _charcoal)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: _citrusAccent,
                  foregroundColor: _charcoal,
                  elevation: 0,
                  padding: const EdgeInsets.symmetric(horizontal: 28, vertical: 18),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildInteractivePhoneMockup() {
    return Column(
      children: [
        Center(
          child: Container(
            width: 270,
            height: 550,
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(32),
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.08),
                  blurRadius: 30,
                  spreadRadius: 2,
                  offset: const Offset(0, 16),
                ),
                BoxShadow(
                  color: _tealPrimary.withValues(alpha: 0.12),
                  blurRadius: 45,
                  spreadRadius: -8,
                  offset: const Offset(0, 8),
                ),
              ],
            ),
            child: Stack(
              children: [
                ClipRRect(
                  borderRadius: BorderRadius.circular(32),
                  child: PageView.builder(
                    controller: _heroPageController,
                    onPageChanged: (idx) {
                      setState(() {
                        _activeHeroIndex = idx;
                      });
                    },
                    itemCount: _heroScreenshots.length,
                    itemBuilder: (context, index) {
                      final item = _heroScreenshots[index];
                      return Image.asset(
                        item['path']!,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) => Container(
                          color: _cardTint,
                          child: Center(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                const Icon(Icons.phone_android, size: 56, color: _tealPrimary),
                                const SizedBox(height: 12),
                                Text(
                                  item['title']!,
                                  style: GoogleFonts.poppins(
                                    color: _charcoal,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
                Align(
                  alignment: Alignment.bottomCenter,
                  child: Container(
                    margin: const EdgeInsets.only(bottom: 8),
                    width: 110,
                    height: 4,
                    decoration: BoxDecoration(
                      color: Colors.black.withValues(alpha: 0.25),
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 20),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(_heroScreenshots.length, (idx) {
            final isSelected = idx == _activeHeroIndex;
            return GestureDetector(
              onTap: () {
                _heroPageController.animateToPage(
                  idx,
                  duration: const Duration(milliseconds: 400),
                  curve: Curves.easeInOut,
                );
              },
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 300),
                margin: const EdgeInsets.symmetric(horizontal: 4),
                width: isSelected ? 24 : 8,
                height: 8,
                decoration: BoxDecoration(
                  color: isSelected ? _tealPrimary : _mutedGrey.withValues(alpha: 0.3),
                  borderRadius: BorderRadius.circular(4),
                ),
              ),
            );
          }),
        ),
      ],
    );
  }

  // ============================================================
  // FEATURE BLOCKS
  // ============================================================
  Widget _buildFeatureBlocks(BuildContext context) {
    final features = [
      {
        'key': _feature1Key,
        'title': 'Post a task in seconds',
        'desc': 'Need help with gardening, pet feeding, or errand running? Describe what you need, set your requirements, and broadcast it to nearby neighbours instantly.',
        'images': [
          'assets/screenshots/create-task.png',
          'assets/screenshots/available-helpers.png',
        ],
        'isReversed': false,
        'bg': _cardTint,
      },
      {
        'key': _feature2Key,
        'title': 'Earn rewards & help out',
        'desc': 'Browse local requests nearby. Accept tasks that match your skills, chat securely, and guide them smoothly from start to finish.',
        'images': [
          'assets/screenshots/tasks-accepted.png',
          'assets/screenshots/task-complete.png',
        ],
        'isReversed': true,
        'bg': _offWhite,
      },
      {
        'key': _feature3Key,
        'title': 'Build neighbourhood trust',
        'desc': 'Leave reviews and ratings after completed tasks. Every helper earns a community trust score that ensures safety and reliability.',
        'images': [
          'assets/screenshots/task-complete.png',
          'assets/screenshots/rating.png',
        ],
        'isReversed': false,
        'bg': Colors.white,
      },
      {
        'key': _feature4Key,
        'title': 'Level up with Gamification',
        'desc': 'Gain XP, level up, unlock achievement badges, and climb the local community leaderboard as a top neighbourhood contributor.',
        'images': [
          'assets/screenshots/leaderboard.png',
          'assets/screenshots/achievements.png',
        ],
        'isReversed': true,
        'bg': _cardTint,
      },
      {
        'key': _feature5Key,
        'title': 'Community Bulletin Board',
        'desc': 'Stay up to date with community announcements, lost & found pet alerts, local news, and upcoming neighbourhood events.',
        'images': [
          'assets/screenshots/bulletin-feed.png',
          'assets/screenshots/create-bulletin.png',
        ],
        'isReversed': false,
        'bg': _offWhite,
      },
    ];

    return Column(
      children: features.map((f) {
        return Container(
          key: f['key'] as GlobalKey,
          width: double.infinity,
          color: f['bg'] as Color,
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 80),
          child: _constrained(
            _buildSingleFeatureRow(
              context: context,
              title: f['title'] as String,
              desc: f['desc'] as String,
              imagePaths: f['images'] as List<String>,
              isReversed: f['isReversed'] as bool,
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildSingleFeatureRow({
    required BuildContext context,
    required String title,
    required String desc,
    required List<String> imagePaths,
    required bool isReversed,
  }) {
    final isMobile = MediaQuery.of(context).size.width < 850;

    final textContent = Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(
          title,
          style: GoogleFonts.poppins(
            color: _charcoal,
            fontSize: isMobile ? 28 : 38,
            fontWeight: FontWeight.bold,
            height: 1.2,
          ),
        ),
        const SizedBox(height: 16),
        Text(
          desc,
          style: GoogleFonts.openSans(
            color: _mutedGrey,
            fontSize: 18,
            height: 1.5,
          ),
        ),
      ],
    );

    final showBoth = MediaQuery.of(context).size.width >= 900;

    final imagesContent = SizedBox(
      height: 420,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildMiniPhoneMockup(imagePaths[0], verticalOffset: 0),
          if (showBoth && imagePaths.length > 1) ...[
            const SizedBox(width: 20),
            _buildMiniPhoneMockup(imagePaths[1], verticalOffset: 30),
          ],
        ],
      ),
    );

    if (isMobile) {
      return Column(
        children: [
          textContent,
          const SizedBox(height: 40),
          imagesContent,
        ],
      );
    }

    return Row(
      children: isReversed
          ? [
              Expanded(flex: 5, child: imagesContent),
              const SizedBox(width: 60),
              Expanded(flex: 5, child: textContent),
            ]
          : [
              Expanded(flex: 5, child: textContent),
              const SizedBox(width: 60),
              Expanded(flex: 5, child: imagesContent),
            ],
    );
  }

  Widget _buildMiniPhoneMockup(String imagePath, {required double verticalOffset}) {
    return Transform.translate(
      offset: Offset(0, verticalOffset),
      child: Container(
        width: 190,
        height: 410,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(28),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: 0.1),
              blurRadius: 25,
              offset: const Offset(0, 12),
            ),
          ],
        ),
        child: Stack(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(28),
              child: Image.asset(
                imagePath,
                fit: BoxFit.cover,
                width: double.infinity,
                height: double.infinity,
                errorBuilder: (context, error, stackTrace) => Container(
                  color: _cardTint,
                  child: const Center(
                    child: Icon(Icons.phone_android, size: 40, color: _tealPrimary),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // WOW FACTORS SECTION
  // ============================================================
  Widget _buildWowFactorsSection(BuildContext context) {
    final wows = [
      {
        'number': '01',
        'title': 'The Endorsement Network',
        'tagline': 'Graph-based trust that cannot be gamed',
        'desc':
            'Every successful task becomes a directed edge in a trust graph. '
                'Users endorse each other for specific skills, and the resulting '
                'network is queryable, traversable, and abuse-resistant. '
                'Background jobs flag mutual rings, sudden spikes, and island '
                'clusters before they inflate a helper\'s reputation.',
        'images': [
          'assets/screenshots/my-trust-network.jpg',
          'assets/screenshots/helper-profile.png',
        ],
        'isReversed': false,
        'bg': _cardTint,
      },
      {
        'number': '02',
        'title': 'The Adaptive Trust Score',
        'tagline': 'A trained model, not a star rating',
        'desc':
            'Static averages can be gamed. Our trust score is a trained model '
                'that weighs six behavioural features - completion rate, rating '
                'reliability, community standing, recency, zone activity, and '
                'longevity - with every contribution visible on the profile.',
        'images': [
          'assets/screenshots/adaptive-trust-score.jpeg',
          'assets/screenshots/leaderboard.png',
        ],
        'isReversed': true,
        'bg': _offWhite,
      },
      {
        'number': '03',
        'title': 'Vision AI Task Verification',
        'tagline': 'Computer vision confirms work was actually done',
        'desc':
            'Helpers submit photo evidence when completing a task. Azure OpenAI '
                'compares the photo against reference images and geofencing '
                'verifies the photo was taken at the task location. Fraud is '
                'flagged before the requester ever sees a false completion.',
        'images': [
          'assets/screenshots/vision-ai.jpg',
          'assets/screenshots/task-complete.png',
        ],
        'isReversed': false,
        'bg': _cardTint,
      },
    ];

    return Container(
      key: _wowKey,
      width: double.infinity,
      color: Colors.white,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      child: _constrained(
        Column(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: _citrusAccent.withValues(alpha: 0.25),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                'WOW FACTORS',
                style: GoogleFonts.openSans(
                  color: _charcoal,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.2,
                ),
              ),
            ),
            const SizedBox(height: 16),
            Text(
              'What makes SupaNeighbour different',
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: _charcoal,
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 640),
              child: Text(
                'Three features that go beyond standard task sharing. '
                'Graph algorithms, machine learning, and computer vision '
                'applied to a real neighbourhood problem.',
                textAlign: TextAlign.center,
                style: GoogleFonts.openSans(
                  color: _mutedGrey,
                  fontSize: 18,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 40),
            ...wows.map((w) {
              return Container(
                width: double.infinity,
                margin: const EdgeInsets.only(bottom: 24),
                color: w['bg'] as Color,
                padding: const EdgeInsets.symmetric(
                    horizontal: 24, vertical: 60),
                child: _buildWowRow(
                  context: context,
                  number: w['number'] as String,
                  title: w['title'] as String,
                  tagline: w['tagline'] as String,
                  desc: w['desc'] as String,
                  imagePaths: w['images'] as List<String>,
                  isReversed: w['isReversed'] as bool,
                ),
              );
            }),
          ],
        ),
      ),
    );
  }

  Widget _buildWowRow({
    required BuildContext context,
    required String number,
    required String title,
    required String tagline,
    required String desc,
    required List<String> imagePaths,
    required bool isReversed,
  }) {
    final isMobile = MediaQuery.of(context).size.width < 850;

    final textContent = Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
          decoration: BoxDecoration(
            color: _tealPrimary,
            borderRadius: BorderRadius.circular(8),
          ),
          child: Text(
            'WOW $number',
            style: GoogleFonts.openSans(
              color: Colors.white,
              fontSize: 11,
              fontWeight: FontWeight.bold,
              letterSpacing: 1.0,
            ),
          ),
        ),
        const SizedBox(height: 16),
        Text(
          title,
          style: GoogleFonts.poppins(
            color: _charcoal,
            fontSize: isMobile ? 28 : 36,
            fontWeight: FontWeight.bold,
            height: 1.2,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          tagline,
          style: GoogleFonts.poppins(
            color: _tealPrimary,
            fontSize: 15,
            fontWeight: FontWeight.w600,
            fontStyle: FontStyle.italic,
          ),
        ),
        const SizedBox(height: 16),
        Text(
          desc,
          style: GoogleFonts.openSans(
            color: _mutedGrey,
            fontSize: 16,
            height: 1.55,
          ),
        ),
      ],
    );

    final showBoth = MediaQuery.of(context).size.width >= 900;

    final imagesContent = SizedBox(
      height: 420,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          _buildMiniPhoneMockup(imagePaths[0], verticalOffset: 0),
          if (showBoth && imagePaths.length > 1) ...[
            const SizedBox(width: 20),
            _buildMiniPhoneMockup(imagePaths[1], verticalOffset: 30),
          ],
        ],
      ),
    );

    if (isMobile) {
      return Column(
        children: [
          textContent,
          const SizedBox(height: 40),
          imagesContent,
        ],
      );
    }

    return Row(
      children: isReversed
          ? [
              Expanded(flex: 5, child: imagesContent),
              const SizedBox(width: 60),
              Expanded(flex: 5, child: textContent),
            ]
          : [
              Expanded(flex: 5, child: textContent),
              const SizedBox(width: 60),
              Expanded(flex: 5, child: imagesContent),
            ],
    );
  }

  // ============================================================
  // HOW IT WORKS
  // ============================================================
  Widget _buildHowItWorksSection(BuildContext context) {
    final steps = [
      {'num': '1', 'title': 'Post a Task', 'desc': 'Describe what you need done, whether it\'s watering plants or collecting a package.'},
      {'num': '2', 'title': 'Match Helper', 'desc': 'Trusted nearby neighbours review your task and offer to assist.'},
      {'num': '3', 'title': 'Coordinate', 'desc': 'Communicate safely inside the app to arrange details and timing.'},
      {'num': '4', 'title': 'Rate & Level Up', 'desc': 'Mark completed, rate your helper, and earn XP rewards.'},
    ];

    return Container(
      width: double.infinity,
      color: Colors.white,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 80),
      child: _constrained(
        Column(
          children: [
            Text(
              'How SupaNeighbour Works',
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: _charcoal,
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Get help in 4 simple steps',
              style: GoogleFonts.openSans(color: _mutedGrey, fontSize: 18),
            ),
            const SizedBox(height: 48),
            LayoutBuilder(
              builder: (context, constraints) {
                final isMobile = constraints.maxWidth < 768;
                return isMobile
                    ? Column(
                        children: steps.map((s) => _buildStepCard(s['num']!, s['title']!, s['desc']!)).toList(),
                      )
                    : Row(
                        children: steps.map((s) => Expanded(child: _buildStepCard(s['num']!, s['title']!, s['desc']!))).toList(),
                      );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStepCard(String num, String title, String desc) {
    return _HoverCard(
      child: Container(
        margin: const EdgeInsets.all(8),
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: _offWhite,
          borderRadius: BorderRadius.circular(20),
          border: Border.all(color: Colors.black.withValues(alpha: 0.03)),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 44,
              height: 44,
              decoration: const BoxDecoration(
                color: _tealPrimary,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Text(num, style: GoogleFonts.poppins(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 18)),
              ),
            ),
            const SizedBox(height: 16),
            Text(title, style: GoogleFonts.poppins(color: _charcoal, fontWeight: FontWeight.bold, fontSize: 18)),
            const SizedBox(height: 8),
            Text(desc, style: GoogleFonts.openSans(color: _mutedGrey, fontSize: 14, height: 1.4)),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // TEAM SECTION
  // ============================================================

// ============================================================
  // TEAM SECTION (Parse & Co Dark Neon Tech Theme)
  // ============================================================
  Widget _buildTeamSection(BuildContext context) {
    // Parse & Co Brand Colors derived from logo
    const parseCoBg = Color(0xFF0D1117);          // Deep dark tech background
    const parseCoNeon = Color(0xFF00FFC2);        // Electric green/teal accent from logo
    const parseCoMuted = Color(0xFF8B949E);       // Muted text color for dark mode

    return Container(
      key: _teamKey,
      width: double.infinity,
      decoration: const BoxDecoration(
        // Smooth gradient transition from white to Parse & Co dark theme
        gradient: LinearGradient(
          begin: Alignment.topCenter,
          end: Alignment.bottomCenter,
          colors: [
            Colors.white,        // Seamless transition from upper section
            parseCoBg,           // Blends smoothly into dark tech section
            parseCoBg,
            Color(0xFF1C7A6F),   // Blends seamlessly into CTA section background
          ],
          stops: [0.0, 0.15, 0.85, 1.0],
        ),
      ),
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 100),
      child: _constrained(
        Column(
          children: [
            // Parse & Co Logo Badge & Monogram Icon
            Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: parseCoBg,
                border: Border.all(color: parseCoNeon, width: 2),
                boxShadow: [
                  BoxShadow(
                    color: parseCoNeon.withValues(alpha: 0.35),
                    blurRadius: 24,
                    spreadRadius: 2,
                  ),
                ],
              ),
              padding: const EdgeInsets.all(8),
              child: ClipOval(
                child: Image.asset(
                  'assets/parse-co.png',
                  fit: BoxFit.contain,
                  errorBuilder: (context, error, stackTrace) => Center(
                    child: Text(
                      'P&C',
                      style: GoogleFonts.poppins(
                        color: parseCoNeon,
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(height: 20),

            // Tagline Pill
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
              decoration: BoxDecoration(
                color: parseCoNeon.withValues(alpha: 0.12),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: parseCoNeon.withValues(alpha: 0.3)),
              ),
              child: Text(
                'BUILT TO SOLVE • DESIGNED TO SCALE',
                style: GoogleFonts.openSans(
                  color: parseCoNeon,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 16),

            // Section Heading
            Text(
              'Meet Parse & Co',
              style: GoogleFonts.poppins(
                color: Colors.white,
                fontSize: 38,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'The team behind SupaNeighbour at the University of Pretoria',
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(color: parseCoMuted, fontSize: 16),
            ),
            const SizedBox(height: 52),

            // Team Cards Row
            Wrap(
              spacing: 20,
              runSpacing: 20,
              alignment: WrapAlignment.center,
              children: _team.map((member) => _buildParseCoTeamCard(member, parseCoNeon)).toList(),
            ),
          ],
        ),
      ),
    );
  }

  // Dark Glassmorphic Card for Parse & Co Team Members
  Widget _buildParseCoTeamCard(_TeamMember member, Color neonAccent) {
    return _HoverCard(
      child: Container(
        width: 210,
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          color: const Color(0xFF161B22), // Dark card surface
          borderRadius: BorderRadius.circular(20),
          border: Border.all(
            color: neonAccent.withValues(alpha: 0.2),
            width: 1,
          ),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: 0.4),
              blurRadius: 16,
              offset: const Offset(0, 8),
            ),
          ],
        ),
        child: Column(
          children: [
            // Member Avatar
            Container(
              width: 76,
              height: 76,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: const Color(0xFF21262D),
                border: Border.all(color: neonAccent.withValues(alpha: 0.4)),
              ),
              child: Center(
                child: Icon(Icons.code_rounded, size: 36, color: neonAccent),
              ),
            ),
            const SizedBox(height: 14),

            // Member Name
            Text(
              member.name,
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: Colors.white,
                fontWeight: FontWeight.bold,
                fontSize: 15,
              ),
            ),
            const SizedBox(height: 4),

            // Member Role
            Text(
              member.role,
              textAlign: TextAlign.center,
              style: GoogleFonts.openSans(
                color: const Color(0xFF8B949E),
                fontSize: 13,
              ),
            ),
            const SizedBox(height: 18),

            // LinkedIn Action
            OutlinedButton(
              onPressed: () => _openUrl(Uri.parse(member.linkedInUrl)),
              style: OutlinedButton.styleFrom(
                minimumSize: const Size.fromHeight(36),
                side: BorderSide(color: neonAccent.withValues(alpha: 0.6)),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: Text(
                'LinkedIn',
                style: GoogleFonts.openSans(
                  color: neonAccent,
                  fontSize: 13,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 8),

            // CV Action
            ElevatedButton(
              onPressed: () => _openCv(member.cvPath),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size.fromHeight(36),
                backgroundColor: neonAccent,
                foregroundColor: const Color(0xFF0D1117),
                elevation: 0,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: Text(
                'View CV',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF0D1117),
                  fontSize: 13,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
  // ============================================================
  // CTA & FOOTER
  // ============================================================
  Widget _buildCTASection(BuildContext context) {
    return Container(
      width: double.infinity,
      color: _tealDark,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 70),
      child: Column(
        children: [
          Text(
            'Ready to connect with your neighbourhood?',
            textAlign: TextAlign.center,
            style: GoogleFonts.poppins(color: Colors.white, fontSize: 32, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 16),
          Text(
            'Download the latest Android release to get started.',
            style: GoogleFonts.openSans(color: Colors.white.withValues(alpha: 0.8), fontSize: 18),
          ),
          const SizedBox(height: 32),
          _HoverButton(
            onPressed: _downloadApk,
            child: ElevatedButton.icon(
              onPressed: _downloadApk,
              icon: const Icon(Icons.download, color: _charcoal),
              label: Text('Download APK Now', style: GoogleFonts.openSans(color: _charcoal, fontWeight: FontWeight.bold, fontSize: 16)),
              style: ElevatedButton.styleFrom(
                backgroundColor: _citrusAccent,
                padding: const EdgeInsets.symmetric(horizontal: 36, vertical: 18),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFooter(BuildContext context) {
    return Container(
      width: double.infinity,
      color: _charcoal,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
      child: Column(
        children: [
          Wrap(
            alignment: WrapAlignment.center,
            spacing: 8,
            children: [
              TextButton(
                onPressed: () {
                  Navigator.push(context, MaterialPageRoute(builder: (context) => const StyleGuidePage()));
                },
                child: Text('Style Guide', style: GoogleFonts.openSans(color: Colors.white70)),
              ),
              const Text('•', style: TextStyle(color: Colors.white38)),
              TextButton(
                onPressed: () {},
                child: Text('Privacy Policy', style: GoogleFonts.openSans(color: Colors.white70)),
              ),
              const Text('•', style: TextStyle(color: Colors.white38)),
              TextButton(
                onPressed: () {},
                child: Text('Terms of Service', style: GoogleFonts.openSans(color: Colors.white70)),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Text(
            '© 2026 SupaNeighbour. Built with Flutter for Web.',
            style: GoogleFonts.openSans(color: Colors.white38, fontSize: 13),
          ),
        ],
      ),
    );
  }
}

// Helper Hover Effect Widgets for Dynamic Interactivity
class _HoverCard extends StatefulWidget {
  final Widget child;
  const _HoverCard({required this.child});

  @override
  State<_HoverCard> createState() => _HoverCardState();
}

class _HoverCardState extends State<_HoverCard> {
  bool _isHovered = false;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() => _isHovered = false),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        transform: Matrix4.translationValues(0, _isHovered ? -6 : 0, 0),
        child: widget.child,
      ),
    );
  }
}

class _HoverButton extends StatefulWidget {
  final Widget child;
  final VoidCallback onPressed;
  const _HoverButton({required this.child, required this.onPressed});

  @override
  State<_HoverButton> createState() => _HoverButtonState();
}

class _HoverButtonState extends State<_HoverButton> {
  bool _isHovered = false;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _isHovered = true),
      onExit: (_) => setState(() => _isHovered = false),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        transform: Matrix4.identity()..scale(_isHovered ? 1.03 : 1.0),
        child: widget.child,
      ),
    );
  }
}