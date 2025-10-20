package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * The item_statuses entity.
 */
@Entity
@Data
@Table(name = "item_statuses")
public class ItemStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Public is mandatory")
    private Boolean visible;

}
