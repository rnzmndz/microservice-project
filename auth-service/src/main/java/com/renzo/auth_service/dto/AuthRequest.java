package com.renzo.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request containing username and password.")
public class AuthRequest {

    @Schema(description = "User's login name", example = "admin")
    private String username;

    @Schema(description = "User's password", example = "admin")
    private String password;
}