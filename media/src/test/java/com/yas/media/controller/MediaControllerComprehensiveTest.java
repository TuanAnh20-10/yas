package com.yas.media.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("MediaController Comprehensive Tests")
class MediaControllerComprehensiveTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController mediaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("POST /medias should return 200 OK with NoFileMediaVm")
    void create_withValidMediaPostVm_shouldReturn200() {
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile",
            "image.jpg",
            "image/jpeg",
            new byte[]{-119, 80, 78, 71}
        );
        MediaPostVm postVm = new MediaPostVm("Test Caption", file, "custom_name.jpg");

        Media savedMedia = new Media();
        savedMedia.setId(1L);
        savedMedia.setCaption("Test Caption");
        savedMedia.setFileName("custom_name.jpg");
        savedMedia.setMediaType("image/jpeg");

        org.mockito.Mockito.when(mediaService.saveMedia(org.mockito.ArgumentMatchers.any()))
            .thenReturn(savedMedia);

        ResponseEntity<Object> response = mediaController.create(postVm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof NoFileMediaVm);

        NoFileMediaVm responseBody = (NoFileMediaVm) response.getBody();
        assertEquals(1L, responseBody.id());
        assertEquals("Test Caption", responseBody.caption());
        assertEquals("custom_name.jpg", responseBody.fileName());
        assertEquals("image/jpeg", responseBody.mediaType());
    }

    @Test
    @DisplayName("POST /medias should include all media metadata in response")
    void create_shouldReturnCompleteMediaMetadata() {
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile",
            "photo.png",
            "image/png",
            new byte[]{-119, 80, 78, 71}
        );
        MediaPostVm postVm = new MediaPostVm("My Photo", file, null);

        Media savedMedia = new Media();
        savedMedia.setId(2L);
        savedMedia.setCaption("My Photo");
        savedMedia.setFileName("photo.png");
        savedMedia.setMediaType("image/png");
        savedMedia.setFilePath("/storage/photo.png");

        org.mockito.Mockito.when(mediaService.saveMedia(org.mockito.ArgumentMatchers.any()))
            .thenReturn(savedMedia);

        ResponseEntity<Object> response = mediaController.create(postVm);

        NoFileMediaVm body = (NoFileMediaVm) response.getBody();
        assertEquals(2L, body.id());
        assertEquals("My Photo", body.caption());
        assertEquals("photo.png", body.fileName());
        assertEquals("image/png", body.mediaType());
    }

    @Test
    @DisplayName("DELETE /medias/{id} should return 204 No Content")
    void delete_withValidId_shouldReturn204() {
        Long mediaId = 1L;
        org.mockito.Mockito.doNothing().when(mediaService).removeMedia(mediaId);

        ResponseEntity<Void> response = mediaController.delete(mediaId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        org.mockito.Mockito.verify(mediaService, org.mockito.Mockito.times(1))
            .removeMedia(mediaId);
    }

    @Test
    @DisplayName("DELETE /medias/{id} should call service with correct ID")
    void delete_shouldCallServiceWithCorrectId() {
        Long mediaId = 42L;
        org.mockito.Mockito.doNothing().when(mediaService).removeMedia(mediaId);

        mediaController.delete(mediaId);

        org.mockito.Mockito.verify(mediaService).removeMedia(42L);
    }

    @Test
    @DisplayName("GET /medias/{id} should return 200 OK with MediaVm")
    void get_withValidId_shouldReturn200WithMediaVm() {
        Long mediaId = 1L;
        MediaVm mediaVm = new MediaVm(
            1L,
            "Test Image",
            "image.jpg",
            "image/jpeg",
            "http://cdn.example.com/medias/1/file/image.jpg"
        );

        org.mockito.Mockito.when(mediaService.getMediaById(mediaId)).thenReturn(mediaVm);

        ResponseEntity<MediaVm> response = mediaController.get(mediaId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mediaVm, response.getBody());
    }

    @Test
    @DisplayName("GET /medias/{id} should return 404 Not Found when media doesn't exist")
    void get_withNonExistentId_shouldReturn404() {
        Long mediaId = 999L;
        org.mockito.Mockito.when(mediaService.getMediaById(mediaId)).thenReturn(null);

        ResponseEntity<MediaVm> response = mediaController.get(mediaId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /medias should return 200 OK with list of MediaVm")
    void getByIds_withValidIds_shouldReturn200WithList() {
        List<Long> ids = List.of(1L, 2L);
        List<MediaVm> mediaList = List.of(
            new MediaVm(1L, "Image 1", "img1.jpg", "image/jpeg", "http://cdn/1/img1.jpg"),
            new MediaVm(2L, "Image 2", "img2.png", "image/png", "http://cdn/2/img2.png")
        );

        org.mockito.Mockito.when(mediaService.getMediaByIds(ids)).thenReturn(mediaList);

        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(mediaList, response.getBody());
    }

    @Test
    @DisplayName("GET /medias should return 404 Not Found when no media found")
    void getByIds_withNoResults_shouldReturn404() {
        List<Long> ids = List.of(999L, 1000L);
        org.mockito.Mockito.when(mediaService.getMediaByIds(ids))
            .thenReturn(Collections.emptyList());

        
        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /medias should handle multiple IDs correctly")
    void getByIds_withMultipleIds_shouldReturnAllMatching() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<MediaVm> mediaList = List.of(
            new MediaVm(1L, "Media 1", "file1.jpg", "image/jpeg", "url1"),
            new MediaVm(2L, "Media 2", "file2.png", "image/png", "url2"),
            new MediaVm(3L, "Media 3", "file3.gif", "image/gif", "url3")
        );

        org.mockito.Mockito.when(mediaService.getMediaByIds(ids)).thenReturn(mediaList);

        
        ResponseEntity<List<MediaVm>> response = mediaController.getByIds(ids);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
    }

    @Test
    @DisplayName("GET /medias/{id}/file/{fileName} should return file with correct headers")
    void getFile_shouldReturnFileWithCorrectHeaders() {
        Long mediaId = 1L;
        String fileName = "image.jpg";
        byte[] fileContent = {-1, -40, -1, -32};
        InputStream inputStream = new ByteArrayInputStream(fileContent);

        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_JPEG)
            .content(inputStream)
            .build();

        org.mockito.Mockito.when(mediaService.getFile(mediaId, fileName))
            .thenReturn(mediaDto);

        
        ResponseEntity<InputStreamResource> response = mediaController.getFile(mediaId, fileName);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
            .contains("attachment"));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)
            .contains(fileName));
    }

    @Test
    @DisplayName("GET /medias/{id}/file/{fileName} should return PNG file correctly")
    void getFile_withPngFile_shouldReturnWithCorrectMediaType() {
        Long mediaId = 2L;
        String fileName = "image.png";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{});

        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_PNG)
            .content(inputStream)
            .build();

        org.mockito.Mockito.when(mediaService.getFile(mediaId, fileName))
            .thenReturn(mediaDto);

        
        ResponseEntity<InputStreamResource> response = mediaController.getFile(mediaId, fileName);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_PNG, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("GET /medias/{id}/file/{fileName} should return GIF file correctly")
    void getFile_withGifFile_shouldReturnWithCorrectMediaType() {
        Long mediaId = 3L;
        String fileName = "animation.gif";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{});

        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_GIF)
            .content(inputStream)
            .build();

        org.mockito.Mockito.when(mediaService.getFile(mediaId, fileName))
            .thenReturn(mediaDto);

        
        ResponseEntity<InputStreamResource> response = mediaController.getFile(mediaId, fileName);

        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.IMAGE_GIF, response.getHeaders().getContentType());
    }

    @Test
    @DisplayName("GET /medias/{id}/file/{fileName} should include filename in content disposition")
    void getFile_shouldIncludeFilenameInContentDisposition() {
        Long mediaId = 1L;
        String fileName = "test_image.jpg";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{});

        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_JPEG)
            .content(inputStream)
            .build();

        org.mockito.Mockito.when(mediaService.getFile(mediaId, fileName))
            .thenReturn(mediaDto);

        
        ResponseEntity<InputStreamResource> response = mediaController.getFile(mediaId, fileName);

        
        String contentDisposition = response.getHeaders()
            .getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(contentDisposition);
        assertTrue(contentDisposition.contains("attachment"));
        assertTrue(contentDisposition.contains(fileName));
    }

    @Test
    @DisplayName("POST /medias should delegate to service")
    void create_shouldDelegateToService() {
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile",
            "test.jpg",
            "image/jpeg",
            new byte[]{}
        );
        MediaPostVm postVm = new MediaPostVm("Caption", file, "name.jpg");

        Media savedMedia = new Media();
        savedMedia.setId(1L);
        org.mockito.Mockito.when(mediaService.saveMedia(postVm)).thenReturn(savedMedia);

        
        mediaController.create(postVm);

        
        org.mockito.Mockito.verify(mediaService).saveMedia(postVm);
    }

    @Test
    @DisplayName("GET /medias/{id} should call service with correct ID")
    void get_shouldCallServiceWithCorrectId() {
        Long mediaId = 123L;
        org.mockito.Mockito.when(mediaService.getMediaById(mediaId)).thenReturn(null);

        
        mediaController.get(mediaId);

        
        org.mockito.Mockito.verify(mediaService).getMediaById(123L);
    }

    @Test
    @DisplayName("GET /medias should call service with provided IDs")
    void getByIds_shouldCallServiceWithProvidedIds() {
        List<Long> ids = List.of(1L, 2L, 3L);
        org.mockito.Mockito.when(mediaService.getMediaByIds(ids))
            .thenReturn(Collections.emptyList());

        
        mediaController.getByIds(ids);

        
        org.mockito.Mockito.verify(mediaService).getMediaByIds(ids);
    }

    @Test
    @DisplayName("GET /medias/{id}/file/{fileName} should call service with correct parameters")
    void getFile_shouldCallServiceWithCorrectParameters() {
        Long mediaId = 1L;
        String fileName = "image.jpg";
        InputStream inputStream = new ByteArrayInputStream(new byte[]{});

        MediaDto mediaDto = MediaDto.builder()
            .mediaType(MediaType.IMAGE_JPEG)
            .content(inputStream)
            .build();

        org.mockito.Mockito.when(mediaService.getFile(mediaId, fileName))
            .thenReturn(mediaDto);

        
        mediaController.getFile(mediaId, fileName);

        
        org.mockito.Mockito.verify(mediaService).getFile(mediaId, fileName);
    }
}
