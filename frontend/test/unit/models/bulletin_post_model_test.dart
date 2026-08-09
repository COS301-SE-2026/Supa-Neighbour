import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/bulletin_post_model.dart';

void main() {
  group('BulletinPost Model Unit Tests', () {
    final now = DateTime.now();
    final later = now.add(const Duration(hours: 1));
    const currentUserId = 123;

    late BulletinPost testPost;

    setUp(() {
      testPost = BulletinPost(
        id: 1,
        postContent: 'This is a test bulletin post',
        mediaUrl: 'https://example.com/image.jpg',
        category: 'general',
        authorId: 123,
        authorUsername: 'testuser',
        likeCount: 5,
        dislikeCount: 2,
        commentCount: 3,
        createdAt: now,
        updatedAt: later,
        isOwner: true,
        isHelpfulByUser: false,
      );
    });

    group('Properties and Getters', () {
      test('should have correct initial values', () {
        expect(testPost.id, 1);
        expect(testPost.postContent, 'This is a test bulletin post');
        expect(testPost.mediaUrl, 'https://example.com/image.jpg');
        expect(testPost.category, 'general');
        expect(testPost.authorId, 123);
        expect(testPost.authorUsername, 'testuser');
        expect(testPost.likeCount, 5);
        expect(testPost.dislikeCount, 2);
        expect(testPost.commentCount, 3);
        expect(testPost.createdAt, now);
        expect(testPost.updatedAt, later);
        expect(testPost.isOwner, true);
        expect(testPost.isHelpfulByUser, false);
      });

      test('authorAvatar should return first letter of username', () {
        expect(testPost.authorAvatar, 'T');
      });

      test('authorAvatar should return "?" when username is empty', () {
        final post = BulletinPost(
          id: 2,
          postContent: 'Test',
          category: 'general',
          authorId: 2,
          authorUsername: '',
          createdAt: now,
          updatedAt: later,
        );
        expect(post.authorAvatar, '?');
      });
    });

    group('fromJson', () {
      test('should create BulletinPost from valid JSON', () {
        final json = {
          'postId': 10,
          'postContent': 'Lost my dog, please help!',
          'mediaUrl': 'https://example.com/dog.jpg',
          'category': 'lost_pet',
          'userId': 456,
          'authorUsername': 'petlover',
          'likeCount': 10,
          'dislikeCount': 1,
          'commentCount': 4,
          'createdAt': now.toIso8601String(),
          'updatedAt': later.toIso8601String(),
        };

        final post = BulletinPost.fromJson(json, 456);

        expect(post.id, 10);
        expect(post.postContent, 'Lost my dog, please help!');
        expect(post.mediaUrl, 'https://example.com/dog.jpg');
        expect(post.category, 'lost_pet');
        expect(post.authorId, 456);
        expect(post.authorUsername, 'petlover');
        expect(post.likeCount, 10);
        expect(post.dislikeCount, 1);
        expect(post.commentCount, 4);
        expect(post.createdAt, now);
        expect(post.updatedAt, later);
        expect(post.isOwner, true);
        expect(post.isHelpfulByUser, false);
      });

      test('should handle null values gracefully', () {
        final json = {
          'postId': 11,
          'userId': 0, // Provide a valid int instead of null
          'createdAt': now.toIso8601String(),
          'updatedAt': later.toIso8601String(),
        };

        final post = BulletinPost.fromJson(json, 789);

        expect(post.id, 11);
        expect(post.postContent, '');
        expect(post.mediaUrl, null);
        expect(post.category, 'general');
        expect(post.authorId, 0);
        expect(post.authorUsername, '');
        expect(post.likeCount, 0);
        expect(post.dislikeCount, 0);
        expect(post.commentCount, 0);
        expect(post.isOwner, false);
        expect(post.isHelpfulByUser, false);
      });

      test('should parse numeric values correctly', () {
        final json = {
          'postId': 12,
          'userId': 1, // Required for authorId
          'likeCount': 15,
          'dislikeCount': 3,
          'commentCount': 7,
          'createdAt': now.toIso8601String(),
          'updatedAt': later.toIso8601String(),
        };

        final post = BulletinPost.fromJson(json, 1);

        expect(post.likeCount, 15);
        expect(post.dislikeCount, 3);
        expect(post.commentCount, 7);
      });

      test('should set isOwner correctly based on currentUserId', () {
        final json = {
          'postId': 13,
          'userId': 999,
          'createdAt': now.toIso8601String(),
          'updatedAt': later.toIso8601String(),
        };

        final post = BulletinPost.fromJson(json, 999);
        expect(post.isOwner, true);

        final postNotOwner = BulletinPost.fromJson(json, 888);
        expect(postNotOwner.isOwner, false);
      });
    });

    group('copyWith', () {
      test('should create new post with updated isHelpfulByUser', () {
        final updatedPost = testPost.copyWith(
          isHelpfulByUser: true,
        );

        expect(updatedPost.id, testPost.id);
        expect(updatedPost.isHelpfulByUser, true);
        expect(updatedPost.likeCount, testPost.likeCount);
        expect(updatedPost.postContent, testPost.postContent);
      });

      test('should create new post with updated likeCount', () {
        final updatedPost = testPost.copyWith(
          likeCount: 20,
        );

        expect(updatedPost.likeCount, 20);
        expect(updatedPost.id, testPost.id);
        expect(updatedPost.isHelpfulByUser, testPost.isHelpfulByUser);
      });

      test('should create new post with both fields updated', () {
        final updatedPost = testPost.copyWith(
          isHelpfulByUser: true,
          likeCount: 25,
        );

        expect(updatedPost.isHelpfulByUser, true);
        expect(updatedPost.likeCount, 25);
        expect(updatedPost.id, testPost.id);
        expect(updatedPost.postContent, testPost.postContent);
      });

      test('should preserve original values when no updates provided', () {
        final updatedPost = testPost.copyWith();

        expect(updatedPost.id, testPost.id);
        expect(updatedPost.postContent, testPost.postContent);
        expect(updatedPost.mediaUrl, testPost.mediaUrl);
        expect(updatedPost.category, testPost.category);
        expect(updatedPost.authorId, testPost.authorId);
        expect(updatedPost.authorUsername, testPost.authorUsername);
        expect(updatedPost.likeCount, testPost.likeCount);
        expect(updatedPost.dislikeCount, testPost.dislikeCount);
        expect(updatedPost.commentCount, testPost.commentCount);
        expect(updatedPost.createdAt, testPost.createdAt);
        expect(updatedPost.updatedAt, testPost.updatedAt);
        expect(updatedPost.isOwner, testPost.isOwner);
        expect(updatedPost.isHelpfulByUser, testPost.isHelpfulByUser);
      });
    });

    group('Edge Cases', () {
      test('should handle posts with very long content', () {
        final longContent = 'a' * 10000;
        final post = BulletinPost(
          id: 99,
          postContent: longContent,
          category: 'general',
          authorId: 1,
          authorUsername: 'longuser',
          createdAt: now,
          updatedAt: later,
        );

        expect(post.postContent.length, 10000);
      });

      test('should handle empty mediaUrl', () {
        final post = BulletinPost(
          id: 100,
          postContent: 'Test',
          mediaUrl: '',
          category: 'general',
          authorId: 1,
          authorUsername: 'user',
          createdAt: now,
          updatedAt: later,
        );

        expect(post.mediaUrl, '');
      });
    });
  });
}