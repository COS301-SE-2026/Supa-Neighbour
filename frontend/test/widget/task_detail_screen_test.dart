import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/app/screens/task_detail_screen.dart';


void main(){
    Widget buildTestableWidget(){
        return const MaterialApp(
            home: TaskDetailScreen(),
        );
    }

    group('TaskDetailsScreen', (){
        testWidgets('renders the app bar with correct title', (WidgetTester tester) async {
            await tester.pumpWidget(buildTestableWidget());
            expect(find.text('Task Details'), findsOneWidget);
        });

        testWidgets('renders the task title', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());
            expect(find.text('Water my plants'), findsOneWidget);
        });

        testWidgets('renders the XP reward badge', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());
            expect(find.text('+50 XP'), findsOneWidget);
        });

        testWidgets('renders task time and location', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());
            expect(find.text('Tomorrow at 3:00 PM'), findsOneWidget);
            expect(find.text('2 doors down • 50m away'), findsOneWidget);
        });

        testWidgets('renders task instructions', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());

            expect(find.text('Please water the 3 pots on the balcony. Use the blue watering can under the sink.'), findsOneWidget);
        });

        testWidgets('renders helper section with correct name', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());

            expect(find.text('Available Helper'), findsOneWidget);
            expect(find.text('Sarah Johnson'), findsOneWidget);
        });

        testWidgets('renders Accept Task and Message Helper buttons', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());
            expect(find.text('Accept Task'), findsOneWidget);
            expect(find.text('Message Helper'), findsOneWidget);
        });

        testWidgets('tapping Accept Task shows a snackbar', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());
            
           await tester.tap(find.text('Accept Task'));
           await tester.pump();

           expect(find.text('Task accepted! (Coming soon)'), findsOneWidget);

        });

        testWidgets('tapping Message Helper shows a snackbar', (WidgetTester tester) async {
            await tester.pumpWidget(buildTestableWidget());

            await tester.tap(find.text('Message Helper'));
            await tester.pump();

            expect(find.text('Message helper (Coming soon)'), findsOneWidget);
        });

        testWidgets('back button is present in app bar', (WidgetTester tester) async{
            await tester.pumpWidget(buildTestableWidget());

            expect(find.byIcon(Icons.arrow_back), findsOneWidget);
        });
    });


}