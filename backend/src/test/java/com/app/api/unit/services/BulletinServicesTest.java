package com.app.api.unit.services;

import com.app.api.dtos.CommentPostResponseDTO;
import com.app.api.dtos.CommentRequestDTO;
import com.app.api.dtos.CommentResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.dtos.ShowStatusResponse;
import com.app.api.dtos.UserStatusResponse;
import com.app.api.models.Settings;
import com.app.api.repositories.SettingsRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.HelperRepository;
import com.app.api.services.HelperTasksService;
import com.app.api.services.AchievementService;
import com.app.api.services.CommentsService;
import com.app.api.services.RatingService;
import com.app.api.services.SettingsServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import com.app.api.models.User;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.Reaction;
import com.app.api.repositories.UserRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.ReactionRepository;
import com.app.api.repositories.CommentsRepository;
import java.util.List;
import com.app.api.services.ReactionService;

import java.time.Instant;
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

    @Mock
    private ReactionService reactionService;

    @InjectMocks
    private CommentsService commentsService;


    
    @Test
    void addCommentToPost_validInput_returnsCommentResponse(){
        CommentRequestDTO request = new CommentRequestDTO();
        request.setCommentContent("Hello world");
        request.setParentCommentId(null);

        User user = new User();
        Posts post = new Posts();
        Comments comment = new Comments();



        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(commentsRepository.save(any(Comments.class))).thenReturn(comment);

        CommentResponseDTO response = commentsService.addCommentToPost(1,request,1);
        assertNotNull(response);
    }

    @Test
    void getCommentsByPostId_returnsComments(){
        
        User user = new User();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(commentsRepository.findByPostid_Postid(1)).thenReturn(List.of(mock(Comments.class)));

        List<CommentPostResponseDTO> response = commentsService.getCommentsByPostId(1, 1);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void addDislikeReaction_validRequest_returnsReaction(){
        User user = new User();
        Posts posts = new Posts();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(postRepository.findById(1)).thenReturn(Optional.of(posts));
        when(reactionRepository.save(any(Reaction.class)));

        ReactionResponseDTO response = reactionService.addDislikeReaction(1,1);

        assertNotNull(response);
    }

    @Test
    void removeDislikeReaction_existingReaction_returnResponse(){

        Reaction reaction = new Reaction();

        when(reactionRepository.findByUserAndPostAndType(1, 1, "Dislike"))
        .thenReturn(Optional.of(reaction));

        ReactionRemovedResponseDTO response = reactionService.removeDisLikeReaction(1, 1);

        assertNotNull(response);

        verify(reactionRepository).delete(reaction);
    }
}

