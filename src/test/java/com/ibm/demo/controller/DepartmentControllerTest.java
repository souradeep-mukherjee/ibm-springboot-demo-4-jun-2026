package com.ibm.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.demo.dto.department.DepartmentRequest;
import com.ibm.demo.dto.department.DepartmentResponse;
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.exception.DepartmentNameAlreadyExistsException;
import com.ibm.demo.exception.DepartmentNotFoundException;
import com.ibm.demo.exception.ReferentialIntegrityException;
import com.ibm.demo.service.DepartmentService;

/**
 * Unit tests for DepartmentController using MockMvc.
 * Tests all REST endpoints with various scenarios including success and error cases.
 */
@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private DepartmentService departmentService;

	private DepartmentResponse departmentResponse;
	private DepartmentRequest departmentRequest;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
		
		departmentRequest = new DepartmentRequest("Engineering", "New York");
		
		departmentResponse = new DepartmentResponse(
			"dept123",
			"Engineering",
			"New York",
			now,
			now
		);
	}

	// ==================== GET /api/v1/departments ====================

	@Test
	@DisplayName("GET /api/v1/departments - Should return all departments")
	void testGetAllDepartments_Success() throws Exception {
		// Arrange
		DepartmentResponse dept2 = new DepartmentResponse(
			"dept456",
			"Sales",
			"Boston",
			now,
			now
		);
		List<DepartmentResponse> departments = Arrays.asList(departmentResponse, dept2);
		when(departmentService.getAllDepartments()).thenReturn(departments);

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("dept123"))
				.andExpect(jsonPath("$[0].name").value("Engineering"))
				.andExpect(jsonPath("$[0].location").value("New York"))
				.andExpect(jsonPath("$[1].id").value("dept456"))
				.andExpect(jsonPath("$[1].name").value("Sales"));

		verify(departmentService, times(1)).getAllDepartments();
	}

	@Test
	@DisplayName("GET /api/v1/departments - Should return empty list when no departments exist")
	void testGetAllDepartments_EmptyList() throws Exception {
		// Arrange
		when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(departmentService, times(1)).getAllDepartments();
	}

	// ==================== GET /api/v1/departments/{id} ====================

	@Test
	@DisplayName("GET /api/v1/departments/{id} - Should return department by ID")
	void testGetDepartmentById_Success() throws Exception {
		// Arrange
		when(departmentService.getDepartmentById("dept123")).thenReturn(departmentResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("dept123"))
				.andExpect(jsonPath("$.name").value("Engineering"))
				.andExpect(jsonPath("$.location").value("New York"));

		verify(departmentService, times(1)).getDepartmentById("dept123");
	}

	@Test
	@DisplayName("GET /api/v1/departments/{id} - Should return 404 when department not found")
	void testGetDepartmentById_NotFound() throws Exception {
		// Arrange
		when(departmentService.getDepartmentById("nonexistent"))
				.thenThrow(new DepartmentNotFoundException("Department not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(departmentService, times(1)).getDepartmentById("nonexistent");
	}

	// ==================== GET /api/v1/departments/name ====================

	@Test
	@DisplayName("GET /api/v1/departments/name - Should return department by name")
	void testGetDepartmentByName_Success() throws Exception {
		// Arrange
		when(departmentService.getDepartmentByName("Engineering")).thenReturn(departmentResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/name")
				.param("name", "Engineering")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("dept123"))
				.andExpect(jsonPath("$.name").value("Engineering"))
				.andExpect(jsonPath("$.location").value("New York"));

		verify(departmentService, times(1)).getDepartmentByName("Engineering");
	}

	@Test
	@DisplayName("GET /api/v1/departments/name - Should return 404 when department not found by name")
	void testGetDepartmentByName_NotFound() throws Exception {
		// Arrange
		when(departmentService.getDepartmentByName("NonExistent"))
				.thenThrow(new DepartmentNotFoundException("Department not found with name: NonExistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/name")
				.param("name", "NonExistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(departmentService, times(1)).getDepartmentByName("NonExistent");
	}

	@Test
	@DisplayName("GET /api/v1/departments/name - Should return 400 when name parameter is blank")
	void testGetDepartmentByName_BlankName() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/name")
				.param("name", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ==================== POST /api/v1/departments ====================

	@Test
	@DisplayName("POST /api/v1/departments - Should create department successfully")
	void testCreateDepartment_Success() throws Exception {
		// Arrange
		when(departmentService.createDepartment(any(DepartmentRequest.class)))
				.thenReturn(departmentResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(departmentRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("dept123"))
				.andExpect(jsonPath("$.name").value("Engineering"))
				.andExpect(jsonPath("$.location").value("New York"));

		verify(departmentService, times(1)).createDepartment(any(DepartmentRequest.class));
	}

	@Test
	@DisplayName("POST /api/v1/departments - Should return 400 when name is blank")
	void testCreateDepartment_BlankName() throws Exception {
		// Arrange
		DepartmentRequest invalidRequest = new DepartmentRequest("", "New York");

		// Act & Assert
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/departments - Should return 400 when location is blank")
	void testCreateDepartment_BlankLocation() throws Exception {
		// Arrange
		DepartmentRequest invalidRequest = new DepartmentRequest("Engineering", "");

		// Act & Assert
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/departments - Should return 400 when name is too short")
	void testCreateDepartment_NameTooShort() throws Exception {
		// Arrange
		DepartmentRequest invalidRequest = new DepartmentRequest("E", "New York");

		// Act & Assert
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/departments - Should return 409 when department name already exists")
	void testCreateDepartment_NameAlreadyExists() throws Exception {
		// Arrange
		when(departmentService.createDepartment(any(DepartmentRequest.class)))
				.thenThrow(new DepartmentNameAlreadyExistsException("Department with name 'Engineering' already exists"));

		// Act & Assert
		mockMvc.perform(post("/api/v1/departments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(departmentRequest)))
				.andExpect(status().isConflict());

		verify(departmentService, times(1)).createDepartment(any(DepartmentRequest.class));
	}

	// ==================== PUT /api/v1/departments/{id} ====================

	@Test
	@DisplayName("PUT /api/v1/departments/{id} - Should update department successfully")
	void testUpdateDepartment_Success() throws Exception {
		// Arrange
		DepartmentResponse updatedResponse = new DepartmentResponse(
			"dept123",
			"Engineering Updated",
			"Boston",
			now,
			LocalDateTime.now()
		);
		DepartmentRequest updateRequest = new DepartmentRequest("Engineering Updated", "Boston");
		
		when(departmentService.updateDepartment(eq("dept123"), any(DepartmentRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(put("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("dept123"))
				.andExpect(jsonPath("$.name").value("Engineering Updated"))
				.andExpect(jsonPath("$.location").value("Boston"));

		verify(departmentService, times(1)).updateDepartment(eq("dept123"), any(DepartmentRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/departments/{id} - Should return 404 when department not found")
	void testUpdateDepartment_NotFound() throws Exception {
		// Arrange
		when(departmentService.updateDepartment(eq("nonexistent"), any(DepartmentRequest.class)))
				.thenThrow(new DepartmentNotFoundException("Department not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(put("/api/v1/departments/nonexistent")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(departmentRequest)))
				.andExpect(status().isNotFound());

		verify(departmentService, times(1)).updateDepartment(eq("nonexistent"), any(DepartmentRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/departments/{id} - Should return 400 when request is invalid")
	void testUpdateDepartment_InvalidRequest() throws Exception {
		// Arrange
		DepartmentRequest invalidRequest = new DepartmentRequest("", "");

		// Act & Assert
		mockMvc.perform(put("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /api/v1/departments/{id} - Should return 409 when new name already exists")
	void testUpdateDepartment_NameAlreadyExists() throws Exception {
		// Arrange
		when(departmentService.updateDepartment(eq("dept123"), any(DepartmentRequest.class)))
				.thenThrow(new DepartmentNameAlreadyExistsException("Department with name 'Engineering' already exists"));

		// Act & Assert
		mockMvc.perform(put("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(departmentRequest)))
				.andExpect(status().isConflict());

		verify(departmentService, times(1)).updateDepartment(eq("dept123"), any(DepartmentRequest.class));
	}

	// ==================== DELETE /api/v1/departments/{id} ====================

	@Test
	@DisplayName("DELETE /api/v1/departments/{id} - Should delete department successfully")
	void testDeleteDepartment_Success() throws Exception {
		// Arrange
		doNothing().when(departmentService).deleteDepartment("dept123");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(departmentService, times(1)).deleteDepartment("dept123");
	}

	@Test
	@DisplayName("DELETE /api/v1/departments/{id} - Should return 404 when department not found")
	void testDeleteDepartment_NotFound() throws Exception {
		// Arrange
		doThrow(new DepartmentNotFoundException("Department not found with id: nonexistent"))
				.when(departmentService).deleteDepartment("nonexistent");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/departments/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(departmentService, times(1)).deleteDepartment("nonexistent");
	}

	@Test
	@DisplayName("DELETE /api/v1/departments/{id} - Should return 409 when department has employees")
	void testDeleteDepartment_HasEmployees() throws Exception {
		// Arrange
		doThrow(new ReferentialIntegrityException("Cannot delete department with existing employees"))
				.when(departmentService).deleteDepartment("dept123");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/departments/dept123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());

		verify(departmentService, times(1)).deleteDepartment("dept123");
	}

	// ==================== GET /api/v1/departments/{id}/employees ====================

	@Test
	@DisplayName("GET /api/v1/departments/{id}/employees - Should return employees by department ID")
	void testGetEmployeesByDepartmentId_Success() throws Exception {
		// Arrange
		EmployeeResponse emp1 = new EmployeeResponse(
			"emp1",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			now,
			now
		);
		EmployeeResponse emp2 = new EmployeeResponse(
			"emp2",
			"Jane",
			"Smith",
			"jane.smith@example.com",
			80000.0,
			now,
			now
		);
		List<EmployeeResponse> employees = Arrays.asList(emp1, emp2);
		
		when(departmentService.getEmployeesByDepartmentId("dept123")).thenReturn(employees);

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/dept123/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("emp1"))
				.andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[1].id").value("emp2"))
				.andExpect(jsonPath("$[1].firstName").value("Jane"));

		verify(departmentService, times(1)).getEmployeesByDepartmentId("dept123");
	}

	@Test
	@DisplayName("GET /api/v1/departments/{id}/employees - Should return empty list when no employees")
	void testGetEmployeesByDepartmentId_EmptyList() throws Exception {
		// Arrange
		when(departmentService.getEmployeesByDepartmentId("dept123")).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/dept123/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(departmentService, times(1)).getEmployeesByDepartmentId("dept123");
	}

	@Test
	@DisplayName("GET /api/v1/departments/{id}/employees - Should return 404 when department not found")
	void testGetEmployeesByDepartmentId_DepartmentNotFound() throws Exception {
		// Arrange
		when(departmentService.getEmployeesByDepartmentId("nonexistent"))
				.thenThrow(new DepartmentNotFoundException("Department not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/departments/nonexistent/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(departmentService, times(1)).getEmployeesByDepartmentId("nonexistent");
	}
}

// Made with Bob