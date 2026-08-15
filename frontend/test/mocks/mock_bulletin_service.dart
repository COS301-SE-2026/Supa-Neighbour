import 'package:image_picker/image_picker.dart';
import 'package:supa_neighbour/models/bulletin_comment_model.dart';
import 'package:supa_neighbour/models/bulletin_post_model.dart';
import 'package:supa_neighbour/services/bulletin_service.dart';

class MockBulletinService implements IBulletinService {
  final List<BulletinPost> _mockPosts = [
    BulletinPost(
      id: 1,
      postContent: 'This is a test bulletin post',
      mediaUrl: null,
      category: 'general',
      authorId: 123,
      authorUsername: 'testuser',
      likeCount: 5,
      dislikeCount: 2,
      commentCount: 3,
      createdAt: DateTime.now().subtract(const Duration(hours: 2)),
      updatedAt: DateTime.now(),
      isOwner: true,
      isHelpfulByUser: false,
    ),
    BulletinPost(
      id: 2,
      postContent: 'Lost my dog, please help!',
      mediaUrl: null,
      category: 'lost_pet',
      authorId: 456,
      authorUsername: 'petlover',
      likeCount: 10,
      dislikeCount: 1,
      commentCount: 4,
      createdAt: DateTime.now().subtract(const Duration(days: 1)),
      updatedAt: DateTime.now().subtract(const Duration(days: 1)),
      isOwner: false,
      isHelpfulByUser: false,
    ),
  ];

  @override
  Future<List<BulletinPost>> getPosts({
    String? category,
    String? search,
    int page = 1,
    int limit = 20,
  }) async {
    var posts = _mockPosts;
    if (category != null && category != 'all') {
      posts = posts.where((p) => p.category == category).toList();
    }
    if (search != null && search.isNotEmpty) {
      posts = posts.where((p) => p.postContent.contains(search)).toList();
    }
    return posts;
  }

  @override
  Future<BulletinPost> getPost(int postId) async {
    final post = _mockPosts.firstWhere((p) => p.id == postId);
    return post;
  }

  @override
  Future<List<BulletinComment>> getComments(int postId) async {
    return [
      BulletinComment(
        id: 1,
        content: 'Great post!',
        authorId: 789,
        authorName: 'commenter1',
        parentCommentId: null,
        createdAt: DateTime.now().subtract(const Duration(hours: 1)),
      ),
      BulletinComment(
        id: 2,
        content: 'Thanks for sharing!',
        authorId: 790,
        authorName: 'commenter2',
        parentCommentId: null,
        createdAt: DateTime.now().subtract(const Duration(minutes: 30)),
      ),
    ];
  }

  @override
  Future<String?> uploadImage(XFile imageFile) async {
    return 'https://example.com/uploaded.jpg';
  }

  @override
  Future<void> createPost({
    required String postContent,
    required String category,
    String? mediaUrl,
  }) async {
    // Mock success
  }

  @override
  Future<void> deletePost(int postId) async {
    // Mock success
  }

  @override
  Future<BulletinComment> addComment(int postId, String content) async {
    return BulletinComment(
      id: 3,
      content: content,
      authorId: 123,
      authorName: 'currentuser',
      parentCommentId: null,
      createdAt: DateTime.now(),
    );
  }

  @override
  Future<void> addHelpful(int postId) async {
    // Mock success
  }

  @override
  Future<void> removeHelpful(int postId) async {
    // Mock success
  }
}

class MockBulletinServiceEmpty implements IBulletinService {
  @override
  Future<List<BulletinPost>> getPosts({
    String? category,
    String? search,
    int page = 1,
    int limit = 20,
  }) async {
    return [];
  }

  @override
  Future<BulletinPost> getPost(int postId) async {
    throw Exception('Post not found');
  }

  @override
  Future<List<BulletinComment>> getComments(int postId) async {
    return [];
  }

  @override
  Future<String?> uploadImage(XFile imageFile) async {
    return null;
  }

  @override
  Future<void> createPost({
    required String postContent,
    required String category,
    String? mediaUrl,
  }) async {
    // Mock success
  }

  @override
  Future<void> deletePost(int postId) async {
    // Mock success
  }

  @override
  Future<BulletinComment> addComment(int postId, String content) async {
    return BulletinComment(
      id: 1,
      content: content,
      authorId: 123,
      authorName: 'currentuser',
      parentCommentId: null,
      createdAt: DateTime.now(),
    );
  }

  @override
  Future<void> addHelpful(int postId) async {
    // Mock success
  }

  @override
  Future<void> removeHelpful(int postId) async {
    // Mock success
  }
}