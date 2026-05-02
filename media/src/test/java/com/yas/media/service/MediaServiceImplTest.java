package com.yas.media.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MediaServiceImplTest {
    @Mock
    private MediaVmMapper mediaVmMapper;
    @Mock
    private MediaRepository mediaRepository;
    @Mock
    private FileSystemRepository fileSystemRepository;
    @Mock
    private YasConfig yasConfig;
    @InjectMocks
    private MediaServiceImpl mediaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveMedia_shouldSaveAndReturnMedia() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{1,2,3});
        MediaPostVm postVm = new MediaPostVm("caption", file, "fileName");
        when(fileSystemRepository.persistFile(any(), any())).thenReturn("/path/to/file");
        when(mediaRepository.save(any(Media.class))).thenAnswer(inv -> inv.getArgument(0));
        Media media = mediaService.saveMedia(postVm);
        assertEquals("caption", media.getCaption());
        assertEquals("fileName", media.getFileName());
        assertEquals("image/png", media.getMediaType());
        assertEquals("/path/to/file", media.getFilePath());
    }

    @Test
    void removeMedia_whenNotFound_shouldThrowException() {
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> mediaService.removeMedia(1L));
    }

    @Test
    void removeMedia_whenFound_shouldDelete() {
        NoFileMediaVm vm = new NoFileMediaVm(1L, "caption", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(vm);
        doNothing().when(mediaRepository).deleteById(1L);
        mediaService.removeMedia(1L);
        verify(mediaRepository, times(1)).deleteById(1L);
    }

    @Test
    void getMediaById_whenFound_shouldReturnVm() {
        NoFileMediaVm vm = new NoFileMediaVm(1L, "caption", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(vm);
        when(yasConfig.publicUrl()).thenReturn("/media/");
        MediaVm result = mediaService.getMediaById(1L);
        assertNotNull(result);
        assertEquals("caption", result.getCaption());
        assertEquals("fileName", result.getFileName());
        assertEquals("image/png", result.getMediaType());
        assertTrue(result.getUrl().contains("/media/"));
    }

    @Test
    void getMediaById_whenNotFound_shouldReturnNull() {
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(null);
        assertNull(mediaService.getMediaById(1L));
    }

    @Test
    void getFile_whenNotFoundOrNameMismatch_shouldReturnEmptyDto() {
        when(mediaRepository.findById(1L)).thenReturn(Optional.empty());
        MediaDto dto = mediaService.getFile(1L, "fileName");
        assertNull(dto.getContent());
        assertNull(dto.getMediaType());
    }

    @Test
    void getFile_whenNameMismatch_shouldReturnEmptyDto() {
        Media media = new Media();
        media.setId(1L);
        media.setFileName("otherName");
        media.setMediaType("image/png");
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));

        MediaDto dto = mediaService.getFile(1L, "fileName");
        assertNull(dto.getContent());
        assertNull(dto.getMediaType());
    }

    @Test
    void getFile_whenFoundAndNameMatch_shouldReturnDto() {
        Media media = new Media();
        media.setId(1L);
        media.setFileName("fileName");
        media.setMediaType("image/png");
        media.setFilePath("/path/to/file");
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        when(fileSystemRepository.getFile("/path/to/file")).thenReturn(new ByteArrayInputStream(new byte[]{1,2,3}));
        MediaDto dto = mediaService.getFile(1L, "fileName");
        assertNotNull(dto.getContent());
        assertEquals(MediaType.IMAGE_PNG, dto.getMediaType());
    }

    @Test
    void getMediaByIds_shouldReturnList() {
        Media media = new Media();
        media.setId(1L);
        media.setFileName("fileName");
        media.setCaption("caption");
        media.setMediaType("image/png");
        List<Media> medias = List.of(media);
        MediaVm vm = new MediaVm(1L, "caption", "fileName", "image/png", null);
        when(mediaRepository.findAllById(List.of(1L))).thenReturn(medias);
        when(mediaVmMapper.toVm(media)).thenReturn(vm);
        when(yasConfig.publicUrl()).thenReturn("/media/");
        List<MediaVm> result = mediaService.getMediaByIds(List.of(1L));
        assertEquals(1, result.size());
        assertTrue(result.get(0).getUrl().contains("/media/"));
    }
}
