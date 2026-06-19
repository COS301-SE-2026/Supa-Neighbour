package com.app.api.services;

import com.app.api.models.Chat;
import com.app.api.models.Message;

import com.app.api.repositories.ChatRepository;
import com.app.api.repositories.MessageRepository;

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
        // Need to Note that page index is 0-based internally but API accepts 1-based hence we minus 1
        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Message> msgPage = msgRepo.findByChat_ChatIdOrderBySentAtAsc(chatId, pageable);

        // msg list
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (Message msg : msgPage.getContent()) {
            Map<String, Object> m = new HashMap<>();
            m.put("messageID", msg.getMessageId());
            m.put("senderID", msg.getSender().getUserid());
            m.put("senderUsername", msg.getSender().getFirstName()
                    + " " + msg.getSender().getLastName());
            m.put("content", msg.getContent());
            m.put("type", msg.getMessageType());
            m.put("timestamp", msg.getSentAt());
            m.put("read", msg.isRead());
            msgList.add(m);
        }

        // participants list
        List<Map<String, Object>> participants = new ArrayList<>();
        Map<String, Object> dependent = new HashMap<>();
        dependent.put("userID", chat.getDependentUser().getUserid());
        dependent.put("username", chat.getDependentUser().getFirstName()
                + " " + chat.getDependentUser().getLastName());
        participants.add(dependent);

        Map<String, Object> helper = new HashMap<>();
        helper.put("userID", chat.getHelperUser().getUserid());
        helper.put("username", chat.getHelperUser().getFirstName()
                + " " + chat.getHelperUser().getLastName());
        participants.add(helper);

        // res
        Map<String, Object> res = new HashMap<>();
        res.put("chatID", chat.getChatId());
        res.put("taskID", chat.getTask().getTaskid());
        res.put("participants", participants);
        res.put("page", page);
        res.put("totalMessages", msgPage.getTotalElements());
        res.put("messages", msgList);

        return res;
    }
}
  