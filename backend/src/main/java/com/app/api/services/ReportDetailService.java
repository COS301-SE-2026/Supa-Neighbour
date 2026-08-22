package com.app.api.services;
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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReportDetailService {
    private static final int SNIPPET_LENGTH = 150;

    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ReportDetailService(PostsRepository postsRepository,  CommentsRepository commentsRepository, TaskRepository taskRepository, UserRepository userRepository){
        this.postsRepository = postsRepository;
        this.commentsRepository = commentsRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Resolves the detail object for a given report. Returns null if the
     * report has no reportType, no target ID for its type, or the
     * referenced entity no longer exists (e.g. a deleted post) — a missing
     * detail shouldn't break the report list.
     *
     * @param report the report to resolve details for
     * @return a type-specific summary DTO, or null if unresolvable
     */
    public Object resolveDetails(Report report){
        if(report.getReportType() == null){
            return null;
        }

        return switch(report.getReportType()){
            case "POST" -> report.getReportedPostId() == null ? null : resolvePost(report.getReportedPostId());
            case "COMMENT" -> report.getReportedCommentId() == null ? null : resolveComment(report.getReportedCommentId());
            case "TASK_DISPUTE" -> report.getTaskId() == null ? null : resolveTask(report.getTaskId());
            case "USER" -> report.getReportedUserId() == null ? null : resolveUser(report.getReportedUserId());
            default -> null;
        };
    }

    private PostSummaryDTO resolvePost( int postId){
        Optional<Posts> postOpt = postsRepository.findById(postId);
        if(postOpt.isEmpty()){
            return null;
        }

        Posts post = postOpt.get();
        Integer authorId = post.getUserid() == null ? null : post.getUserid().getUserid();
        return new PostSummaryDTO(post.getPostid(),
            truncate(post.getPostContent()),
            post.getCategory(),
            post.getMediaURL(),
            authorId,
            post.getCreatedAt());
    }

    private CommentSummaryDTO resolveComment(int commentId){
        Optional<Comments> commentOpt = commentsRepository.findById(commentId);
        if(commentOpt.isEmpty()){
            return null;
        }

        Comments comment = commentOpt.get();
        Integer authorId = comment.getUserid() == null ? null : comment.getUserid().getUserid();
        Integer postId = comment.getPostid() == null ? null : comment.getPostid().getPostid();
        return new CommentSummaryDTO(
            comment.getCommentid(),
            truncate(comment.getCommentContent()),
            postId,
            authorId,
            comment.getCreatedAt());
    }

    private TaskSummaryDTO resolveTask(int taskId){
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if(taskOpt.isEmpty()){
            return null;
        }

        Task task = taskOpt.get();
        return new TaskSummaryDTO(
            task.getTaskId(),
            task.getTitle(),
            truncate(task.getInstructions()),
            task.getStatus(),
            task.getTaskTypeId(),
            task.getHelperId(),
            task.getDependentId());
    }

    private UserSummaryDTO resolveUser(int userId){
        Optional<User> userOpt = userRepository.findById(userId);
        if(userOpt.isEmpty()){
            return null;
        }

        User user = userOpt.get();
        return new UserSummaryDTO(
            user.getUserid(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName());
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        return text.length() <= SNIPPET_LENGTH ? text : text.substring(0, SNIPPET_LENGTH) + "…";
    }
}
