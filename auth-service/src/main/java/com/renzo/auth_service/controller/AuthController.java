package com.renzo.auth_service.controller;

import com.renzo.auth_service.dto.AuthRequest;
import com.renzo.auth_service.dto.RegisterRequest;
import com.renzo.auth_service.dto.RegisterResponse;
import com.renzo.auth_service.dto.TokenResponse;
import com.renzo.auth_service.service.AuthService;
import com.renzo.auth_service.service.KeycloakService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KeycloakService keycloakService;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
        // Build form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());
        formData.add("scope", "openid email profile");

        // Build request entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);

        // Send the request
        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                tokenUri,
                httpEntity,
                TokenResponse.class
        );

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token")
    public String getToken(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getTokenValue();
    }

    @GetMapping("/roles/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getUserRoles(@PathVariable String userId){
        return keycloakService.getUserRoles(userId);
    }

//    @GetMapping("/roles/me")
//    public List<String> getUserMyRoles(@AuthenticationPrincipal Jwt jwt){
//        String userId = jwt.getClaim("sub");
//        return keycloakService.getUserRoles(userId);
//    }

    @GetMapping("/roles/me")
    public Object getUserMyRoles(@AuthenticationPrincipal Jwt jwt,
                                 @RequestHeader Map<String, String> headers) {
        return Map.of(
                "jwt", jwt == null ? "NULL" : jwt.getClaims(),
                "authHeader", headers.get("authorization")
        );
    }


    @GetMapping("/callback")
    public String authCallback(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // only over HTTPS
        cookie.setPath("/"); // available to entire app
        cookie.setMaxAge(3600); // 1 hour
        cookie.setAttribute("SameSite", "Strict"); // or "Lax"
        response.addCookie(cookie);

        return "Login successful";
    }

}
