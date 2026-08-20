// test/widget/auth/signup_residential_screen_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supa_neighbour/screens/auth/signup_residential_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/models/user_model.dart';
import 'package:supa_neighbour/screens/auth/signup_other_details_screen.dart';

void main() {
  group('SignupResidentialScreen', () {
    const testIdToken = 'test-id-token';
    const testPassword = 'password123';

    // Create a test user
    final testUser = User(
      id: '1',
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      phone: '1234567890',
      username: 'johndoe',
      birthday: DateTime(1990, 1, 1),
      gender: 'Male',
      street: '',
      town: '',
      zipCode: '',
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
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Residential Address" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Residential Address'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Where do you live?" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Where do you live?'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Street" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Street'), findsOneWidget);
      expect(find.byType(TextField), findsNWidgets(3));
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Town" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Town'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Zip Code" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Zip Code'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Street field has correct hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your street address'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Town field has correct hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your town'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Zip Code field exists', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Just verify the Zip Code label and field exist
      expect(find.text('Zip Code'), findsOneWidget);
      // There should be 3 text fields (Street, Town, Zip Code)
      expect(find.byType(TextField), findsNWidgets(3));
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Next" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Next'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays back button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
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
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(SignupResidentialScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
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
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
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
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Street field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final streetField = find.byType(TextField).first;
      await tester.enterText(streetField, '123 Main Street');
      await tester.pump();

      final textField = tester.widget<TextField>(streetField);
      expect(textField.controller?.text, '123 Main Street');
      
      resetTestWindow(tester);
    });

    testWidgets('Town field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final townField = find.byType(TextField).at(1);
      await tester.enterText(townField, 'Pretoria');
      await tester.pump();

      final textField = tester.widget<TextField>(townField);
      expect(textField.controller?.text, 'Pretoria');
      
      resetTestWindow(tester);
    });

    testWidgets('Zip Code field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final zipField = find.byType(TextField).last;
      await tester.enterText(zipField, '0001');
      await tester.pump();

      final textField = tester.widget<TextField>(zipField);
      expect(textField.controller?.text, '0001');
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Street is empty', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your street address'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Town is empty after filling Street', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter street
      final streetField = find.byType(TextField).first;
      await tester.enterText(streetField, '123 Main Street');
      await tester.pump();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your town/city'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Zip Code is empty after filling Street and Town', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter street
      final streetField = find.byType(TextField).first;
      await tester.enterText(streetField, '123 Main Street');
      await tester.pump();

      // Enter town
      final townField = find.byType(TextField).at(1);
      await tester.enterText(townField, 'Pretoria');
      await tester.pump();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your zip code'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Uses ProviderScope because SignupOtherDetailsScreen is a ConsumerStatefulWidget
    testWidgets('navigates to SignupOtherDetailsScreen when all fields are filled', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope( // ← Add ProviderScope wrapper
          child: MaterialApp(
            home: SignupResidentialScreen(
              user: testUser,
              idToken: testIdToken,
              password: testPassword,
            ),
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter street
      final streetField = find.byType(TextField).first;
      await tester.enterText(streetField, '123 Main Street');
      await tester.pump();

      // Enter town
      final townField = find.byType(TextField).at(1);
      await tester.enterText(townField, 'Pretoria');
      await tester.pump();

      // Enter zip code
      final zipField = find.byType(TextField).last;
      await tester.enterText(zipField, '0001');
      await tester.pump();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.byType(SignupOtherDetailsScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Residential Address');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Where do you live?');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.color, const Color(0xFF6B7280));
      
      resetTestWindow(tester);
    });

    testWidgets('Next button has teal background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final nextButton = find.ancestor(
        of: find.text('Next'),
        matching: find.byType(Container),
      ).first;
      
      final container = tester.widget<Container>(nextButton);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('Next button text is white', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupResidentialScreen(
            user: testUser,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final nextText = find.text('Next');
      final textWidget = tester.widget<Text>(nextText);
      expect(textWidget.style?.color, Colors.white);
      
      resetTestWindow(tester);
    });
  });
}