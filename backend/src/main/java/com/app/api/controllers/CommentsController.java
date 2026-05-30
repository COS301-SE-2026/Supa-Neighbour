package com.app.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Comments;
import com.app.api.services.CommentsService;

/**
 * Address controller.
 */
@RestController
@RequestMapping("api/comments")
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /**
     * Get all addresses.
     * @return addresses
     */
    @GetMapping
    public List<Comments> getAllComments() {
        return commentsService.getAllComments();
    }

    /**
     * Get address by id.
     * @param id address id
     * @return address
     */
    @GetMapping("api/comments/{id}")
    public Comments getCommentsById(@PathVariable int id) {
        return commentsService.getCommentsById(id);
    }

    /**
     * Create address.
     * @param comments address
     * @return saved address
     */
    @PostMapping
    public Comments createComments(@RequestBody Comments comments) {
        return commentsService.saveComments(comments);
    }
}