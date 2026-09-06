package dk.datamuseum.mobilereg.repositories;

import java.util.List;
import dk.datamuseum.mobilereg.entities.ReverseLink;
import dk.datamuseum.mobilereg.entities.ReverseLinkId;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'reverse_links' table.
 */
@Repository
public interface ReverseLinkRepository extends ListCrudRepository<ReverseLink, ReverseLinkId> {
    /**
     * List all items pointing to item
     */
    List<ReverseLink> findByItemidto(int itemidto);

    void deleteByItemidfrom(int itemidfrom);
}
