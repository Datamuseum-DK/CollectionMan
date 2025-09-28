package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * The item_class entity.
 */
@Entity
@Data
//@Table(name = "items")
public class ItemClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Level is mandatory")
    private Integer level;

}
