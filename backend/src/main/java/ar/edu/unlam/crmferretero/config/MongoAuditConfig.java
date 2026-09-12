package ar.edu.unlam.crmferretero.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableMongoAuditing
public class MongoAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
            if (autenticacion == null || !autenticacion.isAuthenticated()
                    || "anonymousUser".equals(autenticacion.getPrincipal())) {
                return Optional.of("sistema");
            }
            return Optional.of(autenticacion.getName());
        };
    }
}
