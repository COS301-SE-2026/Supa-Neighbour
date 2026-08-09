package com.app.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

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
    private final BlobContainerClient taskImagesContainerClient;
    private final BlobContainerClient chatImagesContainerClient;
    private final BlobContainerClient profilesContainerClient;

    /**
     * Constructs a new {@code BlobStorageService}.
     *
     * @param postsContainerClient the Azure Blob Storage container client used
     *                             for storing post images
     */
    public BlobStorageService(
            @Qualifier("postsContainerClient") BlobContainerClient postsContainerClient,
            @Qualifier("taskImagesContainerClient") BlobContainerClient taskImagesContainerClient,
            @Qualifier("chatImagesContainerClient") BlobContainerClient chatImagesContainerClient,
            @Qualifier("profileContainerClient") BlobContainerClient profilesContainerClient
        ){
        this.postsContainerClient = postsContainerClient;
        this.taskImagesContainerClient = taskImagesContainerClient;
        this.chatImagesContainerClient = chatImagesContainerClient;
        this.profilesContainerClient = profilesContainerClient;
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
        try (InputStream dataStream = file.getInputStream()) {
            blobClient.upload(dataStream, file.getSize(), true);
        }

        return blobClient.getBlobUrl();
    }

    public String uploadTaskImage(MultipartFile file) throws IOException{
        return uploadImage(file, taskImagesContainerClient);
    }

    public String uploadChatImage(MultipartFile file) throws IOException{
        return uploadImage(file, chatImagesContainerClient);
    }

    public String uploadProfileImage(MultipartFile file) throws IOException{
        return uploadImage(file, profilesContainerClient);
    }

    public String uploadImage(MultipartFile file, BlobContainerClient containerClient) throws IOException{
        validateImage(file);

        String extension = getExtension(file.getOriginalFilename());
        String blobName = UUID.randomUUID() + extension;

        BlobClient blobClient = postsContainerClient.getBlobClient(blobName);
        try (InputStream dataStream = file.getInputStream()) {
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
    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
            throw new IllegalArgumentException("File must be a jpg or png");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
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
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."));
    }


    /**
     * Generates a time-limited Shared Access Signature (SAS) URL for a blob.
     * <p>
     * The supplied blob URL is converted into its corresponding blob name, and a
     * read-only SAS token is generated that allows temporary access to the blob.
     * The returned URL can be safely sent to clients for accessing private blobs
     * without exposing the storage account credentials.
     * </p>
     *
     * @param blobUrl the original blob URL stored in the database
     * @return a blob URL with an attached read-only SAS token that expires after
     *         four hours
     * @throws IllegalArgumentException if the provided blob URL is invalid
     */
    public String generateSasUrl(String blobUrl, BlobContainerClient containerClient){
        String blobName = extractBlobName(blobUrl, containerClient.getBlobContainerName());
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(4);

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiry, permission);

        String sasToken = blobClient.generateSas(sasValues);

        return blobClient.getBlobUrl() + "?" + sasToken;
    }


    /**
     * Extracts the blob name from a full Azure Blob Storage URL.
     * <p>
     * For example, given the URL:
     * </p>
     * <pre>
     * https://&lt;storage-account&gt;.blob.core.windows.net/posts/image.png
     * </pre>
     * <p>
     * this method returns:
     * </p>
     * <pre>
     * image.png
     * </pre>
     *
     * @param blobUrl the full Azure Blob Storage URL
     * @return the blob name relative to the container
     * @throws IllegalArgumentException if the supplied URL is malformed
     */
    public String extractBlobName(String blobUrl, String containerName){
        try{
            URI uri = new URI(blobUrl);
            String path = uri.getPath();

            return path.substring(("/"+ containerName + "/").length());
        }catch(URISyntaxException e){
            throw new IllegalArgumentException("Invalid blob URL", e);
        }
    }

    /**
     * Generates a SAS URL for a blob in the posts container.
     * Convenience overload for callers that only ever deal with post images.
     */
    public String generateSasUrl(String blobUrl){
        return generateSasUrl(blobUrl, postsContainerClient);
    }
}
