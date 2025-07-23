package com.renzo.auth_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class FeignOAuth2Interceptor implements RequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public FeignOAuth2Interceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    public void apply(RequestTemplate template) {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak-client-credentials")
                .principal("auth-service") // just a string ID
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
        String token = client.getAccessToken().getTokenValue();

        template.header("Authorization", "Bearer " + token);
    }
}

