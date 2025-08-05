package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'auth_group' table.
 * The Django has the concept of groups, but the way they are used, they are roles.
 * Therefore in this Spring Boot application, the name 'Role' is used to match the terminology
 * of the framework.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    /**
     * Return all available roles ordered by name.
     */
    Iterable<Role> findByOrderByName();

    /**
     * Find a role by name.
     *
     * @param name - name of role to look up.
     */
    Role findByName(String name);
}
