package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

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

import dk.datamuseum.mobilereg.entities.Subject;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.SubjectRepository;

/**
 * Controller for subjects.
 */
@Controller
@RequestMapping("/subjects")
public class SubjectController {
    
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ItemRepository itemRepository;

    private Log logger = LogFactory.getLog(SubjectController.class);

    @PreAuthorize("hasAuthority('VIEW_SUBJECTS')")
    @RequestMapping({"", "/", "/view"})
    public String subjectList(Model model) {
        Iterable<Subject> subjects = subjectRepository.findByOrderByTitle();
        model.addAttribute("subjects", subjects);
        return "subjects";
    }

    @PreAuthorize("hasAuthority('VIEW_SUBJECTS')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int subjectid, Model model) {
        Subject subject = subjectRepository.findById(subjectid).orElseThrow(()
                -> new IllegalArgumentException("Invalid subject Id:" + subjectid));
        model.addAttribute("subject", subject);

        return "subjects-view";
    }

    @PreAuthorize("hasAuthority('ADD_SUBJECTS')")
    @GetMapping("/addform")
    public String addForm(Model model) {
        Subject subject = new Subject();
        model.addAttribute("subject", subject);
        return "subjects-add";
    }

    @PreAuthorize("hasAuthority('ADD_SUBJECTS')")
    @PostMapping("/addsubject")
    public String addSubject(@Valid Subject subject, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "subjects-add";
        }
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @PreAuthorize("hasAuthority('CHANGE_SUBJECTS')")
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);

        return "subjects-edit";
    }

    /**
     * General update of subject.
     *
     * @param id - subject id.
     * @param subject - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PreAuthorize("hasAuthority('CHANGE_SUBJECTS')")
    @PostMapping("/update/{id}")
    public String updateSubject(@PathVariable("id") int id, @Valid Subject subject, BindingResult result, Model model) {
        if (result.hasErrors()) {
            subject.setSubjectid(id);
            model.addAttribute("subject", subject);
            return "subjects-edit";
        }

        subjectRepository.save(subject);

        return "redirect:/subjects";
    }

    /**
     * Delete subject.
     */
    @PreAuthorize("hasAuthority('DELETE_SUBJECTS')")
    @GetMapping("/delete/{id}")
    public String deleteSubject(@PathVariable("id") int id, Model model) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        subjectRepository.delete(subject);
        logger.info(String.format("Deleted subject Id %d", id));
        return "redirect:/subjects";
    }

}
