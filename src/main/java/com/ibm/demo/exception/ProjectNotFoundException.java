package com.ibm.demo.exception;

/**
 * Exception thrown when a project is not found.
 */
public class ProjectNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProjectNotFoundException(String message) {
		super(message);
	}

	public ProjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

// Made with Bob
