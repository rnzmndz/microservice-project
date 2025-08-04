package com.renzo.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Profile({"dev", "cloud"})
@EnableWebFluxSecurity
public class DevSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(new WebFilter() {
                    @Override
                    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                        var auth = new UsernamePasswordAuthenticationToken(
                                "devUser", null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        var context = ReactiveSecurityContextHolder.withAuthentication(auth);
                        return chain.filter(exchange).contextWrite(context);
                    }
                }, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}

