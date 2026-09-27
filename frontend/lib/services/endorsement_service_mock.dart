// frontend/lib/services/endorsement_service_mock.dart

import 'package:shared/shared.dart';
import 'endorsement_service.dart';

/// Mock implementation of [IEndorsementService] for development and testing.
/// Returns realistic in-memory data that mirrors what the real backend will
/// eventually provide, including a dense cluster around the current user,
/// a bridge user, and an isolated user for demo contrast.
class EndorsementServiceMock implements IEndorsementService {
  // ---- Mock users in the zone ----
    final _users = [
    EndorsementGraphNode(
        userId: 'me', displayName: 'You', trustScore: 4.8, isCentreNode: true),
    EndorsementGraphNode(
        userId: 'david', displayName: 'David W.', trustScore: 4.8),
    EndorsementGraphNode(
        userId: 'sarah', displayName: 'Sarah N.', trustScore: 4.9),
    EndorsementGraphNode(
        userId: 'mike', displayName: 'Mike H.', trustScore: 4.6),
    EndorsementGraphNode(
        userId: 'jane', displayName: 'Jane D.', trustScore: 4.7),
    EndorsementGraphNode(
        userId: 'bob', displayName: 'Bob K.', trustScore: 4.5),
    EndorsementGraphNode(
        userId: 'lisa', displayName: 'Lisa M.', trustScore: 4.3),
    EndorsementGraphNode(
        userId: 'tom', displayName: 'Tom R.', trustScore: 4.2),
    EndorsementGraphNode(
        userId: 'mark', displayName: 'Mark S.', trustScore: 3.9),
    EndorsementGraphNode(
        userId: 'nina', displayName: 'Nina P.', trustScore: 4.0),
    EndorsementGraphNode(
        userId: 'chris', displayName: 'Chris B.', trustScore: 4.8),
    EndorsementGraphNode(
        userId: 'alex', displayName: 'Alex T.', trustScore: 4.1),
  ];

  // ---- Mock skill tags ----
  final _skillTags = [
    SkillTag(tag: 'reliable', displayName: 'Reliable', category: 'general'),
    SkillTag(tag: 'pet_care', displayName: 'Great with pets', category: 'pet_care'),
    SkillTag(tag: 'gardening', displayName: 'Excellent gardener', category: 'plant_care'),
    SkillTag(tag: 'communicative', displayName: 'Communicative', category: 'general'),
    SkillTag(tag: 'trustworthy', displayName: 'Trustworthy', category: 'general'),
    SkillTag(tag: 'punctual', displayName: 'Punctual', category: 'general'),
  ];

