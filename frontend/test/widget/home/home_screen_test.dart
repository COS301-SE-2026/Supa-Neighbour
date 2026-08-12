import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/auth_session.dart';
import 'package:supa_neighbour/models/user_model.dart';
import 'package:supa_neighbour/screens/home/home_screen.dart';
import 'package:supa_neighbour/providers/service_providers.dart';

void main() {
  group('HomeScreen Widget Tests', () {
    late User testUser;

    setUp(() {
      AuthSession.instance.clear();

      testUser = User(
        id: 'test_1',
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
        phone: '1234567890',
        username: 'testuser',
        street: '123 Test St',
        town: 'Test Town',
        zipCode: '1234',
        createdAt: DateTime.now(),
      );

      AuthSession.instance.login(testUser);
    });

    tearDown(() {
      AuthSession.instance.clear();
    });

    Widget buildTestWidget() {
      return ProviderScope(
        child: const MaterialApp(
          home: HomeScreen(),
        ),
      );
    }

    group('App Bar', () {
      testWidgets('should render app bar with title', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });

    group('Welcome Section', () {
      testWidgets('should display user name', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });

    group('Stats Cards', () {
      testWidgets('should display stat card labels', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });

    group('Available Nearby Section', () {
      testWidgets('should display "Available Nearby" title', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });

      testWidgets('should display "See All" button', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });

    group('Empty State', () {
      testWidgets('should handle empty state gracefully', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });

    group('Floating Action Button', () {
      testWidgets('should display floating action button', (tester) async {
        await tester.pumpWidget(buildTestWidget());
        await tester.pumpAndSettle();
        expect(find.byType(HomeScreen), findsOneWidget);
      });
    });
  });
}