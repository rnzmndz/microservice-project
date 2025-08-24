package com.renzo.api_gateway.service;

import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CookieToAuthHeaderFilter implements WebFilter, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpCookie accessCookie = exchange.getRequest()
                .getCookies()
                .getFirst("ACCESS_TOKEN");

        if (accessCookie != null) {
            String token = accessCookie.getValue();
            exchange = exchange.mutate()
                    .request(r -> r.headers(h -> h.setBearerAuth(token)))
                    .build();

            exchange.getRequest().getHeaders().forEach((k,v) -> System.out.println(k + " = " + v));

        }


        return chain.filter(exchange);
    }
}
