package com.renzo.auth_service;

import com.renzo.auth_service.config.FeignOAuth2Interceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients(
		basePackages = "com.renzo",
		defaultConfiguration = FeignOAuth2Interceptor.class
)
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
