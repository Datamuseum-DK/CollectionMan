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

import dk.datamuseum.mobilereg.entities.CaseFile;
import dk.datamuseum.mobilereg.repositories.FileRepository;
//import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Controller for case files.
 */
@Controller
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(FileController.class);

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
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('VIEW_FILES')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        CaseFile caseFile = fileRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + id));
        model.addAttribute("caseFile", caseFile);
        model.addAttribute("items", itemRepository.findByFileidOrderByHeadline(id));
        
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
     * @param model - Additional attributes used by the web form.
     */
    @PreAuthorize("hasAuthority('CHANGE_FILES')")
    @PostMapping("/update/{id}")
    public String updateFile(@PathVariable("id") int id, @Valid CaseFile caseFile, BindingResult result, Model model) {
        logger.info(String.format("caseFile %s", caseFile.toString()));
        if (result.hasErrors()) {
            caseFile.setId(id);
            logger.info(String.format("errors %s", result.toString()));
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
        logger.info(String.format("Deleted file Id %d", id));
        return "redirect:/files";
    }
}
