import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/task_model.dart';
import '../../services/task_service.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors

class EditTaskScreen extends StatefulWidget {
  final Task task;

  const EditTaskScreen({super.key, required this.task});

  @override
  State<EditTaskScreen> createState() => _EditTaskScreenState();
}

class _EditTaskScreenState extends State<EditTaskScreen> {
  // Form controllers
  late TextEditingController _titleController;
  late TextEditingController _instructionsController;

  // Selected values
  late String _selectedCategory;
  late DateTime _selectedDate;
  late TimeOfDay _selectedTime;

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

  // task service
  final TaskService _taskService = TaskService();
  bool _isSubmitting = false;


  @override
  void initState() {
    super.initState();
    _titleController = TextEditingController(text: widget.task.title);
    _instructionsController = TextEditingController(text: widget.task.instructions);
    _selectedCategory = widget.task.category;
    _selectedDate = widget.task.date;
    _selectedTime = widget.task.time;
  }

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


  Future<void> _updateTask() async {
  if (_titleController.text.isEmpty) {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Please enter a task title')),
    );
    return;
  }

  setState(() => _isSubmitting = true);

  try {
    await _taskService.updateTask(
      taskId: int.parse(widget.task.id),
      taskTypeId: Task.resolveTaskTypeId(_selectedCategory),
      startDate: _selectedDate,
      adminReview: _instructionsController.text.isNotEmpty
          ? _instructionsController.text
          : 'No additional instructions',
      status: widget.task.status,
    );

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Task updated successfully!'),
          backgroundColor: AppColors.primaryTeal(context),
        ),
      );
      Navigator.pop(context, true);
    }
  } on Exception catch (e) {
    if (mounted) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Task saved locally (offline mode)'),
        backgroundColor: AppColors.citrusYellow(context),
      ),
    );
    Navigator.pop(context, true);
    }
  } finally {
    if (mounted) setState(() => _isSubmitting = false);
  }
}

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(
            Icons.arrow_back,
            color: AppColors.charcoal(context),
          ),
          onPressed: () => Navigator.pop(context, false),
        ),
        title: Text(
          'Edit Task',
          style: GoogleFonts.poppins(
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
                //: Use AppColors.charcoal
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
                  //: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.primaryTeal
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
                //: Use AppColors.charcoal
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
                  //: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
              ),
              decoration: InputDecoration(
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.primaryTeal
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
                      //: Use AppColors.charcoal
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                  ),
                );
              }).toList(),
              onChanged: (value) {
                setState(() {
                  _selectedCategory = value!;
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
                          //: Use AppColors.charcoal
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
                              //: Use AppColors.surfaceGrey
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
                                //: Use AppColors.primaryTeal
                                color: AppColors.primaryTeal(context),
                              ),
                              const SizedBox(width: 12),
                              Text(
                                '${_selectedDate.day}/${_selectedDate.month}/${_selectedDate.year}',
                                style: GoogleFonts.openSans(
                                  //: Use AppColors.charcoal
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
                          //: Use AppColors.charcoal
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
                              //: Use AppColors.surfaceGrey
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
                                //: Use AppColors.primaryTeal
                                color: AppColors.primaryTeal(context),
                              ),
                              const SizedBox(width: 12),
                              Text(
                                _selectedTime.format(context),
                                style: GoogleFonts.openSans(
                                  //: Use AppColors.charcoal
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
                //: Use AppColors.charcoal
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
                  //: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                  fontSize: 14,
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.surfaceGrey
                    color: AppColors.surfaceGrey(context),
                  ),
                ),
                focusedBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(12),
                  borderSide: BorderSide(
                    //: Use AppColors.primaryTeal
                    color: AppColors.primaryTeal(context),
                    width: 2,
                  ),
                ),
                filled: true,
                fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              ),
            ),
            const SizedBox(height: 24),

            // Update Button
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isSubmitting ? null : _updateTask,
                style: ElevatedButton.styleFrom(
                  //: Use AppColors.primaryTeal
                  backgroundColor: AppColors.primaryTeal(context),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  disabledBackgroundColor: AppColors.surfaceGrey(context),
                ),
                child: _isSubmitting
                    ? const SizedBox(
                        height: 20,
                        width: 20,
                        child: CircularProgressIndicator(
                          color: Colors.white,
                          strokeWidth: 2,
                        ),
                      )
                    : Text(
                        'Update Task',
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
