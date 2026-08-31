package com.app.api.unit.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.app.api.controllers.ChatController;
import com.app.api.dtos.ChatResponseDTO;
import com.app.api.services.ChatService;
import com.app.api.services.FirebaseAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Map<String, Object> mockMessagesResponse;
    private Map<String, Object> mockChatsResponse;
    private Map<String, Object> mockMessageResponse;
    private ChatResponseDTO mockChatResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        objectMapper = new ObjectMapper();

        mockMessagesResponse = new HashMap<>();
        mockMessagesResponse.put("chatID", 1);
        mockMessagesResponse.put("taskID", 100);
        mockMessagesResponse.put("page", 1);
        mockMessagesResponse.put("totalMessages", 10L);
        mockMessagesResponse.put("messages", List.of(
            Map.of("messageID", 1, "senderID", 101, "content", "Hello", "type", "text"),
            Map.of("messageID", 2, "senderID", 102, "content", "Hi there", "type", "text")
        ));
        mockMessagesResponse.put("participants", List.of(
            Map.of("userID", 101, "username", "John Doe"),
            Map.of("userID", 102, "username", "Jane Smith")
        ));

        mockChatsResponse = new HashMap<>();
        mockChatsResponse.put("userID", 101);
        mockChatsResponse.put("chats", List.of(
            Map.of("chatID", 1, "taskID", 100, "lastMessage", "Hello", "unreadCount", 2),
            Map.of("chatID", 2, "taskID", 101, "lastMessage", "Hi", "unreadCount", 0)
        ));

        mockMessageResponse = new HashMap<>();
        mockMessageResponse.put("messageID", 1);
        mockMessageResponse.put("chatID", 1);
        mockMessageResponse.put("senderID", 101);
        mockMessageResponse.put("content", "Hello world");
        mockMessageResponse.put("type", "text");
        mockMessageResponse.put("read", false);
        mockMessageResponse.put("timestamp", LocalDateTime.now().toString());

        
        mockChatResponseDTO = new ChatResponseDTO(10, 500, 2, 1, LocalDateTime.now(), true);
    }

    @Test
    void getMessages_WhenChatExists_ShouldReturnMessages() throws Exception {
        when(chatService.getMessagesByChatId(1, 1, 50)).thenReturn(mockMessagesResponse);

        mockMvc.perform(get("/api/chats/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatID").value(1))
                .andExpect(jsonPath("$.taskID").value(100))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalMessages").value(10))
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.participants.length()").value(2));
    }

    @Test
    void getMessages_WhenChatNotExists_ShouldReturnNotFound() throws Exception {
        when(chatService.getMessagesByChatId(999, 1, 50)).thenReturn(null);

        mockMvc.perform(get("/api/chats/999/messages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMessages_WithCustomPagination_ShouldReturnMessages() throws Exception {
        when(chatService.getMessagesByChatId(1, 2, 20)).thenReturn(mockMessagesResponse);

        mockMvc.perform(get("/api/chats/1/messages")
                .param("page", "2")
                .param("limit", "20")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1));
    }

    @Test
    void getChatsByUser_WhenUserHasChats_ShouldReturnChats() throws Exception {
        when(chatService.getChatsByUserId(101)).thenReturn(mockChatsResponse);

        mockMvc.perform(get("/api/chats/101")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID").value(101))
                .andExpect(jsonPath("$.chats.length()").value(2))
                .andExpect(jsonPath("$.chats[0].chatID").value(1))
                .andExpect(jsonPath("$.chats[0].unreadCount").value(2));
    }

    @Test
    void getChatsByUser_WhenUserHasNoChats_ShouldReturnEmptyList() throws Exception {
        Map<String, Object> emptyResponse = new HashMap<>();
        emptyResponse.put("userID", 999);
        emptyResponse.put("chats", List.of());

        when(chatService.getChatsByUserId(999)).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/chats/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID").value(999))
                .andExpect(jsonPath("$.chats.length()").value(0));
    }

    @Test
    void getChatsByUser_WhenServiceReturnsNull_ShouldHandleGracefully() throws Exception {
        when(chatService.getChatsByUserId(999)).thenReturn(null);

        mockMvc.perform(get("/api/chats/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void postMessage_WhenChatExists_ShouldReturnCreatedMessage() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("senderID", 101);
        requestBody.put("content", "Hello world");
        requestBody.put("type", "text");

        when(chatService.sendMessage(eq(1), eq(101), eq("Hello world"), eq("text")))
                .thenReturn(mockMessageResponse);

        mockMvc.perform(post("/api/chats/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.messageID").value(1))
                .andExpect(jsonPath("$.chatID").value(1))
                .andExpect(jsonPath("$.senderID").value(101))
                .andExpect(jsonPath("$.content").value("Hello world"))
                .andExpect(jsonPath("$.type").value("text"));
    }

    @Test
    void postMessage_WhenChatNotExists_ShouldReturnNotFound() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("senderID", 101);
        requestBody.put("content", "Hello world");
        requestBody.put("type", "text");

        when(chatService.sendMessage(eq(999), eq(101), eq("Hello world"), eq("text")))
                .thenReturn(null);

        mockMvc.perform(post("/api/chats/999/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postMessage_WithoutType_ShouldDefaultToText() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("senderID", 101);
        requestBody.put("content", "Hello world");

        when(chatService.sendMessage(eq(1), eq(101), eq("Hello world"), eq("text")))
                .thenReturn(mockMessageResponse);

        mockMvc.perform(post("/api/chats/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello world"));
    }

    @Test
    void postMessage_WithImageType_ShouldHandleImageMessage() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("senderID", 101);
        requestBody.put("content", "https://example.com/image.jpg");
        requestBody.put("type", "image");

        Map<String, Object> imageResponse = new HashMap<>(mockMessageResponse);
        imageResponse.put("type", "image");
        imageResponse.put("content", "https://example.com/image.jpg");

        when(chatService.sendMessage(eq(1), eq(101), eq("https://example.com/image.jpg"), eq("image")))
                .thenReturn(imageResponse);

        mockMvc.perform(post("/api/chats/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("image"));
    }

    @Test
    void getMessages_WithInvalidChatId_ShouldReturnNotFound() throws Exception {
        when(chatService.getMessagesByChatId(-1, 1, 50)).thenReturn(null);

        mockMvc.perform(get("/api/chats/-1/messages")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getChatsByUser_WithInvalidUserId_ShouldReturnEmptyList() throws Exception {
        Map<String, Object> emptyResponse = new HashMap<>();
        emptyResponse.put("userID", -1);
        emptyResponse.put("chats", List.of());

        when(chatService.getChatsByUserId(-1)).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/chats/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chats.length()").value(0));
    }

    @Test
    void postMessage_WithEmptyContent_ShouldSendEmptyMessage() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("senderID", 101);
        requestBody.put("content", "");
        requestBody.put("type", "text");

        Map<String, Object> emptyResponse = new HashMap<>(mockMessageResponse);
        emptyResponse.put("content", "");

        when(chatService.sendMessage(eq(1), eq(101), eq(""), eq("text")))
                .thenReturn(emptyResponse);

        mockMvc.perform(post("/api/chats/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value(""));
    }

    @Test
    void markAsRead_ShouldReturnSuccessResponse() throws Exception{
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("userID", 101);
        mockMvc.perform(put("/api/chats/1/read")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.chatID").value(1))
        .andExpect(jsonPath("$.markedAsRead").value(true));

        verify(chatService, times(1)).markAsRead(1, 101);
    }

    @Test
    void markAsRead_WithDifferentChatAndUser_ShouldMarkMessageAsRead() throws Exception{
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put("userID", 202);
        
        mockMvc.perform(put("/api/chats/55/read")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.chatID").value(55))
        .andExpect(jsonPath("$.markedAsRead").value(true));

        verify(chatService, times(1)).markAsRead(55, 202);
    }

    @Test
    void markAsRead_WithoutUserId_ShouldReturnServerError() throws Exception{
        Map<String, Object> requestBody = new HashMap<>();

        mockMvc.perform(put("/api/chats/1/read")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isBadRequest());
    }

    
    @Test
    void getOrCreateChatForTask_WhenTaskHasExistingChat_ShouldReturn200() throws Exception {
        String authHeader = "Bearer valid-token";
        int taskId = 500;
        int userId = 1;

        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(userId);
        when(chatService.getOrCreateChatForTask(taskId, userId)).thenReturn(mockChatResponseDTO);

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value(10))
                .andExpect(jsonPath("$.taskId").value(500))
                .andExpect(jsonPath("$.alreadyExisted").value(true));

        verify(firebaseAuthService, times(1)).getUserIdFromToken("valid-token");
        verify(chatService, times(1)).getOrCreateChatForTask(taskId, userId);
    }

    @Test
    void getOrCreateChatForTask_WhenTaskHasNoChatAndCreatesNew_ShouldReturn201() throws Exception {
        String authHeader = "Bearer valid-token";
        int taskId = 500;
        int userId = 1;

        ChatResponseDTO newChatResponse = new ChatResponseDTO(15, taskId, 1, 2, LocalDateTime.now(), false);
        newChatResponse.setChatId(15);
        newChatResponse.setTaskId(taskId);
        newChatResponse.setAlreadyExisted(false);

        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(userId);
        when(chatService.getOrCreateChatForTask(taskId, userId)).thenReturn(newChatResponse);

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chatId").value(15))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.alreadyExisted").value(false));

        verify(firebaseAuthService, times(1)).getUserIdFromToken("valid-token");
        verify(chatService, times(1)).getOrCreateChatForTask(taskId, userId);
    }

    @Test
    void getOrCreateChatForTask_WhenTaskNotFound_ShouldReturn404() throws Exception {
        String authHeader = "Bearer valid-token";
        int taskId = 999;
        int userId = 1;

        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(userId);
        when(chatService.getOrCreateChatForTask(taskId, userId))
                .thenThrow(new NoSuchElementException("Task not found"));

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(chatService, times(1)).getOrCreateChatForTask(taskId, userId);
    }

    @Test
    void getOrCreateChatForTask_WhenNoHelperOrDependentAssigned_ShouldReturn409() throws Exception {
        String authHeader = "Bearer valid-token";
        int taskId = 500;
        int userId = 1;
        String errorMessage = "Task has no assigned helper or dependent yet";

        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(userId);
        when(chatService.getOrCreateChatForTask(taskId, userId))
                .thenThrow(new IllegalStateException(errorMessage));

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$").value(errorMessage));

        verify(chatService, times(1)).getOrCreateChatForTask(taskId, userId);
    }

    @Test
    void getOrCreateChatForTask_WhenUserIsNotParticipant_ShouldReturn403() throws Exception {
        String authHeader = "Bearer valid-token";
        int taskId = 500;
        int userId = 3;
        String errorMessage = "User is not a participant in this task";

        when(firebaseAuthService.getUserIdFromToken("valid-token")).thenReturn(userId);
        when(chatService.getOrCreateChatForTask(taskId, userId))
                .thenThrow(new SecurityException(errorMessage));

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value(errorMessage));

        verify(chatService, times(1)).getOrCreateChatForTask(taskId, userId);
    }

    @Test
    void getOrCreateChatForTask_WhenFirebaseTokenInvalid_ShouldReturn401() throws Exception {
        String authHeader = "Bearer invalid-token";
        int taskId = 500;

        // Using mock(FirebaseAuthException.class) as shown in your SettingsControllerTest
        when(firebaseAuthService.getUserIdFromToken("invalid-token"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid or expired Firebase token"));

        verify(chatService, times(0)).getOrCreateChatForTask(anyInt(), anyInt());
    }

    @Test
    void getOrCreateChatForTask_WhenAuthHeaderMissing_ShouldReturn400() throws Exception {
        int taskId = 500;

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());  // Changed from 401 to 400

        verify(firebaseAuthService, times(0)).getUserIdFromToken(anyString());
        verify(chatService, times(0)).getOrCreateChatForTask(anyInt(), anyInt());
    }

    @Test
    void getOrCreateChatForTask_WhenAuthHeaderHasNoBearerPrefix_ShouldReturn401() throws Exception {
        String authHeader = "invalid-token-format";
        int taskId = 500;

        // Use mock(FirebaseAuthException.class) like in your SettingsControllerTest
        when(firebaseAuthService.getUserIdFromToken("invalid-token-format"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/chats/task/{taskId}", taskId)
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid or expired Firebase token"));

        verify(firebaseAuthService, times(1)).getUserIdFromToken("invalid-token-format");
        verify(chatService, times(0)).getOrCreateChatForTask(anyInt(), anyInt());
    }
}