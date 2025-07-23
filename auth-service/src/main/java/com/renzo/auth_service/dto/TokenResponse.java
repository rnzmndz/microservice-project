package com.renzo.auth_service.dto;

import lombok.Data;

@Data
public class TokenResponse {

    private String access_token;
    private String refresh_token;
    private String id_token;
    private String token_type;
    private int expires_in;
    private int refresh_expires_in;
}
