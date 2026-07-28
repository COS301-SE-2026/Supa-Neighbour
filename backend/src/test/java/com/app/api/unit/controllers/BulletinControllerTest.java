package com.app.api.unit.controllers;

import java.util.List;
import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.repositories.UserRepository;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.CommentsService;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.PostsService;
import com.app.api.services.ReactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.app.api.controllers.CommentsController;
import com.app.api.controllers.PostsController;
import com.app.api.controllers.ReactionController;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ CommentsController.class, PostsController.class, ReactionController.class })
@AutoConfigureMockMvc(addFilters = false)
public class BulletinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PostsService postsService;

    @MockitoBean
    private CommentsService commentsService;

    @MockitoBean
    private ReactionService reactionService;

    @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    private static final String VALID_TOKEN = "Bearer valid-token";

    @Test
    void createComment_withValidToken_returns201() throws Exception {
        CommentResponseDTO response = mock(CommentResponseDTO.class);

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(commentsService.addCommentToPost(eq(1), any(CommentRequestDTO.class), eq(1)))
                .thenReturn(response);

        mockMvc.perform(post("/api/comments/bulletin/1")
                .header("Authorization", VALID_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "commentContent":"Nice post!"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_withInvalidToken_returns401() throws Exception {
        when(firebaseAuthService.getUserIdFromToken(anyString()))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/comments/bulletin/1")
                .header("Authorization", "Bearer bad-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "commentContent":"Nice post"
                        }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getComments_withValidToken_returns200() throws Exception {

        List<CommentPostResponseDTO> comments = List.of(mock(CommentPostResponseDTO.class));

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(commentsService.getCommentsByPostId(1, 1)).thenReturn(comments);

        mockMvc.perform(get("/api/bulletin/posts/1/comments")
                .header("Authorization", VALID_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void getComments_withInvalidToken_returns401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(get("/api/bulletin/posts/1/comments")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createDislike_withToken_returns201() throws Exception {

        ReactionResponseDTO response = mock(ReactionResponseDTO.class);

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(reactionService.addDislikeReaction(1, 1)).thenReturn(response);

        mockMvc.perform(post("/api/reaction/posts/1/dislike")
                .header("Authorization", VALID_TOKEN))
                .andExpect(status().isCreated());
    }

    @Test
    void createdDislike_withInvalidToken_return401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(anyString()))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void removeDislike_withValidToken_returns200() throws Exception {

        ReactionRemovedResponseDTO response = mock(ReactionRemovedResponseDTO.class);

        when(firebaseAuthService.getUserIdFromToken(anyString())).thenReturn(1);
        when(reactionService.removeDisLikeReaction(1, 1)).thenReturn(response);

        mockMvc.perform(delete("/api/reaction/posts/1/dislike")
                .header("Authorization", VALID_TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    void removeDislike_withInvalidToken_return401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(anyString()))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(delete("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());
    }
}

