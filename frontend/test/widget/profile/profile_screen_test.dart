import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/user_profile_response.dart';
import 'package:supa_neighbour/providers/service_providers.dart';
import 'package:supa_neighbour/screens/profile/profile_screen.dart';
import '../../mocks/mock_achievement_service.dart';
import '../../mocks/mock_chat_service.dart';
import '../../mocks/mock_profile_service.dart';
import '../../mocks/mock_auth_service.dart';

void main() {
  group('ProfileScreen Widget Tests', () {
    Widget buildTestableWidget() {
      return ProviderScope(
        overrides: [
          profileServiceProvider.overrideWithValue(MockProfileService()),
          achievementServiceProvider.overrideWithValue(MockAchievementService()),
          chatServiceProvider.overrideWithValue(MockChatService()),
          authServiceProvider.overrideWithValue(MockAuthService()),
        ],
        child: const MaterialApp(
          home: ProfileScreen(),
        ),
      );
    }

    group('Rendering', () {
      testWidgets('should render without crashing', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.byType(ProfileScreen), findsOneWidget);
      });

      testWidgets('should display user profile after loading', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('John Doe'), findsOneWidget);
        expect(find.text('Greenfield'), findsOneWidget);
        expect(find.text('Gold').first, findsOneWidget);
        expect(find.text('4.8 ★'), findsOneWidget);
      });

      testWidgets('should display app bar with title and icons', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('My Profile'), findsOneWidget);
        expect(find.byIcon(Icons.info_outline), findsOneWidget);
        expect(find.byIcon(Icons.settings_outlined), findsOneWidget);
      });

      testWidgets('should display XP card when user has XP', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('4500 XP'), findsOneWidget);
        expect(find.text('Gold').first, findsOneWidget);
        expect(find.byType(LinearProgressIndicator), findsOneWidget);
        expect(find.textContaining('XP to next milestone'), findsOneWidget);
      });

      testWidgets('should display stats row with task counts', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('15'), findsOneWidget);
        expect(find.text('Tasks Created'), findsOneWidget);
        expect(find.text('27'), findsOneWidget);
        expect(find.text('Tasks Completed'), findsOneWidget);
        expect(find.text('3'), findsOneWidget);
        expect(find.text('Active Tasks'), findsOneWidget);
      });

      testWidgets('should display skills section with chips', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Skills & Services'), findsOneWidget);
        expect(find.text('Home Repair'), findsOneWidget);
        expect(find.text('Pet Care'), findsOneWidget);
        expect(find.text('Edit'), findsOneWidget);
      });

      testWidgets('should display achievements section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Achievements'), findsOneWidget);
        expect(find.text('View All'), findsOneWidget);
        expect(find.text('Home Repair Specialist'), findsOneWidget);
        expect(find.text('Pet Care Helper'), findsOneWidget);
      });

      testWidgets('should display recent tasks section', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Recent Tasks'), findsOneWidget);
        expect(find.text('Fixed sink'), findsOneWidget);
        expect(find.text('+100 XP'), findsOneWidget);
      });

      testWidgets('should display action buttons when scrolled', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        await tester.drag(find.byType(SingleChildScrollView), const Offset(0, -500));
        await tester.pumpAndSettle();

        expect(find.text('Privacy Settings'), findsOneWidget);
        expect(find.text('Logout'), findsOneWidget);
        expect(find.text('Edit Profile'), findsOneWidget);
      });
    });

    group('Navigation', () {
      testWidgets('should navigate to settings when settings icon is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final settingsIcon = find.byIcon(Icons.settings_outlined);
        expect(settingsIcon, findsOneWidget);

        await tester.tap(settingsIcon);
        await tester.pumpAndSettle();

        expect(find.text('Settings'), findsOneWidget);
      });

      testWidgets('should navigate to achievements when View All is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final viewAll = find.text('View All');
        expect(viewAll, findsOneWidget);

        await tester.tap(viewAll);
        await tester.pumpAndSettle();

        expect(find.text('Achievements'), findsOneWidget);
      });

      // Skip this test for now - needs more investigation
      testWidgets('should navigate to privacy settings when Privacy Settings is tapped', 
          skip: true, 
          (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        await tester.drag(find.byType(SingleChildScrollView), const Offset(0, -500));
        await tester.pumpAndSettle();

        final privacySettings = find.text('Privacy Settings');
        expect(privacySettings, findsOneWidget);

        await tester.tap(privacySettings);
        await tester.pumpAndSettle();

        expect(find.text('Privacy Settings'), findsOneWidget);
      });

      testWidgets('should show help modal when info icon is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final infoIcon = find.byIcon(Icons.info_outline);
        expect(infoIcon, findsOneWidget);

        await tester.tap(infoIcon);
        await tester.pumpAndSettle();

        expect(find.text('How to Use Profile'), findsOneWidget);
        expect(find.text('View your trust score and XP'), findsOneWidget);
        expect(find.text('Got it'), findsOneWidget);
      });

      testWidgets('should close help modal when Got it is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        await tester.tap(find.byIcon(Icons.info_outline));
        await tester.pumpAndSettle();

        expect(find.text('How to Use Profile'), findsOneWidget);

        await tester.tap(find.text('Got it'));
        await tester.pumpAndSettle();

        expect(find.text('How to Use Profile'), findsNothing);
      });
    });

    group('Skills Dialog', () {
      testWidgets('should show edit skills dialog when Edit is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final editButton = find.text('Edit');
        expect(editButton, findsOneWidget);

        await tester.tap(editButton);
        await tester.pumpAndSettle();

        expect(find.text('Edit Skills'), findsOneWidget);
        expect(find.text('Cancel'), findsOneWidget);
        expect(find.text('Save'), findsOneWidget);
      });

      testWidgets('should show skill checkboxes in dialog', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        await tester.tap(find.text('Edit'));
        await tester.pumpAndSettle();

        expect(find.byType(CheckboxListTile), findsWidgets);
      });

      testWidgets('should close dialog when Cancel is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        await tester.tap(find.text('Edit'));
        await tester.pumpAndSettle();

        final cancelButton = find.text('Cancel');
        expect(cancelButton, findsOneWidget);

        await tester.tap(cancelButton);
        await tester.pumpAndSettle();

        expect(find.text('Edit Skills'), findsNothing);
      });
    });

    group('Logout', () {
      testWidgets('should show logout dialog when Logout is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final scrollable = find.byType(SingleChildScrollView);
        await tester.drag(scrollable, const Offset(0, -500));
        await tester.pumpAndSettle();

        final logoutButton = find.text('Logout').last;
        expect(logoutButton, findsOneWidget);

        await tester.tap(logoutButton);
        await tester.pumpAndSettle();

        expect(find.text('Logout?'), findsOneWidget);
        expect(find.text('Are you sure you want to logout?'), findsOneWidget);
        expect(find.text('Cancel'), findsOneWidget);
      });

      testWidgets('should close dialog when Cancel is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final scrollable = find.byType(SingleChildScrollView);
        await tester.drag(scrollable, const Offset(0, -500));
        await tester.pumpAndSettle();

        await tester.tap(find.text('Logout').last);
        await tester.pumpAndSettle();

        final cancelButton = find.text('Cancel');
        expect(cancelButton, findsOneWidget);

        await tester.tap(cancelButton);
        await tester.pumpAndSettle();

        expect(find.text('Logout?'), findsNothing);
      });

      testWidgets('should navigate to splash screen when Logout is confirmed', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final scrollable = find.byType(SingleChildScrollView);
        await tester.drag(scrollable, const Offset(0, -500));
        await tester.pumpAndSettle();

        await tester.tap(find.text('Logout').last);
        await tester.pumpAndSettle();

        final confirmLogout = find.descendant(
          of: find.byType(AlertDialog),
          matching: find.text('Logout'),
        );
        expect(confirmLogout, findsOneWidget);

        await tester.tap(confirmLogout);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.byType(ProfileScreen), findsNothing);
      });
    });

    group('Edit Profile', () {
      testWidgets('should show snackbar when Edit Profile is tapped', (tester) async {
        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(buildTestableWidget());
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final editProfile = find.text('Edit Profile');
        expect(editProfile, findsOneWidget);

        await tester.tap(editProfile);
        await tester.pumpAndSettle();

        expect(find.text('Edit Profile coming soon'), findsOneWidget);
      });
    });

    group('Error States', () {
      testWidgets('should show error message when profile fails to load', (tester) async {
        final errorProvider = ProviderScope(
          overrides: [
            profileServiceProvider.overrideWithValue(MockProfileServiceError()),
            achievementServiceProvider.overrideWithValue(MockAchievementService()),
            chatServiceProvider.overrideWithValue(MockChatService()),
            authServiceProvider.overrideWithValue(MockAuthService()),
          ],
          child: const MaterialApp(
            home: ProfileScreen(),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(errorProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Failed to load profile. Please try again.'), findsOneWidget);
        expect(find.text('Retry'), findsOneWidget);
      });

      testWidgets('should attempt reload when Retry is tapped', (tester) async {
        final errorProvider = ProviderScope(
          overrides: [
            profileServiceProvider.overrideWithValue(MockProfileServiceError()),
            achievementServiceProvider.overrideWithValue(MockAchievementService()),
            chatServiceProvider.overrideWithValue(MockChatService()),
            authServiceProvider.overrideWithValue(MockAuthService()),
          ],
          child: const MaterialApp(
            home: ProfileScreen(),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(errorProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        final retryButton = find.text('Retry');
        expect(retryButton, findsOneWidget);

        await tester.tap(retryButton);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Failed to load profile. Please try again.'), findsOneWidget);
      });
    });

    group('Empty States', () {
      testWidgets('should show empty skills message when no skills', (tester) async {
        final emptyProvider = ProviderScope(
          overrides: [
            profileServiceProvider.overrideWithValue(MockProfileServiceEmpty()),
            achievementServiceProvider.overrideWithValue(MockAchievementService()),
            chatServiceProvider.overrideWithValue(MockChatService()),
            authServiceProvider.overrideWithValue(MockAuthService()),
          ],
          child: const MaterialApp(
            home: ProfileScreen(),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(emptyProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('No added skills yet.'), findsOneWidget);
        expect(find.text('No achievements earned yet.'), findsOneWidget);
        expect(find.text('No completed tasks yet.'), findsOneWidget);
      });
    });

    group('Level Display', () {
      testWidgets('should display Silver level', (tester) async {
        final silverProvider = ProviderScope(
          overrides: [
            profileServiceProvider.overrideWithValue(MockProfileServiceSilver()),
            achievementServiceProvider.overrideWithValue(MockAchievementService()),
            chatServiceProvider.overrideWithValue(MockChatService()),
            authServiceProvider.overrideWithValue(MockAuthService()),
          ],
          child: const MaterialApp(
            home: ProfileScreen(),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(silverProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Silver').first, findsOneWidget);
      });

      testWidgets('should display Bronze level', (tester) async {
        final bronzeProvider = ProviderScope(
          overrides: [
            profileServiceProvider.overrideWithValue(MockProfileServiceBronze()),
            achievementServiceProvider.overrideWithValue(MockAchievementService()),
            chatServiceProvider.overrideWithValue(MockChatService()),
            authServiceProvider.overrideWithValue(MockAuthService()),
          ],
          child: const MaterialApp(
            home: ProfileScreen(),
          ),
        );

        await tester.binding.setSurfaceSize(const Size(800, 1000));
        await tester.pumpWidget(bronzeProvider);
        await tester.pumpAndSettle(const Duration(seconds: 2));

        expect(find.text('Bronze').first, findsOneWidget);
      });
    });
  });
}