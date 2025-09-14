package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.CaseFile;
// import java.util.List;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'files' table.
 * A repository already has a number of default queries: count, delete,
 * existsById, findAll, findById, save.
 */
@Repository
public interface FileRepository extends CrudRepository<CaseFile, Integer> {
    Iterable<CaseFile> findByOrderByTitle();

    Iterable<CaseFile> findByStatusOrderByTitle(boolean status);
}
