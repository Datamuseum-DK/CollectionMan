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
 * A repository already has a number of default queries: count, delete,
 * existsById, findAll, findById, save.
 */
@Repository
public interface ItemRepository extends CrudRepository<Item, Integer> {

    /**
     * Do a full text search on items.
     * Searches headline and description.
     *
     * @param query - the query.
     * @param pageable - information about which page the user wants returned.
     * @return a page of hits.
     */
    @Query("SELECT i FROM Item i WHERE i.headline LIKE %?1% OR i.description LIKE %?1% OR i.itemserialno LIKE %?1% ORDER BY i.headline")
    Page<Item> findByFulltextContaining(String query, Pageable pageable);

    /**
     * Get all locations. I.e., all containers that are the top of
     * the hierarchy.
     *
     * @return a list of items - potentially with no members.
     */
    List<Item> findByPlacementidNull();

    /**
     * Get all locations above a certain item class.
     *
     * @param level - the level of the requesting parent item.
     * @return a list of items - potentially with no members.
     */
    @Query("SELECT i FROM Item i WHERE i.itemClass.level <= ?1")
    List<Item> findByItemclassLevel(int level);

    /**
     * Lookup on primary key or QR code.
     *
     * @return a list of items - potentially with no members.
     */
    List<Item> findByIdOrQrcode(int id, Integer qrcode);

    /**
     * Get all items with given producer.
     *
     * @param id - producer id.
     * @return an iteration of items - potentially with no members.
     */
    Iterable<Item> findByProduceridOrderByHeadline(int id);

    /**
     * Get all items with given donor.
     *
     * @param id - donor id.
     * @return an iteration of items - potentially with no members.
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
     * @return a list of items - potentially with no members.
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
     * @return a list of items - potentially with no members.
     */
    List<Item> findByFileidOrderByHeadline(int id);

    /**
     * All items belonging to a container.
     *
     * @param id - container id.
     * @return a page of hits.
     */
    Page<Item> findByPlacementidOrderByHeadline(int id, Pageable pageable);

    /**
     * Find containers above a given level.
     * Used for hierarchial navigation when moving. You don't want to present
     * containers that can't contain the item to move.
     *
     * @param parentid the returned items must have this parent.
     * @param maxLevel - the level of the item you want to see possible containers for.
     * @return an iteration of items - potentially with no members.
     */
    @Query(value = "SELECT i FROM Item i JOIN i.itemClass c WHERE i.placementid = ?1 AND c.level < ?2 ORDER BY i.headline")
    Iterable<Item> findContainers(int parentid, int maxLevel);

    /**
     * Items that were used at an identified place.
     *
     * @param id - sted id.
     * @return a list of items - potentially with no members.
     */
    List<Item> findByItemusedwhereidOrderByHeadline(int id);

    /**
     * Lookup item on QR code.
     *
     * @param id - qr code.
     * @return the item.
     */
    Item getByQrcode(int id);

    /**
     * List top 50 items in reverse order of last modified.
     *
     * @return an iteration of items - potentially with no members.
     */
    //@Query(value="SELECT TOP 50 i FROM Item i ORDER BY i.lastmodified DESC")
    Iterable<Item> findFirst50ByOrderByLastmodifiedDesc();

    Page<Item> findBySubjectsIsNullOrderByHeadline(Pageable pageable);
}
