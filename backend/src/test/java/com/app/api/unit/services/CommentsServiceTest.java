package com.app.api.unit.services;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtention.class)
public class CommentsServiceTest {
    
    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock 
    private UserRepository userRepository;


    private CommentsService commentsService;
    private User user;
    private Comments existingComment;
    private Posts post;

    @BeforeEach
    void setup(){
        commentsService =  new CommentsService(commentsRepository, postsRepository, userRepository);

        post = mock(Posts.class);
        lenient().when(post.getPostif()).thenReturn(100);

        user = new User();
        user.setUserid(1);
        user.setFirstName("Ble");
        user.setLastName("Neo");

        existingComment = new Comments();
        existingComment.setCommentid(5);
        existingComment.setPostid(post);
        existingComment.setUserid(user);
        existingComment.setCommentContent("Great idea!");
        existingComment.setParentCommentid(null);
        existingComment.setCreatedAt(Timestamp.from(Instant.now()));

    }
}
