package com.yas.media.repository;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaRepositoryTest {
    @Test
    void testFindByIdWithoutFileInReturn() {
        MediaRepository mediaRepository = mock(MediaRepository.class);
        NoFileMediaVm vm = new NoFileMediaVm(1L, "caption", "fileName", "image/png");
        when(mediaRepository.findByIdWithoutFileInReturn(1L)).thenReturn(vm);
        NoFileMediaVm result = mediaRepository.findByIdWithoutFileInReturn(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("caption", result.caption());
        assertEquals("fileName", result.fileName());
        assertEquals("image/png", result.mediaType());
    }
}
