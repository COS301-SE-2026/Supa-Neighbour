// lib/routes.dart

import 'package:go_router/go_router.dart';
import 'screens/login/admin_login_screen.dart';
import 'screens/dashboard/dashboard_screen.dart';

final router = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      name: 'login',
      builder: (context, state) => const AdminLoginScreen(),
    ),
    GoRoute(
      path: '/dashboard',
      name: 'dashboard',
      builder: (context, state) => const DashboardScreen(),
    ),
  ],
);