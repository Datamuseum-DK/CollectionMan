package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 * The locality type entity.
 * A type can be "Municipalities", "Companies", "Countries", etc.
 */
@Entity
@Table(name = "locality_types")
@Data
public class LocalityType {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length=100)
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(length=65536)
    private String description;

    @OneToMany(orphanRemoval = true, mappedBy="localitytype")
    @OrderBy("name")
    private List<Locality> localities;

    @Formula("(SELECT COUNT(*) FROM localities WHERE localities.typeid = id)")
    private Long totalItems;

}
