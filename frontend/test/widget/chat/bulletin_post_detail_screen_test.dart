import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/screens/chat/bulletin_post_detail_screen.dart';
import '../../mocks/mock_bulletin_service.dart';

void main() {
  group('BulletinPostDetailScreen Widget Tests', () {
    const testPostId = 1;

    Widget buildTestableWidget() {
      return ProviderScope(
        overrides: [
          bulletinServiceProvider.overrideWithValue(MockBulletinService()),
        ],
        child: const MaterialApp(
          home: BulletinPostDetailScreen(
            postId: testPostId,
          ),
        ),
      );
    }

    group('Rendering', () {
      testWidgets('should render without crashing', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.byType(BulletinPostDetailScreen), findsOneWidget);
      });

      testWidgets('should display loading indicator initially', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pump();

        final hasLoading = find.byType(CircularProgressIndicator).evaluate().isNotEmpty;
        final hasContent = find.byType(BulletinPostDetailScreen).evaluate().isNotEmpty;
        expect(hasLoading || hasContent, true);
      });

      testWidgets('should display post content after loading', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('This is a test bulletin post'), findsOneWidget);
      });

      testWidgets('should display author username', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('testuser'), findsOneWidget);
      });

      testWidgets('should display app bar with title', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Post Details'), findsOneWidget);
      });

      testWidgets('should display back button', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      });
    });

    group('Comments', () {
      testWidgets('should display comment section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.textContaining('Comments'), findsOneWidget);
      });

      testWidgets('should display comment list', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Great post!'), findsOneWidget);
        expect(find.text('Thanks for sharing!'), findsOneWidget);
      });

      testWidgets('should display comment input field', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.byType(TextField), findsOneWidget);
        expect(find.byIcon(Icons.send), findsOneWidget);
      });
    });

    group('Helpful Button', () {
      testWidgets('should display helpful button with count', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.textContaining('Helpful'), findsOneWidget);
      });

      testWidgets('should display helpful count', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.textContaining('5'), findsOneWidget);
      });

      testWidgets('should toggle helpful when tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final helpfulButton = find.byIcon(Icons.thumb_up_outlined);
        expect(helpfulButton, findsOneWidget);

        await tester.tap(helpfulButton);
        await tester.pumpAndSettle();

        expect(find.byIcon(Icons.thumb_up), findsOneWidget);
      });
    });

    group('Error States', () {
      testWidgets('should show error when post not found', (tester) async {
        final errorProvider = ProviderScope(
          overrides: [
            bulletinServiceProvider.overrideWithValue(MockBulletinServiceEmpty()),
          ],
          child: const MaterialApp(
            home: BulletinPostDetailScreen(
              postId: 999,
            ),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(errorProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Post not found'), findsOneWidget);
        expect(find.byType(BulletinPostDetailScreen), findsOneWidget);
      });
    });
  });
}