package com.renzo.auth_service.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Emergency contact information")
public class EmergencyContactDto {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be less than 50 characters")
    @Schema(description = "Emergency contact's first name", example = "Jane")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be less than 50 characters")
    @Schema(description = "Emergency contact's last name", example = "Doe")
    private String lastName;

<<<<<<< HEAD
    @Size(max = 50, message = "Emergency contact gender must be less than 50 characters")
    @Schema(description = "Gender of the emergency contact", example = "Female", nullable = true)
    private String emergencyContactGender;
=======
    @NotBlank(message = "Relationship is required")
    @Size(max = 50, message = "Relationship must be less than 50 characters")
    @Schema(description = "Emergency contact's relationship to the patient", example = "Spouse")
    private String relationship;
>>>>>>> 314dc7e165f2407a92b240e9e802981c5d485913

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    @Schema(description = "Emergency contact's phone number", example = "+1987654321")
    private String phoneNumber;
}