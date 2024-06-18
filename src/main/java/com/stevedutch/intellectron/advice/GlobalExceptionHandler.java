package com.stevedutch.intellectron.advice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.stevedutch.intellectron.exception.EmptyZettelException;
import com.stevedutch.intellectron.exception.TagNotFoundException;
import com.stevedutch.intellectron.exception.TopicTooLongException;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<String> handleTagNotFoundException(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    	// XXX for gistorical & maybe learning reasons
//    @ExceptionHandler(EmptyZettelException.class)
//    public ResponseEntity<String> handleEmptyZettelException(EmptyZettelException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
//    
//    @ExceptionHandler(EmptyZettelException.class)
//    public ModelAndView handleEmptyZettelException(EmptyZettelException ex) {
//        ModelAndView modelAndView = new ModelAndView("400");
//        modelAndView.setStatus(HttpStatus.BAD_REQUEST);
//        modelAndView.addObject("message", ex.getMessage());
//        System.out.println("Exception message: " + ex.getMessage());
//        return modelAndView;
//    }
    
//    @ExceptionHandler(EmptyZettelException.class)
//    public String handleEmptyZettelException(EmptyZettelException ex, RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("message", ex.getMessage());
//        return "redirect:/400";
//    }
    
    @ExceptionHandler(EmptyZettelException.class)
    public ResponseEntity<Map<String, String>> handleEmptyZettelException(EmptyZettelException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(TopicTooLongException.class)
    public ResponseEntity<Map<String, String>> handleTopicTooLongException(TopicTooLongException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    

}
