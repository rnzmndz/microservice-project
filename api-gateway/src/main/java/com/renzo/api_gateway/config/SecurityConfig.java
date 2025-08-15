package com.renzo.api_gateway.config;

import com.renzo.api_gateway.service.CookieAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;

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
                );

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return ReactiveJwtDecoders.fromIssuerLocation(jwtIssuerUri);
    }

    @Bean
    public GlobalFilter forwardUserClaimsFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/webjars/swagger-ui")) {
                return chain.filter(exchange);
            }

            return exchange.getPrincipal()
                    .flatMap(principal -> {
                        if (principal instanceof JwtAuthenticationToken jwtAuth) {
                            Jwt jwt = jwtAuth.getToken();
                            ServerWebExchange mutatedExchange = exchange.mutate().request(r -> r.headers(headers -> {
                                headers.add("X-User-Id", jwt.getClaimAsString("sub"));
                                headers.add("X-User-Roles", String.join(",", jwt.getClaimAsStringList("roles")));
                            })).build();
                            return chain.filter(mutatedExchange);
                        }
                        return chain.filter(exchange);
                    });
        };
    }



}
