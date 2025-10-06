package com.stevedutch.intellectron.exception;

public class TopicTooLongException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public TopicTooLongException(String message) {
		super(message);
	}

}
