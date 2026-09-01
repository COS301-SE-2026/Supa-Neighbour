import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/screens/chat/create_bulletin_post_screen.dart';
import '../../mocks/mock_bulletin_service.dart';

void main() {
  group('CreateBulletinPostScreen Widget Tests', () {
    Widget buildTestableWidget() {
      return ProviderScope(
        overrides: [
          bulletinServiceProvider.overrideWithValue(MockBulletinService()),
        ],
        child: const MaterialApp(
          home: CreateBulletinPostScreen(),
        ),
      );
    }

    group('Rendering', () {
      testWidgets('should render without crashing', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.byType(CreateBulletinPostScreen), findsOneWidget);
      });

      testWidgets('should display app bar with title', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Create Post'), findsOneWidget);
        expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      });

      testWidgets('should display post content input field', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Post Content'), findsOneWidget);
        expect(find.byType(TextField), findsOneWidget);
      });

      testWidgets('should display category dropdown', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Category'), findsOneWidget);
        expect(find.byType(DropdownButton<String>), findsOneWidget);
      });

      testWidgets('should display "Add Photos" section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Add Photos'), findsOneWidget);
        expect(find.text('Tap to add photos'), findsOneWidget);
      });

      testWidgets('should display POST button', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('POST'), findsOneWidget);
      });

      testWidgets('should display back button in app bar', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      });
    });

    group('Category Dropdown', () {
      testWidgets('should show categories when dropdown is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final dropdown = find.byType(DropdownButton<String>);
        expect(dropdown, findsOneWidget);

        await tester.tap(dropdown);
        await tester.pumpAndSettle();

        expect(find.text('Lost Pet'), findsOneWidget);
        expect(find.text('Local Event'), findsOneWidget);
        expect(find.text('Alert'), findsOneWidget);
        expect(find.text('Free Items'), findsOneWidget);
        expect(find.text('Complaint'), findsOneWidget);
        expect(find.text('Admin Announcement'), findsOneWidget);
      });

      testWidgets('should change selected category when tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final dropdown = find.byType(DropdownButton<String>);
        await tester.tap(dropdown);
        await tester.pumpAndSettle();

        await tester.tap(find.text('Lost Pet'));
        await tester.pumpAndSettle();

        expect(find.text('Lost Pet'), findsOneWidget);
      });
    });

    group('Photo Upload', () {
      testWidgets('should show add photo button when no photos selected', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Tap to add photos'), findsOneWidget);
        expect(find.byIcon(Icons.add_photo_alternate), findsOneWidget);
      });
    });

    group('Form Validation', () {
      testWidgets('should show error when submitting empty post', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final postButton = find.text('POST');
        expect(postButton, findsOneWidget);

        await tester.tap(postButton);
        await tester.pumpAndSettle();

        expect(find.text('Please enter post content'), findsOneWidget);
      });

      testWidgets('should enable POST button when content is entered', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        // Enter post content
        await tester.enterText(find.byType(TextField), 'Test post content');
        await tester.pumpAndSettle();

        // Button should be enabled (onTap should not be null)
        final postButton = find.text('POST');
        expect(postButton, findsOneWidget);
        
        // Tap the button (should not show error)
        await tester.tap(postButton);
        await tester.pumpAndSettle();

        // Error message should NOT appear
        expect(find.text('Please enter post content'), findsNothing);
      });
    });

    group('Navigation', () {
      testWidgets('should pop screen when back button is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final backButton = find.byIcon(Icons.arrow_back);
        expect(backButton, findsOneWidget);

        await tester.tap(backButton);
        await tester.pumpAndSettle();

        expect(find.byType(CreateBulletinPostScreen), findsNothing);
      });
    });
  });
}