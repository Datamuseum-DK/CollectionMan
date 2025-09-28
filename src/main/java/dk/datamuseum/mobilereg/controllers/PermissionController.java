package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;

/**
 * Controller for permissions.
 */
@Slf4j
@Controller
@RequestMapping("/permissions")
public class PermissionController {
    
    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Show list of built-in permissions.
     *
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_PERMISSION')")
    @RequestMapping({"", "/", "/view"})
    public String showPermissionList(Model model) {
        model.addAttribute("permissions", permissionRepository.findByOrderByContentTypeIdAscNameAsc());
        return "permissions";
    }
}
