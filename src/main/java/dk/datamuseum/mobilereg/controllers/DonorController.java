package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
//import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Controller
@RequestMapping("/donors")
public class DonorController {


    private final DonorRepository donorRepository;

    private final ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public DonorController(
            DonorRepository donorRepository,
            ItemRepository itemRepository) {
        this.donorRepository = donorRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * List donors, either all or filtered.
     *
     * @param q - query string.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_DONATORS')")
    @RequestMapping({"", "/", "/view"})
    public String showDonorList(String q, Model model) {
        if (q == null) {
            q = "";
        }
        model.addAttribute("donors", donorRepository.findByQuerytext(q));
        model.addAttribute("q", q);
        return "donors";
    }

    /**
     * Show form for creation of a new donor.
     *
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('ADD_DONATORS')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Donor donor = new Donor();
        model.addAttribute("donor", donor);
        return "donors-add";
    }

    @PreAuthorize("hasAuthority('ADD_DONATORS')")
    @PostMapping("/adddonor")
    public String addDonor(@Valid Donor donor, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "donors-add";
        }
        donorRepository.save(donor);
        return "redirect:/donors";
    }

    /**
     * Show factsheet of a donor.
     *
     * @param id - donor id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_DONATORS')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        model.addAttribute("donor", donor);
        model.addAttribute("items", itemRepository.findByDonoridOrderByHeadline(id));

        return "donors-view";
    }

    /**
     * Show edit form for donor.
     *
     * @param id - donor id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('CHANGE_DONATORS')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        model.addAttribute("donor", donor);

        return "donors-edit";
    }

    /**
     * General update of donor.
     *
     * @param id - donor id.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PreAuthorize("hasAuthority('CHANGE_DONATORS')")
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
     *
     * @param id - donor id.
     * @param model - Additional attributes used by the web form.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_DONATORS')")
    public String deleteDonor(@PathVariable("id") int id, Model model) {
        Donor donor = donorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid donor Id:" + id));
        donorRepository.delete(donor);
        log.info("Deleted donor Id {}", id);
        return "redirect:/donors";
    }
}
