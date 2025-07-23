package com.renzomendoza.employee_service.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "URL of employee's profile image", example = "https://example.com/profile.jpg", nullable = true)
    private String imageUrl;
}