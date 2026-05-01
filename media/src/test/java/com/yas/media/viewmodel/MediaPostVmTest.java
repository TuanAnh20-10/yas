package com.yas.media.viewmodel;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class MediaPostVmTest {
    @Test
    void testRecordFields() {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[]{});
        MediaPostVm vm = new MediaPostVm("caption", file, "fileNameOverride");
        assertEquals("caption", vm.caption());
        assertEquals(file, vm.multipartFile());
        assertEquals("fileNameOverride", vm.fileNameOverride());
    }
}
