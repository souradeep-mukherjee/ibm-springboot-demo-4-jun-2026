package com.ibm.demo.dto.department;

import java.time.LocalDateTime;

/**
 * Outgoing data sent to the client for Department.
 * Read-only response data with all relevant fields.
 */
public class DepartmentResponse {

	private String id;
	private String name;
	private String location;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public DepartmentResponse() {
	}

	public DepartmentResponse(String id, String name, String location, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.location = location;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
		return "DepartmentResponse [id=" + id + ", name=" + name + ", location=" + location + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}
}

// Made with Bob
