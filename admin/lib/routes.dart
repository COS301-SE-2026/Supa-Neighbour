// admin/lib/routes.dart

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'screens/login/admin_login_screen.dart';
import 'screens/dashboard/dashboard_screen.dart';
import 'screens/reports/reports_screen.dart';
import 'screens/reports/report_detail_screen.dart';
import 'widgets/admin_scaffold.dart';
import 'screens/applications/applications_screen.dart';
import 'screens/applications/application_detail_screen.dart';
import 'screens/endorsement/zone_graph_screen.dart';

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
        if (location.startsWith('/reports')) {
          selectedIndex = 1;
        } else if (location.startsWith('/trust-graph')) {
          selectedIndex = 2;
        } else if (location.startsWith('/applications')) {
          selectedIndex = 3;
        }
        
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
          path: '/trust-graph',
          name: 'trustGraph',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const ZoneGraphScreen(),
          ),
        ),
        GoRoute(
          path: '/applications',
          name: 'applications',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            const ApplicationsScreen(),
          ),
        ),
        GoRoute(
          path: '/applications/:id',
          name: 'applicationDetail',
          pageBuilder: (context, state) => _buildPageWithNoTransition(
            ApplicationDetailScreen(
              applicationId: int.parse(state.pathParameters['id']!),
            ),
          ),
        ),
      ],
    ),
  ],
);

String _getTitle(String location) {
  if (location.startsWith('/reports')) return 'Reports Management';
  if (location.startsWith('/trust-graph')) return 'Trust Graph';
  if (location.startsWith('/applications')) return 'Admin Applications';
  return 'Dashboard';
}