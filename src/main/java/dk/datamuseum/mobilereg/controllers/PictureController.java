package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.service.PictureService;

/**
 * Controller for pictures.
 */
@Slf4j
@Controller
@RequestMapping("/pictures")
public class PictureController {

    private final PictureRepository pictureRepository;

    private final PictureService pictureService;

    /**
     * Constructor.
     */
    public PictureController(
                PictureRepository pictureRepository,
                PictureService pictureService) {
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
    }

    /*
    @RequestMapping({"", "/", "/view"})
    public String showPictureList(Model model) {
        model.addAttribute("pictures", pictureRepository.findByOrderByPicturenavn());
        return "pictures";
    }
    */

    @PreAuthorize("hasAuthority('VIEW_PICTURES')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Picture picture = pictureRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid picture Id:" + id));
        model.addAttribute("picture", picture);

        return "pictures-view";
    }

    /**
     * Delete picture.
     * This also removes the physical files in the picture service.
     */
    @PreAuthorize("hasAuthority('DELETE_PICTURES')")
    @GetMapping("/delete/{id}")
    public String deletePicture(@PathVariable("id") int id, Model model) {
        Picture picture = pictureRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid picture Id:" + id));
        Integer returnItem = picture.getItemid();
        pictureRepository.delete(picture);
        log.info("Deleted picture Id {}", id);

        String filename = picture.getOriginal();
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        log.debug("Deleted filename: {}", filename);
        pictureService.delete(filename);

        return String.format("redirect:/items/view/%d", returnItem);
    }
}
