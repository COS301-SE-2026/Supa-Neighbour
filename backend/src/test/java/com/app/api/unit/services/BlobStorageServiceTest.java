package com.app.api.unit.services;

import com.app.api.services.BlobStorageService;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
 
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
 
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

@ExtendWith(MockitoExtension.class)
public class BlobStorageServiceTest {
        @Mock
        private BlobContainerClient postsContainerClient;

        @Mock
        private BlobContainerClient taskImagesContainerClient;

        @Mock
        private BlobContainerClient chatImagesContainerClient;

        @Mock
        private BlobContainerClient profilesContainerClient;

        @Mock
        private BlobClient blobClient;

        @Mock
        private MultipartFile multipartFile;

        @Mock
        private BlobStorageService service;

        @BeforeEach
        void setUp() {
            service = new BlobStorageService(postsContainerClient, taskImagesContainerClient, chatImagesContainerClient, profilesContainerClient);
        }

    private void stubValidJpegFile(long size) throws IOException {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(size);
        when(multipartFile.getOriginalFilename()).thenReturn("photo.jpg");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
    }

    @Nested
    class ValidateTests {
        @Test
        void uploadPostImage_throws_whenFileIsNull() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(null));
            assertEquals("File is required", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenFileIsEmpty() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(true);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("File is required", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenContentTypeIsNull() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn(null);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("File must be a jpg or png", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenFileIsUnsupported() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn("application/pdf");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("File must be a jpg or png", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenFileExceedsSizeLimit() throws IOException {
                        when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn("image/png");
            when(multipartFile.getSize()).thenReturn(6L * 1024 * 1024);
 
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.uploadPostImage(multipartFile));
            assertEquals("File must be under 5MB", ex.getMessage());
        }

        @Test
        void uploadPostImage_allows_pngContentType() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn("image/png");
            when(multipartFile.getSize()).thenReturn(100L);
            when(multipartFile.getOriginalFilename()).thenReturn("photo.png");
            when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://example.com/blob.png");

            String url = service.uploadPostImage(multipartFile);
            assertEquals("https://example.com/blob.png", url);
        }
    }

    @Nested
    class UploadImageTests {

        @Test
        void uploadsToPostContainer_andReturnsBlobUrl() throws IOException {
            stubValidJpegFile(100L);
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://example.com/blob.jpg");

            String url = service.uploadPostImage(multipartFile);

            assertEquals("https://example.com/blob.jpg", url);
            verify(postsContainerClient).getBlobClient(anyString());
            verify(blobClient).upload(any(InputStream.class), eq(100L), eq(true));
        }

        @Test
        void generatedBlobName_preservesOriginalExtension() throws IOException {
            stubValidJpegFile(100L);
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://acct.blob.core.windows.net/posts/uuid.jpg");
 
            service.uploadPostImage(multipartFile);
 
            ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
            verify(postsContainerClient).getBlobClient(nameCaptor.capture());
            assertTrue(nameCaptor.getValue().endsWith(".jpg"));
        }

        @Test
        void propagatesIOException_fromMultipartFileInputStream() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn("image/jpeg");
            when(multipartFile.getSize()).thenReturn(100L);
            when(multipartFile.getOriginalFilename()).thenReturn("photo.jpg");
            when(multipartFile.getInputStream()).thenThrow(new IOException("Simulated IO error"));
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);

            assertThrows(IOException.class, () -> service.uploadPostImage(multipartFile));
        }

