package com.renzo.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class RegisterResponse {
    private UUID userId;
    private String message;
}
