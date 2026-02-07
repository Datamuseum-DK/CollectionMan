package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Activity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'activities' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ActivityRepository extends CrudRepository<Activity, Integer> {
    /**
     * Find all activities for a specific item. This can be expanded to
     * distinguish between public and private activities.
     *
     * @param itemid - the item of interest.
     */
    Iterable<Activity> findByItemidOrderByCreated(int itemid);
}

