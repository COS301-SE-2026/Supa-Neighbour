package com.app.api.unit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.app.api.config.BlobStorageConfig;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;


@ExtendWith(MockitoExtension.class)
class BlobStorageConfigTest {

    private static final String FAKE_CONNECTION_STRING =
            "DefaultEndpointsProtocol=https;AccountName=test;"
            + "AccountKey=dGVzdA==;EndpointSuffix=core.windows.net";

    private static final String POSTS_CONTAINER = "posts";
    private static final String PROFILES_CONTAINER = "profiles";
    private static final String TASK_IMAGES_CONTAINER = "task-images";
    private static final String CHAT_IMAGES_CONTAINER = "chat-images";

    private BlobStorageConfig blobStorageConfig;

  
    @BeforeEach
    void setUp() {
        blobStorageConfig = new BlobStorageConfig();
        ReflectionTestUtils.setField(blobStorageConfig, "connectionString", FAKE_CONNECTION_STRING);
        ReflectionTestUtils.setField(blobStorageConfig, "postsContainer", POSTS_CONTAINER);
        ReflectionTestUtils.setField(blobStorageConfig, "profilesContainer", PROFILES_CONTAINER);
        ReflectionTestUtils.setField(blobStorageConfig, "taskImagesContainer", TASK_IMAGES_CONTAINER);
        ReflectionTestUtils.setField(blobStorageConfig, "chatImagesContainer", CHAT_IMAGES_CONTAINER);
    }


    @Test
    void postsContainerClient_returnsClientForPostsContainer() {
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        BlobContainerClient expectedClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(POSTS_CONTAINER)).thenReturn(expectedClient);

        try (MockedConstruction<BlobServiceClientBuilder> mockedBuilder =
                mockConstruction(BlobServiceClientBuilder.class, (mock, ctx) -> {
                    when(mock.connectionString(FAKE_CONNECTION_STRING)).thenReturn(mock);
                    when(mock.buildClient()).thenReturn(serviceClient);
                })) {

            BlobContainerClient result = blobStorageConfig.postsContainerClient();

            assertThat(result).isNotNull().isSameAs(expectedClient);
            verify(serviceClient).getBlobContainerClient(POSTS_CONTAINER);
        }
    }

    @Test
    void profilesContainerClient_returnsClientForProfilesContainer() {
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        BlobContainerClient expectedClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(PROFILES_CONTAINER)).thenReturn(expectedClient);

        try (MockedConstruction<BlobServiceClientBuilder> mockedBuilder =
                mockConstruction(BlobServiceClientBuilder.class, (mock, ctx) -> {
                    when(mock.connectionString(FAKE_CONNECTION_STRING)).thenReturn(mock);
                    when(mock.buildClient()).thenReturn(serviceClient);
                })) {

            BlobContainerClient result = blobStorageConfig.profilesContainerClient();

            assertThat(result).isNotNull().isSameAs(expectedClient);
            verify(serviceClient).getBlobContainerClient(PROFILES_CONTAINER);
        }
    }


    @Test
    void taskImagesContainerClient_returnsClientForTaskImagesContainer() {
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        BlobContainerClient expectedClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(TASK_IMAGES_CONTAINER)).thenReturn(expectedClient);

        try (MockedConstruction<BlobServiceClientBuilder> mockedBuilder =
                mockConstruction(BlobServiceClientBuilder.class, (mock, ctx) -> {
                    when(mock.connectionString(FAKE_CONNECTION_STRING)).thenReturn(mock);
                    when(mock.buildClient()).thenReturn(serviceClient);
                })) {

            BlobContainerClient result = blobStorageConfig.taskImagesContainerClient();

            assertThat(result).isNotNull().isSameAs(expectedClient);
            verify(serviceClient).getBlobContainerClient(TASK_IMAGES_CONTAINER);
        }
    }

    @Test
    void chatImagesContainerClient_returnsClientForChatImagesContainer() {
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        BlobContainerClient expectedClient = mock(BlobContainerClient.class);
        when(serviceClient.getBlobContainerClient(CHAT_IMAGES_CONTAINER)).thenReturn(expectedClient);

        try (MockedConstruction<BlobServiceClientBuilder> mockedBuilder =
                mockConstruction(BlobServiceClientBuilder.class, (mock, ctx) -> {
                    when(mock.connectionString(FAKE_CONNECTION_STRING)).thenReturn(mock);
                    when(mock.buildClient()).thenReturn(serviceClient);
                })) {

            BlobContainerClient result = blobStorageConfig.chatImagesContainerClient();

            assertThat(result).isNotNull().isSameAs(expectedClient);
            verify(serviceClient).getBlobContainerClient(CHAT_IMAGES_CONTAINER);
        }
    }


    @Test
    void postsContainerClient_passesConnectionStringToBuilder() {
        BlobServiceClient serviceClient = mock(BlobServiceClient.class);
        when(serviceClient.getBlobContainerClient(POSTS_CONTAINER))
                .thenReturn(mock(BlobContainerClient.class));

        try (MockedConstruction<BlobServiceClientBuilder> mockedBuilder =
                mockConstruction(BlobServiceClientBuilder.class, (mock, ctx) -> {
                    when(mock.connectionString(FAKE_CONNECTION_STRING)).thenReturn(mock);
                    when(mock.buildClient()).thenReturn(serviceClient);
                })) {

            blobStorageConfig.postsContainerClient();

            BlobServiceClientBuilder capturedBuilder = mockedBuilder.constructed().get(0);
            verify(capturedBuilder).connectionString(FAKE_CONNECTION_STRING);
            verify(capturedBuilder).buildClient();
        }
    }
}
