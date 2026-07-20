package com.app.api.controllers;


import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.api.services.BlobStorageService;

import com.app.api.models.Posts;
import com.app.api.services.PostsService;
import com.google.rpc.context.AttributeContext.Response;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    
    private final BlobStorageService blobStorageService;

    public ImageUploadController(BlobStorageService blobStorageService){
        this.blobStorageService = blobStorageService;
    }


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
