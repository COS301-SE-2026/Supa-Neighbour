// admin/lib/services/endorsement_service_mock.dart

import 'package:shared/shared.dart';
import 'endorsement_service.dart';

/// Mock implementation of [IEndorsementService] for development and testing.
/// Returns a seeded zone of 20 users with realistic endorsement patterns,
/// including clusters, bridge users, and abuse patterns for demo purposes.
class EndorsementServiceMock implements IEndorsementService {
  // ---- Seeded zone users ----
  final _users = [
    EndorsementGraphNode(userId: 'u1', displayName: 'David W.', trustScore: 4.8),
    EndorsementGraphNode(userId: 'u2', displayName: 'Sarah N.', trustScore: 4.9),
    EndorsementGraphNode(userId: 'u3', displayName: 'Mike H.', trustScore: 4.6),
    EndorsementGraphNode(userId: 'u4', displayName: 'Jane D.', trustScore: 4.7),
    EndorsementGraphNode(userId: 'u5', displayName: 'Bob K.', trustScore: 4.5),
    EndorsementGraphNode(userId: 'u6', displayName: 'Lisa M.', trustScore: 4.3),
    EndorsementGraphNode(userId: 'u7', displayName: 'Tom R.', trustScore: 4.2),
    EndorsementGraphNode(userId: 'u8', displayName: 'Mark S.', trustScore: 3.9),
    EndorsementGraphNode(userId: 'u9', displayName: 'Nina P.', trustScore: 4.0),
    EndorsementGraphNode(userId: 'u10', displayName: 'Chris B.', trustScore: 4.8),
    EndorsementGraphNode(userId: 'u11', displayName: 'Alex T.', trustScore: 4.1),
    EndorsementGraphNode(userId: 'u12', displayName: 'Priya K.', trustScore: 4.6),
    EndorsementGraphNode(userId: 'u13', displayName: 'Omar F.', trustScore: 4.4),
    EndorsementGraphNode(userId: 'u14', displayName: 'Rebecca L.', trustScore: 4.7),
    EndorsementGraphNode(userId: 'u15', displayName: 'Sam J.', trustScore: 4.3),
    EndorsementGraphNode(userId: 'u16', displayName: 'Grace H.', trustScore: 4.5),
    EndorsementGraphNode(userId: 'u17', displayName: 'Daniel O.', trustScore: 4.2),
    EndorsementGraphNode(userId: 'u18', displayName: 'Emma V.', trustScore: 4.6),
    EndorsementGraphNode(userId: 'u19', displayName: 'Liam C.', trustScore: 4.1),
    EndorsementGraphNode(userId: 'u20', displayName: 'Zara M.', trustScore: 4.4),
  ];

  // ---- Seeded endorsement edges ----
  // Structure:
  //   Cluster 1: u1, u2, u3, u4 (dense, high trust)
  //   Cluster 2: u5, u6, u7, u8 (moderate)
  //   Bridge: u4 → u9 → u10 (connects clusters)
  //   Cluster 3: u12–u16 (moderate)
  //   Isolated: u11 (has one endorsement only)
  //   Suspicious ring: u17, u18, u19, u20 (mutual endorsements)
  final _endorsements = [
    // Dense cluster 1
    _e('e1', 'u1', 'u2', 'reliable', 3),
    _e('e2', 'u2', 'u1', 'trustworthy', 3),
    _e('e3', 'u1', 'u3', 'communicative', 2),
    _e('e4', 'u3', 'u1', 'reliable', 2),
    _e('e5', 'u2', 'u4', 'pet_care', 3),
    _e('e6', 'u4', 'u2', 'communicative', 3),
    _e('e7', 'u1', 'u4', 'reliable', 2),
    _e('e8', 'u3', 'u4', 'punctual', 1),

    // Moderate cluster 2
    _e('e9', 'u5', 'u6', 'reliable', 2),
    _e('e10', 'u6', 'u5', 'trustworthy', 1),
    _e('e11', 'u6', 'u7', 'communicative', 1),
    _e('e12', 'u7', 'u8', 'gardening', 1),
    _e('e13', 'u8', 'u6', 'pet_care', 1),

    // Bridge
    _e('e14', 'u4', 'u9', 'trustworthy', 3),
    _e('e15', 'u9', 'u10', 'reliable', 2),
    _e('e16', 'u10', 'u5', 'communicative', 2),

    // Cluster 3
    _e('e17', 'u12', 'u13', 'pet_care', 2),
    _e('e18', 'u13', 'u14', 'gardening', 2),
    _e('e19', 'u14', 'u15', 'reliable', 2),
    _e('e20', 'u15', 'u16', 'communicative', 2),
    _e('e21', 'u16', 'u12', 'trustworthy', 2),
    _e('e22', 'u13', 'u16', 'pet_care', 1),
    _e('e23', 'u14', 'u12', 'punctual', 1),

    // Isolated user (only one incoming endorsement)
    _e('e24', 'u3', 'u11', 'reliable', 1),

    // Suspicious mutual ring (u17-u20) — flagged by abuse detection
    _e('e25', 'u17', 'u18', 'reliable', 1),
    _e('e26', 'u18', 'u17', 'reliable', 1),
    _e('e27', 'u17', 'u19', 'trustworthy', 1),
    _e('e28', 'u19', 'u17', 'trustworthy', 1),
    _e('e29', 'u18', 'u20', 'communicative', 1),
    _e('e30', 'u20', 'u18', 'communicative', 1),
    _e('e31', 'u19', 'u20', 'pet_care', 1),
    _e('e32', 'u20', 'u19', 'pet_care', 1),
    _e('e33', 'u17', 'u20', 'reliable', 1),
    _e('e34', 'u20', 'u17', 'reliable', 1),
  ];

