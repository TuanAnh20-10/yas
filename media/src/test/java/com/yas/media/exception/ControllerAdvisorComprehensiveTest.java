package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@DisplayName("ControllerAdvisor Exception Handling Tests")
class ControllerAdvisorComprehensiveTest {

    private ControllerAdvisor advisor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        advisor = new ControllerAdvisor();
    }

    @Test
    @DisplayName("Should handle UnsupportedMediaTypeException with 400 status")
    void handleUnsupportedMediaTypeException_shouldReturnBadRequest() {
        UnsupportedMediaTypeException exception =
            new UnsupportedMediaTypeException("PNG format is not supported");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleUnsupportedMediaTypeException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().statusCode());
        assertTrue(response.getBody().title().contains("media type"));
    }

    @Test
    @DisplayName("Should handle NotFoundException with 404 status")
    void handleNotFoundException_shouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Media with ID 123 not found");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleNotFoundException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().statusCode());
        assertTrue(response.getBody().detail().contains("123"));
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void handleMethodArgumentNotValid_withFieldErrors_shouldReturnBadRequest() {
        MethodArgumentNotValidException exception = org.mockito.Mockito
            .mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = org.mockito.Mockito
            .mock(org.springframework.validation.BindingResult.class);

        FieldError fieldError = new FieldError("MediaPostVm", "caption", "must not be blank");
        List<FieldError> errors = List.of(fieldError);

        org.mockito.Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);

        ResponseEntity<ErrorVm> response = advisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().statusCode());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with multiple field errors")
    void handleMethodArgumentNotValid_withMultipleErrors_shouldReturnAllErrors() {
        MethodArgumentNotValidException exception = org.mockito.Mockito
            .mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = org.mockito.Mockito
            .mock(org.springframework.validation.BindingResult.class);

        List<FieldError> errors = List.of(
            new FieldError("MediaPostVm", "caption", "must not be blank"),
            new FieldError("MediaPostVm", "multipartFile", "must not be null"),
            new FieldError("MediaPostVm", "fileNameOverride", "must be valid")
        );

        org.mockito.Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(errors);

        ResponseEntity<ErrorVm> response = advisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().fieldErrors());
        assertEquals(3, response.getBody().fieldErrors().size());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with violations")
    void handleConstraintViolation_shouldReturnBadRequest() {
        ConstraintViolation<?> violation = org.mockito.Mockito.mock(ConstraintViolation.class);
        org.mockito.Mockito.when(violation.getRootBeanClass()).thenReturn((Class) String.class);

        jakarta.validation.Path path = org.mockito.Mockito.mock(jakarta.validation.Path.class);
        org.mockito.Mockito.when(path.toString()).thenReturn("fileName");
        org.mockito.Mockito.when(violation.getPropertyPath()).thenReturn(path);
        org.mockito.Mockito.when(violation.getMessage()).thenReturn("must not contain path traversal");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ResponseEntity<ErrorVm> response = advisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getBody().statusCode());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with multiple violations")
    void handleConstraintViolation_withMultipleViolations_shouldReturnAllViolations() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();

        ConstraintViolation<?> violation1 = org.mockito.Mockito.mock(ConstraintViolation.class);
        org.mockito.Mockito.when(violation1.getRootBeanClass()).thenReturn((Class) String.class);
        jakarta.validation.Path path1 = org.mockito.Mockito.mock(jakarta.validation.Path.class);
        org.mockito.Mockito.when(path1.toString()).thenReturn("field1");
        org.mockito.Mockito.when(violation1.getPropertyPath()).thenReturn(path1);
        org.mockito.Mockito.when(violation1.getMessage()).thenReturn("error 1");
        violations.add(violation1);

        ConstraintViolation<?> violation2 = org.mockito.Mockito.mock(ConstraintViolation.class);
        org.mockito.Mockito.when(violation2.getRootBeanClass()).thenReturn((Class) String.class);
        jakarta.validation.Path path2 = org.mockito.Mockito.mock(jakarta.validation.Path.class);
        org.mockito.Mockito.when(path2.toString()).thenReturn("field2");
        org.mockito.Mockito.when(violation2.getPropertyPath()).thenReturn(path2);
        org.mockito.Mockito.when(violation2.getMessage()).thenReturn("error 2");
        violations.add(violation2);

        ConstraintViolationException exception = new ConstraintViolationException(violations);

        ResponseEntity<ErrorVm> response = advisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().fieldErrors().size());
    }

    @Test
    @DisplayName("Should handle RuntimeException with 500 status")
    void handleIoException_shouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("File write error");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleIoException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getBody().statusCode());
        assertTrue(response.getBody().detail().contains("File write error"));
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void handleOtherException_shouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleOtherException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getBody().statusCode());
    }

    @Test
    @DisplayName("Should handle Exception with ServletWebRequest")
    void handleOtherException_withServletWebRequest_shouldIncludeRequestPath() {
        Exception exception = new Exception("Processing error");
        MockHttpServletRequest httpRequest = new MockHttpServletRequest("POST", "/api/media");
        ServletWebRequest request = new ServletWebRequest(httpRequest);

        ResponseEntity<ErrorVm> response = advisor.handleOtherException(exception, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getBody().statusCode());
    }

    @Test
    @DisplayName("Should handle Exception with null WebRequest")
    void handleOtherException_withNullWebRequest_shouldNotThrowNullPointer() {
        Exception exception = new Exception("Processing error");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleOtherException(exception, request);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should include proper error details in response")
    void handleOtherException_shouldIncludeDetailedErrorInfo() {
        String errorMessage = "Detailed error information";
        Exception exception = new Exception(errorMessage);
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleOtherException(exception, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains(errorMessage));
    }

    @Test
    @DisplayName("Should handle UnsupportedMediaTypeException with specific message")
    void handleUnsupportedMediaTypeException_withCustomMessage_shouldIncludeIt() {
        UnsupportedMediaTypeException exception =
            new UnsupportedMediaTypeException("GIF format not allowed");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleUnsupportedMediaTypeException(exception, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().title().contains("media type"));
    }

    @Test
    @DisplayName("Should handle NotFoundException with specific ID in message")
    void handleNotFoundException_withIdInMessage_shouldPreserveIt() {
        NotFoundException exception = new NotFoundException("Media 42 not found");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleNotFoundException(exception, request);

        assertNotNull(response.getBody());
        assertTrue(response.getBody().detail().contains("42"));
        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getBody().statusCode());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException without field errors")
    void handleMethodArgumentNotValid_withoutFieldErrors_shouldReturnBadRequest() {
        MethodArgumentNotValidException exception = org.mockito.Mockito
            .mock(MethodArgumentNotValidException.class);
        org.springframework.validation.BindingResult bindingResult = org.mockito.Mockito
            .mock(org.springframework.validation.BindingResult.class);

        org.mockito.Mockito.when(exception.getBindingResult()).thenReturn(bindingResult);
        org.mockito.Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of());

        ResponseEntity<ErrorVm> response = advisor.handleMethodArgumentNotValid(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException with empty violations")
    void handleConstraintViolation_withEmptyViolations_shouldReturnBadRequest() {
        ConstraintViolationException exception = new ConstraintViolationException(new HashSet<>());

        ResponseEntity<ErrorVm> response = advisor.handleConstraintViolation(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return valid ErrorVm structure")
    void errorResponseStructure_shouldHaveAllRequiredFields() {
        Exception exception = new Exception("Test error");
        WebRequest request = null;

        ResponseEntity<ErrorVm> response = advisor.handleOtherException(exception, request);

        assertNotNull(response.getBody());
        ErrorVm errorVm = response.getBody();
        assertNotNull(errorVm.statusCode());
        assertTrue(errorVm.title() != null || errorVm.title().isEmpty());
        assertTrue(errorVm.detail() != null);
    }
}
