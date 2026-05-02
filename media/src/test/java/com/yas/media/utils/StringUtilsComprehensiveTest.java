package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;

@DisplayName("StringUtils Unit Tests")
class StringUtilsComprehensiveTest {

    @Test
    @DisplayName("hasText should return true for non-empty string")
    void hasText_withNonEmptyString_shouldReturnTrue() {
        String text = "Hello World";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return false for null")
    void hasText_withNull_shouldReturnFalse() {
        String text = null;

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return false for empty string")
    void hasText_withEmptyString_shouldReturnFalse() {
        String text = "";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return false for whitespace only")
    void hasText_withWhitespaceOnly_shouldReturnFalse() {
        String text = "   ";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return false for tab character")
    void hasText_withTabCharacter_shouldReturnFalse() {
        String text = "\t";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return false for newline character")
    void hasText_withNewlineCharacter_shouldReturnFalse() {
        String text = "\n";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return false for carriage return")
    void hasText_withCarriageReturn_shouldReturnFalse() {
        String text = "\r";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @Test
    @DisplayName("hasText should return true for string with leading spaces and content")
    void hasText_withLeadingSpaces_shouldReturnTrue() {
        String text = "   text";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for string with trailing spaces and content")
    void hasText_withTrailingSpaces_shouldReturnTrue() {
        String text = "text   ";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for string with mixed whitespace and content")
    void hasText_withMixedWhitespaceAndContent_shouldReturnTrue() {
        String text = "  \t text \n  ";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for single character")
    void hasText_withSingleCharacter_shouldReturnTrue() {
        String text = "a";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for special characters")
    void hasText_withSpecialCharacters_shouldReturnTrue() {
        String text = "@#$%^&*()";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for unicode characters")
    void hasText_withUnicodeCharacters_shouldReturnTrue() {
        String text = "你好世界";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for numbers")
    void hasText_withNumbers_shouldReturnTrue() {
        String text = "12345";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should return true for mixed alphanumeric")
    void hasText_withMixedAlphanumeric_shouldReturnTrue() {
        String text = "Test123@#$";

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should handle very long strings")
    void hasText_withVeryLongString_shouldReturnTrue() {
        String text = "a".repeat(10000);

        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasText should handle zero-width characters")
    void hasText_withZeroWidthCharacters_shouldReturnFalse() {
        String text = "\u200B";

        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t", "\n", "\r", "  \t\n  "})
    @DisplayName("hasText should return false for various whitespace combinations")
    void hasText_withVariousWhitespace_shouldReturnFalse(String text) {
        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "text", "123", "test@example.com", "File.pdf"})
    @DisplayName("hasText should return true for various non-whitespace strings")
    void hasText_withVariousNonWhitespace_shouldReturnTrue(String text) {
        boolean result = StringUtils.hasText(text);

        assertTrue(result);
    }

    @ParameterizedTest
    @NullSource
    @DisplayName("hasText should return false for null value")
    void hasText_withNullSource_shouldReturnFalse(String text) {
        boolean result = StringUtils.hasText(text);

        assertFalse(result);
    }
}