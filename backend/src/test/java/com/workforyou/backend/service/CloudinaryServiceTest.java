package com.workforyou.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CloudinaryServiceTest {

    @InjectMocks
    private CloudinaryService cloudinaryService;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    public void testaUploadImagem() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "teste.png",
                "image/png",
                "conteudo".getBytes()
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("secure_url", "https://cloudinary.com/fakeimage.png");

        when(uploader.upload(file.getBytes(), ObjectUtils.emptyMap())).thenReturn(mockResult);

        String url = cloudinaryService.uploadImagem(file);

        assertEquals("https://cloudinary.com/fakeimage.png", url);
        verify(uploader, times(1)).upload(file.getBytes(), ObjectUtils.emptyMap());
    }

    @Test
    public void testaUploadImageNegativo() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "teste.png",
                "image/png",
                "conteudo".getBytes()
        );

        when(uploader.upload(file.getBytes(), ObjectUtils.emptyMap()))
                .thenThrow(new IOException("Erro de rede"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cloudinaryService.uploadImagem(file);
        });

        assertTrue(exception.getMessage().contains("Erro ao fazer upload para o Cloudinary"));
        verify(uploader, times(1)).upload(file.getBytes(), ObjectUtils.emptyMap());
    }

}
