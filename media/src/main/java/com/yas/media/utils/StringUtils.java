package com.yas.media.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {
    public static boolean hasText(String input) {
        if (input == null) return false;

        input = input.replaceAll("[\\u200B\\u200C\\u200D\\uFEFF]", "");

        return !input.trim().isEmpty();
    }
}
