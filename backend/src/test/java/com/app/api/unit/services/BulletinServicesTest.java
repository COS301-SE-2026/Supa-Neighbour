package com.app.api.unit.services;

import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.repositories.UserRepository;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.services.CommentsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.app.api.security.FirebaseAuthenticationFilter;
import com.app.api.models.User;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.Reaction;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.ReactionRepository;
import com.app.api.repositories.CommentsRepository;
import java.util.List;
import com.app.api.services.ReactionService;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class BulletinServicesTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostsRepository postRepository;

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private ReactionService reactionService;

    @InjectMocks
    private CommentsService commentsService;

    @MockitoBean
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Test
    void addCommentToPost_validInput_returnsCommentResponse() {

        assertNotNull(commentsService);

        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Hello world");

        User user = new User();
        user.setUserid(1);
        user.setFirstName("Sarag");
        user.setLastName("soon");

        Posts post = new Posts();
        post.setPostid(1);

        Comments comment = new Comments();
        comment.setCommentid(1);
        comment.setPostid(post);
        comment.setUserid(user);
        comment.setCommentContent("Hello world");
        comment.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(postRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(userRepository.getReferenceById(1))
                .thenReturn(user);

        when(commentsRepository.save(any(Comments.class)))
                .thenReturn(comment);

        CommentResponseDTO response = commentsService.addCommentToPost(1, request, 1);
        assertNotNull(response);
        assertEquals(1, response.getCommentId());
        assertEquals("Hello world", response.getCommentContent());
    }

    @Test
    void getCommentsByPostId_returnsComments() {

        assertNotNull(commentsService);

        User user = new User();
        user.setUserid(1);
        user.setFirstName("Sarag");
        user.setLastName("soon");

        Posts post = new Posts();
        post.setPostid(1);

        Comments mockComments = mock(Comments.class);

        when(mockComments.getPostid()).thenReturn(post);
        when(mockComments.getUserid()).thenReturn(user);
        when(mockComments.getCommentid()).thenReturn(1);
        when(mockComments.getCommentContent()).thenReturn("tester");
        when(mockComments.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));

        when(commentsRepository.findByPostid_Postid(1))
                .thenReturn(List.of(mockComments));

        List<CommentPostResponseDTO> response = commentsService.getCommentsByPostId(1, 1);

        assertEquals(1, response.size());
        assertEquals("tester", response.get(0).getContent());
    }

    @Test
    void addDislikeReaction_validRequest_returnsReaction() {

        assertNotNull(commentsService);


        User user = new User();
        user.setUserid(1);
        Posts posts = new Posts();
        posts.setPostid(1);

        when(postRepository.findById(1))
                .thenReturn(Optional.of(posts));

        when(userRepository.getReferenceById(1))
                .thenReturn(user);

        when(reactionRepository.countByUserAndPost(1, 1)).thenReturn(0L);
        System.out.println("addDislikeReaction called");

        when(reactionRepository.save(any(Reaction.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        when(reactionRepository.countDisLiked(1))
                .thenReturn(1L);

        ReactionResponseDTO response = reactionService.addDislikeReaction(1, 1);

        assertNotNull(response);
        assertEquals("Reaction added", response.getMessage());

        verify(reactionRepository).save(any(Reaction.class));
    }

    @Test
    void removeDislikeReaction_existingReaction_returnResponse() {

        assertNotNull(commentsService);

        Reaction reaction = new Reaction();

        when(reactionRepository.findByUserAndPostAndType(1, 1, "dislike"))
                .thenReturn(Optional.of(reaction));

        when(reactionRepository.countDisLiked(1))
                .thenReturn(0L);

        ReactionRemovedResponseDTO response = reactionService.removeDisLikeReaction(1, 1);

        assertNotNull(response);
        assertEquals("Reaction removed", response.getMessage());

        verify(reactionRepository).delete(reaction);
    }
}
