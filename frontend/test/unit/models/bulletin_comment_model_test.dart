import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/bulletin_comment_model.dart';

void main() {
  group('BulletinComment Model Unit Tests', () {
    final now = DateTime.now();

    late BulletinComment testComment;

    setUp(() {
      testComment = BulletinComment(
        id: 1,
        content: 'This is a test comment',
        authorId: 123,
        authorName: 'testuser',
        parentCommentId: null,
        createdAt: now,
      );
    });

    group('Properties', () {
      test('should have correct initial values', () {
        expect(testComment.id, 1);
        expect(testComment.content, 'This is a test comment');
        expect(testComment.authorId, 123);
        expect(testComment.authorName, 'testuser');
        expect(testComment.parentCommentId, null);
        expect(testComment.createdAt, now);
      });

      test('should handle parentCommentId when provided', () {
        final commentWithParent = BulletinComment(
          id: 2,
          content: 'Reply to comment',
          authorId: 456,
          authorName: 'replyuser',
          parentCommentId: 1,
          createdAt: now,
        );

        expect(commentWithParent.parentCommentId, 1);
      });
    });

    group('fromJson', () {
      test('should create BulletinComment from valid JSON', () {
        final json = {
          'commentId': 10,
          'commentContent': 'Great post! Thanks for sharing.',
          'userId': 789,
          'authorUsername': 'helpfulneighbour',
          'parentCommentId': null,
          'createdAt': now.toIso8601String(),
        };

        final comment = BulletinComment.fromJson(json);

        expect(comment.id, 10);
        expect(comment.content, 'Great post! Thanks for sharing.');
        expect(comment.authorId, 789);
        expect(comment.authorName, 'helpfulneighbour');
        expect(comment.parentCommentId, null);
        expect(comment.createdAt, now);
      });

      test('should handle null content and username', () {
        final json = {
          'commentId': 11,
          'userId': 1,
          'commentContent': null,
          'authorUsername': null,
          'createdAt': now.toIso8601String(),
        };

        final comment = BulletinComment.fromJson(json);

        expect(comment.id, 11);
        expect(comment.content, '');
        expect(comment.authorId, 1);
        expect(comment.authorName, '');
        expect(comment.parentCommentId, null);
        expect(comment.createdAt, now);
      });

      test('should handle parentCommentId when present', () {
        final json = {
          'commentId': 12,
          'commentContent': 'Reply to your comment',
          'userId': 456,
          'authorUsername': 'replyuser',
          'parentCommentId': 5,
          'createdAt': now.toIso8601String(),
        };

        final comment = BulletinComment.fromJson(json);

        expect(comment.parentCommentId, 5);
      });

      test('should handle missing optional fields', () {
        final json = {
          'commentId': 13,
          'userId': 1,
          'createdAt': now.toIso8601String(),
        };

        final comment = BulletinComment.fromJson(json);

        expect(comment.id, 13);
        expect(comment.content, '');
        expect(comment.authorId, 1);
        expect(comment.authorName, '');
        expect(comment.parentCommentId, null);
      });
    });

    group('Edge Cases', () {
      test('should handle very long comment content', () {
        final longContent = 'a' * 10000;
        final comment = BulletinComment(
          id: 99,
          content: longContent,
          authorId: 1,
          authorName: 'longuser',
          parentCommentId: null,
          createdAt: now,
        );

        expect(comment.content.length, 10000);
      });

      test('should handle empty authorName', () {
        final comment = BulletinComment(
          id: 100,
          content: 'Test comment',
          authorId: 1,
          authorName: '',
          parentCommentId: null,
          createdAt: now,
        );

        expect(comment.authorName, '');
      });

      test('should handle deeply nested parentCommentId', () {
        final comment = BulletinComment(
          id: 101,
          content: 'Deeply nested reply',
          authorId: 2,
          authorName: 'deepuser',
          parentCommentId: 50,
          createdAt: now,
        );

        expect(comment.parentCommentId, 50);
      });
    });
  });
}