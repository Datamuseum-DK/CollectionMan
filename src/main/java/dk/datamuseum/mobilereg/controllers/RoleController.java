package dk.datamuseum.mobilereg.controllers;

// import jakarta.validation.Valid;
// import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.entities.Role;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;
import dk.datamuseum.mobilereg.repositories.RoleRepository;

/**
 * Controller for roles.
 */
@Controller
@RequestMapping("/roles")
public class RoleController {
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private Log logger = LogFactory.getLog(RoleController.class);

    @ModelAttribute("allPermissions")
    public Iterable<Permission> allPermissions() {
        return permissionRepository.findByOrderByContentTypeIdAscNameAsc();
    }

    //@PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping({"", "/", "/view"})
    public String showRoleList(Model model) {
        model.addAttribute("roles", roleRepository.findByOrderByName());
        return "roles";
    }

    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int roleid, Model model) {
        Role role = roleRepository.findById(roleid).orElseThrow(()
                -> new IllegalArgumentException("Invalid role Id:" + roleid));
        model.addAttribute("role", role);
        return "roles-view";
    }

    /**
     * Show edit form for role.
     */
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid role Id:" + id));
        model.addAttribute("role", role);

        return "roles-edit";
    }

}
