// test/widget/auth/signup_screen_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supa_neighbour/screens/auth/signup_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/screens/auth/login_screen.dart';

void main() {
  group('SignupScreen', () {
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
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Super Neighbour" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Super Neighbour'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Your neighbourly helper" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Sign Up" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Sign Up'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Email" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Email'), findsOneWidget);
      expect(find.byType(TextField), findsNWidgets(3));
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Password" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Confirm password" label and field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Confirm password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Email field has correct hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Both Password and Confirm Password fields have "Enter your password" hint
    testWidgets('Password fields have correct hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Both Password and Confirm Password fields have "Enter your password" hint
      expect(find.text('Enter your password'), findsNWidgets(2));
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Both Password and Confirm Password have visibility icons
    testWidgets('Password fields have visibility toggle icons', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Both Password and Confirm Password fields have visibility icons
      expect(find.byIcon(Icons.visibility_off), findsNWidgets(2));
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Sign up" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Sign up'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Already a neighbour? Login" link', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Already a neighbour? '), findsOneWidget);
      expect(find.text('Login'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Uses ProviderScope because LoginScreen is a ConsumerStatefulWidget
    testWidgets('tapping "Login" navigates to LoginScreen', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        ProviderScope( // ← Add ProviderScope wrapper
          child: MaterialApp(
            home: SignupScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Login'));
      await tester.pumpAndSettle();

      expect(find.byType(LoginScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('screen has white background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
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
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Email field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final emailField = find.byType(TextField).first;
      await tester.enterText(emailField, 'test@example.com');
      await tester.pump();

      final textField = tester.widget<TextField>(emailField);
      expect(textField.controller?.text, 'test@example.com');
      
      resetTestWindow(tester);
    });

    testWidgets('Password field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final passwordField = find.byType(TextField).at(1);
      await tester.enterText(passwordField, 'password123');
      await tester.pump();

      final textField = tester.widget<TextField>(passwordField);
      expect(textField.controller?.text, 'password123');
      
      resetTestWindow(tester);
    });

    testWidgets('Confirm password field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final confirmField = find.byType(TextField).last;
      await tester.enterText(confirmField, 'password123');
      await tester.pump();

      final textField = tester.widget<TextField>(confirmField);
      expect(textField.controller?.text, 'password123');
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Email is empty', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Sign up'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when Password is empty after filling Email', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Enter email
      final emailField = find.byType(TextField).first;
      await tester.enterText(emailField, 'test@example.com');
      await tester.pump();

      await tester.tap(find.text('Sign up'));
      await tester.pumpAndSettle();

      expect(find.text('Please enter your password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('shows snackbar when passwords do not match', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Enter email
      final emailField = find.byType(TextField).first;
      await tester.enterText(emailField, 'test@example.com');
      await tester.pump();

      // Enter password
      final passwordField = find.byType(TextField).at(1);
      await tester.enterText(passwordField, 'password123');
      await tester.pump();

      // Enter different confirm password
      final confirmField = find.byType(TextField).last;
      await tester.enterText(confirmField, 'password456');
      await tester.pump();

      await tester.tap(find.text('Sign up'));
      await tester.pumpAndSettle();

      expect(find.text('Passwords do not match'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.color, const Color(0xFFEAC059));
      
      resetTestWindow(tester);
    });

    testWidgets('Sign up button has teal background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final signupButton = find.ancestor(
        of: find.text('Sign up'),
        matching: find.byType(Container),
      ).first;
      
      final container = tester.widget<Container>(signupButton);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('Sign up button text is white', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: SignupScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final signupText = find.text('Sign up');
      final textWidget = tester.widget<Text>(signupText);
      expect(textWidget.style?.color, Colors.white);
      
      resetTestWindow(tester);
    });
  });
}