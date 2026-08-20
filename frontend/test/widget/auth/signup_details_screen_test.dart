import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/auth/signup_details_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/screens/auth/signup_residential_screen.dart';

void main() {
  group('SignupDetailsScreen', () {
    const testEmail = 'test@example.com';
    const testIdToken = 'test-id-token';
    const testPassword = 'password123';

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
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Complete Profile" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Complete Profile'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Tell us about yourself" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Tell us about yourself'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays email in non-editable container', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Email'), findsOneWidget);
      expect(find.text(testEmail), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "First Name" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('First Name'), findsOneWidget);
      expect(find.byType(TextField), findsNWidgets(2)); // First Name and Last Name
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Last Name" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Last Name'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Birthday" label and date picker', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Birthday'), findsOneWidget);
      expect(find.byIcon(Icons.calendar_today), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Gender" label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Gender'), findsOneWidget);
      // Look for the dropdown button by finding the DropdownButtonHideUnderline
      expect(find.byType(DropdownButtonHideUnderline), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Next" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
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
          home: SignupDetailsScreen(
            email: testEmail,
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
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(SignupDetailsScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
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
          home: SignupDetailsScreen(
            email: testEmail,
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
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('First Name field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final firstNameField = find.byType(TextField).first;
      await tester.enterText(firstNameField, 'John');
      await tester.pump();

      final textField = tester.widget<TextField>(firstNameField);
      expect(textField.controller?.text, 'John');
      
      resetTestWindow(tester);
    });

    testWidgets('Last Name field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final lastNameField = find.byType(TextField).last;
      await tester.enterText(lastNameField, 'Doe');
      await tester.pump();

      final textField = tester.widget<TextField>(lastNameField);
      expect(textField.controller?.text, 'Doe');
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when First Name is empty', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your first name'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Last Name is empty after filling First Name', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter first name
      final firstNameField = find.byType(TextField).first;
      await tester.enterText(firstNameField, 'John');
      await tester.pump();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your last name'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('navigates to SignupResidentialScreen when fields are filled', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Enter first name
      final firstNameField = find.byType(TextField).first;
      await tester.enterText(firstNameField, 'John');
      await tester.pump();

      // Enter last name
      final lastNameField = find.byType(TextField).last;
      await tester.enterText(lastNameField, 'Doe');
      await tester.pump();

      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.byType(SignupResidentialScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Complete Profile');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Tell us about yourself');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.color, const Color(0xFF6B7280));
      
      resetTestWindow(tester);
    });

    testWidgets('Next button has teal background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupDetailsScreen(
            email: testEmail,
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
          home: SignupDetailsScreen(
            email: testEmail,
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