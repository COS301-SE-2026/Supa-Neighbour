class AchievementDTO{
  final int badgeId;
  final String badgeName;
  final String badgeDescription;
  final String? awardedOn;

  AchievementDTO({
    required this.badgeId,
    required this.badgeName,
    required this.badgeDescription,
    this.awardedOn,
  });

  factory AchievementDTO.fromJson(Map<String, dynamic> json){
    return AchievementDTO(
      badgeId: json['badgeId'] as int,
      badgeName: json['name'] as String,
      badgeDescription: json['description'] as String,
      awardedOn: json['awardedOn'] as String?,
    );
  }
}

class RecentTaskDTO{
    final int taskId;
    final String typeDescription;
    final String? endDate;
    final int? xpWorth;

    RecentTaskDTO({
      required this.taskId,
      required this.typeDescription,
      this.endDate,
      this.xpWorth,
    });

    factory RecentTaskDTO.fromJson(Map<String, dynamic> json){
      return RecentTaskDTO(
        taskId: json['taskId'] as int,
        typeDescription: json['type'] as String,
        endDate: json['date'] as String?,
        xpWorth: json['xpWorth'] as int?,
      );
    }
  }

  class UserProfileResponse{
    final int userId;
    final String displayName;
    final String neighbourhood;
    final String? level;
    final int? currentXp;
    final double? trustScore;
    final List<String> skills;
    final List<AchievementDTO> achievements;
    final int completedTasks;
    final List<RecentTaskDTO> recentTasks;
    final int activeTasks;
    final int createdTasks;

    UserProfileResponse({
      required this.userId,
      required this.displayName,
      required this.neighbourhood, 
      this.level, 
      this.currentXp,
      this.trustScore, 
      required this.skills,
      required this.achievements,
      required this.completedTasks, 
      required this.recentTasks,
      required this.activeTasks,
      required this.createdTasks,
    });

    factory UserProfileResponse.fromJson(Map<String, dynamic> json){
      return UserProfileResponse(
        userId: json['userId'] as int, 
        displayName: json['displayName'] as String,
        neighbourhood: json['neighbourhood'] as String,
        level: json['level'] as String?,
        currentXp: json['currentXp'] as int?,
        trustScore: (json['trustScore'] as num?)?.toDouble(),
        skills: (json['skills'] as List<dynamic>?)?.map((e) => e as String).toList() ?? [],
        achievements: (json['achievements'] as List<dynamic>?)?.map((e) => AchievementDTO.fromJson(e as Map<String, dynamic>)).toList() ?? [],
        completedTasks: json['completedTasks'] as int? ?? 0,
        recentTasks: (json['recentTasks'] as List<dynamic>?)?.map((e) => RecentTaskDTO.fromJson(e as Map<String, dynamic>)).toList() ?? [],
        activeTasks: json['activeTasks'] as int? ?? 0,
        createdTasks: json['createdTasks'] as int ? ?? 0,
      );
    }
  }