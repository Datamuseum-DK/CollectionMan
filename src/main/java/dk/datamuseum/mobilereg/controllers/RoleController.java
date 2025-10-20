package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.entities.Role;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;
import dk.datamuseum.mobilereg.repositories.RoleRepository;

/**
 * Controller for roles.
 */
@Slf4j
@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    /**
     * Constructor.
     */
    public RoleController(
                RoleRepository roleRepository,
                PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @ModelAttribute("allPermissions")
    public Iterable<Permission> allPermissions() {
        return permissionRepository.findByOrderByContentTypeIdAscNameAsc();
    }

    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    @RequestMapping({"", "/", "/view"})
    public String showRoleList(Model model) {
        model.addAttribute("roles", roleRepository.findByOrderByName());
        return "roles";
    }

    @PreAuthorize("hasAuthority('VIEW_GROUP')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int roleid, Model model) {
        Role role = roleRepository.findById(roleid).orElseThrow(()
                -> new IllegalArgumentException("Invalid role Id:" + roleid));
        model.addAttribute("role", role);
        return "roles-view";
    }

    @PreAuthorize("hasAuthority('ADD_GROUP')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Role role = new Role();
        model.addAttribute("role", role);
        return "roles-add";
    }

    @PreAuthorize("hasAuthority('ADD_GROUP')")
    @PostMapping("/addrole")
    public String addRole(@Valid Role role, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "roles-add";
        }
        roleRepository.save(role);
        return "redirect:/roles";
    }

    /**
     * Show edit form for role.
     *
     * @param model - Additional attributes used by the web form.
     * @return name of form
     */
    @PreAuthorize("hasAuthority('CHANGE_GROUP')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid role Id:" + id));
        model.addAttribute("role", role);

        return "roles-edit";
    }

    /**
     * Update role.
     *
     * @param id - id of role.
     * @param role - The role record containing the entered information.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PreAuthorize("hasAuthority('CHANGE_GROUP')")
    @PostMapping("/update/{id}")
    public String updateRole(@PathVariable("id") int id, @Valid Role role,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            role.setId(id);
            model.addAttribute("role", role);
            return "roles-edit";
        }

        roleRepository.save(role);

        return "redirect:/roles";
    }

    /**
     * Delete role.
     */
    @PreAuthorize("hasAuthority('DELETE_GROUP')")
    @GetMapping("/delete/{id}")
    public String deleteRole(@PathVariable("id") int id, Model model) {
        Role role = roleRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid role Id:" + id));
        roleRepository.delete(role);
        log.info("Deleted role Id {}", id);
        return "redirect:/roles";
    }
}
