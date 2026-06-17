package com.ibm.demo.dto.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Outgoing data sent to the client for Project.
 * Read-only response data with all relevant fields.
 */
public class ProjectResponse {

	private String id;
	private String name;
	private String description;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private List<String> employeeIds = new ArrayList<>();
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public ProjectResponse() {
	}

	public ProjectResponse(String id, String name, String description, LocalDateTime startDate, LocalDateTime endDate,
			List<String> employeeIds, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.employeeIds = employeeIds != null ? employeeIds : new ArrayList<>();
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public List<String> getEmployeeIds() {
		return employeeIds;
	}

	public void setEmployeeIds(List<String> employeeIds) {
		this.employeeIds = employeeIds;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "ProjectResponse [id=" + id + ", name=" + name + ", description=" + description + ", startDate="
				+ startDate + ", endDate=" + endDate + ", employeeIds=" + employeeIds + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
}

// Made with Bob
