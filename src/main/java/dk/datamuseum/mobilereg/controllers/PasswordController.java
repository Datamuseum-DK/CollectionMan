package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.User;
import dk.datamuseum.mobilereg.repositories.UserRepository;

/**
 * Controller user self service.
 * Currently only change password. Could be expanded to change email, name, etc.
 */
@Slf4j
@Controller
public class PasswordController {

    private static int MIN_LENGTH = 8;

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
    public String changepassword(Model model) {
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
    public String updatepassword(String orgpasswd, String passwd1, String passwd2,
            Model model, Authentication authentication) throws NotFoundException {
        Object principal = authentication.getPrincipal();
        String username = principal.toString();
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        }
        // UserDetails loadedUser = userDetailsService().loadUserByUsername(username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException("Invalid user Name:" + username);
        }
        // Check correct password
        if (!passwordEncoder.matches(orgpasswd, user.getPassword())) {
            model.addAttribute("message", "Forkert kodeord");
            return "changepassword";
        }
        if (!passwd1.equals(passwd2)) {
            model.addAttribute("message", "Der er forskel på kodeordene");
            return "changepassword";
        }
        if (passwd1.length() < MIN_LENGTH) {
            model.addAttribute("message", String.format("Kodeordet skal være mindst %d tegn", MIN_LENGTH));
            return "changepassword";
        }
        user.setPassword(passwordEncoder.encode(passwd1));
        userRepository.save(user);
        log.info("Changed password for user Id {}", user.getId());

        return "redirect:userprofile";
    }
}
