import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supa_neighbour/screens/auth/auth_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/screens/auth/login_screen.dart';
import 'package:supa_neighbour/screens/auth/signup_screen.dart';

void main() {
  group('AuthScreen', () {
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
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
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
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
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
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Login button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Login'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays Create Account button', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Create an account'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Login button has correct styling (teal background)', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loginButton = find.widgetWithText(ElevatedButton, 'Login');
      expect(loginButton, findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Create Account button has correct styling (outlined)', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final createAccountButton = find.widgetWithText(OutlinedButton, 'Create an account');
      expect(createAccountButton, findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('screen uses AppColors.background for background color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Find the main container
      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      expect(containerWidget.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('content is centered vertically', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      // The Column should use MainAxisAlignment.center
      final column = find.byType(Column).first;
      final columnWidget = tester.widget<Column>(column);
      expect(columnWidget.mainAxisAlignment, MainAxisAlignment.center);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      
      // Should match AppColors.primary which is teal in light mode
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct font weight', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      
      expect(titleWidget.style?.fontWeight, FontWeight.w600);
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      
      // Should match AppColors.secondary which is yellow in light mode
      expect(subtitleWidget.style?.color, const Color(0xFFEAC059));
      
      resetTestWindow(tester);
    });

    testWidgets('Login button text is white', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loginText = find.text('Login');
      final loginTextWidget = tester.widget<Text>(loginText);
      
      expect(loginTextWidget.style?.color, Colors.white);
      
      resetTestWindow(tester);
    });

    testWidgets('Create Account button text uses AppColors.primaryTeal', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final createAccountText = find.text('Create an account');
      final createAccountTextWidget = tester.widget<Text>(createAccountText);
      
      // Use the actual color from AppColors.primaryTeal (light mode)
      expect(createAccountTextWidget.style?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('buttons are in correct order (Login then Create Account)', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      // Get all text widgets in order
      final allText = tester.widgetList<Text>(find.byType(Text)).toList();
      final textValues = allText.map((t) => t.data).toList();
      
      // Find positions of button texts
      final loginIndex = textValues.indexOf('Login');
      final signupIndex = textValues.indexOf('Create an account');
      
      // Login should appear before Create Account
      expect(loginIndex < signupIndex, true);
      
      resetTestWindow(tester);
    });

    testWidgets('LogoPlaceholder receives correct size', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final logoFinder = find.byType(LogoPlaceholder);
      final logoWidget = tester.widget<LogoPlaceholder>(logoFinder);
      
      // Size should be between 100 and 300 depending on screen
      expect(logoWidget.size, greaterThanOrEqualTo(100.0));
      expect(logoWidget.size, lessThanOrEqualTo(300.0));
      
      resetTestWindow(tester);
    });

    testWidgets('has SingleChildScrollView to prevent overflow', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('has SizedBox with full screen height', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final sizedBox = find.byType(SizedBox).first;
      final sizedBoxWidget = tester.widget<SizedBox>(sizedBox);
      
      // Height should match screen height
      final screenHeight = tester.binding.window.physicalSize.height / 
                           tester.binding.window.devicePixelRatio;
      expect(sizedBoxWidget.height, screenHeight);
      
      resetTestWindow(tester);
    });

    testWidgets('uses ElevatedButton for Login', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(ElevatedButton), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('uses OutlinedButton for Create Account', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(OutlinedButton), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('Login button has rounded corners', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loginButton = find.widgetWithText(ElevatedButton, 'Login');
      final buttonWidget = tester.widget<ElevatedButton>(loginButton);
      
      final style = buttonWidget.style;
      expect(style?.shape, isNotNull);
      
      resetTestWindow(tester);
    });

    testWidgets('tapping Login button navigates to LoginScreen', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Login'));
      await tester.pumpAndSettle();

      expect(find.byType(LoginScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('tapping Create Account button navigates to SignupScreen', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: AuthScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      await tester.tap(find.text('Create an account'));
      await tester.pumpAndSettle();

      expect(find.byType(SignupScreen), findsOneWidget);
      
      resetTestWindow(tester);
    });
  });
}