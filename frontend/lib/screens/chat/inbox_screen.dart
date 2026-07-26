import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import '../../models/chat_thread.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors
import 'bulletin_screen.dart';
import 'chat_detail_screen.dart';
import '../../services/chat_service.dart';


class InboxScreen extends StatefulWidget {
  const InboxScreen({super.key});

  @override
  State<InboxScreen> createState() => _InboxScreenState();
}

class _InboxScreenState extends State<InboxScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final ChatService _chatService = ChatService();
  List<ChatThread> _chats = [];
  bool _isLoading = false;
  String? _error;

  static const int _currentUserId = 6;

  @override
  void initState() {
    super.initState();
   _tabController = TabController(length: 2, vsync: this);
  _loadChats();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadChats() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final List<Map<String, dynamic>> data =
          await _chatService.getChatsByUserId(_currentUserId);
      setState(() {
        _chats = data.map((c) => ChatThread.fromJson(c)).toList();
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = 'Could not load chats. Please try again.';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        // CHANGE: Use AppColors.primaryTeal
        backgroundColor: AppColors.primaryTeal(context),
        elevation: 0,
        title: Text(
          'Inbox',
          style: GoogleFonts.poppins(
            color: Colors.white,
            fontSize: 24,
            fontWeight: FontWeight.w600,
          ),
        ),
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
          bottom: TabBar(
          controller: _tabController,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(text: 'Inbox'),
            Tab(text: 'Bulletin'),
          ],
        ),
      ),
      body: TabBarView(
  controller: _tabController,
  children: [
    // Inbox Tab
      _isLoading
          ? Center(
              child: CircularProgressIndicator(
                // CHANGE: Use AppColors.primaryTeal
                color: AppColors.primaryTeal(context),
              ),
            )
          : _error != null
              ? Center(
                  child: Text(
                    _error!,
                    // CHANGE: Use AppColors.error
                    style: TextStyle(color: AppColors.error(context)),
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _loadChats,
                  child: ListView.builder(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    itemCount: _chats.length,
                    itemBuilder: (context, index) {
                      final chat = _chats[index];
                      return ChatCard(
                        chat: chat,
                        onTap: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => ChatDetailScreen(chat: chat),
                            ),
                          );
                        },
                      );
                    },
                  ),
                ),
      // Bulletin Tab
      const BulletinScreen(),
    ],
  ),
    );
  }
}

class ChatCard extends StatelessWidget {
  final ChatThread chat;
  final VoidCallback onTap;

  const ChatCard({
    super.key,
    required this.chat,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return GestureDetector(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          // CHANGE: Use AppColors.primaryTeal with alpha
          color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(24),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 55,
              height: 55,
              decoration: const BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Container(
                  width: 48,
                  height: 48,
                  decoration: BoxDecoration(
                    color: chat.avatarColor,
                    shape: BoxShape.circle,
                  ),
                  child: Center(
                    child: Text(
                      chat.name[0],
                      style: const TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          chat.name,
                          style: GoogleFonts.poppins(
                            fontSize: 16,
                            fontWeight: FontWeight.w700,
                            // CHANGE: Use AppColors.charcoal
                            color: AppColors.charcoal(context),
                          ),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                      if (chat.unreadCount > 0)
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 2,
                          ),
                          decoration: BoxDecoration(
                            // CHANGE: Use AppColors.citrusYellow
                            color: AppColors.citrusYellow(context),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            '${chat.unreadCount}',
                            style: GoogleFonts.poppins(
                              fontSize: 12,
                              fontWeight: FontWeight.w600,
                              // CHANGE: Use AppColors.charcoal
                              color: AppColors.charcoal(context),
                            ),
                          ),
                        ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Icon(
                        Icons.location_on,
                        size: 12,
                        // CHANGE: Use AppColors.primaryTeal
                        color: AppColors.primaryTeal(context),
                      ),
                      const SizedBox(width: 2),
                      Expanded(
                        child: Text(
                          chat.location,
                          style: GoogleFonts.openSans(
                            fontSize: 11,
                            fontWeight: FontWeight.w500,
                            // CHANGE: Use AppColors.textGrey
                            color: AppColors.textGrey(context),
                          ),
                          overflow: TextOverflow.ellipsis,
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  Text(
                    chat.lastMessage,
                    style: GoogleFonts.openSans(
                      fontSize: 13,
                      fontWeight: FontWeight.w400,
                      color: chat.unreadCount > 0
                          // CHANGE: Use AppColors.charcoal for unread
                          ? AppColors.charcoal(context)
                          // CHANGE: Use AppColors.textGrey for read
                          : AppColors.textGrey(context),
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
            const SizedBox(width: 8),
            Container(
              padding: const EdgeInsets.symmetric(
                horizontal: 8,
                vertical: 4,
              ),
              decoration: BoxDecoration(
                // CHANGE: Use AppColors.primaryTeal
                color: AppColors.primaryTeal(context),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                chat.timestamp,
                style: GoogleFonts.openSans(
                  fontSize: 10,
                  fontWeight: FontWeight.w600,
                  color: Colors.white,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}