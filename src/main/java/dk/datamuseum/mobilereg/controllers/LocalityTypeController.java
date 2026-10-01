package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.List;
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
import dk.datamuseum.mobilereg.entities.LocalityType;
import dk.datamuseum.mobilereg.repositories.LocalityTypeRepository;
import dk.datamuseum.mobilereg.entities.Locality;
import dk.datamuseum.mobilereg.repositories.LocalityRepository;

/**
 * Controller for localitytypes.
 */
@Slf4j
@Controller
@RequestMapping("/localitytypes")
public class LocalityTypeController {
    

    private final LocalityTypeRepository localityTypeRepository;

    private final LocalityRepository localityRepository;

    /**
     * Constructor.
     */
    public LocalityTypeController(
                LocalityTypeRepository localityTypeRepository,
                LocalityRepository localityRepository) {
        this.localityTypeRepository = localityTypeRepository;
        this.localityRepository = localityRepository;
    }

    @PreAuthorize("hasAuthority('VIEW_LOCALITIES')")
    @RequestMapping({"", "/", "/view"})
    public String showLocalityTypeList(Model model) {
        model.addAttribute("localitytypes", localityTypeRepository.findByOrderByTitle());
        return "localitytypes";
    }

    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        LocalityType sted = new LocalityType();
        model.addAttribute("localitytype", sted);
        return "localitytype-add";
    }
    
    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @PostMapping("/add")
    public String addLocalityType(@Valid LocalityType sted, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "localitytype-add";
        }
        localityTypeRepository.save(sted);
        return "redirect:/localitytypes";
    }

    /**
     * Add locality form under a locality type.
     */
    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @GetMapping("/{typeid}/addform")
    public String addFormLocality(@PathVariable("typeid") int typeid, Model model) {
        LocalityType localityType = localityTypeRepository.findById(typeid)
            .orElseThrow(() -> new NotFoundException("Invalid localityType Id:" + typeid));

        Locality locality = new Locality();
        locality.setLocalitytype(localityType);
        model.addAttribute("locality", locality);
        return "locality-add";
    }

    /**
     * Add locality under a locality type.
     */
    @PreAuthorize("hasAuthority('ADD_LOCALITIES')")
    @PostMapping("/{typeid}/add")
    public String addLocality(@PathVariable("typeid") int typeid,
            @Valid Locality locality, BindingResult result, Model model) {
        LocalityType localityType = localityTypeRepository.findById(typeid)
            .orElseThrow(() -> new NotFoundException("Invalid localityType Id:" + typeid));
        locality.setLocalitytype(localityType);

        if (result.hasErrors()) {
            return "locality-add";
        }
        localityRepository.save(locality);
        return String.format("redirect:/localitytypes/view/%d", typeid);
    }

    /**
     * Show factsheet.
     *
     * @param id - localityType id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_LOCALITIES')")
    @GetMapping("/view/{id}")
    @Transactional
    public String showFactsheet(@PathVariable("id") int id, Model model) throws NotFoundException {
        LocalityType localityType = localityTypeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid localityType Id:" + id));
        model.addAttribute("localitytype", localityType);
        //model.addAttribute("localities", localityRepository.findByTypeidOrderByName(id));
        
        return "localitytype-view";
    }

    /**
     * Show update form.
     *
     * @param id - localityType id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('CHANGE_LOCALITIES')")
    @GetMapping("/edit/{id}")
    @Transactional
    public String showUpdateForm(@PathVariable("id") int id, Model model) throws NotFoundException {
        LocalityType localityType = localityTypeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Invalid localityType Id:" + id));
        model.addAttribute("localitytype", localityType);
        
        return "localitytype-edit";
    }

    /**
     * General update of localityType.
     *
     * @param id - localityType id.
     * @param localityType - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to list.
     */
    @PreAuthorize("hasAuthority('CHANGE_LOCALITIES')")
    @PostMapping("/update/{id}")
    public String updateLocalityType(@PathVariable("id") int id,
            @Valid LocalityType localityType, BindingResult result, Model model) {
        if (result.hasErrors()) {
            localityType.setId(id);
            model.addAttribute("localitytype", localityType);
            return "localitytype-edit";
        }
        // Remember the localities.
        List<Locality> localities = localityRepository.findByTypeidOrderByName(id);
        localityType.setLocalities(localities);
        localityTypeRepository.save(localityType);

        return "redirect:/localitytypes";
    }
    
    /**
     * Delete localityType.
     */
    @PreAuthorize("hasAuthority('DELETE_LOCALITIES')")
    @GetMapping("/delete/{id}")
    public String deleteLocalityType(@PathVariable("id") int id, Model model) {
        LocalityType localityType = localityTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid localityType Id:" + id));
        localityTypeRepository.delete(localityType);
        log.info("Deleted localityType Id {}", id);
        return "redirect:/localitytypes";
    }

    public Comparator<Locality> localityComparator = new Comparator<Locality>() {
        public int compare(Locality m1, Locality m2) {
            return m1.getName().compareToIgnoreCase(m2.getName());
        }
    };

}
