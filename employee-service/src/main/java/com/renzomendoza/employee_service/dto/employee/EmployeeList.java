package com.renzomendoza.employee_service.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee summary information")
public class EmployeeList {

    @Schema(description = "Employee's id")
    private UUID id;

    @Schema(description = "Employee's first name", example = "John")
    private String firstName;

    @Schema(description = "Employee's middle name", example = "Michael", nullable = true)
    private String middleName;

    @Schema(description = "Employee's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Employee's job title", example = "Software Engineer")
    private String jobTitle;

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must be less than 100 characters")
    @Schema(description = "Employee's department", example = "Engineering")
    private String department;

    @Schema(description = "URL of employee's profile image", example = "https://example.com/profile.jpg", nullable = true)
    private String imageUrl;
}