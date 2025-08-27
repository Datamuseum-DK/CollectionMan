package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.ItemPicture;
// import java.util.List;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'items_itempicture' table.
 * The table implements a many-to-may relation between items and pictures.
 * This means that a picture can be used for more than one item. This only
 * makes sense if the picture displays multiple items.
 */
@Repository
public interface ItemPictureRepository extends CrudRepository<ItemPicture, Integer> {
    /**
     * Return all item relations by picture id.
     */
    Iterable<ItemPicture> findByPictureid(int pictureid);
}
