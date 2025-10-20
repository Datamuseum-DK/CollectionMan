package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;
//import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.datamuseum.mobilereg.entities.CaseFile;
import dk.datamuseum.mobilereg.repositories.FileRepository;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for case files.
 */
@Slf4j
@Controller
@RequestMapping("/files")
public class FileController {
    
    private final FileRepository fileRepository;

    private final ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public FileController(
                FileRepository fileRepository,
                ItemRepository itemRepository) {
        this.fileRepository = fileRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * View list of case files.
     *
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('VIEW_FILES')")
    @RequestMapping({"", "/", "/view"})
    public String showFileList(Model model) {
        model.addAttribute("files", fileRepository.findByOrderByTitle());
        return "files";
    }

    /**
     * Show form to add a case file record.
     *
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('ADD_FILES')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        CaseFile caseFile = new CaseFile();
        model.addAttribute("caseFile", caseFile);
        return "files-add";
    }
    
    /**
     * Add a case file to the database.
     *
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('ADD_FILES')")
    @PostMapping("/addfile")
    public String addFile(@Valid CaseFile caseFile, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "files-add";
        }
        fileRepository.save(caseFile);
        return "redirect:/files";
    }
    
    /**
     * Show the factsheet of a case file.
     *
     * @param id - file id.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of paged results.
     * @param size - number of results on a single page.
     * @return name of Thymeleaf template.
     */
    @PreAuthorize("hasAuthority('VIEW_FILES')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        CaseFile caseFile = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + id));
        model.addAttribute("caseFile", caseFile);
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Item> pagedItems =  itemRepository.findByFileidOrderByHeadline(id, paging);
        model.addAttribute("currentPage", pagedItems.getNumber() + 1);
        model.addAttribute("totalItems", pagedItems.getTotalElements());
        model.addAttribute("totalPages", pagedItems.getTotalPages());
        model.addAttribute("pageSize", size);
        List<Item> items = new ArrayList<Item>();
        items = pagedItems.getContent();
        model.addAttribute("items", items);

        return "files-view";
    }

    /**
     * Show the edit form for a case file.
     *
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('CHANGE_FILES')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        CaseFile caseFile = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + id));
        model.addAttribute("caseFile", caseFile);
        
        return "files-edit";
    }
    
    /**
     * Update a case file.
     *
     * @param id - case file id.
     * @param caseFile - the modified record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PreAuthorize("hasAuthority('CHANGE_FILES')")
    @PostMapping("/update/{id}")
    public String updateFile(@PathVariable("id") int id, @Valid CaseFile caseFile, BindingResult result, Model model) {
        log.info("CaseFile {}", caseFile);
        if (result.hasErrors()) {
            caseFile.setId(id);
            log.info("errors {}", result);
            model.addAttribute("caseFile", caseFile);
            return "files-edit";
        }
        
        fileRepository.save(caseFile);

        return String.format("redirect:/files/view/%d", id);
    }
    
    /**
     * Delete case file.
     *
     * @param model - Additional attributes used by the web form.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_FILES')")
    public String deleteFile(@PathVariable("id") int id, Model model) {
        CaseFile caseFile = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + id));
        fileRepository.delete(caseFile);
        log.info("Deleted file Id {}", id);
        return "redirect:/files";
    }
}
