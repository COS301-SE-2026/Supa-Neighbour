class LeaderboardEntry {
  final int rank;
  final String userId;
  final String displayName;
  final double trustScore;
  final String level; // Gold, Silver, Bronze
  final int xp;
  final int completedTasks;
  final bool isCurrentUser;

  LeaderboardEntry({
    required this.rank,
    required this.userId,
    required this.displayName,
    required this.trustScore,
    required this.level,
    required this.xp,
    required this.completedTasks,
    this.isCurrentUser = false,
  });
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
}