package com.app.api.unit.controllers;
import com.app.api.security.FirebaseAuthenticationFilter; // ASSUMPTION: same package guess as prior tests — adjust if wrong
import com.app.api.services.BlobStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.app.api.controllers.ImageUploadController;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = ImageUploadController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = FirebaseAuthenticationFilter.class
    )
)
@AutoConfigureMockMvc(addFilters = false)
public class ImageUploadControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlobStorageService blobStorageService;

    private static final String IMAGE_URL = "https://parseandcoblob.blob.core.windows.net/posts/abc123.png";

    private MockMultipartFile validFile(){
        return new MockMultipartFile("file", 
            "photo.png",
            "image/png",
            "fake-image-bytes".getBytes());
    }

    @Test
    void uploadImage_success_returns201WithUrl() throws Exception{
        when(blobStorageService.uploadPostImage(any())).thenReturn(IMAGE_URL);

        mockMvc.perform(multipart("/api/upload/post/image").file(validFile()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));

        verify(blobStorageService).uploadPostImage(any());
    }

    @Test
    void uploadImage_invalidFile_returns400WithErrorMesage() throws Exception{
        when(blobStorageService.uploadPostImage(any())).thenThrow(new IllegalArgumentException("File must be jpg or png"));

        mockMvc.perform(multipart("/api/upload/post/image").file(validFile()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("File must be jpg or png"));
    }

    @Test
    void uploadImage_ioExceptionDuringUpload_retruns500WithGenericMessage() throws Exception{
        when(blobStorageService.uploadPostImage(any())).thenThrow(
            new IOException("blob stprage unavaliable")
        );

        mockMvc.perform(multipart("/api/upload/post/image").file(validFile()))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("An unexpected error occurred. Please try again."));
    }

    @Test
    void uploadImage_missingFilePart_returns400() throws Exception{
        mockMvc.perform(multipart("/api/upload/post/image"))
        .andExpect(status().isBadRequest());

        verifyNoInteractions(blobStorageService);
    }

    @Test
    void uploadTaskImage_success_returns201WithUrl() throws Exception {
        when(blobStorageService.uploadTaskImage(any())).thenReturn(IMAGE_URL);

        mockMvc.perform(multipart("/api/upload/task/image").file(validFile()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));

        verify(blobStorageService).uploadTaskImage(any());
    }

    @Test
    void uploadTaskImage_invalidFile_returns400() throws Exception{
        when(blobStorageService.uploadTaskImage(any()))
        .thenThrow(new IllegalArgumentException("File must be under 5MB"));

        mockMvc.perform(multipart("/api/upload/task/image").file(validFile()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("File must be under 5MB"));
    }

    @Test
    void uploadTaskImage_ioException_returns500() throws Exception{
        when(blobStorageService.uploadTaskImage(any())).thenThrow(new IOException("network error"));

        mockMvc.perform(multipart("/api/upload/task/image").file(validFile()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occured. Please try again"));
    }

    @Test
    void uploadChatImage_success_returns201WithUrl() throws Exception {
        when(blobStorageService.uploadChatImage(any())).thenReturn(IMAGE_URL);

        mockMvc.perform(multipart("/api/upload/chat/image").file(validFile()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").value(IMAGE_URL));

        verify(blobStorageService).uploadChatImage(any());
    }

    @Test
    void uploadChatImage_invalidFile_returns400() throws Exception {
        when(blobStorageService.uploadChatImage(any()))
                .thenThrow(new IllegalArgumentException("File is required"));

        mockMvc.perform(multipart("/api/upload/chat/image").file(validFile()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("File is required"));
    }

    @Test
    void uploadChatImage_ioException_returns500() throws Exception {
        when(blobStorageService.uploadChatImage(any()))
                .thenThrow(new IOException("upload failed"));

        mockMvc.perform(multipart("/api/upload/chat/image").file(validFile()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An unexpected error occured. Please try again"));
    }
}
