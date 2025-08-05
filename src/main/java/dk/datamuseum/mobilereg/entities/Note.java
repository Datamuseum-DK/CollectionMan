package dk.datamuseum.mobilereg.entities;

import java.util.Date;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.*;
import lombok.Data;

@Entity
@Table(name = "notes")
@Data
public class Note {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Content is mandatory")
    private String content;
    
    private LocalDateTime created;

    private String creator;

    /** Link to item for the note. */
    @NotNull(message = "Item id is mandatory")
    private Integer itemId;
}
