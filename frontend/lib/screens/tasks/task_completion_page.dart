import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import 'package:geolocator/geolocator.dart';
import 'package:supa_neighbour/models/verification_model.dart';
import '../../models/task_model.dart';
import '../../constants/app_colors.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';
import '../../services/task_service.dart';

class TaskCompletionPage extends ConsumerStatefulWidget {
  final String taskId;
  final String taskTitle;
  final String residentName;
  final String dueDate;
  final int xpReward;

  const TaskCompletionPage({
    super.key,
    required this.taskId,
    required this.taskTitle,
    required this.residentName,
    required this.dueDate,
    required this.xpReward,
  });

  @override
  ConsumerState<TaskCompletionPage> createState() => _TaskCompletionPageState();
}

class _TaskCompletionPageState extends ConsumerState<TaskCompletionPage> {
  final TextEditingController _noteController = TextEditingController();
  final List<XFile> _selectedImages = [];
  bool _isSubmitting = false;
  final ImagePicker _picker = ImagePicker();

  final Map<String, DateTime> _capturedAt = {}; // photo path -> capture time
  final Set<String> _submittedPaths = {};        // photos the server already accepted
  String _submitStatus = '';
  

  // ===== REQUIRED EVIDENCE PHOTOS (CAMERA ONLY) =====
  static const int _minImages = 1;
  static const int _maxImages = 5;

  // ===== REQUIRED LOCATION =====
  Position? _location;
  bool _isFetchingLocation = false;

  @override
  void dispose() {
    _noteController.dispose();
    super.dispose();
  }

