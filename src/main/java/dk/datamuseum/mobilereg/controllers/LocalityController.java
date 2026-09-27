package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Locality;
import dk.datamuseum.mobilereg.entities.LocalityType;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.LocalityRepository;
import dk.datamuseum.mobilereg.repositories.LocalityTypeRepository;

/**
 * Controller for localities.
 */
@Slf4j
@Controller
@RequestMapping("/localities")
public class LocalityController {
    
    private final ItemRepository itemRepository;

    private final LocalityTypeRepository localityTypeRepository;

    private final LocalityRepository localityRepository;

    /**
     * Constructor.
     */
    public LocalityController(
                ItemRepository itemRepository,
                LocalityTypeRepository localityTypeRepository,
                LocalityRepository localityRepository) {
        this.itemRepository = itemRepository;
        this.localityTypeRepository = localityTypeRepository;
        this.localityRepository = localityRepository;
    }

    @PreAuthorize("hasAuthority('VIEW_LOCALITIES')")
    @RequestMapping({"", "/", "/view"})
    // @Transactional
    public String showLocalityList(Model model) {
        model.addAttribute("localitytypes", localityTypeRepository.findByOrderByTitle());
        // log.info("Localities: {}", localityTypeRepository.findByOrderByTitle());
        return "localities";
    }

    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Locality locality = new Locality();
        model.addAttribute("locality", locality);
        model.addAttribute("localitytypes", localityTypeRepository.findByOrderByTitle());
        return "locality-add";
    }
    
    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @PostMapping("/add")
    public String addLocality(@Valid Locality locality, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "locality-add";
        }
        localityRepository.save(locality);
        return "redirect:/localities";
    }
    
    /**
     * Show factsheet.
     *
     * @param id - locality id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_LOCALITIES')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model)
                throws NotFoundException {
        Locality locality = localityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid locality Id:" + id));
        model.addAttribute("locality", locality);
        model.addAttribute("items", itemRepository.findByItemusedwhereidOrderByHeadline(id));
        
        return "locality-view";
    }

    /**
     * Show update form.
     *
     * @param id - locality id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('CHANGE_LOCALITIES')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model)
                throws NotFoundException {
        Locality locality = localityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid locality Id:" + id));
        model.addAttribute("locality", locality);
        return "locality-edit";
    }

    /**
     * General update of locality.
     *
     * @param id - locality id.
     * @param locality - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to list.
     */
    @PreAuthorize("hasAuthority('CHANGE_LOCALITIES')")
    @PostMapping("/update/{id}")
    public String updateLocality(@PathVariable("id") int id,
            @Valid Locality locality, BindingResult result, Model model) {
        if (result.hasErrors()) {
            locality.setId(id);
            model.addAttribute("locality", locality);
            return "locality-edit";
        }
        Locality currLocality = localityRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid locality Id:" + id));
        // locality.setLocalitytype(currLocality.getLocalitytype());
        currLocality.setName(locality.getName());
        currLocality.setDescription(locality.getDescription());
        localityRepository.save(currLocality);

        return "redirect:/localities";
    }
    
    /**
     * Delete locality.
     */
    @PreAuthorize("hasAuthority('DELETE_LOCALITIES')")
    @GetMapping("/delete/{id}")
    public String deleteLocality(@PathVariable("id") int id, Model model) {
        Locality locality = localityRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid locality Id:" + id));
        localityRepository.delete(locality);
        log.info("Deleted locality Id {}", id);
        return "redirect:/localities";
    }
}
