package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.ActivityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'activity_types' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ActivityTypeRepository extends CrudRepository<ActivityType, Integer> {


}

