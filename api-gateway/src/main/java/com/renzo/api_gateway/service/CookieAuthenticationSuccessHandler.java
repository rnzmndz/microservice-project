package com.renzo.api_gateway.service;

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
    private final String frontendUrl = "http://localhost:4200/";
    private final boolean secureCookies = false; // Set to true in production
    private final String sameSitePolicy = "None"; // Set to "Strict" in production

    public CookieAuthenticationSuccessHandler(ReactiveOAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
                                              Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            return clientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            ).flatMap(client -> {
                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                setAuthCookies(response, client.getAccessToken().getTokenValue(),
                        client.getRefreshToken() != null ? client.getRefreshToken().getTokenValue() : null);

                redirectToFrontend(response);

                return response.setComplete();
            });
        }

        return Mono.empty();
    }

    private void setAuthCookies(ServerHttpResponse response, String accessToken, String refreshToken) {
        // Access token cookie
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .secure(secureCookies)
                .sameSite(sameSitePolicy)
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        response.addCookie(accessCookie);

        // Refresh token cookie (if available)
        if (refreshToken != null) {
            ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                    .httpOnly(true)
                    .secure(secureCookies)
                    .sameSite(sameSitePolicy)
                    .path("/")
                    .maxAge(Duration.ofDays(7))
                    .build();

            response.addCookie(refreshCookie);
        }
    }

    private void redirectToFrontend(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(frontendUrl));
    }
}

