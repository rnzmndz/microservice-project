package com.renzo.auth_service.controller;

import com.renzo.auth_service.dto.AuthRequest;
import com.renzo.auth_service.dto.RegisterRequest;
import com.renzo.auth_service.dto.RegisterResponse;
import com.renzo.auth_service.dto.TokenResponse;
import com.renzo.auth_service.service.AuthService;
import com.renzo.auth_service.service.KeycloakService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
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

        // Recently Added Under Observation we can delete this block if this wont work
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            TokenResponse tokenResponse = response.getBody();

            // Create secure HttpOnly cookies
            ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokenResponse.getAccess_token())
                    .httpOnly(true)
                    .secure(true) // set false if testing locally on http
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofMinutes(15))
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokenResponse.getRefresh_token())
                    .httpOnly(true)
                    .secure(true) // set false if testing locally on http
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .build();
        }

//        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

    @GetMapping("/roles/me")
    public List<String> getMyUserRoles(@AuthenticationPrincipal Jwt jwt){
        String userId = jwt.getClaim("sub");
        return keycloakService.getUserRoles(userId);
    }

    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "userId", jwt.getClaim("sub"),
                "username", jwt.getClaim("preferred_username"),
                "fullName", jwt.getClaim("name"),
                "firstName", jwt.getClaim("given_name"),
                "lastName", jwt.getClaim("family_name"),
                "email", jwt.getClaim("email")
        );
    }
//    @GetMapping("/callback")
//    public String authCallback(HttpServletResponse response, String token) {
//        Cookie cookie = new Cookie("access_token", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // only over HTTPS
//        cookie.setPath("/"); // available to entire app
//        cookie.setMaxAge(3600); // 1 hour
//        cookie.setAttribute("SameSite", "Strict"); // or "Lax"
//        response.addCookie(cookie);
//
//        return "Login successful";
//    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(@CookieValue(name = "REFRESH_TOKEN", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Prepare form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                tokenUri,
                httpEntity,
                TokenResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            TokenResponse tokenResponse = response.getBody();

            ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokenResponse.getAccess_token())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofMinutes(15))
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteAccess = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie deleteRefresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        // If JWT is valid, return user info or just authenticated = true
        return ResponseEntity.ok(Map.of("authenticated", true));
    }

}
