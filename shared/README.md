
## Admin Dashoboard Project Structure

```text
admin/
├── lib/
│   ├── main.dart                 # Entry point
│   ├── app.dart                  # Main app widget
│   ├── routes.dart               # GoRouter configuration
│   ├── firebase_options.dart     # Firebase configuration
│   ├── screens/                  # All admin screens
│   │   ├── login/                # Login screen
│   │   ├── dashboard/            # Dashboard with stats
│   │   ├── reports/              # Reports management
│   │   ├── users/                # User management
│   │   └── zones/                # Neighbourhood zones
│   ├── widgets/                  # Reusable admin widgets
│   ├── theme/                    # Admin theme (extends shared)
│   ├── providers/                # Riverpod providers
│   └── utils/                    # Utilities (auth guard, etc.)
├── web/                          # Web-specific files
└── pubspec.yaml                  # Dependencies
```

## Authentication

The admin website uses the same authentication system as the mobile app:
- Users must have `user_type = 'admin'` or `'super_admin'` in the database
- Firebase Authentication handles the login flow
- Admin Guard protects all admin routes

### Mock Login (Development Only)

For development, you can use the "Skip Login" button to bypass authentication and test the UI.

## Shared Package

This project depends on the `shared` package at `../shared` which contains:
- Models (User, Task, ChatThread, etc.)
- Services (AuthService, ApiClient)
- Constants (AppColors)

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Flutter Web | Frontend framework |
| Riverpod | State management |
| GoRouter | Navigation |
| Firebase Auth | Authentication |
| fl_chart | Charts for dashboard |



## Notes

- The admin website is a **separate Flutter web project** from the mobile app
- It shares code via the `shared` package
- The admin site is deployed separately from the mobile app
- All admin endpoints should check for `user_type` before returning data
```eof
