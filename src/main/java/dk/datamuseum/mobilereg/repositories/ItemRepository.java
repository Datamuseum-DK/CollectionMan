package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Item;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface for database queries on the 'items' table.
 * A repository has a number of default queries.
 */
@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {
    
    /**
     * Do a full text search on items.
     * Searches headline and description.
     */
    @Query("SELECT i FROM Item i WHERE i.headline LIKE %?1% OR i.description LIKE %?1% ORDER BY i.headline")
    Page<Item> findByFulltextContaining(String query, Pageable pageable);
    
    /**
     * Get all locations. I.e., all containers that are the top of
     * the hierarchy.
     */
    List<Item> findByPlacementidNull();

    /**
     * Get all items with given producer.
     *
     * @param id - producer id.
     */
    Iterable<Item> findByProduceridOrderByHeadline(int id);

    /**
     * Get all items with given donor.
     *
     * @param id - donor id.
     */
    Iterable<Item> findByDonoridOrderByHeadline(int id);

    /**
     * Find container hierarchy ending with a location.
     * This query is called with the id of the item that you need to know the location of.
     * It will then return the item's container, the container's container and so forth
     * until it reaches a container with a null parent.
     * Use database names for columns.
     *
     * @param id - item id.
     */
    @NativeQuery(value = "WITH RECURSIVE name_tree(itemid,placementid,itemheadline) AS ("
          + " select itemid, placementid, itemheadline from items where itemid = ?1"
          + " union all"
          + " select c.itemid, c.placementid, c.itemheadline"
          + " from items c join name_tree p on p.placementid = c.itemid)"
          + " select items.* from name_tree left join items using(itemid) where name_tree.itemid <> ?1")
    List<Item> findParentContainers(int id);

    /**
     * All items belonging to a file.
     *
     * @param id - item id.
     */
    List<Item> findByFileidOrderByHeadline(int id);

    /**
     * All items belonging to a container.
     *
     * @param id - container id.
     */
    Page<Item> findByPlacementidOrderByHeadline(int id, Pageable pageable);

    /**
     * Find containers above a given level.
     *
     * @param level - the level of the item you want to see possible containers for.
     */
    //@NativeQuery(value = "SELECT items.* FROM items JOIN item_class ON items.itemclassid=item_class.id WHERE level < ?1 ORDER BY itemheadline")
    @Query(value="SELECT i FROM Item i JOIN i.itemClass c WHERE c.level < ?1 ORDER BY i.headline")
    Iterable<Item> findContainers(int level);

    /**
     * Items that were used at an identified place.
     *
     * @param id - sted id.
     */
    List<Item> findByItemusedwhereidOrderByHeadline(int id);

    /**
     * Lookup item on QR code.
     *
     * @param id - qr code.
     */
    Item getByQrcode(int id);

}
