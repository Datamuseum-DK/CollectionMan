package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestMethod;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @RequestMapping({"", "/", "/view"})
    public String subjectList(Model model) {
        Iterable<Subject> subjects = subjectRepository.findByOrderByTitle();
        model.addAttribute("subjects", subjects);
        return "subjects";
    }

    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int subjectid, Model model) {
        Subject subject = subjectRepository.findById(subjectid).orElseThrow(()
                -> new IllegalArgumentException("Invalid subject Id:" + subjectid));
        model.addAttribute("subject", subject);

        return "subjects-view";
    }
    @GetMapping("/addform")
    public String addForm(Model model) {
        Subject subject = new Subject();
        model.addAttribute("subject", subject);
        return "subjects-add";
    }

    @PostMapping("/addsubject")
    public String addSubject(@Valid Subject subject, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "subjects-add";
        }
        subjectRepository.save(subject);
        return "redirect:/subjects";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        model.addAttribute("subject", subject);

        return "subjects-edit";
    }

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
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public String deleteSubject(@PathVariable("id") int id, Model model) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid subject Id:" + id));
        subjectRepository.delete(subject);
        logger.info(String.format("Deleted subject Id %d", id));
        return "redirect:/subjects";
    }

}
