package com.app.api.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.dtos.ChatResponseDTO;
import com.app.api.services.ChatService;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * controller for chat-related endpoints.
 */
@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chats", description = "Operations for managing chat messages and threads")
public class ChatController {

    /** The chat service. */
    private final ChatService chatService;

    private final FirebaseAuthService firebaseAuthService;

    /**
     * Constructs a ChatController with the given ChatService.
     * @param chatService  chat service
     */
    public ChatController(ChatService chatService, FirebaseAuthService firebaseAuthService) {
        this.chatService = chatService;
        this.firebaseAuthService = firebaseAuthService;
    }

    /**
     * Get all messages for a specific chat thread with pagination.
     * @param chatId the ID of the chat
     * @param page page num 
     * @param limit messages per page 
     * @return paginated messages or 404 if chat not found
     */
    @Operation(summary = "Get messages for a chat thread", description = "Retrieves paginated messages for a specific chat thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Chat not found", content = @Content)
    })
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @Parameter(description = "ID of the chat thread", example = "1")
            @PathVariable int chatId,
            @Parameter(description = "Page number for pagination", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of messages per page", example = "50")
            @RequestParam(defaultValue = "50") int limit) {

        Map<String, Object> result = chatService.getMessagesByChatId(chatId, page, limit);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get all chat threads for a specific user.
     * @param userId the ID of the user
     * @return list of chat summaries with last message and unread count
     */
    @Operation(summary = "Get all chats for a user", description = "Retrieves all chat threads for a specific user with last message and unread count")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chats retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No chats found for user", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getChatsByUser(
            @Parameter(description = "ID of the user", example = "1")
            @PathVariable int userId) {

        Map<String, Object> result = chatService.getChatsByUserId(userId);

        if(result == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Send a new message to a chat thread.
     * @param chatId the ID of the chat
     * @param body request body containing senderID, content, and optional type
     * @return the created message with HTTP 201, or 404 if chat not found
     */
    @Operation(summary = "Send a message to a chat thread", description = "Sends a new message to a specific chat thread")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully"),
        @ApiResponse(responseCode = "404", description = "Chat not found", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Map<String, Object>> postMessage(
        @Parameter(description = "ID of the chat thread", example = "1")
        @PathVariable int chatId,
        @RequestBody Map<String, Object> body) {

        int senderId = (Integer) body.get("senderID");
        String content = (String) body.get("content");
        String type = body.containsKey("type") ? (String) body.get("type") : "text";

        Map<String, Object> result = chatService.sendMessage(chatId, senderId, content, type);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(201).body(result);
    }

    /**
     * Marks all messages in a chat as read, excluding messages sent by the given user.
     * @param chatId the ID of the chat
     * @param body request body containing userID
     * @return confirmation of marking messages as read
     */
    @Operation(summary = "Mark all messages as read", description = "Marks all messages in a chat as read, excluding messages sent by the given user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages marked as read successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PutMapping("/{chatId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(description = "ID of the chat thread", example = "1")
            @PathVariable int chatId,
            @RequestBody Map<String, Object> body) {

        Object userIdObject = body.get("userID");

        if (userIdObject == null) {
            return ResponseEntity.badRequest().build();
        }
        
        int userId = (Integer) body.get("userID");
        chatService.markAsRead(chatId, userId);

        Map<String, Object> res = new HashMap<>();
        res.put("chatID", chatId);
        res.put("markedAsRead", true);
        return ResponseEntity.ok(res);
    }

    /**
     * Gets the existing chat for a task, or creates one if none exists yet.
     * Intended for the "Chat" button shown once a helper is assigned to a task.
     *
     * @param taskId the task to open/create a chat for
     * @param authHeader the HTTP Authorization header containing a Bearer token
     * @return the chat (200 if it already existed, 201 if newly created),
     *         404 if the task doesn't exist, 409 if no helper/dependent is
     *         assigned yet, 403 if the caller isn't part of this task, or
     *         401 if the Firebase token is invalid or expired
     */
    @Operation(summary = "Get or create chat for task", description = "Gets the existing chat for a task, or creates one if none exists yet")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Existing chat returned"),
        @ApiResponse(responseCode = "201", description = "New chat created"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired Firebase token", content = @Content),
        @ApiResponse(responseCode = "403", description = "Caller is not a participant in this task", content = @Content),
        @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Task has no assigned helper/dependent yet", content = @Content)
    })
    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> getCreateChatForTask(
        @Parameter(description = "ID of the task", example = "1")
        @PathVariable int taskId, 
        @Parameter(description = "Firebase authentication token in format: 'Bearer <token>'", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        @RequestHeader("Authorization") String authHeader
    ){
        int userId;
        try{
            String token = authHeader.replace("Bearer ", "");
            userId = firebaseAuthService.getUserIdFromToken(token);
        }catch(FirebaseAuthException e){
            return ResponseEntity.status(401).body("Invalid or expired Firebase token");
        }

        try{
            ChatResponseDTO result = chatService.getOrCreateChatForTask(taskId, userId);
            return result.isAlreadyExisted() ? ResponseEntity.ok(result) : ResponseEntity.status(201).body(result);
        }catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

}
