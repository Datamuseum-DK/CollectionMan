package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The picture entity.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pictures_dj")
@Data
public class Picture {

    // pictureid           = models.AutoField(verbose_name="billede nr.", primary_key=True)
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int pictureid;

    // picturetext         = models.TextField(verbose_name="beskrivelse", )
    @NotBlank(message = "Title is mandatory")
    @Column(name = "picturetext", length=65535)
    private String title;

    // pictureregisteredby = models.CharField(max_length=12, null=True, verbose_name="oprettet af")
    @CreatedBy
    @Column(name="pictureregisteredby", length=32)
    private String creator;

    // pictureregistered   = models.DateTimeField(verbose_name="registreret", auto_now_add=True) # date NOT NULL default '0000-00-00',
    @CreatedDate
    @Column
    private LocalDateTime pictureregistered;
    
    // lastmodified        = models.DateTimeField(verbose_name='rettet', auto_now=True)
    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    // pictureoriginal     = models.ImageField(upload_to='pictureoriginal', verbose_name="billede", help_text="uploadet billede i oprindelig størrelse" ) # mediumblob
    /** Path to original uploaded picture. */
    @Column(name = "pictureoriginal")
    private String original;

    // picturemedium       = models.ImageField(upload_to='picturemedium', editable=False, ) # mediumblob
    @Column(name = "picturemedium")
    private String medium;

    // picturelow          = models.ImageField(upload_to='picturelow', editable=False, ) # mediumblob
    @Column(name = "picturelow")
    private String low;

    @Column(name = "itemid")
    private Integer itemid;

    /* Reference to variable in Item.java */
    @ManyToMany(mappedBy = "pictures")
    private List<Item> items;
}
