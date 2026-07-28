import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../components/custom_button.dart';
import '../../constants/app_colors.dart';
import '../../models/bulletin_post_model.dart';
import '../../services/bulletin_service.dart';
import 'bulletin_post_detail_screen.dart';
import 'create_bulletin_post_screen.dart';

class BulletinScreen extends StatefulWidget {
  const BulletinScreen({super.key});

  @override
  State<BulletinScreen> createState() => _BulletinScreenState();
}

class _BulletinScreenState extends State<BulletinScreen> {
  final BulletinService _bulletinService = BulletinService();
  List<BulletinPost> _posts = [];
  bool _isLoading = true;
  String _selectedCategory = 'all';
  int _currentPage = 1;
  bool _hasMore = true;
  bool _isLoadingMore = false;

  final List<String> _categories = [
    'all',
    'general',
    'lost_pet',
    'local_event',
    'alert',
    'free_items',
    'complaint',
    'admin'
  ];

  @override
  void initState() {
    super.initState();
    _loadPosts();
  }

  Future<void> _loadPosts({bool refresh = false}) async {
    if (refresh) {
      setState(() {
        _currentPage = 1;
        _posts = [];
        _hasMore = true;
        _isLoading = true;
      });
    }

    try {
      final newPosts = await _bulletinService.getPosts(
        category: _selectedCategory == 'all' ? null : _selectedCategory,
        page: _currentPage,
        limit: 10,
      );
      if (!mounted) return;
      setState(() {
        if (refresh || _currentPage == 1) {
          _posts = newPosts;
        } else {
          _posts.addAll(newPosts);
        }
        _hasMore = newPosts.length >= 10;
        _isLoading = false;
        _isLoadingMore = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _isLoading = false;
        _isLoadingMore = false;
      });
    }
  }

