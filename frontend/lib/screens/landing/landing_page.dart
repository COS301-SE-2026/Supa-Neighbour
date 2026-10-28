import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../style_guide/style_guide_page.dart';
import 'package:url_launcher/url_launcher.dart';

class _TeamMember {
  final String name;
  final String role;
  final String linkedInUrl;

  /// Path relative to the web root, e.g. 'cvs/jane-doe.pdf'
  /// (file lives in web/cvs/jane-doe.pdf)
  final String cvPath;

  /// Optional headshot, e.g. 'assets/team/jane.png' (declare in pubspec.yaml)
  final String? photoAsset;

  const _TeamMember({
    required this.name,
    required this.role,
    required this.linkedInUrl,
    required this.cvPath,
    this.photoAsset,
  });
}

class LandingPage extends StatefulWidget {
  const LandingPage({super.key});

  @override
  State<LandingPage> createState() => _LandingPageState();
}

class _LandingPageState extends State<LandingPage> {
  // Shared max width so every section's content lines up on wide screens
  static const double _contentMaxWidth = 1200;

  final ScrollController _scrollController = ScrollController();
  final GlobalKey _feature1Key = GlobalKey();
  final GlobalKey _feature2Key = GlobalKey();
  final GlobalKey _feature3Key = GlobalKey();
  final GlobalKey _feature4Key = GlobalKey();
  final GlobalKey _feature5Key = GlobalKey();
  final GlobalKey _teamKey = GlobalKey();

