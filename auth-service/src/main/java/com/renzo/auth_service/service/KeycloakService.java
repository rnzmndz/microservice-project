package com.renzo.auth_service.service;

import com.renzo.auth_service.dto.RegisterRequest;
import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakService {

    @Value("${keycloak.server-uri}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // admin realm
                .clientId("admin-cli")
                .username(username)  // Keycloak admin username
                .password(password)  // Keycloak admin password
                .build();
    }

    public String createUser(RegisterRequest request) {
        try {
            // 1. Create user representation with all required fields
            UserRepresentation userRep = new UserRepresentation();
            userRep.setUsername(request.getUsername());
            userRep.setEmail(request.getEmployeeCreateDto().getContactInformationDto().getEmail());
            userRep.setFirstName(request.getEmployeeCreateDto().getFirstName());
            userRep.setLastName(request.getEmployeeCreateDto().getLastName());
            userRep.setEnabled(true);
            userRep.setEmailVerified(true);

            // Explicitly set required actions to empty list
            userRep.setRequiredActions(Collections.emptyList());

            // Set additional attributes to ensure account is fully set up
            userRep.setAttributes(Collections.singletonMap("locale", Arrays.asList("en")));

            // 2. Create the user
            Response response = keycloak.realm(realm).users().create(userRep);
            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed to create user: " + response.getStatusInfo().getReasonPhrase());
            }

            String userId = CreatedResponseUtil.getCreatedId(response);
            response.close(); // Important: close the response

            // 3. Get user resource for further operations
            UserResource userResource = keycloak.realm(realm).users().get(userId);

            // 4. Set permanent password
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(request.getPassword());
            cred.setTemporary(false);
            userResource.resetPassword(cred);

            // 5. Update user to ensure all required actions are cleared
            UserRepresentation updatedUser = userResource.toRepresentation();
            updatedUser.setRequiredActions(Collections.emptyList());
            updatedUser.setEmailVerified(true);
            updatedUser.setEnabled(true);
            userResource.update(updatedUser);

            // 6. Add roles
            try {
                String roleName = request.getRole();
                List<RoleRepresentation> roles =
                        Collections.singletonList(keycloak.realm(realm).roles().get(roleName).toRepresentation());
                userResource.roles().realmLevel().add(roles);
            } catch (Exception e) {
                // If role doesn't exist, log but don't fail the user creation
                System.err.println("Warning: Could not assign role " + request.getRole() + " to user: " + e.getMessage());
            }

            // 7. Final verification - ensure user is fully set up
            UserRepresentation finalUser = userResource.toRepresentation();
            if (finalUser.getRequiredActions() != null && !finalUser.getRequiredActions().isEmpty()) {
                finalUser.setRequiredActions(Collections.emptyList());
                userResource.update(finalUser);
            }

            return userId;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

    public boolean isEmailExisting(String email){
        UsersResource usersResource = keycloak.realm(realm).users();

        // Search for users by email
        List<UserRepresentation> users = usersResource.search(email);

        return !users.isEmpty();
    }

    public List<String> getUserRoles(String userId) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);

        List<String> roleNames = new ArrayList<>();

        // Realm roles
        userResource.roles().realmLevel().listAll()
                .forEach(role -> roleNames.add(role.getName()));

        // Client roles
        keycloak.realm(realm).clients().findAll().forEach(client -> {
            userResource.roles().clientLevel(client.getId()).listAll()
                    .forEach(role -> roleNames.add(role.getName()));
        });

        return roleNames;
    }
}