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
import java.util.ArrayList;
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
import com.ibm.demo.dto.employee.EmployeeRequest;
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.exception.EmailAlreadyExistsException;
import com.ibm.demo.exception.EmployeeNotFoundException;
import com.ibm.demo.service.EmployeeService;

/**
 * Unit tests for EmployeeController using MockMvc.
 * Tests all REST endpoints with various scenarios including success and error cases.
 */
@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private EmployeeService employeeService;

	private EmployeeResponse employeeResponse;
	private EmployeeRequest employeeRequest;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
		
		employeeRequest = new EmployeeRequest(
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept123",
			new ArrayList<>(Arrays.asList("proj1", "proj2"))
		);
		
		employeeResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept123",
			new ArrayList<>(Arrays.asList("proj1", "proj2")),
			now,
			now
		);
	}

	// ==================== GET /api/v1/employees ====================

	@Test
	@DisplayName("GET /api/v1/employees - Should return all employees")
	void testGetAllEmployees_Success() throws Exception {
		// Arrange
		EmployeeResponse emp2 = new EmployeeResponse(
			"emp456",
			"Jane",
			"Smith",
			"jane.smith@example.com",
			80000.0,
			"dept123",
			new ArrayList<>(),
			now,
			now
		);
		List<EmployeeResponse> employees = Arrays.asList(employeeResponse, emp2);
		when(employeeService.getAllEmployees()).thenReturn(employees);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("emp123"))
				.andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[0].lastName").value("Doe"))
				.andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
				.andExpect(jsonPath("$[0].salary").value(75000.0))
				.andExpect(jsonPath("$[1].id").value("emp456"))
				.andExpect(jsonPath("$[1].firstName").value("Jane"));

		verify(employeeService, times(1)).getAllEmployees();
	}

	@Test
	@DisplayName("GET /api/v1/employees - Should return empty list when no employees exist")
	void testGetAllEmployees_EmptyList() throws Exception {
		// Arrange
		when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(employeeService, times(1)).getAllEmployees();
	}

	// ==================== GET /api/v1/employees/{id} ====================

	@Test
	@DisplayName("GET /api/v1/employees/{id} - Should return employee by ID")
	void testGetEmployeeById_Success() throws Exception {
		// Arrange
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/emp123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.email").value("john.doe@example.com"))
				.andExpect(jsonPath("$.salary").value(75000.0))
				.andExpect(jsonPath("$.departmentId").value("dept123"))
				.andExpect(jsonPath("$.projectIds").isArray())
				.andExpect(jsonPath("$.projectIds.length()").value(2));

		verify(employeeService, times(1)).getEmployeeById("emp123");
	}

	@Test
	@DisplayName("GET /api/v1/employees/{id} - Should return 404 when employee not found")
	void testGetEmployeeById_NotFound() throws Exception {
		// Arrange
		when(employeeService.getEmployeeById("nonexistent"))
				.thenThrow(new EmployeeNotFoundException("Employee not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).getEmployeeById("nonexistent");
	}

	// ==================== GET /api/v1/employees/email ====================

	@Test
	@DisplayName("GET /api/v1/employees/email - Should return employee by email")
	void testGetEmployeeByEmail_Success() throws Exception {
		// Arrange
		when(employeeService.getEmployeeByEmail("john.doe@example.com")).thenReturn(employeeResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/email")
				.param("email", "john.doe@example.com")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.email").value("john.doe@example.com"));

		verify(employeeService, times(1)).getEmployeeByEmail("john.doe@example.com");
	}

	@Test
	@DisplayName("GET /api/v1/employees/email - Should return 404 when employee not found by email")
	void testGetEmployeeByEmail_NotFound() throws Exception {
		// Arrange
		when(employeeService.getEmployeeByEmail("nonexistent@example.com"))
				.thenThrow(new EmployeeNotFoundException("Employee not found with email: nonexistent@example.com"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/email")
				.param("email", "nonexistent@example.com")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).getEmployeeByEmail("nonexistent@example.com");
	}

	@Test
	@DisplayName("GET /api/v1/employees/email - Should return 400 when email is blank")
	void testGetEmployeeByEmail_BlankEmail() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/email")
				.param("email", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET /api/v1/employees/email - Should return 400 when email is invalid")
	void testGetEmployeeByEmail_InvalidEmail() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/email")
				.param("email", "invalid-email")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ==================== GET /api/v1/employees/search ====================

	@Test
	@DisplayName("GET /api/v1/employees/search - Should return employees by first name")
	void testGetEmployeesByFirstName_Success() throws Exception {
		// Arrange
		EmployeeResponse emp2 = new EmployeeResponse(
			"emp456",
			"John",
			"Smith",
			"john.smith@example.com",
			70000.0,
			null,
			new ArrayList<>(),
			now,
			now
		);
		List<EmployeeResponse> employees = Arrays.asList(employeeResponse, emp2);
		when(employeeService.getEmployeesByFirstName("John")).thenReturn(employees);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/search")
				.param("firstName", "John")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].firstName").value("John"))
				.andExpect(jsonPath("$[1].firstName").value("John"));

		verify(employeeService, times(1)).getEmployeesByFirstName("John");
	}

	@Test
	@DisplayName("GET /api/v1/employees/search - Should return empty list when no matches")
	void testGetEmployeesByFirstName_EmptyList() throws Exception {
		// Arrange
		when(employeeService.getEmployeesByFirstName("NonExistent")).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/search")
				.param("firstName", "NonExistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(employeeService, times(1)).getEmployeesByFirstName("NonExistent");
	}

	@Test
	@DisplayName("GET /api/v1/employees/search - Should return 400 when firstName is blank")
	void testGetEmployeesByFirstName_BlankName() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/search")
				.param("firstName", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("GET /api/v1/employees/search - Should return 400 when firstName is too short")
	void testGetEmployeesByFirstName_TooShort() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/search")
				.param("firstName", "J")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ==================== GET /api/v1/employees/search/{firstName} ====================

	@Test
	@DisplayName("GET /api/v1/employees/search/{firstName} - Should return employees by first name path variable")
	void testGetEmployeesByFirstNamePath_Success() throws Exception {
		// Arrange
		List<EmployeeResponse> employees = Arrays.asList(employeeResponse);
		when(employeeService.getEmployeesByFirstName("John")).thenReturn(employees);

		// Act & Assert
		mockMvc.perform(get("/api/v1/employees/search/John")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].firstName").value("John"));

		verify(employeeService, times(1)).getEmployeesByFirstName("John");
	}

	// ==================== POST /api/v1/employees ====================

	@Test
	@DisplayName("POST /api/v1/employees - Should create employee successfully")
	void testCreateEmployee_Success() throws Exception {
		// Arrange
		when(employeeService.createEmployee(any(EmployeeRequest.class)))
				.thenReturn(employeeResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.firstName").value("John"))
				.andExpect(jsonPath("$.lastName").value("Doe"))
				.andExpect(jsonPath("$.email").value("john.doe@example.com"))
				.andExpect(jsonPath("$.salary").value(75000.0));

		verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when firstName is blank")
	void testCreateEmployee_BlankFirstName() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("", "Doe", "john.doe@example.com", 75000.0);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when lastName is blank")
	void testCreateEmployee_BlankLastName() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("John", "", "john.doe@example.com", 75000.0);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when email is blank")
	void testCreateEmployee_BlankEmail() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("John", "Doe", "", 75000.0);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when email is invalid")
	void testCreateEmployee_InvalidEmail() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("John", "Doe", "invalid-email", 75000.0);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when salary is null")
	void testCreateEmployee_NullSalary() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("John", "Doe", "john.doe@example.com", null);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 400 when salary is negative")
	void testCreateEmployee_NegativeSalary() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("John", "Doe", "john.doe@example.com", -1000.0);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/employees - Should return 409 when email already exists")
	void testCreateEmployee_EmailAlreadyExists() throws Exception {
		// Arrange
		when(employeeService.createEmployee(any(EmployeeRequest.class)))
				.thenThrow(new EmailAlreadyExistsException("Employee with email 'john.doe@example.com' already exists"));

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeRequest)))
				.andExpect(status().isConflict());

		verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
	}

	// ==================== PUT /api/v1/employees/{id} ====================

	@Test
	@DisplayName("PUT /api/v1/employees/{id} - Should update employee successfully")
	void testUpdateEmployee_Success() throws Exception {
		// Arrange
		EmployeeResponse updatedResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe Updated",
			"john.doe@example.com",
			85000.0,
			"dept123",
			new ArrayList<>(),
			now,
			LocalDateTime.now()
		);
		EmployeeRequest updateRequest = new EmployeeRequest("John", "Doe Updated", "john.doe@example.com", 85000.0);
		
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(put("/api/v1/employees/emp123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.lastName").value("Doe Updated"))
				.andExpect(jsonPath("$.salary").value(85000.0));

		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/employees/{id} - Should return 404 when employee not found")
	void testUpdateEmployee_NotFound() throws Exception {
		// Arrange
		when(employeeService.updateEmployee(eq("nonexistent"), any(EmployeeRequest.class)))
				.thenThrow(new EmployeeNotFoundException("Employee not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(put("/api/v1/employees/nonexistent")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(employeeRequest)))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).updateEmployee(eq("nonexistent"), any(EmployeeRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/employees/{id} - Should return 400 when request is invalid")
	void testUpdateEmployee_InvalidRequest() throws Exception {
		// Arrange
		EmployeeRequest invalidRequest = new EmployeeRequest("", "", "", null);

		// Act & Assert
		mockMvc.perform(put("/api/v1/employees/emp123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	// ==================== DELETE /api/v1/employees/{id} ====================

	@Test
	@DisplayName("DELETE /api/v1/employees/{id} - Should delete employee successfully")
	void testDeleteEmployee_Success() throws Exception {
		// Arrange
		doNothing().when(employeeService).deleteEmployee("emp123");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/employees/emp123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(employeeService, times(1)).deleteEmployee("emp123");
	}

	@Test
	@DisplayName("DELETE /api/v1/employees/{id} - Should return 404 when employee not found")
	void testDeleteEmployee_NotFound() throws Exception {
		// Arrange
		doThrow(new EmployeeNotFoundException("Employee not found with id: nonexistent"))
				.when(employeeService).deleteEmployee("nonexistent");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/employees/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(employeeService, times(1)).deleteEmployee("nonexistent");
	}

	// ==================== PUT /api/v1/employees/{id}/department/{departmentId} ====================

	@Test
	@DisplayName("PUT /api/v1/employees/{id}/department/{departmentId} - Should assign employee to department")
	void testAssignEmployeeToDepartment_Success() throws Exception {
		// Arrange
		EmployeeResponse updatedResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept456",
			new ArrayList<>(),
			now,
			LocalDateTime.now()
		);
		
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(put("/api/v1/employees/emp123/department/dept456")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.departmentId").value("dept456"));

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	// ==================== DELETE /api/v1/employees/{id}/department ====================

	@Test
	@DisplayName("DELETE /api/v1/employees/{id}/department - Should remove employee from department")
	void testRemoveEmployeeFromDepartment_Success() throws Exception {
		// Arrange
		EmployeeResponse updatedResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			null,
			new ArrayList<>(),
			now,
			LocalDateTime.now()
		);
		
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(delete("/api/v1/employees/emp123/department")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.departmentId").isEmpty());

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	// ==================== POST /api/v1/employees/{id}/projects/{projectId} ====================

	@Test
	@DisplayName("POST /api/v1/employees/{id}/projects/{projectId} - Should add employee to project")
	void testAddEmployeeToProject_Success() throws Exception {
		// Arrange
		EmployeeResponse empWithoutProject = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept123",
			new ArrayList<>(),
			now,
			now
		);
		
		EmployeeResponse updatedResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept123",
			new ArrayList<>(Arrays.asList("proj999")),
			now,
			LocalDateTime.now()
		);
		
		when(employeeService.getEmployeeById("emp123")).thenReturn(empWithoutProject);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees/emp123/projects/proj999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.projectIds").isArray())
				.andExpect(jsonPath("$.projectIds[0]").value("proj999"));

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	@Test
	@DisplayName("POST /api/v1/employees/{id}/projects/{projectId} - Should not add duplicate project")
	void testAddEmployeeToProject_AlreadyExists() throws Exception {
		// Arrange - employee already has proj1
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(employeeResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/employees/emp123/projects/proj1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.projectIds.length()").value(2));

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	// ==================== DELETE /api/v1/employees/{id}/projects/{projectId} ====================

	@Test
	@DisplayName("DELETE /api/v1/employees/{id}/projects/{projectId} - Should remove employee from project")
	void testRemoveEmployeeFromProject_Success() throws Exception {
		// Arrange
		EmployeeResponse updatedResponse = new EmployeeResponse(
			"emp123",
			"John",
			"Doe",
			"john.doe@example.com",
			75000.0,
			"dept123",
			new ArrayList<>(Arrays.asList("proj2")),
			now,
			LocalDateTime.now()
		);
		
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(delete("/api/v1/employees/emp123/projects/proj1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("emp123"))
				.andExpect(jsonPath("$.projectIds").isArray())
				.andExpect(jsonPath("$.projectIds.length()").value(1))
				.andExpect(jsonPath("$.projectIds[0]").value("proj2"));

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}

	@Test
	@DisplayName("DELETE /api/v1/employees/{id}/projects/{projectId} - Should handle removing non-existent project")
	void testRemoveEmployeeFromProject_ProjectNotInList() throws Exception {
		// Arrange
		when(employeeService.getEmployeeById("emp123")).thenReturn(employeeResponse);
		when(employeeService.updateEmployee(eq("emp123"), any(EmployeeRequest.class)))
				.thenReturn(employeeResponse);

		// Act & Assert
		mockMvc.perform(delete("/api/v1/employees/emp123/projects/proj999")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.projectIds.length()").value(2));

		verify(employeeService, times(1)).getEmployeeById("emp123");
		verify(employeeService, times(1)).updateEmployee(eq("emp123"), any(EmployeeRequest.class));
	}
}

// Made with Bob