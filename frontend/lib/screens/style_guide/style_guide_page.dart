import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class StyleGuidePage extends StatefulWidget {
  const StyleGuidePage({super.key});

  @override
  State<StyleGuidePage> createState() => _StyleGuidePageState();
}

class _StyleGuidePageState extends State<StyleGuidePage> {
  bool _isDarkMode = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _isDarkMode ? const Color(0xFF1A1A1A) : Colors.white,
      appBar: AppBar(
        backgroundColor: _isDarkMode ? const Color(0xFF1A1A1A) : Colors.white,
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: _isDarkMode ? Colors.white : const Color(0xFF264653)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Row(
          mainAxisAlignment: MainAxisAlignment.center,
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
                    borderRadius: BorderRadius.circular(6),
                  ),
                  child: const Center(
                    child: Text(
                      'S',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                );
              },
            ),
            const SizedBox(width: 8),
            Text(
              'Style Guide',
              style: GoogleFonts.poppins(
                color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        centerTitle: true,
        actions: [
          Row(
            children: [
              Icon(
                Icons.light_mode,
                color: _isDarkMode ? Colors.grey : const Color(0xFFE9C46A),
                size: 20,
              ),
              Switch(
                value: _isDarkMode,
                onChanged: (value) {
                  setState(() {
                    _isDarkMode = value;
                  });
                },
                activeColor: const Color(0xFF2A9D8F),
              ),
              Icon(
                Icons.dark_mode,
                color: _isDarkMode ? Colors.white : Colors.grey,
                size: 20,
              ),
            ],
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildDesignPrinciplesSection(),
            const SizedBox(height: 40),
            _buildColorsSection(),
            const SizedBox(height: 40),
            _buildTypographySection(),
            const SizedBox(height: 40),
            _buildComponentsSection(),
            const SizedBox(height: 40),
            _buildSpacingSection(),
            const SizedBox(height: 40),
            _buildChatBubblesSection(),
            const SizedBox(height: 40),
            _buildShadowsSection(),
            const SizedBox(height: 40),
            _buildBreakpointsSection(),
            const SizedBox(height: 40),
            _buildDarkModeSection(),
            const SizedBox(height: 40),
            _buildAccessibilitySection(),
            const SizedBox(height: 40),
            _buildLogoSection(),
            const SizedBox(height: 40),
            _buildVoiceToneSection(),
            const SizedBox(height: 40),
            _buildChangelogSection(),
            const SizedBox(height: 40),
            _buildFooter(),
          ],
        ),
      ),
    );
  }

  // ============================================================
  // DESIGN PRINCIPLES
  // ============================================================
  Widget _buildDesignPrinciplesSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Design Principles', 'Our high-level design philosophy'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
              _buildPrincipleItem(
                'Consistency',
                'Uniform use of colors, typography, and UI elements across all screens to build familiarity and trust.',
              ),
              const Divider(),
              _buildPrincipleItem(
                'Simplicity',
                'A clean, uncluttered interface that prioritizes essential information. Soft rounded corners and ample whitespace reduce cognitive load.',
              ),
              const Divider(),
              _buildPrincipleItem(
                'Responsiveness',
                'Fluid layouts that adapt seamlessly to different screen sizes and orientations.',
              ),
              const Divider(),
              _buildPrincipleItem(
                'Accessibility',
                'An "Accessibility-First" approach ensuring usability for individuals with diverse abilities, adhering to standards like WCAG.',
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildPrincipleItem(String title, String description) {
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
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
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

  // ============================================================
  // COLORS
  // ============================================================
  Widget _buildColorsSection() {
    final colors = [
      {'name': 'Primary Teal', 'hex': '#2A9D8F', 'rgb': '42, 157, 143', 'hsl': '172°, 58%, 39%', 'color': const Color(0xFF2A9D8F)},
      {'name': 'Citrus Yellow', 'hex': '#E9C46A', 'rgb': '233, 196, 106', 'hsl': '42°, 74%, 66%', 'color': const Color(0xFFE9C46A)},
      {'name': 'Charcoal', 'hex': '#264653', 'rgb': '38, 70, 83', 'hsl': '195°, 37%, 24%', 'color': const Color(0xFF264653)},
      {'name': 'Text Grey', 'hex': '#9CA3AF', 'rgb': '156, 163, 175', 'hsl': '220°, 11%, 65%', 'color': const Color(0xFF9CA3AF)},
      {'name': 'Success Mint', 'hex': '#69B578', 'rgb': '105, 181, 120', 'hsl': '128°, 34%, 56%', 'color': const Color(0xFF69B578)},
      {'name': 'Error Coral', 'hex': '#F4A261', 'rgb': '244, 162, 97', 'hsl': '27°, 87%, 67%', 'color': const Color(0xFFF4A261)},
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Colors', 'Our brand color palette'),
        const SizedBox(height: 16),
            Wrap(
            spacing: 16,
            runSpacing: 16,
            alignment: WrapAlignment.center,
            children: colors.map((color) {
            return Container(
              width: 180,
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                      color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  Text(
                    'HEX: ${color['hex']}',
                    style: GoogleFonts.openSans(
                      color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                      fontSize: 11,
                    ),
                  ),
                  Text(
                    'RGB: ${color['rgb']}',
                    style: GoogleFonts.openSans(
                      color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                      fontSize: 11,
                    ),
                  ),
                  Text(
                    'HSL: ${color['hsl']}',
                    style: GoogleFonts.openSans(
                      color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                      fontSize: 11,
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

  // ============================================================
  // TYPOGRAPHY
  // ============================================================
  Widget _buildTypographySection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Typography', 'Fonts and text styles'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
              color: _isDarkMode ? Colors.white : const Color(0xFF264653),
            ),
          ),
        ),
      ],
    );
  }

  // ============================================================
  // COMPONENTS
  // ============================================================
  Widget _buildComponentsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Components', 'UI elements and patterns'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                  color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                  fontSize: 16,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 12,
                runSpacing: 12,
                alignment: WrapAlignment.center,
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
                  color: _isDarkMode ? Colors.white : const Color(0xFF264653),
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
                  color: _isDarkMode ? Colors.white : const Color(0xFF264653),
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
                    hintStyle: TextStyle(color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF)),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: const BorderSide(color: Color(0xFF2A9D8F)),
                    ),
                    focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(12),
                      borderSide: const BorderSide(color: Color(0xFF2A9D8F), width: 2),
                    ),
                    filled: true,
                    fillColor: _isDarkMode ? const Color(0xFF1A1A1A) : Colors.white,
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

  // ============================================================
  // CHAT BUBBLES
  // ============================================================
  Widget _buildChatBubblesSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Chat Bubbles', 'Message styles for in-app chat'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    decoration: BoxDecoration(
                      color: const Color(0xFF2A9D8F),
                      borderRadius: const BorderRadius.only(
                        topLeft: Radius.circular(20),
                        topRight: Radius.circular(20),
                        bottomLeft: Radius.circular(20),
                        bottomRight: Radius.circular(4),
                      ),
                    ),
                    child: Text(
                      'Hello! I can help with that.',
                      style: GoogleFonts.openSans(
                        color: Colors.white,
                        fontSize: 15,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Align(
                alignment: Alignment.centerRight,
                child: Text(
                  'Sent message • 2:30 PM',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
                    fontSize: 10,
                  ),
                ),
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    decoration: BoxDecoration(
                      color: _isDarkMode ? const Color(0xFF3D3D3D) : const Color(0xFFF5F5F5),
                      borderRadius: const BorderRadius.only(
                        topLeft: Radius.circular(20),
                        topRight: Radius.circular(20),
                        bottomLeft: Radius.circular(4),
                        bottomRight: Radius.circular(20),
                      ),
                    ),
                    child: Text(
                      'Thanks! When can you come?',
                      style: GoogleFonts.openSans(
                        color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                        fontSize: 15,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Align(
                alignment: Alignment.centerLeft,
                child: Text(
                  'Received message • 2:32 PM',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
                    fontSize: 10,
                  ),
                ),
              ),
              const SizedBox(height: 16),
              const Divider(),
              const SizedBox(height: 12),
              _buildChatBubbleSpec('Sent Message', 'Vibrant Teal (#2A9D8F)', 'White (#FFFFFF)', 'Right-aligned'),
              const SizedBox(height: 8),
              _buildChatBubbleSpec('Received Message', 'Light Grey (#F5F5F5)', 'Charcoal (#264653)', 'Left-aligned'),
              const SizedBox(height: 8),
              _buildChatBubbleSpec('Timestamp', '—', 'Muted Grey (#9CA3AF)', 'Below message'),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildChatBubbleSpec(String element, String background, String text, String alignment) {
    return Row(
      children: [
        Container(
          width: 120,
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          decoration: BoxDecoration(
            color: const Color(0xFF2A9D8F).withValues(alpha: 0.1),
            borderRadius: BorderRadius.circular(4),
          ),
          child: Text(
            element,
            style: GoogleFonts.openSans(
              color: const Color(0xFF2A9D8F),
              fontSize: 10,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Text(
            'Background: $background • Text: $text • $alignment',
            style: GoogleFonts.openSans(
              color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
              fontSize: 12,
            ),
          ),
        ),
      ],
    );
  }

  // ============================================================
  // SPACING
  // ============================================================
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
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                        color: _isDarkMode ? Colors.white : const Color(0xFF264653),
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

  // ============================================================
  // SHADOWS
  // ============================================================
  Widget _buildShadowsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Shadows', 'Elevation and depth hierarchy'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              _buildShadowItem(
                'Subtle',
                'Cards, containers',
                '0px 2px 8px rgba(0,0,0,0.04)',
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.04),
                  blurRadius: 8,
                  offset: const Offset(0, 2),
                ),
              ),
              const Divider(),
              _buildShadowItem(
                'Medium',
                'Dropdowns, modals',
                '0px 4px 16px rgba(0,0,0,0.08)',
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.08),
                  blurRadius: 16,
                  offset: const Offset(0, 4),
                ),
              ),
              const Divider(),
              _buildShadowItem(
                'Prominent',
                'FAB, elevated buttons',
                '0px 8px 24px rgba(0,0,0,0.12)',
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.12),
                  blurRadius: 24,
                  offset: const Offset(0, 8),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildShadowItem(String name, String usage, String value, BoxShadow shadow) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Container(
            width: 80,
            height: 50,
            decoration: BoxDecoration(
              color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
              borderRadius: BorderRadius.circular(8),
              boxShadow: [shadow],
            ),
            child: Center(
              child: Text(
                name,
                style: GoogleFonts.openSans(
                  color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                  fontSize: 10,
                  fontWeight: FontWeight.w600,
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
                  name,
                  style: GoogleFonts.poppins(
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  'Usage: $usage',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                    fontSize: 12,
                  ),
                ),
                Text(
                  'Value: $value',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
                    fontSize: 11,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // BREAKPOINTS
  // ============================================================
  Widget _buildBreakpointsSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Breakpoints', 'Responsive design breakpoints'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
            children: [
              _buildBreakpointItem('Mobile', '0px - 767px', 'Phone', Icons.phone_android),
              const Divider(),
              _buildBreakpointItem('Tablet', '768px - 1023px', 'Tablet', Icons.tablet),
              const Divider(),
              _buildBreakpointItem('Desktop', '1024px+', 'Desktop', Icons.desktop_windows),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildBreakpointItem(String name, String range, String target, IconData icon) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        children: [
          Icon(
            icon,
            color: const Color(0xFF2A9D8F),
            size: 28,
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  name,
                  style: GoogleFonts.poppins(
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  'Range: $range',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                    fontSize: 12,
                  ),
                ),
                Text(
                  'Target: $target',
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // DARK MODE
  // ============================================================
  Widget _buildDarkModeSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Dark Mode', 'Dark theme considerations'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
              _buildDarkModeItem(
                'Background',
                'Dark grey (#1A1A1A) or Deep Charcoal (#1E2A2F)',
                const Color(0xFF1A1A1A),
              ),
              const Divider(),
              _buildDarkModeItem(
                'Surface',
                'Slightly lighter grey (#2D2D2D)',
                const Color(0xFF2D2D2D),
              ),
              const Divider(),
              _buildDarkModeItem(
                'Text',
                'White (#FFFFFF) or Light Grey (#E5E5E5)',
                Colors.white,
              ),
              const Divider(),
              _buildDarkModeItem(
                'Primary Button',
                'Vibrant Teal (#2A9D8F) — maintains contrast on dark background',
                const Color(0xFF2A9D8F),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildDarkModeItem(String label, String description, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Container(
            width: 40,
            height: 40,
            decoration: BoxDecoration(
              color: color,
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: Colors.grey.withValues(alpha: 0.3)),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  // ============================================================
  // ACCESSIBILITY
  // ============================================================
  Widget _buildAccessibilitySection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Accessibility', 'WCAG 2.2 AA compliant'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
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

  // ============================================================
  // LOGO & ICONOGRAPHY
  // ============================================================
  Widget _buildLogoSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Logo & Iconography', 'Brand marks and icon usage'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
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
                      color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                  color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
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
                  color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
                  fontSize: 13,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  // ============================================================
  // VOICE & TONE
  // ============================================================
  Widget _buildVoiceToneSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Voice & Tone', 'Writing style for UI copy'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.openSans(
                    color: _isDarkMode ? Colors.grey[400] : const Color(0xFF6B7280),
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

  // ============================================================
  // CHANGELOG
  // ============================================================
  Widget _buildChangelogSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSectionTitle('Changelog', 'What changed from Demo 1 to Demo 2'),
        const SizedBox(height: 16),
        Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: _isDarkMode ? const Color(0xFF2D2D2D) : Colors.white,
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
                color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
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
                    color: _isDarkMode ? Colors.white : const Color(0xFF264653),
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

  // ============================================================
  // SECTION TITLE
  // ============================================================
  Widget _buildSectionTitle(String title, String subtitle) {
  return Column(
    crossAxisAlignment: CrossAxisAlignment.center,
    children: [
      Text(
        title,
        style: GoogleFonts.poppins(
          color: _isDarkMode ? Colors.white : const Color(0xFF264653),
          fontSize: 28,
          fontWeight: FontWeight.bold,
        ),
        textAlign: TextAlign.center,
      ),
      const SizedBox(height: 4),
      Text(
        subtitle,
        style: GoogleFonts.openSans(
          color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
          fontSize: 14,
        ),
        textAlign: TextAlign.center,
      ),
    ],
  );
}

  // ============================================================
  // FOOTER
  // ============================================================
  Widget _buildFooter() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: _isDarkMode ? const Color(0xFF2D2D2D) : const Color(0xFFF8FAFA),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Center(
        child: Text(
          'SupaNeighbour Brand Style Guide v2.0',
          style: GoogleFonts.openSans(
            color: _isDarkMode ? Colors.grey[400] : const Color(0xFF9CA3AF),
            fontSize: 12,
          ),
        ),
      ),
    );
  }
}