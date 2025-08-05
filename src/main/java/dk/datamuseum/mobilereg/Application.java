package dk.datamuseum.mobilereg;

import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot application class.
 */
@SpringBootApplication 
@EnableAsync
@ComponentScan(basePackages={"dk.datamuseum.mobilereg"})
@EnableJpaRepositories(basePackages="dk.datamuseum.mobilereg.repositories")
@EnableTransactionManagement
@EntityScan(basePackages="dk.datamuseum.mobilereg.entities")
@EnableJpaAuditing
public class Application {

    /**
     * Initialisation of Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Create the audit functionality.
     * I.e., auto-fills the fields creator, created and lastmodified for a record.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAwareImpl();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
//      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        encoder.setEncodeHashAsBase64(true);
        return encoder;
    }

}
