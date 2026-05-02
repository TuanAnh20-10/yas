package com.yas.media.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.media.config.YasConfig;
import com.yas.media.mapper.MediaVmMapper;
import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.repository.FileSystemRepository;
import com.yas.media.repository.MediaRepository;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MediaServiceImplIntegrationTest {

    @Spy
    private MediaVmMapper mediaVmMapper = Mappers.getMapper(MediaVmMapper.class);

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileSystemRepository fileSystemRepository;

    @Mock
    private YasConfig yasConfig;

    @InjectMocks
    private MediaServiceImpl mediaService;

    private Media testMedia;
    private NoFileMediaVm testNoFileMediaVm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testMedia = new Media();
        testMedia.setId(1L);
        testMedia.setCaption("Test Media");
        testMedia.setFileName("test.jpg");
        testMedia.setMediaType("image/jpeg");
        testMedia.setFilePath("/path/to/test.jpg");

        testNoFileMediaVm = new NoFileMediaVm(1L, "Test Media", "test.jpg", "image/jpeg");
    }

    @Test
    void saveMedia_withValidPNG_shouldSaveSuccessfully() throws Exception {
        byte[] pngContent = {-119, 80, 78, 71};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "image.png",
            "image/png",
            pngContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("PNG Image", multipartFile, null);

        Media savedMedia = new Media();
        savedMedia.setId(2L);
        savedMedia.setCaption("PNG Image");
        savedMedia.setFileName("image.png");
        savedMedia.setMediaType("image/png");
        savedMedia.setFilePath("/files/image.png");

        when(fileSystemRepository.persistFile("image.png", pngContent))
            .thenReturn("/files/image.png");
        when(mediaRepository.save(org.mockito.ArgumentMatchers.any(Media.class)))
            .thenReturn(savedMedia);

        Media result = mediaService.saveMedia(mediaPostVm);

        assertNotNull(result);
        assertEquals("PNG Image", result.getCaption());
        assertEquals("image.png", result.getFileName());
        assertEquals("image/png", result.getMediaType());
        assertEquals("/files/image.png", result.getFilePath());
    }

    @Test
    void saveMedia_withFileNameOverride_shouldUseTrimmedOverride() throws Exception {
        byte[] jpgContent = new byte[]{};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "original.jpg",
            "image/jpeg",
            jpgContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("Photo", multipartFile, "  custom_name.jpg  ");

        Media savedMedia = new Media();
        savedMedia.setId(3L);
        savedMedia.setCaption("Photo");
        savedMedia.setFileName("custom_name.jpg");
        savedMedia.setMediaType("image/jpeg");
        savedMedia.setFilePath("/files/custom_name.jpg");

        when(fileSystemRepository.persistFile("custom_name.jpg", jpgContent))
            .thenReturn("/files/custom_name.jpg");
        when(mediaRepository.save(org.mockito.ArgumentMatchers.any(Media.class)))
            .thenReturn(savedMedia);

        Media result = mediaService.saveMedia(mediaPostVm);

        assertNotNull(result);
        assertEquals("custom_name.jpg", result.getFileName());
    }

    @Test
    void getMediaById_shouldConstructCorrectUrl() {
        when(mediaRepository.findByIdWithoutFileInReturn(1L))
            .thenReturn(testNoFileMediaVm);
        when(yasConfig.publicUrl()).thenReturn("https://api.example.com");

        MediaVm result = mediaService.getMediaById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Media", result.getCaption());
        assertTrue(result.getUrl().contains("/medias/1/file/test.jpg"));
        assertTrue(result.getUrl().startsWith("https://api.example.com"));
    }

    @Test
    void getMediaByIds_withMultipleIds_shouldReturnAllMediaWithUrls() {
        Media media1 = createMedia(1L, "media1.jpg");
        Media media2 = createMedia(2L, "media2.png");
        List<Media> mediaList = List.of(media1, media2);

        when(mediaRepository.findAllById(List.of(1L, 2L)))
            .thenReturn(mediaList);
        when(yasConfig.publicUrl()).thenReturn("https://cdn.example.com");

        List<MediaVm> results = mediaService.getMediaByIds(List.of(1L, 2L));

        assertThat(results).hasSize(2);
        assertThat(results)
            .allMatch(m -> m.getUrl() != null && m.getUrl().contains("https://cdn.example.com"));
    }

    @Test
    void getFile_withMatchingFileName_shouldReturnMediaDto() {
        InputStream mockInputStream = new ByteArrayInputStream("dummy data".getBytes());

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(testMedia));
        when(fileSystemRepository.getFile(testMedia.getFilePath()))
                .thenReturn(mockInputStream);

        MediaDto result = mediaService.getFile(1L, "test.jpg");

        assertNotNull(result);
        assertEquals(MediaType.IMAGE_JPEG, result.getMediaType());
        assertNotNull(result.getContent());
    }

    @Test
    void getFile_withNonMatchingFileName_shouldReturnEmptyMediaDto() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(testMedia));

        MediaDto result = mediaService.getFile(1L, "wrong_file.jpg");

        assertNotNull(result);
        assertNull(result.getMediaType());
        assertNull(result.getContent());
    }

    @Test
    void getFile_withNonExistentMediaId_shouldReturnEmptyMediaDto() {
        when(mediaRepository.findById(999L)).thenReturn(Optional.empty());

        MediaDto result = mediaService.getFile(999L, "any_file.jpg");

        assertNotNull(result);
        assertNull(result.getMediaType());
        assertNull(result.getContent());
    }

    @Test
    void removeMedia_withNonExistentId_shouldThrowNotFoundException() {
        when(mediaRepository.findByIdWithoutFileInReturn(999L))
            .thenReturn(null);

        NotFoundException exception = assertThrows(
            NotFoundException.class,
            () -> mediaService.removeMedia(999L)
        );
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void removeMedia_withValidId_shouldDeleteSuccessfully() {
        when(mediaRepository.findByIdWithoutFileInReturn(1L))
            .thenReturn(testNoFileMediaVm);
        org.mockito.Mockito.doNothing().when(mediaRepository).deleteById(1L);

        mediaService.removeMedia(1L);

        org.mockito.Mockito.verify(mediaRepository, org.mockito.Mockito.times(1)).deleteById(1L);
    }

    @Test
    void getMediaByIds_withEmptyList_shouldReturnEmptyList() {
        when(mediaRepository.findAllById(List.of()))
            .thenReturn(List.of());

        List<MediaVm> results = mediaService.getMediaByIds(List.of());

        assertThat(results).isEmpty();
    }

    @Test
    void saveMedia_withGifFile_shouldSaveSuccessfully() throws Exception {
        byte[] gifContent = {71, 73, 70, 56};
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            "animation.gif",
            "image/gif",
            gifContent
        );
        MediaPostVm mediaPostVm = new MediaPostVm("Animated GIF", multipartFile, "animated.gif");

        Media savedMedia = new Media();
        savedMedia.setId(4L);
        savedMedia.setCaption("Animated GIF");
        savedMedia.setFileName("animated.gif");
        savedMedia.setMediaType("image/gif");
        savedMedia.setFilePath("/files/animated.gif");

        when(fileSystemRepository.persistFile("animated.gif", gifContent))
            .thenReturn("/files/animated.gif");
        when(mediaRepository.save(org.mockito.ArgumentMatchers.any(Media.class)))
            .thenReturn(savedMedia);

        Media result = mediaService.saveMedia(mediaPostVm);

        assertNotNull(result);
        assertEquals("image/gif", result.getMediaType());
    }

    private Media createMedia(Long id, String fileName) {
        Media media = new Media();
        media.setId(id);
        media.setFileName(fileName);
        media.setCaption("Caption " + id);
        media.setMediaType("image/jpeg");
        media.setFilePath("/path/" + fileName);
        return media;
    }
}