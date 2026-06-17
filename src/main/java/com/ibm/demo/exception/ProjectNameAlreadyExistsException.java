package com.ibm.demo.exception;

/**
 * Exception thrown when attempting to create/update a project with a name that already exists.
 */
public class ProjectNameAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProjectNameAlreadyExistsException(String message) {
		super(message);
	}

	public ProjectNameAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}

// Made with Bob
