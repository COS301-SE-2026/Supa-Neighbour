import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/auth/auth_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';

void main() {
  group('AuthScreen', () {
    testWidgets('displays logo placeholder', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LogoPlaceholder), findsOneWidget);
    });

    testWidgets('displays "Super Neighbour" title', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Super Neighbour'), findsOneWidget);
    });

    testWidgets('displays "Your neighbourly helper" subtitle', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
    });

    testWidgets('displays Login button', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Login'), findsOneWidget);
    });

    testWidgets('displays Create Account button', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Create an account'), findsOneWidget);
    });

    testWidgets('Login button has correct styling', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final loginButton = find.widgetWithText(ElevatedButton, 'Login');
      expect(loginButton, findsOneWidget);
    });

    testWidgets('Create Account button has correct styling (outlined)', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final createAccountButton = find.widgetWithText(OutlinedButton, 'Create an account');
      expect(createAccountButton, findsOneWidget);
    });

    testWidgets('screen uses AppColors.background for background color', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // Find the main container
      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      // Should be using AppColors.background which returns white in light mode
      expect(containerWidget.color, Colors.white);
    });

    testWidgets('content is centered vertically', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      // The Column should use MainAxisAlignment.center
      final column = find.byType(Column).first;
      final columnWidget = tester.widget<Column>(column);
      expect(columnWidget.mainAxisAlignment, MainAxisAlignment.center);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      
      // Should match AppColors.primary which is teal in light mode
      expect(titleWidget.style?.color, const Color(0xFF1C9A89));
    });

    testWidgets('title has correct font weight', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      
      expect(titleWidget.style?.fontWeight, FontWeight.w600);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      
      // Should match AppColors.secondary which is yellow in light mode
      expect(subtitleWidget.style?.color, const Color(0xFFEAC059));
    });

    testWidgets('Login button text is white', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final loginText = find.text('Login');
      final loginTextWidget = tester.widget<Text>(loginText);
      
      expect(loginTextWidget.style?.color, Colors.white);
    });

    testWidgets('Create Account button text uses AppColors.primaryTeal', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final createAccountText = find.text('Create an account');
      final createAccountTextWidget = tester.widget<Text>(createAccountText);
      
      // Use the actual color from AppColors.primaryTeal (light mode)
      expect(createAccountTextWidget.style?.color, const Color(0xFF2A9D8F));
    });

    testWidgets('buttons are in correct order (Login then Create Account)', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
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
    });

    testWidgets('LogoPlaceholder receives correct size', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final logoFinder = find.byType(LogoPlaceholder);
      final logoWidget = tester.widget<LogoPlaceholder>(logoFinder);
      
      // Size should be between 120 and 220 depending on screen
      expect(logoWidget.size, greaterThanOrEqualTo(120.0));
      expect(logoWidget.size, lessThanOrEqualTo(220.0));
    });

    testWidgets('has SingleChildScrollView to prevent overflow', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SingleChildScrollView), findsOneWidget);
    });

    testWidgets('has SizedBox with full screen height', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final sizedBox = find.byType(SizedBox).first;
      final sizedBoxWidget = tester.widget<SizedBox>(sizedBox);
      
      // Height should match screen height
      final screenHeight = tester.binding.window.physicalSize.height / 
                           tester.binding.window.devicePixelRatio;
      expect(sizedBoxWidget.height, screenHeight);
    });

    testWidgets('uses ElevatedButton for Login', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(ElevatedButton), findsOneWidget);
    });

    testWidgets('uses OutlinedButton for Create Account', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(OutlinedButton), findsOneWidget);
    });

    testWidgets('Login button has rounded corners', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: AuthScreen(),
        ),
      );

      await tester.pumpAndSettle();

      final loginButton = find.widgetWithText(ElevatedButton, 'Login');
      final buttonWidget = tester.widget<ElevatedButton>(loginButton);
      
      final style = buttonWidget.style;
      expect(style?.shape, isNotNull);
    });
  });
}