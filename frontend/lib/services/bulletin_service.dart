import '../models/bulletin_post_model.dart';
import '../models/bulletin_comment_model.dart';

class BulletinService {
  //actual API calls willbe here but this is mock for now

  Future<List<BulletinPost>> getPosts({
    String? category,
    int page = 1,
    int limit = 20,
  }) async {
    // simulating API delay
    await Future.delayed(const Duration(milliseconds: 500));

    var posts = BulletinPost.getMockPosts();

    // Filter by category
    if (category != null && category != 'all') {
      posts = posts.where((post) => post.category == category).toList();
    }

    //Sort
    posts.sort((a, b) => b.createdAt.compareTo(a.createdAt));

  
    final start = (page - 1) * limit;
    final end = start + limit;
    return posts.length > start ? posts.sublist(start, end > posts.length ? posts.length : end) : [];
  }

  Future<BulletinPost> getPost(String postId) async {
    await Future.delayed(const Duration(milliseconds: 300));

    final posts = BulletinPost.getMockPosts();
    return posts.firstWhere((post) => post.id == postId);
  }

  Future<List<BulletinComment>> getComments(String postId) async {
    await Future.delayed(const Duration(milliseconds: 300));
    return BulletinComment.getMockComments(postId);
  }

  Future<BulletinPost> createPost({
    required String title,
    required String body,
    required String category,
    List<String> imageUrls = const [],
  }) async {
    await Future.delayed(const Duration(milliseconds: 500));

    //Replace with actual API call
    return BulletinPost(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      title: title,
      body: body,
      category: category,
      authorId: 'currentUser',
      authorName: 'You',
      authorAvatar: 'Y',
      imageUrls: imageUrls,
      createdAt: DateTime.now(),
      isOwner: true,
    );
  }

  Future<void> addHelpful(String postId) async {
    await Future.delayed(const Duration(milliseconds: 300));
    // Replace with actual API call
  }

  Future<void> removeHelpful(String postId) async {
    await Future.delayed(const Duration(milliseconds: 300));
    // Replace with actual API call
  }

  Future<void> reportPost(String postId, String reason) async {
    await Future.delayed(const Duration(milliseconds: 300));
    //Replace with actual API call
  }

  Future<void> deletePost(String postId) async {
    await Future.delayed(const Duration(milliseconds: 300));
    //Replace with actual API call
  }

  Future<BulletinComment> addComment(String postId, String content) async {
    await Future.delayed(const Duration(milliseconds: 300));

    //Replace with actual API call
    return BulletinComment(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      content: content,
      authorId: 'currentUser',
      authorName: 'You',
      createdAt: DateTime.now(),
    );
  }
}