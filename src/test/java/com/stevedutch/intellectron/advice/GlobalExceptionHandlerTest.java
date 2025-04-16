package com.stevedutch.intellectron.advice;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.exception.TagNotFoundException;
import com.stevedutch.intellectron.exception.TopicTooLongException;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleSearchTermNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Search term not found";
        SearchTermNotFoundException exception = new SearchTermNotFoundException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.SearchTermNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void handleEmptyZettelException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "Zettel cannot be empty";
        EmptyZettelException exception = new EmptyZettelException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleEmptyZettelException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleTopicTooLongException_ShouldReturnBadRequestStatus() {
        // Arrange
        String errorMessage = "Topic exceeds maximum length";
        TopicTooLongException exception = new TopicTooLongException(errorMessage);

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleTopicTooLongException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().get("message"));
    }

    @Test
    void handleEntityNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Entity not found";
        EntityNotFoundException exception = new EntityNotFoundException(errorMessage);
        Model model = new ExtendedModelMap();

        // Act
        String viewName = exceptionHandler.handleEntityNotFoundException(exception, model);

        // Assert
        assertEquals("error", viewName);
        assertEquals(errorMessage, model.getAttribute("message"));
    }

    @Test
    void handleTagNotFoundException_ShouldReturnNotFoundStatus() {
        // Arrange
        String errorMessage = "Tag not found";
        TagNotFoundException exception = new TagNotFoundException(errorMessage);

        // Act
        ResponseEntity<String> response = exceptionHandler.handleTagNotFoundException(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }
} 