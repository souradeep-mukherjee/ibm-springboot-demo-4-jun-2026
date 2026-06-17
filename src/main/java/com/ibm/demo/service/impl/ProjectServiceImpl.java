package com.ibm.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.dto.project.ProjectRequest;
import com.ibm.demo.dto.project.ProjectResponse;
import com.ibm.demo.exception.EmployeeNotFoundException;
import com.ibm.demo.exception.ProjectNameAlreadyExistsException;
import com.ibm.demo.exception.ProjectNotFoundException;
import com.ibm.demo.exception.ReferentialIntegrityException;
import com.ibm.demo.mapper.EmployeeMapper;
import com.ibm.demo.mapper.ProjectMapper;
import com.ibm.demo.model.Employee;
import com.ibm.demo.model.Project;
import com.ibm.demo.repository.EmployeeRepository;
import com.ibm.demo.repository.ProjectRepository;
import com.ibm.demo.service.ProjectService;

/**
 * Service implementation for Project operations with bidirectional relationship management.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;

	public ProjectServiceImpl(ProjectRepository projectRepository, EmployeeRepository employeeRepository) {
		this.projectRepository = projectRepository;
		this.employeeRepository = employeeRepository;
	}

	@Override
	public List<ProjectResponse> getAllProjects() {
		return projectRepository.findAll().stream().map(ProjectMapper::toResponseDTO).collect(Collectors.toList());
	}

	@Override
	public ProjectResponse getProjectById(String id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
		return ProjectMapper.toResponseDTO(project);
	}

	@Override
	public ProjectResponse getProjectByName(String name) {
		Project project = projectRepository.findByName(name)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found with name: " + name));
		return ProjectMapper.toResponseDTO(project);
	}

	@Override
	public ProjectResponse createProject(ProjectRequest requestDTO) {
		if (projectRepository.existsByName(requestDTO.getName())) {
			throw new ProjectNameAlreadyExistsException(
					"A project with name " + requestDTO.getName() + " already exists");
		}
		Project project = ProjectMapper.toEntity(requestDTO);
		Project saved = projectRepository.save(project);
		return ProjectMapper.toResponseDTO(saved);
	}

	@Override
	public ProjectResponse updateProject(String id, ProjectRequest requestDTO) {
		Project existing = projectRepository.findById(id)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

		if (!existing.getName().equals(requestDTO.getName()) && projectRepository.existsByName(requestDTO.getName())) {
			throw new ProjectNameAlreadyExistsException(
					"A project with name " + requestDTO.getName() + " already exists");
		}

		ProjectMapper.updateEntity(existing, requestDTO);
		Project updated = projectRepository.save(existing);
		return ProjectMapper.toResponseDTO(updated);
	}

	@Override
	public void deleteProject(String id) {
		if (!projectRepository.existsById(id)) {
			throw new ProjectNotFoundException("Project not found with id: " + id);
		}

		// Check referential integrity - prevent deletion if employees are assigned
		if (employeeRepository.existsByProjectIdsContaining(id)) {
			throw new ReferentialIntegrityException("Cannot delete project with id " + id
					+ " because it has employees assigned. " + "Please remove employees from this project first.");
		}

		projectRepository.deleteById(id);
	}

	@Override
	public List<EmployeeResponse> getEmployeesByProjectId(String projectId) {
		// Verify project exists
		if (!projectRepository.existsById(projectId)) {
			throw new ProjectNotFoundException("Project not found with id: " + projectId);
		}

		return employeeRepository.findByProjectIdsContaining(projectId).stream().map(EmployeeMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	public ProjectResponse addEmployeeToProject(String projectId, String employeeId) {
		// Verify project exists
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

		// Verify employee exists
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

		// Add employee to project if not already present
		if (!project.getEmployeeIds().contains(employeeId)) {
			project.getEmployeeIds().add(employeeId);
			projectRepository.save(project);
		}

		// Add project to employee if not already present (bidirectional sync)
		if (!employee.getProjectIds().contains(projectId)) {
			employee.getProjectIds().add(projectId);
			employeeRepository.save(employee);
		}

		return ProjectMapper.toResponseDTO(project);
	}

	@Override
	public ProjectResponse removeEmployeeFromProject(String projectId, String employeeId) {
		// Verify project exists
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

		// Verify employee exists
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

		// Remove employee from project
		project.getEmployeeIds().remove(employeeId);
		projectRepository.save(project);

		// Remove project from employee (bidirectional sync)
		employee.getProjectIds().remove(projectId);
		employeeRepository.save(employee);

		return ProjectMapper.toResponseDTO(project);
	}
}

// Made with Bob
