package com.renzo.auth_service.service;

import com.renzo.auth_service.client.EmployeeClient;
import com.renzo.auth_service.dto.RegisterRequest;
import com.renzo.auth_service.dto.RegisterResponse;
import com.renzo.auth_service.model.AuthUser;
import com.renzo.auth_service.repository.AuthUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final KeycloakService keycloakService;
    private final EmployeeClient employeeClient;

    @Value("${keycloak.realm}")
    private String realm;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        try {
            // 1. Validate email doesn't already exist
            if (keycloakService.isEmailExisting(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
//            if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
//                throw new RuntimeException("Email already exists");
//            }

            // 2. Create user in Keycloak first
            String keycloakId = keycloakService.createUser(request);
            UUID userId = UUID.fromString(keycloakId);

            // 3. Wait a bit longer to ensure Keycloak fully processes the user
            try {
                Thread.sleep(2000); // Increased to 2 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Registration process interrupted", e);
            }

//            // 4. Create local user record with proper data
//            AuthUser authUser = AuthUser.builder()
//                    .id(userId)
//                    .realmId(realm)
//                    .username(request.getUsername())
//                    .email(request.getEmail())
//                    .firstName(request.getEmployeeCreateDto().getFirstName())
//                    .lastName(request.getEmployeeCreateDto().getLastName())
//                    .enabled(true)
//                    .emailVerified(true)
//                    .createdTimestamp(System.currentTimeMillis())
//                    .build();
//
//            // 5. Save local user first
//            authUserRepository.save(authUser);

            // 6. Create employee record
            request.getEmployeeCreateDto().setId(userId);
            try {
                employeeClient.createEmployee(request.getEmployeeCreateDto());
            } catch (Exception e) {
                log.error("Failed to create employee record for user {}: {}", userId, e.getMessage());
                // You might want to implement compensation logic here
                throw new RuntimeException("Failed to create employee record", e);
            }

            log.info("Successfully registered user with ID: {}", userId);
            return new RegisterResponse(userId, "User registered successfully");

        } catch (Exception e) {
            log.error("Registration failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
}
