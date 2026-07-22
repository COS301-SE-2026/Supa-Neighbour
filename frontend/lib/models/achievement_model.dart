class Achievement {
  final int badgeId;
  final String name;
  final String description;
  final String? awardedOn;
  final String? progress;

  Achievement({
    required this.badgeId,
    required this.name,
    required this.description,
    this.awardedOn,
    this.progress,
  });

  factory Achievement.earned({
    required int badgeId,
    required String name,
    required String description,
    required String awardedOn,
  }) {
    return Achievement(
      badgeId: badgeId,
      name: name,
      description: description,
      awardedOn: awardedOn,
    );
  }

  factory Achievement.unearned({
    required int badgeId,
    required String name,
    required String description,
    required String progress,
  }) {
    return Achievement(
      badgeId: badgeId,
      name: name,
      description: description,
      progress: progress,
    );
  }

  factory Achievement.fromJson(Map<String, dynamic> json) {
    return Achievement(
      badgeId: json['badgeId'] as int,
      name: json['name'] as String,
      description: json['description'] as String,
      awardedOn: json['awardedOn'] as String?,
      progress: json['progress'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'badgeId': badgeId,
      'name': name,
      'description': description,
      if (awardedOn != null) 'awardedOn': awardedOn,
      if (progress != null) 'progress': progress,
    };
  }

  bool get isEarned => awardedOn != null && awardedOn!.isNotEmpty;
  bool get isUnearned => progress != null && progress!.isNotEmpty;

  double get progressFraction {
    if (progress == null || progress!.isEmpty) return 0.0;
    final parts = progress!.split('/');
    if (parts.length != 2) return 0.0;
    final current = double.tryParse(parts[0]) ?? 0;
    final total = double.tryParse(parts[1]) ?? 1;
    return total > 0 ? current / total : 0.0;
  }

  String get formattedDate {
    if (awardedOn == null) return '';
    try {
      final date = DateTime.parse(awardedOn!);
      return _formatDate(date);
    } catch (e) {
      return awardedOn!;
    }
  }

  String _formatDate(DateTime date) {
    final months = [
      'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
      'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
    ];
    return '${months[date.month - 1]} ${date.day}, ${date.year}';
  }
}

class AchievementsResponse {
  final List<Achievement> earned;
  final List<Achievement> unearned;

  AchievementsResponse({
    required this.earned,
    required this.unearned,
  });
//can be updated
  factory AchievementsResponse.fromJson(Map<String, dynamic> json) {
    return AchievementsResponse(
      earned: (json['earned'] as List<dynamic>)
          .map((item) => Achievement.fromJson(item as Map<String, dynamic>))
          .toList(),
      unearned: (json['unearned'] as List<dynamic>)
          .map((item) => Achievement.fromJson(item as Map<String, dynamic>))
          .toList(),
    );
  }
}