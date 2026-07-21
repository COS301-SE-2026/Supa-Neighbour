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
 * This class creates and configures the Azure Blob Storage clients used by the
 * application. Separate {@link BlobContainerClient} beans are provided for the
 * posts and profile images containers.
 * </p>
 */
@Configuration
public class BlobStorageConfig {
     /**
     * Azure Blob Storage connection string.
     */
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    /**
     * Name of the Azure Blob Storage container used for post images.
     */
    @Value("${azure.storage.posts-container}")
    private String postsContainer;

    /**
     * Name of the Azure Blob Storage container used for profile images.
     */
    @Value("${azure.storage.profiles-container}")
    private String profilesContainer;

    /**
     * Creates an Azure {@link BlobServiceClient} using the configured
     * connection string.
     *
     * @return a configured {@link BlobServiceClient}
     */
    private BlobServiceClient serviceClient(){
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    /**
     * Creates a {@link BlobContainerClient} for the posts container.
     *
     * @return the Azure Blob Storage client for post images
     */
    @Bean(name = "postsContainerClient")
    public BlobContainerClient postsContainerClient() {
        return serviceClient().getBlobContainerClient(postsContainer);
    }

    /**
     * Creates a {@link BlobContainerClient} for the profiles container.
     *
     * @return the Azure Blob Storage client for profile images
     */
    @Bean(name = "profilesContainerClient")
    public BlobContainerClient profilesContainerClient() {
        return serviceClient().getBlobContainerClient(profilesContainer);
    }
}
