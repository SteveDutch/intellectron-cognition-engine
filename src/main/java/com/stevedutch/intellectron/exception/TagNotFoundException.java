package com.stevedutch.intellectron.exception;

/**
 * Exception thrown when a requested Tag entity cannot be found in the system.
 * This runtime exception is typically thrown during tag-related operations
 * when a tag with a specified identifier or criteria does not exist.
 *
 * @author stevedutch
 * @since 1.0
 */
public class TagNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new TagNotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public TagNotFoundException(String message) {
        super(message);
    }
}