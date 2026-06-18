package com.ibm.demo.mapper;

import com.ibm.demo.dto.project.ProjectRequest;
import com.ibm.demo.dto.project.ProjectResponse;
import com.ibm.demo.model.Project;

/**
 * Mapper for converting between Project entity and DTOs.
 */
public class ProjectMapper {

	private ProjectMapper() {
	}

	public static Project toEntity(ProjectRequest dto) {
		Project project = new Project();
		project.setName(dto.getName());
		project.setDescription(dto.getDescription());
		project.setStartDate(dto.getStartDate());
		project.setEndDate(dto.getEndDate());
		return project;
	}

	public static void updateEntity(Project existing, ProjectRequest dto) {
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setStartDate(dto.getStartDate());
		existing.setEndDate(dto.getEndDate());
	}

	public static ProjectResponse toResponseDTO(Project project) {
		return new ProjectResponse(project.getId(), project.getName(), project.getDescription(),
				project.getStartDate(), project.getEndDate(), project.getEmployeeIds(), project.getCreatedAt(),
				project.getUpdatedAt());
	}
}

// Made with Bob
