package com.renzomendoza.employee_service.dto.employee;

import com.renzomendoza.employee_service.dto.AddressDto;
import com.renzomendoza.employee_service.dto.ContactInformationDto;
import com.renzomendoza.employee_service.dto.EmergencyContactDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating an employee")
public class EmployeeResponse {

    @Schema(description = "Employee's id", example = "asdf234fffq23wer234faf")
    private UUID id;

    @Schema(description = "Employee's first name", example = "John")
    private String firstName;

    @Schema(description = "Employee's middle name", example = "Michael", nullable = true)
    private String middleName;

    @Schema(description = "Employee's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee's job title", example = "Software Engineer")
    private String jobTitle;

    @Schema(description = "URL of employee's profile image", example = "https://example.com/profile.jpg", nullable = true)
    private String imageUrl;

    @Schema(description = "Date when employee was hired", example = "2023-01-15")
    private LocalDate hiredDate;

    @Schema(description = "Employee's birth date", example = "1990-05-20")
    private LocalDate birthDate;

    @Schema(description = "Employee's address information")
    private AddressDto addressDto;

    @Schema(description = "Employee's contact information")
    private ContactInformationDto contactInformationDto;

    @Schema(description = "Employee's emergency contact information")
    private EmergencyContactDto emergencyContactDto;
}
