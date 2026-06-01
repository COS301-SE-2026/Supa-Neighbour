package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Comments;
import com.app.api.services.CommentsService;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    // GET /api/comments
    @GetMapping
    public ResponseEntity<List<Comments>> getAllComments() {
        return ResponseEntity.ok(commentsService.getAllComments());
    }

    // GET /api/comments/1
    @GetMapping("/{id}")
    public ResponseEntity<Comments> getCommentsById(@PathVariable int id) {
        Comments comments = commentsService.getCommentsById(id);
        if (comments == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comments);
    }

    // POST /api/comments
    @PostMapping
    public ResponseEntity<Comments> createComments(@RequestBody Comments comments) {
        Comments saved = commentsService.saveComments(comments);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/comments/1
    @PutMapping("/{id}")
    public ResponseEntity<Comments> updateComments(@PathVariable int id, @RequestBody Comments comments) {
        Comments existing = commentsService.getCommentsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Comments updated = commentsService.updateComments(id, comments);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/comments/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComments(@PathVariable int id) {
        Comments existing = commentsService.getCommentsById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        commentsService.deleteComments(id);
        return ResponseEntity.noContent().build();
    }
}