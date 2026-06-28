import 'package:flutter/material.dart';

class Task {
  final String id;
  final String title;
  final String category;
  final DateTime date;
  final TimeOfDay time;
  final int xpReward;
  final String instructions;
  final String status; //open, assigned, in_progress, pending_approval, completed, cancelled
  final DateTime createdAt;
  final String createdBy;  // User ID of who created the task
  final String? helperId;  // User ID of who accepted (null if open)
  final String? requesterName; // Name of requester for display
  final String? helperName; // Name of helper for display (optional)

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
    required this.createdBy,
    this.helperId,
    this.requesterName,
    this.helperName
  });

  // Mock data storage
  static List<Task> _mockTasks = [];

  static List<Task> getMockTasks({String currentUserId = 'currentUser'}) {
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
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
          createdBy: currentUserId,
          requesterName: 'You',
          helperId: null,
          helperName: null,
        ),
        Task(
          id: '2',
          title: 'Collect package',
          category: 'Packages',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 10, minute: 0),
          xpReward: 30,
          instructions: 'Pick up from the post office',
          status: 'in_progress',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
          createdBy: currentUserId,
          requesterName: 'You',
          helperId: 'helper123',
          helperName: 'Sarah Johnson',
        ),

        Task(
        id: '3',
        title: 'Walk my dog',
        category: 'Pets',
        date: DateTime.now(),
        time: const TimeOfDay(hour: 8, minute: 0),
        xpReward: 60,
        instructions: 'Take my dog for a 15 min walk',
        status: 'completed',  // Shows in "Completed" tab
        createdAt: DateTime.now().subtract(const Duration(days: 3)),
        createdBy: currentUserId,
        requesterName: 'You',
        helperId: 'helper456',
        helperName: 'Mike Johnson',
      ),

      // Task 4: Created by current user, pending approval (helper says done)
        Task(
          id: '4',
          title: 'Take out bins',
          category: 'Bins',
          date: DateTime.now().subtract(const Duration(days: 1)),
          time: const TimeOfDay(hour: 19, minute: 0),
          xpReward: 20,
          instructions: 'Take bins to the curb',
          status: 'pending_approval',
          createdAt: DateTime.now().subtract(const Duration(days: 4)),
          createdBy: currentUserId,
          requesterName: 'You',
          helperId: 'helper789',
          helperName: 'Lisa Wong',
        ),
        
        // Task 5: Created by current user, completed
        Task(
          id: '5',
          title: 'Check my mail',
          category: 'Packages',
          date: DateTime.now().subtract(const Duration(days: 2)),
          time: const TimeOfDay(hour: 9, minute: 0),
          xpReward: 15,
          instructions: 'Bring mail inside',
          status: 'completed',
          createdAt: DateTime.now().subtract(const Duration(days: 5)),
          createdBy: currentUserId,
          requesterName: 'You',
          helperId: 'helper111',
          helperName: 'Tom Brown',
        ),
        
        // Task 6: Created by neighbour, current user is helper (accepted)
        Task(
          id: '6',
          title: 'Walk my dog',
          category: 'Pets',
          date: DateTime.now(),
          time: const TimeOfDay(hour: 17, minute: 0),
          xpReward: 60,
          instructions: 'Take my dog for a 15 minute walk',
          status: 'assigned',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
          createdBy: 'neighbour123',
          requesterName: 'Sarah Johnson',
          helperId: currentUserId,
          helperName: 'You',
        ),
        
        // Task 7: Created by neighbour, current user is helper (in progress)
        Task(
          id: '7',
          title: 'Water garden',
          category: 'Plants',
          date: DateTime.now(),
          time: const TimeOfDay(hour: 8, minute: 0),
          xpReward: 45,
          instructions: 'Water the vegetable garden',
          status: 'in_progress',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
          createdBy: 'neighbour456',
          requesterName: 'Mike Johnson',
          helperId: currentUserId,
          helperName: 'You',
        ),
        
        // Task 8: Created by neighbour, current user is helper (pending approval)
        Task(
          id: '8',
          title: 'Feed my cat',
          category: 'Pets',
          date: DateTime.now().subtract(const Duration(days: 1)),
          time: const TimeOfDay(hour: 12, minute: 0),
          xpReward: 35,
          instructions: 'Feed the cat and change water',
          status: 'pending_approval',
          createdAt: DateTime.now().subtract(const Duration(days: 3)),
          createdBy: 'neighbour789',
          requesterName: 'Lisa Wong',
          helperId: currentUserId,
          helperName: 'You',
        ),
        
        // Task 9: Created by neighbour, current user is helper (completed)
        Task(
          id: '9',
          title: 'Collect packages',
          category: 'Packages',
          date: DateTime.now().subtract(const Duration(days: 3)),
          time: const TimeOfDay(hour: 14, minute: 0),
          xpReward: 40,
          instructions: 'Pick up packages from front door',
          status: 'completed',
          createdAt: DateTime.now().subtract(const Duration(days: 4)),
          createdBy: 'neighbour111',
          requesterName: 'Tom Brown',
          helperId: currentUserId,
          helperName: 'You',
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
        createdBy: _mockTasks[index].createdBy,
        requesterName: _mockTasks[index].requesterName,
        helperId: _mockTasks[index].helperId,
        helperName: _mockTasks[index].helperName,
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