package com.renzomendoza.employee_service.controller;

import com.renzomendoza.employee_service.dto.AddressDto;
import com.renzomendoza.employee_service.dto.ContactInformationDto;
import com.renzomendoza.employee_service.dto.EmergencyContactDto;
import com.renzomendoza.employee_service.dto.EmployeePage;
import com.renzomendoza.employee_service.dto.employee.EmployeeCreateDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeList;
import com.renzomendoza.employee_service.dto.employee.EmployeeRequestDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeResponse;
import com.renzomendoza.employee_service.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "APIs for managing employee data")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Create a new employee", description = "Creates a new employee with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee created successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateDto employeeCreateDto) {
        EmployeeResponse createdEmployee = employeeService.createEmployee(employeeCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @Operation(summary = "Get employee by ID", description = "Returns a single employee by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee found",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping(value = "/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @Parameter(description = "ID of the employee to be retrieved", required = true)
            @PathVariable UUID employeeId) {
        EmployeeResponse employee = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employee);
    }

    @Operation(summary = "Get all employees", description = "Returns a paginated list of all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
                    content = @Content(schema = @Schema(implementation = EmployeePage.class)))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeePage> getAllEmployees(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<EmployeeList> employees = employeeService.getAllEmployees(page, size);
        return ResponseEntity.ok(new EmployeePage(employees.getContent(), employees.getPageable(), employees.getTotalElements()));
    }

    @Operation(summary = "Get sorted employees", description = "Returns a paginated and sorted list of employees")
    @GetMapping(value = "/sorted", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EmployeeList>> getAllEmployeesSorted(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "firstName")
            @RequestParam(defaultValue = "firstName") String sortBy,

            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        Page<EmployeeList> employees = employeeService.getAllEmployeesSorted(page, size, sortBy, direction);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Update employee", description = "Updates an existing employee's information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated successfully",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping(value = "/{employeeId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Parameter(description = "ID of the employee to be updated", required = true)
            @PathVariable UUID employeeId,

            @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(employeeId, employeeRequestDto);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(summary = "Delete employee", description = "Deletes an employee by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID of the employee to be deleted", required = true)
            @PathVariable UUID employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update employee address", description = "Updates the address information for an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully",
                    content = @Content(schema = @Schema(implementation = AddressDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PatchMapping(value = "/{employeeId}/address", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressDto> updateEmployeeAddress(
            @Parameter(description = "ID of the employee whose address will be updated", required = true)
            @PathVariable UUID employeeId,

            @Valid @RequestBody AddressDto addressDto) {
        AddressDto updatedAddress = employeeService.updateEmployeeAddress(employeeId, addressDto);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "Update employee contact information", description = "Updates the contact information for an employee")
    @PatchMapping(value = "/{employeeId}/contact", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContactInformationDto> updateEmployeeContactInformation(
            @Parameter(description = "ID of the employee whose contact information will be updated", required = true)
            @PathVariable UUID employeeId,

            @Valid @RequestBody ContactInformationDto contactInformationDto) {
        ContactInformationDto updatedContact = employeeService.updateEmployeeContactInformation(employeeId, contactInformationDto);
        return ResponseEntity.ok(updatedContact);
    }

    @Operation(summary = "Update employee emergency contact", description = "Updates the emergency contact information for an employee")
    @PatchMapping(value = "/{employeeId}/emergency-contact", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmergencyContactDto> updateEmployeeEmergencyContact(
            @Parameter(description = "ID of the employee whose emergency contact will be updated", required = true)
            @PathVariable UUID employeeId,

            @Valid @RequestBody EmergencyContactDto emergencyContactDto) {
        EmergencyContactDto updatedEmergencyContact = employeeService.updateEmployeeEmergencyContact(employeeId, emergencyContactDto);
        return ResponseEntity.ok(updatedEmergencyContact);
    }

    @Operation(summary = "Search employees by name", description = "Searches employees by first or last name (contains match)")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EmployeeList>> searchEmployeesByName(
            @Parameter(description = "Name to search for (partial match allowed)", required = true, example = "John")
            @RequestParam String name,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeList> employees = employeeService.searchEmployeesByName(name, page, size);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by job title", description = "Filters employees by their job title (exact match)")
    @GetMapping(value = "/job-title", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<EmployeeList>> getEmployeesByJobTitle(
            @Parameter(description = "Job title to filter by", required = true, example = "Developer")
            @RequestParam String jobTitle,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeList> employees = employeeService.getEmployeesByJobTitle(jobTitle, page, size);
        return ResponseEntity.ok(employees);
    }
}