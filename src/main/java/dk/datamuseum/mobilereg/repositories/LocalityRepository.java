package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Locality;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'locality_types' table.
 */
@Repository
public interface LocalityRepository extends ListCrudRepository<Locality, Integer> {

    /**
     * Return all places ordered by type name.
     */
    Iterable<Locality> findByOrderByName();

    @Query("SELECT l FROM Locality l WHERE l.localitytype.id = ?1 ORDER BY l.name")
    List<Locality> findByTypeidOrderByName(int typeid);

    @Query("SELECT l FROM Locality l ORDER BY l.localitytype.title, l.name")
    Iterable<Locality> findByOrderByTypeidByName();
}
