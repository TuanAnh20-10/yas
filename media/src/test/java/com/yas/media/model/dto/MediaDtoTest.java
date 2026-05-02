package com.yas.media.model.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class MediaDtoTest {
    @Test
    void testBuilderAndGetters() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{1,2,3});
        MediaDto dto = MediaDto.builder()
                .content(inputStream)
                .mediaType(MediaType.IMAGE_PNG)
                .build();
        assertEquals(inputStream, dto.getContent());
        assertEquals(MediaType.IMAGE_PNG, dto.getMediaType());
    }
}
