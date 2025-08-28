package com.renzo.auth_service.config;

import com.renzo.auth_service.utils.NetworkUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@OpenAPIDefinition(
        info = @Info(
                title = "Auth Service API",
                version = "1.0.0",
                description = "API for authentication and user management"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
 public class OpenApiConfig {

     @Value("${services.api-gateway.url:http://api-gateway:8080}")
     private String cloudUrl;

     @Bean
     public OpenAPI customOpenAPI() {
         String ip = NetworkUtils.getLocalIpAddress();

         Server myDomain = new Server()
                 .url("https://api.renzoproject.site")
                 .description("My Domain");

         Server gateway = new Server()
                 .url(cloudUrl)
                 .description("API Gateway");

         Server localhost = new Server()
                 .url("http://localhost:8080")
                 .description("Localhost through API Gateway");

         Server ipBased = new Server()
                 .url("http://" + ip + ":8080")
                 .description("Current machine IP through Gateway");

         Server docker = new Server()
                 .url("http://host.docker.internal:8080")
                 .description("Docker Host through Gateway");

         return new OpenAPI().servers(List.of(myDomain, localhost, gateway, ipBased, docker));
     }
 }
