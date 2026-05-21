import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'task_detail_screen.dart';

class MyTasksScreen extends StatefulWidget {
  const MyTasksScreen({super.key});

  @override
  State<MyTasksScreen> createState() => _MyTasksScreenState();
}

class _MyTasksScreenState extends State<MyTasksScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        title: Text(
          'My Tasks',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        bottom: TabBar(
          controller: _tabController,
          labelColor: const Color(0xFF2A9D8F),
          unselectedLabelColor: const Color(0xFF8D8A8D),
          indicatorColor: const Color(0xFF2A9D8F),
          tabs: const [  // ← const here
            Tab(text: 'Posted'),
            Tab(text: 'Accepted'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          const _PostedTasksTab(),
          const _AcceptedTasksTab(),
        ],
      ),
    );
  }
}

class _PostedTasksTab extends StatelessWidget {
  const _PostedTasksTab();

  @override
  Widget build(BuildContext context) {
    // Mock data - would come from API
    final List<Map<String, dynamic>> tasks = [
      {'title': 'Water my plants', 'status': 'Pending', 'time': 'Today, 3:00 PM', 'xp': 50},
      {'title': 'Collect package', 'status': 'Assigned to Sarah', 'time': 'Tomorrow, 10:00 AM', 'xp': 30},
      {'title': 'Feed small pets', 'status': 'Pending', 'time': 'Today, 5:00 PM', 'xp': 40},
    ];

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: tasks.length,
      itemBuilder: (context, index) {
        final title = tasks[index]['title'] as String;
        final status = tasks[index]['status'] as String;
        final time = tasks[index]['time'] as String;
        final xp = tasks[index]['xp'] as int;
        final isAssigned = status.contains('Assigned');

        return GestureDetector(
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const TaskDetailScreen()),
            );
          },
          child: Container(
            margin: const EdgeInsets.only(bottom: 12),
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
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Expanded(
                      child: Text(
                        title,
                        style: GoogleFonts.poppins(
                          color: const Color(0xFF264653),
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: isAssigned ? const Color(0xFFA5D8C9) : const Color(0xFFE9C46A),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        status,
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    const Icon(Icons.access_time, size: 14, color: Color(0xFF2A9D8F)),
                    const SizedBox(width: 4),
                    Text(
                      time,
                      style: GoogleFonts.openSans(color: const Color(0xFF264653), fontSize: 12),
                    ),
                    const SizedBox(width: 12),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE9C46A),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Text(
                        '+$xp XP',
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 10,
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
      },
    );
  }
}

class _AcceptedTasksTab extends StatelessWidget {
  const _AcceptedTasksTab();

  @override
  Widget build(BuildContext context) {
    final List<Map<String, dynamic>> tasks = [
      {'title': 'Walk dog', 'requester': 'John', 'time': 'Today, 4:00 PM', 'status': 'Not started'},
      {'title': 'Take out bins', 'requester': 'Mary', 'time': 'Tomorrow, 7:00 AM', 'status': 'Not started'},
    ];

    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: tasks.length,
      itemBuilder: (context, index) {
        final title = tasks[index]['title'] as String;
        final requester = tasks[index]['requester'] as String;
        final time = tasks[index]['time'] as String;
        final status = tasks[index]['status'] as String;

        return GestureDetector(
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const TaskDetailScreen()),
            );
          },
          child: Container(
            margin: const EdgeInsets.only(bottom: 12),
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
                Text(
                  'Requested by: $requester',
                  style: GoogleFonts.openSans(color: const Color(0xFF264653), fontSize: 12),
                ),
                const SizedBox(height: 4),
                Row(
                  children: [
                    const Icon(Icons.access_time, size: 14, color: Color(0xFF2A9D8F)),
                    const SizedBox(width: 4),
                    Text(
                      time,
                      style: GoogleFonts.openSans(color: const Color(0xFF264653), fontSize: 12),
                    ),
                    const Spacer(),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE9C46A),
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        status,
                        style: GoogleFonts.openSans(
                          color: const Color(0xFF264653),
                          fontSize: 12,
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
      },
    );
  }
}