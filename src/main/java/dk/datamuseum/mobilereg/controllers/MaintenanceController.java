package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import dk.datamuseum.mobilereg.MobileRegProperties;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.ItemClass;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.entities.Subject;

import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.repositories.SubjectRepository;

import dk.datamuseum.mobilereg.service.PictureService;
import static dk.datamuseum.mobilereg.service.RichTextService.richText;

/**
 * Controller for maintenance pages.
 */
@Slf4j
@Controller
//@RequestMapping("/")
public class MaintenanceController {


    private final ItemRepository itemRepository;

    private final PictureRepository pictureRepository;

    private final PictureService pictureService;

    /**
     * Constructor.
     */
    public MaintenanceController(
                ItemRepository itemRepository,
                PictureRepository pictureRepository,
                PictureService pictureService) {
        this.itemRepository = itemRepository;
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
    }

    /**
     * Run a search and return results.
     *
     * @param query - query string.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of result list.
     * @param size - size of page in number of items.
     * @return name of Thymeleaf template or redirection to factsheet of item.
     */
    @RequestMapping({"/nosubjects"})
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String showNoSubjects(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {

        List<Item> items = new ArrayList<Item>();
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Item> pagedItems =  itemRepository.findBySubjectsIsNullOrderByHeadline(paging);

        items = pagedItems.getContent();
        model.addAttribute("items", items);
        model.addAttribute("currentPage", pagedItems.getNumber() + 1);
        model.addAttribute("totalItems", pagedItems.getTotalElements());
        model.addAttribute("totalPages", pagedItems.getTotalPages());
        model.addAttribute("pageSize", size);
        return "nosubjects";
    }

}
