package com.renzo.api_gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Component
public class CookieAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final ReactiveOAuth2AuthorizedClientService clientService;

    public CookieAuthenticationSuccessHandler(ReactiveOAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                              Authentication authentication) {

        System.out.println("➡️ Handler triggered");

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            System.out.println("✅ Detected OAuth2AuthenticationToken");

            return clientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            ).flatMap(client -> {
                System.out.println("➡️ Inside flatMap");

                if (client == null || client.getAccessToken() == null) {
                    System.out.println("❌ No access token found in authorized client");
                    return Mono.empty(); // Don't proceed if token is missing
                }

                String token = client.getAccessToken().getTokenValue();
                System.out.println("✅ Access token retrieved: " + token);

                ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .maxAge(Duration.ofHours(1))
                        .build();

                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                response.addCookie(cookie);

                // Redirect to frontend
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create("http://localhost:4200/"));

                return response.setComplete();
            });
        } else {
            System.out.println("❌ Not an OAuth2AuthenticationToken: " + authentication.getClass().getName());
        }

        return Mono.empty();
    }
}
