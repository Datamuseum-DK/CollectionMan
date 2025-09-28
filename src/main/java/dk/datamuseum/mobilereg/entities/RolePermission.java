package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import java.util.ArrayList;
//import java.util.List;
import lombok.Data;

/**
 * Many-to-many relation between roles and permission.
 * A permission can be used in many roles.
 * A role can have many permissions.
 */
@Entity
@Table(name = "auth_group_permissions")
@Data
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "group_id")
    @NotNull
    private Integer groupId;

    @Column(name = "permission_id")
    @NotNull
    private Integer permissionId;
}
