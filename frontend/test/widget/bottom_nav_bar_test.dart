import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/widgets/bottom_nav_bar.dart';

void main() {
  testWidgets('BottomNavBar renders 5 items', (WidgetTester tester) async {
    int selectedIndex = 0;

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          bottomNavigationBar: BottomNavBar(
            currentIndex: selectedIndex,
            onTap: (index) {
              selectedIndex = index;
            },
          ),
        ),
      ),
    );

    expect(find.text('Home'), findsOneWidget);
    expect(find.text('Tasks'), findsOneWidget);
    expect(find.text('Inbox'), findsOneWidget);  
    expect(find.text('Stats'), findsOneWidget);  
    expect(find.text('Profile'), findsOneWidget);
  });
}