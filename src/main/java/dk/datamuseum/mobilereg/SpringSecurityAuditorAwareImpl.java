package dk.datamuseum.mobilereg;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

/**
 * Implement security audit.
 */
public class SpringSecurityAuditorAwareImpl implements AuditorAware<String> {
    
    /**
     * Get the name of the authenticated principal.
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }
}
