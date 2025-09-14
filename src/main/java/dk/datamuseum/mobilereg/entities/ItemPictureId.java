package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

/**
 * A combined key for itemids and pictureids.
 */
@Data
public class ItemPictureId {

    private int itemid;

    private int pictureid;

}
