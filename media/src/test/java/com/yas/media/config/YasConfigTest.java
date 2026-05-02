package com.yas.media.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class YasConfigTest {

    @Test
    void recordShouldExposePublicUrl() {
        YasConfig config = new YasConfig("https://media.example.com");

        assertNotNull(config);
        assertEquals("https://media.example.com", config.publicUrl());
    }
}