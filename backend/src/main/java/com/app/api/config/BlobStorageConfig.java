package com.app.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BlobStorageConfig {
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.posts-container}")
    private String postsContainer;

    @Value("${azure.storage.profiles-container}")
    private String profilesContainer;

    private BlobServiceClient serviceClient(){
        return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    }

    @Bean(name = "postsContainerClient")
    public BlobContainerClient postsContainerClient(){
        return serviceClient().getBlobContainerClient(postsContainer);
    }

    @Bean(name = "profilesContainerClient")
    public BlobContainerClient profilesContainerClient(){
        return serviceClient().getBlobContainerClient(profilesContainer);
    }
}
