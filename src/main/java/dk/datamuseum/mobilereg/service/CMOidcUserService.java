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
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

/**
 * An OIDC adapter for Google.
 */
@Slf4j
@Service
public class CMOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepository;
    private final UserIdentityRepository identityRepository;

    private final OidcUserService delegate = new OidcUserService();

    public CMOidcUserService(
            UserRepository userRepository,
            UserIdentityRepository identityRepository) {

        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest request)
            throws OAuth2AuthenticationException {

        OidcUser oidcUser = delegate.loadUser(request);
        log.info("OIDC User: {}", oidcUser);

        String provider;
        String email;

        String subject = oidcUser.getSubject();
        if (subject == null) {
            log.error("Not Google");
            //subject =
            email = "xx@gmail.com";
            provider = "unknown";
        } else {
            provider = "google";
            email = oidcUser.getEmail();
        }

        User user = findOrCreateUser(oidcUser, provider, subject, email);

        Set <SimpleGrantedAuthority> mappedAuthorities = new HashSet<>();
        addRolesFromDB(user, mappedAuthorities);
        addPermissions(user, mappedAuthorities);

        return new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(),
                oidcUser.getUserInfo(), "email");
    }

    /**
     * Find or create user in the database.
     * Look the subject up in the identity table. If not found, then create it.
     */
    private User findOrCreateUser(OidcUser oidcUser,
                String provider, String subject, String email) {

        return identityRepository.findByProviderAndSubject(provider, subject)
                .map(UserIdentity::getUser)
                .orElseGet(() -> createUser(oidcUser, provider, subject, email));
    }

    /**
     * Create account in both User and Identity tables.
     */
    private User createUser(OidcUser oidcUser, String provider, String subject,
                String email) {

        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();

                newUser.setEmail(email);
                newUser.setPassword("dummy");
                newUser.setUsername(generateUsername(email));
                newUser.setFirstName(oidcUser.getGivenName());
                newUser.setLastName(oidcUser.getFamilyName());
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
