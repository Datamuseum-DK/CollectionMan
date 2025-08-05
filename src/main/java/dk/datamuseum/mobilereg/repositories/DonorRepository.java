package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Donor;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'donators' table.
 * A repository has a number of default queries.
 */
@Repository
public interface DonorRepository extends CrudRepository<Donor, Integer> {
    /**
     * List all producers and order by name.
     */
    Iterable<Donor> findByOrderByName();    

    /**
     * List all producers matching query in title.
     */
    @Query("SELECT d FROM Donor d WHERE d.name LIKE %?1% OR d.institution LIKE %?1% OR d.email LIKE %?1% ORDER BY d.name")
    Iterable<Donor> findByQuerytext(String q);
}
