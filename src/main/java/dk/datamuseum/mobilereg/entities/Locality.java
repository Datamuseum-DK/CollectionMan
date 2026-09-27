package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 * The locality entity.
 */
@Entity
@Table(name = "localities")
@Data
public class Locality implements Comparable<Locality> {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length=100)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(length=65536)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="typeid", nullable=false)
    private LocalityType localitytype;

    /** Count how many items use this locality. */
    @Formula("(SELECT COUNT(*) FROM items WHERE items.itemusedwhereid = id)")
    private Long totalItems;

    @Override
    public int compareTo(Locality m1) {
        return this.getName().compareToIgnoreCase(m1.getName());
    }

    @Override
    public String toString() {
        return Integer.valueOf(id) + ":" + name;
    }
}
