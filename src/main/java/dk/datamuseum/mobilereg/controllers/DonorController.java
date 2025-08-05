package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
//import java.time.LocalDateTime;

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

import dk.datamuseum.mobilereg.entities.Donor;
import dk.datamuseum.mobilereg.repositories.DonorRepository;
//import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for donors.
 */
@Controller
@RequestMapping("/donors")
public class DonorController {
    
    @Autowired
    private DonorRepository donorRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(DonorController.class);

    /**
     * List donors, either all or filtered.
     * @param q - query string.
     */
    @RequestMapping({"", "/", "/view"})
    public String showDonorList(String q, Model model) {
        if (q == null) {
            q = "";
        }
        model.addAttribute("donors", donorRepository.findByQuerytext(q));
        model.addAttribute("q", q);
        return "donors";
    }

    @GetMapping("/addform")
    public String addForm(Model model) {
        Donor donor = new Donor();
        model.addAttribute("donor", donor);
        return "donors-add";
    }
    
    @PostMapping("/adddonor")
    public String addDonor(@Valid Donor donor, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "donors-add";
        }
        donorRepository.save(donor);
        return "redirect:/donors";
    }
    
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        model.addAttribute("donor", donor);
        model.addAttribute("items", itemRepository.findByDonoridOrderByHeadline(id));
        
        return "donors-view";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        model.addAttribute("donor", donor);
        
        return "donors-edit";
    }
    
    @PostMapping("/update/{id}")
    public String updateDonor(@PathVariable("id") int id, @Valid Donor donor, BindingResult result, Model model) {
        model.addAttribute("donor", donor);
        if (result.hasErrors()) {
            donor.setId(id);
            return "donors-edit";
        }
        
        donorRepository.save(donor);

        return String.format("redirect:/donors/view/%d", id);
    }

    /**
     * Delete donor.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public String deleteDonor(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        donorRepository.delete(donor);
        logger.info(String.format("Deleted donor Id %d", id));
        return "redirect:/donors";
    }
}
