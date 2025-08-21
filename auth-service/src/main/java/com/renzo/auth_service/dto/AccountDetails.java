package com.renzo.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Account detail response for basic details")
public class AccountDetails {

    @Schema(
            description = "Unique identifier for the user",
            example = "550e8400-e29b-41d4-a716-446655440000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String userId;

    @Schema(
            description = "Username for authentication",
            example = "john_doe",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String username;

    @Schema(
            description = "Full name of the user (concatenated first and last name)",
            example = "John Doe",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String fullName;

    @Schema(
            description = "User's first name",
            example = "John",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String firstName;

    @Schema(
            description = "User's last name",
            example = "Doe",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String lastName;

    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String email;
}