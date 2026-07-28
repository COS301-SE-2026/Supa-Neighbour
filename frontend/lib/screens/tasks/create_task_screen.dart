import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../services/task_service.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors

class CreateTaskScreen extends StatefulWidget {
  const CreateTaskScreen({super.key});

  @override
  State<CreateTaskScreen> createState() => _CreateTaskScreenState();
}

class _CreateTaskScreenState extends State<CreateTaskScreen> {
  // Form controllers
  final _titleController = TextEditingController();
  final _instructionsController = TextEditingController();

  // Selected values
  String? _selectedCategory;
  DateTime _selectedDate = DateTime.now();
  TimeOfDay _selectedTime = TimeOfDay.now();

  // Categories
  final List<String> _categories = [
    'Plants',
    'Pets',
    'Bins',
    'Packages',
    'Home Check-in',
    'Pool Pump',
    'Other',
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

  // Service
  final TaskService _taskService = TaskService();
  bool _isSubmit = false;

  void _submitTask() async {
    // Validate required fields
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

    setState(() => _isSubmit = true);
    
    try {
      await _taskService.createTask(
        dependentId: 1, // will update to auth users
        taskTypeId: Task.resolveTaskTypeId(_selectedCategory!),
        startDate: _selectedDate,
        isImmediate: false,
        needsSpecialist: false,
      );

      if(mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Task created successfully!'),
            // CHANGE: Use AppColors.primaryTeal
            backgroundColor: AppColors.primaryTeal(context),
          ),
        );
        Navigator.pop(context, true);
      }
    } catch(e) {
      // fallback to what we mocked
      // Create new task with all required parameters
      final newTask = Task(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        title: _titleController.text,
        category: _selectedCategory!,
        date: _selectedDate,
        time: _selectedTime,
        xpReward: 50, // Default XP reward
        instructions: _instructionsController.text.isNotEmpty
            ? _instructionsController.text
            : 'No additional instructions',
        status: 'open',  // New task starts as 'open' (waiting for helper)
        createdAt: DateTime.now(),
        createdBy: 'currentUser',  // The current user is the creator
        requesterName: 'You',       // Display name for requester
        helperId: null,             // No helper yet
        helperName: null,           // No helper yet
      );
      // Add to mock data list
      Task.addMockTask(newTask);

      if(mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Task saved locally (offline mode)'),
            // CHANGE: Use AppColors.citrusYellow
            backgroundColor: AppColors.citrusYellow(context),
          ),
        );
        // Navigate back
        Navigator.pop(context, true);
      }
    } finally {
      if(mounted) setState(() => _isSubmit = false);
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
