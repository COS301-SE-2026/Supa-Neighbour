import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/task_model.dart';

void main() {
  group('Task Model Unit Tests', () {
    late Task testTask;
    final now = DateTime.now();

    setUp(() {
      testTask = Task(
        id: '1',
        title: 'Water plants',
        category: 'Plants',
        date: now,
        time: const TimeOfDay(hour: 15, minute: 0),
        xpReward: 50,
        instructions: 'Please water all indoor plants',
        status: 'open',
        createdAt: now.subtract(const Duration(days: 1)),
        createdBy: 'user_1',
        requesterName: 'Test User',
        helperId: null,
        helperName: null,
        completionNote: null,
        completionPhotos: null,
      );
    });

    group('Properties and Getters', () {
      test('should have correct initial values', () {
        expect(testTask.id, '1');
        expect(testTask.title, 'Water plants');
        expect(testTask.category, 'Plants');
        expect(testTask.xpReward, 50);
        expect(testTask.status, 'open');
        expect(testTask.createdBy, 'user_1');
        expect(testTask.requesterName, 'Test User');
        expect(testTask.helperId, null);
        expect(testTask.helperName, null);
        expect(testTask.completionNote, null);
        expect(testTask.completionPhotos, null);
      });

      test('should return correct time as string when time is set', () {
        final taskWithTime = Task(
          id: '2',
          title: 'Test',
          category: 'Plants',
          date: now,
          time: const TimeOfDay(hour: 14, minute: 30),
          xpReward: 10,
          instructions: '',
          status: 'open',
          createdAt: now,
          createdBy: 'user_1',
        );
        expect(taskWithTime.time.toString(), 'TimeOfDay(14:30)');
        expect(taskWithTime.time.hour, 14);
        expect(taskWithTime.time.minute, 30);
      });
    });

    group('copyWith', () {
      test('should create new task with updated status', () {
        final updatedTask = testTask.copyWith(status: 'in_progress');

        expect(updatedTask.id, testTask.id);
        expect(updatedTask.title, testTask.title);
        expect(updatedTask.status, 'in_progress');
        expect(updatedTask.xpReward, testTask.xpReward);
      });

      test('should create new task with helper details', () {
        final updatedTask = testTask.copyWith(
          helperId: 'helper_1',
          helperName: 'Sarah Johnson',
          status: 'assigned',
        );

        expect(updatedTask.helperId, 'helper_1');
        expect(updatedTask.helperName, 'Sarah Johnson');
        expect(updatedTask.status, 'assigned');
      });

      test('should create new task with completion details', () {
        final updatedTask = testTask.copyWith(
          status: 'pending_approval',
          completionNote: 'All plants watered!',
          completionPhotos: ['photo1.jpg', 'photo2.jpg'],
        );

        expect(updatedTask.status, 'pending_approval');
        expect(updatedTask.completionNote, 'All plants watered!');
        expect(updatedTask.completionPhotos, ['photo1.jpg', 'photo2.jpg']);
      });

      test('should preserve original values when no updates provided', () {
        final updatedTask = testTask.copyWith();

        expect(updatedTask.id, testTask.id);
        expect(updatedTask.title, testTask.title);
        expect(updatedTask.category, testTask.category);
        expect(updatedTask.date, testTask.date);
        expect(updatedTask.time, testTask.time);
        expect(updatedTask.xpReward, testTask.xpReward);
        expect(updatedTask.instructions, testTask.instructions);
        expect(updatedTask.status, testTask.status);
      });
    });

    group('fromJson', () {
      test('should create Task from valid JSON', () {
        final json = {
          'taskId': 1,
          'taskTypeId': 1,
          'startDate': now.toIso8601String(),
          'instructions': 'Water all plants twice',
          'requesterUserId': 'user_123',
          'status': 'open',
          'helperId': null,
          'requesterName': 'John Doe',
          'helperName': null,
          'completionNote': null,
          'completionPhotos': null,
        };

        final task = Task.fromJson(json);

        expect(task.id, '1');
        expect(task.title, 'Medical Assistance');
        expect(task.category, 'Medical Assistance');
        expect(task.xpReward, 50);
        expect(task.instructions, 'Water all plants twice');
        expect(task.createdBy, 'user_123');
        expect(task.status, 'open');
        expect(task.requesterName, 'John Doe');
        expect(task.helperId, null);
      });

      test('should handle null values gracefully', () {
        final json = {
          'taskId': 2,
          'startDate': null,
          'instructions': null,
          'dependentId': null,
        };

        final task = Task.fromJson(json);

        expect(task.id, '2');
        expect(task.title, 'Other');
        expect(task.instructions, '');
        expect(task.createdBy, 'unknown');
        expect(task.date, isNotNull);
      });

      test('should resolve all category names correctly', () {
        final json1 = {'taskId': 3, 'taskTypeId': 1, 'startDate': now.toIso8601String()};
        final json2 = {'taskId': 4, 'taskTypeId': 2, 'startDate': now.toIso8601String()};
        final json3 = {'taskId': 5, 'taskTypeId': 3, 'startDate': now.toIso8601String()};
        final json4 = {'taskId': 6, 'taskTypeId': 4, 'startDate': now.toIso8601String()};
        final json5 = {'taskId': 7, 'taskTypeId': 5, 'startDate': now.toIso8601String()};

        final task1 = Task.fromJson(json1);
        final task2 = Task.fromJson(json2);
        final task3 = Task.fromJson(json3);
        final task4 = Task.fromJson(json4);
        final task5 = Task.fromJson(json5);

        expect(task1.category, 'Medical Assistance');
        expect(task2.category, 'Pet Care');
        expect(task3.category, 'Technology Support');
        expect(task4.category, 'Transportation Support');
        expect(task5.category, 'Home Repair');
      });

      // TODO: Consult team about adding these categories back -mich
      // The following categories exist in mock data but not in _resolveCategoryName
      // 'Plants', 'Pets', 'Bins', 'Packages', 'Home Check-in', 'Pool Pump'
      test('should resolve old category names if added back', skip: true, () {
        // This test is skipped until we decide on the final category list
        // When categories are added back, remove skip: true and update expectations
        final json = {'taskId': 8, 'taskTypeId': 6, 'startDate': now.toIso8601String()};
        final task = Task.fromJson(json);
        expect(task.category, 'Pool Pump');
      });

      test('should resolve XP rewards correctly', () {
        final json1 = {'taskId': 6, 'taskTypeId': 1, 'startDate': now.toIso8601String()};
        final json2 = {'taskId': 7, 'taskTypeId': 2, 'startDate': now.toIso8601String()};
        final json3 = {'taskId': 8, 'taskTypeId': 5, 'startDate': now.toIso8601String()};

        final task1 = Task.fromJson(json1);
        final task2 = Task.fromJson(json2);
        final task3 = Task.fromJson(json3);

        expect(task1.xpReward, 50);
        expect(task2.xpReward, 60);
        expect(task3.xpReward, 45);
      });

      test('should use default XP for unknown task type', () {
        final json = {
          'taskId': 9,
          'taskTypeId': 999,
          'startDate': now.toIso8601String(),
        };

        final task = Task.fromJson(json);
        expect(task.xpReward, 25);
      });
    });

    group('fromHelperTaskJson', () {
      test('should create Task from helper task JSON', () {
        final json = {
          'taskId': 10,
          'taskType': 'Plants',
          'startDate': now.toIso8601String(),
          'xpAwarded': 50,
          'status': 'in_progress',
        };

        final task = Task.fromHelperTaskJson(json);

        expect(task.id, '10');
        expect(task.title, 'Plants');
        expect(task.category, 'Plants');
        expect(task.xpReward, 50);
        expect(task.status, 'in_progress');
      });

      test('should handle null values in helper task JSON', () {
        final json = {
          'taskId': 11,
          'startDate': now.toIso8601String(),
        };

        final task = Task.fromHelperTaskJson(json);

        expect(task.id, '11');
        expect(task.title, 'Untitled Task');
        expect(task.category, 'General');
        expect(task.xpReward, 0);
        expect(task.status, 'open');
        expect(task.date, isNotNull);
      });
    });

    group('resolveTaskTypeId', () {
      test('should resolve category to correct task type ID', () {
        expect(Task.resolveTaskTypeId('Medical Assistance'), 1);
        expect(Task.resolveTaskTypeId('Pet Care'), 2);
        expect(Task.resolveTaskTypeId('Technology Support'), 3);
        expect(Task.resolveTaskTypeId('Transportation Support'), 4);
        expect(Task.resolveTaskTypeId('Home Repair'), 5);
        expect(Task.resolveTaskTypeId('Unknown'), 1);
      });

      // TODO: Consult team about adding these categories back
      test('should resolve old categories to their IDs if added back', skip: true, () {
        // This test is skipped until the team decides on the final category list
        // When categories are added back, remove skip: true and update expectations
        expect(Task.resolveTaskTypeId('Plants'), 1);
        expect(Task.resolveTaskTypeId('Pets'), 2);
        expect(Task.resolveTaskTypeId('Packages'), 4);
        expect(Task.resolveTaskTypeId('Home Check-in'), 5);
        expect(Task.resolveTaskTypeId('Pool Pump'), 6);
      });
    });

    group('Mock Data', () {
      test('getMockTasks should return list of tasks', () {
        final tasks = Task.getMockTasks();

        expect(tasks, isNotEmpty);
        expect(tasks.length, greaterThan(5));
      });

      test('getMockTasks should include tasks with different statuses', () {
        final tasks = Task.getMockTasks();

        final statuses = tasks.map((t) => t.status).toSet();
        expect(statuses.contains('open'), true);
        expect(statuses.contains('in_progress'), true);
        expect(statuses.contains('pending_approval'), true);
        expect(statuses.contains('completed'), true);
        expect(statuses.contains('assigned'), true);
      });

      test('addMockTask should insert task at beginning', () {
        final initialCount = Task.getMockTasks().length;

        final newTask = Task(
          id: 'new',
          title: 'New Task',
          category: 'Plants',
          date: DateTime.now(),
          time: const TimeOfDay(hour: 9, minute: 0),
          xpReward: 10,
          instructions: '',
          status: 'open',
          createdAt: DateTime.now(),
          createdBy: 'user_1',
        );

        Task.addMockTask(newTask);
        final updatedTasks = Task.getMockTasks();

        expect(updatedTasks.length, initialCount + 1);
        expect(updatedTasks.first.id, 'new');
      });

      test('updateTaskStatus should update task status', () {
        Task.getMockTasks(); // Populate mock data
        final taskId = Task.getMockTasks().first.id;

        Task.updateTaskStatus(taskId, 'completed');
        final updated = Task.getMockTasks().firstWhere((t) => t.id == taskId);

        expect(updated.status, 'completed');
      });

      test('deleteMockTask should remove task', () {
        Task.getMockTasks(); // Populate mock data
        final initialCount = Task.getMockTasks().length;
        final taskId = Task.getMockTasks().first.id;

        Task.deleteMockTask(taskId);

        expect(Task.getMockTasks().length, initialCount - 1);
        expect(Task.getMockTasks().any((t) => t.id == taskId), false);
      });
      test('should map completionNote from helperRatingId', () {
        final json = {
          'taskId': 12,
          'startDate': now.toIso8601String(),
          'helperRatingId': 'Fixed the fence, all good!',
        };

        final task = Task.fromJson(json);

        expect(task.completionNote, 'Fixed the fence, all good!');
      });
    });
  });
}