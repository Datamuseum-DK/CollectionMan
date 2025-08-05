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

    @RequestMapping({"", "/", "/view"})
    public String showStedList(Model model) {
        model.addAttribute("places", placeRepository.findByOrderByStednavn());
        return "places";
    }

    @GetMapping("/addform")
    public String addForm(Model model) {
        Sted place = new Sted();
        model.addAttribute("place", place);
        return "places-add";
    }
    
    @PostMapping("/addplace")
    public String addSted(@Valid Sted place, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "places-add";
        }
        placeRepository.save(place);
        return "redirect:/places";
    }
    
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        model.addAttribute("place", place);
        model.addAttribute("items", itemRepository.findByItemusedwhereidOrderByHeadline(id));
        
        return "places-view";
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        model.addAttribute("place", place);
        
        return "places-edit";
    }
    
    @PreAuthorize("hasRole('ROLE_STAFF')")
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
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public String deleteSted(@PathVariable("id") int id, Model model) {
        Sted place = placeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid place Id:" + id));
        placeRepository.delete(place);
        logger.info(String.format("Deleted place Id %d", id));
        return "redirect:/places";
    }
}
