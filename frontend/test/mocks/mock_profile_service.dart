import 'package:supa_neighbour/models/user_profile_response.dart';
import 'package:supa_neighbour/models/update_profile_response.dart';
import 'package:supa_neighbour/services/profile_service.dart';

class MockProfileService implements IProfileService {
  @override
  Future<UserProfileResponse> getMyProfile() async {
    return UserProfileResponse(
      userId: 1,
      displayName: 'John Doe',
      neighbourhood: 'Greenfield',
      level: 'Gold',
      currentXp: 4500,
      trustScore: 4.8,
      skills: ['Home Repair', 'Pet Care'],
      achievements: [
        AchievementDTO(
          badgeId: 1,
          badgeName: 'Home Repair Specialist',
          badgeDescription: 'Complete 10 home repair tasks',
          awardedOn: '2026-05-01',
        ),
        AchievementDTO(
          badgeId: 2,
          badgeName: 'Pet Care Helper',
          badgeDescription: 'Complete 5 pet care tasks',
          awardedOn: '2026-04-15',
        ),
      ],
      recentTasks: [
        RecentTaskDTO(
          taskId: 1,
          typeDescription: 'Fixed sink',
          endDate: '2026-05-01',
          xpWorth: 100,
        ),
      ],
      completedTasks: 27,
      activeTasks: 3,
      createdTasks: 15,
    );
  }

  @override
  Future<UpdateProfileResponse> updateSkills(List<String> skills) async {
    return UpdateProfileResponse(
      message: 'Profile updated',
      displayName: 'John Doe',
      skills: skills,
    );
  }
}

class MockProfileServiceError implements IProfileService {
  @override
  Future<UserProfileResponse> getMyProfile() async {
    throw Exception('Failed to load profile');
  }

  @override
  Future<UpdateProfileResponse> updateSkills(List<String> skills) async {
    throw Exception('Failed to update skills');
  }
}

class MockProfileServiceEmpty implements IProfileService {
  @override
  Future<UserProfileResponse> getMyProfile() async {
    return UserProfileResponse(
      userId: 1,
      displayName: 'John Doe',
      neighbourhood: 'Greenfield',
      level: 'Bronze',
      currentXp: 100,
      trustScore: 3.0,
      skills: [],
      achievements: [],
      recentTasks: [],
      completedTasks: 0,
      activeTasks: 0,
      createdTasks: 0,
    );
  }

  @override
  Future<UpdateProfileResponse> updateSkills(List<String> skills) async {
    return UpdateProfileResponse(
      message: 'Profile updated',
      displayName: 'John Doe',
      skills: skills,
    );
  }
}

class MockProfileServiceSilver implements IProfileService {
  @override
  Future<UserProfileResponse> getMyProfile() async {
    return UserProfileResponse(
      userId: 1,
      displayName: 'Jane Smith',
      neighbourhood: 'Riverside',
      level: 'Silver',
      currentXp: 2500,
      trustScore: 4.5,
      skills: ['Medical Assistance', 'Pet Care'],
      achievements: [
        AchievementDTO(
          badgeId: 3,
          badgeName: 'Medical Specialist',
          badgeDescription: 'Complete 5 medical assistance tasks',
          awardedOn: '2026-06-01',
        ),
      ],
      recentTasks: [
        RecentTaskDTO(
          taskId: 2,
          typeDescription: 'Medical check-in',
          endDate: '2026-06-01',
          xpWorth: 50,
        ),
      ],
      completedTasks: 12,
      activeTasks: 2,
      createdTasks: 8,
    );
  }

  @override
  Future<UpdateProfileResponse> updateSkills(List<String> skills) async {
    return UpdateProfileResponse(
      message: 'Profile updated',
      displayName: 'Jane Smith',
      skills: skills,
    );
  }
}

class MockProfileServiceBronze implements IProfileService {
  @override
  Future<UserProfileResponse> getMyProfile() async {
    return UserProfileResponse(
      userId: 1,
      displayName: 'Bob Johnson',
      neighbourhood: 'Oakwood',
      level: 'Bronze',
      currentXp: 500,
      trustScore: 3.8,
      skills: ['Technology Support'],
      achievements: [],
      recentTasks: [
        RecentTaskDTO(
          taskId: 3,
          typeDescription: 'Technology setup',
          endDate: '2026-05-15',
          xpWorth: 30,
        ),
      ],
      completedTasks: 3,
      activeTasks: 1,
      createdTasks: 2,
    );
  }

  @override
  Future<UpdateProfileResponse> updateSkills(List<String> skills) async {
    return UpdateProfileResponse(
      message: 'Profile updated',
      displayName: 'Bob Johnson',
      skills: skills,
    );
  }
}
