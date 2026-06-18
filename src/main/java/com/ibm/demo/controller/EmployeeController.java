package com.ibm.demo.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.demo.dto.employee.EmployeeRequest;
import com.ibm.demo.dto.employee.EmployeeResponse;
import com.ibm.demo.service.EmployeeService;

@Validated
@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable(name = "id") String id) {
//		System.out.println("Id " + id); // Not in production 
		LOG.info("Id " + id); // yes, in production
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	@GetMapping("/email")
	public ResponseEntity<EmployeeResponse> getEmployeeByEmail(
			@RequestParam @NotBlank(message = "Email must not be blank") @Email(message = "Provided email is not valid") String email) {
		return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
	}

//	If the value identifies a resource then @PathVariable
//	If the value filters or searches then @RequestParam

	@GetMapping("/search")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByFirstName(
			@RequestParam @NotBlank(message = "First name must not be blank") @Size(min = 2, message = "First name must be at least 2 characters") String firstName) {
		return ResponseEntity.ok(employeeService.getEmployeesByFirstName(firstName));
	}

	@GetMapping("/search/{firstName}")
	public ResponseEntity<List<EmployeeResponse>> getEmployeesByFirstNamePath(
			@PathVariable @NotBlank(message = "First name must not be blank") @Size(min = 2, message = "First name must be at least 2 characters") String firstName) {
		return ResponseEntity.ok(employeeService.getEmployeesByFirstName(firstName));
	}

	@PostMapping
	public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest requestDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(requestDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable String id,
			@Valid @RequestBody EmployeeRequest requestDTO) {
		return ResponseEntity.ok(employeeService.updateEmployee(id, requestDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/department/{departmentId}")
	public ResponseEntity<EmployeeResponse> assignEmployeeToDepartment(@PathVariable String id,
			@PathVariable String departmentId) {
		EmployeeResponse employee = employeeService.getEmployeeById(id);
		EmployeeRequest updateRequest = new EmployeeRequest(employee.getFirstName(), employee.getLastName(),
				employee.getEmail(), employee.getSalary(), departmentId, employee.getProjectIds());
		return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
	}

	@DeleteMapping("/{id}/department")
	public ResponseEntity<EmployeeResponse> removeEmployeeFromDepartment(@PathVariable String id) {
		EmployeeResponse employee = employeeService.getEmployeeById(id);
		EmployeeRequest updateRequest = new EmployeeRequest(employee.getFirstName(), employee.getLastName(),
				employee.getEmail(), employee.getSalary(), null, employee.getProjectIds());
		return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
	}

	@PostMapping("/{id}/projects/{projectId}")
	public ResponseEntity<EmployeeResponse> addEmployeeToProject(@PathVariable String id,
			@PathVariable String projectId) {
		EmployeeResponse employee = employeeService.getEmployeeById(id);
		if (!employee.getProjectIds().contains(projectId)) {
			employee.getProjectIds().add(projectId);
		}
		EmployeeRequest updateRequest = new EmployeeRequest(employee.getFirstName(), employee.getLastName(),
				employee.getEmail(), employee.getSalary(), employee.getDepartmentId(), employee.getProjectIds());
		return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
	}

	@DeleteMapping("/{id}/projects/{projectId}")
	public ResponseEntity<EmployeeResponse> removeEmployeeFromProject(@PathVariable String id,
			@PathVariable String projectId) {
		EmployeeResponse employee = employeeService.getEmployeeById(id);
		employee.getProjectIds().remove(projectId);
		EmployeeRequest updateRequest = new EmployeeRequest(employee.getFirstName(), employee.getLastName(),
				employee.getEmail(), employee.getSalary(), employee.getDepartmentId(), employee.getProjectIds());
		return ResponseEntity.ok(employeeService.updateEmployee(id, updateRequest));
	}
}

//package com.ibm.demo.controller;
//
//import java.util.List;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.ibm.demo.model.Employee;
//import com.ibm.demo.service.EmployeeService;
//
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/api/v1/employees")
//public class EmployeeController {
//
//	private final EmployeeService employeeService;
//
//	public EmployeeController(EmployeeService employeeService) {
//		this.employeeService = employeeService;
//	}
//
//	@GetMapping
//	public ResponseEntity<List<Employee>> getAllEmployees() {
//		return ResponseEntity.ok(employeeService.getAllEmployees());
//	}
//
////	// controller methods return raw business data -
////	@GetMapping("/{id}")
////	public Employee getEmployeeById(@PathVariable String id) {
////		return employeeService.getEmployeeById(id);
////	}
//
////	// ResponseEntity object with business data and status -
////	@GetMapping("/{id}")
////	public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
////		Employee emp = employeeService.getEmployeeById(id);
////		HttpStatus status = HttpStatus.OK;
////		ResponseEntity<Employee> response = new ResponseEntity<Employee>(emp, status);
////		return response;
////	}
//
////	// ResponseEntity object with business data, headers and status -
////	@GetMapping("/{id}")
////	public ResponseEntity<Em ployee> getEmployeeById(@PathVariable String id) {
////		Employee emp = employeeService.getEmployeeById(id);
////		HttpStatus status = HttpStatus.OK;
////		HttpHeaders headers = new HttpHeaders();
////		headers.add("message", "Employee with the id " + id + " found successfully.");
////		ResponseEntity<Employee> response = new ResponseEntity<Employee>(emp, headers, status);
////		return response;
////	}
//
//	@GetMapping("/email/{email}")
//	public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
//
//		return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
//	}
//
//	@GetMapping("/search")
//	public ResponseEntity<List<Employee>> getEmployeesByFirstName(@RequestParam String firstName) {
//
//		return ResponseEntity.ok(employeeService.getEmployeesByFirstName(firstName));
//	}
//
//	@PostMapping
//	public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
//
//		Employee savedEmployee = employeeService.createEmployee(employee);
//
//		return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
//	}
//
//	@PutMapping("/{id}")
//	public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @Valid @RequestBody Employee employee) {
//
//		return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
//	}
//
//	@DeleteMapping("/{id}")
//	public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
//
//		employeeService.deleteEmployee(id);
//
//		return ResponseEntity.noContent().build();
//	}
//}
//
////package com.ibm.demo.controller;
////
////import java.util.List;
////
////import jakarta.validation.Valid;
////
////import org.springframework.http.HttpStatus;
////import org.springframework.web.bind.annotation.DeleteMapping;
////import org.springframework.web.bind.annotation.GetMapping;
////import org.springframework.web.bind.annotation.PathVariable;
////import org.springframework.web.bind.annotation.PostMapping;
////import org.springframework.web.bind.annotation.PutMapping;
////import org.springframework.web.bind.annotation.RequestBody;
////import org.springframework.web.bind.annotation.RequestMapping;
////import org.springframework.web.bind.annotation.RequestParam;
////import org.springframework.web.bind.annotation.ResponseStatus;
////import org.springframework.web.bind.annotation.RestController;
////
////import com.ibm.demo.model.Employee;
////import com.ibm.demo.service.EmployeeService;
////
////@RestController
////@RequestMapping("/api/v1/employees")
////public class EmployeeController {
////
////	private final EmployeeService employeeService;
////
////	public EmployeeController(EmployeeService employeeService) {
////		this.employeeService = employeeService;
////	}
////
//////    http://localhost:8080/api/v1/employees 
////
////	@GetMapping
////	public List<Employee> getAllEmployees() {
////		return employeeService.getAllEmployees();
////	}
////
////	@GetMapping("/{id}")
////	public Employee getEmployeeById(@PathVariable String id) {
////		return employeeService.getEmployeeById(id);
////	}
////
////	@GetMapping("/email/{email}")
////	public Employee getEmployeeByEmail(@PathVariable String email) {
////		return employeeService.getEmployeeByEmail(email);
////	}
////
////	@GetMapping("/search")
////	public List<Employee> getEmployeesByFirstName(@RequestParam String firstName) {
////		return employeeService.getEmployeesByFirstName(firstName);
////	}
////
////	@PostMapping
////	@ResponseStatus(HttpStatus.CREATED)
////	public Employee createEmployee( @RequestBody Employee employee) {
////		return employeeService.createEmployee(employee);
////	}
////
////	@PutMapping("/{id}")
////	public Employee updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
////		return employeeService.updateEmployee(id, employee);
////	}
////
////	@DeleteMapping("/{id}")
////	@ResponseStatus(HttpStatus.NO_CONTENT)
////	public void deleteEmployee(@PathVariable String id) {
////		employeeService.deleteEmployee(id);
////	}
////}
