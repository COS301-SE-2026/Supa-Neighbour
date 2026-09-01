import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supa_neighbour/screens/auth/signup_other_details_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/models/user_model.dart';

void main() {
  group('SignupOtherDetailsScreen', () {
    const testEmail = 'test@example.com';
    const testIdToken = 'test-id-token';
    const testPassword = 'password123';

    // Create a test user
    final testUser = User(
      id: '1',
      email: testEmail,
      firstName: 'John',
      lastName: 'Doe',
      phone: '1234567890',
      username: 'johndoe',
      birthday: DateTime(1990, 1, 1),
      gender: 'Male',
      street: '123 Main St',
      town: 'Pretoria',
      zipCode: '0001',
      createdAt: DateTime.now(),
    );

    // Helper to set larger test window
    void setLargeTestWindow(WidgetTester tester) {
      tester.binding.window.physicalSizeTestValue = const Size(1080, 2400);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
    }

    void resetTestWindow(WidgetTester tester) {
      tester.binding.window.clearPhysicalSizeTestValue();
      tester.binding.window.clearDevicePixelRatioTestValue();
    }

    testWidgets('displays logo placeholder', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Other Details" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Other Details'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Almost there!" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Almost there!'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Phone Number" label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Phone Number'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Username" label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Username'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays phone number input field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(TextField), findsNWidgets(2));
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Finish Profile" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Finish Profile'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays back button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('back button navigates back', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(SignupOtherDetailsScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final backButton = find.ancestor(
        of: find.byIcon(Icons.arrow_back),
        matching: find.byType(Container),
      ).first;
      
      final container = tester.widget<Container>(backButton);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color, const Color(0xFF2A9D8F));
      expect(decoration?.shape, BoxShape.circle);
      
      resetTestWindow(tester);
    });

    testWidgets('screen has white background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      expect(containerWidget.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('has SingleChildScrollView', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('phone number field has phone keyboard type', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final textFields = tester.widgetList<TextField>(find.byType(TextField)).toList();
      expect(textFields[0].keyboardType, TextInputType.phone);
      
      resetTestWindow(tester);
    });

    testWidgets('phone number field has hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your phone number'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('username field has hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter a unique username'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('phone number field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final phoneField = find.byType(TextField).first;
      await tester.enterText(phoneField, '0123456789');
      await tester.pump();

      final textField = tester.widget<TextField>(phoneField);
      expect(textField.controller?.text, '0123456789');
      
      resetTestWindow(tester);
    });

    testWidgets('username field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final usernameField = find.byType(TextField).last;
      await tester.enterText(usernameField, 'john_doe_123');
      await tester.pump();

      final textField = tester.widget<TextField>(usernameField);
      expect(textField.controller?.text, 'john_doe_123');
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when phone number is empty', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Finish Profile'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your phone number'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when username is empty', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter phone number first
      final phoneField = find.byType(TextField).first;
      await tester.enterText(phoneField, '0123456789');
      await tester.pump();

      await tester.tap(find.text('Finish Profile'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter a username'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Other Details');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Almost there!');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.color, const Color(0xFF6B7280));
      
      resetTestWindow(tester);
    });

    testWidgets('Finish Profile button has teal background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final finishButton = find.ancestor(
        of: find.text('Finish Profile'),
        matching: find.byType(Container),
      ).first;
      
      final container = tester.widget<Container>(finishButton);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('Finish Profile button text is white', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope(
          child: MaterialApp(
            home: SignupOtherDetailsScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final finishText = find.text('Finish Profile');
      final textWidget = tester.widget<Text>(finishText);
      expect(textWidget.style?.color, Colors.white);
      
      resetTestWindow(tester);
    });
  });
}