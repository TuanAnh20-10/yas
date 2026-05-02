package com.yas.media.controller;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MediaControllerTest {
    @Mock
    private MediaService mediaService;
    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_shouldReturnNoFileMediaVm() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{});
        MediaPostVm postVm = new MediaPostVm("caption", file, "fileName");
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("fileName");
        media.setMediaType("image/png");
        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);
        ResponseEntity<Object> response = mediaController.create(postVm);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody() instanceof NoFileMediaVm);
    }

    @Test
    void delete_shouldReturnNoContent() {
        ResponseEntity<Void> response = mediaController.delete(1L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void get_shouldReturnMediaVm() {
        MediaVm mediaVm = new MediaVm(1L, "caption", "fileName", "image/png", "url");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);
        ResponseEntity<MediaVm> response = mediaController.get(1L);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    void get_shouldReturnNotFound() {
        when(mediaService.getMediaById(1L)).thenReturn(null);
        ResponseEntity<MediaVm> response = mediaController.get(1L);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getByIds_shouldReturnList() {
        List<MediaVm> list = List.of(new MediaVm(1L, "caption", "fileName", "image/png", "url"));
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(list);
        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));
        assertEquals(200, response.getStatusCode().value());
        assertEquals(list, response.getBody());
    }

    @Test
    void getByIds_shouldReturnNotFound() {
        when(mediaService.getMediaByIds(List.of(1L))).thenReturn(List.of());
        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(List.of(1L));
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getFile_shouldReturnInputStreamResource() {
        MediaDto dto = MediaDto.builder()
                .mediaType(MediaType.IMAGE_PNG)
                .content(new ByteArrayInputStream(new byte[]{}))
                .build();
        when(mediaService.getFile(1L, "fileName")).thenReturn(dto);
        var response = mediaController.getFile(1L, "fileName");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }
}
