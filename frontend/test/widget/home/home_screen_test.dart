import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/home/home_screen.dart';

void main() {
  Widget buildTestableWidget() {
    return const MaterialApp(
      home: HomeScreen(),
    );
  }

  group('HomeScreen', () {
    testWidgets('renders the app bar with correct title', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.text('Supa Neighbour'), findsOneWidget);
    });

    testWidgets('renders stat cards', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.text('Helps Given'), findsOneWidget);
      expect(find.text('Tasks Posted'), findsOneWidget);
      expect(find.text('Active'), findsOneWidget);
    });

    testWidgets('renders Available Nearby section', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.text('Available Nearby'), findsOneWidget);
    });

    testWidgets('renders floating action button', (WidgetTester tester) async {
      await tester.pumpWidget(buildTestableWidget());
      await tester.pumpAndSettle();
      expect(find.byType(FloatingActionButton), findsOneWidget);
    });
  });
}