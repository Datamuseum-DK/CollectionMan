package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.RolePermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'role permissions' table.
 * A repository has a number of default queries.
 */
@Repository
public interface RolePermissionRepository extends CrudRepository<RolePermission, Integer> {

}

