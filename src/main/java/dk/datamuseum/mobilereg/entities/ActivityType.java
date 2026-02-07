package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * The ActivityType entity. This is a list of activity types.
 * Some are created by the system as changelogs, others by users.
 */
@Entity
@Table(name = "activity_types")
@Data
public class ActivityType {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is mandatory")
    @Column
    private String name;

    @Column
    private Boolean systemact;

    @Column
    private Boolean visible;

    @Column(length = 65535)
    private String placeholder;

}
