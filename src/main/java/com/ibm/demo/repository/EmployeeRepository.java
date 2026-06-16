package com.ibm.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ibm.demo.model.Employee;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByFirstNameIgnoreCase(String firstName);

    boolean existsByEmail(String email);
}
