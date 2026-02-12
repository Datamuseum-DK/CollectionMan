package dk.datamuseum.mobilereg.controllers;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Item;

import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for locations.
 * These are items where the placement id is null.
 */
@Slf4j
@Controller
public class LocationsController {
    
    private final ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public LocationsController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Get the list of locations.
     *
     * @param model - map to place attributes in.
     * @return name of Thymeleaf template.
     */
    @RequestMapping({"/locations"})
    public String locationList(Model model) {
        List<Item> items = itemRepository.findByPlacementidNull();
        log.debug("Items.size: {}", items.size());
        model.addAttribute("items", items);
        return "locations";
    }

}
