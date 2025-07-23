package com.renzomendoza.employee_service.config;

import com.renzomendoza.employee_service.utils.NetworkUtils;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        String ip = NetworkUtils.getLocalIpAddress();

        Server localhost = new Server()
                .url("http://localhost:8080")
                .description("Localhost");

        Server ipBased = new Server()
                .url("http://" + ip + ":8080")
                .description("Current machine IP (" + ip + ")");

        Server docker = new Server()
                .url("http://host.docker.internal:8080")
                .description("Docker Host");

        return new OpenAPI()
                .info(new Info().title("Employee Service API")
                        .description("API for managing employee information")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@employee-service.com")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .servers(List.of(localhost, ipBased, docker));
    }
}