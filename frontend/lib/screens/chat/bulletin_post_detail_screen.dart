import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../constants/app_colors.dart';
import '../../models/bulletin_post_model.dart';
import '../../models/bulletin_comment_model.dart';
import '../../services/bulletin_service.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../providers/service_providers.dart';

class BulletinPostDetailScreen extends ConsumerStatefulWidget {
  final int postId;
class BulletinPostDetailScreen extends ConsumerStatefulWidget {
  final int postId;

  const BulletinPostDetailScreen({
    super.key,
    required this.postId,
  });


  @override
  ConsumerState<BulletinPostDetailScreen> createState() => _BulletinPostDetailScreenState();
  ConsumerState<BulletinPostDetailScreen> createState() => _BulletinPostDetailScreenState();
}

class _BulletinPostDetailScreenState extends ConsumerState<BulletinPostDetailScreen> {
class _BulletinPostDetailScreenState extends ConsumerState<BulletinPostDetailScreen> {
  final TextEditingController _commentController = TextEditingController();
  BulletinPost? _post;
  List<BulletinComment> _comments = [];
  bool _isLoading = true;
  bool _isSubmittingComment = false;
  bool _isHelpful = false;
  int _helpfulCount = 0;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }

  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    try {
      final bulletinService = ref.read(bulletinServiceProvider);
      final post = await bulletinService.getPost(widget.postId);
      final comments = await bulletinService.getComments(widget.postId);
      final bulletinService = ref.read(bulletinServiceProvider);
      final post = await bulletinService.getPost(widget.postId);
      final comments = await bulletinService.getComments(widget.postId);

      setState(() {
        _post = post;
        _comments = comments;
        _isHelpful = post.isHelpfulByUser;
        _helpfulCount = post.likeCount;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _submitComment() async {
    final content = _commentController.text.trim();
    if (content.isEmpty) return;

    setState(() {
      _isSubmittingComment = true;
    });

    try {
      final bulletinService = ref.read(bulletinServiceProvider);
      final newComment = await bulletinService.addComment(widget.postId, content);
      final bulletinService = ref.read(bulletinServiceProvider);
      final newComment = await bulletinService.addComment(widget.postId, content);
      if (!mounted) return;
      setState(() {
        _comments.insert(0, newComment);
        _commentController.clear();
        _isSubmittingComment = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _isSubmittingComment = false;
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to post comment'),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  void _toggleHelpful() async {
    try {
      final bulletinService = ref.read(bulletinServiceProvider);
      final bulletinService = ref.read(bulletinServiceProvider);
      if (_isHelpful) {
        await bulletinService.removeHelpful(widget.postId);
        await bulletinService.removeHelpful(widget.postId);
        if (!mounted) return;
        setState(() {
          _isHelpful = false;
          _helpfulCount--;
        });
      } else {
        await bulletinService.addHelpful(widget.postId);
        await bulletinService.addHelpful(widget.postId);
        if (!mounted) return;
        setState(() {
          _isHelpful = true;
          _helpfulCount++;
        });
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Failed to update helpful status'),
          backgroundColor: AppColors.error(context),
        ),
      );
    }
  }

  void _deletePost() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete Post?'),
        content: const Text('Are you sure you want to delete this post? This cannot be undone.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('Delete'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        final bulletinService = ref.read(bulletinServiceProvider);
        await bulletinService.deletePost(widget.postId);
        final bulletinService = ref.read(bulletinServiceProvider);
        await bulletinService.deletePost(widget.postId);
        if (mounted) {
          Navigator.pop(context, true);
        }
      } catch (e) {
        if (!mounted) return;
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Failed to delete post'),
            backgroundColor: AppColors.error(context),
          ),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(
        backgroundColor: AppColors.background(context),
        body: Center(
          child: CircularProgressIndicator(
            color: AppColors.primaryTeal(context),
          ),
        ),
      );
    }

    if (_post == null) {
      return Scaffold(
        backgroundColor: AppColors.background(context),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.error_outline,
                size: 64,
                color: AppColors.textGrey(context),
              ),
              const SizedBox(height: 16),
              Text(
                'Post not found',
                style: GoogleFonts.poppins(
                  color: AppColors.charcoal(context),
                  fontSize: 18,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ],
          ),
        ),
      );
    }

    final post = _post!;

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
          'Post Details',
          style: GoogleFonts.poppins(
            color: AppColors.charcoal(context),
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        actions: [
          if (post.isOwner)
            IconButton(
              icon: const Icon(Icons.delete_outline, color: Colors.red),
              onPressed: _deletePost,
            ),
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      CircleAvatar(
                        radius: 20,
                        backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                        child: Text(
                          post.authorAvatar,
                          style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.w600,
                            color: AppColors.primaryTeal(context),
                          ),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            post.authorUsername,
                            style: GoogleFonts.openSans(
                              color: AppColors.charcoal(context),
                              fontSize: 16,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                          Text(
                            _getTimeAgo(post.createdAt),
                            style: GoogleFonts.openSans(
                              color: AppColors.textGrey(context),
                              fontSize: 12,
                            ),
                          ),
                        ],
                      ),
                      const Spacer(),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
                        decoration: BoxDecoration(
                          color: Color(
                            int.parse(_getCategoryColor(post.category).replaceFirst('#', '0xFF')),
                          ).withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          _getCategoryLabel(post.category),
                          style: GoogleFonts.openSans(
                            color: Color(
                              int.parse(_getCategoryColor(post.category).replaceFirst('#', '0xFF')),
                            ),
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    post.postContent,
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 22,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 16),
                  if (post.mediaUrl != null)
                    Container(
                      width: double.infinity,
                      height: 200,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(12),
                        image: DecorationImage(
                          image: NetworkImage(post.mediaUrl!),
                          fit: BoxFit.cover,
                        ),
                      ),
                    ),
                    Container(
                      width: double.infinity,
                      height: 200,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(12),
                        image: DecorationImage(
                          image: NetworkImage(post.mediaUrl!),
                          fit: BoxFit.cover,
                        ),
                      ),
                    ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      GestureDetector(
                        onTap: _toggleHelpful,
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          decoration: BoxDecoration(
                            color: _isHelpful
                                ? AppColors.primaryTeal(context).withValues(alpha: 0.1)
                                : AppColors.surfaceGrey(context),
                            borderRadius: BorderRadius.circular(20),
                          ),
                          child: Row(
                            children: [
                              Icon(
                                _isHelpful ? Icons.thumb_up : Icons.thumb_up_outlined,
                                size: 20,
                                color: _isHelpful ? AppColors.primaryTeal(context) : AppColors.textGrey(context),
                              ),
                              const SizedBox(width: 8),
                              Text(
                                '$_helpfulCount Helpful',
                                style: GoogleFonts.openSans(
                                  color: _isHelpful ? AppColors.primaryTeal(context) : AppColors.textGrey(context),
                                  fontSize: 14,
                                  fontWeight: _isHelpful ? FontWeight.w600 : FontWeight.w400,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Comments (${_comments.length})',
                    style: GoogleFonts.poppins(
                      color: AppColors.charcoal(context),
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 8),
                  if (_comments.isEmpty)
                    Center(
                      child: Text(
                        'No comments yet. Be the first!',
                        style: GoogleFonts.openSans(
                          color: AppColors.textGrey(context),
                          fontSize: 14,
                        ),
                      ),
                    )
                  else
                    ListView.builder(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: _comments.length,
                      itemBuilder: (context, index) {
                        final comment = _comments[index];
                        return Container(
                          margin: const EdgeInsets.only(bottom: 8),
                          padding: const EdgeInsets.all(12),
                          decoration: BoxDecoration(
                            color: AppColors.surfaceGrey(context),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  CircleAvatar(
                                    radius: 14,
                                    backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                                    child: Text(
                                      comment.authorName[0],
                                      style: TextStyle(
                                        fontSize: 12,
                                        fontWeight: FontWeight.w600,
                                        color: AppColors.primaryTeal(context),
                                      ),
                                    ),
                                  ),
                                  const SizedBox(width: 8),
                                  Text(
                                    comment.authorName,
                                    style: GoogleFonts.openSans(
                                      color: AppColors.charcoal(context),
                                      fontSize: 13,
                                      fontWeight: FontWeight.w600,
                                    ),
                                  ),
                                  const Spacer(),
                                  Text(
                                    _getTimeAgo(comment.createdAt),
                                    style: GoogleFonts.openSans(
                                      color: AppColors.textGrey(context),
                                      fontSize: 10,
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 4),
                              Text(
                                comment.content,
                                style: GoogleFonts.openSans(
                                  color: AppColors.charcoal(context),
                                  fontSize: 14,
                                ),
                              ),
                            ],
                          ),
                        );
                      },
                    ),
                  const SizedBox(height: 80),
                ],
              ),
            ),
          ),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.04),
                  blurRadius: 8,
                  offset: const Offset(0, -2),
                ),
              ],
            ),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _commentController,
                    decoration: InputDecoration(
                      hintText: 'Write a comment...',
                      hintStyle: GoogleFonts.openSans(
                        color: AppColors.textGrey(context),
                        fontSize: 14,
                      ),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                        borderSide: BorderSide(color: AppColors.surfaceGrey(context)),
                      ),
                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(24),
                        borderSide: BorderSide(color: AppColors.primaryTeal(context), width: 2),
                      ),
                      contentPadding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    ),
                    onSubmitted: (_) => _submitComment(),
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  onPressed: _isSubmittingComment ? null : _submitComment,
                  icon: _isSubmittingComment
                      ? SizedBox(
                          width: 24,
                          height: 24,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            color: AppColors.primaryTeal(context),
                          ),
                        )
                      : Icon(
                          Icons.send,
                          color: AppColors.primaryTeal(context),
                          size: 28,
                        ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  String _getCategoryLabel(String category) {
    switch (category) {
      case 'all':
        return 'All';
      case 'general':
        return 'General';
      case 'lost_pet':
        return 'Lost Pet';
      case 'local_event':
        return 'Events';
      case 'alert':
        return 'Alert';
      case 'free_items':
        return 'Free Items';
      case 'complaint':
        return 'Complaint';
      case 'admin':
        return 'Admin';
      default:
        return category;
    }
  }

  String _getCategoryColor(String category) {
    switch (category) {
      case 'general':
        return '#2A9D8F';
      case 'lost_pet':
        return '#F4A261';
      case 'local_event':
        return '#E9C46A';
      case 'alert':
        return '#F44336';
      case 'free_items':
        return '#69B578';
      case 'complaint':
        return '#9B59B6';
      case 'admin':
        return '#3498DB';
      default:
        return '#2A9D8F';
    }
  }

  String _getTimeAgo(DateTime date) {
    final now = DateTime.now();
    final diff = now.difference(date);

    if (diff.inDays == 0) {
      if (diff.inHours == 0) {
        if (diff.inMinutes == 0) {
          return 'Just now';
        }
        return '${diff.inMinutes}m ago';
      }
      return '${diff.inHours}h ago';
    } else if (diff.inDays == 1) {
      return 'Yesterday';
    } else if (diff.inDays < 7) {
      return '${diff.inDays} days ago';
    } else if (diff.inDays < 30) {
      final weeks = (diff.inDays / 7).floor();
      return '$weeks week${weeks > 1 ? 's' : ''} ago';
    } else if (diff.inDays < 365) {
      final months = (diff.inDays / 30).floor();
      return '$months month${months > 1 ? 's' : ''} ago';
    } else {
      final years = (diff.inDays / 365).floor();
      return '$years year${years > 1 ? 's' : ''} ago';
    }
  }
}