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
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.dto.project.ProjectRequest;
import com.ibm.demo.dto.project.ProjectResponse;
import com.ibm.demo.exception.ProjectNameAlreadyExistsException;
import com.ibm.demo.exception.ProjectNotFoundException;
import com.ibm.demo.service.ProjectService;

/**
 * Unit tests for ProjectController using MockMvc.
 * Tests all REST endpoints with various scenarios including success and error cases.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProjectService projectService;

	private ProjectResponse projectResponse;
	private ProjectRequest projectRequest;
	private LocalDateTime now;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
		startDate = LocalDateTime.of(2026, 1, 1, 0, 0);
		endDate = LocalDateTime.of(2026, 12, 31, 23, 59);
		
		projectRequest = new ProjectRequest(
			"Project Alpha",
			"A strategic initiative for digital transformation",
			startDate,
			endDate
		);
		
		projectResponse = new ProjectResponse(
			"proj123",
			"Project Alpha",
			"A strategic initiative for digital transformation",
			startDate,
			endDate,
			new ArrayList<>(Arrays.asList("emp1", "emp2")),
			now,
			now
		);
	}

	// ==================== GET /api/v1/projects ====================

	@Test
	@DisplayName("GET /api/v1/projects - Should return all projects")
	void testGetAllProjects_Success() throws Exception {
		// Arrange
		ProjectResponse proj2 = new ProjectResponse(
			"proj456",
			"Project Beta",
			"Cloud migration project",
			startDate,
			endDate,
			new ArrayList<>(),
			now,
			now
		);
		List<ProjectResponse> projects = Arrays.asList(projectResponse, proj2);
		when(projectService.getAllProjects()).thenReturn(projects);

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("proj123"))
				.andExpect(jsonPath("$[0].name").value("Project Alpha"))
				.andExpect(jsonPath("$[0].description").value("A strategic initiative for digital transformation"))
				.andExpect(jsonPath("$[0].employeeIds").isArray())
				.andExpect(jsonPath("$[0].employeeIds.length()").value(2))
				.andExpect(jsonPath("$[1].id").value("proj456"))
				.andExpect(jsonPath("$[1].name").value("Project Beta"));

		verify(projectService, times(1)).getAllProjects();
	}

	@Test
	@DisplayName("GET /api/v1/projects - Should return empty list when no projects exist")
	void testGetAllProjects_EmptyList() throws Exception {
		// Arrange
		when(projectService.getAllProjects()).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(projectService, times(1)).getAllProjects();
	}

	// ==================== GET /api/v1/projects/{id} ====================

	@Test
	@DisplayName("GET /api/v1/projects/{id} - Should return project by ID")
	void testGetProjectById_Success() throws Exception {
		// Arrange
		when(projectService.getProjectById("proj123")).thenReturn(projectResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/proj123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.name").value("Project Alpha"))
				.andExpect(jsonPath("$.description").value("A strategic initiative for digital transformation"))
				.andExpect(jsonPath("$.employeeIds").isArray())
				.andExpect(jsonPath("$.employeeIds.length()").value(2));

		verify(projectService, times(1)).getProjectById("proj123");
	}

	@Test
	@DisplayName("GET /api/v1/projects/{id} - Should return 404 when project not found")
	void testGetProjectById_NotFound() throws Exception {
		// Arrange
		when(projectService.getProjectById("nonexistent"))
				.thenThrow(new ProjectNotFoundException("Project not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).getProjectById("nonexistent");
	}

	// ==================== GET /api/v1/projects/name ====================

	@Test
	@DisplayName("GET /api/v1/projects/name - Should return project by name")
	void testGetProjectByName_Success() throws Exception {
		// Arrange
		when(projectService.getProjectByName("Project Alpha")).thenReturn(projectResponse);

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/name")
				.param("name", "Project Alpha")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.name").value("Project Alpha"))
				.andExpect(jsonPath("$.description").value("A strategic initiative for digital transformation"));

		verify(projectService, times(1)).getProjectByName("Project Alpha");
	}

	@Test
	@DisplayName("GET /api/v1/projects/name - Should return 404 when project not found by name")
	void testGetProjectByName_NotFound() throws Exception {
		// Arrange
		when(projectService.getProjectByName("NonExistent"))
				.thenThrow(new ProjectNotFoundException("Project not found with name: NonExistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/name")
				.param("name", "NonExistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).getProjectByName("NonExistent");
	}

	@Test
	@DisplayName("GET /api/v1/projects/name - Should return 400 when name parameter is blank")
	void testGetProjectByName_BlankName() throws Exception {
		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/name")
				.param("name", "")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	// ==================== POST /api/v1/projects ====================

	@Test
	@DisplayName("POST /api/v1/projects - Should create project successfully")
	void testCreateProject_Success() throws Exception {
		// Arrange
		when(projectService.createProject(any(ProjectRequest.class)))
				.thenReturn(projectResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectRequest)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.name").value("Project Alpha"))
				.andExpect(jsonPath("$.description").value("A strategic initiative for digital transformation"));

		verify(projectService, times(1)).createProject(any(ProjectRequest.class));
	}

	@Test
	@DisplayName("POST /api/v1/projects - Should return 400 when name is blank")
	void testCreateProject_BlankName() throws Exception {
		// Arrange
		ProjectRequest invalidRequest = new ProjectRequest("", "Description", startDate, endDate);

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/projects - Should return 400 when name is too short")
	void testCreateProject_NameTooShort() throws Exception {
		// Arrange
		ProjectRequest invalidRequest = new ProjectRequest("P", "Description", startDate, endDate);

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("POST /api/v1/projects - Should create project with null description")
	void testCreateProject_NullDescription() throws Exception {
		// Arrange
		ProjectRequest requestWithNullDesc = new ProjectRequest("Project Alpha", null, startDate, endDate);
		ProjectResponse responseWithNullDesc = new ProjectResponse(
			"proj123",
			"Project Alpha",
			null,
			startDate,
			endDate,
			new ArrayList<>(),
			now,
			now
		);
		when(projectService.createProject(any(ProjectRequest.class)))
				.thenReturn(responseWithNullDesc);

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestWithNullDesc)))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.name").value("Project Alpha"));

		verify(projectService, times(1)).createProject(any(ProjectRequest.class));
	}

	@Test
	@DisplayName("POST /api/v1/projects - Should return 409 when project name already exists")
	void testCreateProject_NameAlreadyExists() throws Exception {
		// Arrange
		when(projectService.createProject(any(ProjectRequest.class)))
				.thenThrow(new ProjectNameAlreadyExistsException("Project with name 'Project Alpha' already exists"));

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectRequest)))
				.andExpect(status().isConflict());

		verify(projectService, times(1)).createProject(any(ProjectRequest.class));
	}

	// ==================== PUT /api/v1/projects/{id} ====================

	@Test
	@DisplayName("PUT /api/v1/projects/{id} - Should update project successfully")
	void testUpdateProject_Success() throws Exception {
		// Arrange
		ProjectResponse updatedResponse = new ProjectResponse(
			"proj123",
			"Project Alpha Updated",
			"Updated description",
			startDate,
			endDate,
			new ArrayList<>(),
			now,
			LocalDateTime.now()
		);
		ProjectRequest updateRequest = new ProjectRequest("Project Alpha Updated", "Updated description", startDate, endDate);
		
		when(projectService.updateProject(eq("proj123"), any(ProjectRequest.class)))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(put("/api/v1/projects/proj123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.name").value("Project Alpha Updated"))
				.andExpect(jsonPath("$.description").value("Updated description"));

		verify(projectService, times(1)).updateProject(eq("proj123"), any(ProjectRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/projects/{id} - Should return 404 when project not found")
	void testUpdateProject_NotFound() throws Exception {
		// Arrange
		when(projectService.updateProject(eq("nonexistent"), any(ProjectRequest.class)))
				.thenThrow(new ProjectNotFoundException("Project not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(put("/api/v1/projects/nonexistent")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectRequest)))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).updateProject(eq("nonexistent"), any(ProjectRequest.class));
	}

	@Test
	@DisplayName("PUT /api/v1/projects/{id} - Should return 400 when request is invalid")
	void testUpdateProject_InvalidRequest() throws Exception {
		// Arrange
		ProjectRequest invalidRequest = new ProjectRequest("", null, null, null);

		// Act & Assert
		mockMvc.perform(put("/api/v1/projects/proj123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("PUT /api/v1/projects/{id} - Should return 409 when new name already exists")
	void testUpdateProject_NameAlreadyExists() throws Exception {
		// Arrange
		when(projectService.updateProject(eq("proj123"), any(ProjectRequest.class)))
				.thenThrow(new ProjectNameAlreadyExistsException("Project with name 'Project Alpha' already exists"));

		// Act & Assert
		mockMvc.perform(put("/api/v1/projects/proj123")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(projectRequest)))
				.andExpect(status().isConflict());

		verify(projectService, times(1)).updateProject(eq("proj123"), any(ProjectRequest.class));
	}

	// ==================== DELETE /api/v1/projects/{id} ====================

	@Test
	@DisplayName("DELETE /api/v1/projects/{id} - Should delete project successfully")
	void testDeleteProject_Success() throws Exception {
		// Arrange
		doNothing().when(projectService).deleteProject("proj123");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/projects/proj123")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

		verify(projectService, times(1)).deleteProject("proj123");
	}

	@Test
	@DisplayName("DELETE /api/v1/projects/{id} - Should return 404 when project not found")
	void testDeleteProject_NotFound() throws Exception {
		// Arrange
		doThrow(new ProjectNotFoundException("Project not found with id: nonexistent"))
				.when(projectService).deleteProject("nonexistent");

		// Act & Assert
		mockMvc.perform(delete("/api/v1/projects/nonexistent")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).deleteProject("nonexistent");
	}

	// ==================== GET /api/v1/projects/{id}/employees ====================

	@Test
	@DisplayName("GET /api/v1/projects/{id}/employees - Should return employees by project ID")
	void testGetEmployeesByProjectId_Success() throws Exception {
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
		
		when(projectService.getEmployeesByProjectId("proj123")).thenReturn(employees);

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/proj123/employees")
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

		verify(projectService, times(1)).getEmployeesByProjectId("proj123");
	}

	@Test
	@DisplayName("GET /api/v1/projects/{id}/employees - Should return empty list when no employees")
	void testGetEmployeesByProjectId_EmptyList() throws Exception {
		// Arrange
		when(projectService.getEmployeesByProjectId("proj123")).thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/proj123/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));

		verify(projectService, times(1)).getEmployeesByProjectId("proj123");
	}

	@Test
	@DisplayName("GET /api/v1/projects/{id}/employees - Should return 404 when project not found")
	void testGetEmployeesByProjectId_ProjectNotFound() throws Exception {
		// Arrange
		when(projectService.getEmployeesByProjectId("nonexistent"))
				.thenThrow(new ProjectNotFoundException("Project not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(get("/api/v1/projects/nonexistent/employees")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).getEmployeesByProjectId("nonexistent");
	}

	// ==================== POST /api/v1/projects/{projectId}/employees/{employeeId} ====================

	@Test
	@DisplayName("POST /api/v1/projects/{projectId}/employees/{employeeId} - Should add employee to project")
	void testAddEmployeeToProject_Success() throws Exception {
		// Arrange
		ProjectResponse updatedResponse = new ProjectResponse(
			"proj123",
			"Project Alpha",
			"A strategic initiative for digital transformation",
			startDate,
			endDate,
			new ArrayList<>(Arrays.asList("emp1", "emp2", "emp3")),
			now,
			LocalDateTime.now()
		);
		
		when(projectService.addEmployeeToProject("proj123", "emp3"))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects/proj123/employees/emp3")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.employeeIds").isArray())
				.andExpect(jsonPath("$.employeeIds.length()").value(3))
				.andExpect(jsonPath("$.employeeIds[2]").value("emp3"));

		verify(projectService, times(1)).addEmployeeToProject("proj123", "emp3");
	}

	@Test
	@DisplayName("POST /api/v1/projects/{projectId}/employees/{employeeId} - Should return 404 when project not found")
	void testAddEmployeeToProject_ProjectNotFound() throws Exception {
		// Arrange
		when(projectService.addEmployeeToProject("nonexistent", "emp1"))
				.thenThrow(new ProjectNotFoundException("Project not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(post("/api/v1/projects/nonexistent/employees/emp1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).addEmployeeToProject("nonexistent", "emp1");
	}

	// ==================== DELETE /api/v1/projects/{projectId}/employees/{employeeId} ====================

	@Test
	@DisplayName("DELETE /api/v1/projects/{projectId}/employees/{employeeId} - Should remove employee from project")
	void testRemoveEmployeeFromProject_Success() throws Exception {
		// Arrange
		ProjectResponse updatedResponse = new ProjectResponse(
			"proj123",
			"Project Alpha",
			"A strategic initiative for digital transformation",
			startDate,
			endDate,
			new ArrayList<>(Arrays.asList("emp2")),
			now,
			LocalDateTime.now()
		);
		
		when(projectService.removeEmployeeFromProject("proj123", "emp1"))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(delete("/api/v1/projects/proj123/employees/emp1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.employeeIds").isArray())
				.andExpect(jsonPath("$.employeeIds.length()").value(1))
				.andExpect(jsonPath("$.employeeIds[0]").value("emp2"));

		verify(projectService, times(1)).removeEmployeeFromProject("proj123", "emp1");
	}

	@Test
	@DisplayName("DELETE /api/v1/projects/{projectId}/employees/{employeeId} - Should return 404 when project not found")
	void testRemoveEmployeeFromProject_ProjectNotFound() throws Exception {
		// Arrange
		when(projectService.removeEmployeeFromProject("nonexistent", "emp1"))
				.thenThrow(new ProjectNotFoundException("Project not found with id: nonexistent"));

		// Act & Assert
		mockMvc.perform(delete("/api/v1/projects/nonexistent/employees/emp1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

		verify(projectService, times(1)).removeEmployeeFromProject("nonexistent", "emp1");
	}

	@Test
	@DisplayName("DELETE /api/v1/projects/{projectId}/employees/{employeeId} - Should handle removing all employees")
	void testRemoveEmployeeFromProject_LastEmployee() throws Exception {
		// Arrange
		ProjectResponse updatedResponse = new ProjectResponse(
			"proj123",
			"Project Alpha",
			"A strategic initiative for digital transformation",
			startDate,
			endDate,
			new ArrayList<>(),
			now,
			LocalDateTime.now()
		);
		
		when(projectService.removeEmployeeFromProject("proj123", "emp1"))
				.thenReturn(updatedResponse);

		// Act & Assert
		mockMvc.perform(delete("/api/v1/projects/proj123/employees/emp1")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value("proj123"))
				.andExpect(jsonPath("$.employeeIds").isArray())
				.andExpect(jsonPath("$.employeeIds.length()").value(0));

		verify(projectService, times(1)).removeEmployeeFromProject("proj123", "emp1");
	}
}

// Made with Bob