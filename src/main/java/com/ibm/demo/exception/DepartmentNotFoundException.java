package com.ibm.demo.exception;

/**
 * Exception thrown when a department is not found.
 */
public class DepartmentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DepartmentNotFoundException(String message) {
		super(message);
	}

	public DepartmentNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}

// Made with Bob
