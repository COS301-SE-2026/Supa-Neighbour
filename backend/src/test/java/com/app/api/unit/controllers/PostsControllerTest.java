package com.app.api.unit.controllers;

import com.app.api.controllers.PostsController;
import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CreatePostRequest;
import com.app.api.dtos.PostDetailDTO;
import com.app.api.dtos.PostFeedResponseDTO;
import com.app.api.models.Posts;
import com.app.api.services.CommentsService;
import com.app.api.services.FirebaseAuthService;
import com.app.api.services.PostsService;
import com.app.api.services.PostsService.InvalidPostException;
import com.google.firebase.auth.FirebaseAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class PostsControllerTest {

    @Mock
    private PostsService postsService;

    @Mock
    private FirebaseAuthService firebaseAuthService;

    @Mock
    private CommentsService commentsService;

    @InjectMocks
    private PostsController postsController;

    private MockMvc mockMvc;

    private static final String BEARER_TOKEN = "Bearer valid-token";
    private static final String RAW_TOKEN = "valid-token";
    private static final int USER_ID = 42;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postsController).build();
    }

    // ---------- GET /api/bulletin/posts ----------

    @Test
    void getAllPosts_WhenAuthorized_ReturnsFeed() throws Exception {

        PostFeedResponseDTO feed = mock(PostFeedResponseDTO.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.getFeed(eq(USER_ID), any(), any(), any(), any())).thenReturn(feed);

        mockMvc.perform(get("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postsService, times(1)).getFeed(eq(USER_ID), any(), any(), any(), any());
    }

    @Test
    void getAllPosts_WithFilters_ReturnsFeed() throws Exception {

        PostFeedResponseDTO feed = mock(PostFeedResponseDTO.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.getFeed(USER_ID, "safety", "fence", 1, 20)).thenReturn(feed);

        mockMvc.perform(get("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN)
                .param("category", "safety")
                .param("search", "fence")
                .param("page", "1")
                .param("limit", "20")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postsService, times(1)).getFeed(USER_ID, "safety", "fence", 1, 20);
    }

    @Test
    void getAllPosts_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(get("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(postsService, never()).getFeed(anyInt(), any(), any(), any(), any());
    }

    // ---------- GET /api/bulletin/posts/{postId} ----------

    @Test
    void getPostsById_WhenFound_ReturnsDetail() throws Exception {

        PostDetailDTO detail = mock(PostDetailDTO.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.getPostDetail(5)).thenReturn(detail);

        mockMvc.perform(get("/api/bulletin/posts/5")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postsService, times(1)).getPostDetail(5);
    }

    @Test
    void getPostsById_WhenNotFound_ReturnsNotFound() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.getPostDetail(999)).thenReturn(null);

        mockMvc.perform(get("/api/bulletin/posts/999")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Post not found"));

        verify(postsService, times(1)).getPostDetail(999);
    }

    @Test
    void getPostsById_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(get("/api/bulletin/posts/5")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(postsService, never()).getPostDetail(anyInt());
    }

    // ---------- POST /api/bulletin/posts ----------

    @Test
    void createPosts_WhenValid_ReturnsCreated() throws Exception {

        Posts savedPost = mock(Posts.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.createPost(eq(USER_ID), any(CreatePostRequest.class))).thenReturn(savedPost);

        mockMvc.perform(post("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isCreated());

        verify(postsService, times(1)).createPost(eq(USER_ID), any(CreatePostRequest.class));
    }

    @Test
    void createPosts_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(post("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());

        verify(postsService, never()).createPost(anyInt(), any(CreatePostRequest.class));
    }

    @Test
    void createPosts_WhenInvalid_ReturnsServiceDefinedStatus() throws Exception {

        InvalidPostException invalidPostException = new InvalidPostException("Post content is required", 400);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(postsService.createPost(eq(USER_ID), any(CreatePostRequest.class))).thenThrow(invalidPostException);

        mockMvc.perform(post("/api/bulletin/posts")
                .header("Authorization", BEARER_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Post content is required"));
    }

    // ---------- PUT /api/bulletin/{id} ----------

    @Test
    void updatePosts_WhenExists_ReturnsUpdatedPost() throws Exception {

        Posts existing = mock(Posts.class);
        Posts updated = mock(Posts.class);
        when(postsService.getPostById(1)).thenReturn(existing);
        when(postsService.updatePost(eq(1), any(Posts.class))).thenReturn(updated);

        mockMvc.perform(put("/api/bulletin/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk());

        verify(postsService, times(1)).getPostById(1);
        verify(postsService, times(1)).updatePost(eq(1), any(Posts.class));
    }

    @Test
    void updatePosts_WhenNotExists_ReturnsNotFound() throws Exception {

        when(postsService.getPostById(999)).thenReturn(null);

        mockMvc.perform(put("/api/bulletin/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());

        verify(postsService, times(1)).getPostById(999);
        verify(postsService, never()).updatePost(anyInt(), any(Posts.class));
    }

    // ---------- DELETE /api/bulletin/posts/{postId} ----------

    @Test
    void deletePosts_WhenValid_ReturnsOk() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        doNothing().when(postsService).deletePost(5, USER_ID);

        mockMvc.perform(delete("/api/bulletin/posts/5")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Post deleted"))
                .andExpect(jsonPath("$.postId").value(5));

        verify(postsService, times(1)).deletePost(5, USER_ID);
    }

    @Test
    void deletePosts_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(delete("/api/bulletin/posts/5")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(postsService, never()).deletePost(anyInt(), anyInt());
    }

    @Test
    void deletePosts_WhenServiceRejects_ReturnsServiceDefinedStatus() throws Exception {

        InvalidPostException invalidPostException = new InvalidPostException("Not the post owner", 403);

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        doThrow(invalidPostException).when(postsService).deletePost(5, USER_ID);

        mockMvc.perform(delete("/api/bulletin/posts/5")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Not the post owner"));
    }

    // ---------- GET /api/bulletin/posts/{postId}/comments ----------

    @Test
    void getPostComments_WhenAuthorized_ReturnsComments() throws Exception {

        List<CommentPostResponseDTO> comments = Arrays.asList(mock(CommentPostResponseDTO.class));
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(commentsService.getCommentsByPostId(5, USER_ID)).thenReturn(comments);

        mockMvc.perform(get("/api/bulletin/posts/5/comments")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(commentsService, times(1)).getCommentsByPostId(5, USER_ID);
    }

    @Test
    void getPostComments_WhenEmpty_ReturnsEmptyList() throws Exception {

        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenReturn(USER_ID);
        when(commentsService.getCommentsByPostId(5, USER_ID)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/bulletin/posts/5/comments")
                .header("Authorization", BEARER_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(commentsService, times(1)).getCommentsByPostId(5, USER_ID);
    }

    @Test
    void getPostComments_WhenUnauthorized_ReturnsUnauthorized() throws Exception {

        FirebaseAuthException authException = mock(FirebaseAuthException.class);
        when(firebaseAuthService.getUserIdFromToken(RAW_TOKEN)).thenThrow(authException);

        mockMvc.perform(get("/api/bulletin/posts/5/comments")
                .header("Authorization", BEARER_TOKEN))
                .andExpect(status().isUnauthorized());

        verify(commentsService, never()).getCommentsByPostId(anyInt(), anyInt());
    }
}