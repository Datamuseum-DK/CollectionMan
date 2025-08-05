package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "producers")
@Data
public class Producer {

    // producerid          = models.AutoField(verbose_name="producent nr.", primary_key=True)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int producerid;

    // creator             = models.CharField(max_length=12, null=False, verbose_name="oprettet af")
    @CreatedBy
    @Column(length=32)
    private String creator;

    // created             = models.DateTimeField(verbose_name='oprettet', help_text="tidspunkt for registrering", auto_now_add=True)
    @CreatedDate
    @Column
    private LocalDateTime created;

    // lastmodified        = models.DateTimeField(verbose_name='rettet', auto_now=True)
    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    // producertitle       = models.CharField('navn', help_text="producent/fabrikant/forfatter", max_length=255, null=True, blank=False)
    @NotBlank(message = "Title is mandatory")
    @Column(name = "producertitle")
    private String title;

    // producerdescription = models.TextField('beskrivelse', null=True, blank=True) # text,
    @Column(name = "producerdescription")
    private String description;

}