  // ---- Mock endorsements received by the current user ----
  final _myEndorsements = [
    Endorsement(
      endorsementId: 'e1',
      endorserId: 'david',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'reliable',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 2)),
      endorserName: 'David W.',
    ),
    Endorsement(
      endorsementId: 'e2',
      endorserId: 'sarah',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'communicative',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 5)),
      endorserName: 'Sarah N.',
    ),
    Endorsement(
      endorsementId: 'e3',
      endorserId: 'mike',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'reliable',
      weight: 2,
      createdAt: DateTime.now().subtract(const Duration(days: 8)),
      endorserName: 'Mike H.',
    ),
    Endorsement(
      endorsementId: 'e4',
      endorserId: 'jane',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'pet_care',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 12)),
      endorserName: 'Jane D.',
    ),
    Endorsement(
      endorsementId: 'e5',
      endorserId: 'bob',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'reliable',
      weight: 2,
      createdAt: DateTime.now().subtract(const Duration(days: 15)),
      endorserName: 'Bob K.',
    ),
    Endorsement(
      endorsementId: 'e6',
      endorserId: 'lisa',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'trustworthy',
      weight: 1,
      createdAt: DateTime.now().subtract(const Duration(days: 18)),
      endorserName: 'Lisa M.',
    ),
    Endorsement(
      endorsementId: 'e7',
      endorserId: 'tom',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'punctual',
      weight: 1,
      createdAt: DateTime.now().subtract(const Duration(days: 22)),
      endorserName: 'Tom R.',
    ),
    Endorsement(
      endorsementId: 'e8',
      endorserId: 'nina',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'pet_care',
      weight: 1,
      createdAt: DateTime.now().subtract(const Duration(days: 25)),
      endorserName: 'Nina P.',
    ),
    Endorsement(
      endorsementId: 'e9',
      endorserId: 'mark',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'gardening',
      weight: 1,
      createdAt: DateTime.now().subtract(const Duration(days: 30)),
      endorserName: 'Mark S.',
    ),
    Endorsement(
      endorsementId: 'e10',
      endorserId: 'david',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'trustworthy',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 35)),
      endorserName: 'David W.',
    ),
    Endorsement(
      endorsementId: 'e11',
      endorserId: 'sarah',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'reliable',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 40)),
      endorserName: 'Sarah N.',
    ),
    Endorsement(
      endorsementId: 'e12',
      endorserId: 'jane',
      endorseeId: 'me',
      zoneId: 'zone1',
      skillTag: 'communicative',
      weight: 3,
      createdAt: DateTime.now().subtract(const Duration(days: 45)),
      endorserName: 'Jane D.',
    ),
  ];

  @override
  Future<List<Endorsement>> getMyEndorsements({String? skillTag}) async {
    await Future.delayed(const Duration(milliseconds: 400));
    if (skillTag == null || skillTag.isEmpty) return List.from(_myEndorsements);
    return _myEndorsements.where((e) => e.skillTag == skillTag).toList();
  }

  @override
  Future<EndorsementSummary> getMySummary() async {
    await Future.delayed(const Duration(milliseconds: 300));

    // Count endorsements per skill
    final counts = <String, int>{};
    for (final e in _myEndorsements) {
      counts[e.skillTag] = (counts[e.skillTag] ?? 0) + 1;
    }

    final topSkills = counts.entries
        .map((entry) {
          final display = _skillTags
              .firstWhere((s) => s.tag == entry.key,
                  orElse: () => SkillTag(
                      tag: entry.key, displayName: entry.key, category: ''))
              .displayName;
          return SkillCount(
            skillTag: entry.key,
            displayName: display,
            count: entry.value,
          );
        })
        .toList()
      ..sort((a, b) => b.count.compareTo(a.count));

    // Mini graph: current user + 1-hop neighbours
    final oneHopNodes = <EndorsementGraphNode>[
      _users.firstWhere((u) => u.userId == 'me'),
      ..._myEndorsements
          .map((e) => _users.firstWhere((u) => u.userId == e.endorserId))
          .toSet(),
    ];

    final miniEdges = _myEndorsements
        .map((e) => EndorsementGraphEdge(
              endorserId: e.endorserId,
              endorseeId: e.endorseeId,
              skillTag: e.skillTag,
              weight: e.weight,
            ))
        .toList();

    return EndorsementSummary(
      totalCount: _myEndorsements.length,
      topSkills: topSkills.take(3).toList(),
      miniGraphNodes: oneHopNodes,
      miniGraphEdges: miniEdges,
    );
  }

  @override
  Future<EndorsementGraph> getGraph({
    String? userId,
    int depth = 2,
    String? skillTag,
  }) async {
    await Future.delayed(const Duration(milliseconds: 600));

    final centre = userId ?? 'me';

    // Mock graph: centre + a set of endorsements with realistic clustering
    // Cluster 1: centre ↔ david, sarah, mike, jane (dense)
    // Cluster 2: centre ↔ bob, lisa, tom (moderate)
    // Bridge user: jane connects to mark → nina (second cluster)
    // Isolated: alex has no endorsements
    // Sparser comparison user: chris has only 2 endorsements

    final edges = <EndorsementGraphEdge>[
      // Dense cluster around centre
      EndorsementGraphEdge(
          endorserId: 'david', endorseeId: centre, skillTag: 'reliable', weight: 3),
      EndorsementGraphEdge(
          endorserId: 'sarah', endorseeId: centre, skillTag: 'communicative', weight: 3),
      EndorsementGraphEdge(
          endorserId: 'mike', endorseeId: centre, skillTag: 'reliable', weight: 2),
      EndorsementGraphEdge(
          endorserId: 'jane', endorseeId: centre, skillTag: 'pet_care', weight: 3),

      // Moderate cluster
      EndorsementGraphEdge(
          endorserId: 'bob', endorseeId: centre, skillTag: 'reliable', weight: 2),
      EndorsementGraphEdge(
          endorserId: 'lisa', endorseeId: centre, skillTag: 'trustworthy', weight: 1),
      EndorsementGraphEdge(
          endorserId: 'tom', endorseeId: centre, skillTag: 'punctual', weight: 1),

      // Bridge: jane endorses mark, mark endorses nina (2-hop from centre)
      EndorsementGraphEdge(
          endorserId: 'jane', endorseeId: 'mark', skillTag: 'gardening', weight: 2),
      EndorsementGraphEdge(
          endorserId: 'mark', endorseeId: 'nina', skillTag: 'pet_care', weight: 1),

      // Cross-links (make the graph look natural)
      EndorsementGraphEdge(
          endorserId: 'david', endorseeId: 'sarah', skillTag: 'trustworthy', weight: 3),
      EndorsementGraphEdge(
          endorserId: 'sarah', endorseeId: 'david', skillTag: 'reliable', weight: 3),
      EndorsementGraphEdge(
          endorserId: 'mike', endorseeId: 'bob', skillTag: 'communicative', weight: 2),
    ];

    // Filter by skill if requested
    final filteredEdges = skillTag == null || skillTag.isEmpty
        ? edges
        : edges.where((e) => e.skillTag == skillTag).toList();

    // Compute which user IDs appear in the graph (within 2 hops)
    final visibleIds = <String>{centre};
    for (var i = 0; i < depth; i++) {
      for (final e in filteredEdges) {
        if (visibleIds.contains(e.endorseeId)) visibleIds.add(e.endorserId);
        if (visibleIds.contains(e.endorserId)) visibleIds.add(e.endorseeId);
      }
    }

    final nodes = _users.where((u) => visibleIds.contains(u.userId)).toList();

    return EndorsementGraph(nodes: nodes, edges: filteredEdges);
  }

  @override
  Future<List<TrustPath>> getTrustPaths({required String toUserId}) async {
    await Future.delayed(const Duration(milliseconds: 400));

    // Return a mock 2-hop path: me ← sarah ← david
    final pathNodes = [
      _users.firstWhere((u) => u.userId == 'me'),
      _users.firstWhere((u) => u.userId == 'sarah'),
      _users.firstWhere((u) => u.userId == toUserId,
          orElse: () => _users.firstWhere((u) => u.userId == 'david')),
    ];

    final pathEdges = [
      EndorsementGraphEdge(
          endorserId: 'sarah', endorseeId: 'me', skillTag: 'communicative', weight: 3),
      EndorsementGraphEdge(
          endorserId: 'sarah', endorseeId: toUserId, skillTag: 'reliable', weight: 3),
    ];

    return [TrustPath(nodes: pathNodes, edges: pathEdges)];
  }

  @override
  Future<Endorsement> createEndorsement({
    required String endorseeId,
    required String skillTag,
    String? taskId,
  }) async {
    await Future.delayed(const Duration(milliseconds: 400));

    final endorsement = Endorsement(
      endorsementId: 'e_new_${DateTime.now().millisecondsSinceEpoch}',
      endorserId: 'me',
      endorseeId: endorseeId,
      zoneId: 'zone1',
      skillTag: skillTag,
      taskId: taskId,
      weight: 3,
      createdAt: DateTime.now(),
      endorserName: 'You',
    );

    _myEndorsements.add(endorsement);
    return endorsement;
  }

  @override
  Future<List<SkillTag>> getSkillTags() async {
    await Future.delayed(const Duration(milliseconds: 200));
    return List.from(_skillTags);
  }
}