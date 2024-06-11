package com.stevedutch.intellectron.exception;

public class EmptyZettelException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public EmptyZettelException(String message) {
		super(message);
	}

}
