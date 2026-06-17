package com.ibm.demo.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.demo.dto.department.DepartmentRequest;
import com.ibm.demo.dto.department.DepartmentResponse;
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.service.DepartmentService;

/**
 * REST Controller for Department operations.
 */
@Validated
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@GetMapping
	public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
		return ResponseEntity.ok(departmentService.getAllDepartments());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable String id) {
		LOG.info("Fetching department with id: " + id);
		return ResponseEntity.ok(departmentService.getDepartmentById(id));
	}

	@GetMapping("/name")
	public ResponseEntity<DepartmentResponse> getDepartmentByName(
			@RequestParam @NotBlank(message = "Department name must not be blank") String name) {
		return ResponseEntity.ok(departmentService.getDepartmentByName(name));
	}

	@PostMapping
	public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest requestDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(requestDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable String id,
			@Valid @RequestBody DepartmentRequest requestDTO) {
		return ResponseEntity.ok(departmentService.updateDepartment(id, requestDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
		departmentService.deleteDepartment(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/employees")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartmentId(@PathVariable String id) {
		return ResponseEntity.ok(departmentService.getEmployeesByDepartmentId(id));
	}
}

// Made with Bob
