package com.ibm.demo.dto.project;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming data from the client for Project creation/update.
 * Carries validation constraints. No 'id' field.
 */
public class ProjectRequest {

	@NotBlank(message = "Project name must not be blank")
	@Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
	private String name;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	private String description;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	public ProjectRequest() {
	}

	public ProjectRequest(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
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

	@Override
	public String toString() {
		return "ProjectRequest [name=" + name + ", description=" + description + ", startDate=" + startDate
				+ ", endDate=" + endDate + "]";
	}
}

// Made with Bob
