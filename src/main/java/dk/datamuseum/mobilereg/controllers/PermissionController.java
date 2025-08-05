package dk.datamuseum.mobilereg.controllers;

//import jakarta.validation.Valid;
//import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Permission;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;

/**
 * Controller for permissions.
 */
@Controller
@RequestMapping("/permissions")
public class PermissionController {
    
    @Autowired
    private PermissionRepository permissionRepository;

    private Log logger = LogFactory.getLog(PermissionController.class);

    //@PreAuthorize("hasRole('ROLE_STAFF')")
    @RequestMapping({"", "/", "/view"})
    public String showPermissionList(Model model) {
        model.addAttribute("permissions", permissionRepository.findByOrderByContentTypeIdAscNameAsc());
        return "permissions";
    }
}
