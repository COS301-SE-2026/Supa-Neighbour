import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/screens/chat/inbox_screen.dart';
import '../../mocks/mock_chat_service.dart';

void main() {
  // Mock SharedPreferences
  TestWidgetsFlutterBinding.ensureInitialized();

  group('InboxScreen Widget Tests', () {
    late Widget testWidget;

    setUp(() async {
      // Setup SharedPreferences mock
      SharedPreferences.setMockInitialValues({
        'current_user_id': 1,
      });

      testWidget = ProviderScope(
        overrides: [
          chatServiceProvider.overrideWithValue(MockChatService()),
        ],
        child: const MaterialApp(
          home: InboxScreen(),
        ),
      );
    });

    testWidgets('should render without crashing', (tester) async {
      await tester.binding.setSurfaceSize(const Size(800, 800));
      await tester.pumpWidget(testWidget);
      await tester.pumpAndSettle();

      expect(find.byType(InboxScreen), findsOneWidget);
    });

    testWidgets('should display app bar with title', (tester) async {
      await tester.binding.setSurfaceSize(const Size(800, 800));
      await tester.pumpWidget(testWidget);
      await tester.pumpAndSettle();

      expect(find.text('Chat'), findsOneWidget);
    });

    testWidgets('should display TabBar with two tabs', (tester) async {
      await tester.binding.setSurfaceSize(const Size(800, 800));
      await tester.pumpWidget(testWidget);
      await tester.pumpAndSettle();

      expect(find.text('Inbox'), findsOneWidget);
      expect(find.text('Community Bulletin'), findsOneWidget);
    });

    testWidgets('should display inbox tab content after loading', (tester) async {
      await tester.binding.setSurfaceSize(const Size(800, 800));
      await tester.pumpWidget(testWidget);
      await tester.pumpAndSettle(const Duration(seconds: 2));

      // Check that the screen rendered
      expect(find.byType(InboxScreen), findsOneWidget);
    });
  });
}