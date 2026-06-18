package com.ibm.demo.service;

import java.util.List;

import com.ibm.demo.dto.department.DepartmentRequest;
import com.ibm.demo.dto.department.DepartmentResponse;
import com.ibm.demo.dto.employee.EmployeeResponse;

/**
 * Service interface for Department operations.
 */
public interface DepartmentService {

	List<DepartmentResponse> getAllDepartments();

	DepartmentResponse getDepartmentById(String id);

	DepartmentResponse getDepartmentByName(String name);

	DepartmentResponse createDepartment(DepartmentRequest requestDTO);

	DepartmentResponse updateDepartment(String id, DepartmentRequest requestDTO);

	void deleteDepartment(String id);

	List<EmployeeResponse> getEmployeesByDepartmentId(String departmentId);
}

// Made with Bob
