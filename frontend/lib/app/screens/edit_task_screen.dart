import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class EditTaskScreen extends StatefulWidget {
  final String taskId;
  const EditTaskScreen({super.key, required this.taskId});

  @override
  State<EditTaskScreen> createState() => _EditTaskScreenState();
}

class _EditTaskScreenState extends State<EditTaskScreen> {
  final _titleController = TextEditingController();
  final _instructionsController = TextEditingController();
  
  String? _selectedCategory;
  DateTime _selectedDate = DateTime.now();
  TimeOfDay _selectedTime = TimeOfDay.now();
  int _xpReward = 50;
  
  final List<String> _categories = [
    'Plants', 'Pets', 'Bins', 'Packages', 'Home Check-in', 'Pool Pump', 'Other',
  ];

  // Mock existing task data - would come from API
  @override
  void initState() {
    super.initState();
    // Simulate loading existing task
    _titleController.text = 'Water my plants';
    _instructionsController.text = 'Please water the 3 pots on the balcony.';
    _selectedCategory = 'Plants';
    _xpReward = 50;
  }

  @override
  void dispose() {
    _titleController.dispose();
    _instructionsController.dispose();
    super.dispose();
  }

  Future<void> _selectDate(BuildContext context) async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 30)),
      builder: (context, child) => Theme(
        data: ThemeData.light().copyWith(
          colorScheme: const ColorScheme.light(primary: Color(0xFF2A9D8F)),
        ),
        child: child!,
      ),
    );
    if (picked != null) setState(() => _selectedDate = picked);
  }

  Future<void> _selectTime(BuildContext context) async {
    final picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime,
      builder: (context, child) => Theme(
        data: ThemeData.light().copyWith(
          colorScheme: const ColorScheme.light(primary: Color(0xFF2A9D8F)),
        ),
        child: child!,
      ),
    );
    if (picked != null) setState(() => _selectedTime = picked);
  }

  void _saveChanges() {
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Task updated! (Coming soon)')),
    );
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFFFFFF),
      appBar: AppBar(
        backgroundColor: const Color(0xFFFFFFFF),
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Color(0xFF264653)),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text(
          'Edit Task',
          style: GoogleFonts.poppins(
            color: const Color(0xFF264653),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          TextButton(
            onPressed: () {
              // TODO: Delete task
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Delete task (Coming soon)')),
              );
            },
            child: Text(
              'Delete',
              style: GoogleFonts.openSans(
                color: const Color(0xFFF4A261),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildTextField('Task Title', _titleController, hint: 'e.g., Water my plants'),
            const SizedBox(height: 16),
            _buildDropdown(),
            const SizedBox(height: 16),
            _buildDateTimeRow(),
            const SizedBox(height: 16),
            _buildXPSlider(),
            const SizedBox(height: 16),
            _buildTextField('Instructions', _instructionsController, maxLines: 4,
                hint: 'Provide details to help the helper...'),
            const SizedBox(height: 24),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _saveChanges,
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF2A9D8F),
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                ),
                child: Text('Save Changes', style: GoogleFonts.openSans(fontSize: 16, fontWeight: FontWeight.w600)),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTextField(String label, TextEditingController controller,
      {int maxLines = 1, String hint = ''}) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 14, fontWeight: FontWeight.w500)),
        const SizedBox(height: 8),
        TextField(
          controller: controller,
          maxLines: maxLines,
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: GoogleFonts.openSans(color: const Color(0xFFB0ADB0), fontSize: 14),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFFE5E2E0))),
            enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFFE5E2E0))),
            focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFF2A9D8F), width: 2)),
            filled: true,
            fillColor: Colors.white,
          ),
        ),
      ],
    );
  }

  Widget _buildDropdown() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Category', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 14, fontWeight: FontWeight.w500)),
        const SizedBox(height: 8),
        DropdownButtonFormField<String>(
         initialValue: _selectedCategory,
          hint: Text('Select category', style: GoogleFonts.openSans(color: const Color(0xFFB0ADB0), fontSize: 14)),
          decoration: InputDecoration(
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFFE5E2E0))),
            enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFFE5E2E0))),
            focusedBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(12), borderSide: const BorderSide(color: Color(0xFF2A9D8F), width: 2)),
            filled: true,
            fillColor: Colors.white,
          ),
          items: _categories.map((c) => DropdownMenuItem(value: c, child: Text(c, style: GoogleFonts.openSans(color: const Color(0xFF264653))))).toList(),
          onChanged: (v) => setState(() => _selectedCategory = v),
        ),
      ],
    );
  }

  Widget _buildDateTimeRow() {
    return Row(
      children: [
        Expanded(child: _buildDatePicker()),
        const SizedBox(width: 16),
        Expanded(child: _buildTimePicker()),
      ],
    );
  }

  Widget _buildDatePicker() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Date', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 14, fontWeight: FontWeight.w500)),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: () => _selectDate(context),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            decoration: BoxDecoration(border: Border.all(color: const Color(0xFFE5E2E0)), borderRadius: BorderRadius.circular(12), color: Colors.white),
            child: Row(
              children: [
                const Icon(Icons.calendar_today, size: 20, color: Color(0xFF2A9D8F)),
                const SizedBox(width: 12),
                Text('${_selectedDate.day}/${_selectedDate.month}/${_selectedDate.year}',
                    style: GoogleFonts.openSans(color: const Color(0xFF264653))),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildTimePicker() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Time', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 14, fontWeight: FontWeight.w500)),
        const SizedBox(height: 8),
        GestureDetector(
          onTap: () => _selectTime(context),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
            decoration: BoxDecoration(border: Border.all(color: const Color(0xFFE5E2E0)), borderRadius: BorderRadius.circular(12), color: Colors.white),
            child: Row(
              children: [
                const Icon(Icons.access_time, size: 20, color: Color(0xFF2A9D8F)),
                const SizedBox(width: 12),
                Text(_selectedTime.format(context), style: GoogleFonts.openSans(color: const Color(0xFF264653))),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildXPSlider() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('XP Reward', style: GoogleFonts.poppins(color: const Color(0xFF264653), fontSize: 14, fontWeight: FontWeight.w500)),
        const SizedBox(height: 8),
        Row(
          children: [
            Expanded(
              child: Slider(
                value: _xpReward.toDouble(),
                min: 10, max: 200, divisions: 19,
                activeColor: const Color(0xFF2A9D8F),
                onChanged: (v) => setState(() => _xpReward = v.toInt()),
              ),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
              decoration: BoxDecoration(color: const Color(0xFFE9C46A), borderRadius: BorderRadius.circular(20)),
              child: Text('+$_xpReward XP', style: GoogleFonts.openSans(color: const Color(0xFF264653), fontWeight: FontWeight.w600)),
            ),
          ],
        ),
      ],
    );
  }
}