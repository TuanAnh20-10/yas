package com.yas.media.mapper;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MediaMapperTest {
    private final MediaVmMapper mapper = new MediaVmMapperImpl();

    @Test
    void testToViewModel() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("file.jpg");
        media.setMediaType("image");
        media.setFilePath("/files/file.jpg");
        MediaVm vm = mapper.toVm(media);
        assertEquals(1L, vm.getId());
        assertEquals("caption", vm.getCaption());
        assertEquals("file.jpg", vm.getFileName());
        assertEquals("image", vm.getMediaType());
        assertEquals("/files/file.jpg", vm.getUrl());
    }

    @Test
    void testToEntity() {
        MediaVm vm = new MediaVm(2L, "cap2", "f2.png", "image", "/files/f2.png");
        Media media = mapper.toModel(vm);
        assertEquals(2L, media.getId());
        assertEquals("cap2", media.getCaption());
        assertEquals("f2.png", media.getFileName());
        assertEquals("image", media.getMediaType());
        assertEquals("/files/f2.png", media.getFilePath());
    }
}
