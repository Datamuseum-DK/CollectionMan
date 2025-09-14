package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The user entity.
 */
@Entity
@Table(name = "auth_user")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length=128)
    private String password;

    @Column(name="last_login")
    private LocalDateTime lastLogin;

    //@NotNull(message = "Angiv om brugeren er superuser")
    @Column(name="is_superuser")
    private boolean superuser;   // Should be TINYINT

    @NotBlank
    @Column(length=150)
    private String username;

    @NotNull
    @Column(name="first_name", length=150)
    private String firstName;

    @NotNull
    @Column(name="last_name", length=150)
    private String lastName;

    @NotNull
    @Column(name="email", length=254)
    private String email;

    //@NotNull(message = "Angiv om brugeren er TA")
    @Column(name="is_staff")
    private boolean staff;   // Should be TINYINT

    //@NotNull(message = "Angiv om brugeren er aktiv")
    @Column(name="is_active")
    private boolean active;   // Should be TINYINT

    @CreatedDate
    @Column(name="date_joined")
    private LocalDateTime dateJoined;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_groups",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
            )
    private List<Role> roles; // = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_user_user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
            )
    private List<Permission> permissions;

    public String getFullName() {
       return firstName + " " + lastName;
    }
}
