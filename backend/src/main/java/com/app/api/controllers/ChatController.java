package com.app.api.controllers;

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


import java.util.Map;

/**
 * controller for chat-related endpoints.
 */
@RestController
@RequestMapping("/api/chats")
public class ChatController {

    /** The chat service. */
    private final ChatService chatService;

    /**
     * Constructs a ChatController with the given ChatService.
     * @param chatService  chat service
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
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



}
