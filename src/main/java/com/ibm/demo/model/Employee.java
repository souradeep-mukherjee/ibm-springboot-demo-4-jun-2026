package com.ibm.demo.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "employees")
public class Employee {

	@Id
	private String id;

	@NotBlank(message = "First name must not be blank")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Field("first_name")
	private String firstName;

	@NotBlank(message = "Last name must not be blank")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	@Field("last_name")
	private String lastName;

	@NotBlank(message = "Email must not be blank")
	@Email(message = "Email must be a valid email address")
	@Indexed(unique = true)
	private String email;

	@NotNull(message = "Salary must not be null")
	@Positive(message = "Salary must be a positive value")
	private double salary;

	public Employee() {
	}

	public Employee(String firstName, String lastName, String email, double salary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.salary = salary;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", salary=" + salary + "]";
	}

}
