package com.renzo.api_gateway.config;

import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//@Component
public class JwtFromCookieWebFilter /* implements WebFilter */ {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("ACCESS_TOKEN");
//        if (cookie != null) {
//            String token = cookie.getValue();
//            exchange.getRequest().mutate()
//                    .headers(headers -> headers.setBearerAuth(token))
//                    .build();
//        }
//        return chain.filter(exchange);
//    }
}
