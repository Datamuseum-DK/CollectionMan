package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.ItemClass;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'itemclass' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ItemClassRepository extends CrudRepository<ItemClass, Integer> {

    /**
     * Get all container types.
     */
    @Query("SELECT a FROM ItemClass a INNER JOIN (SELECT MAX(level) level FROM ItemClass) b ON a.level < b.level")
    List<ItemClass> findAllContainerTypes();

    /**
     * Get all artefact types.
     * I.e., those with no types at a lower level.
     */
    @Query("SELECT a FROM ItemClass a INNER JOIN (SELECT MAX(level) level FROM ItemClass) b ON a.level = b.level")
    List<ItemClass> findAllArtefactTypes();

    /**
     * Find all item classes that can be a parent.
     */
    List<ItemClass> findByLevelGreaterThan(Integer level);

    /**
     * Get the classes ordered by the highest level first.
     */
    @Query("SELECT a FROM ItemClass a ORDER BY level DESC")
    List<ItemClass> findAllOrderByLevelDesc();

}
