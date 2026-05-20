import 'package:flutter/material.dart';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';
import 'package:google_fonts/google_fonts.dart';

class TaskDetailScreen extends StatelessWidget {
  const TaskDetailScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Mock data - will be replaced with real data from API later
    final task = {
      'title': 'Water my plants',
      'xpReward': 50,
      'time': 'Tomorrow at 3:00 PM',
      'location': '2 doors down • 50m away',
      'instructions': 'Please water the 3 pots on the balcony. Use the blue watering can under the sink.',
      'helperName': 'Sarah Johnson',
      'helperTrustScore': 4.8,
    };

    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF), // Clean White
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF264653)), // Charcoal
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Details',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653), // Charcoal
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
            // Task Card
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.04),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    task['title']!,
                    style: GoogleFonts.poppins(
                      color: const Color(0xFF264653), // Charcoal
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  // XP Badge
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                    decoration: BoxDecoration(
                      color: const Color(0xFFE9C46A), // Citrus Yellow
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Text(
                      '+${task['xpReward']} XP',
                      style: GoogleFonts.openSans(
                        color: const Color(0xFF264653), // Charcoal
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(height: 12),
                  _buildInfoRow(Icons.access_time, task['time']!),
                  const SizedBox(height: 8),
                  _buildInfoRow(Icons.location_on, task['location']!),
                  const SizedBox(height: 12),
                  Text(
                    task['instructions']!,
                    style: GoogleFonts.openSans(
                      color: const Color(0xFF264653), // Charcoal
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            // Helper Section
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.04),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Available Helper',
                    style: GoogleFonts.poppins(
                      color: const Color(0xFF264653), // Charcoal
                      fontSize: 16,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Container(
                        width: 50,
                        height: 50,
                        decoration: BoxDecoration(
                          color: const Color(0xFF2A9D8F).withOpacity(0.2), // Vibrant Teal tint
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.person,
                          color: Color(0xFF2A9D8F), // Vibrant Teal
                          size: 30,
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              task['helperName']!,
                              style: GoogleFonts.openSans(
                                color: const Color(0xFF264653), // Charcoal
                                fontSize: 16,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                            RatingBar.builder(
                              initialRating: task['helperTrustScore']!,
                              minRating: 1,
                              direction: Axis.horizontal,
                              allowHalfRating: true,
                              itemCount: 5,
                              itemSize: 18,
                              ignoreGestures: true,
                              itemBuilder: (_, __) => const Icon(
                                Icons.star,
                                color: Color(0xFFE9C46A), // Citrus Yellow
                              ),
                              onRatingUpdate: (_) {},
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            // Accept Task Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: () {
                  // TODO: Implement accept task
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Task accepted! (Coming soon)')),
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2A9D8F), // Vibrant Teal
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: Text(
                  'Accept Task',
                  style: GoogleFonts.openSans(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
            const SizedBox(height: 12),
            // Message Helper Button (Outline)
            SizedBox(
              width: double.infinity,
              child: OutlinedButton(
                onPressed: () {
                  // TODO: Implement message helper
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Message helper (Coming soon)')),
                  );
                },
                style: OutlinedButton.styleFrom(
                  side: const BorderSide(color: Color(0xFF2A9D8F)), // Vibrant Teal
                  foregroundColor: const Color(0xFF264653), // Charcoal
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: Text(
                  'Message Helper',
                  style: GoogleFonts.openSans(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(IconData icon, String text) {
    return Row(
      children: [
        Icon(icon, size: 18, color: const Color(0xFF2A9D8F)), // Vibrant Teal
        const SizedBox(width: 8),
        Text(
          text,
          style: GoogleFonts.openSans(
            color: const Color(0xFF264653), // Charcoal
            fontSize: 14,
          ),
        ),
      ],
    );
  }
}