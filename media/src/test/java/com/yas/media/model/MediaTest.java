package com.yas.media.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {
    @Test
    void testMediaGettersAndSetters() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("fileName");
        media.setFilePath("/path/to/file");
        media.setMediaType("image/png");

        assertEquals(1L, media.getId());
        assertEquals("caption", media.getCaption());
        assertEquals("fileName", media.getFileName());
        assertEquals("/path/to/file", media.getFilePath());
        assertEquals("image/png", media.getMediaType());
    }
}
