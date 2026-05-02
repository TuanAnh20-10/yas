package com.yas.media.viewmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoFileMediaVmTest {
    @Test
    void testRecordFields() {
        NoFileMediaVm vm = new NoFileMediaVm(1L, "caption", "fileName", "image/png");
        assertEquals(1L, vm.id());
        assertEquals("caption", vm.caption());
        assertEquals("fileName", vm.fileName());
        assertEquals("image/png", vm.mediaType());
    }
}
