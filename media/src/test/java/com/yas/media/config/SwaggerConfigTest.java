package com.yas.media.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {
    @Test
    void swaggerConfig_shouldInstantiate() {
        SwaggerConfig config = new SwaggerConfig();
        assertNotNull(config);
    }
}
