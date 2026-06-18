package com.ibm.demo.exception;

/**
 * Exception thrown when attempting to create/update a department with a name that already exists.
 */
public class DepartmentNameAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DepartmentNameAlreadyExistsException(String message) {
		super(message);
	}

	public DepartmentNameAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}
}

// Made with Bob
