import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/auth/forgot_password_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';

void main() {
  group('ForgotPasswordScreen', () {
    const testEmail = 'test@example.com';

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

      await tester.pump();

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

      await tester.pump();

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

      await tester.pump();

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

      await tester.pump();

      expect(find.text('Forgot Password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays email input field', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      expect(find.byType(TextField), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Send Reset Link" button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      expect(find.text('Send Reset Link'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Back" link', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      expect(find.text('Back'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays explanation text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      expect(
        find.textContaining('Enter your email and we\'ll send you a link'),
        findsOneWidget,
      );
      
      resetTestWindow(tester);
    });

    testWidgets('back button navigates back', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(ForgotPasswordScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('has white background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      expect(containerWidget.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const MaterialApp(
          home: ForgotPasswordScreen(),
        ),
      );

      await tester.pump();

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
  });
}