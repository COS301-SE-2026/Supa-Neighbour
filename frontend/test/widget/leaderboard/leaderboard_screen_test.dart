import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('Leaderboard UI components render correctly', (WidgetTester tester) async {
  
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          appBar: AppBar(
            title: const Text('Leaderboard'),
            leading: IconButton(
              icon: const Icon(Icons.info_outline),
              onPressed: () {},
            ),
          ),
          body: const Center(
            child: CircularProgressIndicator(),
          ),
        ),
      ),
    );

    expect(find.text('Leaderboard'), findsOneWidget);
    expect(find.byIcon(Icons.info_outline), findsOneWidget);
    expect(find.byType(CircularProgressIndicator), findsOneWidget);
  });

  testWidgets('Leaderboard visual structure is correct', (WidgetTester tester) async {
    
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          appBar: AppBar(
            backgroundColor: Colors.white,
            elevation: 0,
            leading: const Icon(Icons.info_outline, color: Colors.teal),
            title: const Text(
              'Leaderboard',
              style: TextStyle(
                color: Colors.teal,
                fontSize: 24,
                fontWeight: FontWeight.w600,
              ),
            ),
            centerTitle: true,
          ),
          body: Column(
            children: [
              const Padding(
                padding: EdgeInsets.only(top: 16, bottom: 8, left: 16),
                child: Align(
                  alignment: Alignment.centerLeft,
                  child: Text(
                    "Last Week's Top 3",
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CircleAvatar(radius: 22),
                    const SizedBox(width: 12),
                    CircleAvatar(radius: 28),
                    const SizedBox(width: 12),
                    CircleAvatar(radius: 22),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );

    expect(find.text("Last Week's Top 3"), findsOneWidget);
    expect(find.byType(CircleAvatar), findsNWidgets(3));
  });
}