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
 * Service responsible for uploading image files to Azure Blob Storage.
 * <p>
 * This service validates uploaded images before storing them in the configured
 * Azure Blob Storage container. Each uploaded image is assigned a unique
 * filename to prevent naming conflicts.
 * </p>
 */
@Service
public class BlobStorageService {
    

    /**
     * Azure Blob Storage container used to store post images.
     */
    private final BlobContainerClient postsContainerClient;

     /**
     * Constructs a new {@code BlobStorageService}.
     *
     * @param postsContainerClient the Azure Blob Storage container client used
     *                             for storing post images
     */
    public BlobStorageService(@Qualifier("postsContainerClient") BlobContainerClient postsContainerClient){
        this.postsContainerClient = postsContainerClient;
    }

    /**
     * Uploads an image to Azure Blob Storage.
     * <p>
     * The image is validated before being uploaded. A unique filename is
     * generated using a random UUID while preserving the original file
     * extension.
     * </p>
     *
     * @param file the image file to upload
     * @return the public URL of the uploaded image
     * @throws IOException if an error occurs while reading or uploading the file
     * @throws IllegalArgumentException if the uploaded file is invalid
     */
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

    
    /**
     * Validates an uploaded image.
     * <p>
     * The image must:
     * <ul>
     *   <li>Be present and non-empty</li>
     *   <li>Be a JPEG or PNG image</li>
     *   <li>Be no larger than 5 MB</li>
     * </ul>
     * </p>
     *
     * @param file the uploaded image to validate
     * @throws IllegalArgumentException if the image fails validation
     */
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

    /**
     * Extracts the file extension from a filename.
     *
     * @param filename the original filename
     * @return the file extension, including the leading period (e.g. ".png"),
     *         or an empty string if no extension exists
     */
    private String getExtension(String filename){
        if(filename == null || !filename.contains(".")){
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }


}