  static const List<_TeamMember> _team = [
    _TeamMember(
      name: 'Blessing K Gibendi',
      role: 'Team Leader',
      linkedInUrl: 'https://www.linkedin.com/in/your-handle-1',
      cvPath: 'cvs/member-1.pdf',
    ),
    _TeamMember(
      name: 'Michelle W Njoroge',
      role: 'Frontend Lead and Design',
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

  /// Centres content and caps its width so all sections line up.
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
            _buildTeamSection(context),
            _buildCTASection(context),
            _buildFooter(context),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // TEAM
  // ============================================================
  Widget _buildTeamSection(BuildContext context) {
    const cardWidth = 220.0;
    const gap = 20.0;

    return Container(
      key: _teamKey,
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: Colors.white,
      child: _constrained(
        Column(
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFE9C46A).withValues(alpha: 0.3),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                'Parse & Co',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF264653),
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'Meet the Team',
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 560),
              child: Text(
                'The five students building SupaNeighbour to bring neighbourhoods closer together.',
                textAlign: TextAlign.center,
                style: GoogleFonts.openSans(
                  color: const Color(0xFF6B7280),
                  fontSize: 18,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 40),
            LayoutBuilder(
              builder: (context, constraints) {
                // How many cards fit per row at this width
                var perRow =
                    ((constraints.maxWidth + gap) / (cardWidth + gap)).floor();
                perRow = perRow.clamp(1, _team.length);
                // Avoid an orphaned 4 + 1 split: use 3 + 2 instead
                if (perRow == 4) perRow = 3;

                return SizedBox(
                  width: perRow * cardWidth + (perRow - 1) * gap,
                  child: Wrap(
                    alignment: WrapAlignment.center,
                    spacing: gap,
                    runSpacing: gap,
                    children: _team.map(_buildTeamCard).toList(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTeamAvatar(_TeamMember member) {
    const teal = Color(0xFF2A9D8F);
    const fallback = Icon(Icons.person_outline, size: 44, color: Color(0xFF1C7A6F));

    return Container(
      width: 88,
      height: 88,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: teal.withValues(alpha: 0.12),
      ),
      child: member.photoAsset == null
          ? fallback
          : ClipOval(
              child: Image.asset(
                member.photoAsset!,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) => fallback,
              ),
            ),
    );
  }

  Widget _buildTeamCard(_TeamMember member) {
    const darkTeal = Color(0xFF1C7A6F);

    return Container(
      width: 220,
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 28),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: const Color(0xFF2A9D8F).withValues(alpha: 0.25)),
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
          _buildTeamAvatar(member),
          const SizedBox(height: 16),
          Text(
            member.name,
            textAlign: TextAlign.center,
            style: GoogleFonts.poppins(
              color: const Color(0xFF264653),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            member.role,
            textAlign: TextAlign.center,
            style: GoogleFonts.openSans(
              color: const Color(0xFF6B7280),
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 20),
          SizedBox(
            width: double.infinity,
            child: OutlinedButton.icon(
              onPressed: () => _openUrl(Uri.parse(member.linkedInUrl)),
              icon: const Icon(Icons.link, size: 18, color: darkTeal),
              label: Text(
                'LinkedIn',
                style: GoogleFonts.openSans(
                  color: darkTeal,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              style: OutlinedButton.styleFrom(
                minimumSize: const Size.fromHeight(44),
                side: const BorderSide(color: darkTeal, width: 1.5),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
          ),
          const SizedBox(height: 10),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () => _openCv(member.cvPath),
              icon: const Icon(Icons.description_outlined, size: 18, color: Colors.white),
              label: Text(
                'View CV',
                style: GoogleFonts.openSans(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size.fromHeight(44),
                backgroundColor: darkTeal,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // HEADER
  // ============================================================
  Widget _buildHeader(BuildContext context) {
    final isMobile = MediaQuery.of(context).size.width < 768;

    return Container(
      width: double.infinity,
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
      child: _constrained(
        isMobile
            ? Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
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
                        Flexible(
                          child: Text(
                            'SupaNeighbour',
                            overflow: TextOverflow.ellipsis,
                            style: GoogleFonts.poppins(
                              color: const Color(0xFF264653),
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(width: 12),
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
                      TextButton(
                        onPressed: () {
                          final ctx = _teamKey.currentContext;
                          if (ctx != null) {
                            Scrollable.ensureVisible(
                              ctx,
                              duration: const Duration(milliseconds: 500),
                              curve: Curves.easeInOut,
                            );
                          }
                        },
                        child: Text(
                          'Team',
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
      width: double.infinity,
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
      child: _constrained(
        isMobile
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
        Wrap(
          spacing: 16,
          runSpacing: 12,
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
  // FEATURE SECTIONS (one shared layout)
  // ============================================================
  Widget _buildFeatureSection({
    required GlobalKey sectionKey,
    required String title,
    required String subtitle,
    required List<String> screenshots,
    required Color background,
  }) {
    return Container(
      key: sectionKey,
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: background,
      child: _constrained(
        Column(
          children: [
            Text(
              title,
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 640),
              child: Text(
                subtitle,
                textAlign: TextAlign.center,
                style: GoogleFonts.openSans(
                  color: const Color(0xFF6B7280),
                  fontSize: 18,
                  height: 1.5,
                ),
              ),
            ),
            const SizedBox(height: 32),
            Wrap(
              alignment: WrapAlignment.center,
              spacing: 16,
              runSpacing: 16,
              children: screenshots.map(_buildFeatureScreenshot).toList(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildFeatureDetail1(BuildContext context) {
    return _buildFeatureSection(
      sectionKey: _feature1Key,
      title: 'Create a Task',
      subtitle: 'Post a task in seconds and get help from your neighbours.',
      screenshots: const [
        'assets/screenshots/create-task.png',
        'assets/screenshots/available-helpers.png',
        'assets/screenshots/tasks-posted.png',
      ],
      background: Colors.white,
    );
  }

  Widget _buildFeatureDetail2(BuildContext context) {
    return _buildFeatureSection(
      sectionKey: _feature2Key,
      title: 'Complete a Task',
      subtitle: 'Accept tasks and guide them from start to finish.',
      screenshots: const [
        'assets/screenshots/tasks-accepted.png',
        'assets/screenshots/task-complete.png',
      ],
      background: const Color(0xFFF8FAFA),
    );
  }

  Widget _buildFeatureDetail3(BuildContext context) {
    return _buildFeatureSection(
      sectionKey: _feature3Key,
      title: 'Review & Rate',
      subtitle: 'Review completed tasks and rate helpers to build trust in the community.',
      screenshots: const [
        'assets/screenshots/task-complete.png',
        'assets/screenshots/rating.png',
      ],
      background: Colors.white,
    );
  }

  Widget _buildFeatureDetail4(BuildContext context) {
    return _buildFeatureSection(
      sectionKey: _feature4Key,
      title: 'Gamification',
      subtitle: 'Earn XP, level up, and unlock achievements as you help your community.',
      screenshots: const [
        'assets/screenshots/leaderboard.png',
        'assets/screenshots/achievements.png',
      ],
      background: const Color(0xFFF8FAFA),
    );
  }

  Widget _buildFeatureDetail5(BuildContext context) {
    return _buildFeatureSection(
      sectionKey: _feature5Key,
      title: 'Community Bulletin',
      subtitle: 'Stay informed with neighbourhood announcements, lost pet alerts, and local events.',
      screenshots: const [
        'assets/screenshots/bulletin-feed.png',
        'assets/screenshots/create-bulletin.png',
      ],
      background: Colors.white,
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
      width: double.infinity,
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 60),
      color: const Color(0xFFF8FAFA),
      child: _constrained(
        Column(
          children: [
            Text(
              'How It Works',
              textAlign: TextAlign.center,
              style: GoogleFonts.poppins(
                color: const Color(0xFF264653),
                fontSize: 36,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              'Four simple steps to get started',
              textAlign: TextAlign.center,
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
      ),
    );
  }

  // ============================================================
  // CTA SECTION
  // ============================================================
  Widget _buildCTASection(BuildContext context) {
    return Container(
      width: double.infinity,
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
      width: double.infinity,
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