package com.ibm.demo.mapper;

import com.ibm.demo.dto.department.DepartmentRequest;
import com.ibm.demo.dto.department.DepartmentResponse;
import com.ibm.demo.model.Department;

/**
 * Mapper for converting between Department entity and DTOs.
 */
public class DepartmentMapper {

	private DepartmentMapper() {
	}

	public static Department toEntity(DepartmentRequest dto) {
		Department department = new Department();
		department.setName(dto.getName());
		department.setLocation(dto.getLocation());
		return department;
	}

	public static void updateEntity(Department existing, DepartmentRequest dto) {
		existing.setName(dto.getName());
		existing.setLocation(dto.getLocation());
	}

	public static DepartmentResponse toResponseDTO(Department department) {
		return new DepartmentResponse(department.getId(), department.getName(), department.getLocation(),
				department.getCreatedAt(), department.getUpdatedAt());
	}
}

// Made with Bob
