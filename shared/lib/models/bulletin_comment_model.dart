class BulletinComment {
  final int id;
  final String content;
  final int authorId;
  final String authorName;
  final int? parentCommentId;
  final DateTime createdAt;

  BulletinComment({
    required this.id,
    required this.content,
    required this.authorId,
    required this.authorName,
    this.parentCommentId,
    required this.createdAt,
  });

  factory BulletinComment.fromJson(Map<String, dynamic> json) {
    return BulletinComment(
      id: json['commentId'] as int,
      content: json['commentContent'] as String? ?? '',
      authorId: json['userId'] as int,
      authorName: json['authorUsername'] as String? ?? '',
      parentCommentId: json['parentCommentId'] as int?,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }
}
