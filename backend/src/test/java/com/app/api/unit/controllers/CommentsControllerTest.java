package com.app.api.unit.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
 
import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.models.Comments;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.CommentsService;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import com.app.api.controllers.CommentsController;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = CommentsController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class CommentsControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommentsService commentsService;

    @MockitoBean
    private ReactionService reactionService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    private static final String AUTH_HEADER ="Bearer test-token";

    private Comments sampleComment() {
        Comments comment = new Comments();
        comment.setCommentid(1);
        comment.setCommentContent("Original content");
        comment.setCreatedAt(Timestamp.from(Instant.now()));
        return comment;
    }

    @Test
    void getAllComments_returns200WithList() throws Exception{
        Comments comment = sampleComment();
        when(commentsService.getAllComments()).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/comments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].commentid").value(1));
    }

    @Test
    void getCommentsById_found_returns200() throws Exception{
        when(commentsService.getCommentsById(1)).thenReturn(sampleComment());

        mockMvc.perform(get("/api/comments/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.commentid").value(1));
    }

    @Test
    void getCommentsById_notFound_returns404() throws Exception{
        when(commentsService.getCommentsById(99)).thenReturn(null);

        mockMvc.perform(get("/api/comments/99"))
        .andExpect(status().isNotFound());
    }

    @Test
    void deleteHelpfulReaction_invalidToken_returns401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken("bad-token"))
        .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(delete("/api/comments/bulletin/posts/100/like")
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized());

        verify(reactionService, never()).removeHelpfulReaction(anyInt(), anyInt());

    }

    @Test
    void deleteHelpfulReactionService_validToken_returns204() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("test-token")).thenReturn(10);

        mockMvc.perform(delete("/api/comments/bulletin/posts/100/like")
        .header("Authorization", AUTH_HEADER))
        .andExpect(status().isNoContent());
        verify(reactionService).removeHelpfulReaction(100, 10);
    }

    @Test
    void postHelpfulReaction_invalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("bad-token"))
        .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/comments/bulletin/posts/100/like")
        .header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized());

        verify(reactionService, never()).addHelpfulReactionToPost(anyInt(), anyInt());
    }

    @Test
    void postHelpfulReaction_validToken_returns201() throws Exception{
        CommentReactionResponseDTO reaction = mock(CommentReactionResponseDTO.class);

        when(firebaseAuthService.getUserIdFromToken("test-token")).thenReturn(10);
        when(reactionService.addHelpfulReactionToPost(100, 10)).thenReturn(reaction);

        mockMvc.perform(post("/api/comments/bulletin/posts/100/like")
        .header("Authorization", AUTH_HEADER))
        .andExpect(status().isCreated());

        verify(reactionService).addHelpfulReactionToPost(100, 10);
    }

    @Test
    void deleteCommentUnderPost_invalidToken_returns401() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("bad-token"))
        .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(delete("/api/comments/bulletin/posts/100/1")
        .header("Authorization", "Bearer bad-token"));

        verify(commentsService, never()).deleteCommentFromPost(anyInt(), anyInt(), anyInt());
    }

    @Test
    void deleteCommentsUnderPost_validToken_returns204() throws Exception{
        when(firebaseAuthService.getUserIdFromToken("test-token")).thenReturn(10);
        mockMvc.perform(delete("/api/comments/bulletin/posts/100/1")
        .header("Authorization", AUTH_HEADER))
        .andExpect(status().isNoContent());

        verify(commentsService).deleteCommentFromPost(100, 1, 10);
    }

    @Test
    void updateComments_notFound_returns404() throws Exception{
        when(commentsService.getCommentsById(99)).thenReturn(null);

        mockMvc.perform(put("/api/comments/99")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(sampleComment())))
        .andExpect(status().isNotFound());

        verify(commentsService, never()).updateComments(anyInt(), any());
    }

    @Test
    void updateComments_found_returns200() throws Exception{
        Comments existing = sampleComment();
        Comments updated = sampleComment();
        updated.setCommentContent("Updated content");

        when(commentsService.getCommentsById(1)).thenReturn(existing);
        when(commentsService.updateComments(eq(1), any(Comments.class))).thenReturn(updated);

        mockMvc.perform(put("/api/comments/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.commentContent").value("Updated content"));
    }

    @Test
    void createComments_invalidToken_returns401() throws Exception{
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Nice post!");

        when(firebaseAuthService.getUserIdFromToken("bad-token"))
        .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/comments/bulletin/100")
            .header("Authorization", "Bearer bad-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());

        verify(commentsService, never()).addCommentToPost(anyInt(), any(), anyInt());
    }

    @Test
    void createComment_validToken_returns201() throws Exception{
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Nice post!");

        CommentResponseDTO response = new CommentResponseDTO();
        response.setCommentId(1);
        response.setPostId(100);
        response.setUserId(10);
        response.setCommentContent("Nice post!");

        when(firebaseAuthService.getUserIdFromToken("test-token")).thenReturn(10);
        when(commentsService.addCommentToPost(eq(100), any(CommentRequestDTO.class), eq(10))).thenReturn(response);

        mockMvc.perform(post("/api/comments/bulletin/100")
            .header("Authorization", AUTH_HEADER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.commentId").value(1))
            .andExpect(jsonPath("$.commentContent").value("Nice post!"));
    }

}

