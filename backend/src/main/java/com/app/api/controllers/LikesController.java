package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Likes;
import com.app.api.services.LikesService;

@RestController
@RequestMapping("/api/likes")
public class LikesController {

    @Autowired
    private LikesService likesService;

    // GET /api/likes
    @GetMapping
    public ResponseEntity<List<Likes>> getAllLikes() {
        return ResponseEntity.ok(likesService.getAllLikes());
    }

    // GET /api/likes/1
    @GetMapping("/{id}")
    public ResponseEntity<Likes> getLikesById(@PathVariable int id) {
        Likes likes = likesService.getLikeById(id);
        if (likes == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(likes);
    }

    // POST /api/likes
    @PostMapping
    public ResponseEntity<Likes> createLikes(@RequestBody Likes likes) {
        Likes saved = likesService.saveLike(likes);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/likes/1
    @PutMapping("/{id}")
    public ResponseEntity<Likes> updateLikes(@PathVariable int id, @RequestBody Likes likes) {
        Likes existing = likesService.getLikeById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        Likes updated = likesService.updateLike(id, likes);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/likes/1
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