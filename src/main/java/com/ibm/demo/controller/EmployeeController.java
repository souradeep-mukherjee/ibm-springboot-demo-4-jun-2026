package com.ibm.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ibm.demo.model.Employee;
import com.ibm.demo.service.EmployeeService;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

	private final EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@GetMapping
	public ResponseEntity<List<Employee>> getAllEmployees() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

//	// controller methods return raw business data -
//	@GetMapping("/{id}")
//	public Employee getEmployeeById(@PathVariable String id) {
//		return employeeService.getEmployeeById(id);
//	}

//	// ResponseEntity object with business data and status -
//	@GetMapping("/{id}")
//	public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
//		Employee emp = employeeService.getEmployeeById(id);
//		HttpStatus status = HttpStatus.OK;
//		ResponseEntity<Employee> response = new ResponseEntity<Employee>(emp, status);
//		return response;
//	}

//	// ResponseEntity object with business data, headers and status -
//	@GetMapping("/{id}")
//	public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
//		Employee emp = employeeService.getEmployeeById(id);
//		HttpStatus status = HttpStatus.OK;
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("message", "Employee with the id " + id + " found successfully.");
//		ResponseEntity<Employee> response = new ResponseEntity<Employee>(emp, headers, status);
//		return response;
//	}

	@GetMapping("/email/{email}")
	public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {

		return ResponseEntity.ok(employeeService.getEmployeeByEmail(email));
	}

	@GetMapping("/search")
	public ResponseEntity<List<Employee>> getEmployeesByFirstName(@RequestParam String firstName) {

		return ResponseEntity.ok(employeeService.getEmployeesByFirstName(firstName));
	}

	@PostMapping
	public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {

		Employee savedEmployee = employeeService.createEmployee(employee);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {

		return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {

		employeeService.deleteEmployee(id);

		return ResponseEntity.noContent().build();
	}
}

//package com.ibm.demo.controller;
//
//import java.util.List;
//
//import jakarta.validation.Valid;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ibm.demo.model.Employee;
//import com.ibm.demo.service.EmployeeService;
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
////    http://localhost:8080/api/v1/employees 
//
//	@GetMapping
//	public List<Employee> getAllEmployees() {
//		return employeeService.getAllEmployees();
//	}
//
//	@GetMapping("/{id}")
//	public Employee getEmployeeById(@PathVariable String id) {
//		return employeeService.getEmployeeById(id);
//	}
//
//	@GetMapping("/email/{email}")
//	public Employee getEmployeeByEmail(@PathVariable String email) {
//		return employeeService.getEmployeeByEmail(email);
//	}
//
//	@GetMapping("/search")
//	public List<Employee> getEmployeesByFirstName(@RequestParam String firstName) {
//		return employeeService.getEmployeesByFirstName(firstName);
//	}
//
//	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
//	public Employee createEmployee( @RequestBody Employee employee) {
//		return employeeService.createEmployee(employee);
//	}
//
//	@PutMapping("/{id}")
//	public Employee updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
//		return employeeService.updateEmployee(id, employee);
//	}
//
//	@DeleteMapping("/{id}")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void deleteEmployee(@PathVariable String id) {
//		employeeService.deleteEmployee(id);
//	}
//}
