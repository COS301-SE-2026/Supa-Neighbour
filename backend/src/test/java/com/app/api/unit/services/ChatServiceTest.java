package com.app.api.unit.services;

import com.app.api.models.Chat;
import com.app.api.models.Message;
import com.app.api.models.User;
import com.app.api.models.TaskInvoice;
import com.app.api.repositories.ChatRepository;
import com.app.api.services.ChatService;
import com.app.api.repositories.MessageRepository;
import com.app.api.repositories.TaskInvoiceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    private ChatRepository chatRepo;

    @Mock
    private MessageRepository msgRepo;

    private ChatService chatService;

    @Mock
    private TaskInvoiceRepository taskInvoiceRepository;

    private Chat chat;
    private TaskInvoice task;
    private User dependentUser;
    private User helperUser;

    @BeforeEach
    void setup(){
        chatService = new ChatService(chatRepo, msgRepo, taskInvoiceRepository);

        task = mock(TaskInvoice.class);
        lenient().when(task.getTaskid()).thenReturn(500);

        chat = mock(Chat.class);
        lenient().when(chat.getChatId()).thenReturn(10);
        lenient().when(chat.getTask()).thenReturn(task);

        dependentUser = new User();
        dependentUser.setUserid(1);
        dependentUser.setFirstName("Ble");
        dependentUser.setLastName("Neo");

        helperUser = new User();
        helperUser.setUserid(2);
        helperUser.setFirstName("Divo");
        helperUser.setLastName("M");

        lenient().when(chat.getDependentUser()).thenReturn(dependentUser);
        lenient().when(chat.getHelperUser()).thenReturn(helperUser);
    }

    @Test
    void getMessagedByChatId_chatNotFount_returnsNull(){
        when(chatRepo.findById(999)).thenReturn(Optional.empty());

        Map<String, Object> result = chatService.getMessagesByChatId(999, 1, 20);

        assertThat(result).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void getMessagesByChatId_found_returnsMapWithMessagesAndParticipants () {
        Message msg = new Message();

        msg.setMessageId(1);
        msg.setSender(dependentUser);
        msg.setContent("Hey, are you around?");
        msg.setMessageType("text");
        msg.setRead(true);
        msg.setSentAt(LocalDateTime.of(2026, 7, 1, 10, 0));

        Page<Message> msgPage = new PageImpl<>(List.of(msg));
        when(chatRepo.findById(10)).thenReturn(Optional.of(chat));
        when(msgRepo.findByChat_ChatIdOrderBySentAtAsc(eq(10), any(PageRequest.class))).thenReturn(msgPage);

        Map<String , Object> result = chatService.getMessagesByChatId(10, 1, 20);

        assertThat(result).isNotNull();
        assertThat(result.get("chatID")).isEqualTo(10);
        assertThat(result.get("taskID")).isEqualTo(500);
        assertThat(result.get("page")).isEqualTo(1);
        assertThat(result.get("totalMessages")).isEqualTo(1L);


        List<Map<String, Object>> participants = (List<Map<String, Object>>)  result.get("participants");
        assertThat(participants).hasSize(2);
        assertThat(participants.get(0).get("userID")).isEqualTo(1);
        assertThat(participants.get(0).get("username")).isEqualTo("Ble Neo");
        assertThat(participants.get(1).get("userID")).isEqualTo(2);

        List<Map<String, Object>> messages = (List<Map<String, Object>>) result.get("messages");
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).get("messageID")).isEqualTo(1);
        assertThat(messages.get(0).get("senderID")).isEqualTo(1);
        assertThat(messages.get(0).get("senderUsername")).isEqualTo("Ble Neo");
        assertThat(messages.get(0).get("content")).isEqualTo("Hey, are you around?");
        assertThat(messages.get(0).get("read")).isEqualTo(true);
    }

    @Test
    void getMessagedyChatId_usesOneIndexdPageConvertedToZeroIndexed(){
        when(chatRepo.findById(10)).thenReturn(Optional.of(chat));

        when(msgRepo.findByChat_ChatIdOrderBySentAtAsc(eq(10), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of()));

        chatService.getMessagesByChatId(10, 3, 20);
        verify(msgRepo).findByChat_ChatIdOrderBySentAtAsc(eq(10), eq(PageRequest.of(2, 20)));
    }

    @Test
    void sedMessage_chatNotFouns_returnsNull(){
        when(chatRepo.findById(999)).thenReturn(Optional.empty());
        Map<String, Object> result = chatService.sendMessage(999, 1, "hi", "text");

        assertThat(result).isNull();
        verify(msgRepo, never()).save(any(Message.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getChatsByUserId_noChats_returnsEmptyChatList(){
        when(chatRepo.findByDependentUser_UseridOrHelperUser_Userid(99, 99)).thenReturn(List.of());
        Map<String, Object> result = chatService.getChatsByUserId(99);

        assertThat(result.get("userID")).isEqualTo(99);        
        List<Map<String, Object>> chats = (List<Map<String, Object>>) result.get("chats");
        assertThat(chats).isEmpty();
    }

    @Test
    void sendMessage_nullMsgType_defaultsToText(){
        when(chatRepo.findById(10)).thenReturn(Optional.of(chat));

        Message saved = new Message();
        saved.setMessageId(56);
        saved.setContent("hello");
        saved.setMessageType("text");
        saved.setRead(false);
        saved.setSentAt(LocalDateTime.now());

        when(msgRepo.save(any(Message.class))).thenReturn(saved);
        chatService.sendMessage(10, 1, "hello", null);

        verify(msgRepo).save(argThat(m -> "text".equals(m.getMessageType())));
    }

    @Test
    void sendMessage_validChat_savedAndReturnsMap(){
        when(chatRepo.findById(10)).thenReturn(Optional.of(chat));

        Message saved = new Message();
        saved.setMessageId(55);
        saved.setContent("On my way");
        saved.setMessageType("text");
        saved.setRead(false);
        saved.setSentAt(LocalDateTime.of(2026, 7, 3, 14, 0));

        when(msgRepo.save(any(Message.class))).thenReturn(saved);
        Map<String, Object> result = chatService.sendMessage(10, 1, "On my way", "text");

        assertThat(result).isNotNull();
        assertThat(result.get("messageID")).isEqualTo(55);
        assertThat(result.get("chatID")).isEqualTo(10);
        assertThat(result.get("senderID")).isEqualTo(1);
        assertThat(result.get("content")).isEqualTo("On my way");
        assertThat(result.get("type")).isEqualTo("text");
        assertThat(result.get("read")).isEqualTo(false);

        verify(msgRepo).save(any(Message.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getChatsByUserId_helperPerspective_otherUserIsDependent(){
        when(chatRepo.findByDependentUser_UseridOrHelperUser_Userid(2, 2))
        .thenReturn(List.of(chat));

        

        when(msgRepo.findByChat_ChatIdOrderBySentAtDesc(eq(10), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of()));

        when(msgRepo.countByChat_ChatIdAndIsReadFalseAndSender_UseridNot(10, 2))
        .thenReturn(0L);

        Map<String, Object> result = chatService.getChatsByUserId(2);

        List<Map<String, Object>> chats = (List<Map<String, Object>>) result.get("chats");

        Map<String, Object> summary = chats.get(0);

        assertThat(summary.get("otherUserID")).isEqualTo(1);
        assertThat(summary.get("otherUsername")).isEqualTo("Ble Neo");
        assertThat(summary.get("lastMessage")).isEqualTo("");
        assertThat(summary.get("lastMessageTimestamp")).isEqualTo("");
    }
}