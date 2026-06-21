package com.app.api.services;

import com.app.api.models.Chat;
import com.app.api.models.Message;

import com.app.api.repositories.ChatRepository;
import com.app.api.repositories.MessageRepository;
//import com.sun.jna.platform.win32.WinUser.MSG;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Services for chat related business logic.
 */
@Service
public class ChatService {

    /** The chat repository. */
    private final ChatRepository chatRepo;

    /** The msg repository. */
    private final MessageRepository msgRepo;

    /**
     * Constructs a ChatService with the required repositries.
     * @param chatRepo the chat repository
     * @param msgRepo the message repository
     */
    @Autowired
    public ChatService(ChatRepository chatRepo, MessageRepository msgRepo) {
        this.chatRepo = chatRepo;
        this.msgRepo = msgRepo;
    }

    /**
     * Gets paginated messages for a specific chat thread.
     * @param chatId the chat Id
     * @param page the page num
     * @param limit num of messages per page
     * @return a map representing the response, or null if chat not found
     */
    public Map<String, Object> getMessagesByChatId(int chatId, int page, int limit) {

        // Find the chat if it doesn't exist return null
        Chat chat = chatRepo.findById(chatId).orElse(null);
        if (chat == null) {
            return null;
        }

        // Fetch paginated messages ordered by sent_at ascending
        // Page index is 0-based internally but API accepts 1-based hence we minus 1
        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Message> msgPage = msgRepo.findByChat_ChatIdOrderBySentAtAsc(chatId, pageable);

        // Msg list
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (Message msg : msgPage.getContent()) {
            msgList.add(toMessageMap(msg));
        }

        // Participants list
        List<Map<String, Object>> participants = new ArrayList<>();
        participants.add(toParticipantMap(chat.getDependentUser()));
        participants.add(toParticipantMap(chat.getHelperUser()));

        // Res
        Map<String, Object> res = new HashMap<>();
        res.put("chatID", chat.getChatId());
        res.put("taskID", chat.getTask().getTaskid());
        res.put("participants", participants);
        res.put("page", page);
        res.put("totalMessages", msgPage.getTotalElements());
        res.put("messages", msgList);

        return res;
    }

    /**
     * Gets all chat threads for a specific user.
     * Includes last message, unread count, and task info per thread.
     * @param userId the user ID
     * @return list of chat summaries, or null if the user has no chats
     */
    public Map<String, Object> getChatsByUserId(int userId) {

        // Find all chats where this user is either the dependent or helper
        List<Chat> chats = chatRepo
                .findByDependentUser_UseridOrHelperUser_Userid(userId, userId);

        // Chat list
        List<Map<String, Object>> chatList = new ArrayList<>();

        for (Chat chat : chats) {
            chatList.add(toChatSummary(chat, userId));
        }

        // Res
        Map<String, Object> res = new HashMap<>();
        res.put("userID", userId);
        res.put("chats", chatList);
        return res;
    }


    /**
     * Builds a response map for a single message.
     * @param msg the message entity
     * @return the message represented as a map
     */
    private Map<String, Object> toMessageMap(Message msg) {
        Map<String, Object> m = new HashMap<>();
        m.put("messageID", msg.getMessageId());
        m.put("senderID", msg.getSender().getUserid());
        m.put("senderUsername", msg.getSender().getFirstName()
                + " " + msg.getSender().getLastName());
        m.put("content", msg.getContent());
        m.put("type", msg.getMessageType());
        m.put("timestamp", msg.getSentAt());
        m.put("read", msg.isRead());
        return m;
    }

    /**
     * Builds a participant map for a chat user.
     * @param user the chat participant
     * @return the participant represented as a map
     */
    private Map<String, Object> toParticipantMap(com.app.api.models.User user) {
        Map<String, Object> p = new HashMap<>();
        p.put("userID", user.getUserid());
        p.put("username", user.getFirstName() + " " + user.getLastName());
        return p;
    }

    /**
     * Builds a chat summary for one chat thread relative to the given user.
     * @param chat the chat thread
     * @param userId the user the summary is being built for
     * @return the chat summary represented as a map
     */
    private Map<String, Object> toChatSummary(Chat chat, int userId) {
        // Determine which user is the "other" person in the convo
        boolean isDependent = chat.getDependentUser().getUserid() == userId;
        var otherUser = isDependent ? chat.getHelperUser() : chat.getDependentUser();

        // Get the last message in this chat
        PageRequest lastOne = PageRequest.of(0, 1);
        Page<Message> lastMsgPage = msgRepo
                .findByChat_ChatIdOrderBySentAtDesc(chat.getChatId(), lastOne);

        String lastMessageContent = "";
        String lastMessageTimestamp = "";
        if (!lastMsgPage.isEmpty()) {
            Message last = lastMsgPage.getContent().get(0);
            lastMessageContent = last.getContent();
            lastMessageTimestamp = last.getSentAt().toString();
        }

        // Count of unread messages
        long unreadCount = msgRepo
                .countByChat_ChatIdAndIsReadFalseAndSender_UseridNot(
                        chat.getChatId(), userId);

        Map<String, Object> chatSummary = new HashMap<>();
        chatSummary.put("chatID", chat.getChatId());
        chatSummary.put("taskID", chat.getTask().getTaskid());
        chatSummary.put("otherUserID", otherUser.getUserid());
        chatSummary.put("otherUsername",
                otherUser.getFirstName() + " " + otherUser.getLastName());
        chatSummary.put("lastMessage", lastMessageContent);
        chatSummary.put("lastMessageTimestamp", lastMessageTimestamp);
        chatSummary.put("unreadCount", unreadCount);
        return chatSummary;
    }

    /**
     * Sends a new message to a chat thread.
     * @param chatId the chat ID
     * @param senderId the sender's user ID
     * @param content the message text or image URL
     * @param msgType 'text' or 'image'
     * @return the saved message as a map, or null if chat not found
     */
    public Map<String, Object> sendMessage(int chatId, int senderId,
            String content, String msgType) {

        // Find the chat
        Chat chat = chatRepo.findById(chatId).orElse(null);
        if (chat == null) {
            return null;
        }

        // Create nd save message
        Message msg = new Message();
        msg.setChat(chat);

        // We need a user reference hence y we create a proxy with just the id
        // *will be used as FK without loading the full user
        com.app.api.models.User sender = new com.app.api.models.User();
        sender.setUserid(senderId);
        msg.setSender(sender);

        msg.setContent(content);
        msg.setMessageType(msgType != null ? msgType : "text");
        msg.setRead(false);
        msg.setSentAt(java.time.LocalDateTime.now());

        Message saved = msgRepo.save(msg);

        // Res
        Map<String, Object> res = new HashMap<>();
        res.put("messageID", saved.getMessageId());
        res.put("chatID", chatId);
        res.put("senderID", senderId);
        res.put("content", saved.getContent());
        res.put("type", saved.getMessageType());
        res.put("timestamp", saved.getSentAt());
        res.put("read", saved.isRead());
        return res;
    }


}
  
