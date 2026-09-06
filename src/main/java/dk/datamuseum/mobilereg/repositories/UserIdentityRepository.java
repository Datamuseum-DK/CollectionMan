package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.UserIdentity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Interface for database queries on the 'auth_users_identity' table.
 * A repository has a number of default queries.
 */
@Repository
public interface UserIdentityRepository
        extends ListCrudRepository<UserIdentity, Integer> {

    Optional<UserIdentity> findByProviderAndSubject(
            String provider,
            String subject);
}
