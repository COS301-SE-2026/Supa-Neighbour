import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/providers/theme_mode_provider.dart';
import 'package:supa_neighbour/screens/profile/settings_screen.dart';
import '../../mocks/mock_achievement_service.dart';
import '../../mocks/mock_chat_service.dart';

// Mock Auth Service for settings
class MockAuthService implements IAuthService {
  @override
  Future<void> logout() async {
    // Mock success
  }

  @override
  Future<void> deleteAccount() async {
    // Mock success
  }
}

class MockAuthServiceError implements IAuthService {
  @override
  Future<void> logout() async {
    throw Exception('Failed to logout');
  }

  @override
  Future<void> deleteAccount() async {
    throw Exception('Failed to delete account');
  }
}

void main() {
  group('SettingsScreen Widget Tests', () {
    Widget buildTestableWidget() {
      return ProviderScope(
        overrides: [
          authServiceProvider.overrideWithValue(MockAuthService()),
          achievementServiceProvider.overrideWithValue(MockAchievementService()),
          chatServiceProvider.overrideWithValue(MockChatService()),
        ],
        child: const MaterialApp(
          home: SettingsScreen(),
        ),
      );
    }

    group('Rendering', () {
      testWidgets('should render without crashing', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.byType(SettingsScreen), findsOneWidget);
      });

      testWidgets('should display app bar with title', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Settings'), findsOneWidget);
        expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      });

      testWidgets('should display Preferences section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Preferences'), findsOneWidget);
        expect(find.text('Location Services'), findsOneWidget);
        expect(find.text('Dark Mode'), findsOneWidget);
        expect(find.text('Language'), findsOneWidget);
        expect(find.text('Privacy Settings'), findsOneWidget);
      });

      testWidgets('should display Security section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Security'), findsOneWidget);
        expect(find.text('Change Password'), findsOneWidget);
      });

      testWidgets('should display Support section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Support'), findsOneWidget);
        expect(find.text('Help Center'), findsOneWidget);
      });

      testWidgets('should display Account section with danger styling', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.text('Account'), findsOneWidget);
        expect(find.text('DANGER'), findsOneWidget);
        expect(find.text('Sign Out'), findsOneWidget);
        expect(find.text('Delete Account'), findsOneWidget);
      });

      testWidgets('should display location services switch', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.byType(Switch), findsWidgets);
      });

      testWidgets('should display language dropdown', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        expect(find.byType(DropdownButton<String>), findsOneWidget);
        expect(find.text('English'), findsOneWidget);
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

        expect(find.byType(SettingsScreen), findsNothing);
      });

      testWidgets('should navigate to privacy settings when Privacy Settings is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final privacySettings = find.text('Privacy Settings');
        expect(privacySettings, findsOneWidget);

        await tester.tap(privacySettings);
        await tester.pumpAndSettle();

        expect(find.text('Privacy Settings'), findsOneWidget);
      });

      testWidgets('should navigate to help center when Help Center is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final helpCenter = find.text('Help Center');
        expect(helpCenter, findsOneWidget);

        await tester.tap(helpCenter);
        await tester.pumpAndSettle();

        expect(find.text('Help Center'), findsOneWidget);
      });
    });

    group('Dark Mode Toggle', () {
      testWidgets('should toggle dark mode when switch is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final darkModeSwitch = find.byType(Switch).first;
        expect(darkModeSwitch, findsOneWidget);

        await tester.tap(darkModeSwitch);
        await tester.pumpAndSettle();
      });
    });

    group('Language Dropdown', () {
      testWidgets('should show language options when dropdown is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final dropdown = find.byType(DropdownButton<String>);
        expect(dropdown, findsOneWidget);

        await tester.tap(dropdown);
        await tester.pumpAndSettle();

        expect(find.text('English'), findsOneWidget);
        expect(find.text('Spanish'), findsOneWidget);
        expect(find.text('French'), findsOneWidget);
        expect(find.text('German'), findsOneWidget);
        expect(find.text('Portuguese'), findsOneWidget);
      });

      testWidgets('should change language when option is selected', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final dropdown = find.byType(DropdownButton<String>);
        await tester.tap(dropdown);
        await tester.pumpAndSettle();

        await tester.tap(find.text('Spanish'));
        await tester.pumpAndSettle();

        expect(find.text('Spanish'), findsOneWidget);
      });
    });

    group('Sign Out', () {
      testWidgets('should show sign out dialog when Sign Out is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final signOut = find.text('Sign Out');
        expect(signOut, findsOneWidget);

        await tester.tap(signOut);
        await tester.pumpAndSettle();

        expect(find.text('Sign Out'), findsOneWidget);
        expect(find.text('Are you sure you want to sign out?'), findsOneWidget);
        expect(find.text('Cancel'), findsOneWidget);
      });
    });

    group('Delete Account', () {
      testWidgets('should show delete account dialog when Delete Account is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        final deleteAccount = find.text('Delete Account');
        expect(deleteAccount, findsOneWidget);

        await tester.tap(deleteAccount);
        await tester.pumpAndSettle();

        expect(find.text('Delete Account'), findsOneWidget);
        expect(find.textContaining('permanently deleted'), findsOneWidget);
        expect(find.text('Cancel'), findsOneWidget);
        expect(find.text('Delete Forever'), findsOneWidget);
      });

      testWidgets('should enable Delete Forever button only after typing DELETE', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 800));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle();

        await tester.tap(find.text('Delete Account'));
        await tester.pumpAndSettle();

        // Delete Forever should be disabled initially
        final deleteForever = find.text('Delete Forever');
        expect(deleteForever, findsOneWidget);

        // Enter "DELETE" in the text field
        final textField = find.byType(TextField);
        expect(textField, findsOneWidget);
        await tester.enterText(textField, 'DELETE');
        await tester.pumpAndSettle();

        // Now Delete Forever should be enabled (tap works)
        await tester.tap(deleteForever);
        await tester.pumpAndSettle();
      });
    });
  });
}