  Future<void> _addPhoto() async {
    if (_selectedImages.length >= _maxImages) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('You can attach up to $_maxImages photos.'),
          backgroundColor: AppColors.error(context),
        ),
      );
      return;
    }

    try {
      final XFile? picked = await _picker.pickImage(
        source: ImageSource.camera,
        imageQuality: 85,
        maxWidth: 1920,
      );

      if (picked != null) {
        setState(() {
          _selectedImages.add(picked);
          _capturedAt[picked.path] = DateTime.now();
        });
      }
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to capture image: $e'),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  void _removePhoto(int index) {
    setState(() {
      final removed = _selectedImages.removeAt(index);
      _capturedAt.remove(removed.path);
      _submittedPaths.remove(removed.path);
    });
  }

  // ===== LOCATION HANDLING =====

  void _showSnack(String message) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: AppColors.error(context),
      ),
    );
  }

  String _gpsStatus = '';
  static const double _maxAccuracyM = 100.0; 

  Future<Position?> _acquireAccurateFix({
    Duration timeout = const Duration(seconds: 30),
  }) {
    final completer = Completer<Position?>();
    Position? best;
    StreamSubscription<Position>? sub;
    Timer? timer;

    void finish() {
      timer?.cancel();
      sub?.cancel();
      if (!completer.isCompleted) completer.complete(best);
    }

    sub = Geolocator.getPositionStream(
      locationSettings: AndroidSettings(
        accuracy: LocationAccuracy.best,
        distanceFilter: 0,
        intervalDuration: const Duration(seconds: 1),
      ),
    ).listen(
      (p) {
        if (best == null || p.accuracy < best!.accuracy) best = p;
        if (mounted) {
          setState(() => _gpsStatus =
              'Improving GPS accuracy (${best!.accuracy.round()} m)...');
        }
        if (p.accuracy <= _maxAccuracyM) finish();
      },
      onError: (_) => finish(),
    );

    timer = Timer(timeout, finish);
    return completer.future;
  }

  Future<void> _requestLocation() async {
    if (_isFetchingLocation) return;
    setState(() {
      _isFetchingLocation = true;
      _gpsStatus = 'Getting GPS fix...';
    });

    try {
      final serviceEnabled = await Geolocator.isLocationServiceEnabled()
          .timeout(const Duration(seconds: 3));
      if (!serviceEnabled) {
        _showSnack('Location services are disabled. Please enable them.');
        return;
      }

      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
      }
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        _showSnack(
            'Location permission denied. Location is required to complete a task.');
        return;
      }

      final position = await _acquireAccurateFix();

      if (position == null || position.accuracy > _maxAccuracyM) {
        // Not stored in _location. Dialog is not awaited, like before,
        // so `finally` resets the loading state first.
        if (mounted) _showLowAccuracyDialog(position?.accuracy);
        return;
      }

      if (!mounted) return;
      setState(() => _location = position);
      _showLocationModal(position);
    } on LocationServiceDisabledException {
      _showSnack('Location services are disabled. Please enable them.');
    } catch (e) {
      _showSnack('Failed to get location: $e');
    } finally {
      if (mounted) setState(() => _isFetchingLocation = false);
    }
  }

  Future<void> _showLowAccuracyDialog(double? accuracyM) async {
    final action = await showDialog<String>(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Text(
          'GPS Accuracy Too Low',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Text(
          accuracyM == null
              ? "We couldn't get a GPS fix.\n\n"
              : 'Your current accuracy is ${accuracyM.round()} m '
                  '(needs ${_maxAccuracyM.round()} m or better).\n\n',
          style: GoogleFonts.openSans(color: AppColors.charcoal(context)),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, 'cancel'),
            child: Text('Cancel',
                style: GoogleFonts.openSans(color: AppColors.textGrey(context))),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, 'settings'),
            child: Text('Location settings',
                style: GoogleFonts.openSans(color: AppColors.primaryTeal(context))),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, 'retry'),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
            ),
            child: Text('Retry',
                style: GoogleFonts.openSans(
                    color: Colors.white, fontWeight: FontWeight.w600)),
          ),
        ],
      ),
    );

    if (!mounted) return;
    if (action == 'retry') {
      await _requestLocation();
    } else if (action == 'settings') {
      await Geolocator.openAppSettings();
    }
  }

  Future<void> _showRetryDialog() async {
    final retry = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        title: Text(
          'Location Unavailable',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Text(
          'We couldn\'t get your current location.\n\n'
          '• Make sure GPS / Location is turned on\n'
          '• Move near a window or outdoors\n'
          '• Wait a few seconds, then tap Retry',
          style: GoogleFonts.openSans(
            color: AppColors.charcoal(context),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(color: AppColors.textGrey(context)),
            ),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: Text(
              'Retry',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );

    if (retry == true && mounted) {
      await _requestLocation();
    }
  }

  void _showLocationModal(Position position) {
    showModalBottomSheet(
      context: context,
      backgroundColor: AppColors.background(context),
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (sheetContext) {
        return SafeArea(
          child: Padding(
            padding: const EdgeInsets.fromLTRB(20, 16, 20, 24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Center(
                  child: Container(
                    width: 40,
                    height: 4,
                    margin: const EdgeInsets.only(bottom: 16),
                    decoration: BoxDecoration(
                      color:
                          AppColors.textGrey(context).withValues(alpha: 0.3),
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
                Row(
                  children: [
                    Icon(
                      Icons.location_on,
                      color: AppColors.primaryTeal(context),
                    ),
                    const SizedBox(width: 8),
                    Text(
                      'Location Captured',
                      style: GoogleFonts.poppins(
                        color: AppColors.charcoal(context),
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: AppColors.surfaceGrey(context),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: AppColors.primaryTeal(context)
                          .withValues(alpha: 0.4),
                      width: 1.5,
                    ),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Latitude',
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey(context),
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        position.latitude.toStringAsFixed(6),
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 15,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        'Longitude',
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey(context),
                          fontSize: 12,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        position.longitude.toStringAsFixed(6),
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 15,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => Navigator.pop(sheetContext),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: AppColors.primaryTeal(context),
                      padding: const EdgeInsets.symmetric(vertical: 14),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    child: Text(
                      'Got it',
                      style: GoogleFonts.openSans(
                        color: Colors.white,
                        fontSize: 15,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Future<void> _showCompletionDialog() async {
    if (_selectedImages.length < _minImages) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            'Please capture at least $_minImages photos '
            '(${_selectedImages.length}/$_minImages added).',
          ),
        ),
      );
      return;
    }

    if (_location == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
            content: Text('Please provide your location before completing.')),
      );
      return;
    }

    final confirmed = await showDialog<bool>(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        title: Text(
          'Complete Task?',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Resident will be notified to confirm completion.',
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 12),
            Text(
              'You will earn +${widget.xpReward} XP upon resident confirmation.',
              style: GoogleFonts.openSans(
                color: AppColors.citrusYellow(context),
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: Text(
              'Cancel',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: Text(
              'Confirm',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _submitCompletion();
    }
  }

  Future<void> _submitCompletion() async {
    setState(() {
      _isSubmitting = true;
      _submitStatus = 'Verifying photos...';
    });
    try {
      final taskService = ref.read(taskServiceProvider);
      final taskId = int.parse(widget.taskId);
      final loc = _location!;

      final pending = _selectedImages
      .where((i) => !_submittedPaths.contains(i.path))
      .toList();

      final failed = <XFile, VerificationResult>{};
      final needsReview = <VerificationResult>[];

      for(var i = 0; i < pending.length; i++){
        final image = pending[i];
        if(mounted){
          setState(() =>
            _submitStatus = 'Verifying photo ${i + 1} of ${pending.length}...');
        }

        final result = await taskService.submitCompletionEvidence(
          taskId: taskId, 
          image: image, 
          capturedAt: _capturedAt[image.path] ?? DateTime.now(),
          lat: loc.latitude,
          lng: loc.longitude,
          accuracyM: loc.accuracy,
        );

        _submittedPaths.add(image.path);
        if (result.status == 'FAILED') {
          failed[image] = result;
        } else if (result.status == 'NEEDS_REVIEW') {
          needsReview.add(result);
        }
      }

      if(failed.isNotEmpty){
        if(mounted){
          setState(() {
            for (final img in failed.keys) {
              _selectedImages.remove(img);
              _capturedAt.remove(img.path);
              _submittedPaths.remove(img.path);
            }
          });
          await _showVerificationFailedDialog(failed.values.toList());
        }

        return;
      }

      if (mounted) setState(() => _submitStatus = 'Submitting task...');
      await taskService.updateTask(
        taskId: taskId,
        status: 'pending_approval',
        helperRatingId:
          _noteController.text.isNotEmpty ? _noteController.text : null,
      );

      Task.updateTaskStatus(widget.taskId, "pending_approval");

      if(mounted){
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(needsReview.isEmpty
                ? 'Task submitted! Waiting for resident confirmation.'
                : 'Task submitted. Some photos were flagged for manual review.'),
            backgroundColor: AppColors.primaryTeal(context),
          ),
        );
        Navigator.pop(context);
      }
    } on Exception catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(e.toString().replaceAll('Exception: ', '')),
            backgroundColor: Colors.red,
          ),
        );
      }
    } finally {
      if (mounted) setState(() => _isSubmitting = false);
    }
  }

  Future<void> _showVerificationFailedDialog(
      List<VerificationResult> failed) async {
    await showDialog<void>(
      context: context,
      builder: (ctx) => AlertDialog(
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: Text(
          'Photo Not Accepted',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontWeight: FontWeight.w600,
          ),
        ),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '${failed.length} photo(s) could not be verified and were removed. '
                'Please retake them at the task location.',
                style: GoogleFonts.openSans(color: AppColors.charcoal(context)),
              ),
              for (final r in failed) ...[
                const SizedBox(height: 12),
                if (r.aiInsight != null)
                  Text(
                    r.aiInsight!,
                    style: GoogleFonts.openSans(
                      color: AppColors.textGrey(context),
                      fontSize: 13,
                    ),
                  ),
                if (r.locationVerified == false && r.distanceM != null)
                  Text(
                    'You were ${r.distanceM!.round()} m from the task '
                    '(allowed: ${r.geofenceRadiusM?.round() ?? '?'} m).',
                    style: GoogleFonts.openSans(
                      color: AppColors.textGrey(context),
                      fontSize: 13,
                    ),
                  ),
              ],
            ],
          ),
        ),
        actions: [
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx),
            style: ElevatedButton.styleFrom(
              backgroundColor: AppColors.primaryTeal(context),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(24),
              ),
            ),
            child: Text(
              'OK',
              style: GoogleFonts.openSans(
                color: Colors.white,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Task Completion',
          style: GoogleFonts.poppins(
            fontWeight: FontWeight.w600,
            color: AppColors.charcoal(context),
          ),
        ),
        backgroundColor: AppColors.background(context),
        elevation: 0,
        foregroundColor: AppColors.charcoal(context),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Task Info Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: isDarkMode ? AppColors.surfaceGrey(context) : AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.circular(16),
                boxShadow: [
                  BoxShadow(
                    color: isDarkMode 
                        ? Colors.black.withValues(alpha: 0.2) 
                        : Colors.grey.shade200,
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  ),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.taskTitle,
                    style: GoogleFonts.poppins(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Resident: ${widget.residentName}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Due: ${widget.dueDate}',
                    style: GoogleFonts.openSans(
                      fontSize: 14,
                      color: AppColors.charcoal(context),
                    ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '+${widget.xpReward} XP',
                    style: GoogleFonts.openSans(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: AppColors.citrusYellow(context),
                    ),
                  ),
                ],
              ),
            ),

            const SizedBox(height: 24),

            // Completion Proof Section
            Row(
              children: [
                Text(
                  'Completion Proof',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal(context),
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  '*',
                  style: GoogleFonts.poppins(
                    color: AppColors.error(context),
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Spacer(),
                Text(
                  '${_selectedImages.length}/$_minImages',
                  style: GoogleFonts.openSans(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                    color: _selectedImages.length >= _minImages
                        ? AppColors.primaryTeal(context)
                        : AppColors.textGrey(context),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              'Take at least $_minImages photos of your work using the camera.',
              style: GoogleFonts.openSans(
                fontSize: 12,
                color: AppColors.textGrey(context),
              ),
            ),
            const SizedBox(height: 12),

            // Photo Grid
            SizedBox(
              height: 100,
              child: ListView.builder(
                scrollDirection: Axis.horizontal,
                itemCount: _selectedImages.length + 1,
                itemBuilder: (context, index) {
                  // Add Photo Button
                  if (index == _selectedImages.length) {
                    if (_selectedImages.length >= _maxImages) {
                      return const SizedBox.shrink();
                    }
                    return GestureDetector(
                      onTap: _addPhoto,
                      child: Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          border: Border.all(
                            color: AppColors.primaryTeal(context),
                            width: 2,
                          ),
                          borderRadius: BorderRadius.circular(12),
                          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
                        ),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Icon(
                              Icons.camera_alt_outlined,
                              color: AppColors.primaryTeal(context),
                              size: 32,
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Take Photo',
                              style: GoogleFonts.openSans(
                                fontSize: 10,
                                color: AppColors.primaryTeal(context),
                              ),
                            ),
                          ],
                        ),
                      ),
                    );
                  }

                  // Photo Item
                  return Stack(
                    children: [
                      Container(
                        width: 100,
                        height: 100,
                        margin: const EdgeInsets.only(right: 12),
                        decoration: BoxDecoration(
                          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.grey.shade300,
                          borderRadius: BorderRadius.circular(12),
                          image: DecorationImage(
                            image: FileImage(File(_selectedImages[index].path)),
                            fit: BoxFit.cover,
                          ),
                        ),
                        child: null,
                      ),
                      Positioned(
                        top: 4,
                        right: 4,
                        child: GestureDetector(
                          onTap: () => _removePhoto(index),
                          child: Container(
                            decoration: const BoxDecoration(
                              color: Colors.red,
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(
                              Icons.close,
                              size: 20,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                    ],
                  );
                },
              ),
            ),

            const SizedBox(height: 24),

            // ===== LOCATION (REQUIRED) =====
            Row(
              children: [
                Text(
                  'Location',
                  style: GoogleFonts.poppins(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: AppColors.charcoal(context),
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  '*',
                  style: GoogleFonts.poppins(
                    color: AppColors.error(context),
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Spacer(),
                if (_location != null)
                  Icon(
                    Icons.check_circle,
                    size: 16,
                    color: AppColors.primaryTeal(context),
                  ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              'We need your location to confirm where the task was completed.',
              style: GoogleFonts.openSans(
                fontSize: 12,
                color: AppColors.textGrey(context),
              ),
            ),
            const SizedBox(height: 8),
            GestureDetector(
              onTap: _isFetchingLocation ? null : _requestLocation,
              child: Container(
                width: double.infinity,
                padding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                decoration: BoxDecoration(
                  border: Border.all(
                    color: _location != null
                        ? AppColors.primaryTeal(context)
                        : AppColors.surfaceGrey(context),
                    width: _location != null ? 1.5 : 1,
                  ),
                  borderRadius: BorderRadius.circular(12),
                  color: isDarkMode
                      ? AppColors.surfaceGrey(context)
                      : Colors.white,
                ),
                child: Row(
                  children: [
                    Icon(
                      Icons.location_on,
                      size: 20,
                      color: AppColors.primaryTeal(context),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        _location == null
                            ? (_isFetchingLocation ? _gpsStatus : 'Tap to capture your location')
                            : 'Lat ${_location!.latitude.toStringAsFixed(5)}, '
                                'Lng ${_location!.longitude.toStringAsFixed(5)}',
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                        ),
                        overflow: TextOverflow.ellipsis,
                      ),
                    ),
                    if (_isFetchingLocation)
                      const SizedBox(
                        width: 18,
                        height: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    else
                      Text(
                        _location == null ? 'Get' : 'Update',
                        style: GoogleFonts.openSans(
                          color: AppColors.primaryTeal(context),
                          fontSize: 14,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                  ],
                ),
              ),
            ),

            const SizedBox(height: 24),

            // Completion Note
            Text(
              'Completion Note (optional)',
              style: GoogleFonts.poppins(
                fontSize: 16,
                fontWeight: FontWeight.w600,
                color: AppColors.charcoal(context),
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _noteController,
              maxLines: 4,
              decoration: InputDecoration(
                hintText: 'Tell the resident what you did...',
                hintStyle: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
              ),
            ),

            const SizedBox(height: 24),

            // Submit Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: (_isSubmitting ||
                        _selectedImages.length < _minImages ||
                        _location == null)
                    ? null
                    : _showCompletionDialog,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryTeal(context),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(28),
                  ),
                  disabledBackgroundColor: AppColors.surfaceGrey(context),
                ),
                child: _isSubmitting
                    ? Row( mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                    const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          strokeWidth: 2,
                          valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Text(
                          _submitStatus,
                            style: GoogleFonts.openSans(
                            fontSize: 13,
                            color: Colors.white,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    )
                    : Text(
                        'MARK AS COMPLETE',
                        style: GoogleFonts.poppins(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                          color: Colors.white,
                        ),
                      ),
              ),
            ),

            const SizedBox(height: 12),

            // Info Text
            Center(
              child: Text(
                'Resident will need to confirm before XP is awarded',
                style: GoogleFonts.openSans(
                  fontSize: 12,
                  color: AppColors.textGrey(context),
                ),
              ),
            ),

            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}