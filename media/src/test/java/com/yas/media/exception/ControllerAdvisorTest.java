package com.yas.media.exception;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerAdvisorTest {
    private final ControllerAdvisor advisor = new ControllerAdvisor();

    @Test
    void handleUnsupportedMediaTypeException_returnsBadRequest() {
        UnsupportedMediaTypeException ex = new UnsupportedMediaTypeException("not supported");
        WebRequest request = null;
        ResponseEntity<ErrorVm> response = advisor.handleUnsupportedMediaTypeException(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleNotFoundException_returnsNotFound() {
        NotFoundException ex = new NotFoundException("not found");
        WebRequest request = null;
        ResponseEntity<ErrorVm> response = advisor.handleNotFoundException(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleMethodArgumentNotValid_returnsBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        ResponseEntity<ErrorVm> response = advisor.handleMethodArgumentNotValid(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getRootBeanClass()).thenReturn((Class)String.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        ResponseEntity<ErrorVm> response = advisor.handleConstraintViolation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleIoException_returnsInternalServerError() {
        RuntimeException ex = new RuntimeException("IO error");
        WebRequest request = null;
        ResponseEntity<ErrorVm> response = advisor.handleIoException(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleOtherException_returnsInternalServerError() {
        Exception ex = new Exception("other error");
        WebRequest request = null;
        ResponseEntity<ErrorVm> response = advisor.handleOtherException(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleOtherException_withServletWebRequest_shouldReturnAndNotThrow() {
        Exception ex = new Exception("other error");
        var servletRequest = new org.springframework.mock.web.MockHttpServletRequest("GET", "/test/path");
        var servletWebRequest = new org.springframework.web.context.request.ServletWebRequest(servletRequest);
        ResponseEntity<ErrorVm> response = advisor.handleOtherException(ex, servletWebRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().title().length() > 0);
    }
}
