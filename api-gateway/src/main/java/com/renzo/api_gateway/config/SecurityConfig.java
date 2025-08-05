package com.renzo.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String jwtIssuerUri;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .cors(cors -> {}) // Enable CORS
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() //recently added
                        .pathMatchers("/public/**", "/auth/**", "/webjars/swagger-ui/**", "/api-docs/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/employee-service/v3/api-docs").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint((exchange, ex) -> {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(HttpStatus.UNAUTHORIZED);

                            // Add CORS headers even on error
                            response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:4200");
                            response.getHeaders().add("Access-Control-Allow-Credentials", "true");

                            return response.setComplete();
                        })
                );

        return http.build();
    }


    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Use ReactiveJwtDecoders for reactive applications
        return ReactiveJwtDecoders.fromIssuerLocation(jwtIssuerUri);
    }
}
