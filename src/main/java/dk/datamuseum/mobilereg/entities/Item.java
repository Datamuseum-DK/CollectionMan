package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import dk.datamuseum.mobilereg.UniqueQR;

/**
 * The items entity.
 */
// @UniqueQR(itemid = "id", qrCode="qrcode", message = "QR Duplet")
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "items")
@Data
public class Item {

    /**
     * Enumeration of ways an item was acquired.
     */
    public enum Acquired {
        UKENDT, GAVE, ARV, KØB, DEPONERING;

    }

    public static List<Acquired> ACQ_OPTIONS = Arrays.asList(Acquired.UKENDT,
            Acquired.GAVE, Acquired.KØB, Acquired.DEPONERING );

    @Id
    @Column(name = "itemid")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    /*
     * File = Sag.
     */
    @Column(name = "fileid")
    @NotNull(message = "Der skal angives en sag")
    private Integer fileid;

    //@ColumnDefault("0")
    //@Column(nullable = false)
    //@NotNull(message = "Der skal angives en status")
    @ManyToOne
    @JoinColumn(name="itemstatus", nullable=false)
    private ItemStatus itemStatus;

    @Column(name = "itemheadline", length=255)
    //@NotBlank(message = "Headline is mandatory")
    private String headline;
    
    @Column(name = "itemdescription", length=65535)
    @ColumnDefault("")
    private String description;

    @Column
    private String itemsize;

    @Column
    private String itemweight;


    @Column
    private String itemmodeltype;

    @Column
    private String itemserialno;

    @Column
    //@NotNull(message = "Der skal angives en dato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdatingfrom;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdatingto;

    /*
     * Producer is a ManyToOne relation.
     */
    @Column(name = "producerid")
    private Integer producerid;

    private Integer itemacquiretype;
    
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdepositeduntil;
    /*
     * Donor is a ManyToOne relation.
     */
    @Column(name = "donatorid")
    private Integer donorid;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemborroweduntil;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemreceived;

    @Column
    @ColumnDefault("")
    private String itemreceivedby;

    @CreatedDate
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemregistered;

    @CreatedBy
    @Column
    @ColumnDefault("")
    private String itemregisteredby;

    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    @LastModifiedBy
    @Column
    private String lastmodifiedby;

    /*
     * Id of containing item. Can be null.
     */
    @Column
    private Integer placementid;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemlocation;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemusedby;

    // itemusedwhereid     = models.ForeignKey(Sted, null=True, blank=True,
    //  db_column='itemusedwhereid', verbose_name="brugt i geografisk område",
    //  on_delete=models.CASCADE)
    //@ManyToOne
    //@JoinColumn(name="itemusedwhereid", nullable=true)
    //private Sted usedwhere;
    private Integer itemusedwhereid;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemextrainfo;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemrestoration;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemreferences;

    @Column(length=65535)
    @ColumnDefault("")
    private String itemremarks;

    @ManyToMany
    @JoinTable(
        name="items_itemsubject",
        joinColumns=@JoinColumn(name="items_id", referencedColumnName = "itemid"),
        inverseJoinColumns=@JoinColumn(name="subjects_id", referencedColumnName = "subjectid")
        )
    List<Subject> subjects;

    @OneToMany(mappedBy = "itemid")
    List<Picture> pictures;

    @ManyToOne
    @JoinColumn(name="itemclassid", nullable=false)
    private ItemClass itemClass;

    /**
     * The QR codes we used are integers.
     */
    //@QRCode
    @Min(50000000)
    @Max(59999999)
    @Column
    private Integer qrcode;

    @OneToMany(mappedBy = "itemidto")
    private List<ReverseLink> revlinks;

    /**
     * Constructor.
     * TODO: Don't use join for itemclass.
     */
    public Item() {
        setItemsize("");
        setItemweight("");
        setItemmodeltype("");
        setItemserialno("");
        setItemacquiretype(0); // Ukendt
    }

}
