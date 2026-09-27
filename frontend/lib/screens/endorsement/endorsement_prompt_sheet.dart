// frontend/lib/screens/endorsement/endorsement_prompt_sheet.dart

import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared/shared.dart' hide AppColors;
import '../../constants/app_colors.dart';
import '../../providers/service_providers.dart';

/// Bottom sheet that prompts the user to endorse a helper after a task
/// approval. Skill tags are curated by the backend; the user can select
/// up to 3 chips or skip entirely.
class EndorsementPromptSheet extends ConsumerStatefulWidget {
  final String endorseeId;
  final String endorseeName;
  final String? taskId;

  const EndorsementPromptSheet({
    super.key,
    required this.endorseeId,
    required this.endorseeName,
    this.taskId,
  });

  /// Convenience helper: shows the sheet and returns true if the user
  /// submitted at least one endorsement.
  static Future<bool> show(
    BuildContext context, {
    required String endorseeId,
    required String endorseeName,
    String? taskId,
  }) async {
    final result = await showModalBottomSheet<bool>(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      isDismissible: true,
      enableDrag: true,
      builder: (context) => EndorsementPromptSheet(
        endorseeId: endorseeId,
        endorseeName: endorseeName,
        taskId: taskId,
      ),
    );
    return result ?? false;
  }

  @override
  ConsumerState<EndorsementPromptSheet> createState() =>
      _EndorsementPromptSheetState();
}

class _EndorsementPromptSheetState
    extends ConsumerState<EndorsementPromptSheet> {
  static const int _maxSelections = 3;

  List<SkillTag> _availableTags = [];
  final Set<String> _selectedTags = {};
  bool _isLoading = true;
  bool _isSubmitting = false;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadSkillTags();
  }

  Future<void> _loadSkillTags() async {
    try {
      final service = ref.read(endorsementServiceProvider);
      final tags = await service.getSkillTags();
      if (!mounted) return;
      setState(() {
        _availableTags = tags;
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _errorMessage = 'Could not load skill tags.';
        _isLoading = false;
      });
    }
  }

  void _toggleTag(String tag) {
    setState(() {
      if (_selectedTags.contains(tag)) {
        _selectedTags.remove(tag);
      } else if (_selectedTags.length < _maxSelections) {
        _selectedTags.add(tag);
      }
    });
  }

  Future<void> _submit() async {
    if (_selectedTags.isEmpty) {
      Navigator.pop(context, false);
      return;
    }

    setState(() => _isSubmitting = true);

    try {
      final service = ref.read(endorsementServiceProvider);
      for (final tag in _selectedTags) {
        await service.createEndorsement(
          endorseeId: widget.endorseeId,
          skillTag: tag,
          taskId: widget.taskId,
        );
      }

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            _selectedTags.length == 1
                ? 'Endorsement sent!'
                : '${_selectedTags.length} endorsements sent!',
          ),
          backgroundColor: AppColors.success(context),
          duration: const Duration(seconds: 2),
        ),
      );

      Navigator.pop(context, true);
    } catch (e) {
      if (!mounted) return;
      setState(() => _isSubmitting = false);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            e.toString().replaceFirst('Exception: ', ''),
          ),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  void _skip() {
    Navigator.pop(context, false);
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(context).viewInsets.bottom,
        ),
        child: Container(
          decoration: BoxDecoration(
            color: AppColors.background(context),
            borderRadius: const BorderRadius.vertical(
              top: Radius.circular(24),
            ),
          ),
          padding: const EdgeInsets.fromLTRB(20, 12, 20, 24),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Drag handle
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: AppColors.textGrey(context).withValues(alpha: 0.3),
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 16),

              // Title
              Text(
                'Endorse ${widget.endorseeName}',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 20,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 6),

              // Subtitle
              Text(
                'Which of these best describes ${widget.endorseeName}? '
                'Pick up to $_maxSelections.',
                style: GoogleFonts.openSans(
                  color: AppColors.textGrey(context),
                  fontSize: 13,
                  height: 1.4,
                ),
              ),
              const SizedBox(height: 20),

              // Loading / error / chips
              if (_isLoading)
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 24),
                  child: Center(
                    child: CircularProgressIndicator(
                      color: AppColors.primaryTeal(context),
                    ),
                  ),
                )
              else if (_errorMessage != null)
                _buildError()
              else
                _buildChipWrap(),

              const SizedBox(height: 24),

              // Buttons
              Row(
                children: [
                  Expanded(
                    child: OutlinedButton(
                      onPressed: _isSubmitting ? null : _skip,
                      style: OutlinedButton.styleFrom(
                        side: BorderSide(
                          color: AppColors.textGrey(context),
                        ),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: Text(
                        'Skip',
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey(context),
                          fontSize: 15,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    flex: 2,
                    child: ElevatedButton(
                      onPressed: (_isSubmitting || _selectedTags.isEmpty)
                          ? null
                          : _submit,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: AppColors.primaryTeal(context),
                        padding: const EdgeInsets.symmetric(vertical: 14),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: _isSubmitting
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                color: Colors.white,
                                strokeWidth: 2,
                              ),
                            )
                          : Text(
                              _selectedTags.isEmpty
                                  ? 'Select a skill'
                                  : 'Submit endorsement',
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
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildChipWrap() {
    return Wrap(
      spacing: 10,
      runSpacing: 10,
      children: _availableTags.map((tag) {
        final isSelected = _selectedTags.contains(tag.tag);
        final isDisabled =
            !isSelected && _selectedTags.length >= _maxSelections;

        return GestureDetector(
          onTap: isDisabled ? null : () => _toggleTag(tag.tag),
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 150),
            padding: const EdgeInsets.symmetric(
              horizontal: 16,
              vertical: 10,
            ),
            decoration: BoxDecoration(
              color: isSelected
                  ? AppColors.primaryTeal(context)
                  : Colors.transparent,
              borderRadius: BorderRadius.circular(20),
              border: Border.all(
                color: isSelected
                    ? AppColors.primaryTeal(context)
                    : AppColors.textGrey(context).withValues(
                        alpha: isDisabled ? 0.2 : 0.5,
                      ),
                width: isSelected ? 2 : 1,
              ),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                if (isSelected) ...[
                  const Icon(
                    Icons.check,
                    size: 16,
                    color: Colors.white,
                  ),
                  const SizedBox(width: 6),
                ],
                Text(
                  tag.displayName,
                  style: GoogleFonts.openSans(
                    color: isSelected
                        ? Colors.white
                        : isDisabled
                            ? AppColors.textGrey(context).withValues(alpha: 0.5)
                            : AppColors.charcoal(context),
                    fontSize: 13,
                    fontWeight:
                        isSelected ? FontWeight.w600 : FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildError() {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: AppColors.error(context).withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Icon(
            Icons.error_outline,
            color: AppColors.error(context),
            size: 20,
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              _errorMessage!,
              style: GoogleFonts.openSans(
                color: AppColors.error(context),
                fontSize: 13,
              ),
            ),
          ),
          TextButton(
            onPressed: () {
              setState(() {
                _isLoading = true;
                _errorMessage = null;
              });
              _loadSkillTags();
            },
            child: const Text('Retry'),
          ),
        ],
      ),
    );
  }
}