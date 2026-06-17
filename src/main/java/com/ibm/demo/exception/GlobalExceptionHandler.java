package com.ibm.demo.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(EmployeeNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(DepartmentNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleDepartmentNotFound(DepartmentNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(ProjectNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleProjectNotFound(ProjectNotFoundException ex) {
		return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleEmailConflict(EmailAlreadyExistsException ex) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(DepartmentNameAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleDepartmentNameConflict(DepartmentNameAlreadyExistsException ex) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ProjectNameAlreadyExistsException.class)
	public ResponseEntity<Map<String, Object>> handleProjectNameConflict(ProjectNameAlreadyExistsException ex) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ReferentialIntegrityException.class)
	public ResponseEntity<Map<String, Object>> handleReferentialIntegrity(ReferentialIntegrityException ex) {
		return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage()).collect(Collectors.joining(", "));
		return buildResponse(HttpStatus.BAD_REQUEST, errors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
		String errors = ex.getConstraintViolations().stream().map(cv -> {
			String path = cv.getPropertyPath().toString();
			String param = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
			return param + ": " + cv.getMessage();
		}).collect(Collectors.joining(", "));
		return buildResponse(HttpStatus.BAD_REQUEST, errors);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
	}

	private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		return ResponseEntity.status(status).body(body);
	}
}

//package com.ibm.demo.exception;
//
//import java.time.LocalDateTime;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(EmployeeNotFoundException.class)
//    public ResponseEntity<Map<String, Object>> handleNotFound(EmployeeNotFoundException ex) {
//        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
//    }
//
//    @ExceptionHandler(EmailAlreadyExistsException.class)
//    public ResponseEntity<Map<String, Object>> handleConflict(EmailAlreadyExistsException ex) {
//        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
//    }
//
//    // Handles @Valid failures on request body
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
//        String errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
//                .collect(Collectors.joining(", "));
//        return buildResponse(HttpStatus.BAD_REQUEST, errors);
//    }
//
//    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("timestamp", LocalDateTime.now().toString());
//        body.put("status", status.value());
//        body.put("error", status.getReasonPhrase());
//        body.put("message", message);
//        return ResponseEntity.status(status).body(body);
//    }
//}
