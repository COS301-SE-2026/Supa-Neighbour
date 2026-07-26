import 'dart:io';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:image_picker/image_picker.dart';
import '../../models/chat_thread.dart';
import '../../services/chat_service.dart';
import '../../constants/app_colors.dart'; // ADD: Import AppColors

class ChatDetailScreen extends StatefulWidget {
  final ChatThread chat;

  const ChatDetailScreen({super.key, required this.chat});

  @override
  State<ChatDetailScreen> createState() => _ChatDetailScreenState();
}

class _ChatDetailScreenState extends State<ChatDetailScreen> {
  final TextEditingController _messageController = TextEditingController();
  final List<ChatMessageWidget> _messages = [];
  final ImagePicker _picker = ImagePicker();
  File? _selectedImage;
  // service
  final ChatService _chatService = ChatService();
  bool _isSending = false;
  static const int _currentUserId = 6; // auth usr update


  @override
  void initState() {
    super.initState();
    _loadMessages();
  }


Future<void> _loadMessages() async {
  try {
    final data = await _chatService.getMessages(widget.chat.chatId);
    final msgs = data['messages'] as List<dynamic>;
    setState(() {
      _messages.clear();
      for (final m in msgs) {
        _messages.add(ChatMessageWidget(
          text: m['content'] as String,
          isMe: (m['senderID'] as int) == _currentUserId,
          time: _formatTimestamp(m['timestamp'] as String?),
        ));
      }
    });
  } catch (e) {
    //existing state kept on failure
  }
}

String _formatTimestamp(String? raw) {
  if (raw == null) return _getCurrentTime();
  try {
    final dt = DateTime.parse(raw);
    final h = dt.hour % 12 == 0 ? 12 : dt.hour % 12;
    final m = dt.minute.toString().padLeft(2, '0');
    final ampm = dt.hour >= 12 ? 'PM' : 'AM';
    return '$h:$m $ampm';
  } catch (_) {
    return _getCurrentTime();
  }
}