  static Endorsement _e(String id, String from, String to, String skill, int weight) {
    return Endorsement(
      endorsementId: id,
      endorserId: from,
      endorseeId: to,
      zoneId: 'zone_demo',
      skillTag: skill,
      weight: weight,
      createdAt: DateTime.now().subtract(Duration(days: id.hashCode % 30)),
    );
  }
  // ---- Suspicious patterns ----
  final _suspiciousPatterns = [
    SuspiciousEndorsement(
      patternId: 'sp1',
      patternType: 'mutual_ring',
      involvedUserIds: ['u17', 'u18', 'u19', 'u20'],
      description:
          'Four users have endorsed each other repeatedly over a short period. '
          'All 10 edges exist within this group, with no endorsements from '
          'outside the ring.',
      detectedAt: DateTime.now().subtract(const Duration(days: 1)),
      severity: 'high',
    ),
    SuspiciousEndorsement(
      patternId: 'sp2',
      patternType: 'island_group',
      involvedUserIds: ['u17', 'u18', 'u19', 'u20'],
      description:
          'This cluster has no connections to the wider endorsement graph. '
          'It may represent coordinated self-promotion.',
      detectedAt: DateTime.now().subtract(const Duration(days: 1)),
      severity: 'medium',
    ),
  ];

  @override
  Future<EndorsementGraph> getZoneGraph({int depth = 3}) async {
    await Future.delayed(const Duration(milliseconds: 600));

    return EndorsementGraph(
      nodes: List.from(_users),
      edges: _endorsements
          .map((e) => EndorsementGraphEdge(
                endorserId: e.endorserId,
                endorseeId: e.endorseeId,
                skillTag: e.skillTag,
                weight: e.weight,
              ))
          .toList(),
    );
  }

  @override
  Future<List<Endorsement>> getZoneEndorsements({String? skillTag}) async {
    await Future.delayed(const Duration(milliseconds: 400));

    if (skillTag == null || skillTag.isEmpty) {
      return List.from(_endorsements);
    }

    return _endorsements.where((e) => e.skillTag == skillTag).toList();
  }

  @override
  Future<List<SuspiciousEndorsement>> getSuspiciousPatterns() async {
    await Future.delayed(const Duration(milliseconds: 400));
    return List.from(_suspiciousPatterns);
  }

  @override
  Future<ZoneInsights> getZoneInsights() async {
    await Future.delayed(const Duration(milliseconds: 400));

    // Compute top skills from endorsements
    final skillCounts = <String, int>{};
    for (final e in _endorsements) {
      skillCounts[e.skillTag] = (skillCounts[e.skillTag] ?? 0) + 1;
    }

    final topSkills = skillCounts.entries
        .map((entry) => SkillCount(
              skillTag: entry.key,
              displayName: _displayNameFor(entry.key),
              count: entry.value,
            ))
        .toList()
      ..sort((a, b) => b.count.compareTo(a.count));

    return ZoneInsights(
      totalEndorsements: _endorsements.length,
      totalUsers: _users.length,
      clusterCount: 4, // seeded clusters
      largestClusterSize: 4, // u17-u20 or u1-u4
      isolatedUserCount: 1, // u11
      bridgeUserIds: ['u4', 'u9'],
      topSkills: topSkills.take(5).toList(),
    );
  }

  String _displayNameFor(String skillTag) {
    switch (skillTag) {
      case 'reliable':
        return 'Reliable';
      case 'trustworthy':
        return 'Trustworthy';
      case 'communicative':
        return 'Communicative';
      case 'pet_care':
        return 'Great with pets';
      case 'gardening':
        return 'Excellent gardener';
      case 'punctual':
        return 'Punctual';
      default:
        return skillTag;
    }
  }
}