package com.app.api.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.api.models.Likes;
import com.app.api.services.LikesService;

/**
 * Address controller.
 */
@RestController
@RequestMapping("api/likes")
public class LikesController {

    @Autowired
    private LikesService likesService;

    /**
     * Get all addresses.
     * @return addresses
     */
    @GetMapping
    public List<Likes> getAllLikes() {
        return likesService.getAllLikes();
    }

    /**
     * Get address by id.
     * @param id address id
     * @return address
     */
    @GetMapping("api/likes/{id}")
    public Likes getLikesById(@PathVariable int id) {
        return likesService.getLikesById(id);
    }

    /**
     * Create address.
     * @param likes address
     * @return saved address
     */
    @PostMapping
    public Likes createLikes(@RequestBody Likes likes) {
        return likesService.saveLikes(likes);
    }
}