  void _showFilterDialog() {
  showModalBottomSheet(
    context: context,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
    ),
    builder: (context) {
      return Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Filter by Category',
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 18,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 16),
            ..._categories.map((category) {
              final isSelected = _selectedCategory == category;
              final label = _getCategoryLabel(category);
              final color = _getCategoryColor(category);

              return ListTile(
                contentPadding: EdgeInsets.zero,
                leading: CircleAvatar(
                  radius: 10,
                  backgroundColor: Color(
                    int.parse(color.replaceFirst('#', '0xFF')),
                  ),
                ),
                title: Text(
                  label,
                  style: GoogleFonts.openSans(
                    color: isSelected ? AppColors.primaryTeal(context): AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
                  ),
                ),
                trailing: isSelected
                    ? Icon(Icons.check, color: AppColors.primaryTeal(context))
                    : null,
                onTap: () {
                  _changeCategory(category);
                  if (mounted){ 
                    Navigator.pop(context);
                  }
                },
              );
            }),
          ],
        ),
      );
    },
  );
}
  
  void _loadMore() {
    if (_hasMore && !_isLoadingMore) {
      setState(() {
        _isLoadingMore = true;
        _currentPage++;
      });
      _loadPosts();
    }
  }

  void _changeCategory(String category) {
    if (_selectedCategory != category) {
      setState(() {
        _selectedCategory = category;
        _currentPage = 1;
        _posts = [];
        _isLoading = true;
        _hasMore = true;
      });
      _loadPosts(refresh: true);
    }
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

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      backgroundColor: AppColors.background(context),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    decoration: InputDecoration(
                      hintText: 'Search posts...',
                      hintStyle: GoogleFonts.openSans(
                        color: AppColors.textGrey(context),
                        fontSize: 14,
                      ),
                      prefixIcon:  Icon(Icons.search, color: AppColors.textGrey(context)),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                        borderSide:  BorderSide(color: AppColors.surfaceGrey(context)),
                      ),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                        borderSide: BorderSide(color: AppColors.surfaceGrey(context)),
                      ),
                      focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                        borderSide: BorderSide(color: AppColors.primaryTeal(context), width: 2),
                      ),
                      filled: true,
                      // CHANGE: Use dynamic fill color
                      fillColor: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
                      contentPadding: const EdgeInsets.symmetric(vertical: 4),
                    ),
                    style: GoogleFonts.openSans(
                      color: AppColors.charcoal(context),
                      fontSize: 14,
                    ),
                    onSubmitted: (value) {
                      //Need to Implement search
                    },
                  ),
                ),
                const SizedBox(width: 8),
               
                GestureDetector(
                  onTap: () => _showFilterDialog(),
                  child: Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: _selectedCategory != 'all'
                          ? AppColors.primaryTeal(context)
                          : AppColors.surfaceGrey(context),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: _selectedCategory != 'all'
                            ? AppColors.primaryTeal(context)
                            : AppColors.surfaceGrey(context),
                        width: 1,
                      ),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          Icons.filter_list,
                          size: 20,
                          color: _selectedCategory != 'all'
                              ? Colors.white
                              : AppColors.textGrey(context),
                        ),
                        if (_selectedCategory != 'all') ...[
                          const SizedBox(width: 4),
                          Text(
                            '1',
                            style: GoogleFonts.openSans(
                              color: Colors.white,
                              fontSize: 10,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          Expanded(
            child: _isLoading
                ? Center(
                    child: CircularProgressIndicator(
                      color: AppColors.primaryTeal(context),
                    ),
                  )
                : _posts.isEmpty
                    ? _buildEmptyState()
                    : NotificationListener<ScrollNotification>(
                        onNotification: (scrollInfo) {
                          if (scrollInfo.metrics.pixels ==
                              scrollInfo.metrics.maxScrollExtent) {
                            _loadMore();
                          }
                          return false;
                        },
                        child: ListView.builder(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          itemCount: _posts.length + (_hasMore ? 1 : 0),
                          itemBuilder: (context, index) {
                            if (index == _posts.length) {
                              return _isLoadingMore
                                  ? Padding(
                                      padding: EdgeInsets.all(16),
                                      child: Center(
                                        child: CircularProgressIndicator(
                                          color: AppColors.primaryTeal(context),
                                          strokeWidth: 2,
                                        ),
                                      ),
                                    )
                                  : const SizedBox.shrink();
                            }
                            return _buildPostCard(_posts[index]);
                          },
                        ),
                      ),
          ),
        ],
      ),
      // FAB stays in original position
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const CreateBulletinPostScreen(),
            ),
          );
          if (result == true) {
            _loadPosts(refresh: true);
          }
        },
        backgroundColor: AppColors.primaryTeal(context),
        child: const Icon(Icons.add, color: Colors.white),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.forum_outlined,
            size: 64,
            color: AppColors.textGrey(context).withValues(alpha: 0.5),
          ),
          const SizedBox(height: 16),
          Text(
            'No posts yet',
            style: GoogleFonts.poppins(
              color: AppColors.charcoal(context),
              fontSize: 18,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            'Be the first to share an announcement!',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 16),
          CustomButton(
            text: 'Create Post',
            onTap: () async {
              final result = await Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => const CreateBulletinPostScreen(),
                ),
              );
              if (result == true) {
                _loadPosts(refresh: true);
              }
            },
            width: 200,
          ),
        ],
      ),
    );
  }

  Widget _buildPostCard(BulletinPost post) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    final categoryColor = _getCategoryColor(post.category);

    return GestureDetector(
      onTap: () async {
        final result = await Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => BulletinPostDetailScreen(
              postId: post.id,
            ),
          ),
        );
        if (result == true) {
          _loadPosts(refresh: true);
        }
      },
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          // CHANGE: Use dynamic color based on theme
          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withValues(alpha: 0.04),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  radius: 16,
                  backgroundColor: AppColors.primaryTeal(context).withValues(alpha: 0.1),
                  child: Text(
                    post.authorAvatar,
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                      color: AppColors.primaryTeal(context),
                    ),
                  ),
                ),
                const SizedBox(width: 8),
                Text(
                  post.authorName,
                  style: GoogleFonts.openSans(
                    color: AppColors.charcoal(context),
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Color(int.parse(categoryColor.replaceFirst('#', '0xFF'))).withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    _getCategoryLabel(post.category),
                    style: GoogleFonts.openSans(
                      color: Color(int.parse(categoryColor.replaceFirst('#', '0xFF'))),
                      fontSize: 10,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const Spacer(),
                Text(
                  _getTimeAgo(post.createdAt),
                  style: GoogleFonts.openSans(
                    color: AppColors.textGrey(context),
                    fontSize: 11,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              post.title,
              style: GoogleFonts.poppins(
                color: AppColors.charcoal(context),
                fontSize: 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              post.body,
              style: GoogleFonts.openSans(
                color: AppColors.charcoal(context),
                fontSize: 14,
                height: 1.4,
              ),
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 8),
            if (post.imageUrls.isNotEmpty)
              SizedBox(
                height: 80,
                child: ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: post.imageUrls.length > 3 ? 3 : post.imageUrls.length,
                  itemBuilder: (context, index) {
                    return Container(
                      width: 80,
                      height: 80,
                      margin: const EdgeInsets.only(right: 8),
                      decoration: BoxDecoration(
                        color: AppColors.surfaceGrey(context),
                        borderRadius: BorderRadius.circular(8),
                        image: DecorationImage(
                          image: NetworkImage(post.imageUrls[index]),
                          fit: BoxFit.cover,
                        ),
                      ),
                    );
                  },
                ),
              ),
            const SizedBox(height: 8),
            Row(
              children: [
                GestureDetector(
                  onTap: () {
                    _toggleHelpful(post);
                  },
                  child: Row(
                    children: [
                      Icon(
                        post.isHelpfulByUser ? Icons.thumb_up : Icons.thumb_up_outlined,
                        size: 16,
                        color: post.isHelpfulByUser ? AppColors.primaryTeal(context): AppColors.textGrey(context),
                      ),
                      const SizedBox(width: 4),
                      Text(
                        post.helpfulCount.toString(),
                        style: GoogleFonts.openSans(
                          color: post.isHelpfulByUser ? AppColors.primaryTeal(context): AppColors.textGrey(context),
                          fontSize: 12,
                          fontWeight: post.isHelpfulByUser ? FontWeight.w600 : FontWeight.w400,
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Row(
                  children: [
                    Icon(
                      Icons.comment_outlined,
                      size: 16,
                      color: AppColors.textGrey(context),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      post.commentCount.toString(),
                      style: GoogleFonts.openSans(
                        color: AppColors.textGrey(context),
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
                const Spacer(),
                GestureDetector(
                  onTap: () {
                    _showReportDialog(post);
                  },
                  child: Icon(
                    Icons.flag_outlined,
                    size: 16,
                    color: post.isReported ? Colors.red : AppColors.textGrey(context),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

Future<void> _toggleHelpful(BulletinPost post) async {
  try {
    final index = _posts.indexWhere((p) => p.id == post.id);
    if (index == -1) return;

    if (post.isHelpfulByUser) {
      await _bulletinService.removeHelpful(post.id);
      if (!mounted) return;
      
      // Create updated post with helpful count decreased
      final updatedPost = BulletinPost(
        id: post.id,
        title: post.title,
        body: post.body,
        category: post.category,
        authorId: post.authorId,
        authorName: post.authorName,
        authorAvatar: post.authorAvatar,
        imageUrls: post.imageUrls,
        helpfulCount: post.helpfulCount - 1,
        commentCount: post.commentCount,
        createdAt: post.createdAt,
        isOwner: post.isOwner,
        isReported: post.isReported,
        isHelpfulByUser: false,
        isExpired: post.isExpired,
      );
      
      setState(() {
        _posts[index] = updatedPost;
      });
    } else {
      await _bulletinService.addHelpful(post.id);
      if (!mounted) return;
      
      // Create updated post with helpful count increased
      final updatedPost = BulletinPost(
        id: post.id,
        title: post.title,
        body: post.body,
        category: post.category,
        authorId: post.authorId,
        authorName: post.authorName,
        authorAvatar: post.authorAvatar,
        imageUrls: post.imageUrls,
        helpfulCount: post.helpfulCount + 1,
        commentCount: post.commentCount,
        createdAt: post.createdAt,
        isOwner: post.isOwner,
        isReported: post.isReported,
        isHelpfulByUser: true,
        isExpired: post.isExpired,
      );
      
      setState(() {
        _posts[index] = updatedPost;
      });
    }
  } catch (e) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('Failed to update helpful status'),
        backgroundColor: AppColors.error(context),
        duration: const Duration(seconds: 2),
      ),
    );
  }
}

 void _showReportDialog(BulletinPost post) {
  final TextEditingController reasonController = TextEditingController();

  showDialog(
    context: context,
    builder: (context) => AlertDialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
      ),
      title: Text(
        'Report Post',
        style: GoogleFonts.poppins(
          color: AppColors.charcoal(context),
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            'Why are you reporting this post?',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          ),
          const SizedBox(height: 12),
          TextField(
            controller: reasonController,
            maxLines: 3,
            style: GoogleFonts.openSans(
              color: AppColors.charcoal(context),
              fontSize: 14,
            ),
            decoration: InputDecoration(
              hintText: 'Enter reason...',
              hintStyle: GoogleFonts.openSans(
                color: AppColors.textGrey(context),
                fontSize: 14,
              ),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(color: AppColors.surfaceGrey(context)),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(color: AppColors.surfaceGrey(context)),
              ),
              focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(12),
                borderSide: BorderSide(color: AppColors.primaryTeal(context), width: 2),
              ),
              contentPadding: const EdgeInsets.all(12),
            ),
          ),
        ],
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(
            'Cancel',
            style: GoogleFonts.openSans(
              color: AppColors.textGrey(context),
              fontSize: 14,
            ),
          ),
        ),
        ElevatedButton(
          onPressed: () async {
            if (reasonController.text.isNotEmpty) {
              try {
                await _bulletinService.reportPost(post.id, reasonController.text);
                
                
                if (!mounted) {
                  if (context.mounted) Navigator.pop(context);
                  return;
                }
                
                setState(() {
                  final index = _posts.indexWhere((p) => p.id == post.id);
                  if (index != -1) {
                    _posts[index] = BulletinPost(
                      id: post.id,
                      title: post.title,
                      body: post.body,
                      category: post.category,
                      authorId: post.authorId,
                      authorName: post.authorName,
                      authorAvatar: post.authorAvatar,
                      imageUrls: post.imageUrls,
                      helpfulCount: post.helpfulCount,
                      commentCount: post.commentCount,
                      createdAt: post.createdAt,
                      isOwner: post.isOwner,
                      isReported: true,
                      isHelpfulByUser: post.isHelpfulByUser,
                      isExpired: post.isExpired,
                    );
                  }
                });
                
                if (context.mounted) Navigator.pop(context);
                
                
                if (!context.mounted) return;
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(
                    content: Text('Post reported successfully'),
                    backgroundColor: AppColors.success(context),
                    duration: const Duration(seconds: 2),
                  ),
                );
              } catch (e) {
                if (context.mounted) Navigator.pop(context);
                
                if (!context.mounted) return;
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(
                    content: Text('Failed to report post'),
                    backgroundColor: AppColors.error(context),
                    duration: const Duration(seconds: 2),
                  ),
                );
              }
            }
          },
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.red,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
          child: Text(
            'Report',
            style: GoogleFonts.openSans(
              color: Colors.white,
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
      ],
    ),
  );
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