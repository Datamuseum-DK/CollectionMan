package dk.datamuseum.mobilereg.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.datamuseum.mobilereg.entities.ItemPicture;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.repositories.ItemPictureRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.service.PictureService;

/**
 * Controller for pictures.
 */
@Controller
@RequestMapping("/pictures")
public class PictureController {
    
    @Autowired
    private PictureRepository pictureRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private ItemPictureRepository itemPictureRepository;

    private Log logger = LogFactory.getLog(PictureController.class);

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
     * TODO: Remove many-to-many relation.
     */
    @PreAuthorize("hasAuthority('DELETE_PICTURES')")
    @GetMapping("/delete/{id}")
    public String deletePicture(@PathVariable("id") int id, Model model) {
        Picture picture = pictureRepository.findById(id).orElseThrow(()
		-> new IllegalArgumentException("Invalid picture Id:" + id));
        Iterable<ItemPicture> itempics = itemPictureRepository.findByPictureid(id);
        ItemPicture returnItem = new ItemPicture();
        for (ItemPicture itempic : itempics) {
            returnItem = itempic;
            logger.debug(String.format("Deleting relation %s", itempic));
            itemPictureRepository.delete(itempic);
        }
        pictureRepository.delete(picture);
        logger.info(String.format("Deleted picture Id %d", id));

        String filename = picture.getOriginal();
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        logger.debug(String.format("Deleted filename: %s", filename));
        pictureService.delete(filename);

        return String.format("redirect:/items/view/%d", returnItem.getItemid());
    }
}
