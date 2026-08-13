import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/auth/signup_otp_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';

void main() {
  group('SignupOtpScreen', () {
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
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Super Neighbour" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.text('Super Neighbour'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Your neighbourly helper" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Verify Email" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.text('Verify Email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays email envelope icon', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.byIcon(Icons.mark_email_unread_outlined), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays user email address', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.text(testEmail), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays verification message', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(
        find.textContaining('Click the link in the email to continue.'),
        findsOneWidget,
      );
      
      resetTestWindow(tester);
    });

    testWidgets('displays loading spinner', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Resend email" link', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.text('Resend email'), findsOneWidget);
      expect(find.text('Didn\'t receive it? '), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays back button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('back button navigates back', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.byType(SignupOtpScreen), findsNothing);
      
      resetTestWindow(tester);
    });

    testWidgets('has white background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      expect(containerWidget.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('has SingleChildScrollView', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays email in bold and centered', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
        ),
      );

      await tester.pump();

      final emailText = tester.widget<Text>(find.text(testEmail));
      expect(emailText.textAlign, TextAlign.center);
      expect(emailText.style?.fontWeight, FontWeight.w600);
      
      resetTestWindow(tester);
    });

    testWidgets('back button has teal circle background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: SignupOtpScreen(
            email: testEmail,
            idToken: testIdToken,
            password: testPassword,
          ),
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