// test/widget/auth/splash_screen_test.dart
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:supa_neighbour/screens/auth/splash_screen.dart';
import 'package:supa_neighbour/components/logo_placeholder.dart';
import 'package:supa_neighbour/components/loading_bar.dart';
import 'package:supa_neighbour/components/splash_title.dart';

void main() {
  group('SplashScreen', () {
    // Helper to set larger test window
    void setLargeTestWindow(WidgetTester tester) {
      tester.binding.window.physicalSizeTestValue = const Size(1080, 2400);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
    }

    void resetTestWindow(WidgetTester tester) {
      tester.binding.window.clearPhysicalSizeTestValue();
      tester.binding.window.clearDevicePixelRatioTestValue();
    }

    // Helper to set normal test window (for font size tests)
    void setNormalTestWindow(WidgetTester tester) {
      tester.binding.window.physicalSizeTestValue = const Size(800, 1600);
      tester.binding.window.devicePixelRatioTestValue = 1.0;
    }

    testWidgets('displays logo placeholder', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
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
            home: SplashScreen(),
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
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Your neighbourly helper'), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('displays loading bar', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(LoadingBar), findsOneWidget);
      
      resetTestWindow(tester);
    });

    testWidgets('loading bar has 3 second duration', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loadingBar = tester.widget<LoadingBar>(find.byType(LoadingBar));
      expect(loadingBar.duration, const Duration(seconds: 3));
      
      resetTestWindow(tester);
    });

    testWidgets('displays SplashTitle components', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(SplashTitle), findsNWidgets(2));
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      expect(titleWidget.style?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct color', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      expect(subtitleWidget.style?.color, const Color(0xFF2A9D8F));
      
      resetTestWindow(tester);
    });

    testWidgets('screen has white background', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final container = find.byType(Container).first;
      final containerWidget = tester.widget<Container>(container);
      expect(containerWidget.color, Colors.white);
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Use normal screen size to test normal font sizes
    testWidgets('title has correct font size on normal screens', (WidgetTester tester) async {
      setNormalTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      // For normal screens (400-800px), title size should be 36.0
      expect(titleWidget.style?.fontSize, 36.0);
      
      resetTestWindow(tester);
    });

    // ✅ FIXED: Use normal screen size to test normal font sizes
    testWidgets('subtitle has correct font size on normal screens', (WidgetTester tester) async {
      setNormalTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      // For normal screens (400-800px), subtitle size should be 20.0
      expect(subtitleWidget.style?.fontSize, 20.0);
      
      resetTestWindow(tester);
    });

    testWidgets('title has correct font size on large screens', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final titleFinder = find.text('Super Neighbour');
      final titleWidget = tester.widget<Text>(titleFinder);
      // For large screens (>800px), title size should be 48.0
      expect(titleWidget.style?.fontSize, 48.0);
      
      resetTestWindow(tester);
    });

    testWidgets('subtitle has correct font size on large screens', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final subtitleFinder = find.text('Your neighbourly helper');
      final subtitleWidget = tester.widget<Text>(subtitleFinder);
      // For large screens (>800px), subtitle size should be 28.0
      expect(subtitleWidget.style?.fontSize, 28.0);
      
      resetTestWindow(tester);
    });

    testWidgets('logo is centered horizontally', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final logoFinder = find.byType(LogoPlaceholder);
      final logoWidget = tester.widget<LogoPlaceholder>(logoFinder);
      
      final logoElement = tester.element(logoFinder);
      final logoRenderBox = logoElement.renderObject as RenderBox;
      final logoPosition = logoRenderBox.localToGlobal(Offset.zero);
      
      final screenWidth = tester.binding.window.physicalSize.width / 
                           tester.binding.window.devicePixelRatio;
      final logoWidth = logoWidget.size;
      final expectedLeft = (screenWidth - logoWidth) / 2;
      
      expect(logoPosition.dx, greaterThan(expectedLeft - 10));
      expect(logoPosition.dx, lessThan(expectedLeft + 10));
      
      resetTestWindow(tester);
    });

    testWidgets('loading bar is centered horizontally', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loadingBarFinder = find.byType(LoadingBar);
      final loadingBarElement = tester.element(loadingBarFinder);
      final loadingBarRenderBox = loadingBarElement.renderObject as RenderBox;
      final loadingBarPosition = loadingBarRenderBox.localToGlobal(Offset.zero);
      
      final screenWidth = tester.binding.window.physicalSize.width / 
                           tester.binding.window.devicePixelRatio;
      final loadingBar = tester.widget<LoadingBar>(loadingBarFinder);
      final expectedLeft = (screenWidth - loadingBar.width) / 2;
      
      expect(loadingBarPosition.dx, greaterThan(expectedLeft - 10));
      expect(loadingBarPosition.dx, lessThan(expectedLeft + 10));
      
      resetTestWindow(tester);
    });

    testWidgets('loading bar has correct height', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final loadingBar = tester.widget<LoadingBar>(find.byType(LoadingBar));
      expect(loadingBar.height, greaterThan(0));
      
      resetTestWindow(tester);
    });

    testWidgets('logo has correct size', (WidgetTester tester) async {
      setLargeTestWindow(tester);
      
      await tester.pumpWidget(
        const ProviderScope(
          child: MaterialApp(
            home: SplashScreen(),
          ),
        ),
      );

      await tester.pumpAndSettle();

      final logoWidget = tester.widget<LogoPlaceholder>(find.byType(LogoPlaceholder));
      expect(logoWidget.size, greaterThanOrEqualTo(100.0));
      expect(logoWidget.size, lessThanOrEqualTo(300.0));
      
      resetTestWindow(tester);
    });
  });
}