package dk.datamuseum.mobilereg.repositories;

import java.util.List;
import dk.datamuseum.mobilereg.entities.UserRole;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'user permissions' table.
 * A repository has a number of default queries.
 */
@Repository
public interface UserRoleRepository extends ListCrudRepository<UserRole, Integer> {

    List<UserRole> findByUserId(int userId);
}

