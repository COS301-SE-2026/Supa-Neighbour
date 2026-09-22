import 'dart:io';

import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import 'package:geolocator/geolocator.dart';
import '../../models/task_model.dart';
import '../../models/auth_session.dart';
import '../../constants/app_colors.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';


class CreateTaskScreen extends ConsumerStatefulWidget {
  const CreateTaskScreen({super.key});

  @override
  ConsumerState<CreateTaskScreen> createState() => _CreateTaskScreenState();
}

class _CreateTaskScreenState extends ConsumerState<CreateTaskScreen> {
  // Form controllers
  final _titleController = TextEditingController();
  final _instructionsController = TextEditingController();

  // Selected values
  String? _selectedCategory;
  DateTime _selectedDate = DateTime.now();
  TimeOfDay _selectedTime = TimeOfDay.now();

  // ===== REQUIRED EVIDENCE PHOTOS (CAMERA ONLY) =====
  final List<File> _selectedImages = [];
  final ImagePicker _picker = ImagePicker();
  static const int _minImages = 5;
  static const int _maxImages = 10;

  // ===== REQUIRED LOCATION =====
  Position? _location;
  bool _isFetchingLocation = false;

  // Categories
  final List<String> _categories = [
    'Medical Assistance',
    'Pet Care',
    'Technology Support',
    'Transportation Support',
    'Home Repair',
  ];

