import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'screens/login/admin_login_screen.dart';
import 'screens/dashboard/dashboard_screen.dart';
import 'screens/reports/reports_screen.dart';
import 'screens/reports/report_detail_screen.dart';
import 'screens/users/users_screen.dart';
import 'screens/zones/zones_screen.dart';
import 'screens/settings/settings_screen.dart';
import 'widgets/admin_scaffold.dart';


CustomTransitionPage _buildPageWithNoTransition(Widget child) {
  return CustomTransitionPage(
    child: child,
    transitionsBuilder: (context, animation, secondaryAnimation, child) => child,
    transitionDuration: Duration.zero,
    reverseTransitionDuration: Duration.zero,
  );
}

final router = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      name: 'login',
      builder: (context, state) => const AdminLoginScreen(),
    ),
    ShellRoute(
      builder: (context, state, child) {
        int selectedIndex = 0;
        final location = state.uri.path;
        if (location.startsWith('/reports')) selectedIndex = 1;
        else if (location.startsWith('/users')) selectedIndex = 2;
        else if (location.startsWith('/zones')) selectedIndex = 3;
        else if (location.startsWith('/settings')) selectedIndex = 4;
        
        return AdminScaffold(
          selectedIndex: selectedIndex,
          title: _getTitle(location),
          child: child,
        );
      },
      routes: [
        GoRoute(
          path: '/dashboard',
          name: 'dashboard',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const DashboardScreen(),
          ),
        ),
        GoRoute(
          path: '/reports',
          name: 'reports',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const ReportsScreen(),
          ),
        ),
        GoRoute(
          path: '/reports/:id',
          name: 'reportDetail',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            ReportDetailScreen(reportId: int.parse(state.pathParameters['id']!)),
          ),
        ),
        GoRoute(
          path: '/users',
          name: 'users',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const UsersScreen(),
          ),
        ),
        GoRoute(
          path: '/zones',
          name: 'zones',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const ZonesScreen(),
          ),
        ),
        GoRoute(
          path: '/settings',
          name: 'settings',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const SettingsScreen(),
          ),
        ),
      ],
    ),
  ],
);

String _getTitle(String location) {
  if (location.startsWith('/reports')) return 'Reports Management';
  if (location.startsWith('/users')) return 'User Management';
  if (location.startsWith('/zones')) return 'Neighbourhood Zones';
  if (location.startsWith('/settings')) return 'Settings';
  return 'Dashboard';
}