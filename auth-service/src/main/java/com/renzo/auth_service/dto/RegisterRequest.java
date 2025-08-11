package com.renzo.auth_service.dto;

import com.renzo.auth_service.dto.employee.employee.EmployeeCreateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "User registration request")
public class RegisterRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain alphanumeric characters, dots, underscores and hyphens")
    @Schema(description = "Unique username for the account", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, one special character and no whitespace"
    )
    @Schema(description = "User's password", example = "Password123", required = true)
    private String password;

    @NotBlank(message = "Role cannot be blank")
    @Schema(description = "User role", example = "VIEW_EMPLOYEE_DETAIL", allowableValues = {"USER", "ADMIN", "MANAGER"})
    private String role;

    @Valid
    @NotNull(message = "Employee details cannot be null")
    @Schema(description = "Employee details", required = true)
    private EmployeeCreateDto employeeCreateDto;
}
