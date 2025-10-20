package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.ItemStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'itemstatus' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ItemStatusRepository extends CrudRepository<ItemStatus, Integer> {

}
