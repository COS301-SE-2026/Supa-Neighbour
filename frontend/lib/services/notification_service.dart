import 'package:firebase_messaging/firebase_messaging.dart';
//import '../main.dart';

void _handleNotificationTap(Map<String, dynamic> data) {
  //TODO: open notification page
  //The below code will then be used on another function that goes to the specific pages.
  /*final type = data['type'];
  final entityId = data['entityId'];

  switch (type) {
    case 'TASK_CREATED':
    case 'TASK_START':
      navigatorKey.currentState?.pushNamed('/task/$entityId');
      break;

    case 'POST_CREATED':
    case 'POST_COMMENT':
      navigatorKey.currentState?.pushNamed('/posts/$entityId');
      break;

    default:
      // TODO: Handle unknown notification types
      break;
  }*/
}

void setupNotificationListeners() {
  FirebaseMessaging.onMessageOpenedApp.listen((RemoteMessage message) {
    _handleNotificationTap(message.data);
  });

  FirebaseMessaging.onMessage.listen((RemoteMessage message) {
    // Show an in-app banner using:
    // message.notification?.title
    // message.notification?.body

    // On tap:
    // handleNotificationTap(message.data);
  });
}

Future<void> handleInitialNotification() async {
  final RemoteMessage? initialMessage =
      await FirebaseMessaging.instance.getInitialMessage();

  if (initialMessage != null) {
    _handleNotificationTap(initialMessage.data);
  }
}