package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.UserPermission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'user permissions' table.
 * A repository has a number of default queries.
 */
@Repository
public interface UserPermissionRepository extends CrudRepository<UserPermission, Integer> {

}

