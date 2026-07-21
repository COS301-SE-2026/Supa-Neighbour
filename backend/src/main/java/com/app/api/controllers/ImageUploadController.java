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
 * REST controller responsible for handling image upload requests.
 * <p>
 * This controller accepts image files from clients, uploads them to Azure
 * Blob Storage using the {@link BlobStorageService}, and returns the
 * publicly accessible URL of the uploaded image.
 * </p>
 */
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    
    /**
     * Service used to upload images to Azure Blob Storage.
     */
    private final BlobStorageService blobStorageService;
    /**
     * Creates a reaction response.
     *
     * @param blobstorageservice has the storage information 
     */
    /**
     * Constructs a new {@code ImageUploadController}.
     *
     * @param blobStorageService the service responsible for uploading images
     *                           to Azure Blob Storage
     */
    public ImageUploadController(BlobStorageService blobStorageService){
        this.blobStorageService = blobStorageService;
    }


    /**
     * Uploads an image file to Azure Blob Storage.
     * <p>
     * The uploaded file is validated by the service before being stored.
     * On success, the endpoint returns the URL of the uploaded image.
     * </p>
     *
     * @param file the image file to upload
     * @return a {@link ResponseEntity} containing:
     * <ul>
     *   <li><b>201 Created</b> with the uploaded image URL</li>
     *   <li><b>400 Bad Request</b> if the uploaded file is invalid</li>
     *   <li><b>500 Internal Server Error</b> if an unexpected error occurs during upload</li>
     * </ul>
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
