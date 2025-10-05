package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.repositories.UserRepository;

/**
 * Controller user self service.
 * Currently only change password. Could be expanded to change email, name, etc.
 */
@Slf4j
@Controller
public class PasswordController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor.
     */
    public PasswordController(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Form for password change.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/changepassword")
    public String about(Model model) {
        return "changepassword";
    }

    /**
     * Do the password change.
     * TODO
     * @param model holder for model attributes
     * @return view name
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/updatepassword")
    public String about(String orgpasswd, String passwd1, String passwd2, Model model) {
        if (!passwd1.equals(passwd2)) {
            return "changepassword";
        }
        return "redirect:userprofile";
    }
}
