package com.app.api.unit.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.api.dtos.CommentSummaryDTO;
import com.app.api.dtos.PostSummaryDTO;
import com.app.api.dtos.TaskSummaryDTO;
import com.app.api.dtos.UserSummaryDTO;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.Report;
import com.app.api.models.Task;
import com.app.api.models.User;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.TaskRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.ReportDetailService;

@ExtendWith(MockitoExtension.class)
 
/**
 * Unit tests for {@link ReportDetailService}.
 *
 * All repositories are mocked so only the service's own dispatch,
 * null-handling, and truncation logic is exercised.
 */
@ExtendWith(MockitoExtension.class)
public class ReportDetailServiceTest {
    
    @Mock
    private PostsRepository postsRepository;

    @Mock
    private CommentsRepository commentsRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Report report;

    @Mock
    private Posts post;

    @Mock
    private Comments comment;

    @Mock
    private Task task;

    @Mock
    private User user;

    @Mock
    private User author;

    @InjectMocks
    private ReportDetailService reportDetailService;

    @Test
    void resolveDetailes_whenReportTypeIsNull_returnsNull() {

        when(report.getReportType()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);

        assertNull(result);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            taskRepository,
            userRepository
        );
    }

    @Test
    void resolveDetails_whenPostReportWithNullPostId_returnsNull() {
        when(report.getReportType()).thenReturn("POST");
        when(report.getReportedPostId()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);
        assertNull(result);
        verifyNoInteractions(postsRepository);
    }

    @Test
    void resolveDetails_whenPostDoesNotExist_returnsNull() {
        when(report.getReportType()).thenReturn("POST");
        when(report.getReportedPostId()).thenReturn(1);

        when(postsRepository.findById(1)).thenReturn(Optional.empty());

        Object result = reportDetailService.resolveDetails(report);

        assertNull(result);

        verify(postsRepository).findById(1);

        verifyNoInteractions(
            commentsRepository,
            taskRepository,
            userRepository
        );
    }

    @Test
    void resolveDetails_whenPostExists_returnsPostSummaryDTO() {
        when(report.getReportType()).thenReturn("POST");
        when(report.getReportedPostId()).thenReturn(1);

        when(postsRepository.findById(1)).thenReturn(Optional.of(post));
        
        when(post.getPostid()).thenReturn(1);
        when(post.getPostContent()).thenReturn("this is a test post");
        when(post.getCategory()).thenReturn("GENERAL");
        when(post.getMediaURL()).thenReturn("test.jpg");
        when(post.getUserid()).thenReturn(author);

        when(author.getUserid()).thenReturn(10);

        Object result = reportDetailService.resolveDetails(report);
        assertTrue(result instanceof PostSummaryDTO);

        verify(postsRepository).findById(1);

        verifyNoInteractions(            
            commentsRepository,
            taskRepository,
            userRepository);
    }

    @Test
    void resolveDetails_whenPostContetIsLong_truncatesContent() {
        String longText = "A".repeat(200);

        when(report.getReportType()).thenReturn("POST");
        when(report.getReportedPostId()).thenReturn(1);

        when(postsRepository.findById(1)).thenReturn(Optional.of(post));

        when(post.getPostid()).thenReturn(1);
        when(post.getPostContent()).thenReturn(longText);
        when(post.getCategory()).thenReturn("GENERAL");
        when(post.getMediaURL()).thenReturn(null);
        when(post.getUserid()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);
        assertTrue(result instanceof PostSummaryDTO);
        //PostSummaryDTO dto = (PostSummaryDTO) result;
    }

