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
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "donators")
@Data
public class Donor {
    
    @Id
    @Column(name = "donatorid")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @CreatedBy
    @Column(length=32)
    private String creator;

    // created             = models.DateTimeField(verbose_name='oprettet', help_text="tidspunkt for registrering", auto_now_add=True)
    @CreatedDate
    @Column
    private LocalDateTime created;

    // lastmodified        = models.DateTimeField(verbose_name='sidst rettet', auto_now=True)
    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    @Column(name = "donatorinstitution")
    private String institution;

    @Column(name = "donatorposition")
    private String position;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "donatorname")
    private String name;

    @Column(name = "donatoraddress")
    private String address;

    @Column(name = "donatorphone")
    private String phone;

    @Column(name = "donatoremail")
    private String email;

    //@Formula("if(donatorinstitution in ('', '-'), donatorname, concat(donatorname,', ',donatorinstitution)) AS title")
    @Formula("CASE WHEN donatorinstitution IN ('', '-') THEN donatorname ELSE concat(donatorname,', ',donatorinstitution) END")
    private String title;

}
