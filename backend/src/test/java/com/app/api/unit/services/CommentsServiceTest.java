package com.app.api.unit.services;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
 
import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.User;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.CommentsService;

/**
 * Unite tests for {@link CommentsService}
 */
@ExtendWith(MockitoExtension.class)
public class CommentsServiceTest {
    
    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock 
    private UserRepository userRepository;

    @InjectMocks
    private CommentsService commentsService;


    private User user;
    private Comments existingComment;
    private Posts post;

    @BeforeEach
    void setup(){
        user = new User();
        user.setUserid(10);
        user.setFirstName("Ble");
        user.setLastName("Neo");

        post = new Posts();
        post.setPostid(100);
        post.setUserid(user);

        existingComment = new Comments();
        existingComment.setCommentid(1);
        existingComment.setPostid(post);
        existingComment.setUserid(user);
        existingComment.setCommentContent("Original content");
        existingComment.setCreatedAt(Timestamp.from(Instant.now()));

    }

    @Test
    void getAllComments_returnsRepositoryList(){
        List<Comments> comments = List.of(existingComment);
        when(commentsRepository.findAll()).thenReturn(comments);

        List<Comments> result = commentsService.getAllComments();

        assertThat(result).isEqualTo(comments);
    }

    @Test
    void getCommentsById_found_returnsComment(){
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));

        Comments result = commentsService.getCommentsById(1);

        assertThat(result).isEqualTo(existingComment);
    }

    @Test
    void getCommentsById_notFound_returnsNull(){
        when(commentsRepository.findById(99)).thenReturn(Optional.empty());

        Comments result = commentsService.getCommentsById(99);

        assertThat(result).isNull();

    }

    @Test
    void saveComments_null_returnsNull(){
        Comments result = commentsService.saveComments(null);

        assertThat(result).isNull();
        verify(commentsRepository, never()).save(any());
    }

    @Test
    void saveComments_valid_savesAndReturns(){
        when(commentsRepository.save(existingComment)).thenReturn(existingComment);

        Comments result = commentsService.saveComments(existingComment);

        assertThat(result).isEqualTo(existingComment);
        verify(commentsRepository).save(existingComment);
    }

    @Test
    void updateComments_found_updatesFieldsAndSaves(){
        Comments updated = new Comments();

        updated.setUserid(user);
        updated.setPostid(post);
        updated.setParentCommentid(5);
        updated.setCommentContent("Updated content");
        Timestamp newCreatedAt = Timestamp.from(Instant.now());

        updated.setCreatedAt(newCreatedAt);
        updated.setUpdatedAt(newCreatedAt);

        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));
        when(commentsRepository.save(existingComment)).thenReturn(existingComment);

        Comments result = commentsService.updateComments(1, updated);
        assertThat(result.getCommentContent()).isEqualTo("Updated content");

        assertThat(result.getParentCommentid()).isEqualTo(5);
        verify(commentsRepository).save(existingComment);
    }

    @Test
    void updateComments_notFound_returnsNullAndDoesNotSave(){
        when(commentsRepository.findById(99)).thenReturn(Optional.empty());

        Comments result = commentsService.updateComments(99, new Comments());

        assertThat(result).isNull();

        verify(commentsRepository, never()).save(any());
    }

    @Test
    void deleteComments_callsRepositoryDeleteById(){
        commentsService.deleteComments(1);
        verify(commentsRepository).deleteById(1);
    }

    @Test
    void addCommentToPost_validTopLevelComment_returnsResponseDTO(){
        CommentRequestDTO request = new CommentRequestDTO();

        request.setCommentContent("Nice post!");
        request.setParentCommentId(null);

        when(postsRepository.findById(100)).thenReturn(Optional.of(post));
        when(userRepository.getReferenceById(10)).thenReturn(user);
        when(commentsRepository.save(any(Comments.class))).thenAnswer(invocation -> {
            Comments toSave = invocation.getArgument(0);
            toSave.setCommentid(50);
            return toSave;
        });

        CommentResponseDTO result = commentsService.addCommentToPost(100, request, 10);

        assertThat(result.getCommentId()).isEqualTo(50);
        assertThat(result.getPostId()).isEqualTo(100);
        assertThat(result.getuserId()).isEqualTo(10);
        assertThat(result.getCommentContent()).isEqualTo("Nice post!");
        assertThat(result.getParentCommentId()).isNull();
    }

    @Test
    void addCommentToPost_validReply_returnsResponseDTOWithParent(){
        CommentRequestDTO request = new CommentRequestDTO();

        request.setCommentContent("Replying here");
        request.setParentCommentId(1);
        when(postsRepository.findById(100)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));

        when(userRepository.getReferenceById(10)).thenReturn(user);
        when(commentsRepository.save(any(Comments.class))).thenAnswer(invocation ->{
            Comments toSave = invocation.getArgument(0);
            toSave.setCommentid(51);
            return toSave;
        });
        CommentResponseDTO result = commentsService.addCommentToPost(100, request, 10);
        assertThat(result.getParentCommentId()).isEqualTo(1);
    }

    @Test
    void addCommentToPost_blankContent_throwsUnprocessableEntity(){
        CommentRequestDTO request = new CommentRequestDTO();

        request.setCommentContent("  ");
        assertThatThrownBy(() -> commentsService.addCommentToPost(100, request, 10)).isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);

        verify(postsRepository, never()).findById(any());
    }

    @Test
    void addCommentToPost_nulllContent_throwsnUnprocessableEntity(){
        CommentRequestDTO request = new CommentRequestDTO();

        request.setCommentContent(null);
        assertThatThrownBy(() -> commentsService.addCommentToPost(100, request, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Test
    void addCommentToPost_postNotFound_throwsNotFound(){
        CommentRequestDTO request = new CommentRequestDTO();

        request.setCommentContent("Nice post!");
        when(postsRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentsService.addCommentToPost(999, request, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

        verify(commentsRepository, never()).save(any());
    }

    @Test
    void addCommentToPost_parentCommentNotFound_throwsNotFound(){
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Replying here");
        request.setParentCommentId(404);

        when(postsRepository.findById(100)).thenReturn(Optional.of(post));

        when(commentsRepository.findById(404)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentsService.addCommentToPost(100, request, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void addCommentToPost_parentCommentBelongsToDifferentPost_throwsNotFound(){
        Posts otherPost = new Posts();
        otherPost.setPostid(200);
        existingComment.setPostid(otherPost);

        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Replying here");
        request.setParentCommentId(1);

        when(postsRepository.findById(100)).thenReturn(Optional.of(post));
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));

        assertThatThrownBy(() -> commentsService.addCommentToPost(100, request, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllCommentByPostId_mapsEntitiesToDTOs(){
        when(commentsRepository.findByPostid_Postid(100)).thenReturn(List.of(existingComment));

        List<CommentPostResponseDTO> result = commentsService.getAllCommentsByPostId(100);

        assertThat(result).hasSize(1);
        CommentPostResponseDTO dto = result.get(0);
        assertThat(dto.getCommentId()).isEqualTo(1);
        assertThat(dto.getPostId()).isEqualTo(100);
        assertThat(dto.getUserId()).isEqualTo(10);
        assertThat(dto.getUserName()).isEqualTo("Ble Neo");
        assertThat(dto.getContent()).isEqualTo("Original content");
    }

    @Test
    void getCommentsByPostId_mapEntitiesToDTOs(){
        when(commentsRepository.findByPostid_Postid(100)).thenReturn(List.of(existingComment));

        List<CommentPostResponseDTO> result = commentsService.getCommentsByPostId(100, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Original content");
    }

    @Test
    void getAllCommentByPostId_noComments_returnsEmptyList(){
        when(commentsRepository.findByPostid_Postid(100)).thenReturn(List.of());

        List<CommentPostResponseDTO> result = commentsService.getAllCommentsByPostId(100);

        assertThat(result).isEmpty();
    }

    @Test
    void deleteCommentFromPost_ownerAndCorrectPost_deleteComment(){
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));
        commentsService.deleteCommentFromPost(100, 1, 10);

        verify(commentsRepository, times(1)).delete(existingComment);
    }

    @Test
    void deleteCommentFromPost_commentNotFound_throwsNotFound(){
        when(commentsRepository.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> commentsService.deleteCommentFromPost(100, 1, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
        verify(commentsRepository, never()).delete(any());
    }

    @Test
    void deleteCommentFromPost_commentBelongsToDifferentPost_throwsNotFound(){
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));

        assertThatThrownBy(() -> commentsService.deleteCommentFromPost(999, 1, 10))
        .isInstanceOf(ResponseStatusException.class)
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);

        verify(commentsRepository, never()).delete(any());
    }

    @Test
    void deleteCommentFromPost_notOwner_throwsForbidden() {
        when(commentsRepository.findById(1)).thenReturn(Optional.of(existingComment));
 
        assertThatThrownBy(() -> commentsService.deleteCommentFromPost(100, 1, 999))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
 
        verify(commentsRepository, never()).delete(any());
    }
}