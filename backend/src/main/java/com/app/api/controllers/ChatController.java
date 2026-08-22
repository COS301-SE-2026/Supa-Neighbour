package com.app.api.controllers;

import com.app.api.dtos.ChatResponseDTO;
import com.app.api.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;



import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;
import com.app.api.services.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;


/**
 * controller for chat-related endpoints.
 */
@RestController
@RequestMapping("/api/chats")
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
    @Operation(summary = "Get messages for a chat thread")
    @ApiResponse(responseCode = "200", description = "Messages retrieved")
    @ApiResponse(responseCode = "404", description = "Chat not found")
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @PathVariable int chatId,
            @RequestParam(defaultValue = "1") int page,
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
    @Operation(summary = "Get all chats for a user")
    @ApiResponse(responseCode = "200", description = "Chats retrieved")
    @ApiResponse(responseCode = "404", description = "No chats found for user")
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getChatsByUser(
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
    @Operation(summary = "Send a message to a chat thread")
    @ApiResponse(responseCode = "201", description = "Message sent")
    @ApiResponse(responseCode = "404", description = "Chat not found")
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Map<String, Object>> postMessage(
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
    @Operation(summary = "Mark all messages in a chat as read")
    @ApiResponse(responseCode = "200", description = "Messages marked as read")
    @PutMapping("/{chatId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
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
    @Operation(summary = "Get or create the chat for a task")
    @ApiResponse(responseCode = "200", description = "Existing chat returned")
    @ApiResponse(responseCode = "201", description = "New chat created")
    @ApiResponse(responseCode = "403", description = "Caller is not a participant in this task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @ApiResponse(responseCode = "409", description = "Task has no assigned helper/dependent yet")
    @PostMapping("/task/{taskId}")
    public ResponseEntity<?> getCreateChatForTask(
        @PathVariable int taskId, 
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
