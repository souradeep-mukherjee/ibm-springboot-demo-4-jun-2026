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

import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.dto.project.ProjectRequest;
import com.ibm.demo.dto.project.ProjectResponse;
import com.ibm.demo.service.ProjectService;

/**
 * REST Controller for Project operations.
 */
@Validated
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@GetMapping
	public ResponseEntity<List<ProjectResponse>> getAllProjects() {
		return ResponseEntity.ok(projectService.getAllProjects());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProjectResponse> getProjectById(@PathVariable String id) {
		LOG.info("Fetching project with id: " + id);
		return ResponseEntity.ok(projectService.getProjectById(id));
	}

	@GetMapping("/name")
	public ResponseEntity<ProjectResponse> getProjectByName(
			@RequestParam @NotBlank(message = "Project name must not be blank") String name) {
		return ResponseEntity.ok(projectService.getProjectByName(name));
	}

	@PostMapping
	public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest requestDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(requestDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProjectResponse> updateProject(@PathVariable String id,
			@Valid @RequestBody ProjectRequest requestDTO) {
		return ResponseEntity.ok(projectService.updateProject(id, requestDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProject(@PathVariable String id) {
		projectService.deleteProject(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/employees")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByProjectId(@PathVariable String id) {
		return ResponseEntity.ok(projectService.getEmployeesByProjectId(id));
	}

	@PostMapping("/{projectId}/employees/{employeeId}")
	public ResponseEntity<ProjectResponse> addEmployeeToProject(@PathVariable String projectId,
			@PathVariable String employeeId) {
		return ResponseEntity.ok(projectService.addEmployeeToProject(projectId, employeeId));
	}

	@DeleteMapping("/{projectId}/employees/{employeeId}")
	public ResponseEntity<ProjectResponse> removeEmployeeFromProject(@PathVariable String projectId,
			@PathVariable String employeeId) {
		return ResponseEntity.ok(projectService.removeEmployeeFromProject(projectId, employeeId));
	}
}

// Made with Bob
