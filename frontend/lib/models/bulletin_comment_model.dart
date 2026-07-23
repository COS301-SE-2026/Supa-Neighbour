class BulletinComment {
  final String id;
  final String content;
  final String authorId;
  final String authorName;
  final DateTime createdAt;

  BulletinComment({
    required this.id,
    required this.content,
    required this.authorId,
    required this.authorName,
    required this.createdAt,
  });

  // Mock data for now
  static List<BulletinComment> getMockComments(String postId) {
    return [
      BulletinComment(
        id: 'c1',
        content: 'I hope you find Max!',
        authorId: 'user_2',
        authorName: 'Michael Jackson',
        createdAt: DateTime.now().subtract(const Duration(hours: 1)),
      ),
      BulletinComment(
        id: 'c2',
        content: 'Have you checked plaza maybe someone saw him?',
        authorId: 'user_3',
        authorName: 'Lisa Wong',
        createdAt: DateTime.now().subtract(const Duration(minutes: 30)),
      ),
      BulletinComment(
        id: 'c3',
        content: 'I will keep an eye out!',
        authorId: 'user_4',
        authorName: 'Tom Jerry',
        createdAt: DateTime.now().subtract(const Duration(minutes: 10)),
      ),
    ];
  }
}