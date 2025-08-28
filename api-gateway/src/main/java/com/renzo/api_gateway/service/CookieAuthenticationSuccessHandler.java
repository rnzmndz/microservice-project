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
// This is working if the updated fails uncomment this
//@Component
//public class CookieAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
//
//    private final ReactiveOAuth2AuthorizedClientService clientService;
//
//    public CookieAuthenticationSuccessHandler(ReactiveOAuth2AuthorizedClientService clientService) {
//        this.clientService = clientService;
//    }
//
//    @Override
//    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange,
//                                              Authentication authentication) {
//
//        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
//
//            return clientService.loadAuthorizedClient(
//                    oauthToken.getAuthorizedClientRegistrationId(),
//                    oauthToken.getName()
//            ).flatMap(client -> {
//
//                String token = client.getAccessToken().getTokenValue();
//
//                ResponseCookie cookie = ResponseCookie.from("ACCESS_TOKEN", token)
//                        .httpOnly(true)
//                        .secure(false) // true if in production
//                        .path("/")
//                        .maxAge(Duration.ofHours(1))
//                        .build();
//
//                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
//                response.addCookie(cookie);
//
//                // Redirect to frontend
//                response.setStatusCode(HttpStatus.FOUND);
//                response.getHeaders().setLocation(URI.create("http://localhost:4200/"));
//
//                return response.setComplete();
//            });
//        }
//
//        return Mono.empty();
//    }
//}

@Component
public class CookieAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final ReactiveOAuth2AuthorizedClientService clientService;

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

                String accessToken = client.getAccessToken().getTokenValue();
                String refreshToken = client.getRefreshToken() != null
                        ? client.getRefreshToken().getTokenValue()
                        : null;

                ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                // ACCESS cookie
                ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None") // Set Strict if prod
                        .path("/")
                        .maxAge(Duration.ofMinutes(2))
                        .build();

                response.addCookie(accessCookie);

                // REFRESH cookie (optional, only if present)
                if (refreshToken != null) {
                    ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                            .httpOnly(true)
                            .secure(true)
                            .sameSite("None")
                            .path("/")
                            .maxAge(Duration.ofDays(7))
                            .build();

                    response.addCookie(refreshCookie);
                }

                // Redirect back to frontend app
                response.setStatusCode(HttpStatus.FOUND);
                response.getHeaders().setLocation(URI.create("http://localhost:4200/"));

                return response.setComplete();
            });
        }

        return Mono.empty();
    }
}

