import 'package:dio/dio.dart';
import 'package:firebase_auth/firebase_auth.dart' as fb;
import 'package:shared_preferences/shared_preferences.dart';
import 'package:image_picker/image_picker.dart';
import '../models/bulletin_post_model.dart';
import '../models/bulletin_comment_model.dart';

class BulletinService {
  final Dio _dio;

  BulletinService({Dio? dio})
      : _dio = dio ??
            Dio(BaseOptions(
              baseUrl: 'https://parsebackend-cxgda4a7dthma8bt.southafricanorth-01.azurewebsites.net/',
              connectTimeout: const Duration(seconds: 30),
              receiveTimeout: const Duration(seconds: 30),
            ));

  
  Future<String> _getToken() async {
    final fbUser = fb.FirebaseAuth.instance.currentUser;
    if (fbUser == null) throw Exception('Not authenticated');
    final token = await fbUser.getIdToken();
    return token!;
  }

  Future<int> _getCurrentUserId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt('current_user_id') ?? 0;
  }

  // Get post from /api/bulletin/posts with optional category and search params
  Future<List<BulletinPost>> getPosts({
    String? category,
    String? search,
    int page = 1,
    int limit = 20,
  }) async {
    final token = await _getToken();
    final currentUserId = await _getCurrentUserId();
    final params = <String, dynamic>{'page': page, 'limit': limit};
    if (category != null) params['category'] = category;
    if (search != null && search.isNotEmpty) params['search'] = search;

    final res = await _dio.get(
      '/api/bulletin/posts',
      queryParameters: params,
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );

    final data = res.data as Map<String, dynamic>;
    final posts = data['posts'] as List<dynamic>;
    return posts
        .map((p) => BulletinPost.fromJson(p as Map<String, dynamic>, currentUserId))
        .toList();
  }

  // Get a single post by id from /api/bulletin/posts/{postId}
  Future<BulletinPost> getPost(int postId) async {
    final token = await _getToken();
    final currentUserId = await _getCurrentUserId();
    final res = await _dio.get(
      '/api/bulletin/posts/$postId',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
    return BulletinPost.fromJson(res.data as Map<String, dynamic>, currentUserId);
  }

  // Get comments for a post from /api/bulletin/posts/{postId}
  Future<List<BulletinComment>> getComments(int postId) async {
    final token = await _getToken();
    final res = await _dio.get(
      '/api/bulletin/posts/$postId',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
    final data = res.data as Map<String, dynamic>;
    final comments = data['comments'] as List<dynamic>? ?? [];
    return comments
        .map((c) => BulletinComment.fromJson(c as Map<String, dynamic>))
        .toList();
  }

  // Upload an image to /api/upload/image and return the image URL
  Future<String?> uploadImage(XFile imageFile) async {
    final token = await _getToken();
    final bytes = await imageFile.readAsBytes();
    final formData = FormData.fromMap({
      'file': MultipartFile.fromBytes(
        bytes,
        filename: imageFile.name,
      ),
    });
    final res = await _dio.post(
      '/api/upload/image',
      data: formData,
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
    return (res.data as Map<String, dynamic>)['imageUrl'] as String?;
  }

  // Create a new post to /api/bulletin/posts
  Future<void> createPost({
    required String postContent,
    required String category,
    String? mediaUrl,
  }) async {
    final token = await _getToken();
    await _dio.post(
      '/api/bulletin/posts',
      data: {
        'postContent': postContent,
        'category': category,
        if (mediaUrl != null) 'mediaUrl': mediaUrl,
      },
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
  }

  // Delete a post by id from /api/bulletin/posts/{postId}
  Future<void> deletePost(int postId) async {
    final token = await _getToken();
    await _dio.delete(
      '/api/bulletin/posts/$postId',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
  }

  // Add a comment to a post by id from /api/comments/bulletin/{postId}
  Future<BulletinComment> addComment(int postId, String content) async {
    final token = await _getToken();
    final res = await _dio.post(
      '/api/comments/bulletin/$postId',
      data: {'commentContent': content},
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
    final data = res.data as Map<String, dynamic>;
    return BulletinComment(
      id: data['commentId'] as int,
      content: data['commentContent'] as String? ?? content,
      authorId: data['userId'] as int? ?? 0,
      authorName: data['authorUsername'] as String? ?? 'You',
      createdAt: data['createdAt'] != null
          ? DateTime.parse(data['createdAt'] as String)
          : DateTime.now(),
    );
  }

  // Add a helpful (like) to a post by id from /api/comments/bulletin/posts/{postId}/like
  Future<void> addHelpful(int postId) async {
    final token = await _getToken();
    await _dio.post(
      '/api/comments/bulletin/posts/$postId/like',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
  }

  // Delete a comment by id from /api/comments/bulletin/{commentId}
  Future<void> removeHelpful(int postId) async {
    final token = await _getToken();
    await _dio.delete(
      '/api/comments/bulletin/posts/$postId/like',
      options: Options(headers: {'Authorization': 'Bearer $token'}),
    );
  }
}