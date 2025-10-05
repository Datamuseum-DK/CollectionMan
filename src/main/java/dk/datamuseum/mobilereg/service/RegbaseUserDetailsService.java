package dk.datamuseum.mobilereg.service;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.entities.Role;
import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;
import dk.datamuseum.mobilereg.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.stream.Collectors;

/**
 * UserDetailsService providing information from the registration database.
 * Class does not check the password. This is done in the DaoAuthenticationProvider.
 */
@Slf4j
@Service
public class RegbaseUserDetailsService implements UserDetailsService {

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    /**
     * Constructor.
     *
     * @param permissionRepository - repository for direct permissions.
     * @param userRepository - registration database.
     */
    public RegbaseUserDetailsService(PermissionRepository permissionRepository,
                    UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
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

    /*
     * If user is a super user, then get all possible permissions.
     */
    private Iterable<Permission> allPermissions() {
        return permissionRepository.findAll();
    }

    private void addPermissions(User user, Set<SimpleGrantedAuthority> authorities) {
        List<Permission> permissions = user.getPermissions();
        for (Permission permission : permissions) {
            authorities.add(permissionAsAuth(permission));
        }
    }

    /**
     * Look up the user in the database either by login name or email.
     * If successful, then populates the authority list with information from the database.
     * All authenticated users have the VIEWER role.
     *
     * @param username - the name of the user as entered in the authentication.
     * @return an updated UserDetails record.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }

        if (user != null) {
            Set <SimpleGrantedAuthority> authorities = new HashSet<>();

            authorities.add(new SimpleGrantedAuthority("ROLE_VIEWER"));

            if (user.isSuperuser()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_SUPERUSER"));
                for (Permission permission : allPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(permission.getCodename().toUpperCase()));
                }
            }

            addRolesFromDB(user, authorities);
            addPermissions(user, authorities);

            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(),
                    user.isActive(), // enabled
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    authorities);
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }
}
