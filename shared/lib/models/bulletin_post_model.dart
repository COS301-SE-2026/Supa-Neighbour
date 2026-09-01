class BulletinPost {
  final int id;
  final String postContent;
  final String? mediaUrl;
  final String category;
  final int authorId;
  final String authorUsername;
  final int likeCount;
  final int dislikeCount;
  final int commentCount;
  final DateTime createdAt;
  final DateTime updatedAt;
  final bool isOwner;
  final bool isHelpfulByUser;

  BulletinPost({
    required this.id,
    required this.postContent,
    this.mediaUrl,
    required this.category,
    required this.authorId,
    required this.authorUsername,
    this.likeCount = 0,
    this.dislikeCount = 0,
    this.commentCount = 0,
    required this.createdAt,
    required this.updatedAt,
    this.isOwner = false,
    this.isHelpfulByUser = false,
  });

  factory BulletinPost.fromJson(Map<String, dynamic> json, int currentUserId) {
    return BulletinPost(
      id: json['postId'] as int,
      postContent: json['postContent'] as String? ?? '',
      mediaUrl: json['mediaUrl'] as String?,
      category: json['category'] as String? ?? 'general',
      authorId: json['userId'] as int,
      authorUsername: json['authorUsername'] as String? ?? '',
      likeCount: (json['likeCount'] as num?)?.toInt() ?? 0,
      dislikeCount: (json['dislikeCount'] as num?)?.toInt() ?? 0,
      commentCount: (json['commentCount'] as num?)?.toInt() ?? 0,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      isOwner: (json['userId'] as int) == currentUserId,
      isHelpfulByUser: false,
    );
  }

  BulletinPost copyWith({
    bool? isHelpfulByUser,
    int? likeCount,
  }) {
    return BulletinPost(
      id: id,
      postContent: postContent,
      mediaUrl: mediaUrl,
      category: category,
      authorId: authorId,
      authorUsername: authorUsername,
      likeCount: likeCount ?? this.likeCount,
      dislikeCount: dislikeCount,
      commentCount: commentCount,
      createdAt: createdAt,
      updatedAt: updatedAt,
      isOwner: isOwner,
      isHelpfulByUser: isHelpfulByUser ?? this.isHelpfulByUser,
    );
  }

  String get authorAvatar =>
      authorUsername.isNotEmpty ? authorUsername[0].toUpperCase() : '?';
}
