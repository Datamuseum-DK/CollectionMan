package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "files")
@Data
public class CaseFile {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "fileid")
    private int id;

    @CreatedBy
    @Column(length=32)
    private String creator;

    @CreatedDate
    @Column
    private LocalDateTime created;

    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    @NotBlank(message = "Title is mandatory")
    @Column(name = "filetitle")
    private String title;
    
    @NotBlank(message = "Description is mandatory")
    @Column(name = "filedescription", length=65535)
    private String description;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datingfrom;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate datingto;

    @Column
    private boolean status;

    @Formula("(SELECT COUNT(*) FROM items WHERE items.fileid = fileid)")
    private Integer totalItems;
}
