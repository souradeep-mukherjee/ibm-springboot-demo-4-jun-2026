package com.ibm.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ibm.demo.dto.department.DepartmentRequest;
import com.ibm.demo.dto.department.DepartmentResponse;
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.exception.DepartmentNameAlreadyExistsException;
import com.ibm.demo.exception.DepartmentNotFoundException;
import com.ibm.demo.exception.ReferentialIntegrityException;
import com.ibm.demo.mapper.DepartmentMapper;
import com.ibm.demo.mapper.EmployeeMapper;
import com.ibm.demo.model.Department;
import com.ibm.demo.repository.DepartmentRepository;
import com.ibm.demo.repository.EmployeeRepository;
import com.ibm.demo.service.DepartmentService;

/**
 * Service implementation for Department operations.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

	private final DepartmentRepository departmentRepository;
	private final EmployeeRepository employeeRepository;

	public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
		this.departmentRepository = departmentRepository;
		this.employeeRepository = employeeRepository;
	}

	@Override
	public List<DepartmentResponse> getAllDepartments() {
		return departmentRepository.findAll().stream().map(DepartmentMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	public DepartmentResponse getDepartmentById(String id) {
		Department department = departmentRepository.findById(id)
				.orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
		return DepartmentMapper.toResponseDTO(department);
	}

	@Override
	public DepartmentResponse getDepartmentByName(String name) {
		Department department = departmentRepository.findByName(name)
				.orElseThrow(() -> new DepartmentNotFoundException("Department not found with name: " + name));
		return DepartmentMapper.toResponseDTO(department);
	}

	@Override
	public DepartmentResponse createDepartment(DepartmentRequest requestDTO) {
		if (departmentRepository.existsByName(requestDTO.getName())) {
			throw new DepartmentNameAlreadyExistsException(
					"A department with name " + requestDTO.getName() + " already exists");
		}
		Department department = DepartmentMapper.toEntity(requestDTO);
		Department saved = departmentRepository.save(department);
		return DepartmentMapper.toResponseDTO(saved);
	}

	@Override
	public DepartmentResponse updateDepartment(String id, DepartmentRequest requestDTO) {
		Department existing = departmentRepository.findById(id)
				.orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));

		if (!existing.getName().equals(requestDTO.getName()) && departmentRepository.existsByName(requestDTO.getName())) {
			throw new DepartmentNameAlreadyExistsException(
					"A department with name " + requestDTO.getName() + " already exists");
		}

		DepartmentMapper.updateEntity(existing, requestDTO);
		Department updated = departmentRepository.save(existing);
		return DepartmentMapper.toResponseDTO(updated);
	}

	@Override
	public void deleteDepartment(String id) {
		if (!departmentRepository.existsById(id)) {
			throw new DepartmentNotFoundException("Department not found with id: " + id);
		}

		// Check referential integrity - prevent deletion if employees are assigned
		if (employeeRepository.existsByDepartmentId(id)) {
			throw new ReferentialIntegrityException(
					"Cannot delete department with id " + id + " because it has employees assigned. "
							+ "Please reassign or remove employees first.");
		}

		departmentRepository.deleteById(id);
	}

	@Override
	public List<EmployeeResponse> getEmployeesByDepartmentId(String departmentId) {
		// Verify department exists
		if (!departmentRepository.existsById(departmentId)) {
			throw new DepartmentNotFoundException("Department not found with id: " + departmentId);
		}

		return employeeRepository.findByDepartmentId(departmentId).stream().map(EmployeeMapper::toResponseDTO)
				.collect(Collectors.toList());
	}
}

// Made with Bob
