package com.app.api.unit.services;

import com.app.api.services.BlobStorageService;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
            assertEquals("Uploaded file is null", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenFileIsEmpty() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(true);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("Uploaded file is empty", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenContentTypeIsNull() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn(null);
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("Uploaded file has no content type", exception.getMessage());
        }

        @Test
        void uploadPostImage_throws_whenFileIsUnsupported() throws IOException {
            when(multipartFile.isEmpty()).thenReturn(false);
            when(multipartFile.getContentType()).thenReturn("application/pdf");
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.uploadPostImage(multipartFile));
            assertEquals("Uploaded file is not a supported image type", exception.getMessage());
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
            when(multipartFile.getSize()).thenReturn(1024L);
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
            stubValidJpegFile(1024L);
            when(postsContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.getBlobUrl()).thenReturn("https://example.com/blob.jpg");

            String url = service.uploadPostImage(multipartFile);

            assertEquals("https://example.com/blob.jpg", url);
            verify(postsContainerClient).getBlobClient(anyString());
            verify(blobClient).upload(any(InputStream.class), eq(1024L), eq(true));
        }
    }
}