        @Test
        void doesNotTouchOtherContainers() throws IOException {
            stubValidJpegFile(100L);
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://example.com/blob.jpg");

            service.uploadPostImage(multipartFile);

            verify(taskImagesContainerClient, never()).getBlobClient(anyString());
            verify(chatImagesContainerClient, never()).getBlobClient(anyString());
            verify(profilesContainerClient, never()).getBlobClient(anyString());
        }
    }

    @Nested
    class DelegatingUploadTests {
        @Test
        void uploadTaskImages_usesTaskImagesContainer() throws IOException {
            stubValidJpegFile(100L);
            when(taskImagesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://example.com/task.jpg");

            String url = service.uploadTaskImage(multipartFile);

            assertEquals("https://example.com/task.jpg", url);
            verify(taskImagesContainerClient).getBlobClient(anyString());
            verify(postsContainerClient, never()).getBlobClient(anyString());
        }

    @Test
    void uploadChatImage_usesChatImagesContainer() throws IOException {
        stubValidJpegFile(100L);
        when(chatImagesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://example.com/chat.jpg");

        String url = service.uploadChatImage(multipartFile);

        assertEquals("https://example.com/chat.jpg", url);
        verify(chatImagesContainerClient).getBlobClient(anyString());
        verify(postsContainerClient, never()).getBlobClient(anyString());
        }

    @Test
    void uploadProfileImage_usesProfilesContainer() throws IOException {
        stubValidJpegFile(100L);
        when(profilesContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://example.com/profile.jpg");

        String url = service.uploadProfileImage(multipartFile);

        assertEquals("https://example.com/profile.jpg", url);
        verify(profilesContainerClient).getBlobClient(anyString());
        verify(postsContainerClient, never()).getBlobClient(anyString());
        }

    @Test
    void uploadImage_withNoExtensionInFilename_producesBlobNameWithoutExtension() throws IOException {
    
    
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getContentType()).thenReturn("image/jpeg");
        when(multipartFile.getSize()).thenReturn(100L);
        when(multipartFile.getOriginalFilename()).thenReturn("photo-no-exception"); // No extension
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
        when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(blobClient.getBlobUrl()).thenReturn("https://example.com/blob");

        String url = service.uploadPostImage(multipartFile);

        assertEquals("https://example.com/blob", url);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        verify(postsContainerClient).getBlobClient(nameCaptor.capture());
        assertTrue(!nameCaptor.getValue().contains("."), "Blob name should not contain a dot when original filename has no extension");
        }
    }

    @Nested
    class SasUrlTests {
        @Test
        void generateSasUrl_returnsUrlWithSasTokenAppended() {
            String blobUrl = "https://acct.blob.core.windows.net/posts/uuid.png";
            when(postsContainerClient.getBlobContainerName()).thenReturn("posts");
            when(postsContainerClient.getBlobClient("uuid.png")).thenReturn(blobClient);
            when(blobClient.generateSas(any(BlobServiceSasSignatureValues.class))).thenReturn("sv=2020&sig=abc123");
            when(blobClient.getBlobUrl()).thenReturn(blobUrl);
 
            String result = service.generateSasUrl(blobUrl, postsContainerClient);
 
            assertEquals(blobUrl + "?sv=2020&sig=abc123", result);
        }

        @Test
        void generateSasUrl_throws_onMalformedUrl() {
            String malformedUrl = "https://acct.blob.core.windows.net/posts/{uuid}.png"; // unescaped braces -> URISyntaxException

            assertThrows(IllegalArgumentException.class,
            () -> service.generateSasUrl(malformedUrl, postsContainerClient));
        }

        @Test
        void defaultGenerateSasUrl_delegatwsToPostContainer() {
            String blobUrl = "https://acct.blob.core.windows.net/posts/uuid.png";
            when(postsContainerClient.getBlobContainerName()).thenReturn("posts");
            when(postsContainerClient.getBlobClient("uuid.png")).thenReturn(blobClient);
            when(blobClient.generateSas(any(BlobServiceSasSignatureValues.class))).thenReturn("token");
            when(blobClient.getBlobUrl()).thenReturn(blobUrl);

            service.generateSasUrl(blobUrl);

            verify(postsContainerClient).getBlobClient("uuid.png");
        }

        @Test
        void generateChatSasUrl_delegatesToChatImagesContainer() {
            String blobUrl = "https://acct.blob.core.windows.net/chat-images/uuid.png";
            when(chatImagesContainerClient.getBlobContainerName()).thenReturn("chat-images");
            when(chatImagesContainerClient.getBlobClient("uuid.png")).thenReturn(blobClient);
            when(blobClient.generateSas(any(BlobServiceSasSignatureValues.class))).thenReturn("token");
            when(blobClient.getBlobUrl()).thenReturn(blobUrl);

            service.generateChatSasUrl(blobUrl);

            verify(chatImagesContainerClient).getBlobClient("uuid.png");
            verify(postsContainerClient, never()).getBlobClient(anyString());
        }

        @Test
        void generateTaskSasUrl_delegatesToTaskImagesContainer() {
            String blobUrl = "https://acct.blob.core.windows.net/task-images/uuid.png";
            when(taskImagesContainerClient.getBlobContainerName()).thenReturn("task-images");
            when(taskImagesContainerClient.getBlobClient("uuid.png")).thenReturn(blobClient);
            when(blobClient.generateSas(any(BlobServiceSasSignatureValues.class))).thenReturn("token");
            when(blobClient.getBlobUrl()).thenReturn(blobUrl);

            service.generateTaskSasUrl(blobUrl);

            verify(taskImagesContainerClient).getBlobClient("uuid.png");
            verify(postsContainerClient, never()).getBlobClient(anyString());
        }

        @Test
        void extractBlobName_returnsNameRelativeToContainer() {
            String blobUrl = "https://acct.blob.core.windows.net/posts/uuid.png";
            String containerName = "posts";

            String result = service.extractBlobName(blobUrl, containerName);

            assertEquals("uuid.png", result);
        }

        @Test
        void extractBlobName_throws_onMalformedUrl() {
            String malformedUrl = "https://acct.blob.core.windows.net/posts/{uuid}.png";

            assertThrows(IllegalArgumentException.class,
            () -> service.extractBlobName(malformedUrl, "posts"));
        }
    }
}

