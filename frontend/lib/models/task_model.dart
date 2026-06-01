import 'package:flutter/material.dart';

class Task {
  final String id;
  final String title;
  final String category;
  final DateTime date;
  final TimeOfDay time;
  final int xpReward;
  final String instructions;
  final String status; // pending, in_progress, completed
  final DateTime createdAt;

  Task({
    required this.id,
    required this.title,
    required this.category,
    required this.date,
    required this.time,
    required this.xpReward,
    required this.instructions,
    required this.status,
    required this.createdAt,
  });

  // Mock data storage
  static List<Task> _mockTasks = [];

  static List<Task> getMockTasks() {
    if (_mockTasks.isEmpty) {
      // Add some sample tasks
      _mockTasks = [
        Task(
          id: '1',
          title: 'Water my plants',
          category: 'Plants',
          date: DateTime.now(),
          time: const TimeOfDay(hour: 15, minute: 0),
          xpReward: 50,
          instructions: 'Please water all indoor plants',
          status: 'pending',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
        ),
        Task(
          id: '2',
          title: 'Collect package',
          category: 'Packages',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 10, minute: 0),
          xpReward: 30,
          instructions: 'Pick up from the post office',
          status: 'pending',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
        ),
      ];
    }
    return _mockTasks;
  }

  static void addMockTask(Task task) {
    _mockTasks.insert(0, task); // Add to beginning of list
  }

  static void updateTaskStatus(String taskId, String newStatus) {
    final index = _mockTasks.indexWhere((task) => task.id == taskId);
    if (index != -1) {
      _mockTasks[index] = Task(
        id: _mockTasks[index].id,
        title: _mockTasks[index].title,
        category: _mockTasks[index].category,
        date: _mockTasks[index].date,
        time: _mockTasks[index].time,
        xpReward: _mockTasks[index].xpReward,
        instructions: _mockTasks[index].instructions,
        status: newStatus,
        createdAt: _mockTasks[index].createdAt,
      );
    }
  }

  static void updateMockTask(Task updatedTask) {
    final index = _mockTasks.indexWhere((task) => task.id == updatedTask.id);
    if (index != -1) {
      _mockTasks[index] = updatedTask;
    }
  }

  static void deleteMockTask(String taskId) {
    _mockTasks.removeWhere((task) => task.id == taskId);
  }
}