package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@IdClass(ItemPictureId.class)
@Table(name = "items_itempicture")
@Data
public class ItemPicture {

    @Id
    @NotNull(message = "Item id is mandatory")
    @Column(name = "items_id")
    private int itemid;

    @Id
    @NotNull(message = "Picture id is mandatory")
    @Column(name = "pictures_id")
    private int pictureid;

}
