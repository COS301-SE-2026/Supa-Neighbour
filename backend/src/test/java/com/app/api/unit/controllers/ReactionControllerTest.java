package com.app.api.unit.controllers;

import com.app.api.controllers.ReactionController;
import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.models.Reaction;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.ReactionService;
import com.app.api.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuthException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReactionService reactionService;

    @MockitoBean
    private FirebaseAuthService firebaseAuthService;

    @MockitoBean
    private UserRepository userRepository;


    @Test
    void getAllreaction_returns200() throws Exception {

        Reaction r1 = new Reaction();
        Reaction r2 = new Reaction();

        when(reactionService.getAllreaction())
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/reaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(reactionService).getAllreaction();
    }


    @Test
    void getreactionById_whenFound_returns200() throws Exception {

        Reaction reaction = new Reaction();

        when(reactionService.getLikeById(1))
                .thenReturn(reaction);

        mockMvc.perform(get("/api/reaction/1"))
                .andExpect(status().isOk());

        verify(reactionService).getLikeById(1);
    }

    @Test
    void getreactionById_whenNotFound_returns404() throws Exception {

        when(reactionService.getLikeById(999))
                .thenReturn(null);

        mockMvc.perform(get("/api/reaction/999"))
                .andExpect(status().isNotFound());

        verify(reactionService).getLikeById(999);
    }

    @Test
    void createReaction_whenAuthenticated_returns201() throws Exception {

        ReactionResponseDTO response =
                new ReactionResponseDTO(
                        "Reaction added",
                        1,
                        "dislike",
                        5L
                );

        when(firebaseAuthService.getUserIdFromToken("valid-token"))
                .thenReturn(10);

        when(reactionService.addDislikeReaction(1, 10))
                .thenReturn(response);

        mockMvc.perform(post("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isCreated());

        verify(firebaseAuthService).getUserIdFromToken("valid-token");
        verify(reactionService).addDislikeReaction(1, 10);
    }

    @Test
    void createReaction_whenFirebaseAuthenticationFails_returns401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken("bad-token"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());

        verify(reactionService, never())
                .addDislikeReaction(anyInt(), anyInt());
    }

    @Test
    void updateReaction_whenFound_returns200() throws Exception {

        Reaction existing = new Reaction();
        Reaction updated = new Reaction();

        when(reactionService.getLikeById(1))
                .thenReturn(existing);

        when(reactionService.updateLike(eq(1), any(Reaction.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/reaction/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Reaction())))
                .andExpect(status().isOk());

        verify(reactionService).getLikeById(1);
        verify(reactionService).updateLike(eq(1), any(Reaction.class));
    }

    @Test
    void updateReaction_whenNotFound_returns404() throws Exception {

        when(reactionService.getLikeById(999))
                .thenReturn(null);

        mockMvc.perform(put("/api/reaction/999")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new Reaction())))
                .andExpect(status().isNotFound());

        verify(reactionService, never())
                .updateLike(anyInt(), any(Reaction.class));
    }


    @Test
    void removeDislike_whenAuthenticated_returns200() throws Exception {

        ReactionRemovedResponseDTO response =
                new ReactionRemovedResponseDTO(
                        "Reaction removed",
                        1,
                        4L
                );

        when(firebaseAuthService.getUserIdFromToken("valid-token"))
                .thenReturn(10);

        when(reactionService.removeDisLikeReaction(1, 10))
                .thenReturn(response);

        mockMvc.perform(delete("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());

        verify(reactionService)
                .removeDisLikeReaction(1, 10);
    }

    @Test
    void removeDislike_whenFirebaseFails_returns401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken("bad-token"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(delete("/api/reaction/posts/1/dislike")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());

        verify(reactionService, never())
                .removeDisLikeReaction(anyInt(), anyInt());
    }

    @Test
    void addDislikedToComment_whenAuthenticated_returns201() throws Exception {

        CommentReactionResponseDTO response =
                new CommentReactionResponseDTO(
                        "Reaction added",
                        1,
                        "dislike",
                        3L
                );

        when(firebaseAuthService.getUserIdFromToken("valid-token"))
                .thenReturn(10);

        when(reactionService.addDislikeReactionToComment(1, 10))
                .thenReturn(response);

        mockMvc.perform(post("/api/reaction/comments/1/dislike")
                .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isCreated());

        verify(reactionService)
                .addDislikeReactionToComment(1, 10);
    }

    @Test
    void addDislikedToComment_whenFirebaseFails_returns401() throws Exception {

        when(firebaseAuthService.getUserIdFromToken("bad-token"))
                .thenThrow(mock(FirebaseAuthException.class));

        mockMvc.perform(post("/api/reaction/comments/1/dislike")
                .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isUnauthorized());

        verify(reactionService, never())
                .addDislikeReactionToComment(anyInt(), anyInt());
    }
}
