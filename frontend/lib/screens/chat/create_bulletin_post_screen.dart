import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'dart:io';
import '../../components/custom_button.dart';
import '../../components/custom_field_input.dart';
import '../../constants/app_colors.dart';
import '../../services/bulletin_service.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class CreateBulletinPostScreen extends ConsumerStatefulWidget {
  const CreateBulletinPostScreen({super.key});

  @override
  ConsumerState<CreateBulletinPostScreen> createState() => _CreateBulletinPostScreenState();
}

class _CreateBulletinPostScreenState extends ConsumerState<CreateBulletinPostScreen> {
  final TextEditingController _bodyController = TextEditingController();
  String _selectedCategory = 'general';
  final List<XFile> _selectedImages = [];
  bool _isSubmitting = false;

  final List<Map<String, String>> _categories = [
    {'id': 'general', 'label': 'General'},
    {'id': 'lost_pet', 'label': 'Lost Pet'},
    {'id': 'local_event', 'label': 'Local Event'},
    {'id': 'alert', 'label': 'Alert'},
    {'id': 'free_items', 'label': 'Free Items'},
    {'id': 'complaint', 'label': 'Complaint'},
    {'id': 'admin', 'label': 'Admin Announcement'},
  ];

  final ImagePicker _imagePicker = ImagePicker();

  @override
  void dispose() {
    _bodyController.dispose();
    super.dispose();
  }

  Future<void> _pickImage() async {
    final XFile? image = await _imagePicker.pickImage(
      source: ImageSource.gallery,
      maxWidth: 800,
      maxHeight: 800,
      imageQuality: 80,
    );

    if (image != null && mounted) {
      setState(() {
        _selectedImages.add(image);
      });
    }
  }

  void _removeImage(int index) {
    setState(() {
      _selectedImages.removeAt(index);
    });
  }

  Future<void> _submitPost() async {
    if (_bodyController.text.trim().isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Please enter post content'),
          backgroundColor: AppColors.error(context),
        ),
      );
      return;
    }

    setState(() {
      _isSubmitting = true;
    });

    try {
      final bulletinService = ref.read(bulletinServiceProvider);
      String? mediaUrl;
      if (_selectedImages.isNotEmpty) {
        mediaUrl = await bulletinService.uploadImage(_selectedImages.first);
      }

      await bulletinService.createPost(
        postContent: _bodyController.text.trim(),
        category: _selectedCategory,
        mediaUrl: mediaUrl,
      );

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Post created successfully!'),
            backgroundColor: AppColors.success(context),
          ),
        );
        Navigator.pop(context, true);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to create post: ${e.toString()}'),
            backgroundColor: AppColors.error(context),
          ),
        );
        setState(() {
          _isSubmitting = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        backgroundColor: AppColors.background(context),
        elevation: 0,
        leading: IconButton(
          icon: Icon(Icons.arrow_back, color: AppColors.charcoal(context)),
          onPressed: () => Navigator.pop(context, false),
        ),
        title: Text(
          'Create Post',
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
            CustomInputField(
              label: 'Post Content',
              hintText: 'Write your announcement...',
              controller: _bodyController,
              maxLines: 6,
            ),
            const SizedBox(height: 16),
            Text(
              'Category',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            const SizedBox(height: 8),
            Container(
              padding: EdgeInsets.symmetric(horizontal: 12),
              decoration: BoxDecoration(
                border: Border.all(color: AppColors.primaryTeal(context), width: 1),
                borderRadius: BorderRadius.circular(12),
              ),
              child: DropdownButtonHideUnderline(
                child: DropdownButton<String>(
                  value: _selectedCategory,
                  isExpanded: true,
                  icon:Icon(Icons.arrow_drop_down, color: AppColors.primaryTeal(context)),
                  items: _categories.map((category) {
                    return DropdownMenuItem(
                      value: category['id'],
                      child: Text(
                        category['label']!,
                        style: GoogleFonts.openSans(
                          color: AppColors.charcoal(context),
                          fontSize: 14,
                        ),
                      ),
                    );
                  }).toList(),
                  onChanged: (value) {
                    if (value != null) {
                      setState(() {
                        _selectedCategory = value;
                      });
                    }
                  },
                ),
              ),
            ),
            const SizedBox(height: 16),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Add Photos',
                  style: GoogleFonts.poppins(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 8),
                if (_selectedImages.isNotEmpty)
                  SizedBox(
                    height: 100,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: _selectedImages.length + 1,
                      itemBuilder: (context, index) {
                        if (index == _selectedImages.length) {
                          return GestureDetector(
                            onTap: _pickImage,
                            child: Container(
                              width: 100,
                              height: 100,
                              margin: const EdgeInsets.only(right: 8),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppColors.primaryTeal(context),
                                  width: 2,
                                  style: BorderStyle.solid,
                                ),
                                borderRadius: BorderRadius.circular(12),
                                color: Colors.white,
                              ),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.add,
                                    color: AppColors.primaryTeal(context),
                                    size: 32,
                                  ),
                                  SizedBox(height: 4),
                                  Text(
                                    'Add Photo',
                                    style: TextStyle(
                                      fontSize: 10,
                                      color: AppColors.primaryTeal(context),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          );
                        }

                        return Stack(
                          children: [
                            Container(
                              width: 100,
                              height: 100,
                              margin: const EdgeInsets.only(right: 8),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12),
                                image: DecorationImage(
                                  image: kIsWeb
                                      ? NetworkImage(_selectedImages[index].path) as ImageProvider
                                      : FileImage(File(_selectedImages[index].path)) as ImageProvider,
                                  fit: BoxFit.cover,
                                ),
                              ),
                            ),
                            Positioned(
                              top: 4,
                              right: 4,
                              child: GestureDetector(
                                onTap: () => _removeImage(index),
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
                if (_selectedImages.isEmpty)
                  GestureDetector(
                    onTap: _pickImage,
                    child: Container(
                      width: double.infinity,
                      height: 100,
                      decoration: BoxDecoration(
                        border: Border.all(
                          color: AppColors.primaryTeal(context),
                          width: 2,
                          style: BorderStyle.solid,
                        ),
                        borderRadius: BorderRadius.circular(12),
                        color: Colors.white,
                      ),
                      child:  Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(
                            Icons.add_photo_alternate,
                            color: AppColors.primaryTeal(context),
                            size: 40,
                          ),
                          SizedBox(height: 4),
                          Text(
                            'Tap to add photos',
                            style: TextStyle(
                              fontSize: 14,
                              color: AppColors.primaryTeal(context),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                const SizedBox(height: 8),
                Text(
                  'Photos are optional',
                  style: GoogleFonts.openSans(
                    color: AppColors.textGrey(context),
                    fontSize: 12,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            CustomButton(
              text: 'POST',
              onTap: _isSubmitting ? null : _submitPost,
              isLoading: _isSubmitting,
            ),
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}