package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "sted")
@Data
public class Sted {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(length=60)
    private String stednavn;

    /* Reference to variable in Item.java */
    //@OneToMany(mappedBy = "usedwhere")
    //private List<Item> items;

    @Formula("(SELECT COUNT(*) FROM items WHERE items.itemusedwhereid = id)")
    private Long totalItems;

}
