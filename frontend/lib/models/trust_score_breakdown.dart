class TrustScoreBreakdown {
  final double completionRate;
  final double ratingReliability;
  final double communityStanding;
  final double recency;
  final double zoneActivity;
  final double longevity;
  final double overall;

  const TrustScoreBreakdown({
    required this.completionRate,
    required this.ratingReliability,
    required this.communityStanding,
    required this.recency,
    required this.zoneActivity,
    required this.longevity,
    required this.overall,
  });

  factory TrustScoreBreakdown.fromJson(Map<String, dynamic> json) {
    double parse(dynamic v) => (v as num?)?.toDouble() ?? 0.0;
    return TrustScoreBreakdown(
      completionRate: parse(json['completionRate']),
      ratingReliability: parse(json['ratingReliability']),
      communityStanding: parse(json['communityStanding']),
      recency: parse(json['recency']),
      zoneActivity: parse(json['zoneActivity']),
      longevity: parse(json['longevity']),
      overall: parse(json['overall']),
    );
  }

  /// Ordered list of (label, value) pairs for the UI.
  List<MapEntry<String, double>> get features => [
        MapEntry('Completion rate', completionRate),
        MapEntry('Rating reliability', ratingReliability),
        MapEntry('Community standing', communityStanding),
        MapEntry('Recency', recency),
        MapEntry('Zone activity', zoneActivity),
        MapEntry('Longevity', longevity),
      ];

  static TrustScoreBreakdown empty() => const TrustScoreBreakdown(
        completionRate: 0,
        ratingReliability: 0,
        communityStanding: 0,
        recency: 0,
        zoneActivity: 0,
        longevity: 0,
        overall: 0,
      );
}