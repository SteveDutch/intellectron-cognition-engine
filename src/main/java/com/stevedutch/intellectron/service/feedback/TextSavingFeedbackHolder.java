package com.stevedutch.intellectron.service.feedback; // Or your chosen package

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Holds feedback messages generated during text saving/updating operations 
 * within a single request. Uses request scope for thread safety.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TextSavingFeedbackHolder {

    private String message = ""; // Initialize to empty string instead of null

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        // Handle potential null input defensively, although we aim to avoid it
        this.message = (message != null) ? message : ""; 
    }

    public void clear() {
        this.message = ""; // Reset to empty string
    }

    public boolean hasMessage() { // Optional helper method
        return this.message != null && !this.message.isBlank();
    }
} 