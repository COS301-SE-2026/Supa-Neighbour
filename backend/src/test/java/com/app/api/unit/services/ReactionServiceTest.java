package com.app.api.unit.services;

import com.app.api.dtos.CommentReactionResponseDTO;
import com.app.api.dtos.ReactionRemovedResponseDTO;
import com.app.api.dtos.ReactionResponseDTO;
import com.app.api.models.Comments;
import com.app.api.models.Posts;
import com.app.api.models.Reaction;
import com.app.api.models.User;
import com.app.api.repositories.CommentsRepository;
import com.app.api.repositories.PostsRepository;
import com.app.api.repositories.ReactionRepository;
import com.app.api.repositories.UserRepository;
import com.app.api.services.ReactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostsRepository postsRepository;

    @Mock
    private CommentsRepository commentsRepository;

    private ReactionService reactionService;

    @BeforeEach
    void setUp() {
        reactionService = new ReactionService(
                reactionRepository,
                userRepository,
                postsRepository,
                commentsRepository);
    }

    // =========================================================
    // BASIC CRUD
    // =========================================================

    @Test
    void getAllreaction_returnsAllReactions() {

        Reaction r1 = new Reaction();
        Reaction r2 = new Reaction();

        when(reactionRepository.findAll())
                .thenReturn(List.of(r1, r2));

        List<Reaction> result = reactionService.getAllreaction();

        assertEquals(2, result.size());
        assertEquals(r1, result.get(0));
        assertEquals(r2, result.get(1));

        verify(reactionRepository).findAll();
    }

    @Test
    void getLikeById_whenFound_returnsReaction() {

        Reaction reaction = new Reaction();

        when(reactionRepository.findById(1))
                .thenReturn(Optional.of(reaction));

        Reaction result = reactionService.getLikeById(1);

        assertEquals(reaction, result);

        verify(reactionRepository).findById(1);
    }

    @Test
    void getLikeById_whenNotFound_returnsNull() {

        when(reactionRepository.findById(1))
                .thenReturn(Optional.empty());

        Reaction result = reactionService.getLikeById(1);

        assertNull(result);
    }

    @Test
    void saveLike_whenNull_returnsNull() {

        Reaction result = reactionService.saveLike(null);

        assertNull(result);

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void saveLike_whenValid_returnsSavedReaction() {

        Reaction reaction = new Reaction();

        when(reactionRepository.save(reaction))
                .thenReturn(reaction);

        Reaction result = reactionService.saveLike(reaction);

        assertEquals(reaction, result);

        verify(reactionRepository).save(reaction);
    }

    @Test
    void updateLike_whenFound_updatesFields() {

        Reaction existing = new Reaction();
        Reaction updated = new Reaction();

        Posts post = new Posts();
        Comments comment = new Comments();
        User user = new User();

        updated.setPostid(post);
        updated.setCommentid(comment);
        updated.setUserid(user);
        updated.setReactionType("like");

        when(reactionRepository.findById(1))
                .thenReturn(Optional.of(existing));

        when(reactionRepository.save(existing))
                .thenReturn(existing);

        Reaction result = reactionService.updateLike(1, updated);

        assertEquals(existing, result);
        assertEquals(post, existing.getPostid());
        assertEquals(comment, existing.getCommentid());
        assertEquals(user, existing.getUserid());

        verify(reactionRepository).save(existing);
    }

    @Test
    void updateLike_whenNotFound_returnsNull() {

        when(reactionRepository.findById(1))
                .thenReturn(Optional.empty());

        Reaction updated = new Reaction();

        Reaction result = reactionService.updateLike(1, updated);

        assertNull(result);

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void deleteLike_deletesReaction() {

        reactionService.deleteLike(1);

        verify(reactionRepository).deleteById(1);
    }

    // =========================================================
    // POST DISLIKE
    // =========================================================

    @Test
    void addDislikeReaction_whenValid_createsReaction() {

        Posts post = new Posts();
        User user = new User();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(0L);

        when(userRepository.getReferenceById(10))
                .thenReturn(user);

        when(reactionRepository.countDisLiked(1))
                .thenReturn(5L);

        ReactionResponseDTO result = reactionService.addDislikeReaction(1, 10);

        assertNotNull(result);

        verify(reactionRepository).save(any(Reaction.class));
        verify(reactionRepository).countDisLiked(1);
    }

    @Test
    void addDislikeReaction_whenPostNotFound_throws404() {

        when(postsRepository.findById(1))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addDislikeReaction(1, 10));

        assertEquals(404, exception.getStatusCode().value());

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void addDislikeReaction_whenAlreadyReacted_throws409() {

        Posts post = new Posts();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addDislikeReaction(1, 10));

        assertEquals(409, exception.getStatusCode().value());

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void addDislikeReaction_whenDatabaseRejectsReaction_throws409() {

        Posts post = new Posts();
        User user = new User();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(0L);

        when(userRepository.getReferenceById(10))
                .thenReturn(user);

        when(reactionRepository.save(any(Reaction.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addDislikeReaction(1, 10));

        assertEquals(409, exception.getStatusCode().value());
    }

    // =========================================================
    // COMMENT DISLIKE
    // =========================================================

    @Test
    void addDislikeReactionToComment_whenValid_createsReaction() {

        Comments comment = new Comments();
        User user = new User();

        when(commentsRepository.findById(1))
                .thenReturn(Optional.of(comment));

        when(reactionRepository.countByUserAndComment(10, 1))
                .thenReturn(0L);

        when(userRepository.getReferenceById(10))
                .thenReturn(user);

        when(reactionRepository.countDislikedComment(1))
                .thenReturn(3L);

        CommentReactionResponseDTO result = reactionService.addDislikeReactionToComment(1, 10);

        assertNotNull(result);

        verify(reactionRepository).save(any(Reaction.class));
        verify(reactionRepository).countDislikedComment(1);
    }

    @Test
    void addDislikeReactionToComment_whenCommentNotFound_throws404() {

        when(commentsRepository.findById(1))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addDislikeReactionToComment(1, 10));

        assertEquals(404, exception.getStatusCode().value());

        verify(reactionRepository, never()).save(any());
    }

    @Test
    void addDislikeReactionToComment_whenAlreadyReacted_throws409() {

        Comments comment = new Comments();

        when(commentsRepository.findById(1))
                .thenReturn(Optional.of(comment));

        when(reactionRepository.countByUserAndComment(10, 1))
                .thenReturn(1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addDislikeReactionToComment(1, 10));

        assertEquals(409, exception.getStatusCode().value());

        verify(reactionRepository, never()).save(any());
    }

    // =========================================================
    // REMOVE POST DISLIKE
    // =========================================================

    @Test
    void removeDisLikeReaction_whenFound_deletesReaction() {

        Reaction reaction = new Reaction();

        when(reactionRepository.findByUserAndPostAndType(
                10, 1, "dislike"))
                .thenReturn(Optional.of(reaction));

        when(reactionRepository.countDisLiked(1))
                .thenReturn(4L);

        ReactionRemovedResponseDTO result = reactionService.removeDisLikeReaction(1, 10);

        assertNotNull(result);

        verify(reactionRepository).delete(reaction);
        verify(reactionRepository).countDisLiked(1);
    }

    @Test
    void removeDisLikeReaction_whenNotFound_throws404() {

        when(reactionRepository.findByUserAndPostAndType(
                10, 1, "dislike"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.removeDisLikeReaction(1, 10));

        assertEquals(404, exception.getStatusCode().value());

        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void addHelpfulReactionToPost_whenValid_createsLike() {

        Posts post = new Posts();

        User user = new User();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(0L);

        when(userRepository.getReferenceById(10))
                .thenReturn(user);

        when(reactionRepository.save(any(Reaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(reactionRepository.countLiked(1))
                .thenReturn(1L);

        CommentReactionResponseDTO result = reactionService.addHelpfulReactionToPost(1, 10);

        assertNotNull(result);

        assertEquals(
                "Reaction added",
                result.getMessage());

        assertEquals(
                1,
                result.getCommentId());

        assertEquals(
                "like",
                result.getReactionType());

        assertEquals(
                1L,
                result.getDisLikeCount());

        verify(reactionRepository)
                .save(any(Reaction.class));

        verify(reactionRepository)
                .countByUserAndPost(10, 1);
        verify(reactionRepository)
                .countLiked(1);
    }

    @Test
    void addHelpfulReactionToPost_whenPostNotFound_throws404() {

        when(postsRepository.findById(1))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addHelpfulReactionToPost(1, 10));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void addHelpfulReactionToPost_whenAlreadyReacted_throws409() {

        Posts post = new Posts();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(1L);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.addHelpfulReactionToPost(1, 10));

        assertEquals(409, exception.getStatusCode().value());

        verify(reactionRepository, never()).save(any());
    }

    // =========================================================
    // REMOVE HELPFUL / LIKE
    // =========================================================

    @Test
    void removeHelpfulReaction_whenFound_removesLike() {

        Reaction reaction = new Reaction();

        when(
                reactionRepository.findByUserAndPostAndType(
                        10,
                        1,
                        "like"))
                .thenReturn(Optional.of(reaction));

        // After removing the reaction, 3 likes remain
        when(reactionRepository.countLiked(1))
                .thenReturn(3L);

        ReactionRemovedResponseDTO result = reactionService.removeHelpfulReaction(1, 10);

        assertNotNull(result);

        assertEquals(
                "Reaction removed",
                result.getMessage());

        assertEquals(
                1,
                result.getPostId());

        assertEquals(
                3L,
                result.getDislikedCount());

        verify(reactionRepository)
                .delete(reaction);

        verify(reactionRepository)
                .countLiked(1);
    }

    @Test
    void removeHelpfulReaction_whenNotFound_throws404() {

        when(reactionRepository.findByUserAndPostAndType(
                10, 1, "like"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reactionService.removeHelpfulReaction(1, 10));

        assertEquals(404, exception.getStatusCode().value());

        verify(reactionRepository, never()).delete(any());
    }

    @Test
    void addHelpfulReactionToPost_whenMultipleLikesExist_returnsCorrectCount() {

        Posts post = new Posts();

        User user = new User();

        when(postsRepository.findById(1))
                .thenReturn(Optional.of(post));

        // User has not reacted yet
        when(reactionRepository.countByUserAndPost(10, 1))
                .thenReturn(0L);

        when(userRepository.getReferenceById(10))
                .thenReturn(user);

        when(reactionRepository.save(any(Reaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // There are already 4 likes, including the newly added one
        when(reactionRepository.countLiked(1))
                .thenReturn(4L);

        CommentReactionResponseDTO result = reactionService.addHelpfulReactionToPost(1, 10);

        assertNotNull(result);

        assertEquals(
                4L,
                result.getDisLikeCount());

        verify(reactionRepository)
                .countLiked(1);
    }
}
