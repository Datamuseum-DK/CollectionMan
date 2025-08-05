package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Producer;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'producers' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ProducerRepository extends CrudRepository<Producer, Integer> {
    /**
     * List all producers and order by title.
     */
    Iterable<Producer> findByOrderByTitle();

    /**
     * List all producers matching query in title.
     */
    @Query("SELECT p FROM Producer p WHERE p.title LIKE %?1% OR p.description LIKE %?1% ORDER BY p.title")
    Iterable<Producer> findByQuerytext(String q);
}
