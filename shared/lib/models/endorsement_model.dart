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
/// A node in the endorsement graph (represents a user).
class EndorsementGraphNode {
  final String userId;
  final String displayName;
  final String? profilePhoto;
  final double? trustScore;
  final bool? isCentreNode; // raw, nullable — some endpoints omit it

  EndorsementGraphNode({
    required this.userId,
    required this.displayName,
    this.profilePhoto,
    this.trustScore,
    this.isCentreNode,
  });

  bool get isCentre => isCentreNode ?? false;

  factory EndorsementGraphNode.fromJson(Map<String, dynamic> json) {
    return EndorsementGraphNode(
      userId: _idFromJson(json['userId']),
      displayName: json['displayName'] as String? ?? '',
      profilePhoto: json['profilePhoto'] as String?,
      trustScore: (json['trustScore'] as num?)?.toDouble(),
      isCentreNode: json['isCentreNode'] as bool?,
    );
  }

  static String _idFromJson(dynamic value) {
    if (value == null) return '';
    return value.toString();
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
      endorserId: _idFromJson(json['fromUserId']),
      endorseeId: _idFromJson(json['toUserId']),
      skillTag: json['skillTag'] as String? ?? '',
      weight: json['weight'] as int? ?? 1,
    );
  }

  static String _idFromJson(dynamic value) {
    if (value == null) return '';
    return value.toString();
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
  final String patternTypeDisplay;
  final List<String> involvedUserIds;
  final List<AbuseFlagParticipant> participants;
  final String description;
  final DateTime detectedAt;
  final DateTime firstDetectedAt;
  final String severity; // 'low' / 'medium' / 'high' — derived, see _severityFrom
  final String status; // 'open', 'investigate', 'dismiss'
  final int occurrenceCount;
  final double? metricValue;
  final int? zoneId;
  final String? runId;

SuspiciousEndorsement({
    required this.patternId,
    required this.patternType,
    required this.patternTypeDisplay,
    required this.involvedUserIds,
    required this.participants,
    required this.description,
    required this.detectedAt,
    required this.firstDetectedAt,
    required this.severity,
    required this.status,
    required this.occurrenceCount,
    this.metricValue,
    this.zoneId,
    this.runId,
  });

  factory SuspiciousEndorsement.fromJson(Map<String, dynamic> json) {
    final participants = (json['participants'] as List<dynamic>? ?? [])
        .whereType<Map<String, dynamic>>()
        .map(AbuseFlagParticipant.fromJson)
        .toList();

    final lastDetected = json['lastDetectedAt'] != null
        ? DateTime.tryParse(json['lastDetectedAt'].toString()) ?? DateTime.now()
        : DateTime.now();
    final firstDetected = json['firstDetectedAt'] != null
        ? DateTime.tryParse(json['firstDetectedAt'].toString()) ?? lastDetected
        : lastDetected;
    final occurrenceCount = json['occurrenceCount'] as int? ?? 0;

    return SuspiciousEndorsement(
      patternId: (json['flagId'] as num?)?.toString() ?? '',
      patternType: json['patternType'] as String? ?? '',
      patternTypeDisplay: json['patternTypeDisplay'] as String? ?? '',
      involvedUserIds: participants.map((p) => p.userId).toList(),
      participants: participants,
      description: json['patternTypeDisplay'] as String? ?? '',
      detectedAt: lastDetected,
      firstDetectedAt: firstDetected,
      severity: _severityFrom(occurrenceCount),
      status: json['status'] as String? ?? 'open',
      occurrenceCount: occurrenceCount,
      metricValue: (json['metricValue'] as num?)?.toDouble(),
      zoneId: json['zoneId'] as int?,
      runId: json['runId'] as String?,
    );
  }

  static String _severityFrom(int occurrenceCount) {
    if (occurrenceCount >= 5) return 'high';
    if (occurrenceCount >= 2) return 'medium';
    return 'low';
  }
}

class AbuseFlagParticipant{
  final String userId;
  final String role;

  AbuseFlagParticipant({
    required this.userId,
    required this.role
  });

  factory AbuseFlagParticipant.fromJson(Map<String, dynamic> json){
    return AbuseFlagParticipant(
      userId: _idFromJson(json['userId']), 
      role: json['role'] as String? ?? '',
    );
  }

  static String _idFromJson(dynamic value){
    if(value == null){
      return '';
    }

    return value.toString();
  }
}

class ZoneInsights {
  final int clusterCount;
  final int largestClusterSize;
  final int isolatedUserCount;
  final DateTime? computedAt;
  final String? runId;

  ZoneInsights({
    required this.clusterCount,
    required this.largestClusterSize,
    required this.isolatedUserCount,
    this.computedAt,
    this.runId,
  });

  factory ZoneInsights.empty() => ZoneInsights(
        clusterCount: 0,
        largestClusterSize: 0,
        isolatedUserCount: 0,
      );

  factory ZoneInsights.fromJson(Map<String, dynamic> json) {
    final members = (json['members'] as List<dynamic>? ?? [])
        .whereType<Map<String, dynamic>>()
        .toList();

    final sizeByCluster = <int, int>{};
    var isolatedCount = 0;
    for (final m in members) {
      final clusterLabel = m['clusterLabel'] as int? ?? 0;
      sizeByCluster.update(clusterLabel, (v) => v + 1, ifAbsent: () => 1);
      final totalDegree = m['totalDegree'] as int? ?? 0;
      if (totalDegree == 0) isolatedCount++;
    }
    final largestClusterSize = sizeByCluster.values.isEmpty
        ? 0
        : sizeByCluster.values.reduce((a, b) => a > b ? a : b);

    return ZoneInsights(
      clusterCount: json['clusterCount'] as int? ?? 0,
      largestClusterSize: largestClusterSize,
      isolatedUserCount: isolatedCount,
      computedAt: json['computedAt'] != null
          ? DateTime.tryParse(json['computedAt'].toString())
          : null,
      runId: json['runId'] as String?,
    );
  }


}