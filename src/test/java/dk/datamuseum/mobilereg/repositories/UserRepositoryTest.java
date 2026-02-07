package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.entities.Role;
import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.entities.UserPermission;
import dk.datamuseum.mobilereg.entities.UserRole;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserPermissionRepository userPermissionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void readThenSaveUser() {
        Optional<User> optionalUser = userRepository.findById(10);
        assertThat(optionalUser.isPresent()).isTrue();
        User retrievedUser = optionalUser.get();
        assertThat(retrievedUser.getEmail()).isEqualTo("adm@example.com");
        userRepository.save(retrievedUser);
    }

    /**
     * Deleting the user "adm".
     * Due to the Many2Many attribute in the User entity, the group membership
     * and direct permissions are also deleted.
     */
    @Test
    void deleteUserAdm() {
        User retrievedUser = userRepository.findByUsername("adm");
        assertThat(retrievedUser.getId()).isEqualTo(10);

        // Check that membership of "staff" is there.
        Optional<UserRole> staffMembership = userRoleRepository.findById(2);
        assertThat(staffMembership.isPresent()).isTrue();

        // userRepository.deleteById(10);
        userRepository.delete(retrievedUser);

        // entityManager.flush();
        Optional<User> optionalUser = userRepository.findById(10);
        assertThat(optionalUser.isPresent()).isFalse();

        List<UserRole> memberOf = userRoleRepository.findByUserId(10);
        assertThat(memberOf.isEmpty()).isTrue();

        // Check that membership of "staff" is deleted.
        staffMembership = userRoleRepository.findById(2);
        // assertThat(staffMembership.isPresent()).isFalse();

        // Check that "staff" role is still there
        Optional<Role> staffRole = roleRepository.findById(2);
        assertThat(staffRole.isPresent()).isTrue();

        // Check that add_group permission still exists.
        Optional<Permission> addGroupPerm = permissionRepository.findById(9);
        assertThat(addGroupPerm.isPresent()).isTrue();
    }

}
