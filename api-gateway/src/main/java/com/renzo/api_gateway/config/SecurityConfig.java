package com.renzo.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CookieAuthenticationSuccessHandler successHandler;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String jwtIssuerUri;

    public SecurityConfig (CookieAuthenticationSuccessHandler successHandler){
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() //recently added
                        .pathMatchers("/public/**",
                                "/auth/**", "/v3/api-docs/**","/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/webjars/swagger-ui/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/employee-service/v3/api-docs").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authenticationSuccessHandler(successHandler)) // Recently added
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
//                        .authenticationEntryPoint((exchange, ex) -> {
//                            ServerHttpResponse response = exchange.getResponse();
//                            HttpHeaders headers = response.getHeaders();
//
//                            // ✅ Allow CORS on error responses
//                            headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
//                            headers.add("Access-Control-Allow-Credentials", "true");
//                            headers.add("Access-Control-Allow-Headers", "*");
//                            headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//
//                            // ✅ Set status to 401 if not already committed
//                            if (!response.isCommitted()) {
//                                response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                            }
//
////                            // ✅ Log the error for debugging (optional)
////                            ex.printStackTrace();
//
//                            return response.setComplete();
//                        })
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        // Use ReactiveJwtDecoders for reactive applications
        return ReactiveJwtDecoders.fromIssuerLocation(jwtIssuerUri);
    }
}
