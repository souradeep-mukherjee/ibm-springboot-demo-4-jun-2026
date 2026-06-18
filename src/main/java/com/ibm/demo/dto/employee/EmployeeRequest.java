package com.ibm.demo.dto.employee;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Incoming data from the client (POST / PUT request body). Carries all
 * validation constraints. No 'id' field — the client never sets the database
 * ID.
 */
public class EmployeeRequest {

	@NotBlank(message = "First name must not be blank")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;

	@NotBlank(message = "Last name must not be blank")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;

	@NotBlank(message = "Email must not be blank")
	@Email(message = "Email must be a valid email address")
	private String email;

	@NotNull(message = "Salary must not be null")
	@Positive(message = "Salary must be a positive value")
	private Double salary;

	private String departmentId;

	private List<String> projectIds = new ArrayList<>();

	public EmployeeRequest() {
	}

	public EmployeeRequest(String firstName, String lastName, String email, Double salary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.salary = salary;
	}

	public EmployeeRequest(String firstName, String lastName, String email, Double salary, String departmentId,
			List<String> projectIds) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.salary = salary;
		this.departmentId = departmentId;
		this.projectIds = projectIds != null ? projectIds : new ArrayList<>();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public List<String> getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(List<String> projectIds) {
		this.projectIds = projectIds;
	}

	@Override
	public String toString() {
		return "EmployeeRequest [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", salary="
				+ salary + ", departmentId=" + departmentId + ", projectIds=" + projectIds + "]";
	}
}
