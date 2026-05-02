package com.yas.media.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilesystemConfigTest {
    @Test
    void testSetAndGetDirectory() {
        FilesystemConfig config = new FilesystemConfig();
        String dir = "/tmp/test";
        try {
            java.lang.reflect.Field field = FilesystemConfig.class.getDeclaredField("directory");
            field.setAccessible(true);
            field.set(config, dir);
        } catch (Exception e) {
            fail(e);
        }
        assertEquals(dir, config.getDirectory());
    }
}
