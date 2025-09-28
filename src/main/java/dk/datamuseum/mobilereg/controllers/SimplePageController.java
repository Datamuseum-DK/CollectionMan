package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Controller
public class SimplePageController {

    private ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public SimplePageController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

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
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/about")
    public String about(Model model) {
        return "about";
    }

    /**
     * Administration.
     * Access is not verified as each individual menu item is checked for access.
     *
     * @param model holder for model attributes
     * @return view name
     */
    @RequestMapping(value = "/administration")
    public String administration(Model model) {
        return "administration";
    }

    /**
     * Information page for the authenticated user.
     *
     * @return view name
     */
    @RequestMapping(value = "/userprofile")
    public String userprofile(Authentication authentication) {
        //Principal principal = authentication.getPrincipal();
        //log.info(principal.getAttributes());
        log.info("Authorities: {}", authentication.getAuthorities());
        return "userprofile";
    }

    /**
     * Search.
     *
     * @param model holder for model attributes
     * @return view name
     */
    /*
    @RequestMapping(value = "/search")
    public String search(Model model) {
        return "search";
    }
    */

    /**
     * Error.
     *
     * @param model holder for model attributes
     * @return view name
     */
    /*
    @RequestMapping(value = "/error")
    public String error(Model model) {
        return "error";
    }
    */

    /**
     * Scan QR.
     *
     * @param model holder for model attributes
     * @return view name
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
