package com.renzomendoza.employee_service.mapper;

import com.renzomendoza.employee_service.dto.AddressDto;
import com.renzomendoza.employee_service.dto.ContactInformationDto;
import com.renzomendoza.employee_service.dto.EmergencyContactDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeCreateDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeList;
import com.renzomendoza.employee_service.dto.employee.EmployeeRequestDto;
import com.renzomendoza.employee_service.dto.employee.EmployeeResponse;
import com.renzomendoza.employee_service.model.Address;
import com.renzomendoza.employee_service.model.ContactInformation;
import com.renzomendoza.employee_service.model.EmergencyContact;
import com.renzomendoza.employee_service.model.EmployeeProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    /**
     * Maps EmployeeUpdate to EmployeeProfile entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "addressDto")
    @Mapping(target = "contactInformation", source = "contactInformationDto")
    @Mapping(target = "emergencyContact", source = "emergencyContactDto")
    EmployeeProfile employeeUpdateToEmployee(EmployeeRequestDto employeeRequestDto);

    /**
     * Maps EmployeeCreate to EmployeeProfile entity
     */
    @Mapping(target = "address", source = "addressDto")
    @Mapping(target = "contactInformation", source = "contactInformationDto")
    @Mapping(target = "emergencyContact", source = "emergencyContactDto")
    EmployeeProfile employeeCreateToEmployee(EmployeeCreateDto employeeUpdateDto);

    /**
     * Maps EmployeeProfile to EmployeeResponse DTO
     */
    @Mapping(target = "addressDto", source = "address")
    @Mapping(target = "contactInformationDto", source = "contactInformation")
    @Mapping(target = "emergencyContactDto", source = "emergencyContact")
    EmployeeResponse employeeToEmployeeResponse(EmployeeProfile employeeProfile);

    /**
     * Maps EmployeeProfile to EmployeeRequest DTO (for update operations)
     */
    @Mapping(target = "addressDto", source = "address")
    @Mapping(target = "contactInformationDto", source = "contactInformation")
    @Mapping(target = "emergencyContactDto", source = "emergencyContact")
    EmployeeRequestDto employeeProfileToEmployeeRequest(EmployeeProfile employee);

    /**
     * Maps EmployeeProfile to EmployeeList DTO (for summary views)
     */
    EmployeeList employeeToEmployeeList(EmployeeProfile employee);

    /**
     * Updates existing EmployeeProfile with values from EmployeeRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "addressDto")
    @Mapping(target = "contactInformation", source = "contactInformationDto")
    @Mapping(target = "emergencyContact", source = "emergencyContactDto")
    void updateEmployeeFromRequest(EmployeeRequestDto employeeRequestDto, @MappingTarget EmployeeProfile employeeProfile);

    // Rest of your existing methods remain the same...
    Address addressDtoToAddress(AddressDto addressDto);
    AddressDto addressToAddressDto(Address address);
    void updateAddressFromDto(AddressDto addressDto, @MappingTarget Address address);

    ContactInformation contactInformationDtoToContactInformation(ContactInformationDto contactInformationDto);
    ContactInformationDto contactInformationToContactInformationDto(ContactInformation contactInformation);
    void updateContactInformationFromDto(ContactInformationDto contactInformationDto, @MappingTarget ContactInformation contactInformation);

    EmergencyContact emergencyContactDtoToEmergencyContact(EmergencyContactDto emergencyContactDto);
    EmergencyContactDto emergencyContactToEmergencyContactDto(EmergencyContact emergencyContact);
    void updateEmergencyContactFromDto(EmergencyContactDto emergencyContactDto, @MappingTarget EmergencyContact emergencyContact);
}