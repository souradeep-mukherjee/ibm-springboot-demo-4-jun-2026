package com.ibm.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 8155190066651221431L;

	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
