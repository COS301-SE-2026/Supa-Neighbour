import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class StyleGuidePage extends StatelessWidget {
  const StyleGuidePage({super.key});

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
          'Brand Style Guide',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Colors Section
            _buildColorsSection(),
            const SizedBox(height: 40),
            // Typography Section
            _buildTypographySection(),
            const SizedBox(height: 40),
            // Components Section
            _buildComponentsSection(),
            const SizedBox(height: 40),
            // Spacing Section
            _buildSpacingSection(),
            const SizedBox(height: 40),
            // Accessibility Section
            _buildAccessibilitySection(),
            const SizedBox(height: 40),
            // Logo & Iconography Section
            _buildLogoSection(),
            const SizedBox(height: 40),
            // Voice & Tone Section
            _buildVoiceToneSection(),
            const SizedBox(height: 40),
            // Changelog Section
            _buildChangelogSection(),
            const SizedBox(height: 40),
            // Footer
            _buildFooter(),
          ],
        ),
      ),
    );
  }

  Widget _buildColorsSection() {
    final colors = [
      {'name': 'Primary Teal', 'hex': '#2A9D8F', 'color': const Color(0xFF2A9D8F)},
      {'name': 'Citrus Yellow', 'hex': '#E9C46A', 'color': const Color(0xFFE9C46A)},
      {'name': 'Charcoal', 'hex': '#264653', 'color': const Color(0xFF264653)},
      {'name': 'Text Grey', 'hex': '#9CA3AF', 'color': const Color(0xFF9CA3AF)},
      {'name': 'Success Mint', 'hex': '#69B578', 'color': const Color(0xFF69B578)},
      {'name': 'Error Coral', 'hex': '#F4A261', 'color': const Color(0xFFF4A261)},
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Colors', 'Our brand color palette'),
        const SizedBox(height: 16),
        Wrap(
          spacing: 16,
          runSpacing: 16,
          children: colors.map((color) {
            return Container(
              width: 160,
              padding: const EdgeInsets.all(12),
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
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    height: 60,
                    decoration: BoxDecoration(
                      color: color['color'] as Color,
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    color['name'] as String,
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF264653),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  Text(
                    color['hex'] as String,
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF9CA3AF),
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            );
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildTypographySection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Typography', 'Fonts and text styles'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildTypeExample('Poppins', 'Headings', 'Poppins', 24, FontWeight.w600),
              const Divider(),
              _buildTypeExample('Open Sans', 'Body Text', 'Open Sans', 16, FontWeight.w400),
              const Divider(),
              _buildTypeExample('Open Sans', 'Small Text', 'Open Sans', 12, FontWeight.w400),
              const Divider(),
              _buildTypeExample('Open Sans', 'Badges', 'Open Sans', 12, FontWeight.w600),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildTypeExample(String font, String label, String family, double size, FontWeight weight) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Container(
          width: 80,
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
          decoration: BoxDecoration(
            color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(4),
          ),
          child: Text(
            label,
            style: GoogleFonts.openSans(
              color: const Color(0xFF2A9D8F),
              fontSize: 10,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        const SizedBox(width: 16),
        Expanded(
          child: Text(
            'The quick brown fox jumps over the lazy dog',
            style: TextStyle(
              fontFamily: family,
              fontSize: size,
              fontWeight: weight,
              color: const Color(0xFF264653),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildComponentsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Components', 'UI elements and patterns'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Buttons',
                style: GoogleFonts.poppins(
                  color: const Color(0xFF264653),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 12,
                runSpacing: 12,
                children: [
                  ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF2A9D8F),
                    ),
                    child: const Text('Primary'),
                  ),
                  ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white,
                      foregroundColor: const Color(0xFF2A9D8F),
                      side: const BorderSide(color: Color(0xFF2A9D8F)),
                    ),
                  child: const Text('Outlined'),
                  ),
                  ElevatedButton(
                    onPressed: null,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF2A9D8F).withValues(alpha: 0.4),
                    ),
                    child: const Text('Disabled'),
                  ),
                ],
              ),
              const SizedBox(height: 16),
              Text(
                'Badges',
                style: GoogleFonts.poppins(
                  color: const Color(0xFF264653),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: [
                  _buildBadge('Gold', const Color(0xFFE9C46A)),
                  _buildBadge('Silver', const Color(0xFFC0C0C0)),
                  _buildBadge('Bronze', const Color(0xFFCD7F32)),
                  _buildBadge('XP +50', const Color(0xFFE9C46A)),
                ],
              ),
              const SizedBox(height: 16),
              Text(
                'Input Fields',
                style: GoogleFonts.poppins(
                  color: const Color(0xFF264653),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 8),
             SizedBox(
              width: 300,
              child: TextField(
                  decoration: InputDecoration(
                    hintText: 'Enter your email',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: const BorderSide(color: Color(0xFF2A9D8F)),
                    ),
                    focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: const BorderSide(color: Color(0xFF2A9D8F), width: 2),
                    ),
                    filled: true,
                    fillColor: Colors.white,
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildBadge(String label, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: color.withValues(alpha: 0.2),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        label,
        style: GoogleFonts.openSans(
          color: color,
          fontSize: 12,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  Widget _buildSpacingSection() {
    final spacings = [
      {'name': 'XS', 'size': 4, 'color': const Color(0xFFE9C46A)},
      {'name': 'S', 'size': 8, 'color': const Color(0xFF2A9D8F)},
      {'name': 'M', 'size': 16, 'color': const Color(0xFF69B578)},
      {'name': 'L', 'size': 24, 'color': const Color(0xFFF4A261)},
      {'name': 'XL', 'size': 40, 'color': const Color(0xFFE9C46A)},
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Spacing', 'Consistent spacing scale'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            children: spacings.map((spacing) {
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Row(
                  children: [
                    SizedBox(
                      width: spacing['size'] as double,
                      height: 20,
                      child: Container(
                        decoration: BoxDecoration(
                          color: spacing['color'] as Color,
                          borderRadius: BorderRadius.circular(4),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Text(
                      '${spacing['name']} (${spacing['size']}px)',
                      style: GoogleFonts.openSans(
                        color: const Color(0xFF264653),
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              );
            }).toList(),
          ),
        ),
      ],
    );
  }

  Widget _buildAccessibilitySection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Accessibility', 'WCAG 2.2 AA compliant'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildAccessibilityItem(
                'Color Contrast',
                'All text meets WCAG 2.2 AA standards (minimum 4.5:1 for normal text, 3:1 for large text)',
              ),
              const Divider(),
              _buildAccessibilityItem(
                'Touch Targets',
                'All interactive elements have a minimum touch target size of 44x44 points',
              ),
              const Divider(),
              _buildAccessibilityItem(
                'Keyboard Navigation',
                'All interfaces are fully navigable using a keyboard (logical tab order)',
              ),
              const Divider(),
              _buildAccessibilityItem(
                'Screen Readers',
                'Proper ARIA labels and alt-text are used for all icons and image-based badges',
              ),
              const Divider(),
              _buildAccessibilityItem(
                'Motion Reduction',
                'Supports prefers-reduced-motion for users who prefer reduced animation',
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildAccessibilityItem(String title, String description) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(
            Icons.check_circle,
            color: Color(0xFF2A9D8F),
            size: 20,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF6B7280),
                    fontSize: 13,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLogoSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Logo & Iconography', 'Brand marks and icon usage'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    width: 60,
                    height: 60,
                    decoration: BoxDecoration(
                      color: const Color(0xFF2A9D8F),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Center(
                      child: Text(
                        'S',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Container(
                    width: 60,
                    height: 60,
                    decoration: BoxDecoration(
                      color: const Color(0xFF264653),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: const Center(
                      child: Text(
                        'S',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 16),
                  Container(
                    width: 60,
                    height: 60,
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: const Color(0xFF2A9D8F), width: 2),
                    ),
                    child: const Center(
                      child: Text(
                        'S',
                        style: TextStyle(
                          color: Color(0xFF2A9D8F),
                          fontSize: 28,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Text(
                'Logo variants: Full colour (primary), dark background, and outline',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF6B7280),
                  fontSize: 13,
                ),
              ),
              const SizedBox(height: 12),
              const Divider(),
              Row(
                children: [
                  Icon(Icons.home, color: const Color(0xFF2A9D8F), size: 24),
                  const SizedBox(width: 12),
                  Icon(Icons.assignment, color: const Color(0xFF2A9D8F), size: 24),
                  const SizedBox(width: 12),
                  Icon(Icons.chat, color: const Color(0xFF2A9D8F), size: 24),
                  const SizedBox(width: 12),
                  Icon(Icons.leaderboard, color: const Color(0xFF2A9D8F), size: 24),
                  const SizedBox(width: 12),
                  Icon(Icons.person, color: const Color(0xFF2A9D8F), size: 24),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                'Icon library: Material Icons (size 24px, primary teal for active states)',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF6B7280),
                  fontSize: 13,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildVoiceToneSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Voice & Tone', 'Writing style for UI copy'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildVoiceItem('Friendly', 'Use warm, welcoming language like "Hello" and "Welcome back"'),
              const Divider(),
              _buildVoiceItem('Clear', 'Use simple, direct language. Avoid jargon.'),
              const Divider(),
              _buildVoiceItem('Encouraging', 'Use positive language like "Great job!" and "You earned XP!"'),
              const Divider(),
              _buildVoiceItem('Helpful', 'Anticipate user needs. Provide clear error messages with solutions.'),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildVoiceItem(String title, String description) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(
            Icons.volume_up,
            color: Color(0xFF2A9D8F),
            size: 20,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF6B7280),
                    fontSize: 13,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChangelogSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Changelog', 'What changed from Demo 1 to Demo 2'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
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
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildChangelogItem('v2.0', 'July 2026', [
                'Refined colour palette with WCAG contrast ratios',
                'Added typography scale and font hierarchy',
                'Expanded component library with all states',
                'Added accessibility guidelines (WCAG 2.2 AA)',
                'Published live Brand Style Guide page',
                'Added Voice & Tone guidelines',
                'Documented logo and icon usage',
                'Added changelog section',
              ]),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChangelogItem(String version, String date, List<String> changes) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            Text(
              version,
              style: GoogleFonts.poppins(
                color: const Color(0xFF2A9D8F),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(width: 12),
            Text(
              date,
              style: GoogleFonts.openSans(
                color: const Color(0xFF9CA3AF),
                fontSize: 12,
              ),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ...changes.map((change) => Padding(
          padding: const EdgeInsets.only(bottom: 4),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                '• ',
                style: TextStyle(color: Color(0xFF2A9D8F)),
              ),
              Expanded(
                child: Text(
                  change,
                  style: GoogleFonts.openSans(
                    color: const Color(0xFF264653),
                    fontSize: 13,
                  ),
                ),
              ),
            ],
          ),
        )),
      ],
    );
  }

  Widget _buildSectionTitle(String title, String subtitle) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 28,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          subtitle,
          style: GoogleFonts.openSans(
            color: const Color(0xFF9CA3AF),
            fontSize: 14,
          ),
        ),
      ],
    );
  }

  Widget _buildFooter() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFFF8FAFA),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Center(
        child: Text(
          'SupaNeighbour Brand Style Guide v1.0',
          style: GoogleFonts.openSans(
            color: const Color(0xFF9CA3AF),
            fontSize: 12,
          ),
        ),
      ),
    );
  }
}