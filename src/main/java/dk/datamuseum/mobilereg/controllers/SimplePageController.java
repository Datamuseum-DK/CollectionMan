package dk.datamuseum.mobilereg.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for simple pages that don't call a database.
 */
@Controller
public class SimplePageController {

    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(SimplePageController.class);
/*
    @ModelAttribute("user")
    public String principalAttributes(OAuth2AuthenticationToken authentication) {
        if (authentication == null)
            return "Ikke logget ind";
        else
            return authentication.getPrincipal().getName();
    }
*/
    /**
     * Frontpage.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/")
    public String frontpage(Model model) {
        return "index";
    }

    /**
     * About.
     */
    @RequestMapping(value = "/about")
    public String about(Model model) {
        return "about";
    }

    /**
     * Administration.
     * Access is not verified as each individual menu item is checked for access.
     */
    @RequestMapping(value = "/administration")
    public String administration(Model model) {
        return "administration";
    }

    /**
     * Brugerprofil.
     */
    @RequestMapping(value = "/userprofile")
    public String userprofile(Authentication authentication) {
        //Principal principal = authentication.getPrincipal();
        //logger.info(principal.getAttributes());
        logger.info(authentication.getAuthorities());
        return "userprofile";
    }

    /**
     * Search.
     */
    /*
    @RequestMapping(value = "/search")
    public String search(Model model) {
        return "search";
    }
    */

    /**
     * Error.
     */
    /*
    @RequestMapping(value = "/error")
    public String error(Model model) {
        return "error";
    }
    */

    /**
     * Scan QR.
     */
    @RequestMapping(value = "/scanqr")
    public String scanqr(Model model) {
        return "scanqr";
    }

    /**
     * Show change log.
     * Starting with Items, but will expand
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/changelog")
    public String itemChangelog(Model model) {
        model.addAttribute("changeditems", itemRepository.findFirst50ByOrderByLastmodifiedDesc());
        return "changelog";
    }

    /**
     * Redirects to welcome page after login.
     *
     * @return view name
     */
    /*
    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }
    */

    /**
     * Shows page which allows to perform SingleSignOut.
     *
     * @return view name
     */
    /*
    @RequestMapping(value = "/logout")
    public String logout() {
        return "logout_all_apps";
    }
    */

}
