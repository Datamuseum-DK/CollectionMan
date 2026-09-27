package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.Formula;

/**
 * The locality entity.
 */
@Entity
@Table(name = "django_content_type")
@Data
public class ContentType {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="app_label", length=100)
    @NotBlank(message = "App label is mandatory")
    private String appLabel;

    @Column(length=100)
    @NotBlank(message = "Model is mandatory")
    private String model;

}
