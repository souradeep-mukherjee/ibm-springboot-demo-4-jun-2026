package com.ibm.demo.service;

import java.util.List;

import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.dto.project.ProjectRequest;
import com.ibm.demo.dto.project.ProjectResponse;

/**
 * Service interface for Project operations.
 */
public interface ProjectService {

	List<ProjectResponse> getAllProjects();

	ProjectResponse getProjectById(String id);

	ProjectResponse getProjectByName(String name);

	ProjectResponse createProject(ProjectRequest requestDTO);

	ProjectResponse updateProject(String id, ProjectRequest requestDTO);

	void deleteProject(String id);

	List<EmployeeResponse> getEmployeesByProjectId(String projectId);

	ProjectResponse addEmployeeToProject(String projectId, String employeeId);

	ProjectResponse removeEmployeeFromProject(String projectId, String employeeId);
}

// Made with Bob
