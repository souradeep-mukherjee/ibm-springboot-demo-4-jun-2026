package com.ibm.demo.service;

import java.util.List;

import com.ibm.demo.model.Employee;

public interface EmployeeService {

	public abstract List<Employee> getAllEmployees();

	public abstract Employee getEmployeeById(String id);

	public abstract Employee getEmployeeByEmail(String email);

	public abstract List<Employee> getEmployeesByFirstName(String firstName);

	public abstract Employee createEmployee(Employee employee);

	public abstract Employee updateEmployee(String id, Employee employee);

	public abstract void deleteEmployee(String id);

}