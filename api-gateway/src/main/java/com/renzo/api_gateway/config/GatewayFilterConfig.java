package com.renzo.api_gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;

@Configuration
public class GatewayFilterConfig {

    // No use can delete if needed
//    @Bean
//    public GlobalFilter tokenRelayFilter(ReactiveOAuth2AuthorizedClientManager manager) {
//        return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
//                .flatMap(ctx -> Mono.justOrEmpty(ctx.getAuthentication()))
//                .flatMap(auth -> manager.authorize(
//                        OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
//                                .principal(auth)
//                                .build()
//                ))
//                .map(OAuth2AuthorizedClient::getAccessToken)
//                .map(token -> exchange.mutate()
//                        .request(r -> r.headers(h -> h.setBearerAuth(token.getTokenValue())))
//                        .build()
//                )
//                .defaultIfEmpty(exchange)
//                .flatMap(chain::filter);
//    }

    @Bean
    public GlobalFilter cookieToAuthHeaderFilter() {
        return (exchange, chain) -> {
            HttpCookie accessCookie = exchange.getRequest()
                    .getCookies()
                    .getFirst("ACCESS_TOKEN");

            if (accessCookie != null) {
                String token = accessCookie.getValue();
                exchange = exchange.mutate()
                        .request(r -> r.headers(h -> h.setBearerAuth(token)))
                        .build();
            }

            return chain.filter(exchange);
        };
    }
}