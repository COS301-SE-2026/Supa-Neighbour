package com.app.api.controllers;


import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.services.BlobStorageService;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/**
 * ImageUploadController
 */
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    
    private final BlobStorageService blobStorageService;

    public ImageUploadController(BlobStorageService blobStorageService){
        this.blobStorageService = blobStorageService;
    }

    //Post
    /**
    * Creates a new comment for a post.
    *posts a new image to the database 
    * @param file The comment request.
    * @return image and http status 201 created
    */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file){
        try{
            String imageUrl = blobStorageService.uploadPostImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));

        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch(IOException e){
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }
}
