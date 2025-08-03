package com.renzo.auth_service.audit;

//import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class CustomAuditorAware /* implements AuditorAware<String> */ {

//    @Override
//    public Optional<String> getCurrentAuditor() {
//        return UserContext.getUser().or(() -> Optional.of("system"));
//    }
}