package com.renzo.auth_service.dto.employee.employee;

import com.renzo.auth_service.dto.employee.AddressDto;
import com.renzo.auth_service.dto.employee.ContactInformationDto;
import com.renzo.auth_service.dto.employee.EmergencyContactDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Schema(description = "Employee's middle name", example = "Michael")
    private String middleName;

    @Schema(description = "Employee's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee's name suffix", example = "Jr.")
    private String nameSuffix;

    @Schema(description = "Employee's gender", example = "Male")
    private String gender;

    @Schema(description = "Employee's job title", example = "Software Engineer")
    private String jobTitle;

    @Schema(description = "Employee's department", example = "Engineering")
    private String department;

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

    @Schema(description = "Timestamp when employee was created", example = "2023-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when employee was last updated", example = "2023-01-20T15:45:00")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the employee record", example = "admin@company.com")
    private String createdBy;

    @Schema(description = "User who last modified the employee record", example = "hr@company.com")
    private String modifiedBy;
}
