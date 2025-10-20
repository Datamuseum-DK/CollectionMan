package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
//import jakarta.persistence.FetchType;
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
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The subjects entity.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "subjects")
@Data
public class Subject {

    // subjectid           = models.AutoField(verbose_name="emnegruppe nr.", primary_key=True)
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subjectid;

    // creator             = models.CharField(max_length=12, null=False, verbose_name="oprettet af")
    @CreatedBy
    @Column(length = 32)
    private String creator;

    // created             = models.DateTimeField(verbose_name='oprettet', help_text="tidspunkt for registrering", auto_now_add=True)
    @CreatedDate
    @Column
    private LocalDateTime created;

    // lastmodified        = models.DateTimeField(verbose_name='rettet', auto_now=True)
    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    // subjecttitle        = models.CharField('navn', max_length=255, null=True, blank=False) # varchar(255) default NULL,
    @NotBlank(message = "Title is mandatory")
    @Column(name = "subjecttitle")
    private String title;

    // subjectdescription  = models.TextField('beskrivelse', null=True, blank=True) # text,
    @Column(name = "subjectdescription", length = 65535)
    private String description;

    /* Reference to variable in Item.java */
    @ManyToMany(mappedBy = "subjects")
    @OrderBy(value = "headline ASC")
    private List<Item> items;

    @Formula("(SELECT COUNT(*) FROM items_itemsubject iis WHERE iis.subjects_id = subjectid)")
    private Long totalItems;

    @Override
    public String toString() {
        return this.title;
    }

    /*
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subjectid == null) ? 0 : subjectid.hashCode());
        return result;
    }
    */

 /*
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Subject other = (Subject) obj;
        if (subjectid != other.subjectid)
            return false;
        return true;
    }
    */
}
