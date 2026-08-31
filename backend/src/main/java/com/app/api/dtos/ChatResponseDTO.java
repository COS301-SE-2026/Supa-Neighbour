package com.app.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatResponseDTO {
    private int chatId;
    private int taskId;
    private int dependentUserId;
    private int helperUserId;
    private LocalDateTime createdAt;
    private boolean alreadyExisted;
}
