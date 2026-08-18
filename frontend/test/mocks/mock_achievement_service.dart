import 'package:supa_neighbour/models/achievement_model.dart';
import 'package:supa_neighbour/services/achievement_service.dart';

class MockAchievementService implements IAchievementService {
  @override
  Future<AchievementsResponse> getAchievements() async {
    // Return mock data
    return AchievementsResponse(
      earned: [
        Achievement.earned(
          badgeId: 5,
          name: 'Home Repair Specialist',
          description: 'Complete 10 home repair tasks',
          awardedOn: '2026-05-01',
        ),
        Achievement.earned(
          badgeId: 3,
          name: 'Pet Care Helper',
          description: 'Complete 5 pet care tasks',
          awardedOn: '2026-04-15',
        ),
      ],
      unearned: [
        Achievement.unearned(
          badgeId: 2,
          name: 'Plant Waterer',
          description: 'Water plants 10 times',
          progress: '3/10',
        ),
        Achievement.unearned(
          badgeId: 7,
          name: 'Package Collector',
          description: 'Collect 20 packages',
          progress: '5/20',
        ),
      ],
    );
  }
}

// Mock service that throws an error (for testing error state)
class MockAchievementServiceWithError implements IAchievementService {
  @override
  Future<AchievementsResponse> getAchievements() async {
    throw Exception('Network error');
  }
}