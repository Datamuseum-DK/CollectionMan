package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'permission' table.
 * This table is originally from Django.
 */
@Repository
public interface PermissionRepository extends CrudRepository<Permission, Integer> {
    Iterable<Permission> findByOrderByContentTypeIdAscNameAsc();
}
