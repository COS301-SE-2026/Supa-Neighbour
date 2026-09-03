package com.app.api.unit.services;

import com.app.api.dtos.CreatePostRequest;
import com.app.api.dtos.PostDetailDTO;
import com.app.api.dtos.PostFeedItemDTO;
import com.app.api.dtos.PostFeedResponseDTO;
import com.app.api.models.Posts;
import com.app.api.models.User;
import com.app.api.repositories.BulletinFeedRepository;
import com.app.api.repositories.BulletinFeedRepository.CallerNeighbourhood;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.BlobStorageService;
import com.app.api.services.PostsService;
import com.app.api.services.PostsService.InvalidPostException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostsServiceTest {

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BulletinFeedRepository bulletinFeedRepository;

    @Mock
    private BlobStorageService blobStorageService;

    @InjectMocks
    private PostsService postsService;

    private User buildUser(int id) {
        User user = new User();
        user.setUserid(id);
        return user;
    }

    private CreatePostRequest buildRequest(String content, String mediaUrl, String category) {
        CreatePostRequest request = mock(CreatePostRequest.class);
        lenient().when(request.getPostContent()).thenReturn(content);
        lenient().when(request.getMediaUrl()).thenReturn(mediaUrl);
        lenient().when(request.getCategory()).thenReturn(category);
        return request;
    }


    @Test
    void getAllPosts_ReturnsAllPosts() {
        List<Posts> posts = Arrays.asList(mock(Posts.class));
        when(postsRepository.findAll()).thenReturn(posts);

        List<Posts> result = postsService.getAllPosts();

        assertEquals(1, result.size());
        verify(postsRepository, times(1)).findAll();
    }


    @Test
    void getPostById_WhenFound_ReturnsPost() {
        Posts post = mock(Posts.class);
        when(postsRepository.findById(1)).thenReturn(Optional.of(post));

        Posts result = postsService.getPostById(1);

        assertSame(post, result);
    }

    @Test
    void getPostById_WhenNotFound_ReturnsNull() {
        when(postsRepository.findById(999)).thenReturn(Optional.empty());

        Posts result = postsService.getPostById(999);

        assertNull(result);
    }


    @Test
    void createPost_WhenUserNotFound_ThrowsUnauthorized() {
        when(userRepository.findById(42)).thenReturn(Optional.empty());
        CreatePostRequest request = buildRequest("Hello neighbours", null, null);

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.createPost(42, request));
        assertEquals(401, ex.getStatusCode());
        assertEquals("Unauthorized", ex.getMessage());
    }

    @Test
    void createPost_WhenPostContentIsNull_ThrowsUnprocessableEntity() {
        when(userRepository.findById(42)).thenReturn(Optional.of(buildUser(42)));
        CreatePostRequest request = buildRequest(null, null, null);

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.createPost(42, request));
        assertEquals(422, ex.getStatusCode());
        assertEquals("postContent is required", ex.getMessage());
    }

    @Test
    void createPost_WhenPostContentIsBlank_ThrowsUnprocessableEntity() {
        when(userRepository.findById(42)).thenReturn(Optional.of(buildUser(42)));
        CreatePostRequest request = buildRequest("   ", null, null);

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.createPost(42, request));
        assertEquals(422, ex.getStatusCode());
    }

    @Test
    void createPost_WhenMediaUrlDoesNotLookLikeBlobUrl_ThrowsBadRequest() {
        when(userRepository.findById(42)).thenReturn(Optional.of(buildUser(42)));
        CreatePostRequest request = buildRequest("Hello", "https://evil.example.com/image.png", null);

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.createPost(42, request));
        assertEquals(400, ex.getStatusCode());
        assertEquals("mediaUrl must be a valid uploaded image URL", ex.getMessage());
    }

    @Test
    void createPost_WhenCategoryInvalid_ThrowsBadRequest() {
        when(userRepository.findById(42)).thenReturn(Optional.of(buildUser(42)));
        CreatePostRequest request = buildRequest("Hello", null, "not_a_real_category");

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.createPost(42, request));
        assertEquals(400, ex.getStatusCode());
    }

    @Test
    void createPost_WhenCategoryNull_DefaultsToGeneralAndSaves() {
        User user = buildUser(42);
        when(userRepository.findById(42)).thenReturn(Optional.of(user));
        CreatePostRequest request = buildRequest("Hello neighbours", null, null);

        ArgumentCaptor<Posts> captor = ArgumentCaptor.forClass(Posts.class);
        when(postsRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        Posts result = postsService.createPost(42, request);

        assertNotNull(result);
        Posts saved = captor.getValue();
        assertEquals("general", saved.getCategory());
        assertEquals(user, saved.getUserid());
        assertEquals("Hello neighbours", saved.getPostContent());
        assertNull(saved.getMediaURL());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void createPost_WhenValid_SavesPostWithAllFieldsAndReturnsSavedPost() {
        User user = buildUser(42);
        when(userRepository.findById(42)).thenReturn(Optional.of(user));
        CreatePostRequest request = buildRequest(
                "Lost cat near the park",
                "https://myaccount.blob.core.windows.net/container/image.png",
                "lost_pet");

        ArgumentCaptor<Posts> captor = ArgumentCaptor.forClass(Posts.class);
        Posts savedPost = mock(Posts.class);
        when(postsRepository.save(captor.capture())).thenReturn(savedPost);

        Posts result = postsService.createPost(42, request);

        assertSame(savedPost, result);
        Posts passedToSave = captor.getValue();
        assertEquals(user, passedToSave.getUserid());
        assertEquals("Lost cat near the park", passedToSave.getPostContent());
        assertEquals("https://myaccount.blob.core.windows.net/container/image.png", passedToSave.getMediaURL());
        assertEquals("lost_pet", passedToSave.getCategory());
        assertNotNull(passedToSave.getCreatedAt());
        assertNotNull(passedToSave.getUpdatedAt());
    }


    @Test
    void updatePost_WhenNotExists_ReturnsNull() {
        when(postsRepository.findById(999)).thenReturn(Optional.empty());

        Posts result = postsService.updatePost(999, mock(Posts.class));

        assertNull(result);
        verify(postsRepository, never()).save(any());
    }

    @Test
    void updatePost_WhenExists_CopiesAllFieldsOntoExistingAndSaves() {
        Posts existing = mock(Posts.class);
        Posts updated = mock(Posts.class);

        User updatedUser = buildUser(99);
        Timestamp createdAt = new Timestamp(1000L);
        Timestamp updatedAt = new Timestamp(2000L);

        when(updated.getUserid()).thenReturn(updatedUser);
        when(updated.getPostContent()).thenReturn("Updated content");
        when(updated.getCreatedAt()).thenReturn(createdAt);
        when(updated.getUpdatedAt()).thenReturn(updatedAt);
        when(updated.getMediaURL()).thenReturn("https://acct.blob.core.windows.net/c/img.png");
        when(updated.getCategory()).thenReturn("alert");

        when(postsRepository.findById(1)).thenReturn(Optional.of(existing));
        when(postsRepository.save(existing)).thenReturn(existing);

        Posts result = postsService.updatePost(1, updated);

        assertSame(existing, result);
        verify(existing).setUserid(updatedUser);
        verify(existing).setPostContent("Updated content");
        verify(existing).setCreatedAt(createdAt);
        verify(existing).setUpdatedAt(updatedAt);
        verify(existing).setMediaURL("https://acct.blob.core.windows.net/c/img.png");
        verify(existing).setCategory("alert");
        verify(postsRepository, times(1)).save(existing);
        verify(postsRepository, never()).save(updated);
    }


    @Test
    void deletePost_WhenNotFound_ThrowsNotFound() {
        when(postsRepository.findById(5)).thenReturn(Optional.empty());

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.deletePost(5, 42));
        assertEquals(404, ex.getStatusCode());
        assertEquals("Post not found", ex.getMessage());
        verify(postsRepository, never()).deleteById(anyInt());
    }

    @Test
    void deletePost_WhenCallerIsNotOwner_ThrowsForbidden() {
        Posts existing = mock(Posts.class);
        User owner = buildUser(99);
        when(existing.getUserid()).thenReturn(owner);
        when(postsRepository.findById(5)).thenReturn(Optional.of(existing));

        InvalidPostException ex = assertThrows(InvalidPostException.class,
                () -> postsService.deletePost(5, 42));
        assertEquals(403, ex.getStatusCode());
        assertEquals("You are not authorised to delete this post", ex.getMessage());
        verify(postsRepository, never()).deleteById(anyInt());
    }

    @Test
    void deletePost_WhenCallerIsOwner_DeletesSuccessfully() {
        Posts existing = mock(Posts.class);
        User owner = buildUser(42);
        when(existing.getUserid()).thenReturn(owner);
        when(postsRepository.findById(5)).thenReturn(Optional.of(existing));

        postsService.deletePost(5, 42);

        verify(postsRepository, times(1)).deleteById(5);
    }


    @Test
    void getFeed_WhenNeighbourhoodNotFound_ReturnsWithoutQueryingFeed() {
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(null);

        PostFeedResponseDTO result = postsService.getFeed(42, null, null, null, null);

        assertNotNull(result);
        verify(bulletinFeedRepository, never()).findFeed(anyInt(), any(), any(), anyInt(), anyInt());
        verify(bulletinFeedRepository, never()).countFeed(anyInt(), any(), any());
        verify(blobStorageService, never()).generateSasUrl(anyString());
    }

    @Test
    void getFeed_WhenNeighbourhoodNotFound_StillResolvesUserId() {
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(null);

        postsService.getFeed(42, "general", "cat", 3, 5);

        verify(bulletinFeedRepository, times(1)).findCallerNeighbourhood(42);
    }

    @Test
    void getFeed_WhenNeighbourhoodFound_UsesDefaultPageAndLimitWithZeroOffset() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, null, null, 20, 0)).thenReturn(List.of());
        when(bulletinFeedRepository.countFeed(5, null, null)).thenReturn(0L);

        PostFeedResponseDTO result = postsService.getFeed(42, null, null, null, null);

        assertNotNull(result);
        verify(bulletinFeedRepository, times(1)).findFeed(5, null, null, 20, 0);
        verify(bulletinFeedRepository, times(1)).countFeed(5, null, null);
    }

    @Test
    void getFeed_WhenPageAndLimitProvided_ComputesCorrectOffset() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, "general", "cat", 10, 20)).thenReturn(List.of());
        when(bulletinFeedRepository.countFeed(5, "general", "cat")).thenReturn(0L);

        postsService.getFeed(42, "general", "cat", 3, 10);

        verify(bulletinFeedRepository, times(1)).findFeed(5, "general", "cat", 10, 20);
        verify(bulletinFeedRepository, times(1)).countFeed(5, "general", "cat");
    }

    @Test
    void getFeed_WhenPageLessThanOne_DefaultsToPageOne() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, null, null, 20, 0)).thenReturn(List.of());
        when(bulletinFeedRepository.countFeed(5, null, null)).thenReturn(0L);

        postsService.getFeed(42, null, null, 0, null);

        verify(bulletinFeedRepository, times(1)).findFeed(5, null, null, 20, 0);
    }

    @Test
    void getFeed_WhenLimitLessThanOne_DefaultsToLimitTwenty() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, null, null, 20, 0)).thenReturn(List.of());
        when(bulletinFeedRepository.countFeed(5, null, null)).thenReturn(0L);

        postsService.getFeed(42, null, null, 1, 0);

        verify(bulletinFeedRepository, times(1)).findFeed(5, null, null, 20, 0);
    }

    @Test
    void getFeed_WhenPostsHaveMediaUrls_GeneratesSasUrlOnlyForThoseWithMedia() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");

        PostFeedItemDTO withMedia = mock(PostFeedItemDTO.class);
        when(withMedia.getMediaUrl()).thenReturn("https://acct.blob.core.windows.net/c/img.png");
        PostFeedItemDTO withoutMedia = mock(PostFeedItemDTO.class);
        when(withoutMedia.getMediaUrl()).thenReturn(null);

        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, null, null, 20, 0))
                .thenReturn(List.of(withMedia, withoutMedia));
        when(bulletinFeedRepository.countFeed(5, null, null)).thenReturn(2L);
        when(blobStorageService.generateSasUrl("https://acct.blob.core.windows.net/c/img.png"))
                .thenReturn("https://acct.blob.core.windows.net/c/img.png?sas=token");

        postsService.getFeed(42, null, null, null, null);

        verify(withMedia).setMediaUrl("https://acct.blob.core.windows.net/c/img.png?sas=token");
        verify(withoutMedia, never()).setMediaUrl(anyString());
        verify(blobStorageService, times(1)).generateSasUrl(anyString());
    }

    @Test
    void getFeed_PassesCategoryAndSearchThroughToFindFeedAndCountFeed() {
        CallerNeighbourhood neighbourhood = new CallerNeighbourhood(5, "Hillcrest");
        when(bulletinFeedRepository.findCallerNeighbourhood(42)).thenReturn(neighbourhood);
        when(bulletinFeedRepository.findFeed(5, "alert", "flood", 20, 0)).thenReturn(List.of());
        when(bulletinFeedRepository.countFeed(5, "alert", "flood")).thenReturn(0L);

        postsService.getFeed(42, "alert", "flood", null, null);

        verify(bulletinFeedRepository, times(1)).findFeed(5, "alert", "flood", 20, 0);
        verify(bulletinFeedRepository, times(1)).countFeed(5, "alert", "flood");
    }


    @Test
    void getPostDetail_WhenNotFound_ReturnsNull() {
        when(bulletinFeedRepository.findPostDetail(5)).thenReturn(null);

        PostDetailDTO result = postsService.getPostDetail(5);

        assertNull(result);
    }

    @Test
    void getPostDetail_WhenFoundWithMediaUrl_GeneratesSasUrl() {
        PostDetailDTO detail = mock(PostDetailDTO.class);
        when(detail.getMediaUrl()).thenReturn("https://acct.blob.core.windows.net/c/img.png");
        when(bulletinFeedRepository.findPostDetail(5)).thenReturn(detail);
        when(blobStorageService.generateSasUrl("https://acct.blob.core.windows.net/c/img.png"))
                .thenReturn("https://acct.blob.core.windows.net/c/img.png?sas=token");

        PostDetailDTO result = postsService.getPostDetail(5);

        assertSame(detail, result);
        verify(detail).setMediaUrl("https://acct.blob.core.windows.net/c/img.png?sas=token");
    }

    @Test
    void getPostDetail_WhenFoundWithoutMediaUrl_DoesNotGenerateSasUrl() {
        PostDetailDTO detail = mock(PostDetailDTO.class);
        when(detail.getMediaUrl()).thenReturn(null);
        when(bulletinFeedRepository.findPostDetail(5)).thenReturn(detail);

        PostDetailDTO result = postsService.getPostDetail(5);

        assertSame(detail, result);
        verify(detail, never()).setMediaUrl(anyString());
        verify(blobStorageService, never()).generateSasUrl(anyString());
    }
}