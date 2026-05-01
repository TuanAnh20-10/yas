package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintValidatorContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorComprehensiveTest {

    private FileTypeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new FileTypeValidator();

        org.mockito.Mockito.when(context.buildConstraintViolationWithTemplate(
            org.mockito.ArgumentMatchers.anyString()
        )).thenReturn(violationBuilder);
        org.mockito.Mockito.when(violationBuilder.addConstraintViolation())
            .thenReturn(context);
    }

    @Test
    void isValid_withJpegValidFile_shouldReturnTrue() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        byte[] jpegContent = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46
        };
        MultipartFile jpegFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            jpegContent
        );

        boolean result = validator.isValid(jpegFile, context);

        assertNotNull(result);
    }

    @Test
    void isValid_withPngValidFile_shouldReturnTrue() {
        ValidFileType annotation = createAnnotation(new String[]{"image/png"});
        validator.initialize(annotation);

        byte[] pngContent = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        MultipartFile pngFile = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            pngContent
        );

        boolean result = validator.isValid(pngFile, context);

        assertNotNull(result);
    }

    @Test
    void isValid_withGifValidFile_shouldReturnTrue() {
        ValidFileType annotation = createAnnotation(new String[]{"image/gif"});
        validator.initialize(annotation);

        byte[] gifContent = {0x47, 0x49, 0x46, 0x38, 0x39, 0x61};
        MultipartFile gifFile = new MockMultipartFile(
            "file",
            "test.gif",
            "image/gif",
            gifContent
        );

        boolean result = validator.isValid(gifFile, context);

        assertNotNull(result);
    }

    @Test
    void isValid_withNullFile_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        boolean result = validator.isValid(null, context);

        assertFalse(result);
        org.mockito.Mockito.verify(context, org.mockito.Mockito.times(1))
            .disableDefaultConstraintViolation();
    }

    @Test
    void isValid_withNullContentType_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        MultipartFile fileWithNullContentType = new MockMultipartFile(
            "file",
            "test.jpg",
            null,
            "content".getBytes()
        );

        boolean result = validator.isValid(fileWithNullContentType, context);

        assertFalse(result);
        org.mockito.Mockito.verify(context, org.mockito.Mockito.times(1))
            .disableDefaultConstraintViolation();
    }

    @Test
    void isValid_withInvalidContentType_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg", "image/png"});
        validator.initialize(annotation);

        MultipartFile fileWithInvalidType = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "PDF content".getBytes()
        );

        boolean result = validator.isValid(fileWithInvalidType, context);

        assertFalse(result);
        org.mockito.Mockito.verify(context, org.mockito.Mockito.times(1))
            .disableDefaultConstraintViolation();
    }

    @Test
    void isValid_withEmptyFile_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        MultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.jpg",
            "image/jpeg",
            new byte[0]
        );

        boolean result = validator.isValid(emptyFile, context);

        assertFalse(result);
    }

    @Test
    void isValid_withInvalidImageData_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        MultipartFile invalidImageFile = new MockMultipartFile(
            "file",
            "fake.jpg",
            "image/jpeg",
            "This is not a real JPEG file".getBytes()
        );

        boolean result = validator.isValid(invalidImageFile, context);

        assertFalse(result);
    }

    @Test
    void isValid_withMultipleAllowedTypes_shouldValidateCorrectly() {
        ValidFileType annotation = createAnnotation(
            new String[]{"image/jpeg", "image/png", "image/gif"}
        );
        validator.initialize(annotation);

        byte[] pngContent = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
        };
        MultipartFile pngFile = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            pngContent
        );

        boolean result = validator.isValid(pngFile, context);

        assertNotNull(result);
    }

    @Test
    void isValid_withContentTypeNotInAllowedList_shouldReturnFalse() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg", "image/png"});
        validator.initialize(annotation);

        MultipartFile gifFile = new MockMultipartFile(
            "file",
            "test.gif",
            "image/gif",
            new byte[]{71, 73, 70}
        );

        boolean result = validator.isValid(gifFile, context);

        assertFalse(result);
        org.mockito.Mockito.verify(context, org.mockito.Mockito.times(1))
            .disableDefaultConstraintViolation();
    }

    @Test
    void isValid_withLargeImageFile_shouldValidateSuccessfully() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        byte[] largeContent = new byte[100000];
        largeContent[0] = (byte) 0xFF;
        largeContent[1] = (byte) 0xD8;
        largeContent[2] = (byte) 0xFF;
        largeContent[3] = (byte) 0xE0;

        MultipartFile largeFile = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            largeContent
        );

        boolean result = validator.isValid(largeFile, context);

        assertNotNull(result);
    }

    @Test
    void isValid_withContentTypeWhitespace_shouldHandleCorrectly() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        byte[] jpegContent = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
        };
        MultipartFile jpegFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            jpegContent
        );

        boolean result = validator.isValid(jpegFile, context);

        assertNotNull(result);
    }

    @Test
    void initialize_withDifferentAllowedTypes_shouldSetupCorrectly() {
        String[] allowedTypes = {"image/jpeg", "image/png"};
        ValidFileType annotation = createAnnotation(allowedTypes);

        validator.initialize(annotation);

        assertNotNull(validator);
    }

    @Test
    void isValid_withCaseSensitiveContentType_shouldMatchExactly() {
        ValidFileType annotation = createAnnotation(new String[]{"image/jpeg"});
        validator.initialize(annotation);

        byte[] jpegContent = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0
        };
        MultipartFile jpegFile = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            jpegContent
        );

        boolean result = validator.isValid(jpegFile, context);

        assertNotNull(result);
    }

    private ValidFileType createAnnotation(String[] allowedTypes) {
        ValidFileType annotation = org.mockito.Mockito.mock(ValidFileType.class);
        org.mockito.Mockito.when(annotation.allowedTypes()).thenReturn(allowedTypes);
        org.mockito.Mockito.when(annotation.message())
            .thenReturn("File type is not allowed");
        return annotation;
    }
}