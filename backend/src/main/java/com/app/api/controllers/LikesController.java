package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.api.models.Likes;
import com.app.api.services.LikesService;

/**
 * REST controller for Likes.
 */
@RestController
@RequestMapping("/api/likes")
public class LikesController {

    @Autowired
    private LikesService likesService;

    /**
     * Retrieves all likes.
     *
     * @return a list of all likes
     */
    @GetMapping
    public ResponseEntity<List<Likes>> getAllLikes() {
        return ResponseEntity.ok(likesService.getAllLikes());
    }

    /**
     * Retrieves a like by its ID.
     *
     * @param id the like ID
     * @return the likes if found, otherwise 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Likes> getLikesById(@PathVariable int id) {
        Likes likes = likesService.getLikeById(id);
        if (likes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(likes);
    }

    /**
     * Creates a new like.
     *
     * @param likes the comment to create
     * @return the created likes with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Likes> createLikes(@RequestBody Likes likes) {
        Likes saved = likesService.saveLike(likes);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Updates an existing likes.
     *
     * @param id the ID of the likes to update
     * @param likes the updated likes data
     * @return the updated likes if found, otherwise 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Likes> updateLikes(@PathVariable int id, @RequestBody Likes likes) {
        Likes existing = likesService.getLikeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Likes updated = likesService.updateLike(id, likes);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a like by its ID.
     *
     * @param id the ID of the likes to delete
     * @return 204 No Content if deleted, otherwise 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLikes(@PathVariable int id) {
        Likes existing = likesService.getLikeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        likesService.deleteLike(id);
        return ResponseEntity.noContent().build();
    }
}
