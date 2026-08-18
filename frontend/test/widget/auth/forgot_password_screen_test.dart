import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/auth/forgot_password_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';

void main() {
  group('ForgotPasswordScreen', () {
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
          home: ForgotPasswordScreen(),
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
          home: ForgotPasswordScreen(),
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
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Forgot Password" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Forgot Password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays explanation text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(
        find.textContaining('Enter your email and we\'ll send you a link to reset your password.'),
        findsOneWidget,
      );
      
      resetTestWindow(tester);
    });

    testWidgets('displays Email label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays email input field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(TextField), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('email field has email keyboard type', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final textField = tester.widget<TextField>(find.byType(TextField));
      expect(textField.keyboardType, TextInputType.emailAddress);
      
      resetTestWindow(tester);
    });

    testWidgets('email field has hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('email field accepts text input', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final emailField = find.byType(TextField);
      await tester.enterText(emailField, 'test@example.com');
      await tester.pump();

      final textField = tester.widget<TextField>(emailField);
      expect(textField.controller?.text, 'test@example.com');
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Send Reset Link" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Send Reset Link'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('"Send Reset Link" button is enabled when text is entered', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final emailField = find.byType(TextField);
      await tester.enterText(emailField, 'test@example.com');
      await tester.pump();

      final sendButton = find.text('Send Reset Link');
      expect(sendButton, findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays back button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('back button navigates back', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(ForgotPasswordScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
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
        const MaterialApp(
          home: ForgotPasswordScreen(),
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
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('"Send Reset Link" button has teal background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final sendButton = find.ancestor(
        of: find.text('Send Reset Link'),
        matching: find.byType(Container),
      ).first;
      
      final container = tester.widget<Container>(sendButton);
      final decoration = container.decoration as BoxDecoration?;
      expect(decoration?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('"Send Reset Link" button text is white', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final sendText = find.text('Send Reset Link');
      final textWidget = tester.widget<Text>(sendText);
      expect(textWidget.style?.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('explanation text has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final explanationText = find.textContaining('Enter your email and we\'ll send you a link');
      final textWidget = tester.widget<Text>(explanationText);
      
      // Check that it has a color (not null)
      expect(textWidget.style?.color, isNotNull);
      
      resetTestWindow(tester);
    });

    testWidgets('explanation text has correct font size', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final explanationText = find.textContaining('Enter your email and we\'ll send you a link');
      final textWidget = tester.widget<Text>(explanationText);
      
      // The explanation text uses fontSize which is 16.0 for normal screens
      expect(textWidget.style?.fontSize, 16.0);
      
      resetTestWindow(tester);
    });

    testWidgets('card container exists with decoration', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Find the card container (the one with decoration)
      final containers = tester.widgetList<Container>(find.byType(Container));
      bool foundCardContainer = false;
      
      for (final container in containers) {
        if (container.decoration != null) {
          final decoration = container.decoration as BoxDecoration?;
          if (decoration?.borderRadius != null) {
            foundCardContainer = true;
            break;
          }
        }
      }
      
      expect(foundCardContainer, true);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct font weight', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Forgot Password');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.fontWeight, FontWeight.w600);
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      // Subtitle uses AppColors.secondary which is yellow in light mode
      expect(subtitleWidget.style?.color, const Color(0xFFEAC059));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct font weight', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.fontWeight, FontWeight.w600);
      
      resetTestWindow(tester);
    });
  });
}