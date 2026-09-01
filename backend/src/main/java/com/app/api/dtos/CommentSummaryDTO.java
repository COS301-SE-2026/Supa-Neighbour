package com.app.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentSummaryDTO {
    private Integer commentId;
    private String contentSnippet;
    private Integer postId;
    private Integer authorUserId;
    private Timestamp createdAt;
}
