import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:supa_neighbour/screens/auth/login_screen.dart';
import 'package:supa_neighbour/screens/auth/signup_screen.dart';
import 'package:supa_neighbour/screens/auth/forgot_password_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/services/auth_service.dart';

import 'login_screen_test.mocks.dart';

@GenerateMocks([AuthService])
void main() {
  late MockAuthService mockAuthService;

  setUp(() async {
    mockAuthService = MockAuthService();
    SharedPreferences.setMockInitialValues({
      'current_user_id': 1,
    });
  });

  group('LoginScreen', () {
    // Helper function to set a larger test window
    void setLargeTestWindow(WidgetTester tester) {
      tester.binding.window.physicalSizeTestValue = const Size(1080, 2400);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
    }

    // Helper function to reset test window
    void resetTestWindow(WidgetTester tester) {
      tester.binding.window.clearPhysicalSizeTestValue();
      tester.binding.window.clearDevicePixelRatioTestValue();
    }

    testWidgets('displays logo placeholder', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Super Neighbour" title', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Super Neighbour'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Your neighbourly helper" subtitle', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Login" title in card', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Use first to get the title, not the button
      expect(find.text('Login').first, findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Email label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Password label', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Email and Password text fields', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(TextField), findsNWidgets(2));
      
      resetTestWindow(tester);
    });

    testWidgets('Password field has obscureText true', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final textFields = tester.widgetList<TextField>(find.byType(TextField)).toList();
      expect(textFields.length, 2);
      expect(textFields[1].obscureText, true);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Remember me" checkbox', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(Checkbox), findsOneWidget);
      expect(find.text('Remember me'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Forgot Password?" link', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Forgot Password?'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Login button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Use last to get the button text, not the title
      expect(find.text('Login').last, findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays "Don\'t have an account? Sign up" link', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Don\'t have an account? '), findsOneWidget);
      expect(find.text('Sign up'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('tapping "Sign up" navigates to SignupScreen', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Sign up'));
      await tester.pumpAndSettle();

      expect(find.byType(SignupScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('tapping "Forgot Password?" navigates to ForgotPasswordScreen', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Forgot Password?'));
      await tester.pumpAndSettle();

      expect(find.byType(ForgotPasswordScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Email field has email keyboard type', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final textFields = tester.widgetList<TextField>(find.byType(TextField)).toList();
      expect(textFields[0].keyboardType, TextInputType.emailAddress);
      
      resetTestWindow(tester);
    });

    testWidgets('Email field has hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your email'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Password field has hint text', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Enter your password'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Remember me checkbox is unchecked by default', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final checkbox = tester.widget<Checkbox>(find.byType(Checkbox));
      expect(checkbox.value, false);
      
      resetTestWindow(tester);
    });

    testWidgets('tapping checkbox toggles Remember me state', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        MaterialApp(
          home: LoginScreen(
            authService: mockAuthService,
          ),
        ),
      );

      await tester.pumpAndSettle();

      final checkboxFinder = find.byType(Checkbox);
      await tester.tap(checkboxFinder);
      await tester.pump();

      final checkbox = tester.widget<Checkbox>(checkboxFinder);
      expect(checkbox.value, true);
      
      resetTestWindow(tester);
    });
  });
}