package com.renzomendoza.employee_service.service;

import com.renzomendoza.employee_service.dto.*;
import com.renzomendoza.employee_service.dto.employee.EmployeeCreateDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeList;
import com.renzomendoza.employee_service.dto.employee.EmployeeRequestDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeResponse;
import com.renzomendoza.employee_service.exception.EmployeeNotFoundException;
import com.renzomendoza.employee_service.mapper.EmployeeMapper;
import com.renzomendoza.employee_service.model.EmployeeProfile;
import com.renzomendoza.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private final UUID TEST_UUID = UUID.randomUUID();
    private final LocalDate TEST_DATE = LocalDate.now();

    @Test
    void createEmployee_ShouldReturnSavedEmployee() {
        // Arrange
        EmployeeCreateDto request = createTestEmployeeCreate();
        EmployeeProfile employeeProfile = createTestEmployeeProfile();
        when(employeeMapper.employeeCreateToEmployee(any(EmployeeCreateDto.class))).thenReturn(employeeProfile);
        when(employeeRepository.save(any(EmployeeProfile.class))).thenReturn(employeeProfile);

        // Act
        EmployeeResponse result = employeeService.createEmployee(request);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_UUID, result.getId());
        verify(employeeMapper).employeeCreateToEmployee(request);
        verify(employeeRepository).save(employeeProfile);
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenExists() {
        // Arrange
        EmployeeProfile employeeProfile = createTestEmployeeProfile();
        when(employeeRepository.findById(TEST_UUID)).thenReturn(Optional.of(employeeProfile));

        // Act
        EmployeeResponse result = employeeService.getEmployeeById(TEST_UUID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_UUID, result.getId());
    }

    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(employeeRepository.findById(TEST_UUID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(TEST_UUID);
        });
    }

    @Test
    void getAllEmployees_ShouldReturnPageOfEmployees() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeProfile> employeePage = new PageImpl<>(List.of(createTestEmployeeProfile()));
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.employeeToEmployeeList(any(EmployeeProfile.class))).thenReturn(new EmployeeList());

        // Act
        Page<EmployeeList> result = employeeService.getAllEmployees(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void updateEmployee_ShouldUpdateExistingEmployee() {
        // Arrange
        EmployeeRequestDto request = createTestEmployeeRequest();
        EmployeeProfile existingEmployee = createTestEmployeeProfile();
        when(employeeRepository.findById(TEST_UUID)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(EmployeeProfile.class))).thenReturn(existingEmployee);

        // Act
        EmployeeResponse result = employeeService.updateEmployee(TEST_UUID, request);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    void deleteEmployee_ShouldDeleteWhenExists() {
        // Arrange
        EmployeeProfile employeeProfile = createTestEmployeeProfile();
        when(employeeRepository.findById(TEST_UUID)).thenReturn(Optional.of(employeeProfile));
        doNothing().when(employeeRepository).delete(employeeProfile);

        // Act
        employeeService.deleteEmployee(TEST_UUID);

        // Assert
        verify(employeeRepository).delete(employeeProfile);
    }

    @Test
    void getAllEmployeesSorted_ShouldReturnSortedPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "firstName");
        Page<EmployeeProfile> employeePage = new PageImpl<>(List.of(createTestEmployeeProfile()));
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.employeeToEmployeeList(any(EmployeeProfile.class))).thenReturn(new EmployeeList());

        // Act
        Page<EmployeeList> result = employeeService.getAllEmployeesSorted(0, 10, "firstName", Sort.Direction.ASC);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void getEmployeesByJobTitle_ShouldReturnFilteredPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeProfile> employeePage = new PageImpl<>(List.of(createTestEmployeeProfile()));
        when(employeeRepository.findByJobTitle("Developer", pageable)).thenReturn(employeePage);
        when(employeeMapper.employeeToEmployeeList(any(EmployeeProfile.class))).thenReturn(new EmployeeList());

        // Act
        Page<EmployeeList> result = employeeService.getEmployeesByJobTitle("Developer", 0, 10);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).findByJobTitle("Developer", pageable);
    }

    @Test
    void searchEmployeesByName_ShouldReturnMatchingResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeProfile> employeePage = new PageImpl<>(List.of(createTestEmployeeProfile()));
        when(employeeRepository.findByFirstNameContainingOrLastNameContaining("John", "John", pageable))
                .thenReturn(employeePage);
        when(employeeMapper.employeeToEmployeeList(any(EmployeeProfile.class))).thenReturn(new EmployeeList());

        // Act
        Page<EmployeeList> result = employeeService.searchEmployeesByName("John", 0, 10);

        // Assert
        assertNotNull(result);
        verify(employeeRepository).findByFirstNameContainingOrLastNameContaining("John", "John", pageable);
    }

    // Helper methods
    private EmployeeRequestDto createTestEmployeeRequest() {
        return EmployeeRequestDto.builder()
                .firstName("Updated")
                .middleName("Middle")
                .lastName("Last")
                .jobTitle("Developer")
                .imageUrl("http://example.com/image.jpg")
                .hiredDate(TEST_DATE)
                .birthDate(TEST_DATE)
                .addressDto(new AddressDto())
                .contactInformationDto(new ContactInformationDto())
                .emergencyContactDto(new EmergencyContactDto())
                .build();
    }

    // Helper methods
    private EmployeeCreateDto createTestEmployeeCreate() {
        return EmployeeCreateDto.builder()
                .id(TEST_UUID)
                .firstName("Updated")
                .middleName("Middle")
                .lastName("Last")
                .jobTitle("Developer")
                .imageUrl("http://example.com/image.jpg")
                .hiredDate(TEST_DATE)
                .birthDate(TEST_DATE)
                .addressDto(new AddressDto())
                .contactInformationDto(new ContactInformationDto())
                .emergencyContactDto(new EmergencyContactDto())
                .build();
    }

    private EmployeeProfile createTestEmployeeProfile() {
        EmployeeProfile employee = new EmployeeProfile();
        employee.setId(TEST_UUID);
        employee.setFirstName("Test");
        employee.setMiddleName("Middle");
        employee.setLastName("Employee");
        employee.setJobTitle("Developer");
        employee.setImageUrl("http://example.com/image.jpg");
        employee.setHiredDate(TEST_DATE);
        employee.setBirthDate(TEST_DATE);
        return employee;
    }
}