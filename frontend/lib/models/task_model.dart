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
  final String? completionNote;
  final List<String>? completionPhotos;

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
    this.helperName,
    this.completionNote,
    this.completionPhotos,
  });

  //note:
  // _meansPrivate
  // w/o underscore public

  //Creates a new Task with updated fields
  Task copyWith({
    String? id,
    String? title,
    String? category,
    DateTime? date,
    TimeOfDay? time,
    int? xpReward,
    String? instructions,
    String? status,
    DateTime? createdAt,
    String? createdBy,
    String? helperId,
    String? requesterName,
    String? helperName,
    String? completionNote,
    List<String>? completionPhotos,
  }) {
    return Task(
      id: id ?? this.id,
      title: title ?? this.title,
      category: category ?? this.category,
      date: date ?? this.date,
      time: time ?? this.time,
      xpReward: xpReward ?? this.xpReward,
      instructions: instructions ?? this.instructions,
      status: status ?? this.status,
      createdAt: createdAt ?? this.createdAt,
      createdBy: createdBy ?? this.createdBy,
      helperId: helperId ?? this.helperId,
      requesterName: requesterName ?? this.requesterName,
      helperName: helperName ?? this.helperName,
      completionNote: completionNote ?? this.completionNote,
      completionPhotos: completionPhotos ?? this.completionPhotos,
    );
  }

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
      createdBy: json['createdBy'] as String? ?? 'unknown',  
      requesterName: json['requesterName'] as String?,       
      helperId: json['helperId'] as String?,                 
      helperName: json['helperName'] as String?,
      completionNote: json['completionNote'] as String?,
      completionPhotos: json['completionPhotos'] != null
      ? List<String>.from(json['completionPhotos'] as List)
      : null,             
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

  /// categoryName -> taskTypeId
  static int resolveTaskTypeId(String category){
    switch (category) {
      case 'Plants':
        return 1;
      case 'Pets':
        return 2;
      case 'Bins':
      return 3;
      case 'Packages':
        return 4;
      case 'Home Check-in':
        return 5;
      case 'Pool Pump':
        return 6;
      default:
        return 7;

    }
  }





////////////////////TEMP MCL DATA\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
/////////// wil remove as integration is being complete essentially
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
          completionNote: null,
          completionPhotos: null,
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
          completionNote: null,
          completionPhotos: null,
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
        completionNote: null,
        completionPhotos: null,
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
          completionNote: null,
          completionPhotos: null,
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
          completionNote: null,
          completionPhotos: null,
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
          completionNote: null,
          completionPhotos: null,
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

        // Task 10: Created by current user, pending approval with completion details
        Task(
          id: '10',
          title: 'Fix the garden fence',
          category: 'Home Check-in',
          date: DateTime.now().subtract(const Duration(days: 2)),
          time: const TimeOfDay(hour: 10, minute: 0),
          xpReward: 80,
          instructions: 'Fix the broken fence panel in the backyard',
          status: 'pending_approval',
          createdAt: DateTime.now().subtract(const Duration(days: 3)),
          createdBy: currentUserId,
          requesterName: 'You',
          helperId: 'helper999',
          helperName: 'John Carpenter',
          completionNote: 'Fixed the fence panel. Replaced two broken slats and secured the hinges. Good as new!',
          completionPhotos: [
            'https://via.placeholder.com/150/2A9D8F/FFFFFF?text=Fence+After',
            'https://via.placeholder.com/150/E9C46A/264653?text=Repair+Detail',
          ],
        ),

                // Task 11: Available task from other neighbour (open)
        Task(
          id: '11',
          title: 'Water the garden',
          category: 'Plants',
          date: DateTime.now().add(const Duration(days: 2)),
          time: const TimeOfDay(hour: 9, minute: 0),
          xpReward: 40,
          instructions: 'Water the vegetable garden and flower beds',
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
          createdBy: 'neighbour555',
          requesterName: 'Alice Green',
          helperId: null,
          helperName: null,
        ),
        // Task 12: Available task from other neighbour (open)
        Task(
          id: '12',
          title: 'Collect my parcel',
          category: 'Packages',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 14, minute: 0),
          xpReward: 25,
          instructions: 'Pick up a parcel from the post office',
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
          createdBy: 'neighbour666',
          requesterName: 'Bob Parcel',
          helperId: null,
          helperName: null,
        ),
        // Task 13: Available task from otherneighbour (open)
        Task(
          id: '13',
          title: 'Feed my cat',
          category: 'Pets',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 18, minute: 0),
          xpReward: 50,
          instructions: 'Feed the cat and clean the litter box',
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
          createdBy: 'neighbour777',
          requesterName: 'Carol Petlover',
          helperId: null,
          helperName: null,
        ),
        // Task 14: Available task from other neighbour (open)
        Task(
          id: '14',
          title: 'Take out the bins',
          category: 'Bins',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 7, minute: 0),
          xpReward: 15,
          instructions: 'Take the bins to the curb for collection',
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 3)),
          createdBy: 'neighbour888',
          requesterName: 'Dave Bins',
          helperId: null,
          helperName: null,
        ),
        // Task 15: Available task from other neighbour (open)
        Task(
          id: '15',
          title: 'Check my mail',
          category: 'Home Check-in',
          date: DateTime.now().add(const Duration(days: 1)),
          time: const TimeOfDay(hour: 11, minute: 0),
          xpReward: 20,
          instructions: 'Bring the mail inside the house',
          status: 'open',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
          createdBy: 'neighbour999',
          requesterName: 'Eve Mail',
          helperId: null,
          helperName: null,
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
        createdBy: _mockTasks[index].createdBy,
        requesterName: _mockTasks[index].requesterName,
        helperId: _mockTasks[index].helperId,
        helperName: _mockTasks[index].helperName,
        completionNote: _mockTasks[index].completionNote,
        completionPhotos: _mockTasks[index].completionPhotos,
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