package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Many-to-many relation between items and subjects.
 */
@Entity
@IdClass(ItemSubjectId.class)
@Table(name = "items_itemsubject")
@Data
public class ItemSubject {

    @Id
    @NotNull(message = "Item id is mandatory")
    @Column(name = "items_id")
    private int itemid;

    @Id
    @NotNull(message = "Subject id is mandatory")
    @Column(name = "subjects_id")
    private int subjectid;
}
