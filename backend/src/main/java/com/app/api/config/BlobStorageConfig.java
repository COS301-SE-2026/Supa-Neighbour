package com.app.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

/**
 * Configuration class for Azure Blob Storage.
 * <p>
 * Creates and exposes {@link BlobContainerClient} beans for the
 * application's Azure Blob Storage containers.
 * </p>
 */
@Configuration
public class BlobStorageConfig {

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.posts-container}")
    private String postsContainer;

    @Value("${azure.storage.profiles-container}")
    private String profilesContainer;

    /**
     * Creates an Azure Blob Service client using the configured
     * connection string.
     *
     * @return a configured {@link BlobServiceClient}
     */
    private BlobServiceClient serviceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    /**
     * Creates a {@link BlobContainerClient} for the posts container.
     *
     * @return the BlobContainerClient used to store post images
     */
    @Bean(name = "postsContainerClient")
    public BlobContainerClient postsContainerClient() {
        return serviceClient().getBlobContainerClient(postsContainer);
    }

    /**
     * Creates a {@link BlobContainerClient} for the profiles container.
     *
     * @return the BlobContainerClient used to store profile images
     */
    @Bean(name = "profilesContainerClient")
    public BlobContainerClient profilesContainerClient() {
        return serviceClient().getBlobContainerClient(profilesContainer);
    }
}
