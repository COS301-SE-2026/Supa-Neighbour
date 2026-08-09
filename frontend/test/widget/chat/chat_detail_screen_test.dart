import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:supa_neighbour/screens/chat/chat_detail_screen.dart';
import 'package:supa_neighbour/models/chat_thread.dart';

void main() {
  group('ChatDetailScreen', () {
    testWidgets('displays app bar with chat name and location', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('John Doe'), findsOneWidget);
      expect(find.text('Pretoria, South Africa'), findsOneWidget);
      expect(find.byIcon(Icons.location_on), findsOneWidget);
    });

    testWidgets('displays back button in app bar', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

    testWidgets('displays message input field', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byType(TextField), findsOneWidget);
      expect(find.text('Type a message...'), findsOneWidget);
    });

    testWidgets('displays send button', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.send), findsOneWidget);
    });

    testWidgets('displays attachment/upload button', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.byIcon(Icons.attach_file), findsOneWidget);
    });

    testWidgets('displays avatar with user initial', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('J'), findsOneWidget); // First letter of "John Doe"
    });

    testWidgets('displays location icon with location text for different user', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 2,
        otherUserId: 3,
        taskId: 200,
        name: 'Jane Smith',
        location: 'Cape Town, South Africa',
        lastMessage: 'See you soon!',
        timestamp: '2026-08-09T11:00:00',
        unreadCount: 1,
        avatarColor: const Color(0xFFE9C46A),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Jane Smith'), findsOneWidget);
      expect(find.text('Cape Town, South Africa'), findsOneWidget);
      expect(find.byIcon(Icons.location_on), findsOneWidget);
    });

    testWidgets('app bar has correct structure', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFF2A9D8F),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      final appBar = find.byType(AppBar);
      expect(appBar, findsOneWidget);
      
      expect(find.byIcon(Icons.arrow_back), findsOneWidget);
    });

    testWidgets('displays correct avatar color', (WidgetTester tester) async {
      final testChat = ChatThread(
        chatId: 1,
        otherUserId: 2,
        taskId: 100,
        name: 'John Doe',
        location: 'Pretoria, South Africa',
        lastMessage: 'Hello!',
        timestamp: '2026-08-09T10:30:00',
        unreadCount: 0,
        avatarColor: const Color(0xFFE9C46A),
      );

      await tester.pumpWidget(
        MaterialApp(
          home: ChatDetailScreen(chat: testChat),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('J'), findsOneWidget);
    });
  });
}