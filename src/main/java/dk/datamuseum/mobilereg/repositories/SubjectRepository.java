package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Subject;
// import java.util.List;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'producers' table.
 * A repository has a number of default queries.
 */
@Repository
public interface SubjectRepository extends CrudRepository<Subject, Integer> {
    /**
     * List all subjects and order by title.
     */
    Iterable<Subject> findByOrderByTitle();
}
