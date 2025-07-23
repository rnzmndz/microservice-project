package com.renzomendoza.employee_service.mapper;

import com.renzomendoza.employee_service.dto.*;
import com.renzomendoza.employee_service.dto.employee.EmployeeRequestDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeResponse;
import com.renzomendoza.employee_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    private EmployeeMapper employeeMapper;
    private EmployeeRequestDto sampleRequest;
    private EmployeeProfile sampleProfile;

    @BeforeEach
    void setUp() {
        employeeMapper = new EmployeeMapperImpl();
        sampleRequest = createSampleRequest();
        sampleProfile = createSampleProfile();
    }

    @Test
    void employeeUpdateToEmployee_shouldMapCorrectly() {
        EmployeeProfile result = employeeMapper.employeeUpdateToEmployee(sampleRequest);

        assertNull(result.getId(), "ID should not be mapped from request");
        assertEmployeeFieldsMatch(sampleRequest, result);

        // Check if nested objects are null first, then assert their content
        if (result.getAddress() != null) {
            assertNestedObjectsMatch(sampleRequest, result);
        } else {
            // Log the issue for debugging
            System.out.println("WARNING: Address is null - check MapStruct configuration");
            assertNotNull(result.getAddress(), "Address should be mapped");
        }
    }

    @Test
    void employeeToEmployeeResponse_shouldMapCorrectly() {
        EmployeeResponse result = employeeMapper.employeeToEmployeeResponse(sampleProfile);

        assertEquals(sampleProfile.getId(), result.getId());
        assertEmployeeFieldsMatch(sampleProfile, result);
        assertNestedObjectsMatch(sampleProfile, result);
    }

    @Test
    void employeeProfileToEmployeeRequest_shouldMapCorrectly() {
        EmployeeRequestDto result = employeeMapper.employeeProfileToEmployeeRequest(sampleProfile);

        assertEmployeeFieldsMatch(sampleProfile, result);
        assertNestedObjectsMatch(sampleProfile, result);
    }

    @Test
    void updateEmployeeFromRequest_shouldUpdateNonNullFields() {
        EmployeeRequestDto partialUpdate = EmployeeRequestDto.builder()
                .firstName("Updated")
                .lastName("NewLastName")
                .jobTitle("Senior Software Engineer")
                .contactInformationDto(ContactInformationDto.builder()
                        .email("updated@example.com")
                        .phoneNumber("+1234567890") // Include phone number to avoid null issues
                        .build())
                .build();

        // Store original values for comparison
        String originalMiddleName = sampleProfile.getMiddleName();
        String originalImageUrl = sampleProfile.getImageUrl();
        LocalDate originalHiredDate = sampleProfile.getHiredDate();
        String originalEmergencyContactFirstName = sampleProfile.getEmergencyContact().getFirstName();

        employeeMapper.updateEmployeeFromRequest(partialUpdate, sampleProfile);

        // Verify updated fields
        assertEquals("Updated", sampleProfile.getFirstName());
        assertEquals("NewLastName", sampleProfile.getLastName());
        assertEquals("Senior Software Engineer", sampleProfile.getJobTitle());

        // Debug the contact information update
        if (sampleProfile.getContactInformation() != null) {
            System.out.println("Contact info email: " + sampleProfile.getContactInformation().getEmail());
            assertEquals("updated@example.com", sampleProfile.getContactInformation().getEmail());
        } else {
            fail("ContactInformation should not be null after update");
        }

        // Verify unchanged fields
        assertEquals(originalMiddleName, sampleProfile.getMiddleName());
        assertEquals(originalImageUrl, sampleProfile.getImageUrl());
        assertEquals(originalHiredDate, sampleProfile.getHiredDate());
        assertEquals(originalEmergencyContactFirstName, sampleProfile.getEmergencyContact().getFirstName());
    }

    @Test
    void updateEmployeeFromRequest_shouldHandleNullRequest() {
        EmployeeProfile originalProfile = createSampleProfile();

        employeeMapper.updateEmployeeFromRequest(null, sampleProfile);

        // Verify no changes by comparing key fields
        assertEquals(originalProfile.getFirstName(), sampleProfile.getFirstName());
        assertEquals(originalProfile.getJobTitle(), sampleProfile.getJobTitle());
        assertEquals(originalProfile.getImageUrl(), sampleProfile.getImageUrl());
    }

    // Helper methods for creating test data
    private EmployeeRequestDto createSampleRequest() {
        return EmployeeRequestDto.builder()
                .firstName("John")
                .middleName("Michael")
                .lastName("Doe")
                .jobTitle("Software Engineer")
                .imageUrl("https://example.com/profile.jpg")
                .hiredDate(LocalDate.of(2023, 1, 15))
                .birthDate(LocalDate.of(1990, 5, 20))
                .addressDto(createSampleAddressDto())
                .contactInformationDto(createSampleContactInfoDto())
                .emergencyContactDto(createSampleEmergencyContactDto())
                .build();
    }

    private EmployeeProfile createSampleProfile() {
        return EmployeeProfile.builder()
                .id(UUID.randomUUID())
                .firstName("Old")
                .middleName("Name")
                .lastName("Oldson")
                .jobTitle("Old Job")
                .imageUrl("https://old.com/image.jpg")
                .hiredDate(LocalDate.of(2020, 1, 1))
                .birthDate(LocalDate.of(1980, 1, 1))
                .address(createSampleAddress())
                .contactInformation(createSampleContactInfo())
                .emergencyContact(createSampleEmergencyContact())
                .build();
    }

    private AddressDto createSampleAddressDto() {
        return AddressDto.builder()
                .street("123 Main St")
                .city("New York")
                .state("NY")
                .zipCode("10001")
                .build();
    }

    private Address createSampleAddress() {
        return Address.builder()
                .street("Old St")
                .city("Old City")
                .state("OC")
                .zipCode("00000")
                .build();
    }

    private ContactInformationDto createSampleContactInfoDto() {
        return ContactInformationDto.builder()
                .phoneNumber("+1234567890")
                .email("john.doe@example.com")
                .build();
    }

    private ContactInformation createSampleContactInfo() {
        return ContactInformation.builder()
                .phoneNumber("+0000000000")
                .email("old@example.com")
                .build();
    }

    private EmergencyContactDto createSampleEmergencyContactDto() {
        return EmergencyContactDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("+1987654321")
                .build();
    }

    private EmergencyContact createSampleEmergencyContact() {
        return EmergencyContact.builder()
                .firstName("Old")
                .lastName("Contact")
                .phoneNumber("+0000000000")
                .build();
    }

    // Helper methods for assertions
    private void assertEmployeeFieldsMatch(EmployeeRequestDto request, EmployeeProfile profile) {
        assertEquals(request.getFirstName(), profile.getFirstName());
        assertEquals(request.getMiddleName(), profile.getMiddleName());
        assertEquals(request.getLastName(), profile.getLastName());
        assertEquals(request.getJobTitle(), profile.getJobTitle());
        assertEquals(request.getImageUrl(), profile.getImageUrl());
        assertEquals(request.getHiredDate(), profile.getHiredDate());
        assertEquals(request.getBirthDate(), profile.getBirthDate());
    }

    private void assertEmployeeFieldsMatch(EmployeeProfile profile, EmployeeResponse response) {
        assertEquals(profile.getFirstName(), response.getFirstName());
        assertEquals(profile.getMiddleName(), response.getMiddleName());
        assertEquals(profile.getLastName(), response.getLastName());
        assertEquals(profile.getJobTitle(), response.getJobTitle());
        assertEquals(profile.getImageUrl(), response.getImageUrl());
        assertEquals(profile.getHiredDate(), response.getHiredDate());
        assertEquals(profile.getBirthDate(), response.getBirthDate());
    }

    private void assertEmployeeFieldsMatch(EmployeeProfile profile, EmployeeRequestDto request) {
        assertEquals(profile.getFirstName(), request.getFirstName());
        assertEquals(profile.getMiddleName(), request.getMiddleName());
        assertEquals(profile.getLastName(), request.getLastName());
        assertEquals(profile.getJobTitle(), request.getJobTitle());
        assertEquals(profile.getImageUrl(), request.getImageUrl());
        assertEquals(profile.getHiredDate(), request.getHiredDate());
        assertEquals(profile.getBirthDate(), request.getBirthDate());
    }

    private void assertNestedObjectsMatch(EmployeeRequestDto request, EmployeeProfile profile) {
        assertAddressMatch(request.getAddressDto(), profile.getAddress());
        assertContactInfoMatch(request.getContactInformationDto(), profile.getContactInformation());
        assertEmergencyContactMatch(request.getEmergencyContactDto(), profile.getEmergencyContact());
    }

    private void assertNestedObjectsMatch(EmployeeProfile profile, EmployeeResponse response) {
        assertAddressMatch(profile.getAddress(), response.getAddressDto());
        assertContactInfoMatch(profile.getContactInformation(), response.getContactInformationDto());
        assertEmergencyContactMatch(profile.getEmergencyContact(), response.getEmergencyContactDto());
    }

    private void assertNestedObjectsMatch(EmployeeProfile profile, EmployeeRequestDto request) {
        assertAddressMatch(profile.getAddress(), request.getAddressDto());
        assertContactInfoMatch(profile.getContactInformation(), request.getContactInformationDto());
        assertEmergencyContactMatch(profile.getEmergencyContact(), request.getEmergencyContactDto());
    }

    private void assertAddressMatch(AddressDto dto, Address entity) {
        assertNotNull(entity);
        assertEquals(dto.getStreet(), entity.getStreet());
        assertEquals(dto.getCity(), entity.getCity());
        assertEquals(dto.getState(), entity.getState());
        assertEquals(dto.getZipCode(), entity.getZipCode());
    }

    private void assertAddressMatch(Address entity, AddressDto dto) {
        assertNotNull(dto);
        assertEquals(entity.getStreet(), dto.getStreet());
        assertEquals(entity.getCity(), dto.getCity());
        assertEquals(entity.getState(), dto.getState());
        assertEquals(entity.getZipCode(), dto.getZipCode());
    }

    private void assertContactInfoMatch(ContactInformationDto dto, ContactInformation entity) {
        assertNotNull(entity);
        assertEquals(dto.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(dto.getEmail(), entity.getEmail());
    }

    private void assertContactInfoMatch(ContactInformation entity, ContactInformationDto dto) {
        assertNotNull(dto);
        assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(entity.getEmail(), dto.getEmail());
    }

    private void assertEmergencyContactMatch(EmergencyContactDto dto, EmergencyContact entity) {
        assertNotNull(entity);
        assertEquals(dto.getFirstName(), entity.getFirstName());
        assertEquals(dto.getLastName(), entity.getLastName());
        assertEquals(dto.getPhoneNumber(), entity.getPhoneNumber());
    }

    private void assertEmergencyContactMatch(EmergencyContact entity, EmergencyContactDto dto) {
        assertNotNull(dto);
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertEquals(entity.getLastName(), dto.getLastName());
        assertEquals(entity.getPhoneNumber(), dto.getPhoneNumber());
    }
}