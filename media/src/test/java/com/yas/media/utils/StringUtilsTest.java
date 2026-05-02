package com.yas.media.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {
    @Test
    void hasText_withNull_returnsFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_withEmpty_returnsFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_withBlank_returnsFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_withText_returnsTrue() {
        assertTrue(StringUtils.hasText("abc"));
    }
}
