package com.app.api.controllers;

import com.app.api.services.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
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
}
