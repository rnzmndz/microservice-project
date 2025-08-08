package com.renzo.api_gateway.service;

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
