import 'package:flutter/material.dart';
import 'app/screens/task_detail_screen.dart';

void main() {
  runApp(const SupaNeighbourApp());
}

class SupaNeighbourApp extends StatelessWidget {
  const SupaNeighbourApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Supa Neighbour',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF2A9D8F),
        ),
        useMaterial3: true,
      ),
      home: const TaskDetailScreen(),
    );
  }
}