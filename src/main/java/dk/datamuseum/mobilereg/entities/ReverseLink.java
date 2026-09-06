package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Many-to-many relation between items and other items.
 * Populated by scanning for [[genstand:\d]] in the text areas. This can be
 * used to show a What links to here.
 */
@Entity
@IdClass(ReverseLinkId.class)
@Table(name = "reverse_links")
@Data
public class ReverseLink {

    @Id
    @NotNull(message = "From item id is mandatory")
    @Column(name = "itemidfrom")
    private int itemidfrom;

    @Id
    @NotNull(message = "To item id is mandatory")
    @Column(name = "itemidto")
    private int itemidto;
}
