# Employee Management System - API Endpoints

## Overview
This document describes all REST API endpoints for the Employee Management System (EMS) with Department, Employee, and Project entities.

## Base URL
```
http://localhost:8080/api/v1
```

---

## Department Endpoints

### 1. Get All Departments
- **GET** `/departments`
- **Response**: List of all departments

### 2. Get Department by ID
- **GET** `/departments/{id}`
- **Response**: Department details

### 3. Get Department by Name
- **GET** `/departments/name?name={name}`
- **Response**: Department details

### 4. Create Department
- **POST** `/departments`
- **Request Body**:
```json
{
  "name": "Engineering",
  "location": "Building A"
}
```

### 5. Update Department
- **PUT** `/departments/{id}`
- **Request Body**:
```json
{
  "name": "Engineering",
  "location": "Building B"
}
```

### 6. Delete Department
- **DELETE** `/departments/{id}`
- **Note**: Cannot delete if employees are assigned

### 7. Get Employees in Department
- **GET** `/departments/{id}/employees`
- **Response**: List of employees in the department

---

## Project Endpoints

### 1. Get All Projects
- **GET** `/projects`
- **Response**: List of all projects

### 2. Get Project by ID
- **GET** `/projects/{id}`
- **Response**: Project details with employee IDs

### 3. Get Project by Name
- **GET** `/projects/name?name={name}`
- **Response**: Project details

### 4. Create Project
- **POST** `/projects`
- **Request Body**:
```json
{
  "name": "Project Alpha",
  "description": "New product development",
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-12-31T23:59:59"
}
```

### 5. Update Project
- **PUT** `/projects/{id}`
- **Request Body**:
```json
{
  "name": "Project Alpha",
  "description": "Updated description",
  "startDate": "2026-01-01T00:00:00",
  "endDate": "2026-12-31T23:59:59"
}
```

### 6. Delete Project
- **DELETE** `/projects/{id}`
- **Note**: Cannot delete if employees are assigned

### 7. Get Employees in Project
- **GET** `/projects/{id}/employees`
- **Response**: List of employees assigned to the project

### 8. Add Employee to Project
- **POST** `/projects/{projectId}/employees/{employeeId}`
- **Response**: Updated project with employee added
- **Note**: Bidirectional sync - updates both project and employee

### 9. Remove Employee from Project
- **DELETE** `/projects/{projectId}/employees/{employeeId}`
- **Response**: Updated project with employee removed
- **Note**: Bidirectional sync - updates both project and employee

---

## Employee Endpoints

### 1. Get All Employees
- **GET** `/employees`
- **Response**: List of all employees

### 2. Get Employee by ID
- **GET** `/employees/{id}`
- **Response**: Employee details with departmentId and projectIds

### 3. Get Employee by Email
- **GET** `/employees/email?email={email}`
- **Response**: Employee details

### 4. Search Employees by First Name
- **GET** `/employees/search?firstName={firstName}`
- **Response**: List of employees matching the first name

### 5. Create Employee
- **POST** `/employees`
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "salary": 75000.00,
  "departmentId": "dept123",
  "projectIds": ["proj456", "proj789"]
}
```
- **Note**: departmentId and projectIds are optional but validated if provided

### 6. Update Employee
- **PUT** `/employees/{id}`
- **Request Body**:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "salary": 80000.00,
  "departmentId": "dept123",
  "projectIds": ["proj456"]
}
```
- **Note**: Bidirectional sync for projects - automatically updates project memberships

### 7. Delete Employee
- **DELETE** `/employees/{id}`
- **Note**: Automatically removes employee from all assigned projects

### 8. Assign Employee to Department
- **PUT** `/employees/{id}/department/{departmentId}`
- **Response**: Updated employee with new department

### 9. Remove Employee from Department
- **DELETE** `/employees/{id}/department`
- **Response**: Updated employee with department removed

### 10. Add Employee to Project
- **POST** `/employees/{id}/projects/{projectId}`
- **Response**: Updated employee with project added
- **Note**: Bidirectional sync - updates both employee and project

### 11. Remove Employee from Project
- **DELETE** `/employees/{id}/projects/{projectId}`
- **Response**: Updated employee with project removed
- **Note**: Bidirectional sync - updates both employee and project

---

## Relationships

### Department ↔ Employee (One-to-Many)
- One department can have many employees
- One employee belongs to one department (optional)
- Deleting a department is blocked if employees are assigned

### Project ↔ Employee (Many-to-Many)
- One project can have many employees
- One employee can work on many projects
- Bidirectional synchronization maintained automatically
- Deleting a project is blocked if employees are assigned
- Deleting an employee removes them from all projects

---

## Validation Rules

### Department
- Name: Required, 2-100 characters, must be unique
- Location: Required, 2-100 characters

### Project
- Name: Required, 2-100 characters, must be unique
- Description: Optional, max 500 characters
- Start Date: Optional
- End Date: Optional

### Employee
- First Name: Required, 2-50 characters
- Last Name: Required, 2-50 characters
- Email: Required, valid email format, must be unique
- Salary: Required, must be positive
- Department ID: Optional, validated if provided
- Project IDs: Optional, all IDs validated if provided

---

## Error Responses

### 400 Bad Request
- Invalid input data
- Validation failures

### 404 Not Found
- Employee not found
- Department not found
- Project not found

### 409 Conflict
- Email already exists
- Department name already exists
- Project name already exists
- Referential integrity violation (cannot delete with dependencies)

### 500 Internal Server Error
- Unexpected server errors

---

## Example Workflows

### Creating a Complete Employee Record
1. Create Department: `POST /departments`
2. Create Projects: `POST /projects` (multiple)
3. Create Employee with references: `POST /employees`

### Assigning Employee to Projects
1. Create Employee: `POST /employees`
2. Add to Project: `POST /employees/{id}/projects/{projectId}`
3. Or use: `POST /projects/{projectId}/employees/{employeeId}`

### Updating Employee Projects
1. Update Employee: `PUT /employees/{id}` with new projectIds list
2. System automatically syncs project memberships

---

## Notes

- All timestamps are in ISO 8601 format
- All IDs are MongoDB ObjectId strings
- Bidirectional relationships are automatically maintained
- Referential integrity is enforced
- Email addresses are stored in lowercase