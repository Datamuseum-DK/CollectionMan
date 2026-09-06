package dk.datamuseum.mobilereg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Log authentications.
 */
@Slf4j
@Component
public class LoggingAuthenticationProvider extends DaoAuthenticationProvider {

    final static DateTimeFormatter CUSTOM_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /*
     * super(passwordEncoder) is deprecated, but super() calls the wrong constructor.
     * FIXME when unable to compile.
     */
    public LoggingAuthenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        super.setUserDetailsService(userDetailsService);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal,
                Authentication authentication, UserDetails user) {
        Authentication auth = super.createSuccessAuthentication(principal, authentication, user);
        log.info("Authenticated: {} at {}", user.getUsername(), LocalDateTime.now().format(CUSTOM_FORMATTER));
        return auth;
    }

}
