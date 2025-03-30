package com.stevedutch.intellectron.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TagNotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String errorMessage = "Tag not found with id: 42";
        TagNotFoundException exception = new TagNotFoundException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithEmptyMessage() {
        String errorMessage = "";
        TagNotFoundException exception = new TagNotFoundException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
} 