package com.yas.media.utils;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileTypeValidatorTest {
    private final FileTypeValidator validator = new FileTypeValidator();


    @Test
    void isValid_withNullFile_returnsFalse() {
        ValidFileType annotation = new ValidFileType() {
            public String message() { return "Invalid file type"; }
            public Class<?>[] groups() { return new Class[0]; }
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidFileType.class; }
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            public String[] allowedTypes() { return new String[]{"image/png"}; }
        };
        validator.initialize(annotation);
        var context = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.class);
        var builder = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.class);
        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(org.mockito.Mockito.anyString())).thenReturn(builder);
        org.mockito.Mockito.when(builder.addConstraintViolation()).thenReturn(context);
        assertFalse(validator.isValid(null, context));
    }


    @Test
    void isValid_withInvalidType_returnsFalse() {
        ValidFileType annotation = new ValidFileType() {
            public String message() { return "Invalid file type"; }
            public Class<?>[] groups() { return new Class[0]; }
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidFileType.class; }
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            public String[] allowedTypes() { return new String[]{"image/png"}; }
        };
        validator.initialize(annotation);
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", "text/plain", "abc".getBytes());
        var context = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.class);
        var builder = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.class);
        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(org.mockito.Mockito.anyString())).thenReturn(builder);
        org.mockito.Mockito.when(builder.addConstraintViolation()).thenReturn(context);
        assertFalse(validator.isValid(file, context));
    }


    @Test
    void isValid_withValidImageType_returnsTrue() throws Exception {
        ValidFileType annotation = new ValidFileType() {
            public String message() { return "Invalid file type"; }
            public Class<?>[] groups() { return new Class[0]; }
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return ValidFileType.class; }
            public Class<? extends jakarta.validation.Payload>[] payload() { return new Class[0]; }
            public String[] allowedTypes() { return new String[]{"image/png"}; }
        };
        validator.initialize(annotation);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(img, "png", baos);
        byte[] pngBytes = baos.toByteArray();
        MockMultipartFile file = new MockMultipartFile("file", "file.png", "image/png", pngBytes);
        var context = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.class);
        var builder = org.mockito.Mockito.mock(jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.class);
        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(org.mockito.Mockito.anyString())).thenReturn(builder);
        org.mockito.Mockito.when(builder.addConstraintViolation()).thenReturn(context);
        assertTrue(validator.isValid(file, context));
    }
}
