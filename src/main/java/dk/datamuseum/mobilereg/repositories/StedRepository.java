package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Sted;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'sted' table.
 * A repository has a number of default queries.
 */
@Repository
public interface StedRepository extends CrudRepository<Sted, Integer> {

    /**
     * Return all places ordered by place name.
     */
    Iterable<Sted> findByOrderByStednavn();
}

