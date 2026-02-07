package dk.datamuseum.mobilereg.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Many-to-many relation between users and roles.
 * A role can be used in many users.
 * A user can have many roles.
 */
@Entity
@Table(name = "auth_user_groups")
@Data
public class UserRole {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    @NotNull
    private Integer userId;

    @Column(name="group_id")
    @NotNull
    private Integer roleId;
}
