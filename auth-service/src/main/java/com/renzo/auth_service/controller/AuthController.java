package com.renzo.auth_service.controller;

import com.renzo.auth_service.dto.*;
import com.renzo.auth_service.service.AuthService;
import com.renzo.auth_service.service.CookieConfig;
import com.renzo.auth_service.service.KeycloakService;
import com.renzo.auth_service.service.TokenService;
import com.renzo.auth_service.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final KeycloakService keycloakService;
    private final TokenService tokenService;
    private final CookieConfig cookieConfig;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
        MultiValueMap<String, String> formData = tokenService.buildPasswordGrantForm(
                request.getUsername(),
                request.getPassword()
        );

        ResponseEntity<TokenResponse> response = tokenService.requestToken(formData);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            TokenResponse tokenResponse = response.getBody();

            ResponseCookie accessCookie = cookieConfig.createAccessCookie(tokenResponse.getAccess_token());
            ResponseCookie refreshCookie = cookieConfig.createRefreshCookie(tokenResponse.getRefresh_token());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .build();
        }

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
    public List<String> getMyUserRoles(Authentication authentication) {
        String userId = AuthUtils.extractUserId(authentication);
        return keycloakService.getUserRoles(userId);
    }

    @GetMapping("/user-info")
    public AccountDetails getAccountDetails(Authentication authentication) {
        Jwt jwt = AuthUtils.extractJwt(authentication);

        return AccountDetails.builder()
                .userId(jwt.getClaim("sub"))
                .username(jwt.getClaim("preferred_username"))
                .fullName(jwt.getClaim("name"))
                .firstName(jwt.getClaim("given_name"))
                .lastName(jwt.getClaim("family_name"))
                .email(jwt.getClaim("email"))
                .build();
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

        MultiValueMap<String, String> formData = tokenService.buildRefreshGrantForm(refreshToken);
        ResponseEntity<TokenResponse> response = tokenService.requestToken(formData);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            TokenResponse tokenResponse = response.getBody();
            ResponseCookie accessCookie = cookieConfig.createAccessCookie(tokenResponse.getAccess_token());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteAccess = cookieConfig.createExpiredCookie("ACCESS_TOKEN");
        ResponseCookie deleteRefresh = cookieConfig.createExpiredCookie("REFRESH_TOKEN");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }

    @GetMapping("/session")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("authenticated", true));
    }

}
