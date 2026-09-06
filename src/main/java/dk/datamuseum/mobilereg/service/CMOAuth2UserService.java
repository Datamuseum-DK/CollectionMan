package dk.datamuseum.mobilereg.service;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.entities.Role;
import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.entities.UserIdentity;
import dk.datamuseum.mobilereg.repositories.UserIdentityRepository;
import dk.datamuseum.mobilereg.repositories.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * An OAuth2 adapter for GitHub and other OAuth2 services.
 */
@Slf4j
@Service
public class CMOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserIdentityRepository identityRepository;

    public CMOAuth2UserService(
            UserRepository userRepository,
            UserIdentityRepository identityRepository) {

        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(request);
        log.info("OIDC User: {}", oauthUser);

        String provider;
        String email;

        String subject = oauthUser.getAttribute("login");
        if (subject == null) {
            log.error("Not GitHub");
            email = "xx@gmail.com";
            provider = "google";
        } else {
            provider = "github";
            email = oauthUser.getAttribute("email");
        }

        User user = findOrCreateUser(provider, subject, email);

        Set <SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
        addRolesFromDB(user, mappedAuthorities);
        addPermissions(user, mappedAuthorities);

        return new DefaultOAuth2User(mappedAuthorities,
            oauthUser.getAttributes(), "login");
    }

    /**
     * Find or create user in the database.
     * Look the subject up in the identity table. If not found, then create it.
     */
    private User findOrCreateUser(String provider, String subject, String email) {

        return identityRepository.findByProviderAndSubject(provider, subject)
                .map(UserIdentity::getUser)
                .orElseGet(() -> createUserGH(provider, subject, email));
    }

    /**
     * Create account in both User and Identity tables.
     */
    private User createUserGH(String provider, String subject, String email) {

        User user = userRepository.findByUsername(subject)
            .orElseGet(() -> {
                User newUser = new User();

                newUser.setEmail("dummy@dummy");
                newUser.setPassword("dummy");
                newUser.setUsername(subject);
                newUser.setFirstName("Fornavn");
                newUser.setLastName("Efternavn");
                newUser.setActive(true);
                newUser.setDateJoined(LocalDateTime.now());
                newUser.setRoles(new ArrayList<Role>());
                newUser.setPermissions(new ArrayList<Permission>());

                return userRepository.save(newUser);
            });
        createIdentity(user, provider, subject);
        return user;
    }

    private void createIdentity(User user, String provider, String subject) {
        UserIdentity identity = new UserIdentity();

        identity.setUser(user);
        identity.setProvider(provider);
        identity.setSubject(subject);

        identityRepository.save(identity);
    }

    private String generateUsername(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    /*
     * Add roles and permissions from roles.
     */
    private void addRolesFromDB(User user, Set<SimpleGrantedAuthority> authorities) {
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            authorities.add(roleAsAuth(role));
            List<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                authorities.add(permissionAsAuth(permission));
            }

        }
    }

    private void addPermissions(User user, Set<SimpleGrantedAuthority> authorities) {
        List<Permission> permissions = user.getPermissions();
        for (Permission permission : permissions) {
            authorities.add(permissionAsAuth(permission));
        }
    }

    /*
     * Create an Authority from a role.
     *
     * @param role - the role entity.
     * @return Authority
     */
    private SimpleGrantedAuthority roleAsAuth(Role role) {
        return new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase());
    }

    /*
     * Create an Authority from a permission.
     *
     * @param permission - the permission entity.
     * @return Authority
     */
    private SimpleGrantedAuthority permissionAsAuth(Permission permission) {
        return new SimpleGrantedAuthority(permission.getCodename().toUpperCase());
    }

}
