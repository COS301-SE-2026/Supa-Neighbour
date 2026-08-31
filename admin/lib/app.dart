import 'package:flutter/material.dart';
//import 'package:google_fonts/google_fonts.dart';
import 'routes.dart';
import 'theme/app_theme.dart';

class AdminApp extends StatelessWidget {
  const AdminApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Supa Neighbour Admin',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      routerConfig: router,
    );
  }
}