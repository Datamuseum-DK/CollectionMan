package dk.datamuseum.mobilereg;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;

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
     *
     * @param args - all the arguments given on the command line.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Create the audit functionality.
     * I.e., auto-fills the fields creator, created and lastmodified for a record.
     *
     * @return instantiated class, which is added to the bean registry.
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAwareImpl();
    }


    /**
     * Instantiate the PBKDF2 password encoder.
     *
     * @return instantiated class, which is added to the bean registry.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
//      return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Pbkdf2PasswordEncoder encoder = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        encoder.setEncodeHashAsBase64(true);
        return encoder;
    }

    /**
     * Instantiate the special validator for Items.
     *
     * @return instantiated class, which is added to the bean registry.
     */
    //@Bean
    public Validator itemValidator() {
        return new ItemValidator();
    }

}
