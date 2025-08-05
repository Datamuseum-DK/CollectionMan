package dk.datamuseum.mobilereg.controllers;

//import jakarta.validation.Valid;

//import java.io.IOException;
//import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dk.datamuseum.mobilereg.entities.Item;
//import dk.datamuseum.mobilereg.entities.ItemClass;
//import dk.datamuseum.mobilereg.entities.ItemPicture;
//import dk.datamuseum.mobilereg.entities.Picture;
//import dk.datamuseum.mobilereg.entities.Producer;

import dk.datamuseum.mobilereg.repositories.ItemRepository;
//import dk.datamuseum.mobilereg.repositories.ItemClassRepository;

/**
 * Controller for locations.
 * These are items where the placement id is null.
 */
@Controller
public class LocationsController {
    
    @Autowired
    private ItemRepository itemRepository;
    // @Autowired
    // private ItemClassRepository itemClassRepository;

    private Log logger = LogFactory.getLog(LocationsController.class);

    @RequestMapping({"/locations"})
    public String locationList(Model model) {
        List<Item> items = itemRepository.findByPlacementidNull();
        logger.debug(String.format("Items.size: %d", items.size()));
        model.addAttribute("items", items);
        return "locations";
    }

}
