package com.ibm.demo.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.demo.model.Employee;
import com.ibm.demo.service.EmployeeService;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

//    http://localhost:8080/api/v1/employees 

	@GetMapping
	public List<Employee> getAllEmployees() {
		return employeeService.getAllEmployees();
	}

	@GetMapping("/{id}")
	public Employee getEmployeeById(@PathVariable String id) {
		return employeeService.getEmployeeById(id);
	}

	@GetMapping("/email/{email}")
	public Employee getEmployeeByEmail(@PathVariable String email) {
		return employeeService.getEmployeeByEmail(email);
	}

	@GetMapping("/search")
	public List<Employee> getEmployeesByFirstName(@RequestParam String firstName) {
		return employeeService.getEmployeesByFirstName(firstName);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Employee createEmployee(@Valid @RequestBody Employee employee) {
		return employeeService.createEmployee(employee);
	}

	@PutMapping("/{id}")
	public Employee updateEmployee(@PathVariable String id, @Valid @RequestBody Employee employee) {
		return employeeService.updateEmployee(id, employee);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteEmployee(@PathVariable String id) {
		employeeService.deleteEmployee(id);
	}
}
