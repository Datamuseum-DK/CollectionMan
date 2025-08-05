package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Picture;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Class for database queries on the 'pictures' table.
 * A repository has a number of default queries.
 * @see <a href="https://docs.spring.io/spring-data/data-commons/docs/3.0.0/reference/html/#repositories">Working with Spring Data Repositories</a>
 */
@Repository
public interface PictureRepository extends CrudRepository<Picture, Integer> {
}
