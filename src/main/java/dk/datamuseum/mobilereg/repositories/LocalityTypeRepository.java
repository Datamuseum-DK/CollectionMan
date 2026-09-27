package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.LocalityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'locality_types' table.
 */
@Repository
public interface LocalityTypeRepository extends CrudRepository<LocalityType, Integer> {

    /**
     * Return all places ordered by type name.
     */
    Iterable<LocalityType> findByOrderByTitle();
}
