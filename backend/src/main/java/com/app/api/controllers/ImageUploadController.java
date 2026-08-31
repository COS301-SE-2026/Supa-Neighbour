package com.app.api.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.api.services.BlobStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Image Upload", description = "Endpoints for uploading images to Azure Blob Storage")
public class ImageUploadController {
    
    /**
     * Service used to upload images to Azure Blob Storage.
     */
    private final BlobStorageService blobStorageService;

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
    @PostMapping("/post/image")
    @Operation(
        summary = "Upload post image",
        description = "Uploads an image file associated with a post to Azure Blob Storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Image uploaded successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"imageUrl\": \"https://storageaccount.blob.core.windows.net/container/posts/image-123456.jpg\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"File must be an image (JPEG, PNG, or GIF)\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected error during upload",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"An unexpected error occurred. Please try again.\"}"
                )
            )
        )
    })
    public ResponseEntity<?> uploadImage(
        @Parameter(description = "The image file to upload (JPEG, PNG, or GIF)", required = true)
        @RequestParam("file") MultipartFile file
    ){
        try{
            String imageUrl = blobStorageService.uploadPostImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));

        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch(IOException e){
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }

    /**
     * Uploads an image file associated with a task to Azure Blob Storage.
     *
     * @param file the image file to upload
     * @return a {@link ResponseEntity} containing:
     * <ul>
     *   <li><b>201 Created</b> with the uploaded image URL</li>
     *   <li><b>400 Bad Request</b> if the uploaded file is invalid</li>
     *   <li><b>500 Internal Server Error</b> if an unexpected error occurs during upload</li>
     * </ul>
     */
    @PostMapping("/task/image")
    @Operation(
        summary = "Upload task image",
        description = "Uploads an image file associated with a task to Azure Blob Storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Image uploaded successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"imageUrl\": \"https://storageaccount.blob.core.windows.net/container/tasks/image-123456.jpg\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"File must be an image (JPEG, PNG, or GIF)\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected error during upload",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"An unexpected error occurred. Please try again.\"}"
                )
            )
        )
    })
    public ResponseEntity<?> uploadTaskImage(
        @Parameter(description = "The image file to upload (JPEG, PNG, or GIF)", required = true)
        @RequestParam("file") MultipartFile file
    ){
        try{
            String imageUrl = blobStorageService.uploadTaskImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch(IOException e){
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occured. Please try again"));
        }
    }

    /**
     * Uploads an image file associated with a chat message to Azure Blob Storage.
     *
     * @param file the image file to upload
     * @return a {@link ResponseEntity} containing:
     * <ul>
     *   <li><b>201 Created</b> with the uploaded image URL</li>
     *   <li><b>400 Bad Request</b> if the uploaded file is invalid</li>
     *   <li><b>500 Internal Server Error</b> if an unexpected error occurs during upload</li>
     * </ul>
     */
    @PostMapping("/chat/image")
    @Operation(
        summary = "Upload chat image",
        description = "Uploads an image file associated with a chat message to Azure Blob Storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Image uploaded successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"imageUrl\": \"https://storageaccount.blob.core.windows.net/container/chats/image-123456.jpg\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid image file",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"File must be an image (JPEG, PNG, or GIF)\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Unexpected error during upload",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"An unexpected error occurred. Please try again.\"}"
                )
            )
        )
    })
    public ResponseEntity<?> uploadChatImage(
        @Parameter(description = "The image file to upload (JPEG, PNG, or GIF)", required = true)
        @RequestParam("file") MultipartFile file
    ){
        try{
            String imageUrl = blobStorageService.uploadChatImage(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("imageUrl", imageUrl));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }catch(IOException e){
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occured. Please try again"));
        }
    }
}
