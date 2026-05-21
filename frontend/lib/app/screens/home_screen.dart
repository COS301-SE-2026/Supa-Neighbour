import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../widgets/bottom_nav_bar.dart';
import 'create_task_screen.dart';
import 'task_detail_screen.dart';
import 'my_tasks_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const HomeContent(),
    const MyTasksScreen(),
    const PlaceholderScreen(title: 'Chat'),
    const PlaceholderScreen(title: 'Leaderboard'),
    const PlaceholderScreen(title: 'Profile'),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      body: _screens[_currentIndex],
      bottomNavigationBar: BottomNavBar(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
      ),
    );
  }
}

// Home Content Widget
class HomeContent extends StatelessWidget {
  const HomeContent({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        title: Text(
          'Supa Neighbour',
          style: GoogleFonts.poppins(
            color: const Color(0xFF2A9D8F),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.notifications_none, color: Color(0xFF2A9D8F)),
            onPressed: () {
              // TODO: Show notifications
            },
          ),
        ],
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          // TODO: Refresh data
        },
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Welcome Section
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      const Color(0xFF2A9D8F).withValues(alpha: 0.1),
                      const Color(0xFFE9C46A).withValues(alpha: 0.05),
                    ],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            'Good morning,',
                            style: GoogleFonts.openSans(
                              color: const Color(0xFF264653),
                              fontSize: 14,
                            ),
                          ),
                          Text(
                            'Blessing',
                            style: GoogleFonts.poppins(
                              color: const Color(0xFF2A9D8F),
                              fontSize: 24,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                            decoration: BoxDecoration(
                              color: const Color(0xFFE9C46A),
                              borderRadius: BorderRadius.circular(20),
                            ),
                            child: Text(
                              '⭐ 4.8 Trust Score',
                              style: GoogleFonts.openSans(
                                color: const Color(0xFF264653),
                                fontSize: 12,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                    Container(
                      width: 60,
                      height: 60,
                      decoration: BoxDecoration(
                        color: const Color(0xFF2A9D8F).withValues(alpha: 0.2),
                        shape: BoxShape.circle,
                      ),
                      child: const Icon(
                        Icons.person,
                        color: Color(0xFF2A9D8F),
                        size: 40,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),

              // Quick Stats Row
              Row(
                children: [
                  _buildStatCard('5', 'Helps Given', const Color(0xFF2A9D8F)),
                  const SizedBox(width: 12),
                  _buildStatCard('12', 'Tasks Posted', const Color(0xFFE9C46A)),
                  const SizedBox(width: 12),
                  _buildStatCard('3', 'Active', const Color(0xFF69B578)),
                ],
              ),
              const SizedBox(height: 24),

              // Nearby Tasks Section
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    'Nearby Tasks',
                    style: GoogleFonts.poppins(
                      color: const Color(0xFF264653),
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  TextButton(
                    onPressed: () {
                      // TODO: Navigate to My Tasks
                    },
                    child: Text(
                      'See All',
                      style: GoogleFonts.openSans(
                        color: const Color(0xFF2A9D8F),
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),

              // Task List
              _buildTaskCard(
                title: 'Water my plants',
                xpReward: 50,
                time: 'Today, 3:00 PM',
                distance: '2 doors down',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const TaskDetailScreen(),
                    ),
                  );
                },
              ),
              const SizedBox(height: 12),
              _buildTaskCard(
                title: 'Collect package',
                xpReward: 30,
                time: 'Tomorrow, 10:00 AM',
                distance: 'Around the corner',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const TaskDetailScreen(),
                    ),
                  );
                },
              ),
              const SizedBox(height: 12),
              _buildTaskCard(
                title: 'Feed small pets',
                xpReward: 40,
                time: 'Today, 5:00 PM',
                distance: 'Next street',
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const TaskDetailScreen(),
                    ),
                  );
                },
              ),
              const SizedBox(height: 12),

              // Floating Action Button for Create Task
              const SizedBox(height: 80),
            ],
          ),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => const CreateTaskScreen(),
            ),
          );
        },
        backgroundColor: const Color(0xFF2A9D8F),
        child: const Icon(Icons.add, color: Colors.white),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }

  Widget _buildStatCard(String value, String label, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
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
          children: [
            Text(
              value,
              style: GoogleFonts.poppins(
                color: color,
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              label,
              style: GoogleFonts.openSans(
                color: const Color(0xFF264653),
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTaskCard({
    required String title,
    required int xpReward,
    required String time,
    required String distance,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
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
                borderRadius: BorderRadius.circular(12),
              ),
              child: const Icon(
                Icons.assignment,
                color: Color(0xFF2A9D8F),
                size: 28,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: GoogleFonts.poppins(
                      color: const Color(0xFF264653),
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      const Icon(Icons.access_time, size: 14, color: Color(0xFF2A9D8F)),
                      const SizedBox(width: 4),
                      Text(
                        time,
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(width: 12),
                      const Icon(Icons.location_on, size: 14, color: Color(0xFF2A9D8F)),
                      const SizedBox(width: 4),
                      Text(
                        distance,
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFE9C46A),
                borderRadius: BorderRadius.circular(20),
              ),
              child: Text(
                '+$xpReward XP',
                style: GoogleFonts.openSans(
                  color: const Color(0xFF264653),
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// Temporary placeholder for screens that are not yet built
class PlaceholderScreen extends StatelessWidget {
  final String title;
  const PlaceholderScreen({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      body: Center(
        child: Text(
          '$title Screen Coming Soon',
          style: GoogleFonts.openSans(
            color: const Color(0xFF264653),
            fontSize: 16,
          ),
        ),
      ),
    );
  }
}