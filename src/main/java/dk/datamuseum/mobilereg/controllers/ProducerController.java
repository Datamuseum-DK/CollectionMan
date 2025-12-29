package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

import dk.datamuseum.mobilereg.entities.Producer;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.ProducerRepository;

/**
 * Controller for producers.
 */
@Slf4j
@Controller
@RequestMapping("/producers")
public class ProducerController {
    
    private final ProducerRepository producerRepository;
    private final ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public ProducerController(
                ProducerRepository producerRepository,
                ItemRepository itemRepository) {
        this.producerRepository = producerRepository;
        this.itemRepository = itemRepository;
    }

    private List<List<Producer>> makeIndex(Iterable<Producer> producers) {
        List<List<Producer>> producerIndex = new ArrayList<List<Producer>>();
        char currChar = '\0';
        List<Producer> currElem = null;

        for (Producer curr : producers) {
            if (curr.getTitle().charAt(0) == currChar) {
                currElem.add(curr);
            } else {
                currChar = curr.getTitle().charAt(0);
                currElem = new ArrayList<Producer>();
                producerIndex.add(currElem);
                currElem.add(curr);
            }
        }
        return producerIndex;
    }
    /**
     * List producers, either all or filtered.
     * @param q - query string.
     */
    @PreAuthorize("hasAuthority('VIEW_PRODUCERS')")
    @RequestMapping({"", "/", "/view"})
    public String producerList(String q, Model model) {
        Iterable<Producer> producers;
        if (q == null) {
            q = "";
        }
        producers = producerRepository.findByQuerytext(q);
        model.addAttribute("prodindex", makeIndex(producers));
        model.addAttribute("producers", producers);
        model.addAttribute("q", q);
        return "producers";
    }

    @PreAuthorize("hasAuthority('VIEW_PRODUCERS')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int producerid,
            Model model) throws NotFoundException {
        Producer producer = producerRepository.findById(producerid).orElseThrow(()
                -> new NotFoundException("Invalid producer Id:" + producerid));
        model.addAttribute("producer", producer);
        model.addAttribute("items", itemRepository.findByProduceridOrderByHeadline(producerid));

        return "producers-view";
    }

    @PreAuthorize("hasAuthority('ADD_PRODUCERS')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Producer producer = new Producer();
        model.addAttribute("producer", producer);
        return "producers-add";
    }

    @PreAuthorize("hasAuthority('ADD_PRODUCERS')")
    @PostMapping("/addproducer")
    public String addProducer(@Valid Producer producer, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "producers-add";
        }
        producerRepository.save(producer);
        return String.format("redirect:/producers/view/%d", producer.getProducerid());
    }

    @PreAuthorize("hasAuthority('CHANGE_PRODUCERS')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) 
                throws NotFoundException {
        Producer producer = producerRepository.findById(id).orElseThrow(() -> new NotFoundException("Invalid producer Id:" + id));
        model.addAttribute("producer", producer);
        
        return "producers-edit";
    }

    /**
     * General update of producer.
     *
     * @param id - producer id.
     * @param producer - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PreAuthorize("hasAuthority('CHANGE_PRODUCERS')")
    @PostMapping("/update/{id}")
    public String updateProducer(@PathVariable("id") int id,
            @Valid Producer producer, BindingResult result, Model model) {
        model.addAttribute("producer", producer);
        if (result.hasErrors()) {
            producer.setProducerid(id);
            return "producers-edit";
        }
        producer.setProducerid(id);
        producerRepository.save(producer);

        return String.format("redirect:/producers/view/%d", id);
    }
    
    /**
     * Delete producer.
     */
    @PreAuthorize("hasAuthority('DELETE_PRODUCERS')")
    @GetMapping("/delete/{id}")
    public String deleteProducer(@PathVariable("id") int id, Model model) {
        Producer producer = producerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid producer Id:" + id));
        producerRepository.delete(producer);
        log.info("Deleted producer Id {}", id);
        return "redirect:/producers";
    }
}