@Test
void resolveDetails_whenPostAuthorIsNull_returnPostSummary() {

    // Report information
    when(report.getReportType()).thenReturn("POST");
    when(report.getReportedPostId()).thenReturn(1);

    // Repository finds the post
    when(postsRepository.findById(1))
            .thenReturn(Optional.of(post));

    // Post information
    when(post.getPostid()).thenReturn(1);
    when(post.getPostContent()).thenReturn("Test post");
    when(post.getCategory()).thenReturn("GENERAL");
    when(post.getMediaURL()).thenReturn(null);

    // IMPORTANT: author is null
    when(post.getUserid()).thenReturn(null);

    // Execute
    Object result = reportDetailService.resolveDetails(report);

    // Verify
    
    assertNotNull(result);
    assertTrue(result instanceof PostSummaryDTO);

    PostSummaryDTO dto = (PostSummaryDTO) result;

    assertEquals(1, dto.getPostId());
    assertEquals("Test post", dto.getContentSnippet());
    assertEquals("GENERAL", dto.getCategory());
    assertNull(dto.getAuthorUserId());

    verify(postsRepository).findById(1);
}
    //comments section 

    @Test
    void resolveDetails_whenCommentReportWithNullCommentId_returnsNull() {
        when(report.getReportType()).thenReturn("COMMENT");
        when(report.getReportedCommentId()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);

        assertNull(result);
        verifyNoInteractions(commentsRepository);
    }

    @Test
    void resolveDetails_whenCommentDoesNotExist_returnsNull() {
        
        when(report.getReportType()).thenReturn("COMMENT");
        when(report.getReportedCommentId()).thenReturn(5);

        when(commentsRepository.findById(5)).thenReturn(Optional.empty());

        Object result = reportDetailService.resolveDetails(report);

        assertNull(result);
        verify(commentsRepository).findById(5);

        verifyNoInteractions(            
            postsRepository,
            taskRepository,
            userRepository);
    }

        @Test
    void resolveDetails_whenCommentExists_returnsCommentSummaryDTO() {

        when(report.getReportType()).thenReturn("COMMENT");
        when(report.getReportedCommentId()).thenReturn(5);

        when(commentsRepository.findById(5))
            .thenReturn(Optional.of(comment));

        when(comment.getCommentid()).thenReturn(5);
        when(comment.getCommentContent())
            .thenReturn("This is a test comment");

        when(comment.getUserid()).thenReturn(author);
        when(author.getUserid()).thenReturn(10);

        when(comment.getPostid()).thenReturn(post);
        when(post.getPostid()).thenReturn(20);

        Object result = reportDetailService.resolveDetails(report);

        assertTrue(result instanceof CommentSummaryDTO);

        verify(commentsRepository).findById(5);

        verifyNoInteractions(
            postsRepository,
            taskRepository,
            userRepository
        );
    }

    @Test
    void resolveDetails_whenCommentAuthorIsNull_returnsCommentSummary() {
                
        when(report.getReportType()).thenReturn("COMMENT");
        when(report.getReportedCommentId()).thenReturn(5);

        when(commentsRepository.findById(5))
            .thenReturn(Optional.of(comment));

        when(comment.getCommentid()).thenReturn(5);
        when(comment.getCommentContent()).thenReturn("Test comment");
        when(comment.getUserid()).thenReturn(null);
        when(comment.getPostid()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);

        assertTrue(result instanceof CommentSummaryDTO);
    }

    //tasks

    @Test
    void resolveDetails_whenTaskDisputeWithNullTaskId_returnsNull() {
        when(report.getReportType()).thenReturn("TASK_DISPUTE");
        when(report.getTaskId()).thenReturn(null);

        Object result= reportDetailService.resolveDetails(report);

        assertNull(result);
        verifyNoInteractions(taskRepository);
    }

    @Test
    void resolveDetails_whenTasjDoesNitExist_returnsNull() {
        when(report.getReportType()).thenReturn("TASK_DISPUTE");
        when(report.getTaskId()).thenReturn(10);

        when(taskRepository.findById(10)).thenReturn(Optional.empty());

        Object result = reportDetailService.resolveDetails(report);
        assertNull(result);
        verify(taskRepository).findById(10);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            userRepository
        );
    }

    @Test
    void resolveDetails_whenTaskExists_returnsTaskSummaryDTO() {
        
        when(report.getReportType()).thenReturn("TASK_DISPUTE");
        when(report.getTaskId()).thenReturn(10);

        when(taskRepository.findById(10)).thenReturn(Optional.of(task));
        when(task.getTaskId()).thenReturn(10);
        when(task.getTitle()).thenReturn("Test Task");
        when(task.getInstructions())
            .thenReturn("Complete this test task");

        Object result = reportDetailService.resolveDetails(report);

        assertTrue(result instanceof TaskSummaryDTO);

        verify(taskRepository).findById(10);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            userRepository
        );
    }
    //users reports 

    @Test
    void resolveDetails_whenUserReportWithNullUserid_returnsNul() {
        when(report.getReportType()).thenReturn("USER");
        when(report.getReportedUserId()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);
        assertNull(result);
        verifyNoInteractions(userRepository);
    }

    @Test
    void resolveDetailsd_whenUserDoesNotExist_returnNull() {

        when(report.getReportType()).thenReturn("USER");
        when(report.getReportedUserId()).thenReturn(20);

        when(userRepository.findById(20)).thenReturn(Optional.empty());

        Object result= reportDetailService.resolveDetails(report);
        assertNull(result);

        verify(userRepository).findById(20);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            taskRepository
        );
    }

    @Test
    void resolveDetails_whenUserExists_returnsUserSummaryDTO() {

        when(report.getReportType()).thenReturn("USER");
        when(report.getReportedUserId()).thenReturn(20);

        when(userRepository.findById(20)).thenReturn(Optional.of(user));

        when(user.getUserid()).thenReturn(20);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getFirstName()).thenReturn("Test");
        when(user.getLastName()).thenReturn("User");

        Object result = reportDetailService.resolveDetails(report);
        assertTrue(result instanceof UserSummaryDTO);


        verify(userRepository).findById(20);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            taskRepository
        );
    }
    //unknown type

    @Test
    void resolveDetails_whenRepostTypeIsUnknown_returnsNull() {
        when(report.getReportType()).thenReturn("UNKNOWN");

        Object result = reportDetailService.resolveDetails(report);
        assertNull(result);

        verifyNoInteractions(
            postsRepository,
            commentsRepository,
            taskRepository,
            userRepository
        );
    }

    @Test
    void resolveDetails_whenPostContentIsNull_returnsPostSummary() {

        when(report.getReportType()).thenReturn("POST");
        when(report.getReportedPostId()).thenReturn(1);

        when(postsRepository.findById(1)).thenReturn(Optional.of(post));

        when(post.getPostid()).thenReturn(1);
        when(post.getPostContent()).thenReturn(null);
        when(post.getCategory()).thenReturn("GENERAL");
        when(post.getMediaURL()).thenReturn(null);
        when(post.getUserid()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);
        assertTrue(result instanceof PostSummaryDTO);
    }

    @Test 
    void resolveDetails_whenCommentContentIsNull_returnsCommentSummary() {
        when(report.getReportType()).thenReturn("COMMENT");
        when(report.getReportedCommentId()).thenReturn(5);

        when(commentsRepository.findById(5)).thenReturn(Optional.of(comment));

        when(comment.getCommentid()).thenReturn(5);
        when(comment.getCommentContent()).thenReturn(null);
        when(comment.getUserid()).thenReturn(null);
        when(comment.getPostid()).thenReturn(null);

        Object result = reportDetailService.resolveDetails(report);
        assertTrue(result instanceof CommentSummaryDTO);
    }
}
