package dk.datamuseum.mobilereg.model;

import lombok.Data;

/**
 * Slim Item class for parents of an item.
 */
@Data
public class ItemParent {

    private int id;
    private Integer placementid;
    private String itemheadline;

    public ItemParent(int id, Integer placementid, String itemheadline) {
        this.id = id;
        this.placementid = placementid;
        this.itemheadline = itemheadline;
    }
}
