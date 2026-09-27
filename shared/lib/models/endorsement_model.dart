// shared/lib/models/endorsement_model.dart

/// A single endorsement given by one user to another for a specific skill.
class Endorsement {
  final String endorsementId;
  final String endorserId;
  final String endorseeId;
  final String zoneId;
  final String skillTag;
  final String? taskId;
  final int weight;
  final DateTime createdAt;

  // Optional display fields, populated by the backend on list/graph calls
  final String? endorserName;
  final String? endorserProfilePhoto;

  Endorsement({
    required this.endorsementId,
    required this.endorserId,
    required this.endorseeId,
    required this.zoneId,
    required this.skillTag,
    this.taskId,
    this.weight = 1,
    required this.createdAt,
    this.endorserName,
    this.endorserProfilePhoto,
  });

  factory Endorsement.fromJson(Map<String, dynamic> json) {
    return Endorsement(
      endorsementId: json['endorsementId'] as String? ?? '',
      endorserId: json['endorserId'] as String? ?? '',
      endorseeId: json['endorseeId'] as String? ?? '',
      zoneId: json['zoneId'] as String? ?? '',
      skillTag: json['skillTag'] as String? ?? '',
      taskId: json['taskId'] as String?,
      weight: json['weight'] as int? ?? 1,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'].toString()) ?? DateTime.now()
          : DateTime.now(),
      endorserName: json['endorserName'] as String?,
      endorserProfilePhoto: json['endorserProfilePhoto'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'endorsementId': endorsementId,
      'endorserId': endorserId,
      'endorseeId': endorseeId,
      'zoneId': zoneId,
      'skillTag': skillTag,
      'taskId': taskId,
      'weight': weight,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// Lightweight request body for creating a new endorsement.
  Map<String, dynamic> toCreateRequest() {
    return {
      'endorseeId': endorseeId,
      'skillTag': skillTag,
      if (taskId != null) 'taskId': taskId,
    };
  }
}

/// Summary of a user's endorsements for the profile card.
class EndorsementSummary {
  final int totalCount;
  final List<SkillCount> topSkills;
  final List<EndorsementGraphNode> miniGraphNodes;
  final List<EndorsementGraphEdge> miniGraphEdges;

  EndorsementSummary({
    required this.totalCount,
    required this.topSkills,
    required this.miniGraphNodes,
    required this.miniGraphEdges,
  });

  factory EndorsementSummary.empty() => EndorsementSummary(
        totalCount: 0,
        topSkills: const [],
        miniGraphNodes: const [],
        miniGraphEdges: const [],
      );

  factory EndorsementSummary.fromJson(Map<String, dynamic> json) {
    return EndorsementSummary(
      totalCount: json['totalCount'] as int? ?? 0,
      topSkills: (json['topSkills'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(SkillCount.fromJson)
          .toList(),
      miniGraphNodes: (json['miniGraphNodes'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphNode.fromJson)
          .toList(),
      miniGraphEdges: (json['miniGraphEdges'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphEdge.fromJson)
          .toList(),
    );
  }
}

/// A skill tag with a count of how many times it's been given.
class SkillCount {
  final String skillTag;
  final String displayName;
  final int count;

  SkillCount({
    required this.skillTag,
    required this.displayName,
    required this.count,
  });

  factory SkillCount.fromJson(Map<String, dynamic> json) {
    return SkillCount(
      skillTag: json['skillTag'] as String? ?? '',
      displayName: json['displayName'] as String? ?? '',
      count: json['count'] as int? ?? 0,
    );
  }
}

/// A node in the endorsement graph (represents a user).
class EndorsementGraphNode {
  final String userId;
  final String displayName;
  final String? profilePhoto;
  final double trustScore;
  final bool isCentreNode;

  EndorsementGraphNode({
    required this.userId,
    required this.displayName,
    this.profilePhoto,
    required this.trustScore,
    this.isCentreNode = false,
  });

  factory EndorsementGraphNode.fromJson(Map<String, dynamic> json) {
    return EndorsementGraphNode(
      userId: json['userId'] as String? ?? '',
      displayName: json['displayName'] as String? ?? '',
      profilePhoto: json['profilePhoto'] as String?,
      trustScore: (json['trustScore'] as num?)?.toDouble() ?? 0.0,
      isCentreNode: json['isCentreNode'] as bool? ?? false,
    );
  }
}

/// An edge in the endorsement graph (represents an endorsement).
class EndorsementGraphEdge {
  final String endorserId;
  final String endorseeId;
  final String skillTag;
  final int weight;

  EndorsementGraphEdge({
    required this.endorserId,
    required this.endorseeId,
    required this.skillTag,
    required this.weight,
  });

  factory EndorsementGraphEdge.fromJson(Map<String, dynamic> json) {
    return EndorsementGraphEdge(
      endorserId: json['endorserId'] as String? ?? '',
      endorseeId: json['endorseeId'] as String? ?? '',
      skillTag: json['skillTag'] as String? ?? '',
      weight: json['weight'] as int? ?? 1,
    );
  }
}

/// The full graph payload returned by the graph endpoint.
class EndorsementGraph {
  final List<EndorsementGraphNode> nodes;
  final List<EndorsementGraphEdge> edges;

  EndorsementGraph({required this.nodes, required this.edges});

  factory EndorsementGraph.empty() =>
      EndorsementGraph(nodes: const [], edges: const []);

  factory EndorsementGraph.fromJson(Map<String, dynamic> json) {
    return EndorsementGraph(
      nodes: (json['nodes'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphNode.fromJson)
          .toList(),
      edges: (json['edges'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphEdge.fromJson)
          .toList(),
    );
  }
}

/// A trust path between two users.
class TrustPath {
  final List<EndorsementGraphNode> nodes;
  final List<EndorsementGraphEdge> edges;

  TrustPath({required this.nodes, required this.edges});

  factory TrustPath.fromJson(Map<String, dynamic> json) {
    return TrustPath(
      nodes: (json['nodes'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphNode.fromJson)
          .toList(),
      edges: (json['edges'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(EndorsementGraphEdge.fromJson)
          .toList(),
    );
  }
}

/// A curated skill tag available for endorsements.
class SkillTag {
  final String tag;
  final String displayName;
  final String category;

  SkillTag({
    required this.tag,
    required this.displayName,
    required this.category,
  });

  factory SkillTag.fromJson(Map<String, dynamic> json) {
    return SkillTag(
      tag: json['tag'] as String? ?? '',
      displayName: json['displayName'] as String? ?? '',
      category: json['category'] as String? ?? '',
    );
  }
}


/// A flagged endorsement pattern for admin review.
/// Detected by the backend's anti-abuse scan.
class SuspiciousEndorsement {
  final String patternId;
  final String patternType; // 'mutual_ring', 'sudden_spike', 'island_group'
  final List<String> involvedUserIds;
  final String description;
  final DateTime detectedAt;
  final String severity; // 'low', 'medium', 'high'

  SuspiciousEndorsement({
    required this.patternId,
    required this.patternType,
    required this.involvedUserIds,
    required this.description,
    required this.detectedAt,
    required this.severity,
  });

  factory SuspiciousEndorsement.fromJson(Map<String, dynamic> json) {
    return SuspiciousEndorsement(
      patternId: json['patternId'] as String? ?? '',
      patternType: json['patternType'] as String? ?? '',
      involvedUserIds: (json['involvedUserIds'] as List<dynamic>? ?? [])
          .whereType<String>()
          .toList(),
      description: json['description'] as String? ?? '',
      detectedAt: json['detectedAt'] != null
          ? DateTime.tryParse(json['detectedAt'].toString()) ?? DateTime.now()
          : DateTime.now(),
      severity: json['severity'] as String? ?? 'low',
    );
  }
}

/// Zone-wide metrics for the admin dashboard.
class ZoneInsights {
  final int totalEndorsements;
  final int totalUsers;
  final int clusterCount;
  final int largestClusterSize;
  final int isolatedUserCount;
  final List<String> bridgeUserIds;
  final List<SkillCount> topSkills;

  ZoneInsights({
    required this.totalEndorsements,
    required this.totalUsers,
    required this.clusterCount,
    required this.largestClusterSize,
    required this.isolatedUserCount,
    required this.bridgeUserIds,
    required this.topSkills,
  });

  factory ZoneInsights.empty() => ZoneInsights(
        totalEndorsements: 0,
        totalUsers: 0,
        clusterCount: 0,
        largestClusterSize: 0,
        isolatedUserCount: 0,
        bridgeUserIds: const [],
        topSkills: const [],
      );

  factory ZoneInsights.fromJson(Map<String, dynamic> json) {
    return ZoneInsights(
      totalEndorsements: json['totalEndorsements'] as int? ?? 0,
      totalUsers: json['totalUsers'] as int? ?? 0,
      clusterCount: json['clusterCount'] as int? ?? 0,
      largestClusterSize: json['largestClusterSize'] as int? ?? 0,
      isolatedUserCount: json['isolatedUserCount'] as int? ?? 0,
      bridgeUserIds: (json['bridgeUserIds'] as List<dynamic>? ?? [])
          .whereType<String>()
          .toList(),
      topSkills: (json['topSkills'] as List<dynamic>? ?? [])
          .whereType<Map<String, dynamic>>()
          .map(SkillCount.fromJson)
          .toList(),
    );
  }
}