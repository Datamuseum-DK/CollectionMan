package dk.datamuseum.mobilereg.controllers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@Controller
public class LocationsController {
    
    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(LocationsController.class);

    @RequestMapping({"/locations"})
    public String locationList(Model model) {
        List<Item> items = itemRepository.findByPlacementidNull();
        logger.debug(String.format("Items.size: %d", items.size()));
        model.addAttribute("items", items);
        return "locations";
    }

}
