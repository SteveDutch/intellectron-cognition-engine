package com.stevedutch.intellectron.exception;

public class SearchTermNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

	public SearchTermNotFoundException(String message) {
        super(message);
    }

}
