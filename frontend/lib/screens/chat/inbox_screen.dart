import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../models/chat_thread.dart';
import '../../constants/app_colors.dart';
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
  int _currentUserId = 0;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _initUserId();
  }

  Future<void> _initUserId() async {
    final prefs = await SharedPreferences.getInstance();
    final stored = prefs.getInt('current_user_id');
    if (stored != null) {
      setState(() => _currentUserId = stored);
    }
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
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
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
        // ADD: TabBar
        bottom: TabBar(
          controller: _tabController,
          labelColor: Colors.white,
          unselectedLabelColor: Colors.white70,
          indicatorColor: Colors.white,
          tabs: const [
            Tab(text: 'Inbox', icon: Icon(Icons.chat, size: 20)),
            Tab(text: 'Bulletin', icon: Icon(Icons.forum, size: 20)),
          ],
        ),
      ),
      // ADD: TabBarView
      body: TabBarView(
        controller: _tabController,
        children: [
          // Inbox Tab
          _isLoading
              ? Center(
                  child: CircularProgressIndicator(
                    color: AppColors.primaryTeal(context),
                  ),
                )
              : _error != null
                  ? Center(
                      child: Text(
                        _error!,
                        style: TextStyle(color: AppColors.error(context)),
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: _loadChats,
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 12, vertical: 8),
                        itemCount: _chats.length,
                        itemBuilder: (context, index) {
                          final chat = _chats[index];
                          return ChatCard(
                            chat: chat,
                            onTap: () async {
                              await Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (context) =>
                                      ChatDetailScreen(chat: chat),
                                ),
                              );
                              _loadChats();
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
          color: AppColors.primaryTeal(context).withValues(alpha: 0.1),
          borderRadius: BorderRadius.circular(24),
        ),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: 55,
              height: 55,
              decoration: BoxDecoration(
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
                            color: AppColors.citrusYellow(context),
                            borderRadius: BorderRadius.circular(12),
                          ),
                          child: Text(
                            '${chat.unreadCount}',
                            style: GoogleFonts.poppins(
                              fontSize: 12,
                              fontWeight: FontWeight.w600,
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
                        color: AppColors.primaryTeal(context),
                      ),
                      const SizedBox(width: 2),
                      Expanded(
                        child: Text(
                          chat.location,
                          style: GoogleFonts.openSans(
                            fontSize: 11,
                            fontWeight: FontWeight.w500,
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
                          ? AppColors.charcoal(context)
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