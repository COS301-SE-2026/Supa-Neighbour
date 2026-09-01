// admin/lib/widgets/admin_scaffold.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/constants/constants.dart';
import 'admin_sidebar.dart';

class AdminScaffold extends StatefulWidget {
  final Widget child;
  final String title;
  final int selectedIndex;
  final List<Widget>? actions;

  const AdminScaffold({
    super.key,
    required this.child,
    required this.title,
    required this.selectedIndex,
    this.actions,
  });

  @override
  State<AdminScaffold> createState() => _AdminScaffoldState();
}

class _AdminScaffoldState extends State<AdminScaffold> {
  bool _isSidebarCollapsed = false;

  void _toggleSidebar() {
    setState(() {
      _isSidebarCollapsed = !_isSidebarCollapsed;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      body: Row(
        children: [
          // Sidebar
          AdminSidebar(
            selectedIndex: widget.selectedIndex,
            isCollapsed: _isSidebarCollapsed,
            onToggle: _toggleSidebar,
          ),
          
          // Main Content
          Expanded(
            child: Column(
              children: [
                // App Bar
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    border: Border(
                      bottom: BorderSide(
                        color: AppColors.textGrey,
                        width: 0.5,
                      ),
                    ),
                  ),
                  child: Row(
                    children: [
                      // Toggle button
                      IconButton(
                        onPressed: _toggleSidebar,
                        icon: Icon(
                          _isSidebarCollapsed ? Icons.menu : Icons.menu_open,
                          color: AppColors.charcoal,
                          size: 28,
                        ),
                        tooltip: _isSidebarCollapsed ? 'Expand sidebar' : 'Collapse sidebar',
                        padding: EdgeInsets.zero,
                        constraints: const BoxConstraints(),
                      ),
                      const SizedBox(width: 12),
                      
                      // Title only
                      Text(
                        widget.title,
                        style: GoogleFonts.poppins(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                          color: AppColors.charcoal,
                        ),
                      ),
                      const Spacer(),
                      
                      // User Info
                      Row(
                        children: [
                          const CircleAvatar(
                            radius: 16,
                            backgroundColor: AppColors.primaryTeal,
                            child: Text(
                              'A',
                              style: TextStyle(
                                color: Colors.white,
                                fontSize: 14,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                          ),
                          const SizedBox(width: 8),
                          Text(
                            'Admin',
                            style: GoogleFonts.openSans(
                              fontSize: 14,
                              color: AppColors.charcoal,
                            ),
                          ),
                          IconButton(
                            icon: const Icon(Icons.logout, size: 20),
                            onPressed: () {
                              context.go('/login');
                            },
                            tooltip: 'Logout',
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                
                // Content
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.all(24),
                    child: widget.child,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}