import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/models/user_model.dart';
import 'package:supa_neighbour/screens/auth/signup_residential_screen.dart';
import 'package:supa_neighbour/screens/auth/signup_other_details_screen.dart';

void main() {
  group('SignupResidentialScreen', () {
    // ─── Helpers ────────────────────────────────────────────────

    User buildTestUser() {
      return User(
        id: '1',
        email: 'test@example.com',
        firstName: 'Test',
        lastName: 'User',
        username: 'testuser',
        phone: '0000000000',
        birthday: DateTime(2000, 1, 1),
        gender: 'Other',
        createdAt: DateTime.now(),
      );
    }

    Widget wrapScreen() {
      return MaterialApp(
        home: SignupResidentialScreen(
          user: buildTestUser(),
          idToken: 'fake-id-token',
          password: 'password123',
        ),
      );
    }

    Future<void> pumpScreen(WidgetTester tester) async {
      // Taller viewport so the Next button is visible
      tester.view.physicalSize = const Size(1080, 2400);
      tester.view.devicePixelRatio = 1.0;
      addTearDown(tester.view.reset);

      await tester.pumpWidget(wrapScreen());
      await tester.pumpAndSettle();
    }

    // ─── Rendering ──────────────────────────────────────────────

    testWidgets('renders the title and subtitle', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Residential Address'), findsOneWidget);
      expect(find.text('Where do you live?'), findsOneWidget);
    });

    testWidgets('displays "Street" label and its TextField', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Street'), findsOneWidget);
      expect(find.byType(TextField), findsNWidgets(2));
    });

    testWidgets('Street field has correct hint text', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Enter your street address'), findsOneWidget);
    });

    testWidgets('displays "Town" label and a DropdownButton', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Town'), findsOneWidget);
      expect(find.byType(DropdownButton<String>), findsOneWidget);
    });

    testWidgets('Town dropdown has correct hint text', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Select your town'), findsOneWidget);
    });

    testWidgets('Town dropdown contains all seven towns', (tester) async {
      await pumpScreen(tester);

      await tester.tap(find.byType(DropdownButton<String>));
      await tester.pumpAndSettle();

      expect(find.text('Hillcrest'), findsOneWidget);
      expect(find.text('Hatfield'), findsOneWidget);
      expect(find.text('Brooklyn'), findsOneWidget);
      expect(find.text('Sunnyside'), findsOneWidget);
      expect(find.text('Lynnwood'), findsOneWidget);
      expect(find.text('Menlyn'), findsOneWidget);
      expect(find.text('Menlo Park'), findsOneWidget);
    });

    testWidgets('displays "Zip Code" label and its TextField', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Zip Code'), findsOneWidget);
    });

    testWidgets('displays "Next" button', (tester) async {
      await pumpScreen(tester);

      expect(find.text('Next'), findsOneWidget);
    });

    // ─── Validation ─────────────────────────────────────────────

    testWidgets('shows snackbar when Street is empty on Next', (tester) async {
      await pumpScreen(tester);

      await tester.ensureVisible(find.text('Next'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Next'));
      await tester.pump();

      expect(find.text('Please enter your street address'), findsOneWidget);
    });

    testWidgets('shows snackbar when Town is empty after filling Street',
        (tester) async {
      await pumpScreen(tester);

      await tester.enterText(
        find.byType(TextField).first,
        '42 Rosewood Lane',
      );
      await tester.pump();

      await tester.ensureVisible(find.text('Next'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Next'));
      await tester.pump();

      expect(find.text('Please select your town'), findsOneWidget);
    });

    testWidgets('shows snackbar when Zip Code is empty after Street + Town',
        (tester) async {
      await pumpScreen(tester);

      await tester.enterText(
        find.byType(TextField).first,
        '42 Rosewood Lane',
      );
      await tester.pump();

      await tester.tap(find.byType(DropdownButton<String>));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Hatfield').last);
      await tester.pumpAndSettle();

      await tester.ensureVisible(find.text('Next'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Next'));
      await tester.pump();

      expect(find.text('Please enter your zip code'), findsOneWidget);
    });

    // ─── Dropdown selection ─────────────────────────────────────

    testWidgets('selecting a town updates the dropdown value', (tester) async {
      await pumpScreen(tester);

      await tester.tap(find.byType(DropdownButton<String>));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Menlyn').last);
      await tester.pumpAndSettle();

      expect(find.text('Menlyn'), findsOneWidget);
      expect(find.text('Select your town'), findsNothing);
    });

    // ─── Navigation ─────────────────────────────────────────────

    testWidgets(
        'navigates to SignupOtherDetailsScreen when all fields are filled',
        (tester) async {
      await pumpScreen(tester);

      await tester.enterText(
        find.byType(TextField).first,
        '42 Rosewood Lane',
      );
      await tester.pump();

      await tester.tap(find.byType(DropdownButton<String>));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Brooklyn').last);
      await tester.pumpAndSettle();

      await tester.enterText(
        find.byType(TextField).at(1),
        '0028',
      );
      await tester.pump();

      await tester.ensureVisible(find.text('Next'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Next'));
      await tester.pumpAndSettle();

      expect(find.byType(SignupOtherDetailsScreen), findsOneWidget);
    });

    testWidgets('back button pops the screen', (tester) async {
      tester.view.physicalSize = const Size(1080, 2400);
      tester.view.devicePixelRatio = 1.0;
      addTearDown(tester.view.reset);

      await tester.pumpWidget(
        MaterialApp(
          home: Builder(
            builder: (context) => Scaffold(
              body: Center(
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => SignupResidentialScreen(
                          user: buildTestUser(),
                          idToken: 'fake-id-token',
                          password: 'password123',
                        ),
                      ),
                    );
                  },
                  child: const Text('Open'),
                ),
              ),
            ),
          ),
        ),
      );

      await tester.tap(find.text('Open'));
      await tester.pumpAndSettle();
      expect(find.text('Residential Address'), findsOneWidget);

      await tester.tap(find.byIcon(Icons.arrow_back));
      await tester.pumpAndSettle();

      expect(find.text('Open'), findsOneWidget);
    });
  });
}