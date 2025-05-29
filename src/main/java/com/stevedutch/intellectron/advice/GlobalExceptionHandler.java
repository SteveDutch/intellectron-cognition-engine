package com.stevedutch.intellectron.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.ui.Model;

import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.exception.SearchTermNotFoundException;
import com.stevedutch.intellectron.exception.TagNotFoundException;
import com.stevedutch.intellectron.exception.TopicTooLongException;

import jakarta.persistence.EntityNotFoundException;

/**
 * Global exception handler for the application.
 * This class provides centralized exception handling across all {@code @RequestMapping} methods
 * through {@code @ExceptionHandler} methods.
 * 
 * <p>Each method handles a specific type of exception and returns an appropriate
 * HTTP response with error details. The handler methods are designed to provide
 * consistent error responses across the application.</p>
 *
 * @author stevedutch
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
    /**
     * Handles exceptions when a search term is not found.
     *
     * @param ex the SearchTermNotFoundException that was thrown
     * @return ResponseEntity containing the error message with HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(SearchTermNotFoundException.class)
    public ResponseEntity<String> SearchTermNotFoundException(SearchTermNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    
    /**
     * Handles exceptions when an empty Zettel is submitted.
     *
     * @param ex the EmptyZettelException that was thrown
     * @return ResponseEntity containing a map with the error message and HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(EmptyZettelException.class)
    public ResponseEntity<Map<String, String>> handleEmptyZettelException(EmptyZettelException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles exceptions when a topic exceeds the maximum allowed length.
     *
     * @param ex the TopicTooLongException that was thrown
     * @return ResponseEntity containing a map with the error message and HTTP status 400 (BAD_REQUEST)
     */
    @ExceptionHandler(TopicTooLongException.class)
    public ResponseEntity<Map<String, String>> handleTopicTooLongException(TopicTooLongException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles JPA entity not found exceptions.
     *
     * @param ex the EntityNotFoundException that was thrown
     * @return ResponseEntity containing a map with the error message and HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        // Assuming you have an error.html template in your templates directory
        return "error";  // or whatever your error page template name is
    }
    
    /**
     * Handles exceptions when a requested tag cannot be found.
     *
     * @param ex the TagNotFoundException that was thrown
     * @return ResponseEntity containing the error message with HTTP status 404 (NOT_FOUND)
     */
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<String> handleTagNotFoundException(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    

}
