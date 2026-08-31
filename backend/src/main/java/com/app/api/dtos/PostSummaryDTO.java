package com.app.api.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class PostSummaryDTO {
    private int postId;
    private String contentSnippet;
    private String category;
    private String mediaURL;
    private Integer authorUserId;
    private Timestamp  createdAt;
}
