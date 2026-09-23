// admin/lib/providers/service_providers.dart

import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/admin_application_service.dart';
import '../services/admin_application_service_mock.dart';

/// Provider for the Admin Application service.
///
/// Currently returns the mock implementation so the UI works before
/// the backend endpoint is deployed.
///
/// To switch to the real service (once the backend is live):
/// 1. Comment out the `AdminApplicationServiceMock()` line below
/// 2. Uncomment the `AdminApplicationService()` line
///
/// That's the ONLY change needed because all screens read from this provider.
final adminApplicationServiceProvider = Provider<IAdminApplicationService>(
  (ref) {
     return AdminApplicationService();     
    //return AdminApplicationServiceMock();    
  },
);