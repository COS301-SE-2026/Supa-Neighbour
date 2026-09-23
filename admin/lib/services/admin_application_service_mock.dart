// admin/lib/services/admin_application_service_mock.dart

import 'package:shared/shared.dart';
import 'admin_application_service.dart';

/// Mock implementation of [IAdminApplicationService] for development
/// and testing. Keeps an in-memory list of fake applications that
/// supports approve/reject to simulate the full flow.
class AdminApplicationServiceMock implements IAdminApplicationService {
  /// In-memory store of mock applications.
  final List<AdminApplication> _applications = [
    AdminApplication(
      applicationId: 1,
      userId: 101,
      username: 'jane_doe',
      applicationStatus: 'Pending',
      applicationDate: DateTime.now().subtract(const Duration(days: 2)),
      justification:
          'I have been an active helper for 6 months and would like to help moderate task disputes in my neighbourhood. I am available most evenings.',
    ),
    AdminApplication(
      applicationId: 2,
      userId: 102,
      username: 'john_smith',
      applicationStatus: 'Pending',
      applicationDate: DateTime.now().subtract(const Duration(days: 5)),
      justification:
          'As a retired teacher, I have plenty of free time and want to give back to my community. I am patient, fair, and good with conflict resolution.',
    ),
    AdminApplication(
      applicationId: 3,
      userId: 103,
      username: 'sarah_nearby',
      applicationStatus: 'Approved',
      applicationDate: DateTime.now().subtract(const Duration(days: 10)),
      justification:
          'I have completed over 30 tasks on the platform and would like to take on a moderator role.',
      reviewedByAdminId: 2,
      reviewedDate: DateTime.now().subtract(const Duration(days: 8)),
    ),
    AdminApplication(
      applicationId: 4,
      userId: 104,
      username: 'bob_neighbour',
      applicationStatus: 'Rejected',
      applicationDate: DateTime.now().subtract(const Duration(days: 14)),
      justification: 'I want admin powers.',
      reviewedByAdminId: 2,
      reviewedDate: DateTime.now().subtract(const Duration(days: 12)),
      rejectionReason:
          'Application lacks sufficient detail. Please provide more information about your experience on the platform.',
    ),
  ];

  @override
  Future<List<AdminApplication>> getAllApplications({String? status}) async {
    // Simulate network latency
    await Future.delayed(const Duration(milliseconds: 400));

    if (status == null || status.isEmpty) {
      return List.from(_applications);
    }

    return _applications
        .where((a) => a.applicationStatus == status)
        .toList();
  }

  @override
  Future<AdminApplication> getApplicationById(int applicationId) async {
    await Future.delayed(const Duration(milliseconds: 400));

    final match = _applications.firstWhere(
      (a) => a.applicationId == applicationId,
      orElse: () => throw AdminApplicationServiceException(
        'Application not found.',
        statusCode: 404,
      ),
    );

    return match;
  }


  @override
  Future<AdminApplication> approveApplication(int applicationId) async {
    await Future.delayed(const Duration(milliseconds: 400));

    final index = _applications.indexWhere(
      (a) => a.applicationId == applicationId,
    );
    if (index == -1) {
      throw AdminApplicationServiceException(
        'Application not found.',
        statusCode: 404,
      );
    }

    final existing = _applications[index];
    if (!existing.isPending) {
      throw AdminApplicationServiceException(
        'Application has already been reviewed.',
        statusCode: 409,
      );
    }

    final updated = AdminApplication(
      applicationId: existing.applicationId,
      userId: existing.userId,
      username: existing.username,
      applicationStatus: 'Approved',
      applicationDate: existing.applicationDate,
      justification: existing.justification,
      reviewedByAdminId: 1, // pretend current admin
      reviewedDate: DateTime.now(),
    );

    _applications[index] = updated;
    return updated;
  }

  @override
  Future<AdminApplication> rejectApplication(
    int applicationId, {
    String? rejectionReason,
  }) async {
    await Future.delayed(const Duration(milliseconds: 400));

    final index = _applications.indexWhere(
      (a) => a.applicationId == applicationId,
    );
    if (index == -1) {
      throw AdminApplicationServiceException(
        'Application not found.',
        statusCode: 404,
      );
    }

    final existing = _applications[index];
    if (!existing.isPending) {
      throw AdminApplicationServiceException(
        'Application has already been reviewed.',
        statusCode: 409,
      );
    }

    final updated = AdminApplication(
      applicationId: existing.applicationId,
      userId: existing.userId,
      username: existing.username,
      applicationStatus: 'Rejected',
      applicationDate: existing.applicationDate,
      justification: existing.justification,
      reviewedByAdminId: 1,
      reviewedDate: DateTime.now(),
      rejectionReason: rejectionReason,
    );

    _applications[index] = updated;
    return updated;
  }
}