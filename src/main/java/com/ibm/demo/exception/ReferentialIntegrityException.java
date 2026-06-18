package com.ibm.demo.exception;

/**
 * Exception thrown when attempting to delete an entity that has dependent relationships.
 */
public class ReferentialIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReferentialIntegrityException(String message) {
		super(message);
	}

	public ReferentialIntegrityException(String message, Throwable cause) {
		super(message, cause);
	}
}

// Made with Bob
