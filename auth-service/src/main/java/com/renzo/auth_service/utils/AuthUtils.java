package com.renzo.auth_service.utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

public class AuthUtils {

    public static Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No valid JWT found in security context");
    }

    public static String extractUserId(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        return jwt.getClaim("sub");
    }
}
