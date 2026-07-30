class LeaderboardEntry {
  final int rank;
  final int userId;
  final int helperId;
  final String displayName;
  final double trustScore;
  final String level; // Gold, Silver, Bronze
  final int xp;
  final int completedTasks;
  final bool isCurrentUser;

  LeaderboardEntry({
    required this.rank,
    required this.userId,
    required this.helperId,
    required this.displayName,
    required this.trustScore,
    required this.level,
    required this.xp,
    required this.completedTasks,
    this.isCurrentUser = false,
  });

  factory LeaderboardEntry.fromJson(Map<String, dynamic> json) {
    return LeaderboardEntry(
      rank: json['rank'] as int? ?? 0,
      userId: json['userId'] as int? ?? 0,
      helperId: json['helperId'] as int? ?? 0,
      displayName: json['displayName'] as String? ?? '',
      trustScore: (json['trustScore'] as num?)?.toDouble() ?? 0.0,
      level: json['level'] as String? ?? 'Rising',
      xp: json['xp'] as int? ?? 0,
      completedTasks: json['completedTasks'] as int? ?? 0,
      isCurrentUser: json['isCurrentUser'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'rank': rank,
      'userId': userId,
      'helperId': helperId,
      'displayName': displayName,
      'trustScore': trustScore,
      'level': level,
      'xp': xp,
      'completedTasks': completedTasks,
      'isCurrentUser': isCurrentUser,
    };
  }
}

class LeaderboardData {
  final String period; // 'week', 'month', 'all'
  final List<LeaderboardEntry> entries;
  final LeaderboardEntry? currentUserEntry;

  LeaderboardData({
    required this.period,
    required this.entries,
    this.currentUserEntry,
  });

  factory LeaderboardData.fromJson(Map<String, dynamic> json) {
    final entriesList = (json['entries'] as List?)
            ?.map((e) => LeaderboardEntry.fromJson(e as Map<String, dynamic>))
            .toList() ??
        [];

    final currentUser = json['currentUserEntry'] != null
        ? LeaderboardEntry.fromJson(
            json['currentUserEntry'] as Map<String, dynamic>)
        : null;

    return LeaderboardData(
      period: json['period'] as String? ?? 'week',
      entries: entriesList,
      currentUserEntry: currentUser,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'period': period,
      'entries': entries.map((e) => e.toJson()).toList(),
      'currentUserEntry': currentUserEntry?.toJson(),
    };
  }
}

class LeaderboardResponse {
  final String neighbourhood;
  final String rankBy;
  final List<LeaderboardEntryResponse> leaderboard;
  final LeaderboardEntryResponse? currentUser;

  LeaderboardResponse({
    required this.neighbourhood,
    required this.rankBy,
    required this.leaderboard,
    this.currentUser,
  });

  factory LeaderboardResponse.fromJson(Map<String, dynamic> json) {
    final leaderboardList = (json['leaderboard'] as List?)
            ?.map((e) => LeaderboardEntryResponse.fromJson(e as Map<String, dynamic>))
            .toList() ??
        [];

    final currentUser = json['currentUser'] != null
        ? LeaderboardEntryResponse.fromJson(
            json['currentUser'] as Map<String, dynamic>)
        : null;

    return LeaderboardResponse(
      neighbourhood: json['neighbourhood'] as String? ?? '',
      rankBy: json['rankBy'] as String? ?? 'averageRating',
      leaderboard: leaderboardList,
      currentUser: currentUser,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'neighbourhood': neighbourhood,
      'rankBy': rankBy,
      'leaderboard': leaderboard.map((e) => e.toJson()).toList(),
      'currentUser': currentUser?.toJson(),
    };
  }

  LeaderboardData toLeaderboardData({String period = 'week'}) {
    final entries = leaderboard.map((entry) {
      return LeaderboardEntry(
        rank: entry.rank,
        userId: entry.userId,
        helperId: entry.helperId,
        displayName: entry.displayName,
        trustScore: entry.score,
        level: entry.level ?? _getLevelFromScore(entry.score),
        xp: (entry.score * 100).toInt(),
        completedTasks: _getCompletedTasksFromScore(entry.score),
        isCurrentUser: currentUser != null && currentUser!.userId == entry.userId,
      );
    }).toList();

    LeaderboardEntry? currentUserEntry;
    if (currentUser != null) {
      currentUserEntry = LeaderboardEntry(
        rank: currentUser!.rank,
        userId: currentUser!.userId,
        helperId: currentUser!.helperId,
        displayName: currentUser!.displayName,
        trustScore: currentUser!.score,
        level: currentUser!.level ?? _getLevelFromScore(currentUser!.score),
        xp: (currentUser!.score * 100).toInt(),
        completedTasks: _getCompletedTasksFromScore(currentUser!.score),
        isCurrentUser: true,
      );
    }

    return LeaderboardData(
      period: period,
      entries: entries,
      currentUserEntry: currentUserEntry,
    );
  }

  String _getLevelFromScore(double score) {
    if (score >= 4.8) return 'Gold';
    if (score >= 4.5) return 'Silver';
    if (score >= 4.0) return 'Bronze';
    return 'Rising';
  }

  int _getCompletedTasksFromScore(double score) {
    return (score * 10).toInt();
  }
}

class LeaderboardEntryResponse {
  final int rank;
  final int userId;
  final int helperId;
  final String displayName;
  final double score;
  final String? level;

  LeaderboardEntryResponse({
    required this.rank,
    required this.userId,
    required this.helperId,
    required this.displayName,
    required this.score,
    this.level,
  });

  factory LeaderboardEntryResponse.fromJson(Map<String, dynamic> json) {
    return LeaderboardEntryResponse(
      rank: json['rank'] as int? ?? 0,
      userId: json['userId'] as int? ?? 0,
      helperId: json['helperId'] as int? ?? 0,
      displayName: json['displayName'] as String? ?? '',
      score: (json['score'] as num?)?.toDouble() ?? 0.0,
      level: json['level'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'rank': rank,
      'userId': userId,
      'helperId': helperId,
      'displayName': displayName,
      'score': score,
      'level': level,
    };
  }
}