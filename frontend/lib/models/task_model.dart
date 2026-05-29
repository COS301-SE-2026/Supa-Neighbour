import 'package:flutter/material.dart';

class Task {
  final String id;
  final String title;
  final String category;
  final DateTime date;
  final TimeOfDay time;
  final int xpReward;
  final String instructions;
  final String status; // pending, in_progress, completed /// remind divo to update table
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

  ////////////////////////
  /// MAP RES TO A TASK
  factory Task.fromJson(Map<String, dynamic> json){

    final DateTime startDate = json['startDate'] != null ? DateTime.parse(json['startDate'] as String): DateTime.now();

    return Task(
      id: (json['taskId'] as int).toString(),
      title: _resolveCategoryName(json['taskTypeId'] as int?),
      category: _resolveCategoryName(json['taskTypeId'] as int?),
      date: startDate,
      time: TimeOfDay(hour: startDate.hour, minute: startDate.minute),
      xpReward: 0 ,
      instructions: json['adminReview'] as String? ?? 'No instructions provided',
      status: json['helperId'] != null ? 'in_progress' : 'pending',
      createdAt: startDate,
    );
  }

  /// taskTypeId -> categoryName
  static String _resolveCategoryName(int? taskTypeId){
    switch(taskTypeId){
      case 1:
        return 'Plants';
      case 2:
        return 'Pets';
         case 3:
        return 'Bins';
      case 4:
        return 'Packages';
      case 5:
        return 'Home Check-in';
      case 6:
        return 'Pool Pump';
      default:
        return 'Other';
    }
  }





////////////////////TEMP MCL DATA\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
/////////// wil remove as integration is being complete essentially
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
    _mockTasks.insert(0, task); 
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
  /////////////////////////////////////////////
  /////////////////////////////////////////////
}