  Future<void> _pickImage() async {
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      setState(() {
        _selectedImage = File(image.path);
      });
      _showImagePreview();
    }
  }

  void _showImagePreview() {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(20),
          // CHANGE: Use dynamic color based on theme
          color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Preview Image',
                style: GoogleFonts.poppins(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                  // CHANGE: Use AppColors.charcoal
                  color: AppColors.charcoal(context),
                ),
              ),
              const SizedBox(height: 16),
              ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Image.file(
                  _selectedImage!,
                  height: 200,
                  width: double.infinity,
                  fit: BoxFit.cover,
                ),
              ),
              const SizedBox(height: 16),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.pop(context);
                        _sendImage();
                      },
                      style: ElevatedButton.styleFrom(
                        // CHANGE: Use AppColors.primaryTeal
                        backgroundColor: AppColors.primaryTeal(context),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                      ),
                      child: Text(
                        'Send Image',
                        style: GoogleFonts.openSans(
                          color: Colors.white,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton(
                      onPressed: () {
                        setState(() {
                          _selectedImage = null;
                        });
                        Navigator.pop(context);
                      },
                      style: OutlinedButton.styleFrom(
                        // CHANGE: Use AppColors.primaryTeal
                        side: BorderSide(color: AppColors.primaryTeal(context)),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                      ),
                      child: Text(
                        'Cancel',
                        style: GoogleFonts.openSans(
                          // CHANGE: Use AppColors.primaryTeal
                          color: AppColors.primaryTeal(context),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        );
      },
    );
  }

  void _sendImage() {
    if (_selectedImage != null) {
      setState(() {
        _messages.add(
          ChatMessageWidget(
            text: '📷 Image shared',
            isMe: true,
            time: _getCurrentTime(),
            isImage: true,
            imageFile: _selectedImage,
          ),
        );
        _selectedImage = null;
      });
    }
  }

  Future<void> _sendMessage() async {
  final text = _messageController.text.trim();
  if (text.isEmpty || _isSending) return;

  setState(() => _isSending = true);
  _messageController.clear();

  setState(() {
    _messages.add(ChatMessageWidget(
      text: text,
      isMe: true,
      time: _getCurrentTime(),
    ));
  });

  try {
    await _chatService.sendMessage(widget.chat.chatId, _currentUserId, text);
  } catch (e) {
    // message already shown in UI — fail silently for now
  } finally {
    if (mounted) setState(() => _isSending = false);
  }
}


  String _getCurrentTime() {
    final now = DateTime.now();
    final hour = now.hour;
    final minute = now.minute.toString().padLeft(2, '0');
    final ampm = hour >= 12 ? 'PM' : 'AM';
    final displayHour = hour % 12 == 0 ? 12 : hour % 12;
    return '$displayHour:$minute $ampm';
  }

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Scaffold(
      // CHANGE: Use AppColors.background
      backgroundColor: AppColors.background(context),
      appBar: AppBar(
        // CHANGE: Use AppColors.primaryTeal
        backgroundColor: AppColors.primaryTeal(context),
        elevation: 0,
        title: Row(
          children: [
            // Avatar
            Container(
              width: 40,
              height: 40,
              decoration: const BoxDecoration(
                color: Colors.white,
                shape: BoxShape.circle,
              ),
              child: Center(
                child: Container(
                  width: 34,
                  height: 34,
                  decoration: BoxDecoration(
                    color: widget.chat.avatarColor,
                    shape: BoxShape.circle,
                  ),
                  child: Center(
                    child: Text(
                      widget.chat.name[0],
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.w600,
                        color: Colors.white,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            const SizedBox(width: 12),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.chat.name,
                  style: GoogleFonts.poppins(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                Row(
                  children: [
                    const Icon(
                      Icons.location_on,
                      size: 12,
                      color: Colors.white70,
                    ),
                    const SizedBox(width: 2),
                    Text(
                      widget.chat.location,
                      style: GoogleFonts.openSans(
                        fontSize: 11,
                        color: Colors.white70,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ],
        ),
        centerTitle: false,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pop(context);
          },
        ),
      ),
      body: Column(
        children: [
          // Messages List
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              reverse: true,
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                final message = _messages.reversed.toList()[index];
                return MessageBubble(message: message);
              },
            ),
          ),
          // Message Input Area
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
            decoration: BoxDecoration(
              // CHANGE: Use dynamic color based on theme
              color: isDarkMode ? AppColors.surfaceGrey(context) : Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.05),
                  blurRadius: 8,
                  offset: const Offset(0, -2),
                ),
              ],
            ),
            child: Row(
              children: [
                // Attachment Button (Image Upload)
                GestureDetector(
                  onTap: _pickImage,
                  child: Container(
                    width: 45,
                    height: 45,
                    decoration: BoxDecoration(
                      // CHANGE: Use AppColors.surfaceGrey
                      color: AppColors.surfaceGrey(context),
                      borderRadius: BorderRadius.circular(30),
                    ),
                    child: Icon(
                      Icons.attach_file,
                      // CHANGE: Use AppColors.primaryTeal
                      color: AppColors.primaryTeal(context),
                      size: 24,
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                // Text Input
                Expanded(
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    decoration: BoxDecoration(
                      // CHANGE: Use AppColors.surfaceGrey
                      color: AppColors.surfaceGrey(context),
                      borderRadius: BorderRadius.circular(30),
                    ),
                    child: TextField(
                      controller: _messageController,
                      style: GoogleFonts.openSans(
                        fontSize: 16,
                        // CHANGE: Use AppColors.charcoal
                        color: AppColors.charcoal(context),
                      ),
                      decoration: InputDecoration(
                        hintText: 'Type a message...',
                        hintStyle: GoogleFonts.openSans(
                          fontSize: 14,
                          // CHANGE: Use AppColors.textGrey
                          color: AppColors.textGrey(context),
                        ),
                        border: InputBorder.none,
                        contentPadding: const EdgeInsets.symmetric(vertical: 12),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                // Send Button
                GestureDetector(
                  onTap: _sendMessage,
                  child: Container(
                    width: 45,
                    height: 45,
                    decoration: BoxDecoration(
                      // CHANGE: Use AppColors.primaryTeal
                      color: AppColors.primaryTeal(context),
                      shape: BoxShape.circle,
                    ),
                    child: Center(
                      child: _isSending
                          ? const SizedBox(
                              width: 20,
                              height: 20,
                              child: CircularProgressIndicator(
                                color: Colors.white,
                                strokeWidth: 2,
                              ),
                            )
                          : const Icon(Icons.send, color: Colors.white, size: 22),
                          ),
                        ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// Chat Message Widget Model
class ChatMessageWidget {
  final String text;
  final bool isMe;
  final String time;
  final bool isImage;
  final File? imageFile;

  ChatMessageWidget({
    required this.text,
    required this.isMe,
    required this.time,
    this.isImage = false,
    this.imageFile,
  });
}

// Message Bubble Widget
class MessageBubble extends StatelessWidget {
  final ChatMessageWidget message;

  const MessageBubble({super.key, required this.message});

  @override
  Widget build(BuildContext context) {
    final isDarkMode = Theme.of(context).brightness == Brightness.dark;
    
    return Align(
      alignment: message.isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
              decoration: BoxDecoration(
                color: message.isMe
                    // CHANGE: Use AppColors.primaryTeal for sent messages
                    ? AppColors.primaryTeal(context)
                    // CHANGE: Use AppColors.surfaceGrey for received messages
                    : AppColors.surfaceGrey(context),
                borderRadius: BorderRadius.only(
                  topLeft: const Radius.circular(20),
                  topRight: const Radius.circular(20),
                  bottomLeft: message.isMe
                      ? const Radius.circular(20)
                      : const Radius.circular(4),
                  bottomRight: message.isMe
                      ? const Radius.circular(4)
                      : const Radius.circular(20),
                ),
              ),
              child: message.isImage && message.imageFile != null
                  ? ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Image.file(
                  message.imageFile!,
                  width: 200,
                  height: 150,
                  fit: BoxFit.cover,
                ),
              )
                  : Text(
                message.text,
                style: GoogleFonts.openSans(
                  fontSize: 15,
                  // CHANGE: Use dynamic text color based on sender
                  color: message.isMe 
                      ? Colors.white 
                      : AppColors.charcoal(context),
                ),
              ),
            ),
            const SizedBox(height: 4),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Text(
                message.time,
                style: GoogleFonts.openSans(
                  fontSize: 10,
                  // CHANGE: Use AppColors.textGrey
                  color: AppColors.textGrey(context),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}