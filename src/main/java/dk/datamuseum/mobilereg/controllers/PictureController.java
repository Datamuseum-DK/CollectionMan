package dk.datamuseum.mobilereg.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.repositories.PictureRepository;

/**
 * Controller for pictures.
 */
@Controller
@RequestMapping("/pictures")
public class PictureController {
    
    @Autowired
    private PictureRepository pictureRepository;

    private Log logger = LogFactory.getLog(PictureController.class);

    /*
    @RequestMapping({"", "/", "/view"})
    public String showPictureList(Model model) {
        model.addAttribute("pictures", pictureRepository.findByOrderByPicturenavn());
        return "pictures";
    }
    */

    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) {
        Picture picture = pictureRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid picture Id:" + id));
        model.addAttribute("picture", picture);
        
        return "pictures-view";
    }

}
