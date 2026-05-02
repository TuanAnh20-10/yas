package com.yas.media.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaVmTest {
    @Test
    void testConstructorAndGetters() {
        MediaVm vm = new MediaVm(1L, "caption", "fileName", "image/png", "url");
        assertEquals(1L, vm.getId());
        assertEquals("caption", vm.getCaption());
        assertEquals("fileName", vm.getFileName());
        assertEquals("image/png", vm.getMediaType());
        assertEquals("url", vm.getUrl());
    }

    @Test
    void testSetters() {
        MediaVm vm = new MediaVm(1L, "caption", "fileName", "image/png", "url");
        vm.setCaption("newCaption");
        vm.setFileName("newFileName");
        vm.setMediaType("image/gif");
        vm.setUrl("newUrl");
        assertEquals("newCaption", vm.getCaption());
        assertEquals("newFileName", vm.getFileName());
        assertEquals("image/gif", vm.getMediaType());
        assertEquals("newUrl", vm.getUrl());
    }
}
