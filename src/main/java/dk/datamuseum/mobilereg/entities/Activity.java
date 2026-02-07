package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
//import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * The activities entity is a sequence of things that happen to an item.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int itemid;

    @CreatedDate
    @Column
    private LocalDateTime created;

    @CreatedBy
    @Column(length = 32)
    private String creator;

    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    @ManyToOne
    @JoinColumn(name="typeid", nullable=false)
    private ActivityType activityType;

    @NotBlank(message = "Note is mandatory")
    @Column(name = "note")
    private String note;


}
