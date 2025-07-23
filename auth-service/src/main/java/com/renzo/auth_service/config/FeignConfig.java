package com.renzo.auth_service.config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignConfig {

    @Bean
    public HttpMessageConverters feignHttpMessageConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    public Encoder feignEncoder() {
        return new SpringEncoder(() -> feignHttpMessageConverters());
    }

    @Bean
    public Decoder feignDecoder() {
        return new SpringDecoder(() -> feignHttpMessageConverters());
    }
}