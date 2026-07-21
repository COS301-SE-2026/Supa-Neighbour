package com.app.api.services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Adds a new comment to the specified post.
 *
 * @param postId the ID of the post
 * @param request the request containing the comment content
 * @param authenticatedUserId the authenticated user's ID
 * @return the newly created comment
 */
@Service
public class BlobStorageService {
    
    private final BlobContainerClient postsContainerClient;

    public BlobStorageService(@Qualifier("postsContainerClient") BlobContainerClient postsContainerClient){
        this.postsContainerClient = postsContainerClient;
    }

    public String uploadPostImage(MultipartFile file) throws IOException{
        validateImage(file);

        String extension = getExtension(file.getOriginalFilename());
        String blobName = UUID.randomUUID() + extension;

        BlobClient blobClient = postsContainerClient.getBlobClient(blobName);
        try(InputStream dataStream = file.getInputStream()){
            blobClient.upload(dataStream, file.getSize(), true);
        }

        return blobClient.getBlobUrl();
    }

    private void validateImage(MultipartFile file){
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if(contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))){
            throw new IllegalArgumentException("File must be a jpg or png");
        }

        if(file.getSize() > 5 * 1024 * 1024){
            throw new IllegalArgumentException("File must be under 5MB");
        }
    }

    private String getExtension(String filename){
        if(filename == null || !filename.contains(".")){
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }
}
