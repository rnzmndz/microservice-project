package com.renzomendoza.employee_service.service;

import com.renzomendoza.employee_service.dto.AddressDto;
import com.renzomendoza.employee_service.dto.ContactInformationDto;
import com.renzomendoza.employee_service.dto.EmergencyContactDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeCreateDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeList;
import com.renzomendoza.employee_service.dto.employee.EmployeeRequestDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeResponse;
import com.renzomendoza.employee_service.exception.EmployeeNotFoundException;
import com.renzomendoza.employee_service.mapper.EmployeeMapper;
import com.renzomendoza.employee_service.model.EmployeeProfile;
import com.renzomendoza.employee_service.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeResponse createEmployee(EmployeeCreateDto employeeCreateDto) {
        EmployeeProfile employee = employeeMapper.employeeCreateToEmployee(employeeCreateDto);
        employeeRepository.save(employee);
        return employeeMapper.employeeToEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(UUID employeeId) {
        EmployeeProfile employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        return employeeMapper.employeeToEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeList> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::employeeToEmployeeList);
    }

    public EmployeeResponse updateEmployee(UUID employeeId, EmployeeRequestDto employeeRequestDto) {
        EmployeeProfile existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        employeeMapper.updateEmployeeFromRequest(employeeRequestDto, existingEmployee);

        employeeRepository.save(existingEmployee);
        return employeeMapper.employeeToEmployeeResponse(existingEmployee);
    }

    public void deleteEmployee(UUID employeeId) {
        EmployeeProfile employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        employeeRepository.delete(employee);
    }

    public AddressDto updateEmployeeAddress(UUID employeeId, AddressDto addressDto) {
        EmployeeProfile employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        employee.setAddress(employeeMapper.addressDtoToAddress(addressDto));
        EmployeeProfile updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.addressToAddressDto(updatedEmployee.getAddress());
    }

    public ContactInformationDto updateEmployeeContactInformation(UUID employeeId, ContactInformationDto contactInformationDto) {
        EmployeeProfile employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        employee.setContactInformation(employeeMapper.contactInformationDtoToContactInformation(contactInformationDto));
        EmployeeProfile updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.contactInformationToContactInformationDto(updatedEmployee.getContactInformation());
    }

    public EmergencyContactDto updateEmployeeEmergencyContact(UUID employeeId, EmergencyContactDto emergencyContactDto) {
        EmployeeProfile employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        employee.setEmergencyContact(employeeMapper.emergencyContactDtoToEmergencyContact(emergencyContactDto));
        EmployeeProfile updatedEmployee = employeeRepository.save(employee);
        return employeeMapper.emergencyContactToEmergencyContactDto(updatedEmployee.getEmergencyContact());
    }

    @Transactional(readOnly = true)
    public Page<EmployeeList> getAllEmployeesSorted(int page, int size, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::employeeToEmployeeList);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeList> getEmployeesByJobTitle(String jobTitle, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findByJobTitle(jobTitle, pageable)
                .map(employeeMapper::employeeToEmployeeList);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeList> searchEmployeesByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findByFirstNameContainingOrLastNameContaining(name, name, pageable)
                .map(employeeMapper::employeeToEmployeeList);
    }
}