package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Sted;
import dk.datamuseum.mobilereg.repositories.StedRepository;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for places.
 */
@Controller
@RequestMapping("/places")
public class PlaceController {
    
    @Autowired
    private StedRepository placeRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(PlaceController.class);

    @PreAuthorize("hasAuthority('VIEW_STED')")
    @RequestMapping({"", "/", "/view"})
    public String showStedList(Model model) {
        model.addAttribute("places", placeRepository.findByOrderByStednavn());
        return "places";
    }

    @PreAuthorize("hasAuthority('ADD_STED')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Sted sted = new Sted();
        model.addAttribute("sted", sted);
        return "places-add";
    }
    
    @PreAuthorize("hasAuthority('ADD_STED')")
    @PostMapping("/addplace")
    public String addSted(@Valid Sted sted, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "places-add";
        }
        placeRepository.save(sted);
        return "redirect:/places";
    }
    
    @PreAuthorize("hasAuthority('VIEW_STED')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        model.addAttribute("place", place);
        model.addAttribute("items", itemRepository.findByItemusedwhereidOrderByHeadline(id));
        
        return "places-view";
    }

    @PreAuthorize("hasAuthority('CHANGE_STED')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        model.addAttribute("place", place);
        
        return "places-edit";
    }

    /**
     * General update of place.
     *
     * @param id - place id.
     * @param place - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to list.
     */
    @PreAuthorize("hasAuthority('CHANGE_STED')")
    @PostMapping("/update/{id}")
    public String updateSted(@PathVariable("id") int id, @Valid Sted place, BindingResult result, Model model) {
        if (result.hasErrors()) {
            place.setId(id);
            model.addAttribute("place", place);
            return "places-edit";
        }
        
        placeRepository.save(place);

        return "redirect:/places";
    }
    
    /**
     * Delete place.
     */
    @PreAuthorize("hasAuthority('DELETE_STED')")
    @GetMapping("/delete/{id}")
    public String deleteSted(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        placeRepository.delete(place);
        logger.info(String.format("Deleted place Id %d", id));
        return "redirect:/places";
    }
}
