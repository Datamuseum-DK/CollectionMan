package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Interface for database queries on the 'auth_users' table.
 * A repository has a number of default queries.
 */
@Repository
public interface UserRepository extends ListCrudRepository<User, Integer> {

    /**
     * Get all users in the database ordered by user name.
     */
    List<User> findByOrderByUsername();

    /**
     * Lookup a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Lookup a user by email.
     * This is used for authentication, so the users can enter their email.
     */
    Optional<User> findByEmail(String email);
}

