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
import jakarta.persistence.Table;
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
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * The items entity.
 */
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

    public static List<Acquired> ACQ_OPTIONS = Arrays.asList(Acquired.UKENDT, Acquired.GAVE, Acquired.KØB, Acquired.DEPONERING );

    // itemid              = models.AutoField(verbose_name="genstand nr.", primary_key=True, editable=False)
    @Id
    @Column(name = "itemid")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    // itemtemporary       = models.IntegerField(blank=True, null=False, default=0)
    @ColumnDefault("0")
    @Column(nullable = false)
    private Integer itemtemporary;

    /**
     * File = Sag.
     */
    // fileid              = models.ForeignKey(Files, db_column='fileid', null=False, verbose_name="sag nr.", on_delete=models.CASCADE)
    @Column(name = "fileid")
    @NotNull(message = "Der skal angives en sag")
    private Integer fileid;

    // olditemid           = models.CharField(verbose_name="gammelt nr.", max_length=255, blank=True)
    @Column(length=255)
    private String olditemid;

    // itemdeleted         = models.IntegerField(verbose_name="status", choices=status_as, null=True, default=0)
    @ColumnDefault("0")
    @Column(nullable = false)
    @NotNull(message = "Der skal angives en status")
    private Integer itemdeleted;

    // itemheadline        = models.CharField('betegnelse', max_length=255, blank=False, db_index=True)
    @Column(name = "itemheadline", length=255)
    @NotBlank(message = "Headline is mandatory")
    private String headline;
    
    // itemdescription     = models.TextField('beskrivelse', default='', blank=True)
    @Column(name = "itemdescription", length=65535)
    @ColumnDefault("")
    private String description;

    // itemsize            = models.CharField('størrelse', max_length=255, blank=True)
    @Column
    private String itemsize;
    // itemweight          = models.CharField('Vægt', max_length=255, blank=True)
    @Column
    private String itemweight;

    // itemmodeltype       = models.CharField(verbose_name="model/type", max_length=255, blank=True)
    @Column
    private String itemmodeltype;
    // itemserialno        = models.CharField(verbose_name="serienummer", max_length=255, blank=True)
    @Column
    private String itemserialno;
    // itemdatingfrom      = models.DateField(verbose_name="Datering fra", null=True, blank=False)
    @Column
    //@NotNull(message = "Der skal angives en dato")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdatingfrom;

    // itemdatingto        = models.DateField(verbose_name="Datering til", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdatingto;

    /**
     * Producer is a ManyToOne relation.
     */
    // producerid          = models.ForeignKey(Producers, db_column="producerid",
    // verbose_name='producent', help_text="producent/fabrikant/forfatter",
    // null=True, blank=True, on_delete=models.CASCADE)
    @Column(name = "producerid")
    private Integer producerid;

    // itemacquiretype     = models.IntegerField(verbose_name="modtaget som", choices=acquiretype_as, null=True, blank=False, default=0)
    private Integer itemacquiretype;
    
    // itemdepositeduntil  = models.DateField(verbose_name="deponeret/udlånt indtil", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemdepositeduntil;
    /**
     * Donor is a ManyToOne relation.
     */
    // donatorid           = models.ForeignKey(Donators, db_column="donatorid", verbose_name='donator', null=True, blank=True, on_delete=models.CASCADE)
    @Column(name = "donatorid")
    private Integer donorid;

    // itemoutdated        = models.IntegerField("udgået",null=True, blank=True)
    @Column
    private Integer itemoutdated;

    // itemborroweduntil   = models.DateField(verbose_name="udlånt indtil",null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemborroweduntil;

    // itemreceived        = models.DateField(verbose_name="modtagelsesdato", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemreceived;
    // itemreceivedby      = models.CharField("modtaget af", max_length=12, blank=True)
    @Column
    @ColumnDefault("")
    private String itemreceivedby;

    // itemregistered      = models.DateField(verbose_name="registreringsdato", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemregistered;

    // itemregisteredby    = models.CharField(max_length=12, null=True, blank=True, verbose_name="oprettet af")
    @Column
    @ColumnDefault("")
    private String itemregisteredby;

    // lastmodified        = models.DateTimeField(verbose_name='rettet', null=True, auto_now=True)
    @LastModifiedDate
    @Column
    private LocalDateTime lastmodified;

    // itemthanksletter    = models.DateField(verbose_name="takkebrev afsendt", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemthanksletter;

    /**
     * Id of containing item. Can be null.
     */
    // placementid         = models.IntegerField(verbose_name="placering", null=True, blank=True)
    @Column
    private Integer placementid;

    // itemlocation        = models.TextField("pladsering", db_column='itemlocation', blank=True, null=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemlocation;
    // itemusedby          = models.TextField("brugt af", blank=True)

    @Column(length=65535)
    @ColumnDefault("")
    private String itemusedby;

    // itemusedfor         = models.TextField("til hvad", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemusedfor;

    // itemusedwhere       = models.TextField("hvor", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemusedwhere;

    // itemusedwhereid     = models.ForeignKey(Sted, null=True, blank=True, db_column='itemusedwhereid', verbose_name="brugt i geografisk område", on_delete=models.CASCADE)
    //@ManyToOne
    //@JoinColumn(name="itemusedwhereid", nullable=true)
    //private Sted usedwhere;
    private Integer itemusedwhereid;

    // itemusedfrom        = models.DateField(verbose_name="brugt fra", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemusedfrom;
    // itemusedto          = models.DateField(verbose_name="brugt indtil", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemusedto;
    // itemusedendfrom     = models.DateField(verbose_name="udgået af brug/nedtaget", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemusedendfrom;
    // itemusedendto       = models.DateField(verbose_name="udgået af brug/nedtaget", null=True, blank=True)
    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate itemusedendto;

    // itemextrainfo       = models.TextField("særlige oplysninger", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemextrainfo;

    // itemrestoration     = models.TextField("restaurering", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemrestoration;
    // itemreferences      = models.TextField("litteraturhenvisninger", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemreferences;

    // itemremarks         = models.TextField("bemærkninger", help_text="internt brug", blank=True)
    @Column(length=65535)
    @ColumnDefault("")
    private String itemremarks;

    // iteminternal        = models.IntegerField("intern", help_text="forenigens aktiv", default=0, null=False, blank=True)
    @Column
    @ColumnDefault("0")
    @NotNull(message = "Der skal angives en status")
    private Integer iteminternal;

    // itemsubject         = models.ManyToManyField(Subjects, related_name="itemlist", verbose_name="emnegrupper" ,null=True, blank=True)
    @ManyToMany
    @JoinTable(
        name="items_itemsubject",
        joinColumns=@JoinColumn(name="items_id", referencedColumnName = "itemid"),
        inverseJoinColumns=@JoinColumn(name="subjects_id", referencedColumnName = "subjectid")
        )
    List<Subject> subjects;

    // itempicture         = models.ManyToManyField(Pictures, verbose_name="billeder" , null=True, blank=True)
    @ManyToMany
    @JoinTable(
        name="items_itempicture",
        joinColumns=@JoinColumn(name="items_id", referencedColumnName = "itemid"),
        inverseJoinColumns=@JoinColumn(name="pictures_id", referencedColumnName = "pictureid")
        )
    List<Picture> pictures;

    @ManyToOne
    @JoinColumn(name="itemclassid", nullable=false)
    private ItemClass itemClass;

    @Column
    private Integer qrcode;

    /**
     * Constructor.
     * TODO: Don't use join for itemclass.
     */
    public Item() {
        setIteminternal(0);
        setItemdeleted(0); // Godkendt
        setItemtemporary(0);
        //setItemClass(1);
        setOlditemid("");
        setItemsize("");
        setItemweight("");
        setItemmodeltype("");
        setItemserialno("");
        setItemacquiretype(0); // Ukendt
    }

}
