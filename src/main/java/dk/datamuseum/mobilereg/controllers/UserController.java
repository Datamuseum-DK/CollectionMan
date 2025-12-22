package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.repositories.PermissionRepository;
import dk.datamuseum.mobilereg.repositories.RoleRepository;
import dk.datamuseum.mobilereg.repositories.UserRepository;

/**
 * Controller for users.
 */
@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor.
     */
    public UserController(
                RoleRepository roleRepository,
                PermissionRepository permissionRepository,
                UserRepository userRepository,
                PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * View all possible permissions.
     */
    @ModelAttribute("allPermissions")
    public Iterable<Permission> allPermissions() {
        return permissionRepository.findByOrderByContentTypeIdAscNameAsc();
    }

    /**
     * View all possible roles.
     */
    @ModelAttribute("allRoles")
    public Iterable<Role> allRoles() {
        return roleRepository.findByOrderByName();
    }

    /**
     * View all users.
     *
     * @param model - addition information from the form.
     */
    @RequestMapping({"", "/", "/view"})
    public String showUserList(Model model) {
        model.addAttribute("users", userRepository.findByOrderByUsername());
        return "users";
    }

    /**
     * Show add form for a user.
     *
     * @param model - addition information from the form.
     */
    @GetMapping("/addform")
    @PreAuthorize("hasAuthority('ADD_USER')")
    public String addForm(Model model) {
        User user = new User();
        user.setActive(true);
        model.addAttribute("user", user);
        return "users-addform";
    }
    
    /**
     * Add user to database.
     *
     * @param user - User entitity after validation.
     * @param result - errors etc. from validation.
     * @param model - addition information from the form.
     */
    @PostMapping("/adduser")
    @PreAuthorize("hasAuthority('ADD_USER')")
    public String addUser(@Valid User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "users-add";
        }
        user.setDateJoined(LocalDateTime.now());
        String passwd1 = user.getPassword();
        if (passwd1 != null && !passwd1.equals("")) {
            user.setPassword(passwordEncoder.encode(passwd1));
        }
        userRepository.save(user);
        return "redirect:/users";
    }
    
    /**
     * View one user.
     *
     * @param id - user id.
     */
    @GetMapping("/view/{id}")
    @PreAuthorize("hasAuthority('VIEW_USER')")
    public String showFactsheet(@PathVariable("id") int id, Model model) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Unknown user Id:" + id));
        model.addAttribute("user", user);
        
        return "users-view";
    }

    /**
     * Show edit form for a user.
     *
     * @param id - user id.
     * @param model - addition information from the form.
     */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('CHANGE_USER')")
    public String showUpdateForm(@PathVariable("id") int id, Model model) throws NotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Invalid user Id:" + id));
        user.setPassword("");  // Don't show hashed password on form.
        model.addAttribute("user", user);
        
        return "users-edit";
    }

    /**
     * Update user by an account manager. Not for users updating own password.
     *
     * @param id - User id
     * @param user - User entitity
     * @param result - errors etc. from validation.
     * @param model - addition information from the form.
     * @return name of Thymeleaf template or redirection to list.
     */
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CHANGE_USER')")
    public String updateUser(@PathVariable("id") int id, @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            user.setId(id);
            model.addAttribute("user", user);
            return "users-edit";
        }

        User orgUser = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        String passwd1 = user.getPassword();
        if (passwd1 == null || passwd1.equals("")) {
            user.setPassword(orgUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(passwd1));
        }

        userRepository.save(user);
        log.info("Updated user Id {}", id);
        return "redirect:/users";
    }
    
    /**
     * Delete user.
     *
     * @param id - User id
     * @param model - addition information from the form.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public String deleteUser(@PathVariable("id") int id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        userRepository.delete(user);
        log.info("Deleted user Id {}", id);
        return "redirect:/users";
    }
}
