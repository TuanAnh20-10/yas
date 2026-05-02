package com.yas.media.viewmodel;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {
    @Test
    void testRecordFields() {
        List<String> errors = List.of("field1", "field2");
        ErrorVm vm = new ErrorVm("400", "title", "detail", errors);
        assertEquals("400", vm.statusCode());
        assertEquals("title", vm.title());
        assertEquals("detail", vm.detail());
        assertEquals(errors, vm.fieldErrors());
    }

    @Test
    void testConstructorWithoutFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "title", "detail");
        assertEquals("400", vm.statusCode());
        assertEquals("title", vm.title());
        assertEquals("detail", vm.detail());
        assertNotNull(vm.fieldErrors());
        assertTrue(vm.fieldErrors().isEmpty());
    }
}