  @override
  void dispose() {
    _titleController.dispose();
    _instructionsController.dispose();
    super.dispose();
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 30)),
      builder: (context, child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: ColorScheme.light(
              // CHANGE: Use AppColors.primaryTeal
              primary: AppColors.primaryTeal(context),
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  Future<void> _selectTime(BuildContext context) async {
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime,
      builder: (context, child) {
        return Theme(
          data: ThemeData.light().copyWith(
            colorScheme: ColorScheme.light(
              // CHANGE: Use AppColors.primaryTeal
              primary: AppColors.primaryTeal(context),
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != _selectedTime) {
      setState(() {
        _selectedTime = picked;
      });
    }
  }

  Future<void> _captureImage() async {
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
        imageQuality: 75,
        maxWidth: 1600,
      );
      if (picked == null) return;

      setState(() => _selectedImages.add(File(picked.path)));
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

  void _removeImage(int index) {
    setState(() => _selectedImages.removeAt(index));
  }

  Widget _buildImageThumbnail(int index) {
    return Stack(
      clipBehavior: Clip.none,
      children: [
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: Image.file(
            _selectedImages[index],
            width: 80,
            height: 80,
            fit: BoxFit.cover,
          ),
        ),
        Positioned(
          top: -6,
          right: -6,
          child: GestureDetector(
            onTap: () => _removeImage(index),
            child: Container(
              padding: const EdgeInsets.all(3),
              decoration: BoxDecoration(
                color: AppColors.error(context),
                shape: BoxShape.circle,
                border: Border.all(
                  color: AppColors.background(context),
                  width: 1.5,
                ),
              ),
              child: const Icon(Icons.close, color: Colors.white, size: 12),
            ),
          ),
        ),
      ],
    );
  }

  // ===== LOCATION HANDLING =====

  Future<void> _requestLocation() async {
    if (_isFetchingLocation) return;
    setState(() => _isFetchingLocation = true);

    try {
      // 1) Is location service enabled on the device?
      final serviceEnabled =
          await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: const Text(
                  'Location services are disabled. Please enable them.'),
              backgroundColor: AppColors.error(context),
            ),
          );
        }
        return;
      }

      // 2) Permission status
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
      }
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: const Text(
                  'Location permission denied. Location is required to create a task.'),
              backgroundColor: AppColors.error(context),
            ),
          );
        }
        return;
      }

      // 3) Get current position
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );

      if (!mounted) return;
      setState(() => _location = position);

      _showLocationModal(position);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to get location: $e'),
          backgroundColor: AppColors.error(context),
        ),
      );
    } finally {
      if (mounted) setState(() => _isFetchingLocation = false);
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

  // Service
  bool _isSubmit = false;

Future<void> _submitTask() async {
  if (_titleController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter a task title')),
    );
    return;
  }
  if (_selectedCategory == null) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please select a category')),
    );
    return;
  }

  if (_location == null) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
          content: Text('Please provide your location before posting.')),
    );
    return;
  }

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

      final userId = int.tryParse(AuthSession.instance.currentUser?.id ?? '');
    if (userId == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('You must be logged in to create a task')),
      );
      return;
    }

    setState(() => _isSubmit = true);

    try {
       final taskService = ref.read(taskServiceProvider);
       final dependentId = await taskService.getDependentIdForUser(userId);
        if (dependentId == null) {
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('Could not resolve your profile. Please log out and back in.')),
            );
            setState(() => _isSubmit = false);
          }
          return;
        }

        final createdTask = await taskService.createTask(
          dependentId: dependentId,
          taskTypeId: Task.resolveTaskTypeId(_selectedCategory!),
          startDate: DateTime(
            _selectedDate.year,
            _selectedDate.month,
            _selectedDate.day,
            _selectedTime.hour,
            _selectedTime.minute,
          ),
          startTime: '${_selectedTime.hour.toString().padLeft(2, '0')}:${_selectedTime.minute.toString().padLeft(2, '0')}:00',
          isImmediate: false,
          needsSpecialist: false,
          title: _titleController.text.trim(),
          instructions: _instructionsController.text.trim().isEmpty
              ? null
              : _instructionsController.text.trim(),
        );


      final taskId = int.tryParse(createdTask.id);
      if (taskId != null) {
        taskService.matchHelpersForTask(taskId);
      }
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Task created successfully!'),
            backgroundColor: Color(0xFF2A9D8F),
          ),
        );
        Navigator.pop(context, {'taskId': taskId});
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
    if (mounted) setState(() => _isSubmit = false);
  }
  }


  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        // CHANGE: Use AppColors.background
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Create Task',
          style: GoogleFonts.poppins(
            // CHANGE: Use AppColors.charcoal
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Task Title
            Text(
              'Task Title',
              style: GoogleFonts.poppins(
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _titleController,
              decoration: InputDecoration(
                hintText: 'e.g., Water my plants',
                hintStyle: GoogleFonts.openSans(
                  // CHANGE: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.primaryTeal
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
            ),
            const SizedBox(height: 16),

            // Category Dropdown
            Text(
              'Category',
              style: GoogleFonts.poppins(
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            DropdownButtonFormField<String>(
              initialValue: _selectedCategory,
              hint: Text(
                'Select category',
                style: GoogleFonts.openSans(
                  // CHANGE: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
              ),
              decoration: InputDecoration(
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.primaryTeal
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
              items: _categories.map((category) {
                return DropdownMenuItem(
                  value: category,
                  child: Text(
                    category,
                    style: GoogleFonts.openSans(
                      // CHANGE: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                  ),
                );
              }).toList(),
              onChanged: (value) {
                setState(() {
                  _selectedCategory = value;
                });
              },
            ),
            const SizedBox(height: 16),

            // ===== LOCATION (REQUIRED) =====
            Row(
              children: [
                Text(
                  'Location',
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  '*',
                  style: GoogleFonts.poppins(
                    color: AppColors.error(context),
                    fontSize: 14,
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
              'We need your location so helpers can find the task.',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 12,
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
                            ? 'Tap to capture your location'
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
            const SizedBox(height: 16),

            // Date and Time Row
            Row(
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Date',
                        style: GoogleFonts.poppins(
                          // CHANGE: Use AppColors.charcoal
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(height: 8),
                      GestureDetector(
                        onTap: () => _selectDate(context),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                          decoration: BoxDecoration(
                            border: Border.all(
                              // CHANGE: Use AppColors.surfaceGrey
                              color: AppColors.surfaceGrey(context),
                            ),
                            borderRadius: BorderRadius.circular(12),
                            color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
                          ),
                          child: Row(
                            children: [
                              Icon(
                                Icons.calendar_today,
                                size: 20,
                                // CHANGE: Use AppColors.primaryTeal
                                color: AppColors.primaryTeal(context),
                              ),
                              const SizedBox(width: 12),
                              Text(
                                '${_selectedDate.day}/${_selectedDate.month}/${_selectedDate.year}',
                                style: GoogleFonts.openSans(
                                  // CHANGE: Use AppColors.charcoal
                                  color: AppColors.charcoal(context),
                                  fontSize: 14,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Time',
                        style: GoogleFonts.poppins(
                          // CHANGE: Use AppColors.charcoal
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      const SizedBox(height: 8),
                      GestureDetector(
                        onTap: () => _selectTime(context),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                          decoration: BoxDecoration(
                            border: Border.all(
                              // CHANGE: Use AppColors.surfaceGrey
                              color: AppColors.surfaceGrey(context),
                            ),
                            borderRadius: BorderRadius.circular(12),
                            color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
                          ),
                          child: Row(
                            children: [
                              Icon(
                                Icons.access_time,
                                size: 20,
                                // CHANGE: Use AppColors.primaryTeal
                                color: AppColors.primaryTeal(context),
                              ),
                              const SizedBox(width: 12),
                              Text(
                                _selectedTime.format(context),
                                style: GoogleFonts.openSans(
                                  // CHANGE: Use AppColors.charcoal
                                  color: AppColors.charcoal(context),
                                  fontSize: 14,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),

            // ===== TASK PHOTOS (REQUIRED, CAMERA ONLY) =====
            Row(
              children: [
                Text(
                  'Task Photos',
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(width: 4),
                Text(
                  '*',
                  style: GoogleFonts.poppins(
                    color: AppColors.error(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const Spacer(),
                Text(
                  '${_selectedImages.length}/$_minImages',
                  style: GoogleFonts.openSans(
                    color: _selectedImages.length >= _minImages
                        ? AppColors.primaryTeal(context)
                        : AppColors.textGrey(context),
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(
              'Take at least $_minImages photos of the task using your camera.',
              style: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 12,
              ),
            ),
            const SizedBox(height: 12),

            if (_selectedImages.isNotEmpty) ...[
              Wrap(
                spacing: 10,
                runSpacing: 10,
                children: [
                  for (int i = 0; i < _selectedImages.length; i++)
                    _buildImageThumbnail(i),
                ],
              ),
              const SizedBox(height: 12),
            ],

            if (_selectedImages.length < _maxImages)
              GestureDetector(
                onTap: _captureImage,
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  decoration: BoxDecoration(
                    color: AppColors.surfaceGrey(context),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color:
                          AppColors.primaryTeal(context).withValues(alpha: 0.4),
                      width: 1.5,
                    ),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.camera_alt_outlined,
                        color: AppColors.primaryTeal(context),
                        size: 22,
                      ),
                      const SizedBox(width: 8),
                      Text(
                        'Take photo',
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
            const SizedBox(height: 16),

            // Instructions
            Text(
              'Instructions',
              style: GoogleFonts.poppins(
                // CHANGE: Use AppColors.charcoal
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _instructionsController,
              maxLines: 4,
              decoration: InputDecoration(
                hintText: 'Provide details to help the helper...',
                hintStyle: GoogleFonts.openSans(
                  // CHANGE: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    // CHANGE: Use AppColors.primaryTeal
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
            ),
            const SizedBox(height: 24),

            // Submit Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: (_titleController.text.isNotEmpty &&
                        _selectedCategory != null &&
                        _location != null &&
                        _selectedImages.length >= _minImages &&
                        !_isSubmit)
                    ? _submitTask
                    : null,
                style: ElevatedButton.styleFrom(
                  backgroundColor: AppColors.primaryTeal(context),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  disabledBackgroundColor: AppColors.surfaceGrey(context),
                ),
                child: _isSubmit
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      )
                    : Text(
                        'Post Task',
                        style: GoogleFonts.openSans(
                          fontSize: 16,
                          fontWeight: FontWeight.w600,
                        ),
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