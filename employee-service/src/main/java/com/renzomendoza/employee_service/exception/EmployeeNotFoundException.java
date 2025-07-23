package com.renzomendoza.employee_service.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(UUID employeeId) {
        super("Employee not found with ID: " + employeeId);
    }
}