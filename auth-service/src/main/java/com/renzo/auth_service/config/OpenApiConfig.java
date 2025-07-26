package com.renzo.auth_service.config;

import com.renzo.auth_service.utils.NetworkUtils;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${services.api-gateway.url}"
    private String cloudUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        String ip = NetworkUtils.getLocalIpAddress();
        
         Server cloud = new Server()
                .url(cloudUrl)
                .description("Cloud");

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
                .servers(List.of(localhost, ipBased, docker, cloud));
    }
}
